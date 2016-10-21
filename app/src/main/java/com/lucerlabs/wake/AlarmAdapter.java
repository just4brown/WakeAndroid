package com.lucerlabs.wake;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lucerlabs.wake.databinding.AlarmBinding;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmViewHolder> {
	private ObservableArrayList<Alarm> mAlarms;

	public AlarmAdapter(ObservableArrayList<Alarm> alarms) {
		mAlarms = alarms;
	}

	@Override
	public AlarmViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
		Context context = parent.getContext();
		LayoutInflater inflater = LayoutInflater.from(context);
		AlarmBinding alarm = AlarmBinding.inflate(inflater, parent, false);
		return new AlarmViewHolder(context, alarm);
	}

	@Override
	public void onBindViewHolder(AlarmViewHolder holder, int position) {
		Alarm alarm = mAlarms.get(position);
		holder.bindAlarm(alarm);

		// potentially should be using getAdapterPosition?`
		final int index = position;
		holder.getBinding().deleteButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mAlarms.remove(index);
				notifyItemRemoved(index);
			}
		});
	}

	@Override
	public int getItemCount() {
		return mAlarms.size();
	}
}
