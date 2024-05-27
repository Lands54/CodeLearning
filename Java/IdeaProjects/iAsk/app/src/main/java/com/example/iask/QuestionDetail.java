package com.example.iask;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import org.example.ClientBlock.Question;
import org.example.ClientBlock.QuestionList;

public class QuestionDetail extends AppCompatActivity {
    //这里保存了当前页面查看的具体问题ID
    static int ID = 1;
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //显示question_display.xml中的界面
        setContentView(R.layout.question_display);
        //设置上一页按钮的作用，跳转回问题列表
        findViewById(R.id.back_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(QuestionDetail.this, QuestionTable.class));
                finish();
            }
        });
        //设置查看评论按钮的作用
        findViewById(R.id.comment_question).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Comment.pos存储了当前查看问题的答案的ID（实际上并不是AnswerID） - 1
                //初始化显示答案的ID并跳转到答案界面
                Comment.pos = 0;
                startActivity(new Intent(QuestionDetail.this, Comment.class));
                finish();
            }
        });

        //新建异步任务
        new AsyncTask(){
            @Override
            protected Object doInBackground(Object[] objects) {
                //在进入该界面之前，问题Id会因为点击问题的不同而被改变
                //根据问题ID获取问题
                Question question = QuestionList.get(ID);
                return question;
            }

            @Override
            protected void onPostExecute(Object o) {
                //如果 没拿到问题就什么也不干了
                if (o == null) {
                    return;
                }
                //把问题的基本信息填写到文本框里面
                Question question = (Question) o;
                TextView textView = findViewById(R.id.display_body);
                textView.setText(question.body);
                textView = findViewById(R.id.display_title);
                textView.setText(question.title);
                textView = findViewById(R.id.display_username);
                textView.setText(question.userName);
            }
        }.execute();

    }
}
