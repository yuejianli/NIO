package top.yueshushu.channel.filechannel;

import cn.hutool.core.util.ArrayUtil;
import lombok.extern.log4j.Log4j;
import org.junit.Test;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @ClassName:FileChannelTest
 * @Description 文件读写Channel
 * @Author 岳建立
 * @Date 2022/3/6 15:59
 * @Version 1.0
 **/
@Log4j
public class FileChannelTest {

    //1. 使用 RandomAccessFile 创建 FileChannel
    //2. 创建缓冲区 ByteBuffer
    //3.将数据读到Buffer 里面
    //3.1 循环读取，然后反转，写入，清空。

    /**
     * 读文件测试
     *
     * @date 2022/3/24 9:23
     * @author zk_yjl
     * 读文件测试
     */
    @Test
    public void readFileTest() throws IOException {
        RandomAccessFile randomAccessFile =
                new RandomAccessFile("F:\\Java\\NIO\\Code\\NIO\\read.txt", "rw");
        //创建通道
        FileChannel fileChannel = randomAccessFile.getChannel();

        //创建缓冲区 通过 allocate 指定大小  一次性读取 1024个字节，不是一个个读
        // ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        // allocate(4) 时会多读写几回。
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);

        //读取到byteBuffer 缓冲区里面  如果读取不到信息，返回 -1
        int readCount = fileChannel.read(byteBuffer);
        while (readCount > -1) {
            //信息
            log.info(">>读取字节数" + readCount);
            //调用 flip 进行写入
            byteBuffer.flip();
            while (byteBuffer.hasRemaining()) {
                //读取, 中文会乱码.
                log.info(">>>写出数据:" + (char) byteBuffer.get());
            }
            byteBuffer.clear();
            //清空之后，继续读取, 第二回是 -1 ,读取不到数据。
            readCount = fileChannel.read(byteBuffer);
        }
        fileChannel.close();
        randomAccessFile.close();
        log.info(">>>读出数据成功");

    }

    /**
     * 写入数据
     */
    @Test
    public void writeTest() throws IOException {
        RandomAccessFile randomAccessFile =
                new RandomAccessFile("F:\\Java\\NIO\\Code\\NIO\\write.txt", "rw");
        //1. 获取通道
        FileChannel fileChannel = randomAccessFile.getChannel();
        //2. 定义数据，并写入到 ByteBuffer 里面
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        //放置信息
        byteBuffer.clear();
        byteBuffer.put("Hello NIO".getBytes());
        //进行读取转换
        byteBuffer.flip();
        log.info(">>>开始写入数据");
        //将数据写入到 Channel 里面
        while (byteBuffer.hasRemaining()) {
            //写入到 channel 里面
            fileChannel.write(byteBuffer);
        }
        log.info(">>>写入数据完成");
        fileChannel.close();
    }

    /**
     * 写入大量数据
     */
    @Test
    public void writeMoreTest() throws IOException {
        RandomAccessFile randomAccessFile =
                new RandomAccessFile("F:\\Java\\NIO\\Code\\NIO\\write.txt", "rw");
        //1. 获取通道
        FileChannel fileChannel = randomAccessFile.getChannel();
        //2. 定义数据，并写入到 ByteBuffer 里面
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byte[] bytes = "Hello NIO !!!".getBytes();
        log.info(">>>开始写入数据");
        for (int i = 0; i < bytes.length; i = i + 4) {
            //放置信息
            byteBuffer.clear();
            byteBuffer.put(ArrayUtil.sub(
                    bytes, i, Math.min(
                            i + 4,
                            bytes.length
                    )
            ));
            //进行读取转换
            byteBuffer.flip();
            //将数据写入到 Channel 里面
            while (byteBuffer.hasRemaining()) {
                //写入到 channel 里面
                fileChannel.write(byteBuffer);
            }
            log.info(">>>写入一次");
        }
        log.info(">>>写入数据完成");
        fileChannel.close();
    }

    /**
     * position()   返回当前的位置
     * position(index) 设置当前的位置。
     * 如果超过文件 结束符， 读取时，会返回  -1
     * 写入时，会从 index 处开始写入，将文件增大。会造成 "文件空洞" 现象
     * <p>
     * size () 获取关联的文件 的大小， 与position() 位置无关。 truncate() 截取，会导致 size() 发生改变。
     * <p>
     * truncate(index) 截取， 会将源文件进行截取， size() 会改变。 截取后再读取 position
     *
     * @throws IOException 异常
     */
    @Test
    public void otherTest() throws IOException {
        //读取
        RandomAccessFile randomAccessFile = new RandomAccessFile("F:\\Java\\NIO\\Code\\NIO\\read.txt", "rw");
        FileChannel fileChannel = randomAccessFile.getChannel();
        //获取对应的位置信息
        log.info(">>>获取文件的大小:" + fileChannel.size());
        log.info(">>获取位置:" + fileChannel.position());
        //设置位置， 即前四个不读
        fileChannel.position(
                fileChannel.position() + 4
        );

        log.info(">>>获取文件的大小:" + fileChannel.size());
        //截取，只要前四个。 后面的不要。
        fileChannel.truncate(8);
        log.info(">>>截取文件内容，只要前八个");
        log.info(">>>获取文件的大小:" + fileChannel.size());
        ByteBuffer byteBuffer = ByteBuffer.allocate(128);

        int readCount = fileChannel.read(byteBuffer);
        while (readCount > -1) {
            log.info(">>>读取信息:" + readCount);
            byteBuffer.flip();
            while (byteBuffer.hasRemaining()) {
                log.info(">>获取读取的信息:" + (char) byteBuffer.get());
            }
            byteBuffer.clear();
            readCount = fileChannel.read(byteBuffer);
        }
        //进行关闭
        fileChannel.close();
        randomAccessFile.close();
        log.info(">>>读取文件内容结束");
    }

    /**
     * 文件复制， target.transferFrom(sourceChannel,position,count)
     *
     * @throws IOException count 时，数目为 当前 channel 的最大内容数目。
     *                     并且只是，当前接收到的数目。
     */
    @Test
    public void transferFromTest() throws IOException {
        //定义两个文件流
        RandomAccessFile randomAccessFile = new RandomAccessFile("F:\\Java\\NIO\\Code\\NIO\\read.txt", "rw");
        FileChannel sourceChannel = randomAccessFile.getChannel();

        RandomAccessFile copyFile = new RandomAccessFile("F:\\Java\\NIO\\Code\\NIO\\readCopy.txt", "rw");
        FileChannel targetChannel = copyFile.getChannel();

        //获取文件的大小
        log.info(">>>>开始进行文件");
        int position = 0;
        long count = sourceChannel.size();

        //调用，进行复制
        targetChannel.transferFrom(
                sourceChannel, position, count
        );
        log.info(">>>复制文件成功");
    }

    /**
     * 文件复制， source.transferTo(position,count,targetChannel)
     *
     * @throws IOException 异常
     */
    @Test
    public void transferToTest() throws IOException {
        //定义两个文件流
        RandomAccessFile randomAccessFile = new RandomAccessFile("F:\\Java\\NIO\\Code\\NIO\\read.txt", "rw");
        FileChannel sourceChannel = randomAccessFile.getChannel();

        RandomAccessFile copyFile = new RandomAccessFile("F:\\Java\\NIO\\Code\\NIO\\readCopy.txt", "rw");
        FileChannel targetChannel = copyFile.getChannel();

        //获取文件的大小
        log.info(">>>>开始进行文件");
        int position = 0;
        long count = sourceChannel.size();

        //调用，进行复制
        sourceChannel.transferTo(
                position, count, targetChannel
        );
        log.info(">>>复制文件成功");
    }

    /**
     * 会依次读取
     *
     * @throws IOException Scattering Reads 在移动下一个 buffer 前，必须填满当前的 buffer，这也意味着它
     *                     不适用于动态消息(译者注：消息大小不固定)。换句话说，如果存在消息头和消息体，
     *                     消息头必须完成填充（例如 128byte），Scattering Reads 才能正常工作。
     */
    @Test
    public void scatterTest() throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile("F:\\Java\\NIO\\Code\\NIO\\read.txt", "rw");
        FileChannel fileChannel = randomAccessFile.getChannel();
        //创建 ByteBuffer 数组
        ByteBuffer[] byteBuffers = new ByteBuffer[3];
        byteBuffers[0] = ByteBuffer.allocate(4);
        byteBuffers[1] = ByteBuffer.allocate(4);
        byteBuffers[2] = ByteBuffer.allocate(10);

        //读取数据到 fileChannel
        log.info(">>>读取数据到 byteBuffers");
        fileChannel.read(byteBuffers);

        //输出信息
        //写入数据
        log.info(">>> 打印第一个 ByteBuffer 里面的数据");
        //需要转换 flip
        byteBuffers[0].flip();
        while (byteBuffers[0].hasRemaining()) {
            //写入数据
            log.info("读取数据:" + (char) (byteBuffers[0].get()));
        }
        log.info(">>> 打印第二个 ByteBuffer 里面的数据");
        byteBuffers[1].flip();
        while (byteBuffers[1].hasRemaining()) {
            //写入数据
            log.info("读取数据:" + (char) (byteBuffers[1].get()));
        }
        log.info(">>> 打印第三个 ByteBuffer 里面的数据");
        byteBuffers[2].flip();
        while (byteBuffers[2].hasRemaining()) {
            //写入数据
            log.info("读取数据:" + (char) (byteBuffers[2].get()));
        }
        fileChannel.close();
        randomAccessFile.close();
    }

    /**
     * 会依次写入
     * <p>
     * buffers 数组是 write()方法的入参，write()方法会按照 buffer 在数组中的顺序，将数
     * 据写入到 channel，注意只有 position 和 limit 之间的数据才会被写入。因此，如果
     * 一个 buffer 的容量为 128byte，但是仅仅包含 58byte 的数据，那么这 58byte 的数
     * 据将被写入到 channel 中。因此与 Scattering Reads 相反，Gathering Writes 能较
     * 好的处理动态消息
     *
     * @throws IOException 异常
     */
    @Test
    public void gatherTest() throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile("F:\\Java\\NIO\\Code\\NIO\\write.txt", "rw");
        FileChannel fileChannel = randomAccessFile.getChannel();
        //创建 ByteBuffer 数组
        ByteBuffer[] byteBuffers = new ByteBuffer[3];
        byteBuffers[0] = ByteBuffer.allocate(4);
        byteBuffers[1] = ByteBuffer.allocate(4);
        byteBuffers[2] = ByteBuffer.allocate(10);

        //读取数据到 fileChannel   不能超过对应的长度
        byteBuffers[0].put("Hell".getBytes());
        byteBuffers[1].put("NIOO".getBytes());
        byteBuffers[2].put("!!!!!!".getBytes());
        log.info(">>>写入数据到 byteBuffers");
        //不要忘记 调用  flip () 处理
        byteBuffers[0].flip();
        byteBuffers[1].flip();
        byteBuffers[2].flip();
        fileChannel.write(byteBuffers);
        log.info(">>写入数据成功");
        fileChannel.close();
        randomAccessFile.close();
    }


}
