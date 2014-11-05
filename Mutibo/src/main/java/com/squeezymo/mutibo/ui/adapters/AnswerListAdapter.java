package com.squeezymo.mutibo.ui.adapters;

import com.squeezymo.mutibo.R;
import com.squeezymo.mutibo.model.Answer;
import com.squeezymo.mutibo.ui.activites.QuizzActivity;

import android.content.Context;
import android.graphics.Color;
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
                    itemLayout.setBackgroundColor(Color.YELLOW);
                }
                else {
                    itemLayout.setBackgroundColor(Color.BLACK);
                }
                break;
            case CONFIRMED:
                if (answer.isCorrect()) {
                    itemLayout.setBackgroundColor(Color.GREEN);
                }
                else if (answer.isPicked()) {
                    itemLayout.setBackgroundColor(Color.RED);
                }
                else {
                    itemLayout.setBackgroundColor(Color.BLACK);
                }
                break;
        }

        return itemLayout;
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
