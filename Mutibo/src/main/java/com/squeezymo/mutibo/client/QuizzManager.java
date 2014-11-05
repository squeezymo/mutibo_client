package com.squeezymo.mutibo.client;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.squeezymo.mutibo.model.*;
import com.squeezymo.mutibo.ui.activites.QuizzActivity;

public class QuizzManager {

    private QuizzActivity mActivityContext;

    public QuizzManager(QuizzActivity activityContext) {
        mActivityContext = activityContext;
    }

    public void requestNextQuestionSet() {

        QuizzDownloaderTask quizzDownloaderTask = new QuizzDownloaderTask(this);
        quizzDownloaderTask.execute("");

    }

    public void setNextQuestionSet(final QuestionSet questionSet) {
        // check if a context is still there before referring to it
        mActivityContext.sendOrderedBroadcast(
                new Intent(QuizzActivity.IS_ACTIVE),
                null,
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        if ( getResultCode() == Activity.RESULT_OK ) {
                            mActivityContext.setQuestionSet(questionSet);
                        }
                    }
                },
                null,
                0,
                null,
                null);
    }

}
