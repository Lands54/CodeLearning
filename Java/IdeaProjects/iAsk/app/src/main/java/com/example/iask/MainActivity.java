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
    static User user;
    static boolean temp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.login);
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        EditText temp = findViewById(R.id.accountIN);
        temp.setText(preferences.getString("AccountID", ""));
        temp = findViewById(R.id.password);
        temp.setText(preferences.getString("Password", ""));
        temp = findViewById(R.id.ip);
        temp.setText(preferences.getString("ip", ""));
        temp = findViewById(R.id.port);
        temp.setText(preferences.getString("port", ""));
        super.onCreate(savedInstanceState);

        findViewById(R.id.apply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = findViewById(R.id.ip);
                EditText port = findViewById(R.id.port);
                new AsyncTask(){
                    @Override
                    protected Object doInBackground(Object[] objects) {
                        user = Client.open(editText.getText().toString(), Integer.parseInt(port.getText().toString()));
                        return null;
                    }
                }.execute();

            }
        });

        findViewById(R.id.save_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    EditText temp = findViewById(R.id.accountIN);
                    String account = temp.getText().toString();
                    temp = findViewById(R.id.password);
                    String password = temp.getText().toString();


                    new AsyncTask() {
                        @Override
                        protected Object doInBackground(Object[] objects) {
                            EditText editText = findViewById(R.id.ip);
                            EditText port = findViewById(R.id.port);
                            edit.putString("ip", editText.getText().toString());
                            edit.putString("port", port.getText().toString());
                            System.out.println(account);
                            edit.putString("AccountID", account);
                            System.out.println(password);
                            edit.putString("Password", password);
                            boolean temp = user.login(account, password);
                            edit.apply();
                            if (temp) {
                                user.watchSelfInformation();
                            }
                            return temp;
                        }

                        @Override
                        protected void onPostExecute(Object o) {
                            Toast.makeText(getApplicationContext(), (boolean) o? "Success" : "Failed", Toast.LENGTH_LONG).show();
                            if (!(boolean) o)
                                return;
                            PersonInformation.accountID = Integer.parseInt(user.accountID);
                            startActivity(new Intent(MainActivity.this, QuestionTable.class));
                        }
                    }.execute();
            }
        });

        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Register.class);
                startActivity(intent);
                finish();
            }
        });

    }

}