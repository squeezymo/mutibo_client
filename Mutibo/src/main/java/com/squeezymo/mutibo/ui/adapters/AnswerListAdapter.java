package com.squeezymo.mutibo.ui.adapters;

import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.squeezymo.mutibo.R;
import com.squeezymo.mutibo.model.Answer;
import com.squeezymo.mutibo.ui.activites.QuizzActivity;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.view.*;
import android.widget.*;

import java.util.*;

public class AnswerListAdapter extends BaseAdapter {
    private static final String LOG_TAG = AnswerListAdapter.class.getCanonicalName();

    private final Context mContext;
    private List<Answer> mItems;
    private int pickedCnt;
    private QuizzActivity.QuizzState mState;

    public AnswerListAdapter(Context context) {
        mContext = context;
        mState = QuizzActivity.QuizzState.UNCONFIRMED;
        mItems = new ArrayList<Answer>();
    }

    public void setItems(List<Answer> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        final Answer answer = (Answer) getItem(i);
        final View itemLayout = (convertView == null ? LayoutInflater.from(mContext).inflate(R.layout.answer_item, null) : convertView);

        TextView answerContent = (TextView) itemLayout.findViewById(R.id.answer_content);

        answerContent.setText( answer.getTextContent() );

        switch (mState) {
            case UNCONFIRMED:
                if ( answer.isPicked() ) {
                    itemLayout.setBackgroundResource(R.drawable.answer_bg_picked);
                }
                else {
                    itemLayout.setBackgroundResource(R.drawable.answer_bg_default);
                }
                break;
            case CONFIRMED:
            case GAME_OVER:
                if (answer.isCorrect()) {
                    blink(itemLayout, R.drawable.answer_bg_default, R.drawable.answer_bg_correct);
                }
                else if (answer.isPicked()) {
                    itemLayout.setBackgroundResource(R.drawable.answer_bg_incorrect);
                }
                else {
                    itemLayout.setBackgroundResource(R.drawable.answer_bg_default);
                }
                break;
        }

        return itemLayout;
    }

    private void blink(final View layout, int res1, int res2) {
        final int TIMES_TO_REPEAT = 7;
        final int DURATION = 200;

        final AnimationDrawable a = new AnimationDrawable();

        for (int i = 0; i < TIMES_TO_REPEAT; i++) {
            a.addFrame(mContext.getResources().getDrawable(res1), DURATION);
            a.addFrame(mContext.getResources().getDrawable(res2), DURATION);
        }

        a.setOneShot(true);
        layout.setBackgroundDrawable(a);
        a.start();
    }

    @Override
    public int getCount() { return mItems.size(); }

    @Override
    public Object getItem(int i) { return mItems.get(i); }

    @Override
    public long getItemId(int i) { return i; }

    public int getPickedCnt() { return pickedCnt; }

    public void pickPosition(int i) {
        Answer answer = (Answer) getItem(i);
        answer.setPicked(!answer.isPicked());
        pickedCnt += answer.isPicked() ? 1 : -1;
        notifyDataSetChanged();
    }

    public void unpickAll() {
        for (Answer answer : mItems)
            answer.setPicked(false);

        pickedCnt = 0;
        notifyDataSetChanged();
    }

    public QuizzActivity.QuizzState getState() {
        return mState;
    }

    public void setState(QuizzActivity.QuizzState state) {
        mState = state;
        notifyDataSetChanged();
    }

    public boolean isCorrect() {
        for (Answer answer : mItems) {
            if (answer.isCorrect() && !answer.isPicked())
                return false;
        }

        return true;
    }
}
