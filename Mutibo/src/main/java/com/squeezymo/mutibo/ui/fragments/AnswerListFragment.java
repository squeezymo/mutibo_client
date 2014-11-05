package com.squeezymo.mutibo.ui.fragments;

import com.squeezymo.mutibo.model.*;
import com.squeezymo.mutibo.ui.activites.QuizzActivity;
import com.squeezymo.mutibo.ui.adapters.AnswerListAdapter;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import java.util.*;

public class AnswerListFragment extends ListFragment implements AdapterView.OnItemClickListener {
    private static final String LOG_TAG = AnswerListFragment.class.getCanonicalName();

    private QuestionSet questionSet;
    private AnswerListAdapter mAnswerListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAnswerListAdapter = new AnswerListAdapter(getActivity());
        setListAdapter(mAnswerListAdapter);
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if ( getAnswerListAdapter().getState() == QuizzActivity.QuizzState.CONFIRMED )
            return;

        int correctAnswersNum = questionSet.getCorrectAnswers().size();

        if (correctAnswersNum == 1) {
            mAnswerListAdapter.unpickAll();
            mAnswerListAdapter.pickPosition(position);
        }
        else if (mAnswerListAdapter.getPickedCnt() >= correctAnswersNum) {
            Toast.makeText(getActivity(), "Too many answers.\nYou have to unselect your choice first", Toast.LENGTH_LONG).show();
        }

    }

    public void setQuestionSet(final QuestionSet questionSet, boolean shuffle) {
        this.questionSet = questionSet;

        for ( Answer answer : questionSet.getCorrectAnswers() ) { answer.setCorrect(true); }
        for ( Answer answer : questionSet.getIncorrectAnswers() ) { answer.setCorrect(false); }

        List<Answer> answers = new ArrayList<Answer>() {{
            addAll(questionSet.getCorrectAnswers());
            addAll(questionSet.getIncorrectAnswers());
        }};

        if (shuffle) Collections.shuffle(answers);

        for (int i = 0; i < answers.size(); i++) {
            Answer answer = answers.get(i);
            answer.setTextContent("" + ((char) (i + 65)) + ". " + answer.getTextContent());
        }

        mAnswerListAdapter.setItems(answers);
        mAnswerListAdapter.setState(QuizzActivity.QuizzState.UNCONFIRMED);
    }

    public AnswerListAdapter getAnswerListAdapter() { return mAnswerListAdapter; }
}
