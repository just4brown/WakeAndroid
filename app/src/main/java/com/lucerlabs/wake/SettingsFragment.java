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

public class SettingsFragment extends PreferenceFragment {

	private ListPreference mListPreference;

	private SettingsFragmentListener mListener;

	public SettingsFragment() {
	}

	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		setPreferencesFromResource(R.xml.pref_general, rootKey);
		mListPreference = (ListPreference) getPreferenceManager().findPreference("side_of_bed_preference");
		mListPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
			preference.setSummary(newValue.toString());
				mListener.postSideOfBedPreference(newValue.toString());
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

		ListPreference timezonePref = (ListPreference) getPreferenceManager().findPreference("timezone_preference");
		timezonePref.setSummary(TimeZone.getDefault().getID());
		timezonePref.setEntries(TimeZone.getAvailableIDs());
		timezonePref.setEntryValues(TimeZone.getAvailableIDs());
		timezonePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				preference.setSummary(newValue.toString());
				return true;
			}
		});

		Preference addSecondaryUserPreference = getPreferenceManager().findPreference("secondary_user_section_add_preference");
		addSecondaryUserPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				mListener.generateSecondaryUserCode();
				return true;
			}
		});

		UserDto currentUserInfo = mListener.getCurrentUser();
		if (currentUserInfo != null) {
			String coreId = currentUserInfo.getCoreID();
			String timezone = currentUserInfo.getTimezone();
			String sideOfBed = currentUserInfo.getSideOfBed();

			if (coreId != null && !coreId.isEmpty()) {
				connection.setSummary("Connected");
			}

			if (timezone != null && !timezone.isEmpty()) {
				timezonePref.setSummary(timezone);
			}

			if (sideOfBed != null && !sideOfBed.isEmpty()) {
				String displayText = null;
				if (sideOfBed.contentEquals("P")) {
					displayText = "Left side";
				} else if (sideOfBed.contentEquals("S")) {
					displayText = "Right side";
				} else if (sideOfBed.contentEquals("N")) {
					displayText = "I have the bed to myself";
				}

				if (displayText != null) {
					mListPreference.setSummary(displayText);
				}
			}
		}
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof SettingsFragmentListener) {
			mListener = (SettingsFragmentListener) context;
		} else {
			throw new RuntimeException(context.toString()
					+ " must implement SettingsFragmentListener");
		}
	}

	public interface SettingsFragmentListener {
		void setNewFragment(Fragment fragment, boolean showBackButton);
		void postSideOfBedPreference(String sideOfBed);
		void postTimezone(String timezone);
		void startParticleSetup();
		void generateSecondaryUserCode();
		UserDto getCurrentUser();
	}
}
