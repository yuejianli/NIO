#   NIO  同步非阻塞IO

[TOC]





# 前期处理



对应的思维导图地址:    https://www.processon.com/view/link/62247e810e3e74108ca1b5d7

对应的 Github地址:      https://github.com/yuejianli/NIO

## 依赖

全局 pom.xml 依赖

~~~xml
    <dependencies>


        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.13</version>
        </dependency>
        <dependency>
            <groupId>log4j</groupId>
            <artifactId>log4j</artifactId>
            <version>1.2.17</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-log4j12</artifactId>
            <version>1.7.32</version>
        </dependency>

        <!--添加 lombok 依赖，用于日志打印-->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.22</version>
        </dependency>
        <!--用于统计时间-->
        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
            <version>5.7.20</version>
        </dependency>
    </dependencies>
~~~

##  日志配置

log4j.properties

~~~properties
# priority  :debug<info<warn<error
#you cannot specify every priority with different file for log4j
log4j.rootLogger=debug,stdout,info,debug,warn,error 

#console
log4j.appender.stdout=org.apache.log4j.ConsoleAppender 
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout 
log4j.appender.stdout.layout.ConversionPattern= [%d{yyyy-MM-dd HH:mm:ss a}]:%p %l%m%n
#info log
log4j.logger.info=info
log4j.appender.info=org.apache.log4j.DailyRollingFileAppender 
log4j.appender.info.DatePattern='_'yyyy-MM-dd'.log'
log4j.appender.info.File=./src/com/hp/log/info.log
log4j.appender.info.Append=true
log4j.appender.info.Threshold=INFO
log4j.appender.info.layout=org.apache.log4j.PatternLayout 
log4j.appender.info.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss a} [Thread: %t][ Class:%c >> Method: %l ]%n%p:%m%n
~~~



# 项目代码 

##  一.  FileChannel



###  读取文件

~~~java
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

~~~



###  写入数据到文件



~~~java
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
~~~



###  写入大量数据

~~~java
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
~~~



###  其它方法操作

~~~java
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
~~~



###  文件复制1

```
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
```



###  文件复制2

~~~java
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
~~~



###  依次读取

~~~java
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

~~~



###  依次写入

~~~java
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
~~~





##  ServerSocketChannel



###  简单的配置

~~~java
  /**
     * 测试处理
     *
     * @throws Exception 异常
     */
    @Test
    public void simpleTest() throws Exception {
        //1. 准备数据  ByteBuffer  包装到缓冲区
        ByteBuffer byteBuffer = ByteBuffer.wrap(
                "Hello NIO!!!".getBytes()
        );
        //创建 ServerSocketChannel  .open ()
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //进行绑定 指定的端口
        serverSocketChannel.socket().bind(
                new InetSocketAddress(9999)
        );
        //配置为异步信息  false 为异步， true 为同步
        //  serverSocketChannel.configureBlocking(false);
        serverSocketChannel.configureBlocking(true);
        log.info(">>>>开始连接");
        while (true) {
            SocketChannel socketChannel = serverSocketChannel.accept();
            if (null == socketChannel) {
                log.info(">>>>没有客户端连接，2s后重新检测");
                TimeUnit.SECONDS.sleep(2);
            } else {
                log.info("获取远程连接:" + socketChannel.socket().getRemoteSocketAddress());
                //倒回数据，即每次都重新取.
                byteBuffer.rewind();
                socketChannel.write(byteBuffer);
                //连接之后，就关闭。
                socketChannel.close();
            }

        }
    }
~~~



## SocketChannel



###  同步阻塞

~~~java
/**
     * 同步,阻塞， 会一直等待下去
     */
    @Test
    public void blockingTest() throws IOException {
        //1. 创建 SocketChannel 连接
        SocketChannel socketChannel =
                SocketChannel.open(
                        //没有协议
                        new InetSocketAddress(
                                "pre.zkong.com",
                                80
                        )
                );
        //2. 创建 ByteBuffer 连接
        ByteBuffer byteBuffer = ByteBuffer.allocate(102400);
        //3. 将数据写入到 ByteBuffer 里面
        log.info(">>>>将数据写入到 ByteBuffer 缓冲里面");
        socketChannel.read(byteBuffer);
        byteBuffer.flip();
        while (byteBuffer.hasRemaining()) {
            log.info(
                    (char) byteBuffer.get()
            );
        }
        socketChannel.close();
        log.info(">>>将数据输出成功");
    }
