package top.yueshushu.chat;

/**
 * @ClassName:AClient
 * @Description A用户客户端
 * @Author zk_yjl
 * @Date 2022/3/16 11:23
 * @Version 1.0
 * @Since 1.0
 **/
public class AClient {

    public static void main(String[] args) {
        try {
            new ChatClient().start("A");
        } catch (Exception ignored) {

        }
    }
}
