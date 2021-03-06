package com.lucerlabs.wake;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.EditText;

public class AlarmViewModel extends BaseObservable {
	final Context mContext;
	private CharSequence mFormat12;
	private Alarm mAlarm;
	private TimePickerDialog mTimePicker;

	private AlertDialog mLabelEditorDialog;
	private String mTime;
	private Boolean mEnabled;
	private Boolean mIsExpanded;
	private ViewGroup mTransitionContainer;
	private View mAlarmDetails;
	private LinearLayout mDaysContainer;
	private Spinner mAudioTrackSpinner;
	private String mDurationStatus;

	private final AlarmsFragment.AlarmFragmentListener mAlarmOnChange;
	private final TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// what if mAlarm is null?
			if (mAlarm != null) {
				mAlarm.setHour(hourOfDay);
				mAlarm.setMinute(minute);
				notifyChange();
				updateTime();
				mAlarmOnChange.postAlarms();
			}
		}
	};

	public AlarmViewModel(Context context, AlarmsFragment.AlarmFragmentListener changeHandler) {
		mContext = context;
		mAlarmOnChange = changeHandler;
		mFormat12 = get12ModeFormat(0.001f /* amPmRatio */);
		mTimePicker = new TimePickerDialog(mContext, timePickerListener, 9, 0, false);
		initLabelEditorDialog(mContext);
		mTime = "0:00";
		mIsExpanded = false;

	}

	public void onTimeClick() {
		mTimePicker.updateTime(mAlarm.getHour(), mAlarm.getMinute());
		mTimePicker.show();
	}

	public void onLabelClick() {
		mLabelEditorDialog.show();
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

	public void setAudioTrackSpinner(Spinner spinner) {
		mAudioTrackSpinner = spinner;
	}

	public void setAlarmDetailsView (View view) {
		mAlarmDetails = view;
	}

	public void setDayContainer (LinearLayout layout) {
		mDaysContainer = layout;
	}

	public void setAlarm(Alarm alarm) {
		mAlarm = alarm;
		updateTime();
		updateDays();
		updateDurationStatus();
		updateAudioTrack();
		notifyChange();
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
		mAlarmOnChange.postAlarms();
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

	public void updateAudioTrack() {
		int audioTrack = mAlarm.getAudio();
		int spinnerIndex = 0;
		switch (audioTrack) {
			case 11:
				spinnerIndex = 0;
				break;
			case 13:
				spinnerIndex = 1;
				break;
			case 15:
				spinnerIndex = 2;
				break;
			case 17:
				spinnerIndex = 3;
				break;
			case 19:
				spinnerIndex = 4;
				break;
			case 21:
				spinnerIndex = 5;
				break;
			case 22:
				spinnerIndex = 6;
				break;
		}
		mAudioTrackSpinner.setSelection(spinnerIndex);
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
		mAlarm.setBrightness(value);
		mAlarmOnChange.postAlarms();
	}

	public int getBrightness() {
		return mAlarm.getBrightness();
	}

	public void setVolume(int value) {
		mAlarm.setVolume(value);
		mAlarmOnChange.postAlarms();
	}

	public int getVolume() {
		return mAlarm.getVolume();
	}

	public void setDuration(int value) {
		mAlarm.setDuration(value);
		updateDurationStatus();
		mAlarmOnChange.postAlarms();
	}

	public int getDuration() {
		return mAlarm.getDuration();
	}

	public int getAudio(){ return mAlarm.getAudio();}

	public void setAudio(int value){
		mAlarm.setAudio(value);
		mAlarmOnChange.postAlarms();
	}

	@Bindable
	public String getLabel() {
		return mAlarm.getLabel();
	}

	public void setLabel(String label) {
		mAlarm.setLabel(label);
		notifyPropertyChanged(com.lucerlabs.wake.BR.label);
		mAlarmOnChange.postAlarms();
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
		int dayButtonDimension = (int) mContext.getResources().getDimension(R.dimen.day_of_week_button_size);
		dayButtonLayoutParams.width = dayButtonDimension;
		dayButtonLayoutParams.height = dayButtonDimension;

		List<DayOfWeek> days = new ArrayList<>();
		days.add(DayOfWeek.SUNDAY);
		days.add(DayOfWeek.MONDAY);
		days.add(DayOfWeek.TUESDAY);
		days.add(DayOfWeek.WEDNESDAY);
		days.add(DayOfWeek.THURSDAY);
		days.add(DayOfWeek.FRIDAY);
		days.add(DayOfWeek.SATURDAY);

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
					mAlarmOnChange.postAlarms();
				}
			});
			mDaysContainer.addView(d, dayButtonLayoutParams);
		}
	}

	private void initLabelEditorDialog(Context context) {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		LayoutInflater inflater = LayoutInflater.from(context);
		View editorView = inflater.inflate(R.layout.label_edit_dialog, null);
		dialogBuilder.setView(editorView);
		final EditText input = (EditText) editorView.findViewById(R.id.input);


		dialogBuilder
			.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						String currentText = input.getText().toString();
						if (currentText.length() > 0) {
							setLabel(currentText);
						}
					}
				})
			.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

		mLabelEditorDialog = dialogBuilder.create();
	}

	private void initAudioSelectDialog(Context context){

	}
}


