package com.lucerlabs.wake;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.lucerlabs.wake.databinding.AlarmBinding;

public class AlarmAdapter extends BaseAdapter {
	private ObservableArrayList<AlarmViewModel> mAlarms;
	private final ObservableList.OnListChangedCallback<ObservableList<AlarmViewModel>> onListChangedCallback;

	public AlarmAdapter(ObservableArrayList<AlarmViewModel> alarms) {
		mAlarms = alarms;
		onListChangedCallback = new ObservableList.OnListChangedCallback<ObservableList<AlarmViewModel>>() {
			@Override public void onChanged(ObservableList<AlarmViewModel> sender) {
				notifyDataSetChanged();
			}

			@Override public void onItemRangeChanged(ObservableList<AlarmViewModel> sender, int positionStart, int itemCount) {
				notifyDataSetChanged();
			}

			@Override public void onItemRangeInserted(ObservableList<AlarmViewModel> sender, int positionStart, int itemCount) {
				notifyDataSetChanged();
			}

			@Override public void onItemRangeMoved(ObservableList<AlarmViewModel> sender, int fromPosition, int toPosition, int itemCount) {
				notifyDataSetChanged();
			}

			@Override public void onItemRangeRemoved(ObservableList<AlarmViewModel> sender, int positionStart, int itemCount) {
				notifyDataSetChanged();
			}
		};
		alarms.addOnListChangedCallback(onListChangedCallback);
	}

	@Override
	public AlarmViewModel getItem(int position) {
		return mAlarms.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getCount() {
		return mAlarms.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		AlarmBinding alarmbind = AlarmBinding.inflate(inflater, parent, false);
		alarmbind.setAlarm(mAlarms.get(position));

		return alarmbind.getRoot();
	}
}
