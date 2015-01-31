package com.squeezymo.mutibo.ui.fragments.quizz;

import android.app.Fragment;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;

import com.squeezymo.mutibo.R;
import com.squeezymo.mutibo.model.QuestionSet;

import org.w3c.dom.Text;

import java.text.DecimalFormat;

public class GameStatScoreFragment extends Fragment {

    private static final DecimalFormat sDecimalFormat = new DecimalFormat();
    private int mScore;
    private float mRating;
    private QuestionSet mQuestionSet;

    private TextView ratingView;
    private TextView scoreView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sDecimalFormat.setMaximumFractionDigits(1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_stats, null);
        ratingView = (TextView) view.findViewById(R.id.rating_value);
        scoreView = (TextView) view.findViewById(R.id.score_value);

        return view;
    }

    public void setQuestionSet(QuestionSet questionSet) {
        mQuestionSet = questionSet;

        if ( mQuestionSet.getRatingNum() == 0 )
            mRating = 0;
        else
            mRating = mQuestionSet.getRatingSum() / (float) mQuestionSet.getRatingNum();

        ratingView.setText(sDecimalFormat.format(mRating));
    }

    public void setRating(int rating) {
        mRating = (mQuestionSet.getRatingSum()+rating) / (float) (mQuestionSet.getRatingNum() + 1);
        ratingView.setText(sDecimalFormat.format(mRating));
    }

    public void incScore() {
        mScore += 100;
        scoreView.setText("" + mScore);
    }

    public int getScore() {
        return mScore;
    }

    public void reset() {
        mScore = 0;
        scoreView.setText("" + mScore);
    }
}
