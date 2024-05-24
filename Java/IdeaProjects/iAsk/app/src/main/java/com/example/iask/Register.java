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
        setContentView(R.layout.register);
        findViewById(R.id.save_information).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText temp = findViewById(R.id.accountIN);
                String account = temp.getText().toString();
                temp = findViewById(R.id.password);
                String password = temp.getText().toString();
                new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] objects) {
                        boolean temp = user.register(account, password);
                        return temp;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        Toast.makeText(getApplicationContext(), (boolean) o? "Success" : "Failed", Toast.LENGTH_LONG).show();
                        if (!(boolean)o)
                            return;

                        Intent intent = new Intent(Register.this, MainActivity.class);
                        startActivity(intent);
                    }
                }.execute();

            }
        });

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
