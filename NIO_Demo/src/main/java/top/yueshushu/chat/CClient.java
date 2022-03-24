package top.yueshushu.chat;

/**
 * @ClassName:CClient
 * @Description C客户端
 * @Author zk_yjl
 * @Date 2022/3/16 11:23
 * @Version 1.0
 * @Since 1.0
 **/
public class CClient {

    public static void main(String[] args) {
        try {
            new ChatClient().start("C");
        } catch (Exception ignored) {

        }
    }
}
