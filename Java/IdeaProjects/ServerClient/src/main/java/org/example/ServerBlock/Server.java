//qby
package org.example.ServerBlock;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;

//允许被多进程并发执行的容器
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static java.lang.System.*;
import static java.lang.Thread.sleep;

public class Server {
    //loginBuffer是一个哈希表，其为未登录用户提供从AccountID到目标连接的映射
    ConcurrentHashMap<String, SocketChannel> loginBuffer = new ConcurrentHashMap<>();
    //SocketChannelMap为已登陆用户提供AccountID到目标连接的映射
    ConcurrentHashMap<String, SocketChannel> SocketchannelMap = new ConcurrentHashMap<>();
    //commandBuffer为任务队列，存放用户到来的任务，并且作为任务处理进程从其中抓取命令
    private LinkedBlockingQueue<String> commendBuffer = new LinkedBlockingQueue<>();
    //任务处理进程将处理结果存防至sendBuffer，通过Server进行发送
    private LinkedBlockingQueue<String> sendBuffer = new LinkedBlockingQueue<>();
    //存放目前已经建立的连接
    ArrayList<SocketChannel> connectedList = new ArrayList<>();
    //监听器，用来监听目标事件，当需要从连接中接收或，向连接中发送的时候将唤醒
    private Selector listener = Selector.open();

