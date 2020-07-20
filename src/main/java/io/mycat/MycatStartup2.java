package io.mycat;

import java.io.IOException;

public class MycatStartup2 {
    public static void main(String[] args) throws IOException {
        MycatServer2 mycatServer = MycatServer2.getInstance();
        mycatServer.startup();
    }
}
