package com.lucerlabs.wake;

import android.app.Fragment;
import android.content.Context;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class OnboardingFragment extends Fragment {

	private AlarmAdapter mAlarmAdapter;
	private RecyclerView mRecyclerView;

	private OnboardingFragmentListener mListener;

	public OnboardingFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View alarmView = inflater.inflate(R.layout.onboarding, container, false);
		// set on clicker for set up
		// set on clicker for associate
		// set on clicker for
		return alarmView;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnboardingFragmentListener) {
			mListener = (OnboardingFragmentListener) context;

		} else {
			throw new RuntimeException(context.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
		mAlarmAdapter = null;
	}

	public interface OnboardingFragmentListener {
		View.OnClickListener getSignOutButtonListener();
		View.OnClickListener getPrimaryUserSelectedListener();
		View.OnClickListener getSecondaryUserSelectedListener();
	}
}

