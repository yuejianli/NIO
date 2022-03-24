package top.yueshushu.channel.buffer;

import lombok.extern.log4j.Log4j;
import org.junit.Test;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @ClassName:BufferTest
 * @Description 四种Buffer 类型处理
 * @Author 岳建立
 * @Date 2022/3/6 20:54
 * @Version 1.0
 **/
@Log4j
public class BufferTest {
    /**
     * 子缓冲区大小
     */
    @Test
    public void sliceTest() {
        IntBuffer intBuffer = IntBuffer.allocate(10);
        //放置信息
        for (int i = 0; i < intBuffer.capacity(); i++) {
            intBuffer.put(i);
        }
        //这是原有的信息

        //3. 创建子缓冲区   只在 3~7 时创建
        intBuffer.position(3);
        intBuffer.limit(7);
        IntBuffer sliceBuffer = intBuffer.slice();
        //将这一段子缓冲区的信息改变
        log.info("改变子缓冲区的内容信息");
        for (int i = 0; i < sliceBuffer.capacity(); i++) {
            //扩大10倍
            int index = sliceBuffer.get();
            sliceBuffer.put(i, index * 10);
        }
        //重新读
        intBuffer.position(0);
        intBuffer.limit(intBuffer.capacity());
        while (intBuffer.hasRemaining()) {
            log.info(">>>读取信息:" + intBuffer.get());
        }
        // 输出信息:  0  1  2 30 40 50 60 7 8 9
    }

    /**
     * 只读缓冲区
     */
    @Test
    public void readOnlyTest() {
        IntBuffer intBuffer = IntBuffer.allocate(10);
        //放置信息
        for (int i = 0; i < intBuffer.capacity(); i++) {
            intBuffer.put(i);
        }
        //这是原有的信息
        //3. 创建子缓冲区   只在 3~7 时创建
        IntBuffer readOnlyBuffer = intBuffer.asReadOnlyBuffer();
        //将这一段子缓冲区的信息改变
        log.info("改变子缓冲区的内容信息");
        intBuffer.flip();
        for (int i = 0; i < intBuffer.capacity(); i++) {
            //扩大10倍
            int index = intBuffer.get();
            intBuffer.put(i, index * 10);
        }
        //重新读
        readOnlyBuffer.position(0);
        readOnlyBuffer.limit(intBuffer.capacity());
        intBuffer.flip();
        while (readOnlyBuffer.hasRemaining()) {
            log.info(">>>只读缓冲区信息:" + readOnlyBuffer.get());
        }
        // 输出信息:  0  10  20 30 40 50 60 70 80 90
    }

    /**
     * 直接缓冲区
     *
     * @throws Exception 异常
     */
    @Test
    public void allocateDirectTest() throws Exception {

        //定义两个文件流
        RandomAccessFile randomAccessFile = new RandomAccessFile("F:\\Java\\NIO\\Code\\NIO\\read.txt", "rw");
        FileChannel sourceChannel = randomAccessFile.getChannel();

        RandomAccessFile copyFile = new RandomAccessFile("F:\\Java\\NIO\\Code\\NIO\\readCopy.txt", "rw");
        FileChannel targetChannel = copyFile.getChannel();

        //1. 创建一个直接缓冲区

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);

        //往里面写入数据
        while (true) {
            int readCount = sourceChannel.read(byteBuffer);
            //读取不到，则退出循环，完成复制
            if (readCount > -1) {
                break;
            }
            byteBuffer.flip();

            //写入进去
            targetChannel.write(byteBuffer);

        }
        log.info(">>>>写入数据成功");

    }


    /**
     * 内存映射IO
     *
     * @throws Exception 异常信息
     */
    @Test
    public void mapTest() throws Exception {

        //定义两个文件流
        RandomAccessFile randomAccessFile = new RandomAccessFile("F:\\Java\\NIO\\Code\\NIO\\read.txt", "rw");
        FileChannel fileChannel = randomAccessFile.getChannel();

        //内存映射IO
        MappedByteBuffer mappedByteBuffer = fileChannel.map(
                FileChannel.MapMode.READ_WRITE, 0, 1024
        );
        //更改相应部分的数据。  不能超过原文件的大小
        mappedByteBuffer.putChar(
                2, 'L'
        );
        mappedByteBuffer.putChar(4, 'O');
        mappedByteBuffer.putChar(6, 'V');
        mappedByteBuffer.putChar(8, 'E');
        log.info(">>>>写入数据成功");

    }
}
