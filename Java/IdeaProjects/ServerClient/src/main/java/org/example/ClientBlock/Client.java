//qby
package org.example.ClientBlock;

import static java.lang.Thread.sleep;

public class Client {
    static User user;
    static ClientDecoder clientDecoder;

    public static User open(String ip, int port){
        Client.user = new User();
        Client.clientDecoder = new ClientDecoder(ip, port);
        return user;
    }



}
