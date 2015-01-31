package com.squeezymo.mutibo.ui.activites;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import com.squeezymo.mutibo.R;
import com.squeezymo.mutibo.ui.adapters.LogInPagerAdapter;

public class LogInActivity extends FragmentActivity implements ActionBar.TabListener, CallbackHandler {
    private static final String LOG_TAG = LogInActivity.class.getCanonicalName();

    private ViewPager mViewPager;
    private LogInPagerAdapter mAdapter;
    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.log_in_pager);
        //getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mActionBar = getActionBar();
        mAdapter = new LogInPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mAdapter);
        mActionBar.setHomeButtonEnabled(false);
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mActionBar.addTab( mActionBar.newTab().setText(getString(R.string.signIn)).setTabListener(this) );
        mActionBar.addTab( mActionBar.newTab().setText(getString(R.string.singUp)).setTabListener(this) );

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mActionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.starter, menu);
        return true;
    }

    private Intent constructExtras() {
        Intent intent = null;

        if (getIntent().hasExtra(QuizzActivity.START_RATING_ACTIVITY_EXTRA)) {
            if (intent == null)
                intent = new Intent();

            intent.putExtra(QuizzActivity.START_RATING_ACTIVITY_EXTRA, 1);
        }

        if (getIntent().hasExtra(MainMenuActivity.START_ADDER_ACTIVITY_EXTRA)) {
            if (intent == null)
                intent = new Intent();

            intent.putExtra(MainMenuActivity.START_ADDER_ACTIVITY_EXTRA, 1);
        }

        return intent;
    }

    @Override
    public void pass(int resultCode) {
        setResult(resultCode, constructExtras());
        finish();
    }

    @Override
    public void pass(int resultCode, Intent intent) {
        Intent extras = constructExtras();

        if (resultCode == RESULT_OK && intent != null) {
            if (extras != null)
                intent.putExtras(extras.getExtras());

            setResult(resultCode, intent);
        }
        else
            setResult(resultCode, extras);

        finish();
    }
}