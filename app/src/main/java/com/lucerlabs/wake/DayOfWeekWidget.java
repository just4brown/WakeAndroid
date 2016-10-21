package com.lucerlabs.wake;

import android.content.Context;
import android.widget.Button;


import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class DayOfWeekWidget extends Button {
	private Boolean mSelected;
	private DayOfWeek mDay;
	// style="?android:attr/borderlessButtonStyle"

	public DayOfWeekWidget(Context context, int defStyleAttr, DayOfWeek day, Boolean selected) {
		super(context, null, defStyleAttr);
		mDay = day;
		mSelected = selected;
		this.setText(day.toString().substring(0, 1));
		this.setPadding(8, 0, 8, 0);
		updateBackground(mSelected);

		// TODO: widget will not respond to programmatic changes to underlying data. Use observable?
	}

	public void setIsSelected(boolean value) {
		mSelected = value;
		updateBackground(mSelected);
	}

	public boolean getIsSelected() {
		return mSelected;
	}

	public DayOfWeek getDayOfWeek() {
		return mDay;
	}

	private void updateBackground(Boolean selected) {
		if (selected) {
			this.setBackgroundResource(R.drawable.bg_circle_selected);
			this.setTextColor(BLACK);
		} else {
			this.setBackgroundResource(R.drawable.bg_circle_unselected);
			this.setTextColor(WHITE);
		}
	}
}
