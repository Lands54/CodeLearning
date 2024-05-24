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
    static int ID = 1;
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_display);
        findViewById(R.id.back_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(QuestionDetail.this, QuestionTable.class));
                finish();
            }
        });

        findViewById(R.id.comment_question).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Comment.pos = 0;
                startActivity(new Intent(QuestionDetail.this, Comment.class));
                finish();
            }
        });

        new AsyncTask(){
            @Override
            protected Object doInBackground(Object[] objects) {
                Question question = QuestionList.get(ID);
                return question;
            }

            @Override
            protected void onPostExecute(Object o) {
                if (o == null) {
                    return;
                }
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
