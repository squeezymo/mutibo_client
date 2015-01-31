package com.squeezymo.mutibo.ui.fragments.quizz;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.squeezymo.mutibo.R;
import com.squeezymo.mutibo.model.QuestionSet;

public class ExplanationFragment extends Fragment {

    private QuestionSet questionSet;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.explanation_content, null);
        return view;
    }

    public void setQuestionSet(QuestionSet questionSet) {
        this.questionSet = questionSet;

        TextView explanationContent = (TextView) getView().findViewById(R.id.explanation_content);
        explanationContent.setText(questionSet.getExplanation());
    }

    public void setCorrect(boolean correct) {
        TextView explanationHeader = (TextView) getView().findViewById(R.id.explanation_header);
        if (correct) {
            explanationHeader.setText("Correct!");
        }
        else {
            explanationHeader.setText("Incorrect");
        }
    }

}
