package com.squeezymo.mutibo.ui.activites;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.squeezymo.mutibo.R;
import com.squeezymo.mutibo.client.RestfulClient;
import com.squeezymo.mutibo.model.Answer;
import com.squeezymo.mutibo.model.QuestionSet;

import java.util.HashSet;

public class AdderActivity extends Activity {
    private static final String LOG_TAG = AdderActivity.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_adder);

        Button submitBtn = (Button) findViewById(R.id.btn_submit);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText questionFld = (EditText) findViewById(R.id.field_question);
                final EditText movieFld1 = (EditText) findViewById(R.id.field_movie1);
                final EditText movieFld2 = (EditText) findViewById(R.id.field_movie2);
                final EditText movieFld3 = (EditText) findViewById(R.id.field_movie3);
                final EditText movieOddFld = (EditText) findViewById(R.id.field_odd_movie);
                final EditText explanationFld = (EditText) findViewById(R.id.field_explanation);

                new Thread( new Runnable() {
                    @Override
                    public void run() {
                        QuestionSet questionSet = new QuestionSet(
                                questionFld.getText().toString(),
                                explanationFld.getText().toString(),
                                new HashSet<Answer>() {{
                                    add(new Answer(movieOddFld.getText().toString()));
                                }},
                                new HashSet<Answer>() {{
                                    add(new Answer(movieFld1.getText().toString()));
                                    add(new Answer(movieFld2.getText().toString()));
                                    add(new Answer(movieFld3.getText().toString()));
                                }}
                        );

                        RestfulClient.getInstance().addQuestionSet(questionSet);

                        AdderActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(AdderActivity.this, AdderActivity.this.getString(R.string.questionAdded), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }).start();

                AdderActivity.this.finish();
            }
        });
    }
}
