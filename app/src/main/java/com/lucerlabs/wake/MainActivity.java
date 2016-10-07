package com.lucerlabs.wake;

import android.app.TimePickerDialog;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TimePicker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

	private ObservableArrayList<Alarm> mAlarms;
	private AlarmAdapter mAlarmAdapter;
	private final OkHttpClient httpClient = new OkHttpClient();
	public static final okhttp3.MediaType MEDIA_TYPE_JSON = okhttp3.MediaType.parse("application/json; charset=utf-8");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		final TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				Alarm alarm = new Alarm("New alarm", hourOfDay, minute, true);
				mAlarms.add(alarm);
				mAlarmAdapter.notifyDataSetChanged();
			}
		};
		final TimePickerDialog newAlarmDialog = new TimePickerDialog(this, timePickerListener, 9, 0, false);
		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				newAlarmDialog.show();
			}
		});

		ImageButton cloudUpload = (ImageButton) findViewById(R.id.upload_button);
		cloudUpload.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				postAlarmsAsync();
			}
		});

		RecyclerView recyclerView = (RecyclerView) findViewById(R.id.alarms);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		mAlarms = new ObservableArrayList<Alarm>();
		mAlarmAdapter = new AlarmAdapter(mAlarms);
		recyclerView.setAdapter(mAlarmAdapter);
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
							mAlarmAdapter.notifyDataSetChanged();
						}
					});
				}
				else {
					throw new IOException("Http failure");
				}
			}

			@Override
			public void onFailure(Call call, IOException e) {
				e.printStackTrace();
			}
		});
	}

	public void postAlarmsAsync() {
		httpClient.newCall(BuildPostAlarmsRequest(mAlarms)).enqueue(new Callback() {
			@Override
			public void onResponse(Call call, final Response response) throws IOException {
				if (response.isSuccessful()) {

				}
				else {
					throw new IOException("Http failure");
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

	private static Request BuildPostAlarmsRequest(ObservableArrayList<Alarm> alarms) {
		List<AlarmDto> alarmDtos = new ArrayList();
		for (Alarm model : alarms) {
			alarmDtos.add(mapAlarm(model));
		}
		ObjectMapper objectMapper = new ObjectMapper();
		final ObjectWriter w = objectMapper.writer();
		try {
			byte[] json = w.writeValueAsBytes(alarmDtos);

			return new Request.Builder()
					.header("Authorization", "bearer 3")
					.header("Content-Type","application/json")
					.post(RequestBody.create(MEDIA_TYPE_JSON, json))
					.url("http://wakeuserapi.azurewebsites.net/v1/alarms")
					.build();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return new Request.Builder().build();
		}
	}

	public static Alarm mapAlarm(AlarmDto alarmDto) {
		return new Alarm(
			alarmDto.getHour(),
			alarmDto.getMinute(),
			alarmDto.getIsActive(),
			alarmDto.getUserID(),
			alarmDto.getIsSynchronized(),
			alarmDto.getDays(),
			alarmDto.getAudio(),
			alarmDto.getDuration(),
			alarmDto.getBrightness(),
			alarmDto.getVolume(),
			alarmDto.getAllowSnooze(),
			alarmDto.getCreatedAt(),
			alarmDto.getLabel()
		);
	}

	public static AlarmDto mapAlarm(Alarm alarm) {
		return new AlarmDto(
				alarm.getUserID(),
				alarm.getLabel(),
				alarm.getSynchronized(),
				alarm.getEnabled(),
				alarm.getHour(),
				alarm.getMinute(),
				alarm.getDays(),
				alarm.getAudio(),
				alarm.getDuration(),
				alarm.getBrightness(),
				alarm.getVolume(),
				alarm.getAllowSnooze(),
				alarm.getCreatedAt()
		);
	}
}
