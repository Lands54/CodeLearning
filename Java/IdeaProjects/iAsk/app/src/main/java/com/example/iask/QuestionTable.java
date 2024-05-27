package com.example.iask;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import org.example.ClientBlock.Client;
import org.example.ClientBlock.QuestionList;
import org.example.ClientBlock.Question;

import static com.example.iask.MainActivity.user;

public class QuestionTable extends AppCompatActivity {
    static int pos = 0;
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //显示界面question.xml
        setContentView(R.layout.question);
        //设置Information按钮的作用
        findViewById(R.id.SelfInformation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //把用户的Id写入到PersonInformation界面里
                PersonInformation.accountID = Integer.parseInt(user.accountID);
                //跳转到用户信息界面
                startActivity(new Intent(QuestionTable.this, PersonInformation.class));
                finish();
            }
        });
        //pos表示当前显示界面的 第一个问题ID - 1
        //如果pos为0，则是第一页，不显示上一页
        if(pos == 0)
            findViewById(R.id.previous_page).setVisibility(View.GONE);
        //设置上一页按钮的功能
        findViewById(R.id.previous_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果pos>0，则说明不在第一页，则让第一个问题的问题id减少10，并且重新进入该界面
                if (pos > 0) {
                    pos -= 10;
                    startActivity(new Intent(QuestionTable.this, QuestionTable.class));
                    finish();
                }
            }
        });
        //设置下一页按钮
        findViewById(R.id.next_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //让显示的 第一个 问题的id增加10，并重新进入本页面
                pos += 10;
                startActivity(new Intent(QuestionTable.this, QuestionTable.class));
                finish();
            }
        });
        //设置提问题按钮
        findViewById(R.id.ask_question).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果用户是Asker，则跳转到提问题界面
                if (user.identity.equals("Asker")) {
                    startActivity(new Intent(QuestionTable.this, AskQuestion.class));
                    finish();
                }
                //如果不是则显示“你不是Asker”然后什么都不做，因为只有Asker能提问题
                else {
                    Toast.makeText(getApplicationContext(), "YOU ARE NOT ASKER", Toast.LENGTH_LONG).show();
                }
            }
        });

        //新建异步任务在其中
        //根据当前的pos从服务器获取问题列表
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                //这里return的对象会变成底下的o
                return QuestionList.getList(pos + 1, 10);
            }

            @Override
            protected void onPostExecute(Object o) {
                //此处o为刚才的return结果
                try {
                    //如果o为空，直接结束运行
                    if (o == null) {
                        return;
                    }
                    //把o转化为问题列表
                    Question[] questions = (Question[]) o ;
                    //获取第一个问题的文本框
                    TextView temp = findViewById(R.id.first_question);
                    //显示问题的标题
                    temp.setText("NO." + questions[0].questionID + ":\n  " + questions[0].title);
                    //让这个标题变得可以被点击
                    temp.setClickable(true);
                    //设置这个标题被点击的效果
                    temp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //把问题细节界面的Id设置为，被点击问题的ID。然后跳转到问题细节界面
                            QuestionDetail.ID = questions[0].questionID;
                            startActivity(new Intent(QuestionTable.this, QuestionDetail.class));
                            finish();
                        }
                    });

                    //后续的操作与第一个问题完全一致！！！！
                    //只不过是更改的文本框变了而已！！！
                    //！！！下一行很重要
                    //当服务器发来的问题小于10个，后续代码中列表会因为没有第10个元素(或更少)而出现问题，此时会去执行第223行！！！
                    temp = findViewById(R.id.second_question);
                    temp.setText("NO." + questions[1].questionID + ":\n  " + questions[1].title);
                    temp.setClickable(true);
                    temp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            QuestionDetail.ID = questions[1].questionID;
                            startActivity(new Intent(QuestionTable.this, QuestionDetail.class));
                            finish();
                        }
                    });

                    temp = findViewById(R.id.third_question);
                    temp.setText("NO." + questions[2].questionID + ":\n  " + questions[2].title);
                    temp.setClickable(true);
                    temp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            QuestionDetail.ID = questions[2].questionID;
                            startActivity(new Intent(QuestionTable.this, QuestionDetail.class));
                            finish();
                        }
                    });

                    temp = findViewById(R.id.forth_question);
                    temp.setText("NO." + questions[3].questionID + ":\n  " + questions[3].title);
                    temp.setClickable(true);
                    temp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            QuestionDetail.ID = questions[3].questionID;
                            startActivity(new Intent(QuestionTable.this, QuestionDetail.class));
                            finish();
                        }
                    });

                    temp = findViewById(R.id.fifth_question);
                    temp.setText("NO." + questions[4].questionID + ":\n  " + questions[4].title);
                    temp.setClickable(true);
                    temp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            QuestionDetail.ID = questions[4].questionID;
                            startActivity(new Intent(QuestionTable.this, QuestionDetail.class));
                            finish();
                        }
                    });

                    temp = findViewById(R.id.sixth_question);
                    temp.setText("NO." + questions[5].questionID + ":\n  " + questions[5].title);
                    temp.setClickable(true);
                    temp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            QuestionDetail.ID = questions[5].questionID;
                            startActivity(new Intent(QuestionTable.this, QuestionDetail.class));
                            finish();
                        }
                    });

                    temp = findViewById(R.id.seventh_question);
                    temp.setText("NO." + questions[6].questionID + ":\n  " + questions[6].title);
                    temp.setClickable(true);
                    temp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            QuestionDetail.ID = questions[6].questionID;
                            startActivity(new Intent(QuestionTable.this, QuestionDetail.class));
                            finish();
                        }
                    });

                    temp = findViewById(R.id.eighth_question);
                    temp.setText("NO." + questions[7].questionID + ":\n  " + questions[7].title);
                    temp.setClickable(true);
                    temp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            QuestionDetail.ID = questions[7].questionID;
                            startActivity(new Intent(QuestionTable.this, QuestionDetail.class));
                            finish();
                        }
                    });

                    temp = findViewById(R.id.ninth_question);
                    temp.setText("NO." + questions[8].questionID + ":\n  " + questions[8].title);
                    temp.setClickable(true);
                    temp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            QuestionDetail.ID = questions[8].questionID;
                            startActivity(new Intent(QuestionTable.this, QuestionDetail.class));
                            finish();
                        }
                    });

                    temp = findViewById(R.id.tenth_question);
                    temp.setText("NO." + questions[9].questionID + ":\n  " + questions[9].title);
                    temp.setClickable(true);
                    temp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            QuestionDetail.ID = questions[9].questionID;
                            startActivity(new Intent(QuestionTable.this, QuestionDetail.class));
                            finish();
                        }
                    });
                } catch (Exception e) {
                    //因为问题不足10个，说明数据库问题已经没有后续了
                    //所以隐藏下一页按钮
                    findViewById(R.id.next_page).setVisibility(View.GONE);
                    return;
                }
            }
        }.execute();
    }

}
