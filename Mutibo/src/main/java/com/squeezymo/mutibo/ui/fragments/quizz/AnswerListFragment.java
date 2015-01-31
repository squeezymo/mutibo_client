package com.squeezymo.mutibo.ui.fragments.quizz;

import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingLeftInAnimationAdapter;
import com.squeezymo.mutibo.model.*;
import com.squeezymo.mutibo.ui.activites.QuizzActivity;
import com.squeezymo.mutibo.ui.adapters.AnswerListAdapter;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import java.util.*;

public class AnswerListFragment extends ListFragment implements AdapterView.OnItemClickListener {
    private static final String LOG_TAG = AnswerListFragment.class.getCanonicalName();

    private QuestionSet questionSet;
    private AnimationAdapter mDecoratingAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mDecoratingAdapter = new SwingBottomInAnimationAdapter(new AnswerListAdapter(getActivity()));
        mDecoratingAdapter.setAbsListView(getListView());

        setListAdapter(mDecoratingAdapter);

        getListView().setOnItemClickListener(this);
        getListView().setDivider(null);
        getListView().setDividerHeight(10);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if ( getAnswerListAdapter().getState() != QuizzActivity.QuizzState.UNCONFIRMED )
            return;

        int correctAnswersNum = questionSet.getCorrectAnswers().size();

        if (correctAnswersNum == 1) {
            getAnswerListAdapter().unpickAll();
            getAnswerListAdapter().pickPosition(position);
        }
        else if (getAnswerListAdapter().getPickedCnt() >= correctAnswersNum) {
            Toast.makeText(getActivity(), "Too many answers.\nYou have to unselect your choice first", Toast.LENGTH_LONG).show();
        }

    }

    public void setQuestionSet(final QuestionSet questionSet) {
        this.questionSet = questionSet;

        for ( Answer answer : questionSet.getCorrectAnswers() ) { answer.setCorrect(true); }
        for ( Answer answer : questionSet.getIncorrectAnswers() ) { answer.setCorrect(false); }

        List<Answer> answers = new ArrayList<Answer>() {{
            addAll(questionSet.getCorrectAnswers());
            addAll(questionSet.getIncorrectAnswers());
        }};

        Collections.shuffle(answers);

        for (int i = 0; i < answers.size(); i++) {
            Answer answer = answers.get(i);
            answer.setTextContent("" + ((char) (i + 65)) + ". " + answer.getTextContent());
        }

        mDecoratingAdapter.reset();
        getAnswerListAdapter().setItems(answers);
        getAnswerListAdapter().setState(QuizzActivity.QuizzState.UNCONFIRMED);
    }

    public AnswerListAdapter getAnswerListAdapter() {
        if ( mDecoratingAdapter == null ) {
            throw new IllegalStateException("AnimationAdapter not created");
        }

        if ( !(mDecoratingAdapter.getDecoratedBaseAdapter() instanceof AnswerListAdapter) )
            throw new IllegalStateException("Must decorate " + AnswerListAdapter.class.getCanonicalName());

        return (AnswerListAdapter) mDecoratingAdapter.getDecoratedBaseAdapter();
    }
}
