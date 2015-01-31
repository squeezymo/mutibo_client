package com.squeezymo.mutibo.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.squeezymo.mutibo.ui.fragments.login.SignInFragment;
import com.squeezymo.mutibo.ui.fragments.login.SignUpFragment;

public class LogInPagerAdapter extends FragmentPagerAdapter {

	public LogInPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:
            return new SignInFragment();
		case 1:
			return new SignUpFragment();
		}

		return null;
	}

	@Override
	public int getCount() {
		return 2;
	}

}
