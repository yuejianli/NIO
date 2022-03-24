package top.yueshushu.chat;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * @ClassName:ChatClient
 * @Description 客户端
 * @Author zk_yjl
 * @Date 2022/3/16 9:03
 * @Version 1.0
 * @Since 1.0
 **/
public class ChatClient {

    /**
     * 客户端处理
     *
     启动客户端
     * @date 2022/3/16 9:03
     * @author zk_yjl
     */
    public void start(String name) throws Exception {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(
                new InetSocketAddress(
                        "127.0.0.1",
                        9997
                )
        );
        socketChannel.configureBlocking(false);

        //创建Select

        Selector selector = Selector.open();

        //注册可读事件
        socketChannel.register(
                selector,
                SelectionKey.OP_READ
        );

        new Thread(
                new ClientThread(selector)
        ).start();
        //进行处理
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            //获取到下一行
            String message = scanner.nextLine();
            socketChannel.write(
                    StandardCharsets.UTF_8
                            .encode(
                                    name + ":" + message
                            )
            );
        }
    }
}
