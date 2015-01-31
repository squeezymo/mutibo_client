package com.squeezymo.mutibo.ui.activites;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Toast;

import com.squeezymo.mutibo.R;
import com.squeezymo.mutibo.client.QuizzManager;
import com.squeezymo.mutibo.client.RestfulClient;
import com.squeezymo.mutibo.helpers.YesNoDialog;
import com.squeezymo.mutibo.model.*;
import com.squeezymo.mutibo.ui.fragments.quizz.AnswerListFragment;
import com.squeezymo.mutibo.ui.fragments.quizz.ExplanationFragment;
import com.squeezymo.mutibo.ui.fragments.quizz.GameStatLossesFragment;
import com.squeezymo.mutibo.ui.fragments.quizz.GameStatScoreFragment;
import com.squeezymo.mutibo.ui.fragments.quizz.QuestionFragment;

public class QuizzActivity extends Activity {
    private static final String LOG_TAG = QuizzActivity.class.getCanonicalName();
    public static final String RATING_EXTRA = "rating";
    public static final String START_RATING_ACTIVITY_EXTRA = "start_rating";
    public static final int RATE_QUESTION_SET_REQUEST = 1;
    public static final int SIGN_IN_REQUEST = 2;

    public static class RatingActivity extends Activity {
        RatingBar ratingBar;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.rating_bar);

