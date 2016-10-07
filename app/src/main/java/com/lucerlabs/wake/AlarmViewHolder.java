package com.lucerlabs.wake;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.lucerlabs.wake.databinding.AlarmBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AlarmViewHolder extends RecyclerView.ViewHolder {
	private final AlarmBinding mBinding;
	private final AlarmViewModel mAlarmViewModel;
	private final Pattern mDurationPattern = Pattern.compile("(\\d*)");

	public AlarmViewHolder(Context context, AlarmBinding binding) {
		super(binding.mainLayout);
		mBinding = binding;
		mAlarmViewModel = new AlarmViewModel(context);
		mBinding.setAlarm(mAlarmViewModel);

		// Where should these initializations live?
		// Here in the AlarmViewHolder? Or in the AlarmViewModel?
		List<String> durationValues = new ArrayList<>();
		// TODO: Move thresholds like this into an xml config file.
		int durationMax = 15;
		for (int i = 1; i <= durationMax; i++ ) {
			String value = Integer.toString(i);
			durationValues.add(i < 2 ? value + " minute" : value + " minutes");
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.spinner_item, durationValues);
		Spinner durationSpinner = mBinding.durationSpinner;
		durationSpinner.setAdapter(adapter);

		// HACK: getting the minute value by parsing the string; not ideal.
		durationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				TextView item = (TextView) view;
				CharSequence displayValue = item.getText();
				if (displayValue.length() > 0) {
					Matcher matcher = mDurationPattern.matcher(displayValue);
					boolean foundMatches = matcher.lookingAt();
					if (foundMatches) {
						Integer minuteValue = 0;
						try {
							minuteValue = Integer.parseInt(matcher.group());
						} catch (NumberFormatException e) {
							e.printStackTrace();
							return;
						}

						mAlarmViewModel.setDuration(minuteValue);
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		SeekBar brightnessSeekBar = mBinding.brightnessSeekBar;
		// TODO: Move thresholds like this into an xml config file.
		brightnessSeekBar.setMax(10);
		brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				mAlarmViewModel.setBrightness(progress);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});

		SeekBar volumeSeekBar = mBinding.volumeSeekBar;
		// TODO: Move thresholds like this into an xml config file.
		volumeSeekBar.setMax(10);
		volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				mAlarmViewModel.setVolume(progress);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});
	}

	public AlarmBinding getBinding() {
		return mBinding;
	}

	public void bindAlarm(Alarm alarm) {
		AlarmViewModel alarmViewModel = mBinding.getAlarm();
		alarmViewModel.setAlarm(alarm);
		alarmViewModel.setmTransitionContainer(mBinding.mainLayout);
		alarmViewModel.setAlarmDetailsView(mBinding.alarmDetails);
	}
}