~~~



###  异步阻塞

~~~java
/**
     * 异步，会输出打印
     *
     * @throws IOException 异常
     */
    @Test
    public void asyncTest() throws IOException {
        //1. 创建 SocketChannel 连接
        SocketChannel socketChannel =
                SocketChannel.open(
                        //没有协议
                        new InetSocketAddress(
                                "pre.zkong.com",
                                80
                        )
                );
        socketChannel.configureBlocking(false);
        //2. 创建 ByteBuffer 连接
        ByteBuffer byteBuffer = ByteBuffer.allocate(102400);
        //3. 将数据写入到 ByteBuffer 里面
        log.info(">>>>将数据写入到 ByteBuffer 缓冲里面");
        int readCount = socketChannel.read(byteBuffer);
        while (readCount > 0) {
            byteBuffer.flip();
            while (byteBuffer.hasRemaining()) {
                log.info(
                        (char) byteBuffer.get()
                );
            }
            byteBuffer.clear();
            readCount = socketChannel.read(byteBuffer);
        }
        socketChannel.close();
        log.info(">>>将数据输出成功");
    }
~~~



##  DatagramChannel



###  发送数据

~~~java
 /**
     * 发送数据
     *
     * @throws Exception 异常
     */
    @Test
    public void sendTest() throws Exception {
        DatagramChannel datagramChannel = DatagramChannel.open();
        //发送到哪个, 指定地址
        InetSocketAddress inetSocketAddress = new InetSocketAddress(
                "127.0.0.1", 9999);
        ByteBuffer byteBuffer = ByteBuffer.allocate(128);
        byteBuffer.put(Charset.forName("UTF-8").encode(
                "两个蝴蝶飞,你好啊"
        ));
        while (true) {
            // 调用 send 方法，进行发送
            datagramChannel.send(
                    byteBuffer, inetSocketAddress
            );
            //重置
            byteBuffer.rewind();
            log.info(">>>发送数据成功");
            TimeUnit.SECONDS.sleep(1);

        }
    }
~~~



### 接收数据

~~~java
@Test
    public void receiveTest() throws Exception {
        //1. 创建连接
        DatagramChannel datagramChannel = DatagramChannel.open();
        //2. 绑定端口号
        datagramChannel.socket().bind(
                new InetSocketAddress(
                        9999
                )
        );
        //3. 创建ByteBuffer 缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        while (true) {
            //清空
            byteBuffer.clear();
            //接收数据
            SocketAddress socketAddress = datagramChannel.receive(byteBuffer);
            byteBuffer.flip();
            log.info(">>>发送方:" + socketAddress.toString());
            log.info(">>>具体数据:" + Charset.forName(
                    "UTF-8"
            ).decode(byteBuffer));

            datagramChannel.send(ByteBuffer.wrap(
                    "接收到数据了".getBytes("UTF-8")
            ), socketAddress);
            TimeUnit.MILLISECONDS.sleep(500);
        }
    }
~~~



### 端口绑定处理

~~~java
@Test
    public void portTest() throws Exception {
        DatagramChannel datagramChannel = DatagramChannel.open();
        // datagramChannel.configureBlocking(false);
        //绑定到  9998, 当前发送方的 端口号
        datagramChannel.socket().bind(
                new InetSocketAddress(
                        9998
                )
        );
        //连接到 9999, 连接服务器端
        datagramChannel.connect(
                new InetSocketAddress(
                        "127.0.0.1",
                        9999
                )
        );

        log.info(">>>>写入数据");


        //接收数据
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        while (true) {
            datagramChannel.write(
                    ByteBuffer.wrap(
                            "两个蝴蝶飞，你好啊222!!!".getBytes("UTF-8")
                    )

            );

            //先清空， 再写入进去
            readBuffer.clear();
            datagramChannel.read(
                    readBuffer
            );

            readBuffer.flip();

            log.info(">>输出信息:" + Charset.forName(
                    "UTF-8"
            ).decode(readBuffer));

            TimeUnit.MILLISECONDS.sleep(500);
        }


    }
