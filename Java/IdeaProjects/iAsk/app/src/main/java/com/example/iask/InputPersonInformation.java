package com.example.iask;

import android.app.DatePickerDialog;
import android.app.Person;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import org.example.ClientBlock.Question;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.iask.MainActivity.user;

public class InputPersonInformation extends AppCompatActivity {
    static String pro;
    static String gen;
    static TextView bir;
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_person_information);
        EditText eT = findViewById(R.id.input_username);
        eT.setText(user.userName);

        bir = findViewById(R.id.input_birthday);
        bir.setText(user.birthday);
        bir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        findViewById(R.id.back_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersonInformation.accountID = Integer.parseInt(user.accountID);
                startActivity(new Intent(InputPersonInformation.this, PersonInformation.class));
                finish();
            }
        });
        //Profession Spinner
        List<String> professions = Arrays.asList("Swimer", "Asker", "Teacher");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, professions);
        Spinner temp = findViewById(R.id.input_profession);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        temp.setAdapter(arrayAdapter);
        int pos = professions.indexOf(user.identity);
        if (pos != - 1)
            temp.setSelection(pos);
        temp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                InputPersonInformation.pro = professions.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //gender Spinner
        List<String> gend = Arrays.asList("Male", "Female");
        ArrayAdapter<String> an = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, gend);
        temp = findViewById(R.id.input_gender);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        temp.setAdapter(an);
        pos = gend.indexOf(user.gender);
        if(pos != -1)
            temp.setSelection(pos);
        temp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gen = gend.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        findViewById(R.id.save_information).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = findViewById(R.id.input_username);
                String userName = editText.getText().toString();
                TextView Text = findViewById(R.id.input_birthday);
                String birthday = Text.getText().toString();
                new AsyncTask(){
                    @Override
                    protected Object doInBackground(Object[] objects) {
                        user.changeUserName(userName);
                        user.changeIdentity(InputPersonInformation.pro);
                        user.changeBirthday(birthday);
                        user.changeGender(gen);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(InputPersonInformation.this,QuestionTable.class));
                        finish();
                    }
                }.execute();

            }

        });
    }



    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                InputPersonInformation.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // month从0开始，所以要加1
                        String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                        bir.setText(selectedDate);
                    }
                },
                year, month, day);

        datePickerDialog.show();
    }
}
