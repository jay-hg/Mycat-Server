package study.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class ChatServer {
    private Selector selector;

    public ChatServer() throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        ServerSocket serverSocket = serverSocketChannel.socket();
        serverSocket.bind(new InetSocketAddress(8520));
        System.out.println("server listening on port 8520");

        this.selector = Selector.open();

        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    public static void main(String[] args) throws IOException {
        new ChatServer().service();
    }

    private void service() throws IOException {
        while (selector.select() > 0) {
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                iterator.remove();
                if (selectionKey.isAcceptable()) {
                    System.out.println("isAcceptable");
                    ServerSocketChannel channel = (ServerSocketChannel) selectionKey.channel();

                    SocketChannel socketChannel = channel.accept();
                    if (socketChannel == null) {
                        continue;
                    }
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);

                    ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
                    buffer.put("hello world!".getBytes());
                    buffer.flip();
                    socketChannel.write(buffer);
                }
            }
        }

    }
}
