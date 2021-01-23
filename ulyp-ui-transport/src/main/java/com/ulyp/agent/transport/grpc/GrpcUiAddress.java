package com.ulyp.agent.transport.grpc;

import com.ulyp.agent.transport.UiAddress;
import com.ulyp.agent.transport.UiTransport;

/**
 * Used by default unless UI connection is disabled explicitly or file path is specified for the agent
 *
 * User can specify host/port for UI
 */
public class GrpcUiAddress implements UiAddress {

    public final String hostName;
    public final int port;

    public GrpcUiAddress(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
    }

    @Override
    public String toString() {
        return "UiAddress{" +
                "hostName='" + hostName + '\'' +
                ", port=" + port +
                '}';
    }

    @Override
    public UiTransport buildTransport() {
        return new GrpcUiTransport(this);
    }
}
