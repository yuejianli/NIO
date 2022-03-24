package top.yueshushu.channel.asynchronousFileChannel;

import lombok.extern.log4j.Log4j;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName:AsynchronousFileChannelTest
 * @Description 异步读取写入文件
 * @Author zk_yjl
 * @Date 2022/3/15 20:13
 * @Version 1.0
 * @Since 1.0
 **/
@Log4j
public class AsynchronousFileChannelTest {
    /**
     * Future 异步形式读取文件
     */
    @Test
    public void futureReadTest() throws Exception {

        Path path = Paths.get("D:\\filelock.txt");

        //创建 异步读
        AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(path,
                StandardOpenOption.READ);

        //放置到 Future 里面
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        Future<Integer> readFuture = asynchronousFileChannel.read(byteBuffer, 0);
        //如果没有完成，则一直循环
        while (!readFuture.isDone()) {

        }
        //获取信息
        log.info(">>>读取长度:" + readFuture.get());

        byteBuffer.flip();

        //切换之后，输出信息
        log.info("信息：" + new String(byteBuffer.array()));
    }

    /**
     * handler 形式的读
     */
    @Test
    public void handlerReadTest() {

        try {
            Path path = Paths.get("D:\\filelock.txt");

            AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.READ);

            //构建 ByteBuffer
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

            asynchronousFileChannel.read(
                    byteBuffer, 0, byteBuffer,
                    new CompletionHandler<Integer, ByteBuffer>() {
                        @Override
                        public void completed(Integer result, ByteBuffer attachment) {
                            log.info(">>>字节数:" + result);
                            // 放置到 attachment 里面了
                            attachment.flip();
                            log.info(">>>读取内容:" + new String(attachment.array()));
                        }

                        @Override
                        public void failed(Throwable exc, ByteBuffer attachment) {
                            log.error(">>>读取失败");
                        }
                    }
            );
            //需要休眠处理
            TimeUnit.SECONDS.sleep(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Future 异步形式写入文件
     */
    @Test
    public void futureWriteTest() throws Exception {

        Path path = Paths.get("D:\\filelock.txt");

        //创建 异步读
        AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(path,
                StandardOpenOption.WRITE);

        //放置到 Future 里面
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put("我是个好人".getBytes());

        byteBuffer.flip();

        Future<Integer> writeFuture = asynchronousFileChannel.write(byteBuffer, 0);
        //如果没有完成，则一直循环
        while (!writeFuture.isDone()) {

        }
        log.info(">>>>>写入成功");
    }


    /**
     * handler 形式的写
     */
    @Test
    public void handlerWriteTest() {

        try {
            Path path = Paths.get("D:\\filelock.txt");

            AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.WRITE);

            //构建 ByteBuffer
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            byteBuffer.put("我是个好人2222".getBytes());
            byteBuffer.flip();
            asynchronousFileChannel.write(
                    byteBuffer, 0, byteBuffer,
                    new CompletionHandler<Integer, ByteBuffer>() {
                        @Override
                        public void completed(Integer result, ByteBuffer attachment) {
                            log.info(">>>写入内容:" + new String(attachment.array()));
                        }

                        @Override
                        public void failed(Throwable exc, ByteBuffer attachment) {
                            log.error(">>>写入失败");
                        }
                    }
            );
            //需要休眠处理
            TimeUnit.SECONDS.sleep(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
