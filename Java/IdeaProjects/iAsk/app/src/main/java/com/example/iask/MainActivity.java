//ycx
package com.example.iask;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import org.example.ClientBlock.Client;
import org.example.ClientBlock.QuestionList;
import org.example.ClientBlock.User;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {
    //用户本身，其包含了所有用户操作的方法
    static User user;
    static boolean temp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //显示login.xml中的界面
        setContentView(R.layout.login);
        //用preferences保存之前输入过的账号密码和ip
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        //edit是负责修改preferences的对象
        SharedPreferences.Editor edit = preferences.edit();
        //把之前保存在preferences里的文本写到账号框
        EditText temp = findViewById(R.id.accountIN);
        temp.setText(preferences.getString("AccountID", ""));
        //把之前保存在preferences里的文本写到密码框
        temp = findViewById(R.id.password);
        temp.setText(preferences.getString("Password", ""));
        //把之前保存在preferences里的文本写到ip框
        temp = findViewById(R.id.ip);
        temp.setText(preferences.getString("ip", ""));
        //把之前保存在preferences里的文本写到端口框
        temp = findViewById(R.id.port);
        temp.setText(preferences.getString("port", ""));
        //父类方法(实际没有任何意义)
        super.onCreate(savedInstanceState);
        //设置Link键的功能
        findViewById(R.id.apply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取ip文本框和端口文本框里面的字符
                EditText editText = findViewById(R.id.ip);
                EditText port = findViewById(R.id.port);
                //建立一个和主线程并行运行的新任务
                new AsyncTask(){
                    @Override
                    protected Object doInBackground(Object[] objects) {
                        //在这个任务中执行对服务器的连接
                        //以后可以通过user进行网络操作
                        user = Client.open(editText.getText().toString(), Integer.parseInt(port.getText().toString()));
                        return null;
                    }
                }.execute();

            }
        });
        //设置登录键功能
        findViewById(R.id.save_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    //获取账号 和 密码
                    EditText temp = findViewById(R.id.accountIN);
                    String account = temp.getText().toString();
                    temp = findViewById(R.id.password);
                    String password = temp.getText().toString();

                    //新建异步任务进行 用户账号密码ip端口在preferences的本地保存
                    new AsyncTask() {
                        @Override
                        protected Object doInBackground(Object[] objects) {
                            //获取ip 端口
                            EditText editText = findViewById(R.id.ip);
                            EditText port = findViewById(R.id.port);
                            //让edit准备去 修改preferences中保存的ip和端口和账号和密码
                            edit.putString("ip", editText.getText().toString());
                            edit.putString("port", port.getText().toString());
                            System.out.println(account);
                            edit.putString("AccountID", account);
                            System.out.println(password);
                            edit.putString("Password", password);
                            //执行 登录 网络操作，如果登录成功temp为true，失败temp为false
                            boolean temp = user.login(account, password);
                            //让edit去真的修改preferences
                            edit.apply();
                            if (temp) {
                                //从 网络 获取用户个人信息到本地
                                user.watchSelfInformation();
                            }
                            return temp;
                        }

                        @Override
                        protected void onPostExecute(Object o) {
                            //显示登录是否成功
                            Toast.makeText(getApplicationContext(), (boolean) o? "Success" : "Failed", Toast.LENGTH_LONG).show();
                            //如果失败就return，不往下执行了
                            if (!(boolean) o)
                                return;
                            //成功则执行下列代码
                            //修改PersonInformation界面里保存的账号
                            PersonInformation.accountID = Integer.parseInt(user.accountID);
                            //跳转到问题列表界面
                            startActivity(new Intent(MainActivity.this, QuestionTable.class));
                        }
                    }.execute();
            }
        });
        //设置注册按钮
        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到注册
                Intent intent = new Intent(MainActivity.this, Register.class);
                startActivity(intent);
                //关闭当前页面（如果不关闭页面会越来越多，因为跳转是新建一个页面覆盖在当前页面上方，不会销毁当前页面）
                finish();
            }
        });

    }

}