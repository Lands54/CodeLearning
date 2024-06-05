//qby
package org.example.ServerBlock;

import static java.lang.Thread.sleep;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Hashtable;

public class ServerCommandHandler implements Runnable{
    //在命令处理线程中，他们共有一个通过 accountID 到 Person实例 的映射表，在此进程中，任何读写命令都抽象为对Person实例方法的执行
    static Hashtable<String, Object> accountTable = new Hashtable<>();
    Server server;
    static int number = 0;
    Boolean shouldStop = false;

    ServerCommandHandler(Server server){
        this.server = server;
    }
    @Override
    public void run() {
        System.out.println("Handler thread start");
        while (!this.shouldStop){
            //从服务器中抓取命令
            String ir = server.getCommand();
            System.out.println("get!");
            try {
                //命令处理进程
                commandHandle(ir);
                number++;
                if (number > 99999)
                    throw new BigException();
            } catch (NullPointerException e){
                try {
                    sleep(100);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            } catch (BigException e) {
                number = 0;
            }
        }
    }
    //！！！
    // 命令格式 账号ID@需要执行的方法名@参数++
    //！！！
    // Command ::= ID@Method@$0@$1@$2+
    // SocketChannel built
    // SocketChannel --send-> ID@Login@Password
    // Login --pass-> new Person
    // Bundle<SocketChannel, Server.Server.Person> --Socket.Request-> equal(MessageID, PersonID) -> Server.Server.Person
    // -> Person.Method(Attributes) {-> WriteBuffer<ID@DATA> -> equal(MessageID, PersonID) -> Socket.Register(OP_WRITE)}
    // Socket.write(WriteBuffer)
    public void commandHandle(String command) {
        //将命令中以@分开，化为各个参数
        String[] runnableCommand = command.split("@");
        String[] args = new String[runnableCommand.length - 2];
        Class identity;
        for (int i = 2; i < runnableCommand.length; i++) {
            args[i - 2] = runnableCommand[i];
        }
        //reflect
        try {
            //基于反射的命令处理机制
            //反射即“通过程序内字符串再将对象进行创建 与 通过字符串 找到对象的方法执行的过程，可以理解为执行“字符串”的过程”
            //此模块将命令转化为可执行代码 根据AccountId获取其对应的Person对象，并执行“Person.方法名(参数1， 参数为2， ....)”
            if (runnableCommand[1].equals("login") | runnableCommand[1].equals("register")){
                //注册与登录命令的实现
                //直接调用抽象类中static方法实现
                Method method = Class.forName("org.example.ServerBlock.Person").getMethod(runnableCommand[1], String.class, String.class);
                Class user = Class.forName("org.example.ServerBlock.Person");
                method.invoke(user, args);
            } else {
                //对于一般命令
                String accountId = runnableCommand[0];
                String methodName = runnableCommand[1];
                //找到其Person对象
                Person requester = (Person) accountTable.get(accountId);
                //根据其对象类型获取类
                if (!requester.identity.equals("Asker")&!requester.identity.equals("Teacher")){
                    identity = Class.forName("org.example.ServerBlock.Swimer");
                } else {
                    identity = Class.forName("org.example.ServerBlock." + requester.identity);
                }
                //获取此类的方法列表，从中找到Method同名的方法
                Method[] methods = identity.getMethods();
                for (Method method : identity.getMethods()){
                    if (method.getName().equals(methodName)){
                        //执行
                        method.invoke(requester, args);
                        return;
                    }
                }
                //没找到命令中的方法，向用户发送数据
                server.putMessage(runnableCommand[0], runnableCommand[1], "errorMethod");
            }
        } catch (Exception e) {
            e.printStackTrace();
            //当方法执行错误向用户发送的数据
            server.putMessage(runnableCommand[0], runnableCommand[1], "errorArgs");
        }
    }

    public void close(){
        this.shouldStop = !this.shouldStop;
    }

    public static void main(String[] args) throws IOException, SQLException, InterruptedException {
        Person.server = new Server(3389);
    }
}
