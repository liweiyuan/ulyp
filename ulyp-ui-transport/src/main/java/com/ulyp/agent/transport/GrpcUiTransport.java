package com.ulyp.agent.transport;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.ulyp.core.*;
import com.ulyp.core.process.ProcessInfo;
import com.ulyp.transport.*;
import io.grpc.ManagedChannel;
import io.grpc.internal.DnsNameResolverProvider;
import io.grpc.netty.NettyChannelBuilder;

import java.time.Duration;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.*;

public class GrpcUiTransport implements UiTransport {

    public static final UiAddress DEFAULT_ADDRESS = new UiAddress("localhost", 13991);

    private final ManagedChannel channel;
    private final UiTransportGrpc.UiTransportFutureStub uploadingServiceFutureStub;

    private final ExecutorService uploadExecutor = Executors.newFixedThreadPool(
            5,
            new NamedThreadFactory("GRPC-Transport-Senders", true)
    );
    private final ExecutorService responseProcessingExecutor = Executors.newFixedThreadPool(
            3,
            new NamedThreadFactory("GRPC-Response-processor", true)
    );
    private final Set<Long> traceLogsCurrentlyInSending = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public GrpcUiTransport(UiAddress address) {
        channel = NettyChannelBuilder.forAddress(address.hostName, address.port)
                .nameResolverFactory(new DnsNameResolverProvider())
                .usePlaintext()
                .build();
        uploadingServiceFutureStub = UiTransportGrpc
                .newFutureStub(channel)
                .withExecutor(Executors.newFixedThreadPool(3, new NamedThreadFactory("GRPC-Connector", true)));
    }

    public SettingsResponse getSettingsBlocking(Duration duration) throws InterruptedException, ExecutionException, TimeoutException {
        return uploadingServiceFutureStub.requestSettings(SettingsRequest.newBuilder().build()).get(duration.toMillis(), TimeUnit.MILLISECONDS);
    }

    public void uploadAsync(CallRecordLog recordLog, MethodDescriptionDictionary methodDescriptionDictionary, ProcessInfo processInfo) {

        long endLifetimeEpochMillis = System.currentTimeMillis();

        uploadExecutor.submit(
                () -> {
                    TCallRecordLog log = TCallRecordLog.newBuilder()
                            .setEnterTraces(recordLog.getEnterRecords().toByteString())
                            .setExitTraces(recordLog.getExitRecords().toByteString())
                            .build();

                    MethodDescriptionList methodDescriptionList = new MethodDescriptionList();
                    for (MethodDescription description : methodDescriptionDictionary.getMethodDescriptions()) {
                        methodDescriptionList.add(description);
                    }
                    ClassDescriptionList classDescriptionList = new ClassDescriptionList();
                    for (ClassDescription classDescription : methodDescriptionDictionary.getClassDescriptions()) {
                        classDescriptionList.add(classDescription);
                    }

                    TCallRecordLogUploadRequest.Builder requestBuilder = TCallRecordLogUploadRequest.newBuilder();

                    requestBuilder
                            .setTraceLogId(recordLog.getId())
                            .setRecordLog(log)
                            .setMethodDescriptionList(TMethodDescriptionList.newBuilder().setData(methodDescriptionList.toByteString()).build())
                            .setClassDescriptionList(TClassDescriptionList.newBuilder().setData(classDescriptionList.toByteString()).build())
                            .setProcessInfo(com.ulyp.transport.ProcessInfo.newBuilder()
                                    .setMainClassName(processInfo.getMainClassName())
                                    .addAllClasspath(processInfo.getClasspath().toList())
                                    .build())
                            .setCreateEpochMillis(recordLog.getEpochMillisCreatedTime())
                            .setLifetimeMillis(endLifetimeEpochMillis - recordLog.getEpochMillisCreatedTime());

                    long id = recordLog.getId();
                    ListenableFuture<TCallRecordLogUploadResponse> upload = uploadingServiceFutureStub.uploadCallGraph(requestBuilder.build());

                    Futures.addCallback(upload, new FutureCallback<TCallRecordLogUploadResponse>() {
                        @Override
                        public void onSuccess(TCallRecordLogUploadResponse result) {
                            traceLogsCurrentlyInSending.remove(id);
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            t.printStackTrace();
                            traceLogsCurrentlyInSending.remove(id);
                        }
                    }, responseProcessingExecutor);
                }
        );

        long id = recordLog.getId();
        traceLogsCurrentlyInSending.add(id);
    }

    public void shutdownNowAndAwaitForRecordsLogsSending(long time, TimeUnit timeUnit) throws InterruptedException {
        long startWaitingAtEpochMillis = System.currentTimeMillis();
        long deadline = startWaitingAtEpochMillis + timeUnit.toMillis(time);
        while (System.currentTimeMillis() < deadline && !traceLogsCurrentlyInSending.isEmpty()) {
            Thread.sleep(100);
        }
        if (!traceLogsCurrentlyInSending.isEmpty()) {
            System.err.println(
                    "Didn't send " + traceLogsCurrentlyInSending + " trace logs, but shutting " +
                    "down anyway as waited for " + time + " " + timeUnit
            );
        }
    }
}
