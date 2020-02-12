package com.ulyp.agent.transport;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.ulyp.transport.TMethodTraceLogUploadRequest;
import com.ulyp.transport.TMethodTraceLogUploadResponse;
import com.ulyp.transport.UploadingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.internal.DnsNameResolverProvider;
import io.grpc.netty.NettyChannelBuilder;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.*;

public final class UploadingTransport {

    public static final UiAddress DEFAULT_ADDRESS = new UiAddress("localhost", 13991);

    private final ManagedChannel channel;
    private final UploadingServiceGrpc.UploadingServiceFutureStub uploadingServiceFutureStub;

    private final ExecutorService responseProcessingExecutor = Executors.newFixedThreadPool(3, new NamedThreadFactory("GRPC-Response-processor", true));
    private final Set<Long> traceLogsCurrentlyInSending = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private volatile boolean active = true;

    public UploadingTransport(UiAddress address) {
        channel = NettyChannelBuilder.forAddress(address.hostName, address.port)
                .nameResolverFactory(new DnsNameResolverProvider())
                .usePlaintext()
                .build();
        uploadingServiceFutureStub = UploadingServiceGrpc
                .newFutureStub(channel)
                .withExecutor(Executors.newFixedThreadPool(3, new NamedThreadFactory("GRPC-Connector", true)));
    }

    public void upload(TMethodTraceLogUploadRequest request) {
        if (!active) {
            throw new RuntimeException("Can't send trace log as transport is shutting down");
        }

        long id = request.getTraceLogId();
        traceLogsCurrentlyInSending.add(id);
        ListenableFuture<TMethodTraceLogUploadResponse> upload = uploadingServiceFutureStub.upload(request);

        Futures.addCallback(upload, new FutureCallback<TMethodTraceLogUploadResponse>() {
            @Override
            public void onSuccess(TMethodTraceLogUploadResponse result) {
                traceLogsCurrentlyInSending.remove(id);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                traceLogsCurrentlyInSending.remove(id);
            }
        }, responseProcessingExecutor);
    }

    public void shutdownNowAndAwaitForTraceLogsSending(long time, TimeUnit timeUnit) throws InterruptedException {
        active = false;

        long startWaitingAtEpochMillis = System.currentTimeMillis();
        long deadline = startWaitingAtEpochMillis + timeUnit.toMillis(time);
        while (System.currentTimeMillis() < deadline && !traceLogsCurrentlyInSending.isEmpty()) {
            Thread.sleep(100);
        }
    }
}
