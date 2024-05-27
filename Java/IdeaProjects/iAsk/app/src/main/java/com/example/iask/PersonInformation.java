package com.example.iask;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import org.example.ClientBlock.QuestionList;
import org.example.ClientBlock.User;

import static com.example.iask.MainActivity.user;

public class PersonInformation extends AppCompatActivity {
    //该变量存放目前显示的用户的id
    static int accountID;
    //存放目前显示的用户
    static User tempUser;
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //显示个人信息界面
        setContentView(R.layout.person_information);
        //新建异步任务
        new AsyncTask(){
            @Override
            protected Object doInBackground(Object[] objects) {
                //从服务器获取想要查看的用户的个人信息，存到tempUser
                tempUser = user.watchInformation(accountID);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                //把tempUser里面的数据填写到界面里
                TextView temp = findViewById(R.id.input_username);
                temp.setText(tempUser.userName);
                temp = findViewById(R.id.input_birthday);
                temp.setText(tempUser.birthday);
                temp = findViewById(R.id.input_gender);
                temp.setText(tempUser.gender);
                temp = findViewById(R.id.input_profession);
                temp.setText(tempUser.identity);
                //如果目前查看的用户刚好 和 我自己手机本机登录的用户ID一样
                if (user.accountID.equals(String.valueOf(PersonInformation.accountID))){
                    //则显示更改个人信息按钮
                    findViewById(R.id.change).setVisibility(View.VISIBLE);
                }else {
                    //如果不一样，则不显示个人信息按钮
                    findViewById(R.id.change).setVisibility(View.GONE);
                }
            }
        }.execute();
        findViewById(R.id.change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置修改个人信息按钮作用为跳转到 “修改个人信息界面”
                startActivity(new Intent(PersonInformation.this, InputPersonInformation.class));
                finish();
            }
        });
        findViewById(R.id.back_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到问题列表界面
                startActivity(new Intent(PersonInformation.this, QuestionTable.class));
                finish();
            }
        });
    }
}
