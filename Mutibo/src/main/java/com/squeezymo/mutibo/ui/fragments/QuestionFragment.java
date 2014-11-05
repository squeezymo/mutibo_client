package com.squeezymo.mutibo.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squeezymo.mutibo.R;
import com.squeezymo.mutibo.model.Question;
import com.squeezymo.mutibo.model.QuestionSet;

public class QuestionFragment extends Fragment {

    private QuestionSet questionSet;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.question_content, null);
        return view;
    }

    public void setQuestionSet(QuestionSet questionSet) {
        this.questionSet = questionSet;

        TextView questionContent = (TextView) getView().findViewById(R.id.question_content);
//        questionContent.setText(questionSet.getQuestion().getTextContent());
    }

}
