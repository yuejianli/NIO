package top.yueshushu.channel.selector;

import lombok.extern.log4j.Log4j;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName:SelectorTest
 * @Description 选择器
 * @Author zk_yjl
 * @Date 2022/3/15 15:31
 * @Version 1.0
 * @Since 1.0
 **/
@Log4j
public class SelectorTest {

    @Test
    public void serverTest() throws Exception {
        //1. 构建 ServerSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //进行绑定
        serverSocketChannel.socket().bind(
                new InetSocketAddress(
                        9997
                )
        );
        //2. 设置异步
        serverSocketChannel.configureBlocking(false);

        //3. 创建 Selector
        Selector selector = Selector.open();
        //4. 注册事件  接收连接事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        //5. 构建可读，可写的 Buffer 信息
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);

        ByteBuffer writeBuffer = ByteBuffer.allocate(1024);

        writeBuffer.put("Hello,I am Server".getBytes());

        //切换
        writeBuffer.flip();

        while (true) {
            //6.获取信息
            int select = selector.select();
            log.info("输出数量" + select);
            Set<SelectionKey> selectionKeySet = selector.selectedKeys();
            //如果有的话，进行处理
            Iterator<SelectionKey> iterator = selectionKeySet.iterator();
            while (iterator.hasNext()) {
                //获取下一下
                SelectionKey selectionKey = iterator.next();
                //移除
                iterator.remove();

                //对类型处理判断
                if (selectionKey.isAcceptable()) {
                    //连接状态
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    log.info("获取一个连接: " + socketChannel);
                    //注册读事件
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                }
                //是读的话
                if (selectionKey.isReadable()) {
                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

                    //读信息
                    readBuffer.clear();

                    socketChannel.read(readBuffer);

                    //flip
                    readBuffer.flip();

                    //写数据

                    log.info(">>>读取的数据:" + new String(readBuffer.array()));

                    //注册写事件
                    selectionKey.interestOps(SelectionKey.OP_WRITE);

                }
                if (selectionKey.isWritable()) {

                    //写事件
                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

                    writeBuffer.rewind();

                    socketChannel.write(writeBuffer);

                    log.info(">>>写数据数据:" + new String(writeBuffer.array()));
                    selectionKey.interestOps(SelectionKey.OP_READ);


                }
            }
            TimeUnit.SECONDS.sleep(1);
        }
    }

    @Test
    public void clientTest() throws Exception {
        //1. 创建 SocketChannel
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(
                new InetSocketAddress(
                        "127.0.0.1",
                        9997
                )
        );
        socketChannel.configureBlocking(false);

        //读写 Buffer
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);

        ByteBuffer writeBuffer = ByteBuffer.allocate(1024);

        writeBuffer.put("Hello,I am Client".getBytes());

        //写数据，然后读数据

        while (true) {
            writeBuffer.rewind();
            readBuffer.clear();
            socketChannel.read(readBuffer);
            socketChannel.write(writeBuffer);

            TimeUnit.SECONDS.sleep(1);
        }
    }
}
