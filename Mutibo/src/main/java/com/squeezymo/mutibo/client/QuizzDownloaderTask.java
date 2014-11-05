package com.squeezymo.mutibo.client;

import com.squeezymo.mutibo.model.*;
import com.squeezymo.mutibo.ui.activites.QuizzActivity;

import java.util.*;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class QuizzDownloaderTask extends AsyncTask<String, Void, QuestionSet> {
    private static final String LOG_TAG = QuizzDownloaderTask.class.getCanonicalName();
    private static final String LOCAL_HOST = "http://192.168.56.1:8080"; //"http://192.168.1.100:8080";

    private QuizzManager mQuizzManager;
    private RestfulClient mClient;

    public QuizzDownloaderTask(QuizzManager quizzManager) {
        mQuizzManager = quizzManager;
    }

    @Override
    protected QuestionSet doInBackground(String... strings) {
        mClient = new RestfulClient(LOCAL_HOST);


        final Random gen = new Random();
        final QuestionSet qset = mClient.getQuestionSetById(1); //(1+gen.nextInt()%3);
        return qset;
    }

    public void onPostExecute(final QuestionSet qset) {
        mQuizzManager.setNextQuestionSet(qset);
    }

}
