package top.yueshushu.channel.buffer;

import lombok.extern.log4j.Log4j;
import org.junit.Test;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;

/**
 * @ClassName:ByteBufferTest
 * @Description ByteBuffer的测试信息
 * @Author 岳建立
 * @Date 2022/3/6 20:12
 * @Version 1.0
 **/
@Log4j
public class ByteBufferTest {
    /**
     * byte 缓冲区处理
     *
     * @throws IOException IO异常
     */
    @Test
    public void byteBufferTest() throws IOException {
        RandomAccessFile randomAccessFile =
                new RandomAccessFile("F:\\Java\\NIO\\Code\\NIO\\read.txt", "rw");
        //写入到数据里面
        FileChannel fileChannel = randomAccessFile.getChannel();
        //2. 创建 ByteBuffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(32);
        //将数据写入到 byteBuffer
        int readCount = fileChannel.read(byteBuffer);
        while (readCount > 0) {
            //调转，切换成读模式
            byteBuffer.flip();

            //读出数据
            while (byteBuffer.hasRemaining()) {
                log.info((char) byteBuffer.get());
            }

            //清空
            byteBuffer.clear();

            readCount = fileChannel.read(byteBuffer);
        }
        fileChannel.close();
        randomAccessFile.close();
        log.info(">>>读取数据成功");
    }

    /**
     * int 缓冲区处理
     */
    @Test
    public void intBufferTest() {
        // 设置容量为 10
        IntBuffer intBuffer = IntBuffer.allocate(10);

        //放置数据
        for (int i = 0; i < intBuffer.capacity(); i++) {
            //放置信息
            intBuffer.put(i * 2);
        }

        //切换成写模式

        intBuffer.flip();

        //读出数据
        while (intBuffer.hasRemaining()) {
            log.info(">>>输出:" + intBuffer.get());
        }

        intBuffer.clear();

        log.info(">>>读取数据成功");
    }

    /**
     * 重读
     *
     */
    @Test
    public void rewindTest() {
        // 设置容量为 10
        IntBuffer intBuffer = IntBuffer.allocate(10);

        //放置数据
        for (int i = 0; i < intBuffer.capacity(); i++) {
            //放置信息
            intBuffer.put(i * 2);
        }

        //切换成写模式
        intBuffer.flip();
        log.info(">>>> 第一回输出");
        while (intBuffer.hasRemaining()) {
            log.info(">>>1输出:" + intBuffer.get());
        }
        log.info(">>>> 再次进行输出");
        // 没有 数据
        while (intBuffer.hasRemaining()) {
            log.info(">>>2输出:" + intBuffer.get());
        }
        //重置
        intBuffer.rewind();
        log.info(">>>> rewind() 后再次进行输出");
        while (intBuffer.hasRemaining()) {
            log.info(">>>3输出:" + intBuffer.get());
        }


        intBuffer.clear();

        log.info(">>>读取数据成功");
    }


    /**
     * 全部清空  clear  和 compact 清空已读
     *
     */
    @Test
    public void clearAndCompactTest() {
        // 设置容量为 10
        IntBuffer intBuffer = IntBuffer.allocate(10);
        //放置数据
        for (int i = 0; i < intBuffer.capacity(); i++) {
            //放置信息
            intBuffer.put(i * 2);
        }
        //切换成写模式
        intBuffer.flip();
        log.info(">>>>全部清空");
        intBuffer.clear();
        while (intBuffer.hasRemaining()) {
            log.info(">>>全部清空输出:" + intBuffer.get());
        }
        intBuffer = IntBuffer.allocate(10);
        //放置数据
        for (int i = 0; i < intBuffer.capacity(); i++) {
            //放置信息
            intBuffer.put(i * 2);
        }
        //切换成写模式
        intBuffer.flip();
        //先读两个
        // 0 2
        for (int i = 0; i < 2; i++) {
            log.info(">>>先读两个输出:" + intBuffer.get());
        }
        log.info(">>>>已读清空");
        /*
         * 调用 compact 之前，    position 为 2， limit 为 10
         *  调用之后   position  为  10-2 =8， limit 为 10
         */
        log.info(">>>调用前 limit 输出位置:" + intBuffer.limit());
        log.info(">>>调用前 position 输出位置:" + intBuffer.position());
        intBuffer.compact();
        log.info(">>>调用后 limit 输出位置:" + intBuffer.limit());
        log.info(">>>调用后 position 输出位置:" + intBuffer.position());
        while (intBuffer.hasRemaining()) {
            //  16  18
            log.info(">>>已读清空输出:" + intBuffer.get());
        }
        log.info(">>>读取数据成功");
    }


    /**
     * mark 标记
     * 和 reset 重置标记
     *
     */
    @Test
    public void markAndResetTest() {
        // 设置容量为 10
        IntBuffer intBuffer = IntBuffer.allocate(10);

        //放置数据
        for (int i = 0; i < intBuffer.capacity(); i++) {
            //放置信息
            intBuffer.put(i * 2);
        }

        //切换成写模式
        intBuffer.flip();
        // 0 2
        //先读两个
        for (int i = 0; i < 2; i++) {
            log.info(">>>先读两个输出:" + intBuffer.get());
        }
        log.info(">>>读两个之后，设置标记 mark ");
        intBuffer.mark();
        //读剩下的
        while (intBuffer.hasRemaining()) {
            log.info(">>>读取剩下的:" + intBuffer.get());
        }
        //重置标记，继续读以前的 2~9
        intBuffer.reset();
        log.info(">>>重置  reset 之后，继续读取");
        while (intBuffer.hasRemaining()) {
            log.info(">>>重读剩下的:" + intBuffer.get());
        }
        intBuffer.clear();

        log.info(">>>读取数据成功");
    }
}
