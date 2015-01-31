package com.squeezymo.mutibo.client;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.squeezymo.mutibo.client.provider.QuizzProvider;
import com.squeezymo.mutibo.model.*;

public class QuizzManager {
    private static final String LOG_TAG = QuizzManager.class.getCanonicalName();

    public static final String QUIZZ_SVC_QSET_RETRIEVED = "com.squeezymo.mutibo.client.RETRIEVED";
    public static final String QUIZZ_SVC_QSET_OUT_OF_QSETS = "com.squeezymo.mutibo.client.OUT_OF_QSETS";
    public static final String QUIZZ_SVC_QSET_SYNCHRONIZED = "com.squeezymo.mutibo.client.SYNCHRONIZED";
    public static final String EXTRA_QSET = "qset";
    public static final String EXTRA_TOTAL = "total";

    private Context mContext;
    private Intent mQuizzServiceIntent;
    private Cursor mCursor;
    private boolean mOutOfQuestions;

    public QuizzManager(Context context) {
        mContext = context;
        mQuizzServiceIntent = new Intent(mContext, QuizzService.class);
        mCursor = mContext.getContentResolver().query(
                QuizzProvider.URI_QSET_CONTENT,
                null,
                QuizzProvider.COL_ANSWERED + "=?",
                new String[] {"0"},
                "random()"
        );
        mOutOfQuestions = (mCursor == null || mCursor.getCount() == 0);

        if (!mOutOfQuestions)
            mCursor.moveToFirst();
    }

    public int getCount() {
        if (!mOutOfQuestions)
            return mCursor.getCount();

        return 0;
    }

    public void requestNextQuestionSet() {
        if (!mOutOfQuestions) {
            QuestionSet questionSet = QuizzProvider.retrieveQuestionSet(mContext, mCursor);
            Log.d(LOG_TAG, "QuestionSet retrieved: " + questionSet.getId());

            Intent callback = new Intent(QUIZZ_SVC_QSET_RETRIEVED);
            callback.putExtra(EXTRA_QSET, questionSet);

            mCursor.moveToNext();
            mOutOfQuestions = mCursor.isAfterLast();

            LocalBroadcastManager.getInstance(mContext).sendBroadcast(callback);
        }
        else {
            terminate();
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(QUIZZ_SVC_QSET_OUT_OF_QSETS));
        }
    }

    public void syncWithServer() {
        mQuizzServiceIntent.putExtra(QuizzService.K_TASK, QuizzService.V_SYNC_QUESTION_SETS);
        mContext.startService(mQuizzServiceIntent);
    }

    public void terminate() {
        if (mCursor != null)
            mCursor.close();
    }

    public void markAsAnswered(final QuestionSet questionSet) {
        questionSet.setAnswered(true);
        QuizzProvider.updateQuestionSet(mContext, questionSet);
    }

    public void markAsUnanswered(final QuestionSet questionSet) {
        questionSet.setAnswered(false);
        QuizzProvider.updateQuestionSet(mContext, questionSet);
    }

    public void rate(final QuestionSet questionSet, final int rating) {
        new Thread() {
            @Override
            public void run() {
                RestfulClient.getInstance().rateQuestionSet(questionSet, rating);
            }
        }.start();
    }

}