            final Button cancelBtn = (Button) findViewById(R.id.rating_cancel_btn);
            final Button okBtn = (Button) findViewById(R.id.rating_confirm_btn);

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setResult(RESULT_CANCELED);
                    finish();
                }
            });

            okBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setResult(RESULT_OK, new Intent() {{ putExtra(RATING_EXTRA, (int) ratingBar.getRating()); }});
                    finish();
                }
            });

            ratingBar = (RatingBar) findViewById(R.id.ratingbar);
        }
    }


    public enum QuizzState {UNCONFIRMED, CONFIRMED, GAME_OVER};

    private QuestionSet mQuestionSet;

    private FragmentManager mFragmentManager;
    private QuestionFragment mQuestionFragment;
    private AnswerListFragment mAnswerListFragment;
    private ExplanationFragment mExplanationFragment;
    private GameStatLossesFragment mGameStatLossesFragment;
    private GameStatScoreFragment mGameStatScoreFragment;

    private Button leftBtn;
    private Button rightBtn;

    private QuizzManager mQuizzManager;
    private BroadcastReceiver mSvcReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_quizz);

        mFragmentManager = getFragmentManager();
        mQuestionFragment = (QuestionFragment) mFragmentManager.findFragmentById(R.id.fragment_question);
        mAnswerListFragment = (AnswerListFragment) mFragmentManager.findFragmentById(R.id.fragment_answer_list);
        mExplanationFragment = (ExplanationFragment) mFragmentManager.findFragmentById(R.id.fragment_explanation);
        mGameStatLossesFragment = (GameStatLossesFragment) mFragmentManager.findFragmentById(R.id.fragment_losses);
        mGameStatScoreFragment = (GameStatScoreFragment) mFragmentManager.findFragmentById(R.id.fragment_score);

        mQuizzManager = new QuizzManager(this);

        leftBtn = (Button) findViewById(R.id.btn_unpick_all);
        rightBtn = (Button) findViewById(R.id.btn_confirm);

        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mAnswerListFragment.getAnswerListAdapter().getState()) {
                    case UNCONFIRMED:
                        mAnswerListFragment.getAnswerListAdapter().unpickAll();
                        break;
                    case CONFIRMED:
                    case GAME_OVER:
                        // rate
                        User user = RestfulClient.getInstance().getUser();
                        if (!RestfulClient.getInstance().isAuthorized() || user == null || !user.getAuthorityNames().contains(User.ROLE_USER)) {
                            new YesNoDialog(QuizzActivity.this, QuizzActivity.this.getString(R.string.singInRatingWarning)) {
                                @Override
                                public void yesClicked() {
                                    Intent intent = new Intent(QuizzActivity.this, LogInActivity.class);
                                    intent.putExtra(START_RATING_ACTIVITY_EXTRA, 1);
                                    startActivityForResult(intent, SIGN_IN_REQUEST);
                                }

                                @Override
                                public void noClicked() {}
                            }.issue();
                        }
                        else {
                            startActivityForResult(new Intent(QuizzActivity.this, RatingActivity.class), RATE_QUESTION_SET_REQUEST);
                        }

                        break;
                }

            }
        });

        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mAnswerListFragment.getAnswerListAdapter().getState()) {
                    case UNCONFIRMED:
                        boolean isCorrect = mAnswerListFragment.getAnswerListAdapter().isCorrect();
                        mExplanationFragment.setCorrect(isCorrect);

                        if ( !isCorrect ) {
                            mGameStatLossesFragment.incLossesCnt();
                        }
                        else {
                            mGameStatScoreFragment.incScore();
                        }

                        leftBtn.setText(getString(R.string.rate));
                        displayExplanationArea(true);

                        if ( isGameOver() ) {
                            mAnswerListFragment.getAnswerListAdapter().setState(QuizzState.GAME_OVER);
                            rightBtn.setText(getString(R.string.retry));

                            final SharedPreferences prefs = QuizzActivity.this.getSharedPreferences(RestfulClient.USER_PREFS, Context.MODE_PRIVATE);
                            int prevHighscore = prefs.getInt(RestfulClient.HIGHSCORE_PREF, 0);
                            int currScore = mGameStatScoreFragment.getScore();
                            String msg = getString(R.string.youScored) + ": " + currScore;

                            if (currScore > prevHighscore) {
                                SharedPreferences.Editor prefEditor = prefs.edit();
                                prefEditor.putInt(RestfulClient.HIGHSCORE_PREF, currScore);
                                prefEditor.commit();

                                msg += "\n" + getString(R.string.newHighscore);
                            }

                            new AlertDialog.Builder(QuizzActivity.this)
                                    .setTitle(getString(R.string.gameOver))
                                    .setMessage(msg)
                                    .setIcon(R.drawable.cup)
                                    .setNegativeButton(getString(R.string.ok),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            })
                                    .create()
                                    .show();
                        }
                        else {
                            mAnswerListFragment.getAnswerListAdapter().setState(QuizzState.CONFIRMED);
                            rightBtn.setText(getString(R.string.next));
                        }

                        mQuizzManager.markAsAnswered(mQuestionSet);
                        break;
                    case CONFIRMED:
                        mQuizzManager.requestNextQuestionSet();
                        leftBtn.setClickable(true);
                        leftBtn.setText(getString(R.string.clear));
                        rightBtn.setText(getString(R.string.submit));

                        break;
                    case GAME_OVER:
                        mGameStatLossesFragment.reset();
                        mGameStatScoreFragment.reset();

                        mQuizzManager.requestNextQuestionSet();
                        leftBtn.setText(getString(R.string.clear));
                        rightBtn.setText(getString(R.string.submit));

                        break;
                    default:
                        throw new IllegalStateException("Illegal quizz state: " + mAnswerListFragment.getAnswerListAdapter().getState());
                }
            }
        });

        mSvcReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if ( action.equals(QuizzManager.QUIZZ_SVC_QSET_RETRIEVED) ) {
                    QuestionSet questionSet = intent.getParcelableExtra(QuizzManager.EXTRA_QSET);
                    setQuestionSet(questionSet);
                }
                else if ( action.equals(QuizzManager.QUIZZ_SVC_QSET_OUT_OF_QSETS)) {
                    new YesNoDialog(QuizzActivity.this, QuizzActivity.this.getString(R.string.outOfQSets)) {
                        @Override
                        public void yesClicked() {
                            Toast.makeText(QuizzActivity.this, QuizzActivity.this.getString(R.string.download), Toast.LENGTH_SHORT).show();
                            mQuizzManager.syncWithServer();
                        }

                        @Override
                        public void noClicked() {
                            QuizzActivity.this.finish();
                        }
                    }.issue();
                }
                else if ( action.equals(QuizzManager.QUIZZ_SVC_QSET_SYNCHRONIZED) ) {
                    long total = intent.getLongExtra(QuizzManager.EXTRA_TOTAL, 0);
                    final String toastMsg = (total == 0 ?
                                    QuizzActivity.this.getString(R.string.nothingToDownload) :
                                    "" + total + " " + QuizzActivity.this.getString(R.string.downloaded) );

                    QuizzActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(QuizzActivity.this, toastMsg, Toast.LENGTH_SHORT).show();
                        }
                    });

                    if (total == 0) {
                        QuizzActivity.this.finish();
                    }
                    else {
                        mQuizzManager = new QuizzManager(QuizzActivity.this);
                        mQuizzManager.requestNextQuestionSet();
                    }
                }

            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RATE_QUESTION_SET_REQUEST:
                if ( resultCode != Activity.RESULT_OK ) return;
                if ( mQuestionSet == null ) return;

                int rating = data.getIntExtra(RATING_EXTRA, 2);

                mGameStatScoreFragment.setRating(rating);
                mQuizzManager.rate(mQuestionSet, rating);
                leftBtn.setVisibility(View.INVISIBLE);
                break;
            case SIGN_IN_REQUEST:
                if ( resultCode != Activity.RESULT_OK ) return;
                if ( data != null && data.hasExtra(START_RATING_ACTIVITY_EXTRA) ) {
                    User user = RestfulClient.getInstance().getUser();
                    if (RestfulClient.getInstance().isAuthorized() && user != null && user.getAuthorityNames().contains(User.ROLE_USER)) {
                        startActivityForResult(new Intent(QuizzActivity.this, RatingActivity.class), RATE_QUESTION_SET_REQUEST);
                    }
                }
                break;
        }
    }

    private boolean isGameOver() {
        return mGameStatLossesFragment.getLossesCnt() >= 3;
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(QuizzManager.QUIZZ_SVC_QSET_RETRIEVED);
        intentFilter.addAction(QuizzManager.QUIZZ_SVC_QSET_OUT_OF_QSETS);
        intentFilter.addAction(QuizzManager.QUIZZ_SVC_QSET_SYNCHRONIZED);

        LocalBroadcastManager.getInstance(this).registerReceiver(mSvcReceiver, intentFilter);

        if (mQuestionSet == null)
            mQuizzManager.requestNextQuestionSet();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mSvcReceiver);
        //mQuizzManager.terminate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.quizz, menu);
        return true;
    }

    public void setQuestionSet(QuestionSet questionSet) {
        mQuestionSet = questionSet;
        displayExplanationArea(false);

        mQuestionFragment.setQuestionSet(mQuestionSet);
        mAnswerListFragment.setQuestionSet(mQuestionSet);
        mExplanationFragment.setQuestionSet(mQuestionSet);
        mGameStatScoreFragment.setQuestionSet(mQuestionSet);

        leftBtn.setVisibility(View.VISIBLE);
    }

    private void displayExplanationArea(final boolean show) {
        Animation anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                LinearLayout.LayoutParams answersAreaParam, explanationAreaParam;

                if ( show ) {
                   answersAreaParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 60);
                   explanationAreaParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, interpolatedTime * 30);
                }
                else {
                    answersAreaParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 90);
                    explanationAreaParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0);
                }

                mAnswerListFragment.getView().setLayoutParams(answersAreaParam);
                mExplanationFragment.getView().setLayoutParams(explanationAreaParam);
            }
        };

        anim.setDuration(1000);
        mExplanationFragment.getView().startAnimation(anim);
    }
    
}
