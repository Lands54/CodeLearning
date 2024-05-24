package org.example.ClientBlock;

public class Test extends Thread{
    static int all = 0;
    int num = 0;
    User user;
    Test(String a, String p){
        this.num = all++;
        user = Client.open("127.0.0.1", 9999);
        user.login(a, p);
    }
    @Override
    public void run() {
        while(true)
            user.changeIdentity("Teacher");
    }

    public static void main(String[] args) {
        Test[] tests = new Test[1000];
        for (int i  = 0; i < 10; i++){
            tests[i] = new Test(args[0], args[1]);
            tests[i].start();
        }
    }
}
