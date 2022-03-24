package top.yueshushu.chat;

/**
 * @ClassName:BClient
 * @Description B客户端
 * @Author zk_yjl
 * @Date 2022/3/16 11:23
 * @Version 1.0
 * @Since 1.0
 **/
public class BClient {

    public static void main(String[] args) {
        try {
            new ChatClient().start("B");
        } catch (Exception ignored) {

        }
    }
}
