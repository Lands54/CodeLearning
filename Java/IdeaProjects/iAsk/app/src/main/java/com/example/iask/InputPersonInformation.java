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
        //显示输入个人信息界面
        setContentView(R.layout.input_person_information);
        //把用户目前的用户名和生日先默认写在文本框里面
        EditText eT = findViewById(R.id.input_username);
        eT.setText(user.userName);
        bir = findViewById(R.id.input_birthday);
        bir.setText(user.birthday);
        //设置点击生日会发生的行为
        bir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出生日选择菜单
                showDatePickerDialog();
            }
        });
        //设置上一页，跳转到个人信息界面
        findViewById(R.id.back_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersonInformation.accountID = Integer.parseInt(user.accountID);
                startActivity(new Intent(InputPersonInformation.this, PersonInformation.class));
                finish();
            }
        });
        //Profession Spinner 职责菜单
        //设置菜单里会出现的选项
        List<String> professions = Arrays.asList("Swimer", "Asker", "Teacher");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, professions);
        //找到下拉菜单对象
        Spinner temp = findViewById(R.id.input_profession);
        //设置下拉菜单的格式
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //把选项加载到界面的下拉菜单中
        temp.setAdapter(arrayAdapter);
        //看看用户的身份是下拉菜单中第几个选项
        int pos = professions.indexOf(user.identity);
        //如果用户身份在下拉菜单中存在，则让默认选项变为用户当前的身份
        if (pos != - 1)
            temp.setSelection(pos);
        //设置这个选中下拉菜单中选项时，发生的事情
        temp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //position是第几个选项，把用户选择了哪个选项保存在pro变量里
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                InputPersonInformation.pro = professions.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //gender Spinner 具体内容和上面极度类似不再赘述
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
        //～～～
        //设置保存个人信息按钮
        findViewById(R.id.save_information).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //把目前用户选择的个人信息读取出来，性别和职责保存在了该界面全局变量pro和gen
                EditText editText = findViewById(R.id.input_username);
                String userName = editText.getText().toString();
                TextView Text = findViewById(R.id.input_birthday);
                String birthday = Text.getText().toString();
                //新建异步任务
                new AsyncTask(){
                    @Override
                    protected Object doInBackground(Object[] objects) {
                        //通过网络 改变用户四项基本信息
                        user.changeUserName(userName);
                        user.changeIdentity(InputPersonInformation.pro);
                        user.changeBirthday(birthday);
                        user.changeGender(gen);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        //执行完之后显示已经保存
                        Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                        //跳转回问题菜单界面
                        startActivity(new Intent(InputPersonInformation.this,QuestionTable.class));
                        finish();
                    }
                }.execute();

            }

        });
    }


    //该函数为修改日期函数，会弹出修改日期界面，并在确定日期后显示
    private void showDatePickerDialog() {
        //存放日历对象
        final Calendar calendar = Calendar.getInstance();
        //存放选择的年月日
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        //新建日期选择窗口
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                InputPersonInformation.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    //如果确定了日期则执行下列代码
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // month从0开始，所以要加1
                        String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                        //把生日文本框设置为 选择的日期
                        bir.setText(selectedDate);
                    }
                },
                year, month, day);
        //显示这个窗口
        datePickerDialog.show();
    }
}
