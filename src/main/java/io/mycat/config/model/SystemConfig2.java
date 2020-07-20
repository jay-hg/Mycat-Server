package io.mycat.config.model;

public class SystemConfig2 {
    private String bindIp = "127.0.0.1";
    private int serverPort = 8066;

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
