package io.mycat.config.model;

public class SystemConfig2 {
    private String bindIp = "0.0.0.0";
    private int serverPort;

    public String getBindIp() {
        return bindIp;
    }

    public void setBindIp(String bindIp) {
        this.bindIp = bindIp;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
}
