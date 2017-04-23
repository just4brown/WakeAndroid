package com.lucerlabs.wake;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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

	public AlarmViewHolder(Context context, AlarmBinding binding, AlarmsFragment.AlarmFragmentListener changeHandler) {
		super(binding.mainLayout);
		mBinding = binding;
		mAlarmViewModel = new AlarmViewModel(context, changeHandler);
		mBinding.setAlarm(mAlarmViewModel);

		// Populate audio select spinner and set listeners

		Spinner spinner = mBinding.audioSpinner;

		final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.audio_array, R.layout.spinner_style);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner.setAdapter(adapter);

		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String track = parent.getItemAtPosition(position).toString();

				int track_num = 0;

				switch (track){
					case "Groove":
						track_num = 11;
						break;
					case "Move":
						track_num = 13;
						break;
					case "Natural":
						track_num = 15;
						break;
					case "Rise":
						track_num = 17;
						break;
					case "Stroll":
						track_num = 19;
						break;
					case "Sunshine":
						track_num = 21;
						break;
					case "Alert":
						track_num = 22;
						break;
				}

				System.out.println("audio selected: " + track + " (" + track_num + ")");

				mAlarmViewModel.setAudio(track_num);

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});







		// Where should these initializations live?
		// Here in the AlarmViewHolder? Or in the AlarmViewModel?
		int maxDuration = 15;
		SeekBar durationSeekBar = mBinding.durationSeekBar;
		// TODO: Move thresholds like this into an xml config file.
		durationSeekBar.setMax(maxDuration);
		durationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				mAlarmViewModel.setDuration(progress);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

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

		mAlarmViewModel.setDayContainer(mBinding.daysOfWeek);
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
