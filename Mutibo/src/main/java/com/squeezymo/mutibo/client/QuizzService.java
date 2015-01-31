package com.squeezymo.mutibo.client;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.squeezymo.mutibo.client.provider.QuizzProvider;
import com.squeezymo.mutibo.model.QuestionSet;

import java.util.List;

public class QuizzService extends IntentService {
    private static final String LOG_TAG = QuizzService.class.getCanonicalName();

    public final static String K_TASK = "task";
    public final static String K_ID = "id";
    public final static int V_GET_QUESTION_SET_BY_ID = 1;
    public final static int V_SYNC_QUESTION_SETS = 2;

    public QuizzService() {
        super(QuizzService.class.getCanonicalName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if ( !intent.hasExtra(K_TASK) )
            throw new IllegalArgumentException(K_TASK + " not provided");

        QuestionSet questionSet;
        Intent callback;

        switch (intent.getIntExtra(K_TASK, -1)) {
            case V_GET_QUESTION_SET_BY_ID:
                if ( !intent.hasExtra(K_ID) )
                    throw new IllegalArgumentException(K_ID + " not provided");

                long id = intent.getLongExtra(K_ID, -1L);

                questionSet = RestfulClient.getInstance().getQuestionSetById(id);

                callback = new Intent(QuizzManager.QUIZZ_SVC_QSET_RETRIEVED);
                callback.putExtra(QuizzManager.EXTRA_QSET, questionSet);

                LocalBroadcastManager.getInstance(this).sendBroadcast(callback);

                break;
            case V_SYNC_QUESTION_SETS:
                long cnt = 0;

                List<Long> remoteIds = RestfulClient.getInstance().getAllQuestionSetIDs();
                Cursor localSetsCursor = getContentResolver().query(QuizzProvider.URI_QSET_CONTENT, null, null, null, null);

                for (Long remoteId : remoteIds) {
                    boolean found = false;

                    if (localSetsCursor != null) {
                        for (localSetsCursor.moveToFirst(); !localSetsCursor.isAfterLast(); localSetsCursor.moveToNext()) {
                            if (localSetsCursor.getLong(0) == remoteId) {
                                found = true;
                                break;
                            }
                        }
                    }

                    if (!found) {
                        Log.d(LOG_TAG, "Storing " + remoteId);
                        QuizzProvider.storeQuestionSet(this, RestfulClient.getInstance().getQuestionSetById(remoteId));
                        cnt++;
                    }
                    else {
                        Log.d(LOG_TAG, "Already stored " + remoteId);
                    }
                }

                localSetsCursor.close();

                callback = new Intent(QuizzManager.QUIZZ_SVC_QSET_SYNCHRONIZED);
                callback.putExtra(QuizzManager.EXTRA_TOTAL, cnt);
                LocalBroadcastManager.getInstance(this).sendBroadcast(callback);
        }

    }


}
