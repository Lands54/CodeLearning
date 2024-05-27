package com.example.iask;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import org.example.ClientBlock.Answer;
import org.example.ClientBlock.Question;
import org.example.ClientBlock.QuestionList;
import org.example.ClientBlock.User;

import static com.example.iask.MainActivity.user;

public class Comment extends AppCompatActivity {
    //存放当前显示的答案的ID
    static int pos = 0;
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //显示 问题答案 界面
        setContentView(R.layout.comment_question);
        //如果pos为0就是在第一页，不显示上一页按钮
        if (pos == 0)
            findViewById(R.id.previous_page2).setVisibility(View.GONE);
        //先把下一页隐藏，
        findViewById(R.id.next_page2).setVisibility(View.GONE);
        //设置上一页的功能
        findViewById(R.id.previous_page2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果pos>0，则为不是第一页就让其-4
                if (pos > 0){
                    pos -= 4;
                    //刷新本界面
                    startActivity(new Intent(Comment.this, Comment.class));
                    finish();
                }
            }
        });
        //设置下一页按钮
        findViewById(R.id.next_page2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //让pos+4，也就是显示的第一个回答的id +4
                pos += 4;
                //刷新本页面，本页面后续会从当前pos从网络获得问题
                startActivity(new Intent(Comment.this, Comment.class));
                finish();
            }
        });
        //设置回答按钮
        findViewById(R.id.add_comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置回答按钮不可见，防止被重复点击
                findViewById(R.id.add_comment).setVisibility(View.GONE);
                //新建异步任务
                new AsyncTask(){
                    @Override
                    protected Object doInBackground(Object[] objects) {
                        //把回答保存在变量里
                        EditText textView = findViewById(R.id.input_comment);
                        String comment = textView.getText().toString();
                        //检测回答是否为空
                        if (comment.equals("")) {
                            //空就返回false
                            return false;
                        }
                        //不空就进行回答操作并返回true
                        user.answer(QuestionDetail.ID, comment);
                        return true;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        //如果不为空
                        if((boolean) o) {
                            //如果用户是老师
                            if (user.identity.equals("Teacher")) {
                                //则显示成功，并刷新本界面
                                Toast.makeText(Comment.this, "SUCCEED", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(Comment.this, Comment.class));
                            }
                            else {
                                //如果用户不是老师，则把提问按钮可视，并显示你不是老师
                                findViewById(R.id.add_comment).setVisibility(View.VISIBLE);
                                Toast.makeText(Comment.this, "You are not Teacher", Toast.LENGTH_LONG).show();
                            }
                        }
                        else {
                            //如果问题为空，就显示问题不能为空，并将按钮可视
                            findViewById(R.id.add_comment).setVisibility(View.VISIBLE);
                            Toast.makeText(Comment.this, "COMMENT NOT EMPTY!", Toast.LENGTH_LONG).show();
                        }
                    }
                }.execute();
            }

        });

        //上一页按钮，跳转到问题细节界面
        findViewById(R.id.back_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Comment.this, QuestionDetail.class));
                finish();
            }
        });

        //新建异步任务
        new AsyncTask(){
            //因为回答只包含用户的ID，待会要通过ID从网络获得用户名
            User[] temp = new User[10];
            @Override
            protected Object doInBackground(Object[] objects) {
                //先获取当前问题
                Question question = null;
                try {
                    question = QuestionList.get(QuestionDetail.ID);
                } catch (Exception e) {
                    //获取失败就把问题设置为空
                    question = null;
                }
                if (question == null) {
                    //就啥也不干了，返回空
                    return null;
                }
                //如果不为空就根据问题和pos获取回答,然后把问题列表返回
                return question.getAnswer(pos + 1, 4);
            }

            @Override
            protected void onPostExecute(Object o) {
                //如果刚才返回了空就啥也不干了
                try {
                    if (o == null) {
                        return;
                    }
                    //把问题列表存到answers里
                    Answer[] answers = (Answer[]) o;
                    //测量answers的数量
                    int size = 0;
                    for(Answer temp : answers){
                        size++;
                    }
                    //如果answers里面不包含问题就到此为止了，直接返回啥也不干了
                    if (size <= 0)
                        return;
                    //如果包含了至少一个问题就新建下列任务
                    new AsyncTask(){
                        //这个pos和外面的pos没有关系
                        //他表示了当前获取的4个问题里面的第pos个问题
                        int pos = 0;
                        @Override
                        protected Object doInBackground(Object[] objects) {
                            //通过答案里面的ID获取回答的人的基本信息
                            //存到temp里面
                            temp[pos] = user.watchInformation(Integer.parseInt(answers[pos].accountID));
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Object o) {
                            //上面执行完之后执行这下面
                            //suit函数在下面定义了，其内容为，把字符串显示到文本框，并让被设置的文本框变得可视化，
                            suit(R.id.Comment1, answers[pos].answerBody);
                            suit(R.id.first_comment, temp[pos].userName);
                            suit(R.id.like1, String.valueOf(answers[pos].like));
                            //设置点赞按钮
                            findViewById(R.id.like1).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    new AsyncTask(){
                                        //根据答案进行点赞功能
                                        @Override
                                        protected Object doInBackground(Object[] objects) {
                                            answers[pos].like();
                                            return null;
                                        }

                                        @Override
                                        protected void onPostExecute(Object o) {
                                            //显示当前点赞的数量
                                            suit(R.id.like1, String.valueOf(answers[pos].like));
                                        }
                                    }.execute();

                                }
                            });
                            //设置第一个评论的用户名，被点击的时候发生什么
                            findViewById(R.id.first_comment).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //更改PersonInformation里的AccountID，这个变量用来决定个人信息界面显示谁的信息
                                    PersonInformation.accountID = Integer.parseInt(answers[pos].accountID);
                                    //跳转到个人信息界面
                                    startActivity(new Intent(Comment.this, PersonInformation.class));
                                }
                            });
                        }
                    }.execute();
                    //如果包含至少两个回答，则后面的内容和第一个回答完全一样！！！
                    //！！！！
                    //！！！！
                    //只有第314行对第四个问题进行填写的时候有区别
                    if (size <= 1)
                        return;

                    new AsyncTask(){
                        int pos = 1;
                        @Override
                        protected Object doInBackground(Object[] objects) {
                            temp[pos] = user.watchInformation(Integer.parseInt(answers[pos].accountID));
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Object o) {
                            suit(R.id.Comment2, answers[pos].answerBody);
                            suit(R.id.second_comment, temp[pos].userName);
                            suit(R.id.like2, String.valueOf(answers[pos].like));
                            findViewById(R.id.like2).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    new AsyncTask(){
                                        @Override
                                        protected Object doInBackground(Object[] objects) {
                                            answers[pos].like();
                                            return null;
                                        }

                                        @Override
                                        protected void onPostExecute(Object o) {
                                            suit(R.id.like2, String.valueOf(answers[pos].like));
                                        }
                                    }.execute();
                                }
                            });
                            findViewById(R.id.second_comment).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    PersonInformation.accountID = Integer.parseInt(answers[pos].accountID);
                                    startActivity(new Intent(Comment.this, PersonInformation.class));
                                }
                            });
                        }
                    }.execute();

                    if (size <= 2)
                        return;

                    new AsyncTask(){
                        int pos = 2;
                        @Override
                        protected Object doInBackground(Object[] objects) {
                            temp[pos] = user.watchInformation(Integer.parseInt(answers[pos].accountID));
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Object o) {
                            suit(R.id.Comment3, answers[pos].answerBody);
                            suit(R.id.third_comment, temp[pos].userName);
                            suit(R.id.like3, String.valueOf(answers[pos].like));
                            findViewById(R.id.like3).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    new AsyncTask(){
                                        @Override
                                        protected Object doInBackground(Object[] objects) {
                                            answers[pos].like();
                                            return null;
                                        }

                                        @Override
                                        protected void onPostExecute(Object o) {
                                            suit(R.id.like3, String.valueOf(answers[pos].like));
                                        }
                                    }.execute();
                                }
                            });

                            findViewById(R.id.third_comment).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    PersonInformation.accountID = Integer.parseInt(answers[pos].accountID);
                                    startActivity(new Intent(Comment.this, PersonInformation.class));
                                }
                            });
                        }
                    }.execute();

                    if (size <= 3)
                        return;

                    new AsyncTask(){
                        int pos = 3;
                        @Override
                        protected Object doInBackground(Object[] objects) {
                            temp[pos] = user.watchInformation(Integer.parseInt(answers[pos].accountID));
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Object o) {
                            suit(R.id.Comment4, answers[pos].answerBody);
                            suit(R.id.forth_comment, temp[pos].userName);
                            suit(R.id.like4, String.valueOf(answers[pos].like));
                            //这个界面一开始的时候把下一页按钮进行了隐藏，这里因为4个问题都已经成功显示了，所以不能确定数据库里的答案已经显示完了
                            //所以就可以把下一页按钮可视化
                            findViewById(R.id.next_page2).setVisibility(View.VISIBLE);
                            findViewById(R.id.like4).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    new AsyncTask(){
                                        @Override
                                        protected Object doInBackground(Object[] objects) {
                                            answers[pos].like();
                                            return null;
                                        }

                                        @Override
                                        protected void onPostExecute(Object o) {
                                            suit(R.id.like4, String.valueOf(answers[pos].like));
                                        }
                                    }.execute();
                                }
                            });
                            findViewById(R.id.forth_comment).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    PersonInformation.accountID = Integer.parseInt(answers[pos].accountID);
                                    startActivity(new Intent(Comment.this, PersonInformation.class));
                                }
                            });
                        }
                    }.execute();
                } catch (Exception e) {
                    System.out.println("____");
                }
            }

            //第一个参数是R.id.textview。是每个页面中元素的唯一id，第二个参数是想要这个元素显示的内容
            private void suit(int id, String s){
                //设置这个元素是可视的
                findViewById(id).setVisibility(View.VISIBLE);
                //显示传入的字符串内容
                TextView textView = findViewById(id);
                textView.setText(s);
            }
        }.execute();
    }
}
