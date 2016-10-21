package com.lucerlabs.wake;

import android.app.TimePickerDialog;
import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
	private LinearLayout mDaysContainer;
	private String mDurationStatus;

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

	public void setDayContainer (LinearLayout layout) {
		mDaysContainer = layout;
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
		updateDays();
		updateDurationStatus();
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

	@Bindable
	public String getDurationStatus() {
		return mDurationStatus;
	}

	public void updateDurationStatus() {
		int currentDuration = mAlarm.getDuration();
		String units = currentDuration > 1 ? " minutes" : " minute";
		mDurationStatus = "Duration: " + Integer.toString(currentDuration) + units;
		notifyPropertyChanged(com.lucerlabs.wake.BR.durationStatus);
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
		updateDurationStatus();
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

	private void updateTime() {
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, mAlarm.getHour());
		calendar.set(Calendar.MINUTE, mAlarm.getMinute());
		final CharSequence text = DateFormat.format(mFormat12, calendar);
		mTime = text.toString();
		notifyPropertyChanged(com.lucerlabs.wake.BR.time);
	}

	private void updateDays() {
		// TODO: consier using recycler view
		if (mAlarm == null) {
			return;
		}

		LinearLayout.LayoutParams dayButtonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		int dayButtonDimension = (int) mContext.getResources().getDimension(R.dimen.touch_target_min_size);
		dayButtonLayoutParams.width = dayButtonDimension;
		dayButtonLayoutParams.height = dayButtonDimension;

		List<DayOfWeek> days = Arrays.asList(DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY);
		HashSet<DayOfWeek> selectedDays = new HashSet<>(mAlarm.getDays());
		for (DayOfWeek day : days) {
			DayOfWeekWidget d = new DayOfWeekWidget(mContext, android.support.design.R.attr.borderlessButtonStyle, day, selectedDays.contains(day));
			d.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					DayOfWeekWidget dayWidget = ((DayOfWeekWidget) v);
					boolean currentSelectionValue = dayWidget.getIsSelected();
					boolean newSelectionValue = !currentSelectionValue;
					dayWidget.setIsSelected(newSelectionValue);
					if (newSelectionValue) {
						mAlarm.addDay(dayWidget.getDayOfWeek());
					} else {
						mAlarm.removeDay(dayWidget.getDayOfWeek());
					}

				}
			});
			mDaysContainer.addView(d, dayButtonLayoutParams);
		}
	}
}
