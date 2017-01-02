package com.lucerlabs.wake;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class ScreenSlidePageFragment extends Fragment {

	private ViewPager mPager;
	private PagerAdapter mPagerAdapter;

	// private AlarmFragmentListener mListener;

	public ScreenSlidePageFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View installationView = inflater.inflate(R.layout.installation_slide_show, container, false);

		ArrayList<String> paths = new ArrayList<>();
		paths.add("installation_icons_1.png");
		paths.add("installation_icons_2.png");
		paths.add("installation_icons_3.png");
		paths.add("installation_icons_4.png");
		mPager = (ViewPager) installationView.findViewById(R.id.pager);
		mPagerAdapter = new FullScreenImageAdapter(inflater, paths);
		mPager.setAdapter(mPagerAdapter);
		return installationView;
	}
}

