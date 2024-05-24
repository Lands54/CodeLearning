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
        setContentView(R.layout.question);
        findViewById(R.id.SelfInformation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersonInformation.accountID = Integer.parseInt(user.accountID);
                startActivity(new Intent(QuestionTable.this, PersonInformation.class));
                finish();
            }
        });
        if(pos == 0)
            findViewById(R.id.previous_page).setVisibility(View.GONE);
        findViewById(R.id.previous_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pos > 0) {
                    pos -= 10;
                    startActivity(new Intent(QuestionTable.this, QuestionTable.class));
                    finish();
                }
            }
        });

        findViewById(R.id.next_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pos += 10;
                startActivity(new Intent(QuestionTable.this, QuestionTable.class));
                finish();
            }
        });

        findViewById(R.id.ask_question).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.identity.equals("Asker")) {
                    startActivity(new Intent(QuestionTable.this, AskQuestion.class));
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(), "YOU ARE NOT ASKER", Toast.LENGTH_LONG).show();
                }
            }
        });

        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                return QuestionList.getList(pos + 1, 10);
            }

            @Override
            protected void onPostExecute(Object o) {
                try {
                    if (o == null) {
                        return;
                    }
                    Question[] questions = (Question[]) o ;
                    TextView temp = findViewById(R.id.first_question);
                    temp.setText("NO." + questions[0].questionID + ":\n  " + questions[0].title);
                    temp.setClickable(true);
                    temp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            QuestionDetail.ID = questions[0].questionID;
                            startActivity(new Intent(QuestionTable.this, QuestionDetail.class));
                            finish();
                        }
                    });

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
                    findViewById(R.id.next_page).setVisibility(View.GONE);
                    return;
                }
            }
        }.execute();
    }

}