~~~



## Buffer

###  ByteBuffer 缓冲区处理

~~~java
 /**
     * byte 缓冲区处理
     *
     * @throws IOException IO异常
     */
    @Test
    public void byteBufferTest() throws IOException {
        RandomAccessFile randomAccessFile =
                new RandomAccessFile("F:\\Java\\NIO\\Code\\NIO\\read.txt", "rw");
        //写入到数据里面
        FileChannel fileChannel = randomAccessFile.getChannel();
        //2. 创建 ByteBuffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(32);
        //将数据写入到 byteBuffer
        int readCount = fileChannel.read(byteBuffer);
        while (readCount > 0) {
            //调转，切换成读模式
            byteBuffer.flip();

            //读出数据
            while (byteBuffer.hasRemaining()) {
                log.info((char) byteBuffer.get());
            }

            //清空
            byteBuffer.clear();

            readCount = fileChannel.read(byteBuffer);
        }
        fileChannel.close();
        randomAccessFile.close();
        log.info(">>>读取数据成功");
    }
~~~



### Int 缓冲区处理

~~~java
    /**
     * int 缓冲区处理
     */
    @Test
    public void intBufferTest() {
        // 设置容量为 10
        IntBuffer intBuffer = IntBuffer.allocate(10);

        //放置数据
        for (int i = 0; i < intBuffer.capacity(); i++) {
            //放置信息
            intBuffer.put(i * 2);
        }

        //切换成写模式

        intBuffer.flip();

        //读出数据
        while (intBuffer.hasRemaining()) {
            log.info(">>>输出:" + intBuffer.get());
        }

        intBuffer.clear();

        log.info(">>>读取数据成功");
    }

~~~



###  rewind 重读

~~~java
 /**
     * 重读
     *
     */
    @Test
    public void rewindTest() {
        // 设置容量为 10
        IntBuffer intBuffer = IntBuffer.allocate(10);

        //放置数据
        for (int i = 0; i < intBuffer.capacity(); i++) {
            //放置信息
            intBuffer.put(i * 2);
        }

        //切换成写模式
        intBuffer.flip();
        log.info(">>>> 第一回输出");
        while (intBuffer.hasRemaining()) {
            log.info(">>>1输出:" + intBuffer.get());
        }
        log.info(">>>> 再次进行输出");
        // 没有 数据
        while (intBuffer.hasRemaining()) {
            log.info(">>>2输出:" + intBuffer.get());
        }
        //重置
        intBuffer.rewind();
        log.info(">>>> rewind() 后再次进行输出");
        while (intBuffer.hasRemaining()) {
            log.info(">>>3输出:" + intBuffer.get());
        }


        intBuffer.clear();

        log.info(">>>读取数据成功");
    }
~~~



###  clear 清空

~~~java
 /**
     * 全部清空  clear  和 compact 清空已读
     *
     */
    @Test
    public void clearAndCompactTest() {
        // 设置容量为 10
        IntBuffer intBuffer = IntBuffer.allocate(10);
        //放置数据
        for (int i = 0; i < intBuffer.capacity(); i++) {
            //放置信息
            intBuffer.put(i * 2);
        }
        //切换成写模式
        intBuffer.flip();
        log.info(">>>>全部清空");
        intBuffer.clear();
        while (intBuffer.hasRemaining()) {
            log.info(">>>全部清空输出:" + intBuffer.get());
        }
        intBuffer = IntBuffer.allocate(10);
        //放置数据
        for (int i = 0; i < intBuffer.capacity(); i++) {
            //放置信息
            intBuffer.put(i * 2);
        }
        //切换成写模式
        intBuffer.flip();
        //先读两个
        // 0 2
        for (int i = 0; i < 2; i++) {
            log.info(">>>先读两个输出:" + intBuffer.get());
        }
        log.info(">>>>已读清空");
        /*
         * 调用 compact 之前，    position 为 2， limit 为 10
         *  调用之后   position  为  10-2 =8， limit 为 10
         */
        log.info(">>>调用前 limit 输出位置:" + intBuffer.limit());
        log.info(">>>调用前 position 输出位置:" + intBuffer.position());
        intBuffer.compact();
        log.info(">>>调用后 limit 输出位置:" + intBuffer.limit());
        log.info(">>>调用后 position 输出位置:" + intBuffer.position());
        while (intBuffer.hasRemaining()) {
            //  16  18
            log.info(">>>已读清空输出:" + intBuffer.get());
        }
        log.info(">>>读取数据成功");
    }

