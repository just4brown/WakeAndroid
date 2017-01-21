package com.lucerlabs.wake;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SecondaryOnboardingFragment extends Fragment {

	private SecondaryOnboardingFragmentListener mListener;

	public SecondaryOnboardingFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View mainView = inflater.inflate(R.layout.secondary_onboarding, container, false);
		// set on clicker for set up
		// set on clicker for associate
		// set on clicker for
		return mainView;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof SecondaryOnboardingFragmentListener) {
			mListener = (SecondaryOnboardingFragmentListener) context;
			// getFragmentManager().beginTransaction().replace(R.id.onboarding_settings_fragment, new OnboardingSettingsFragment()).commit();
		} else {
			throw new RuntimeException(context.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	public interface SecondaryOnboardingFragmentListener {
		// void registerCode();
	}
}

