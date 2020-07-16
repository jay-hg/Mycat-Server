package study.chat;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

public class ChatClient {
    private Selector selector;
    private final ByteBuffer receiveBuffer = ByteBuffer.allocate(1024);

    public ChatClient() throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress(InetAddress.getLocalHost(), 8520));
        socketChannel.configureBlocking(false);
        System.out.println("与服务端连接成功!");

        this.selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_READ);
    }

    public static void main(String[] args) throws IOException {
        new ChatClient().chat();
    }

    private void chat() throws IOException {
        while (selector.select() > 0) {
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                if (key.isReadable()) {
                    recive(key);
                }
            }
        }
    }

    private void recive(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        socketChannel.read(receiveBuffer);
        receiveBuffer.flip();
        String receiveStr = Charset.forName("UTF-8").decode(receiveBuffer).toString();

        System.out.println("receive str:" + receiveStr);
        receiveBuffer.clear();
    }
}
