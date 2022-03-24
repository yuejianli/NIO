package top.yueshushu.chat;

import lombok.extern.log4j.Log4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName:ChatServer
 * @Description 服务器端代码
 * @Author zk_yjl
 * @Date 2022/3/15 20:39
 * @Version 1.0
 * @Since 1.0
 **/
@Log4j
public class ChatServer {

    /**
     * 启动服务器
     * @date 2022/3/16 8:40
     * @author zk_yjl
     */
    public void start() throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(
                new InetSocketAddress(9997)
        );
        //设置成 异步的
        serverSocketChannel.configureBlocking(false);

        //创建   Selector 并注册
        Selector selector = Selector.open();

        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println(">>>>服务器已经成功启动");
        //进行循环处理
        while (true) {
            int selectCount = selector.select();
            if (selectCount <= 0) {
                continue;
            }
            //进行处理
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            TimeUnit.MILLISECONDS.sleep(100);
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                //获取下一个
                SelectionKey selectionKey = iterator.next();
                //对条件进行判断
                iterator.remove();
                if (selectionKey.isAcceptable()) {
                    handlerAccept(
                            serverSocketChannel, selector
                    );
                }
                if (selectionKey.isReadable()) {
                    handlerRead(
                            selectionKey, selector
                    );
                }
            }
        }

    }

    /**
     * 接收到消息，进行处理
     *
     * @param selectionKey  选择key
     * @param selector 选择器
     * @date 2022/3/16 8:49
     * @author zk_yjl
     */
    private void handlerRead(SelectionKey selectionKey, Selector selector) throws Exception {
        //1 获取对应的通道
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        int readLength = socketChannel.read(byteBuffer);
        String message = null;
        if (readLength > 0) {
            //获取信息
            byteBuffer.flip();

            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(byteBuffer);
            //获取到信息
            charBuffer.rewind();
            message = new String(charBuffer.array());
        }

        //注册可读事件
        socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);

        if (message != null) {
            //对消息进行打印
            System.out.println(socketChannel.toString() + "发送的消息是:" + message);

            //将这个消息，发送给其他的人
            sendMessageToOther(
                    message, selector, socketChannel
            );
        }
    }

    /**
     * 发送消息给其他人
     *
     * @param message 消息
     * @param selector 选择器
     * @param socketChannel channel
     * @date 2022/3/16 8:55
     * @author zk_yjl
     */
    private void sendMessageToOther(String message, Selector selector, SocketChannel socketChannel) throws Exception {
        //1. 获取到现在的信息
        Set<SelectionKey> selectionKeys = selector.keys();
        for (SelectionKey selectionKey : selectionKeys) {
            //进行判断.
            SelectableChannel channel = selectionKey.channel();
            if (channel instanceof SocketChannel && channel != socketChannel) {
                SocketChannel tempChannel = (SocketChannel) channel;
                tempChannel.write(
                        StandardCharsets.UTF_8.encode(
                                message
                        )
                );
            }
        }
    }

    /**
     * 接收到连接时，发送一个欢迎的信息
     *
     * @param serverSocketChannel channel
     * @param selector  选择器
     * @date 2022/3/16 8:45
     * @author zk_yjl
     */
    private void handlerAccept(ServerSocketChannel serverSocketChannel, Selector selector) throws Exception {
        SocketChannel socketChannel = serverSocketChannel.accept();
        Charset charset = StandardCharsets.UTF_8;
        socketChannel.configureBlocking(false);
        //注册可读事件
        socketChannel.register(
                selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE
        );
        socketChannel.write(
                charset.encode(
                        "欢迎你来到聊天室，注意隐私安全"
                )
        );


    }

    public static void main(String[] args) {

        try {
            new ChatServer().start();
            System.out.println(">>>>启动服务器成功");
        } catch (Exception e) {
            log.info(">>>启动服务器失败 {}", e);
        }

    }
}
