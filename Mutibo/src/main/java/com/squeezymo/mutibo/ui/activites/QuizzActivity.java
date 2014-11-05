package com.squeezymo.mutibo.ui.activites;

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.squeezymo.mutibo.R;
import com.squeezymo.mutibo.client.QuizzManager;
import com.squeezymo.mutibo.model.*;
import com.squeezymo.mutibo.ui.fragments.*;


public class QuizzActivity extends Activity {
    private static final String LOG_TAG = QuizzActivity.class.getCanonicalName();
    public static final String IS_ACTIVE = LOG_TAG + ".IS_ACTIVE";
    public enum QuizzState {UNCONFIRMED, CONFIRMED};

    private QuestionSet mQuestionSet;

    private FragmentManager mFragmentManager;
    private QuestionFragment mQuestionFragment;
    private AnswerListFragment mAnswerListFragment;
    private ExplanationFragment mExplanationFragment;

    private QuizzManager mQuizzManager;
    private BroadcastReceiver mIsActiveReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizz);

        mFragmentManager = getFragmentManager();
        mQuestionFragment = (QuestionFragment) mFragmentManager.findFragmentById(R.id.fragment_question);
        mAnswerListFragment = (AnswerListFragment) mFragmentManager.findFragmentById(R.id.fragment_answer_list);
        mExplanationFragment = (ExplanationFragment) mFragmentManager.findFragmentById(R.id.fragment_explanation);
        mQuizzManager = new QuizzManager(this);

        final Button leftBtn = (Button) findViewById(R.id.btn_unpick_all);
        final Button rightBtn = (Button) findViewById(R.id.btn_confirm);

        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAnswerListFragment.getAnswerListAdapter().unpickAll();
            }
        });

        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mAnswerListFragment.getAnswerListAdapter().getState()) {
                    case UNCONFIRMED:
                        // check the given answer and show explanation
                        boolean isCorrect = mAnswerListFragment.getAnswerListAdapter().isCorrect();

                        mExplanationFragment.setCorrect(isCorrect);
                        mAnswerListFragment.getAnswerListAdapter().setState(QuizzState.CONFIRMED);
                        leftBtn.setText("Rate");
                        rightBtn.setText("Next");

                        displayExplanationArea(true);
                        break;
                    case CONFIRMED:
                        // proceed to the next question set if possible
                        mQuizzManager.requestNextQuestionSet();
                        leftBtn.setText("Clear");
                        rightBtn.setText("OK");

                        break;
                    default:

                }
            }
        });

        mIsActiveReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                setResultCode(RESULT_OK);
            }
        };

         mQuizzManager.requestNextQuestionSet();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getApplicationContext().registerReceiver(mIsActiveReceiver, new IntentFilter(IS_ACTIVE));
    }

    @Override
    protected void onPause() {
        if (mIsActiveReceiver != null)
            getApplicationContext().unregisterReceiver(mIsActiveReceiver);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.quizz, menu);
        return true;
    }

    public void setQuestionSet(QuestionSet questionSet) {
        mQuestionSet = questionSet;
        displayExplanationArea(false);
        mQuestionFragment.setQuestionSet(questionSet);
        mAnswerListFragment.setQuestionSet(questionSet, true);
        mExplanationFragment.setQuestionSet(questionSet);
    }

    private void displayExplanationArea(boolean show) {
        LinearLayout.LayoutParams answersAreaParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, show ? 60 : 90);
        LinearLayout.LayoutParams explanationAreaParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, show ? 30 : 0);

        mAnswerListFragment.getView().setLayoutParams(answersAreaParam);
        mExplanationFragment.getView().setLayoutParams(explanationAreaParam);
    }
    
}
