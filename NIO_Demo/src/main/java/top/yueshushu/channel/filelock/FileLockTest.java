package top.yueshushu.channel.filelock;

import lombok.extern.log4j.Log4j;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @ClassName:FileLockTest
 * @Description 文件锁测试
 * @Author zk_yjl
 * @Date 2022/3/15 16:51
 * @Version 1.0
 * @Since 1.0
 **/
@Log4j
public class FileLockTest {
    @Test
    public void writeFile() throws Exception {
        //1. 构建 ByteBuffer,写的内容信息
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put(" I am FileLock".getBytes());

        //2. 构建文件 Channel
        String filePath = "D:\\filelock.txt";

        Path path = Paths.get(
                filePath
        );
        //  StandardOpenOption  定义参数，  写入，追加内容
        FileChannel fileChannel = FileChannel.open(
                path,
                StandardOpenOption.WRITE,
                StandardOpenOption.APPEND
        );

        // FileLock fileLock = fileChannel.tryLock();

        // FileLock fileLock = fileChannel.lock();
        FileLock fileLock = fileChannel.tryLock(0, Long.MAX_VALUE, false);
        if (null == fileLock) {
            log.info(">>>>>没有获取到锁");
            return;
        }
        //写入内容
        log.info(">>>>获取到锁了");
        byteBuffer.flip();
        fileChannel.write(byteBuffer);


        //释放锁
        fileLock.release();
    }

    @Test
    public void readTest() throws Exception {
        String filePath = "D:\\filelock.txt";
        //包装类，  FileReader   BufferedReader
        FileReader fileReader = new FileReader(filePath);

        BufferedReader bufferedReader = new BufferedReader(
                fileReader
        );

        String content = bufferedReader.readLine();


        while (content != null && content.length() > 0) {
            log.info(content);
            content = bufferedReader.readLine();
        }

        fileReader.close();

        bufferedReader.close();
    }
}
