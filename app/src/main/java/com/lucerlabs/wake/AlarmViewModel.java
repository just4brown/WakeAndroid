package com.lucerlabs.wake;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;

import java.util.Calendar;
import java.util.Locale;

public class AlarmViewModel {
	private CharSequence mFormat12;

	private int mHour;
	private int mMinute;
	private boolean Enabled;
	private String Time;

	public AlarmViewModel(int hour, int minute, boolean enabled) {
		mHour = hour;
		mMinute = minute;
		Enabled = enabled;
		mFormat12 = get12ModeFormat(0.22f /* amPmRatio */);

		updateTime();
	}

	public boolean isEnabled() {
		return Enabled;
	}

	public void setEnabled(boolean enabled) {
		Enabled = enabled;
	}

	public int getHour() {
		return mHour;
	}

	public void setHour(int mHour) {
		this.mHour = mHour;
		updateTime();
	}

	public int getMinute() {
		return mMinute;
	}

	public void setMinute(int mMinute) {
		this.mMinute = mMinute;
		updateTime();
	}

	public String getTime() {
		return Time;
	}

	private void updateTime() {
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, mHour);
		calendar.set(Calendar.MINUTE, mMinute);
		final CharSequence text = DateFormat.format(mFormat12, calendar);
		Time = text.toString();
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
}
