package com.squeezymo.mutibo.ui.fragments.quizz;

import android.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squeezymo.mutibo.R;

public class GameStatLossesFragment extends Fragment {

    private int lossesCnt;
    private ImageView[] imgViews;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout view = new LinearLayout(getActivity());
        view.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams imgLayout = new LinearLayout.LayoutParams(48, 48);
        imgViews = new ImageView[3];

        for (int i = 0; i < imgViews.length; i++) {
            imgViews[i] = new ImageView(getActivity());
            imgViews[i].setImageResource(R.drawable.dot);
            imgViews[i].setLayoutParams(imgLayout);
            view.addView(imgViews[i]);
        }

        view.setGravity( Gravity.CENTER | Gravity.CENTER_HORIZONTAL );
        return view;
    }

    public int getLossesCnt() { return lossesCnt; }

    public void incLossesCnt() {
        imgViews[lossesCnt].setImageResource(R.drawable.cross);
        lossesCnt = lossesCnt + 1;

    }

    public void reset() {
        lossesCnt = 0;

        for (int i = 0; i < imgViews.length; i++) {
            imgViews[i].setImageResource(R.drawable.dot);
        }
    }
}
