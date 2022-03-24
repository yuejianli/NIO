package top.yueshushu.channel.path;

import lombok.extern.log4j.Log4j;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @ClassName:PathTest
 * @Description Path 类测试
 * @Author zk_yjl
 * @Date 2022/3/15 17:42
 * @Version 1.0
 * @Since 1.0
 **/
@Log4j
public class PathTest {

    @Test
    public void pathTest() {

        Path path = Paths.get("D:\\filelock.txt");

        log.info("path:" + path);


        Path path2 = Paths.get("D:\\", "filelock.txt");

        log.info("path2:" + path2);

        //标准化输出

        Path path3 = Paths.get("D:\\\\\\filelock.txt");
        path3 = path3.normalize();
        log.info("path3:" + path3);
    }


    @Test
    public void pathToFileTest() {

        Path path = Paths.get("D:\\filelock.txt");

        log.info("path:" + path);

        File file = path.toFile();
        log.info("文件:" + file.length());

        //置换成 path
        Path path1 = file.toPath();
        log.info("path1:" + path1);
    }


}
