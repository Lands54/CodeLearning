package com.example.iask;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import static com.example.iask.MainActivity.user;
import static java.lang.Thread.sleep;

public class Register extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        //显示register.xml
        setContentView(R.layout.register);
        //设置注册按钮的作用
        findViewById(R.id.save_information).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //读取文本框里的账号和密码
                EditText temp = findViewById(R.id.accountIN);
                String account = temp.getText().toString();
                temp = findViewById(R.id.password);
                String password = temp.getText().toString();
                //新建异步任务，在其中进行注册网络操作
                new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] objects) {
                        boolean temp = user.register(account, password);
                        //temp表示注册是否成功
                        return temp;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        //根据是否成功显示“成功”或“失败”
                        Toast.makeText(getApplicationContext(), (boolean) o? "Success" : "Failed", Toast.LENGTH_LONG).show();
                        //如果不成功则到此为止，什么都不做了
                        if (!(boolean)o)
                            return;
                        //如果成功就跳转回登陆界面
                        Intent intent = new Intent(Register.this, MainActivity.class);
                        startActivity(intent);
                    }
                }.execute();

            }
        });
        //设置上一页跳转会登陆界面
        findViewById(R.id.back_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
