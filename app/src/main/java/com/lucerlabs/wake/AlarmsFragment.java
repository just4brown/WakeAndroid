package com.lucerlabs.wake;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AlarmsFragment extends Fragment {

	private AlarmAdapter mAlarmAdapter;
	private RecyclerView mRecyclerView;

	private AlarmFragmentListener mListener;

	public AlarmsFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View alarmView = inflater.inflate(R.layout.content_main, container, false);
		mRecyclerView = (RecyclerView) alarmView.findViewById(R.id.alarms);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
		mRecyclerView.setAdapter(mAlarmAdapter);
		return alarmView;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof AlarmFragmentListener) {
			mListener = (AlarmFragmentListener) context;
			mAlarmAdapter = new AlarmAdapter(mListener.getObservableAlarms());
		} else {
			throw new RuntimeException(context.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
		mAlarmAdapter = null;
	}

	public interface AlarmFragmentListener {
		ObservableArrayList<Alarm> getObservableAlarms();
	}
}
