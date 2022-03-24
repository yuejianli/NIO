package top.yueshushu.channel.files;

import lombok.extern.log4j.Log4j;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName:FilesTest
 * @Description 文件类 Files 测试
 * @Author zk_yjl
 * @Date 2022/3/15 17:53
 * @Version 1.0
 * @Since 1.0
 **/
@Log4j
public class FilesTest {
    /**
     * 创建目录
     * 传入的是 Path 类， 返回一个新的 Path 实例。
     * <p>
     * 如果目录存在，抛出  FileAlreadyExistsException
     * <p>
     * 如果新目录的父目录不存在， IOException
     * <p>
     * 其他系统信息，抛出 IOException
     */
    @Test
    public void createDirectoryTest() throws IOException {

        Path path = Paths.get("D:\\newPath");
        // Path path2 = Paths.get("D:\\a\\b");   // 创建不成功

        Path directory = Files.createDirectory(path);

        log.info(directory);
    }

    /**
     * 如果目标文件已经存在，抛出 FileAlreadyExistsException 异常。
     * <p>
     * 文件复制到不存在的目录，抛出 IOException
     * <p>
     * 有其他错误，抛出 IOException
     */
    @Test
    public void copyTest() throws IOException {
        Path source = Paths.get("D:\\filelock.txt");
        Path target = Paths.get("D:\\filelockcopy.txt");

        //复制文件
        // Path copy = Files.copy(source, target);

        //替换
        Path copy2 = Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * 可以从一个路径移动到另外一个路径。
     * 也可以移动到相同的路径，但是文件名不同，这个时候，表示重命名
     */
    @Test
    public void moveTest() throws IOException {
        Path source = Paths.get("D:\\filelock.txt");
        Path target = Paths.get("D:\\filelockcopy.txt");

        // Path move = Files.move(source, target);

        Path move2 = Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);

    }


    /**
     * 删除文件
     * <p>
     * 不能直接删除有文件的目录
     */
    @Test
    public void deleteTest() throws IOException {
        // Path delFile1 = Paths.get("D:\\a\\b\\a.txt");

        Path delFile2 = Paths.get("D:\\a\\b");
        //Path delFile3 = Paths.get("D:\\a");
        //删除文件
        Files.delete(delFile2);
    }

    @Test
    public void walkFileTreeTest() throws Exception {

        Path rootPath = Paths.get("D:\\a");
        List<String> searchFileList = new ArrayList<>();
        Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                // 获取文件的绝对路径
                String fileString = file.toAbsolutePath().toString();

                //如果以 .txt 的话，进行处理
                if (fileString.endsWith(".txt")) {
                    searchFileList.add(fileString);
                }
                return FileVisitResult.CONTINUE;
            }
        });

        searchFileList.forEach(
                log::info
        );

    }
}
