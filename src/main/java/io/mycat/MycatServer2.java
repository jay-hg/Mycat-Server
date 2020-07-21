package io.mycat;

import io.mycat.buffer.BufferPool;
import io.mycat.buffer.DirectByteBufferPool;
import io.mycat.config.model.SystemConfig2;
import io.mycat.net.*;
import io.mycat.server.ServerConnectionFactory;
import io.mycat.server.ServerConnectionFactory2;
import io.mycat.util.ExecutorUtil;
import io.mycat.util.NameableExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MycatServer2 {
    private static final Logger LOGGER = LoggerFactory.getLogger("MycatServer2");
    private final static MycatServer2 INSTANCE = new MycatServer2();
    private final SystemConfig2 systemConfig;

    private NIOProcessor[] processors;
    private NameableExecutor businessExecutor;

    public MycatServer2() {
        this.systemConfig = new SystemConfig2();
    }

    public static MycatServer2 getInstance() {
        return INSTANCE;
    }

    public void startup() throws IOException {
        LOGGER.info("startup...");

        // 初始化processor
        businessExecutor = ExecutorUtil.create("BusinessExecutor",
                1);
        this.processors = new NIOProcessor[1];
        BufferPool bufferPool = new DirectByteBufferPool(2097152, (short) 4096,
                (short) 80, 1048576);

        NIOProcessor processor = new NIOProcessor("Processor" + 0, bufferPool,
                businessExecutor);
        processors[0] = processor;

        ServerConnectionFactory2 sf = new ServerConnectionFactory2();
        NIOReactorPool reactorPool = new NIOReactorPool(
                DirectByteBufferPool.LOCAL_BUF_THREAD_PREX + "NIOREACTOR",
                1);
        SocketAcceptor server = new NIOAcceptor2("Mycat2",systemConfig.getBindIp(),systemConfig.getServerPort(),sf,reactorPool);
        server.start();
    }

    public NIOProcessor nextProcessor() {
        return processors[0];
    }
}
