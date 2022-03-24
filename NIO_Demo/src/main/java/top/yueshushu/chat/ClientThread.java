package top.yueshushu.chat;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName:ClientThread
 * @Description 线程信息
 * @Author zk_yjl
 * @Date 2022/3/16 9:29
 * @Version 1.0
 * @Since 1.0
 **/
@Log4j
public class ClientThread implements Runnable {

    public Selector selector;
    public ClientThread(Selector selector){
        this.selector = selector;
    }
    @SneakyThrows
    @Override
    public void run() {
        //进行循环处理
        while(true){
            int selectCount = selector.select();
            if(selectCount<=0){
                continue;
            }
            //进行处理
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            TimeUnit.MILLISECONDS.sleep(100);
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while(iterator.hasNext()) {
                //获取下一个
                SelectionKey selectionKey = iterator.next();
                //对条件进行判断
                iterator.remove();

                if (selectionKey.isReadable()) {
                    handlerRead(
                            selectionKey, selector
                    );
                }
            }
        }
    }
    /**
     * 接收到消息，进行处理
     *
     * @param selectionKey 选择key
     * @param selector 选择器
     * @date 2022/3/16 8:49
     * @author zk_yjl
     */
    private  void handlerRead(SelectionKey selectionKey, Selector selector) throws Exception {
        //1 获取对应的通道
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        int readLength = socketChannel.read(byteBuffer);
        String message = null;
        if (readLength > 0) {
            //获取信息
            byteBuffer.flip();

            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(byteBuffer);
            //获取到信息
            charBuffer.rewind();
            message = new String(charBuffer.array());
        }

        //注册可读事件
        socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);

        if (message != null) {
            //对消息进行打印
            System.out.println(socketChannel.toString() + "发送的消息是:" + message);
        }
    }
}
