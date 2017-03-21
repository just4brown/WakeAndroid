package com.lucerlabs.wake;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class SecondaryOnboardingFragment extends Fragment {

	private SecondaryOnboardingFragmentListener mListener;
	private Button mSubmitButton;
	private EditText mInput;

	public SecondaryOnboardingFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View mainView = inflater.inflate(R.layout.secondary_onboarding, container, false);

		mInput = (EditText) mainView.findViewById(R.id.secondary_onboarding_code_input );
		mSubmitButton = (Button) mainView.findViewById(R.id.secondary_onboarding_submit_button);
		mSubmitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mListener.submitSecondaryUserCode(mInput.getText().toString());
			}
		});

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
		mInput = null;
		mSubmitButton = null;
	}

	public interface SecondaryOnboardingFragmentListener {
		void submitSecondaryUserCode(String code);
	}
}

