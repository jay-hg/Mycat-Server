package io.mycat.net.handler;

import io.mycat.MycatServer;
import io.mycat.config.Capabilities;
import io.mycat.config.ErrorCode;
import io.mycat.config.model.UserConfig;
import io.mycat.net.FrontendConnection;
import io.mycat.net.NIOHandler;
import io.mycat.net.mysql.AuthPacket;
import io.mycat.net.mysql.MySQLPacket;
import io.mycat.net.mysql.QuitPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Map;

public class FrontendAuthenticator2 implements NIOHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(FrontendAuthenticator2.class);
    private static final byte[] AUTH_OK = new byte[]{7, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0};

    protected final FrontendConnection source;

    public FrontendAuthenticator2(FrontendConnection source) {
        this.source = source;
    }

    @Override
    public void handle(byte[] data) {
        // check quit packet
        if (data.length == QuitPacket.QUIT.length && data[4] == MySQLPacket.COM_QUIT) {
            source.close("quit packet");
            return;
        }

        AuthPacket auth = new AuthPacket();
        auth.read(data);
        auth.database = "TESTDB";

        success(auth);
    }

    protected void success(AuthPacket auth) {
        source.setAuthenticated(true);
        source.setUser(auth.user);
        source.setSchema(auth.database);
        source.setCharsetIndex(auth.charsetIndex);
        source.setHandler(new FrontendCommandHandler(source));

        if (LOGGER.isInfoEnabled()) {
            StringBuilder s = new StringBuilder();
            s.append(source).append('\'').append(auth.user).append("' login success");
            byte[] extra = auth.extra;
            if (extra != null && extra.length > 0) {
                s.append(",extra:").append(new String(extra));
            }
            LOGGER.info(s.toString());
        }

        ByteBuffer buffer = source.allocate();
        source.write(source.writeToBuffer(AUTH_OK, buffer));
        boolean clientCompress = Capabilities.CLIENT_COMPRESS == (Capabilities.CLIENT_COMPRESS & auth.clientFlags);
        boolean usingCompress = true;
        if (clientCompress && usingCompress) {
            source.setSupportCompress(true);
        }
    }
}