~~~



###  mark 标记

~~~java
 /**
     * mark 标记
     * 和 reset 重置标记
     *
     */
    @Test
    public void markAndResetTest() {
        // 设置容量为 10
        IntBuffer intBuffer = IntBuffer.allocate(10);

        //放置数据
        for (int i = 0; i < intBuffer.capacity(); i++) {
            //放置信息
            intBuffer.put(i * 2);
        }

        //切换成写模式
        intBuffer.flip();
        // 0 2
        //先读两个
        for (int i = 0; i < 2; i++) {
            log.info(">>>先读两个输出:" + intBuffer.get());
        }
        log.info(">>>读两个之后，设置标记 mark ");
        intBuffer.mark();
        //读剩下的
        while (intBuffer.hasRemaining()) {
            log.info(">>>读取剩下的:" + intBuffer.get());
        }
        //重置标记，继续读以前的 2~9
        intBuffer.reset();
        log.info(">>>重置  reset 之后，继续读取");
        while (intBuffer.hasRemaining()) {
            log.info(">>>重读剩下的:" + intBuffer.get());
        }
        intBuffer.clear();

        log.info(">>>读取数据成功");
    }
~~~



###  缓冲区分类

####  子缓冲区

~~~java
 /**
     * 子缓冲区大小
     */
    @Test
    public void sliceTest() {
        IntBuffer intBuffer = IntBuffer.allocate(10);
        //放置信息
        for (int i = 0; i < intBuffer.capacity(); i++) {
            intBuffer.put(i);
        }
        //这是原有的信息

        //3. 创建子缓冲区   只在 3~7 时创建
        intBuffer.position(3);
        intBuffer.limit(7);
        IntBuffer sliceBuffer = intBuffer.slice();
        //将这一段子缓冲区的信息改变
        log.info("改变子缓冲区的内容信息");
        for (int i = 0; i < sliceBuffer.capacity(); i++) {
            //扩大10倍
            int index = sliceBuffer.get();
            sliceBuffer.put(i, index * 10);
        }
        //重新读
        intBuffer.position(0);
        intBuffer.limit(intBuffer.capacity());
        while (intBuffer.hasRemaining()) {
            log.info(">>>读取信息:" + intBuffer.get());
        }
        // 输出信息:  0  1  2 30 40 50 60 7 8 9
    }
~~~



####  只读缓冲区

~~~java
/**
     * 只读缓冲区
     */
    @Test
    public void readOnlyTest() {
        IntBuffer intBuffer = IntBuffer.allocate(10);
        //放置信息
        for (int i = 0; i < intBuffer.capacity(); i++) {
            intBuffer.put(i);
        }
        //这是原有的信息
        //3. 创建子缓冲区   只在 3~7 时创建
        IntBuffer readOnlyBuffer = intBuffer.asReadOnlyBuffer();
        //将这一段子缓冲区的信息改变
        log.info("改变子缓冲区的内容信息");
        intBuffer.flip();
        for (int i = 0; i < intBuffer.capacity(); i++) {
            //扩大10倍
            int index = intBuffer.get();
            intBuffer.put(i, index * 10);
        }
        //重新读
        readOnlyBuffer.position(0);
        readOnlyBuffer.limit(intBuffer.capacity());
        intBuffer.flip();
        while (readOnlyBuffer.hasRemaining()) {
            log.info(">>>只读缓冲区信息:" + readOnlyBuffer.get());
        }
        // 输出信息:  0  10  20 30 40 50 60 70 80 90
    }

~~~



####  直接缓冲区

