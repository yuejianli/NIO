package top.yueshushu.channel.datagramchannel;

import lombok.extern.log4j.Log4j;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName:DatagramChannelTest
 * @Description DatagrameChannel
 * @Author 岳建立
 * @Date 2022/3/6 19:32
 * @Version 1.0
 **/
@Log4j
public class DatagramChannelTest {
    /**
     * 发送数据
     *
     * @throws Exception 异常
     */
    @Test
    public void sendTest() throws Exception {
        DatagramChannel datagramChannel = DatagramChannel.open();
        //发送到哪个, 指定地址
        InetSocketAddress inetSocketAddress = new InetSocketAddress(
                "127.0.0.1", 9999);
        ByteBuffer byteBuffer = ByteBuffer.allocate(128);
        byteBuffer.put(Charset.forName("UTF-8").encode(
                "两个蝴蝶飞,你好啊"
        ));
        while (true) {
            // 调用 send 方法，进行发送
            datagramChannel.send(
                    byteBuffer, inetSocketAddress
            );
            //重置
            byteBuffer.rewind();
            log.info(">>>发送数据成功");
            TimeUnit.SECONDS.sleep(1);

        }
    }

    @Test
    public void receiveTest() throws Exception {
        //1. 创建连接
        DatagramChannel datagramChannel = DatagramChannel.open();
        //2. 绑定端口号
        datagramChannel.socket().bind(
                new InetSocketAddress(
                        9999
                )
        );
        //3. 创建ByteBuffer 缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        while (true) {
            //清空
            byteBuffer.clear();
            //接收数据
            SocketAddress socketAddress = datagramChannel.receive(byteBuffer);
            byteBuffer.flip();
            log.info(">>>发送方:" + socketAddress.toString());
            log.info(">>>具体数据:" + Charset.forName(
                    "UTF-8"
            ).decode(byteBuffer));

            datagramChannel.send(ByteBuffer.wrap(
                    "接收到数据了".getBytes("UTF-8")
            ), socketAddress);
            TimeUnit.MILLISECONDS.sleep(500);
        }
    }

    @Test
    public void portTest() throws Exception {
        DatagramChannel datagramChannel = DatagramChannel.open();
        // datagramChannel.configureBlocking(false);
        //绑定到  9998, 当前发送方的 端口号
        datagramChannel.socket().bind(
                new InetSocketAddress(
                        9998
                )
        );
        //连接到 9999, 连接服务器端
        datagramChannel.connect(
                new InetSocketAddress(
                        "127.0.0.1",
                        9999
                )
        );

        log.info(">>>>写入数据");


        //接收数据
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        while (true) {
            datagramChannel.write(
                    ByteBuffer.wrap(
                            "两个蝴蝶飞，你好啊222!!!".getBytes("UTF-8")
                    )

            );

            //先清空， 再写入进去
            readBuffer.clear();
            datagramChannel.read(
                    readBuffer
            );

            readBuffer.flip();

            log.info(">>输出信息:" + Charset.forName(
                    "UTF-8"
            ).decode(readBuffer));

            TimeUnit.MILLISECONDS.sleep(500);
        }


    }
}
