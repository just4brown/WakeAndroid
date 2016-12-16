package com.lucerlabs.wake;

import android.os.Bundle;
import android.support.v14.preference.PreferenceFragment;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class SettingsFragment extends PreferenceFragment {

	private ListPreference mListPreference;

	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		setPreferencesFromResource(R.xml.pref_general, rootKey);
		mListPreference = (ListPreference) getPreferenceManager().findPreference("side_of_bed_preference");
		mListPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				// your code here
				preference.setSummary(newValue.toString());
				return true;
			}
		});
	}
}
