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
    static int accountID;
    static User tempUser;
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_information);
        new AsyncTask(){
            @Override
            protected Object doInBackground(Object[] objects) {
                tempUser = user.watchInformation(accountID);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                TextView temp = findViewById(R.id.input_username);
                temp.setText(tempUser.userName);
                temp = findViewById(R.id.input_birthday);
                temp.setText(tempUser.birthday);
                temp = findViewById(R.id.input_gender);
                temp.setText(tempUser.gender);
                temp = findViewById(R.id.input_profession);
                temp.setText(tempUser.identity);
                if (user.accountID.equals(String.valueOf(PersonInformation.accountID))){
                    findViewById(R.id.change).setVisibility(View.VISIBLE);
                }else {
                    findViewById(R.id.change).setVisibility(View.GONE);
                }
            }
        }.execute();
        findViewById(R.id.change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PersonInformation.this, InputPersonInformation.class));
                finish();
            }
        });
        findViewById(R.id.back_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PersonInformation.this, QuestionTable.class));
                finish();
            }
        });
    }
}
