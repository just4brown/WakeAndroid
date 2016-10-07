package com.lucerlabs.wake;

import android.app.TimePickerDialog;
import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import com.lucerlabs.wake.databinding.AlarmBinding;

public class AlarmViewModel extends BaseObservable {
	final Context mContext;
	private CharSequence mFormat12;
	private Alarm mAlarm;
	private TimePickerDialog mTimePicker;
	private String mTime;
	private Boolean mEnabled;
	private Boolean mIsExpanded;
	private ViewGroup mTransitionContainer;
	private View mAlarmDetails;

	public AlarmViewModel(Context context) {
		mContext = context;
		mFormat12 = get12ModeFormat(0.001f /* amPmRatio */);
		mTimePicker = new TimePickerDialog(mContext, timePickerListener, 9, 0, false);
		mTime = "0:00";
		mIsExpanded = false;
	}

	public void onClick() {
		mTimePicker.show();
	}

	public boolean isExpanded() {
		return mIsExpanded;
	}

	public void Expand() {
		if (mTransitionContainer != null) {
			TransitionManager.beginDelayedTransition(mTransitionContainer);
			mIsExpanded = !mIsExpanded;
			mAlarmDetails.setVisibility(mIsExpanded ? View.VISIBLE : View.GONE);
			notifyChange();
		}

	}

	public void setmTransitionContainer(ViewGroup view) {
		mTransitionContainer = view;
	}

	public void setAlarmDetailsView (View view) {
		mAlarmDetails = view;
	}

	TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// what if mAlarm is null?
			if (mAlarm != null) {
				mAlarm.setHour(hourOfDay);
				mAlarm.setMinute(minute);
				notifyChange();
				updateTime();
			}
		}
	};

	public void setAlarm(Alarm alarm) {
		mAlarm = alarm;
		notifyChange();
		updateTime();
	}

	public Alarm getAlarm()  {
		return mAlarm;
	}

	@Bindable
	public Boolean getEnabled() {
		return mAlarm.getEnabled();
	}

	public void setEnabled(Boolean enabled) {
		mAlarm.setEnabled(enabled);
		notifyPropertyChanged(com.lucerlabs.wake.BR.enabled);
	}

	@Bindable
	public Boolean getIsExpanded() {
		return mIsExpanded;
	}

	public void setmIsExpanded(Boolean expanded) {
		mIsExpanded = expanded;
		notifyChange();
	}

	@Bindable
	public String getTime() {
		return mTime;
	}

	private void updateTime() {
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, mAlarm.getHour());
		calendar.set(Calendar.MINUTE, mAlarm.getMinute());
		final CharSequence text = DateFormat.format(mFormat12, calendar);
		mTime = text.toString();
		notifyPropertyChanged(com.lucerlabs.wake.BR.time);
	}

	public static CharSequence get12ModeFormat(float amPmRatio) {
		String pattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), "hma");
		if (amPmRatio <= 0) {
			pattern = pattern.replaceAll("a", "").trim();
		}

		// Replace spaces with "Hair Space"
		pattern = pattern.replaceAll(" ", "\u200A");
		// Build a spannable so that the am/pm will be formatted
		int amPmPos = pattern.indexOf('a');
		if (amPmPos == -1) {
			return pattern;
		}

		final Spannable sp = new SpannableString(pattern);
		sp.setSpan(new RelativeSizeSpan(amPmRatio), amPmPos, amPmPos + 1,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp.setSpan(new StyleSpan(Typeface.NORMAL), amPmPos, amPmPos + 1,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp.setSpan(new TypefaceSpan("sans-serif"), amPmPos, amPmPos + 1,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		return sp;
	}

	public void setBrightness(int value) {
		// How should we manage the "isSynchronized" flag?
		// For now, any change to the alarm triggered by a user action will clear the alarm's isSynchronized flag.
		// Potential issue: what happens if a user makes a change, then reverts it before the alarms is posted,
		// should we be smart about that and enusre that fields have actually changed before marking an alarm
		// as not synchronized?
		mAlarm.setSynchronized(false);
		mAlarm.setBrightness(value);
	}

	public int getBrightness() {
		return mAlarm.getBrightness();
	}

	public void setVolume(int value) {
		mAlarm.setSynchronized(false);
		mAlarm.setVolume(value);
	}

	public int getVolume() {
		return mAlarm.getVolume();
	}

	public void setDuration(int value) {
		mAlarm.setSynchronized(false);
		mAlarm.setDuration(value);
	}

	public int getDuration() {
		return mAlarm.getDuration();
	}

	@Bindable
	public String getLabel() {
		return mAlarm.getLabel();
	}

	public void setLabel(String label) {
		mAlarm.setLabel(label);
		notifyPropertyChanged(com.lucerlabs.wake.BR.label);
	}
}
