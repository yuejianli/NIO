package top.yueshushu.channel.charset;

import lombok.extern.log4j.Log4j;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Map;
import java.util.SortedMap;

/**
 * @ClassName:CharsetTest
 * @Description 字符集相关的操作
 * @Author zk_yjl
 * @Date 2022/3/15 19:23
 * @Version 1.0
 * @Since 1.0
 **/
@Log4j
public class CharsetTest {
    /**
     * 获取可用的编码方式
     */
    @Test
    public void avaliableCharsetsTest() {

        SortedMap<String, Charset> charsetMap = Charset.availableCharsets();
        for (Map.Entry<String, Charset> entry : charsetMap.entrySet()) {
            log.info(entry.getKey() + "--->" + entry.getValue());
        }

    }

    @Test
    public void charsetUtf8Test() {
        CharBuffer charBuffer = CharBuffer.allocate(1024);
        charBuffer.put("两个蝴蝶飞".toCharArray());
        charBuffer.flip();

        //进行处理
        for (int i = 0; i < charBuffer.limit(); i++) {
            log.info(charBuffer.get());
        }
    }

    /**
     * 进行编码测试
     */
    @Test
    public void charsetTest() throws Exception {
        //指令默认的 UTF-8
        Charset charset = Charset.forName("UTF-8");

        //获取对应的编码器和解码器
        CharsetEncoder charsetEncoder = charset.newEncoder();

        CharsetDecoder charsetDecoder = charset.newDecoder();


        CharBuffer charBuffer = CharBuffer.allocate(1024);
        charBuffer.put("两个蝴蝶飞".toCharArray());
        charBuffer.flip();

        //进行重新编码
        ByteBuffer encodeBuffer = charsetEncoder.encode(charBuffer);
        log.info("编码后:");
        for (int i = 0; i < encodeBuffer.limit(); i++) {
            log.info(encodeBuffer.get());
        }

        log.info("解码后:");
        encodeBuffer.flip();
        //进行解码
        CharBuffer decodeBuffer = charsetDecoder.decode(encodeBuffer);
        for (int i = 0; i < decodeBuffer.limit(); i++) {
            log.info(decodeBuffer.get());
        }
        //字符串解码方法
        encodeBuffer.rewind();
        log.info(">>>解码UTF8:" + new String(decodeBuffer.array()));


        //指定 GBK 进行解码
        log.info(">>>>其他方式进行解码:");
        Charset charset1 = Charset.forName("GBK");
        CharsetDecoder charsetDecoder1 = charset1.newDecoder();
        encodeBuffer.flip();
        CharBuffer decodeBuffer1 = charsetDecoder1.decode(encodeBuffer);
        decodeBuffer1.rewind();
        for (int i = 0; i < decodeBuffer1.limit(); i++) {
            log.info(decodeBuffer1.get());
        }
        //无法解析，为空
        decodeBuffer1.flip();
        log.info(">>>解码GBK:" + decodeBuffer1.toString());


    }

}
