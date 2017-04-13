package com.lucerlabs.wake;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class OnboardingFragment extends Fragment {

	private Button mPrimaryUserButton;
	private Button mSecondaryUserButton;
	private Button mSignOutButton;

	private OnboardingFragmentListener mListener;

	public OnboardingFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View mainView = inflater.inflate(R.layout.onboarding, container, false);
		mPrimaryUserButton = (Button) mainView.findViewById(R.id.primary_user_button);
		mSecondaryUserButton = (Button) mainView.findViewById(R.id.secondary_user_button);
		mSignOutButton = (Button) mainView.findViewById(R.id.sign_out_button);
		return mainView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mPrimaryUserButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mListener.selectPrimaryUserOnboarding();
			}
		});
		mSecondaryUserButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mListener.selectSecondaryUserOnboarding();
			}
		});
		mSignOutButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mListener.doSignOut();
			}
		});
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
		mPrimaryUserButton = null;
		mSecondaryUserButton = null;
	}

	public void setOnboardingFragmentListener(OnboardingFragmentListener listener) {
		if (mListener == null) {
			mListener = listener;
		}
	}

	public interface OnboardingFragmentListener {
		void doSignOut();
		void selectPrimaryUserOnboarding();
		void selectSecondaryUserOnboarding();
	}
}

