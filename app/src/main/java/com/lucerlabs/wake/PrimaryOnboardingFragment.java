package com.lucerlabs.wake;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class PrimaryOnboardingFragment extends Fragment {

	private PrimaryOnboardingFragmentListener mListener;
	private SettingsFragment.SettingsFragmentListener mSettingsListener;
	private Button mBackButton;
	private Button mFinishButton;

	public PrimaryOnboardingFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View mainView = inflater.inflate(R.layout.primary_onboarding, container, false);
		mFinishButton = (Button) mainView.findViewById(R.id.finish_button);
		mBackButton = (Button) mainView.findViewById(R.id.back_button);

		Fragment existingFragment = getChildFragmentManager().findFragmentByTag("onboardingSettings");
		if (existingFragment == null) {
			existingFragment = new OnboardingSettingsFragment();
		}
		((OnboardingSettingsFragment) existingFragment).setFragmentListener(mSettingsListener);

		FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
		transaction.replace(R.id.frame_onboarding_settings, existingFragment, "onboardingSettings").commit();
		return mainView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mFinishButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mListener.finishOnboarding();
			}
		});
		mBackButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mListener.goBack();
			}
		});
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof PrimaryOnboardingFragmentListener) {
			mListener = (PrimaryOnboardingFragmentListener) context;

		} else {
			throw new RuntimeException(context.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
		mBackButton = null;
		mFinishButton = null;
	}

	public void setFragmentListener(PrimaryOnboardingFragmentListener listener) {
		if (mListener == null) {
			mListener = listener;
		}
	}

	public void setPreferenceListener(SettingsFragment.SettingsFragmentListener listener) {
		if (mSettingsListener == null) {
			mSettingsListener = listener;
		}
	}

	public interface PrimaryOnboardingFragmentListener {
		void finishOnboarding();
		void goBack();
	}
}

