package top.yueshushu.channel.pipe;

import lombok.extern.log4j.Log4j;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.channels.Pipe;

/**
 * @ClassName:PipeTest
 * @Description Pipe 管道
 * @Author zk_yjl
 * @Date 2022/3/15 16:22
 * @Version 1.0
 * @Since 1.0
 **/
@Log4j
public class PipeTest {
    @Test
    public void pipe() throws Exception {
        //1. 创建 Pipe
        Pipe pipe = Pipe.open();
        //2. 创建写入数据的 Buffer
        ByteBuffer writeBuffer = ByteBuffer.allocate(1024);

        writeBuffer.put("Write Data".getBytes());

        writeBuffer.flip();

        //3.  获取 sink() 写通道, 调用 write() 进行写入
        Pipe.SinkChannel sinkChannel = pipe.sink();

        sinkChannel.write(writeBuffer);

        log.info(">>>>写入数据成功");

        //4. 创建 readBuffer 的读取数据存放位置
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);

        readBuffer.clear();

        //5. 获取 source() 读通道 ，调用 read() 进行读取

        Pipe.SourceChannel sourceChannel = pipe.source();

        int length = sourceChannel.read(readBuffer);

        //6. 获取数据信息
        log.info(">>>获取数据信息:" + new String(readBuffer.array(), 0, length));
    }
}
