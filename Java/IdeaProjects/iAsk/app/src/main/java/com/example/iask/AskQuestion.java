package com.example.iask;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import org.example.ClientBlock.Client;
import org.example.ClientBlock.Question;

import static com.example.iask.MainActivity.user;

public class AskQuestion extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //显示问问题界面
        setContentView(R.layout.ask_question);
        //设置返回按钮，返回到问题列表
        findViewById(R.id.back_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AskQuestion.this, QuestionTable.class));
                finish();
            }
        });
        //设置保存问题按钮的作用
        findViewById(R.id.save_question).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //把问题中输入的内容取出来，放入title和body
                EditText editText = findViewById(R.id.question_title);
                String title = editText.getText().toString();
                editText = findViewById(R.id.question_body);
                String body = editText.getText().toString();
                //先把该按钮隐藏，防止重复点击
                findViewById(R.id.save_question).setVisibility(View.GONE);
                //新建异步任务，来通过网络进行提问操作
                new AsyncTask(){
                    @Override
                    protected Object doInBackground(Object[] objects) {
                        //进行提问
                        user.ask(title, body);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        //显示“提问成功”
                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                        //跳转到问题列表界面
                        startActivity(new Intent(AskQuestion.this, QuestionTable.class));
                        finish();
                    }
                }.execute();
            }
        });

    }
}
