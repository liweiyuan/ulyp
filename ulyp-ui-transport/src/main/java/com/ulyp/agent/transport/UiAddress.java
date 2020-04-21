package com.ulyp.agent.transport;

public class UiAddress {

    public final String hostName;
    public final int port;

    public UiAddress(String hostName, int port) {
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
}
