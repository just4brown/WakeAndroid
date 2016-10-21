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

	public AlarmViewHolder(Context context, AlarmBinding binding) {
		super(binding.mainLayout);
		mBinding = binding;
		mAlarmViewModel = new AlarmViewModel(context);
		mBinding.setAlarm(mAlarmViewModel);

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
