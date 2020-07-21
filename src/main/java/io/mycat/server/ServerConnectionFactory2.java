package io.mycat.server;

import io.mycat.MycatServer;
import io.mycat.net.FrontendConnection;
import io.mycat.net.NIOHandler;
import io.mycat.net.factory.FrontendConnectionFactory;
import io.mycat.net.handler.FrontendAuthenticator2;

import java.io.IOException;
import java.net.StandardSocketOptions;
import java.nio.channels.NetworkChannel;

public class ServerConnectionFactory2 extends FrontendConnectionFactory {
    @Override
    protected FrontendConnection getConnection(NetworkChannel channel) throws IOException {
        ServerConnection c = new ServerConnection(channel);
        return c;
    }

    @Override
    public FrontendConnection make(NetworkChannel channel) throws IOException {
        channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        channel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

        FrontendConnection c = getConnection(channel);
//        MycatServer.getInstance().getConfig().setSocketParams(c, true);
        NIOHandler handler = new FrontendAuthenticator2(c);
        c.setHandler(handler);
        return c;
    }
}
