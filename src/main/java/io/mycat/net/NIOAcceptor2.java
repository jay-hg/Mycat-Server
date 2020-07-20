package io.mycat.net;

import io.mycat.server.ServerConnectionFactory;
import io.mycat.server.ServerConnectionFactory2;
import io.mycat.util.SelectorUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

public class NIOAcceptor2 extends Thread implements SocketAcceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(NIOAcceptor2.class);
    private String bindIp;
    private int port;
    private ServerConnectionFactory2 sf;
    private NIOReactorPool nioReactorPool;

    private final ServerSocketChannel serverSocketChannel;
    private volatile Selector selector;

    public NIOAcceptor2(@NotNull String name, String bindIp, int port, ServerConnectionFactory2 sf, NIOReactorPool nioReactorPool) throws IOException {
        super(name);
        this.bindIp = bindIp;
        this.port = port;
        this.sf = sf;
        this.nioReactorPool = nioReactorPool;

        this.selector = Selector.open();
        this.serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        /** 设置TCP属性 */
        serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        serverSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 1024 * 16 * 2);

        serverSocketChannel.bind(new InetSocketAddress(bindIp, port), 100);

        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public void run() {
        int invalidSelectCount = 0;
        for (; ; ) {
            try {
                final Selector tSelector = this.selector;
                long start = System.nanoTime();
                long end = System.nanoTime();
                tSelector.select(1000L);
                Set<SelectionKey> keys = tSelector.selectedKeys();

                if (keys.size() == 0 && (end - start) < SelectorUtil.MIN_SELECT_TIME_IN_NANO_SECONDS) {
                    invalidSelectCount++;
                } else {
                    try {
                        for (SelectionKey key : keys) {
                            if (key.isValid() && key.isReadable()) {
                                accept();
                            } else {
                                key.cancel();
                            }
                        }
                    } finally {
                        keys.clear();
                        invalidSelectCount = 0;
                    }
                }

                if (invalidSelectCount > SelectorUtil.REBUILD_COUNT_THRESHOLD)
                {
                    final Selector rebuildSelector = SelectorUtil.rebuildSelector(this.selector);
                    if (rebuildSelector != null)
                    {
                        this.selector = rebuildSelector;
                    }
                    invalidSelectCount = 0;
                }
            } catch (Exception e) {
                LOGGER.warn(getName(), e);
            } catch (final Throwable e) {
                LOGGER.warn("caught Throwable err: ", e);
            }
        }
    }

    private void accept() throws IOException {
        SocketChannel socketChannel = null;
        socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        FrontendConnection connection = sf.make(socketChannel);
        connection.setAccepted(true);
        connection.setId(1L);

        NIOReactor reactor = nioReactorPool.getNextReactor();
        reactor.postRegister(connection);
    }
}
