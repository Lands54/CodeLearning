//byh
package org.example.ClientBlock;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

class ClientDecoder {
        SocketChannel serverChannel;
        InetSocketAddress serverAddress;
        ByteBuffer byteBuffer = ByteBuffer.allocate(65535);
        public ClientDecoder(String ip, int port){
            this.serverAddress = new InetSocketAddress(ip, port);
            try {
                connect(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public String[] commandHandle(String method, String body) {
            try {
                this.send(method, body);
                String rec = this.receive();
                return regular(rec.substring(0, rec.length() - "@END".length()).split("@"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public String[] regular(String[] strings){
            for(int i = 0; i < strings.length; i++){
                strings[i] = strings[i].replace("~a~", "@");
            }
            return strings;
        }

        public boolean connect(boolean n) throws IOException {
            this.serverChannel = SocketChannel.open();
            this.serverChannel.configureBlocking(n);
            boolean temp = this.serverChannel.connect(this.serverAddress);
            while (!this.serverChannel.finishConnect()) ;
            if (this.serverChannel.finishConnect()) {
                System.out.println("Connection built");
            } else {
                System.out.println("Connection failed");
            }
            return temp;
        }

        private String commandBuild(String method, String body){
            StringBuilder stringBuilder = new StringBuilder(Client.user.accountID);
            stringBuilder.append("@");
            stringBuilder.append(method);
            stringBuilder.append("@");
            stringBuilder.append(body);
            return stringBuilder.toString();
        }

        public void send(String method, String body) throws IOException {
            String command = commandBuild(method, body);
            System.out.println("SEND" + command);
            this.serverChannel.write(ByteBuffer.wrap(command.getBytes(StandardCharsets.UTF_8)));
        }

        public String receive() throws IOException {
            StringBuilder stringBuilder = new StringBuilder();
            while (!endDetect(stringBuilder.toString())){
                byteBuffer.clear();
                this.serverChannel.read(byteBuffer);
                byteBuffer.flip();
                stringBuilder.append(StandardCharsets.UTF_8.decode(this.byteBuffer).toString());
            }
            System.out.println("RECE" + stringBuilder.toString());
            return stringBuilder.toString();
        }

        public boolean endDetect(String s){
            String string = null;
            try {
                String[] strings = s.split("@");
                string = strings[strings.length - 1];
            } catch (Exception e) {
                return false;
            }
            return string.equals("END");
        }
    }