~~~java
 /**
     * 直接缓冲区
     *
     * @throws Exception 异常
     */
    @Test
    public void allocateDirectTest() throws Exception {

        //定义两个文件流
        RandomAccessFile randomAccessFile = new RandomAccessFile("F:\\Java\\NIO\\Code\\NIO\\read.txt", "rw");
        FileChannel sourceChannel = randomAccessFile.getChannel();

        RandomAccessFile copyFile = new RandomAccessFile("F:\\Java\\NIO\\Code\\NIO\\readCopy.txt", "rw");
        FileChannel targetChannel = copyFile.getChannel();

        //1. 创建一个直接缓冲区

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);

        //往里面写入数据
        while (true) {
            int readCount = sourceChannel.read(byteBuffer);
            //读取不到，则退出循环，完成复制
            if (readCount > -1) {
                break;
            }
            byteBuffer.flip();

            //写入进去
            targetChannel.write(byteBuffer);

        }
        log.info(">>>>写入数据成功");

    }
~~~



####  内存映射IO

~~~java
 /**
     * 内存映射IO
     *
     * @throws Exception 异常信息
     */
    @Test
    public void mapTest() throws Exception {

        //定义两个文件流
        RandomAccessFile randomAccessFile = new RandomAccessFile("F:\\Java\\NIO\\Code\\NIO\\read.txt", "rw");
        FileChannel fileChannel = randomAccessFile.getChannel();

        //内存映射IO
        MappedByteBuffer mappedByteBuffer = fileChannel.map(
                FileChannel.MapMode.READ_WRITE, 0, 1024
        );
        //更改相应部分的数据。  不能超过原文件的大小
        mappedByteBuffer.putChar(
                2, 'L'
        );
        mappedByteBuffer.putChar(4, 'O');
        mappedByteBuffer.putChar(6, 'V');
        mappedByteBuffer.putChar(8, 'E');
        log.info(">>>>写入数据成功");

    }
~~~



## Selector

###  服务器端

~~~java
@Test
    public void serverTest() throws Exception {
        //1. 构建 ServerSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //进行绑定
        serverSocketChannel.socket().bind(
                new InetSocketAddress(
                        9997
                )
        );
        //2. 设置异步
        serverSocketChannel.configureBlocking(false);

        //3. 创建 Selector
        Selector selector = Selector.open();
        //4. 注册事件  接收连接事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        //5. 构建可读，可写的 Buffer 信息
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);

        ByteBuffer writeBuffer = ByteBuffer.allocate(1024);

        writeBuffer.put("Hello,I am Server".getBytes());

        //切换
        writeBuffer.flip();

        while (true) {
            //6.获取信息
            int select = selector.select();
            log.info("输出数量" + select);
            Set<SelectionKey> selectionKeySet = selector.selectedKeys();
            //如果有的话，进行处理
            Iterator<SelectionKey> iterator = selectionKeySet.iterator();
            while (iterator.hasNext()) {
                //获取下一下
                SelectionKey selectionKey = iterator.next();
                //移除
                iterator.remove();

                //对类型处理判断
                if (selectionKey.isAcceptable()) {
                    //连接状态
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    log.info("获取一个连接: " + socketChannel);
                    //注册读事件
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                }
                //是读的话
                if (selectionKey.isReadable()) {
                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

                    //读信息
                    readBuffer.clear();

                    socketChannel.read(readBuffer);

                    //flip
                    readBuffer.flip();

                    //写数据

                    log.info(">>>读取的数据:" + new String(readBuffer.array()));

                    //注册写事件
                    selectionKey.interestOps(SelectionKey.OP_WRITE);

                }
                if (selectionKey.isWritable()) {

                    //写事件
                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

                    writeBuffer.rewind();

                    socketChannel.write(writeBuffer);

                    log.info(">>>写数据数据:" + new String(writeBuffer.array()));
                    selectionKey.interestOps(SelectionKey.OP_READ);


                }
            }
            TimeUnit.SECONDS.sleep(1);
        }
    }
~~~



### 客户端

~~~java
 @Test
    public void clientTest() throws Exception {
        //1. 创建 SocketChannel
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(
                new InetSocketAddress(
                        "127.0.0.1",
                        9997
                )
        );
        socketChannel.configureBlocking(false);

        //读写 Buffer
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);

        ByteBuffer writeBuffer = ByteBuffer.allocate(1024);

        writeBuffer.put("Hello,I am Client".getBytes());

        //写数据，然后读数据

        while (true) {
            writeBuffer.rewind();
            readBuffer.clear();
            socketChannel.read(readBuffer);
            socketChannel.write(writeBuffer);

            TimeUnit.SECONDS.sleep(1);
        }
    }
