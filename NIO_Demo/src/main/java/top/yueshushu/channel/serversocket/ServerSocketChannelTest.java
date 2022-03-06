package top.yueshushu.channel.serversocket;

import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName:ServerSocketChannelTest
 * @Description ServerSocketChannel 测试
 * @Author 岳建立
 * @Date 2022/3/6 18:48
 * @Version 1.0
 **/
@Log4j
public class ServerSocketChannelTest {
    /**
     * 测试处理
     * @throws Exception
     */
    @Test
    public void simpleTest() throws Exception{
        //1. 准备数据  ByteBuffer  包装到缓冲区
        ByteBuffer byteBuffer = ByteBuffer.wrap(
                "Hello NIO!!!".getBytes()
        );
        //创建 ServerSocketChannel  .open ()
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //进行绑定 指定的端口
        serverSocketChannel.socket().bind(
                new InetSocketAddress(9999)
        );
        //配置为异步信息  false 为异步， true 为同步
      //  serverSocketChannel.configureBlocking(false);
        serverSocketChannel.configureBlocking(true);
        log.info(">>>>开始连接");
        while(true){
            SocketChannel socketChannel = serverSocketChannel.accept();
            if(null == socketChannel){
                log.info(">>>>没有客户端连接，2s后重新检测");
                TimeUnit.SECONDS.sleep(2);
            }else{
               log.info("获取远程连接:"+ socketChannel.socket().getRemoteSocketAddress());
               //倒回数据，即每次都重新取.
               byteBuffer.rewind();
               socketChannel.write(byteBuffer);
               //连接之后，就关闭。
               socketChannel.close();
            }

        }
    }
}
