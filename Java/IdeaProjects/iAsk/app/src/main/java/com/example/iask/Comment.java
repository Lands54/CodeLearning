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
    static int pos = 0;
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_question);
        if (pos == 0)
            findViewById(R.id.previous_page2).setVisibility(View.GONE);
        findViewById(R.id.next_page2).setVisibility(View.GONE);
        findViewById(R.id.previous_page2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pos > 0){
                    pos -= 4;
                    startActivity(new Intent(Comment.this, Comment.class));
                    finish();
                }
            }
        });

        findViewById(R.id.next_page2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pos += 4;
                startActivity(new Intent(Comment.this, Comment.class));
                finish();
            }
        });

        findViewById(R.id.add_comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.add_comment).setVisibility(View.GONE);
                new AsyncTask(){
                    @Override
                    protected Object doInBackground(Object[] objects) {
                        EditText textView = findViewById(R.id.input_comment);
                        String comment = textView.getText().toString();
                        if (comment.equals("")) {
                            return false;
                        }
                        user.answer(QuestionDetail.ID, comment);
                        return true;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        if((boolean) o) {
                            if (user.identity.equals("Teacher")) {
                                Toast.makeText(Comment.this, "SUCCEED", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(Comment.this, Comment.class));
                            }
                            else {
                                findViewById(R.id.add_comment).setVisibility(View.VISIBLE);
                                Toast.makeText(Comment.this, "You are not Teacher", Toast.LENGTH_LONG).show();
                            }
                        }
                        else {
                            findViewById(R.id.add_comment).setVisibility(View.VISIBLE);
                            Toast.makeText(Comment.this, "COMMENT NOT EMPTY!", Toast.LENGTH_LONG).show();
                        }
                    }
                }.execute();
            }

        });

        findViewById(R.id.back_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Comment.this, QuestionDetail.class));
                finish();
            }
        });

        new AsyncTask(){
            User[] temp = new User[10];
            @Override
            protected Object doInBackground(Object[] objects) {
                Question question = null;
                try {
                    question = QuestionList.get(QuestionDetail.ID);
                } catch (Exception e) {
                    question = null;
                }
                if (question == null) {
                    return null;
                }
                return question.getAnswer(pos + 1, 4);
            }

            @Override
            protected void onPostExecute(Object o) {
                try {
                    if (o == null) {
                        return;
                    }
                    Answer[] answers = (Answer[]) o;
                    int size = 0;
                    for(Answer temp : answers){
                        size++;
                    }
                    if (size <= 0)
                        return;
                    new AsyncTask(){
                        int pos = 0;
                        @Override
                        protected Object doInBackground(Object[] objects) {
                            temp[pos] = user.watchInformation(Integer.parseInt(answers[pos].accountID));
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Object o) {
                            suit(R.id.Comment1, answers[pos].answerBody);
                            suit(R.id.first_comment, temp[pos].userName);
                            suit(R.id.like1, String.valueOf(answers[pos].like));
                            findViewById(R.id.like1).setOnClickListener(new View.OnClickListener() {
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
                                            suit(R.id.like1, String.valueOf(answers[pos].like));
                                        }
                                    }.execute();

                                }
                            });
                            findViewById(R.id.first_comment).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    PersonInformation.accountID = Integer.parseInt(answers[pos].accountID);
                                    startActivity(new Intent(Comment.this, PersonInformation.class));
                                }
                            });
                        }
                    }.execute();

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

            private void suit(int id, String s){
                findViewById(id).setVisibility(View.VISIBLE);
                TextView textView = findViewById(id);
                textView.setText(s);
            }
        }.execute();
    }
}
