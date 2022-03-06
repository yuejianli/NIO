package top.yueshushu.channel.socketchannel;

import lombok.extern.log4j.Log4j;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @ClassName:SocketChannelTest
 * @Description SocketChannel 测试
 * @Author 岳建立
 * @Date 2022/3/6 19:13
 * @Version 1.0
 **/
@Log4j
public class SocketChannelTest {
    /**
     * 同步,阻塞， 会一直等待下去
     */
    @Test
    public void blockingTest() throws IOException {
        //1. 创建 SocketChannel 连接
        SocketChannel socketChannel =
                SocketChannel.open(
                        //没有协议
                        new InetSocketAddress(
                                "pre.zkong.com",
                                80
                        )
                );
        //2. 创建 ByteBuffer 连接
        ByteBuffer byteBuffer = ByteBuffer.allocate(102400);
        //3. 将数据写入到 ByteBuffer 里面
        log.info(">>>>将数据写入到 ByteBuffer 缓冲里面");
        socketChannel.read(byteBuffer);
        byteBuffer.flip();
        while(byteBuffer.hasRemaining()){
            log.info(
                    (char)byteBuffer.get()
            );
        }
        socketChannel.close();
        log.info(">>>将数据输出成功");
    }

    /**
     * 异步，会输出打印
     * @throws IOException
     */
    @Test
    public void asyncTest() throws IOException {
        //1. 创建 SocketChannel 连接
        SocketChannel socketChannel =
                SocketChannel.open(
                        //没有协议
                        new InetSocketAddress(
                                "pre.zkong.com",
                                80
                        )
                );
        socketChannel.configureBlocking(false);
        //2. 创建 ByteBuffer 连接
        ByteBuffer byteBuffer = ByteBuffer.allocate(102400);
        //3. 将数据写入到 ByteBuffer 里面
        log.info(">>>>将数据写入到 ByteBuffer 缓冲里面");
        int readCount = socketChannel.read(byteBuffer);
        while(readCount>0){
            byteBuffer.flip();
            while(byteBuffer.hasRemaining()){
                log.info(
                        (char)byteBuffer.get()
                );
            }
            byteBuffer.clear();
            readCount = socketChannel.read(byteBuffer);
        }
        socketChannel.close();
        log.info(">>>将数据输出成功");
    }
}