    //Server构造函数
    public Server(int port) throws IOException {
        //创建服务器本身的连接端
        ServerSocketChannel server = ServerSocketChannel.open();
        //将其绑定在本地的port的端口 127.0.0.1:9999
        server.bind(new InetSocketAddress(port));
        //设置通信模式为非阻塞通信
        server.configureBlocking(false);
        //监听服务器收到的“连接”事件
        SelectionKey key = server.register(listener, SelectionKey.OP_ACCEPT);
        out.println("Server.Server has been built");
        //新建程序数据库
        Person.personSQL = new PersonSQL();
        //将本服务器绑定值Person
        Person.server = this;
        try {
            //检查路径中是否包含数据库文件
            File temp = new File("iASK.db");
            if (!temp.exists()){
                out.println("DB NOT EXIST, try to create");
                //文件不存在对数据进行初始化
                Person.personSQL.initDataBase();
                out.println("complete");
            }
            //建立对数据库文件的连接
            Person.personSQL.createDB("iASK");
            //开启数据库可执行sql语句模式
            Person.personSQL.setStmt();
            out.println("DB has been Loaded");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            out.println("Server start:" + port);
            //服务器开始执行
            this.serverManage();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String getCommand() {
        try {
            return this.commendBuffer.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void readyToWrite(String AccountID){
        SocketChannel temp = this.loginBuffer.get(AccountID);
        if (temp == null)
            temp = this.SocketchannelMap.get(AccountID);
        try {
            temp.register(this.listener, SelectionKey.OP_WRITE);
        } catch (ClosedChannelException e) {
            try {
                temp.close();
            } catch (IOException ex) {
            }
        }
    }

    public void putMessage(String message0, String message1, String message2) {
        this.sendBuffer.add(message0 + "@" + message1 + "@" + message2);
        this.readyToWrite(message0);
        this.listener.wakeup();
    }

    private int numberOfSocketChannel() {
        Set<SelectionKey> keySet = listener.keys();
        Iterator<SelectionKey> keyIterator = keySet.iterator();
        HashSet<SocketChannel> socketChannelSet = new HashSet<SocketChannel>();
        return loginBuffer.size() + SocketchannelMap.size();
    }

    private void showCommandBuffer() {
        for (String command :
                this.commendBuffer) {
            out.println("|" + command + "|");
        }
    }

    //该函数为服务器主函数，其负责1.创建命令处理进程 2.维护登录连接表 3.对请求操作的连接 4.对已连接请求进行接收 5.对刚刚发送完连接的请求
    // 在处理进程处理完毕后将结果进行发送
    public void serverManage() throws IOException, InterruptedException {
        //创建负责命令处理的对象

        //将命令处理作为新线程进行创建，此时处理进程与Server进程同时运行 (可根据需要创建多个命令处理进程)
        Thread[] handler = new Thread[10];
        for (int i = 0; i< 10; i++){
            handler[i] = new Thread(new ServerCommandHandler(this));
            handler[i].start();
        }
        //开始进程·
        //事件Key，用来存放当前进行的处理的事件
        SelectionKey key = null;
        int i = 0;
        out.println("Server build complete");
        while (true) {
            try {
                //选择器对象(后文称监听器)的select函数，在没有监听的事件发生时，会阻塞进程，参数为最长阻塞时间
                listener.select();
                //从监听器中获取当前已经发生的事件
                Set<SelectionKey> keySet = listener.selectedKeys();
                //将其转化为迭代器，迭代器是一个对象，他的next函数从一个“列表”中输出一个元素，并且下一次next输出下一个元素，直至遍历
                Iterator<SelectionKey> keyIterator = keySet.iterator();
                //当还有下一个事件的时候运行，将当前正在处理的事件从迭代器中移除，并且将事件送如“事件处理函数”
                while (keyIterator.hasNext()) {
                    key = keyIterator.next();
                    keyIterator.remove();
                    opDetectServer(key);
                }
                //显示当前服务器存在的所有连接数
                out.println("Number of Connection is " + numberOfSocketChannel() + ":" + i++);
                i = i > 1000? 0 : i;
            } catch (IOException e) {
                //当事件处理产生意外，消除本事件，防止意外
                key.channel().close();
                key.cancel();
                out.println("Error Connection, Server has closed it");
            } catch (NullPointerException e) {
                out.println("?");
                //当发生空指针事件，可能是存在进程通信时间问题，延迟100ms
                sleep(100);
            } catch (Exception e){
                key.channel().close();
                key.cancel();
                e.printStackTrace();
                out.println("Unknown Error, Server has closed it");
            }

        }
    }

    private void opDetectServer(SelectionKey key) throws IOException, NullPointerException {
        //命令处理进程负责 1. 对连接事件，建立连接 2.对发送事件读取命令 3.对命令处理事件的结果进行发送 4.拦截非法命令如非登录查看数据，登录账号A，发送来自账号B的请求
        //              5.维护两个登录表
        StringBuffer stringBuffer = new StringBuffer();
        //字节缓存器，缓存来自用户的数据
        ByteBuffer byteBuffer = ByteBuffer.allocate(65565);
        //获取发生事件的 用户端
        SelectableChannel tempServer = key.channel();
        //key to Int 函数负责将事件类型转化为0 1 2 3四个整数
        switch (keyToInt(key)) {
            case 0:
                //当服务器收到建立连接事件
                ServerSocketChannel server = (ServerSocketChannel)tempServer;
                out.println("Receive a connection, try to accept");
                //服务器进行连接，返回连接到的用户端
                SocketChannel client = server.accept();
                out.println("Connecting");
                //当连接结束，设置通信模式为非阻塞通信，监听来自用户有没有发来数据，即“可读”
                while (!client.isConnected()) ;
                if (client.finishConnect()) {
                    out.println("Connection Successfully");
                    client.configureBlocking(false);
                    client.register(listener, SelectionKey.OP_READ);
                }
                break;

            case 1:
                //理论上不会发生的事件
                //连接事件
                ((ServerSocketChannel) tempServer).register(listener, SelectionKey.OP_READ);
                break;

            case 2://当目标可读时，发生此事件
                client = (SocketChannel) tempServer;
                //设置非阻塞通信
                client.configureBlocking(false);
                //将数据写到字节缓存起
                while (client.read(byteBuffer) > 0);
                //转化为从字节寄存器中读数据模式
                byteBuffer.flip();
                //当对方持续发送空数据，即断开了连接
                if (!byteBuffer.hasRemaining()) {
                    //切换目标连接，并维护表
                    out.println("disconnect with " + client.getRemoteAddress());
                    for (String k:SocketchannelMap.keySet()){
                        if(SocketchannelMap.get(k) == client){
                            SocketchannelMap.remove(k);
                        }
                    }
                    //将断开连接的端删除
                    client.close();
                    key.cancel();
                    break;
                }
                //将字节缓存器数据读到字符串缓存区
                while (byteBuffer.hasRemaining()) {
                    stringBuffer.append(StandardCharsets.UTF_8.decode(byteBuffer).toString());
                }
                //当命令结构错误放弃本次数据
                if(stringBuffer.toString().split("@").length < 2) {
                    client.write(ByteBuffer.wrap(("unknown@" +stringBuffer.toString()+"@invalid").getBytes()));
                    key.interestOps(SelectionKey.OP_READ);
                    break;
                }
                //当为未登录用户发来非法命令时拦截
                String accountID = stringBuffer.toString().split("@")[0];
                if(!loginRegisterDetect(stringBuffer.toString(), client)){
                    if(!client.equals(SocketchannelMap.get(accountID))){
                        //向端发送 未登录提醒
                        client.write(ByteBuffer.wrap((accountID + "@"+ stringBuffer.toString().split("@")[1] + "@notLogin").getBytes()));
                        break;
                    }
                }
                //数据接收完毕，开始准备向用户端发送数据，监听 用户“可写入时，
                //key.interestOps(SelectionKey.OP_WRITE);
                //向命令Buffer中加入本条命令
                commendBuffer.add(stringBuffer.toString());
                //显示当前命令Buffer
                showCommandBuffer();
                break;

            case 3:
                //发送事件处理
                //从sendBuffer中拿取数据
                String data = sendBuffer.poll();
                //对发送的数据进行检查
                String[] dataSet = data.split("@");
                out.println("Pre:" + data);
                //byteBuffer Init and put
                //将命令加入到字节缓冲器
                byteBuffer.put((data + "@END").getBytes(StandardCharsets.UTF_8));
                byteBuffer.flip();
                //login register
                //如果这是登录命令或注册命令，根据命令第一个参数accountID通过loginBuffer找到需要发送的端口
                if (dataSet[1].equals("login") | dataSet[1].equals("register")) {
                    client = loginRegisterResultServer(data);
                    client.write(byteBuffer);
                } else {
                    //一般命令则通过SocketChannelMap中找到端口
                    client = SocketchannelMap.get(dataSet[0]);
                    //发送
                    client.write(byteBuffer);
                }
                out.println("SEND:" + data);
                //一次收发结束，开始监听目标时候发来信息
                key.interestOps(SelectionKey.OP_READ);
                break;
        }

    }

    //检测命令是否是登录或注册命令，若是则放入loginBuffer，并返回true
    private boolean loginRegisterDetect(String command, SocketChannel channel) {
        String[] temp = command.split("@");
        if (Objects.equals(temp[1], "login") | Objects.equals(temp[1], "register")) {
            loginBuffer.put(temp[0], channel);
            return true;
        }
        return false;
    }

    //对登录注册结果进行处理，若登录 注册 成功，则放入socketchannelMap，并返回之前放入loginBuffer的用户端
    private SocketChannel loginRegisterResultServer(String command) {
        String[] temp = command.split("@");
        SocketChannel tempSocket = loginBuffer.remove(temp[0]);
        if (temp[1].equals("register"))
            return tempSocket;
        if (temp[temp.length - 1].equals("True") ) {
            SocketchannelMap.put(temp[0], tempSocket);
            out.println("new login");
        } else {
            out.println("fail login");
        }
        return tempSocket;
    }

    //根据key的类型返回数字
    private int keyToInt(SelectionKey key) {
        if (key.isAcceptable()) return 0;
        if (key.isConnectable()) return 1;
        if (key.isReadable()) return 2;
        if (key.isWritable()) return 3;
        return -1;
    }

}