~~~



##  Pipe 管道

###  Pipe测试

~~~java
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
~~~



##  Path



###  Path简单构建

~~~java
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
~~~



### Path转换成File

~~~java
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
~~~



## 文件锁

###  写文件锁

~~~java
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
~~~

### 读文件锁

~~~java
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
~~~



## Files 工具类

### 创建目录

~~~java
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
~~~

### 文件复制

~~~java
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
~~~



### 文件移动

~~~java
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
~~~



### 文件删除

~~~java
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

~~~

### 文件遍历

~~~java
    /**
     * 文件遍历处理
     * @throws Exception
     */
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
~~~



## Charset 字符集

###  获取可用编码

~~~java
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
~~~



### 字符集 UTF8

~~~java
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
~~~



### 字符编码测试

~~~java
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
~~~



## 异步数据  AsynchronousFileChannel

### 异步读1



~~~java
/**
     * Future 异步形式读取文件
     */
    @Test
    public void futureReadTest() throws Exception {

        Path path = Paths.get("D:\\filelock.txt");

        //创建 异步读
        AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(path,
                StandardOpenOption.READ);

        //放置到 Future 里面
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        Future<Integer> readFuture = asynchronousFileChannel.read(byteBuffer, 0);
        //如果没有完成，则一直循环
        while (!readFuture.isDone()) {

        }
        //获取信息
        log.info(">>>读取长度:" + readFuture.get());

        byteBuffer.flip();

        //切换之后，输出信息
        log.info("信息：" + new String(byteBuffer.array()));
    }

~~~



### 异步读 handler

