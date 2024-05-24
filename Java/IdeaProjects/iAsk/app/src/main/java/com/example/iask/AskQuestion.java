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
        setContentView(R.layout.ask_question);

        findViewById(R.id.back_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AskQuestion.this, QuestionTable.class));
                finish();
            }
        });

        findViewById(R.id.save_question).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = findViewById(R.id.question_title);
                String title = editText.getText().toString();
                editText = findViewById(R.id.question_body);
                String body = editText.getText().toString();
                findViewById(R.id.save_question).setVisibility(View.GONE);
                new AsyncTask(){
                    @Override
                    protected Object doInBackground(Object[] objects) {
                        user.ask(title, body);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AskQuestion.this, QuestionTable.class));
                        finish();
                    }
                }.execute();
            }
        });

    }
}
