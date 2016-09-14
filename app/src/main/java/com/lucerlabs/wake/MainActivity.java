package com.lucerlabs.wake;

import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import java.io.IOException;
import java.util.Collection;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

	private ObservableArrayList<AlarmViewModel> mAlarms;
	private AlarmAdapter mAlarmAdapter;
	private final OkHttpClient httpClient = new OkHttpClient();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
			}
		});

		ListView alarms = (ListView) findViewById(R.id.alarms);
		mAlarms = new ObservableArrayList<AlarmViewModel>();
		mAlarmAdapter = new AlarmAdapter(mAlarms);
		alarms.setAdapter(mAlarmAdapter);
		getAlarmsAsync();
	}

	public void getAlarmsAsync() {
		httpClient.newCall(BuildGetAlarmsRequest()).enqueue(new Callback() {
			@Override
			public void onResponse(Call call, final Response response) throws IOException {
				if (response.isSuccessful()) {
					final String rawJsonData = response.body().string();
					ObjectMapper mapper = new ObjectMapper();
					// TODO: catch exceptions here
					final Collection<AlarmDto> result = mapper.readValue(rawJsonData, new TypeReference<Collection<AlarmDto>>() { });

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							for (AlarmDto alarmDto : result) {
								mAlarms.add(mapAlarm(alarmDto));
							}
						}
					});
				}
				else {
					throw new IOException("Htp failure");
				}
			}

			@Override
			public void onFailure(Call call, IOException e) {
				e.printStackTrace();
			}
		});
	}

	private static Request BuildGetAlarmsRequest() {
		return new Request.Builder()
			.header("Authorization", "bearer 3")
			.header("Content-Type","application/json")
			.url("http://wakeuserapi.azurewebsites.net/v1/alarms")
			.build();
	}

	public static AlarmViewModel mapAlarm(AlarmDto alarmDto) {
		return new AlarmViewModel(alarmDto.getHour(), alarmDto.getMinute(), alarmDto.getIsActive());
	}
}