~~~java
 /**
     * handler 形式的读
     */
    @Test
    public void handlerReadTest() {

        try {
            Path path = Paths.get("D:\\filelock.txt");

            AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.READ);

            //构建 ByteBuffer
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

            asynchronousFileChannel.read(
                    byteBuffer, 0, byteBuffer,
                    new CompletionHandler<Integer, ByteBuffer>() {
                        @Override
                        public void completed(Integer result, ByteBuffer attachment) {
                            log.info(">>>字节数:" + result);
                            // 放置到 attachment 里面了
                            attachment.flip();
                            log.info(">>>读取内容:" + new String(attachment.array()));
                        }

                        @Override
                        public void failed(Throwable exc, ByteBuffer attachment) {
                            log.error(">>>读取失败");
                        }
                    }
            );
            //需要休眠处理
            TimeUnit.SECONDS.sleep(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
~~~



### 异步写

~~~java
/**
     * Future 异步形式写入文件
     */
    @Test
    public void futureWriteTest() throws Exception {

        Path path = Paths.get("D:\\filelock.txt");

        //创建 异步读
        AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(path,
                StandardOpenOption.WRITE);

        //放置到 Future 里面
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put("我是个好人".getBytes());

        byteBuffer.flip();

        Future<Integer> writeFuture = asynchronousFileChannel.write(byteBuffer, 0);
        //如果没有完成，则一直循环
        while (!writeFuture.isDone()) {

        }
        log.info(">>>>>写入成功");
    }

~~~



### handler 形式异步写

~~~java
/**
     * handler 形式的写
     */
    @Test
    public void handlerWriteTest() {

        try {
            Path path = Paths.get("D:\\filelock.txt");

            AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.WRITE);

            //构建 ByteBuffer
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            byteBuffer.put("我是个好人2222".getBytes());
            byteBuffer.flip();
            asynchronousFileChannel.write(
                    byteBuffer, 0, byteBuffer,
                    new CompletionHandler<Integer, ByteBuffer>() {
                        @Override
                        public void completed(Integer result, ByteBuffer attachment) {
                            log.info(">>>写入内容:" + new String(attachment.array()));
                        }

                        @Override
                        public void failed(Throwable exc, ByteBuffer attachment) {
                            log.error(">>>写入失败");
                        }
                    }
            );
            //需要休眠处理
            TimeUnit.SECONDS.sleep(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
~~~



# 聊天室应用

## 服务器端

~~~java
@Log4j
public class ChatServer {

    /**
     * 启动服务器
     * @date 2022/3/16 8:40
     * @author zk_yjl
     */
    public void start() throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(
                new InetSocketAddress(9997)
        );
        //设置成 异步的
        serverSocketChannel.configureBlocking(false);

        //创建   Selector 并注册
        Selector selector = Selector.open();

        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println(">>>>服务器已经成功启动");
        //进行循环处理
        while (true) {
            int selectCount = selector.select();
            if (selectCount <= 0) {
                continue;
            }
            //进行处理
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            TimeUnit.MILLISECONDS.sleep(100);
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                //获取下一个
                SelectionKey selectionKey = iterator.next();
                //对条件进行判断
                iterator.remove();
                if (selectionKey.isAcceptable()) {
                    handlerAccept(
                            serverSocketChannel, selector
                    );
                }
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
     * @param selectionKey  选择key
     * @param selector 选择器
     * @date 2022/3/16 8:49
     * @author zk_yjl
     */
    private void handlerRead(SelectionKey selectionKey, Selector selector) throws Exception {
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

            //将这个消息，发送给其他的人
            sendMessageToOther(
                    message, selector, socketChannel
            );
        }
    }

    /**
     * 发送消息给其他人
     *
     * @param message 消息
     * @param selector 选择器
     * @param socketChannel channel
     * @date 2022/3/16 8:55
     * @author zk_yjl
     */
    private void sendMessageToOther(String message, Selector selector, SocketChannel socketChannel) throws Exception {
        //1. 获取到现在的信息
        Set<SelectionKey> selectionKeys = selector.keys();
        for (SelectionKey selectionKey : selectionKeys) {
            //进行判断.
            SelectableChannel channel = selectionKey.channel();
            if (channel instanceof SocketChannel && channel != socketChannel) {
                SocketChannel tempChannel = (SocketChannel) channel;
                tempChannel.write(
                        StandardCharsets.UTF_8.encode(
                                message
                        )
                );
            }
        }
    }

    /**
     * 接收到连接时，发送一个欢迎的信息
     *
     * @param serverSocketChannel channel
     * @param selector  选择器
     * @date 2022/3/16 8:45
     * @author zk_yjl
     */
    private void handlerAccept(ServerSocketChannel serverSocketChannel, Selector selector) throws Exception {
        SocketChannel socketChannel = serverSocketChannel.accept();
        Charset charset = StandardCharsets.UTF_8;
        socketChannel.configureBlocking(false);
        //注册可读事件
        socketChannel.register(
                selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE
        );
        socketChannel.write(
                charset.encode(
                        "欢迎你来到聊天室，注意隐私安全"
                )
        );


    }

    public static void main(String[] args) {

        try {
            new ChatServer().start();
            System.out.println(">>>>启动服务器成功");
        } catch (Exception e) {
            log.info(">>>启动服务器失败 {}", e);
        }

    }
}
~~~

## 客户端

### 客户端启动

~~~java
public class ChatClient {

    /**
     * 客户端处理
     *
     启动客户端
     * @date 2022/3/16 9:03
     * @author zk_yjl
     */
    public void start(String name) throws Exception {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(
                new InetSocketAddress(
                        "127.0.0.1",
                        9997
                )
        );
        socketChannel.configureBlocking(false);

        //创建Select

        Selector selector = Selector.open();

        //注册可读事件
        socketChannel.register(
                selector,
                SelectionKey.OP_READ
        );

        new Thread(
                new ClientThread(selector)
        ).start();
        //进行处理
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            //获取到下一行
            String message = scanner.nextLine();
            socketChannel.write(
                    StandardCharsets.UTF_8
                            .encode(
                                    name + ":" + message
                            )
            );
        }
    }
}
~~~



### 客户端线程

~~~java
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
~~~



## 聊天室人员实例化

###  A 用户

~~~java
public class AClient {

    public static void main(String[] args) {
        try {
            new ChatClient().start("A");
        } catch (Exception ignored) {

        }
    }
}

~~~



### B 用户

~~~java
public class BClient {

    public static void main(String[] args) {
        try {
            new ChatClient().start("B");
        } catch (Exception ignored) {

        }
    }
}

~~~



###  C 用户

~~~java
public class CClient {

    public static void main(String[] args) {
        try {
            new ChatClient().start("C");
        } catch (Exception ignored) {

        }
    }
}

~~~

