package com.lucerlabs.wake;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.support.v14.preference.PreferenceFragment;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;

import java.util.TimeZone;

import io.particle.android.sdk.devicesetup.ParticleDeviceSetupLibrary;

public class OnboardingSettingsFragment extends PreferenceFragment {

	private ListPreference mListPreference;

	private SettingsFragment.SettingsFragmentListener mListener;

	public OnboardingSettingsFragment() {
	}

	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		setPreferencesFromResource(R.xml.pref_onboarding, rootKey);
		mListPreference = (ListPreference) getPreferenceManager().findPreference("side_of_bed_preference");
		mListPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				preference.setSummary(newValue.toString());
				return true;
			}
		});

		Preference installation = getPreferenceManager().findPreference("install_preference");
		installation.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				mListener.setNewFragment(new ScreenSlidePageFragment(), true);
				return true;
			}
		});

		Preference connection = getPreferenceManager().findPreference("connect_preference");
		connection.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				mListener.startParticleSetup();
				return true;
			}
		});

		ListPreference timezone = (ListPreference) getPreferenceManager().findPreference("timezone_preference");
		timezone.setSummary(TimeZone.getDefault().getID());
		timezone.setEntries(TimeZone.getAvailableIDs());
		timezone.setEntryValues(TimeZone.getAvailableIDs());
		connection.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				preference.setSummary(newValue.toString());
				return true;
			}
		});
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof SettingsFragment.SettingsFragmentListener) {
			mListener = (SettingsFragment.SettingsFragmentListener) context;
		} else {
			throw new RuntimeException(context.toString()
					+ " must implement SettingsFragmentListener");
		}
	}
}