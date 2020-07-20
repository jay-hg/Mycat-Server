package io.mycat;

import io.mycat.buffer.DirectByteBufferPool;
import io.mycat.config.model.SystemConfig2;
import io.mycat.net.NIOAcceptor;
import io.mycat.net.NIOAcceptor2;
import io.mycat.net.NIOReactorPool;
import io.mycat.net.SocketAcceptor;
import io.mycat.server.ServerConnectionFactory;
import io.mycat.server.ServerConnectionFactory2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MycatServer2 {
    private static final Logger LOGGER = LoggerFactory.getLogger("MycatServer2");
    private final static MycatServer2 INSTANCE = new MycatServer2();
    private final SystemConfig2 systemConfig;

    public MycatServer2() {
        this.systemConfig = new SystemConfig2();
    }

    public static MycatServer2 getInstance() {
        return INSTANCE;
    }

    public void startup() throws IOException {
        LOGGER.info("startup...");
        ServerConnectionFactory2 sf = new ServerConnectionFactory2();
        NIOReactorPool reactorPool = new NIOReactorPool(
                DirectByteBufferPool.LOCAL_BUF_THREAD_PREX + "NIOREACTOR",
                4);
        SocketAcceptor server = new NIOAcceptor2("Mycat2",systemConfig.getBindIp(),systemConfig.getServerPort(),sf,reactorPool);
        server.start();
    }
}
