package com.lucerlabs.wake;

import android.app.Activity;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TimePicker;
import android.support.v4.widget.DrawerLayout;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener, AlarmsFragment.AlarmFragmentListener, SettingsFragment.SettingsFragmentListener {

	private ObservableArrayList<Alarm> mAlarms;
	private final OkHttpClient httpClient = new OkHttpClient();
	public static final okhttp3.MediaType MEDIA_TYPE_JSON = okhttp3.MediaType.parse("application/json; charset=utf-8");
	private String authIdToken;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private ListView mDrawerList;
	private FloatingActionButton addAlarmButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		authIdToken = extras.getString("AUTH_ID_TOKEN");
		setContentView(R.layout.activity_main);


		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		mDrawerLayout.setDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);
		navigationView.setCheckedItem(R.id.nav_alarms);

		final TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				Alarm alarm = new Alarm("New alarm", hourOfDay, minute, true);
				mAlarms.add(alarm);
			}
		};
		final TimePickerDialog newAlarmDialog = new TimePickerDialog(this, timePickerListener, 9, 0, false);

		addAlarmButton = (FloatingActionButton) findViewById(R.id.fab);
		addAlarmButton.setOnClickListener(new View.OnClickListener() {
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

		mAlarms = new ObservableArrayList<Alarm>();
		getFragmentManager().beginTransaction().add(R.id.frame_content, new AlarmsFragment()).commit();
		getAlarmsAsync();
	}

	@Override
	public ObservableArrayList<Alarm> getObservableAlarms() {
		return mAlarms;
	}

	@Override
	public void setNewFragment(Fragment fragment) {
		getFragmentManager().beginTransaction().replace(R.id.frame_content, fragment).commit();
	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();

		if (id == R.id.nav_settings) {
			addAlarmButton.setVisibility(View.INVISIBLE);
			getFragmentManager().beginTransaction().replace(R.id.frame_content, new SettingsFragment()).commit();
		} else if (id == R.id.nav_alarms) {
			addAlarmButton.setVisibility(View.VISIBLE);
			getFragmentManager().beginTransaction().replace(R.id.frame_content, new AlarmsFragment()).commit();
		}

		mDrawerLayout.closeDrawer(GravityCompat.START);
		return true;
	}

	public void getAlarmsAsync() {
		httpClient.newCall(BuildGetAlarmsRequest(this.authIdToken)).enqueue(new Callback() {
			@Override
			public void onResponse(Call call, final Response response) throws IOException {
				if (response.isSuccessful()) {
					final String rawJsonData = response.body().string();
					ObjectMapper mapper = new ObjectMapper();
					// TODO: catch exceptions here

					// Deserialize the alarmBody here
					final AlarmBody result = mapper.readValue(rawJsonData, AlarmBody.class);

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							for (AlarmDto alarmDto : result.getAlarms()) {
								mAlarms.add(mapAlarm(alarmDto));
							}
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
		httpClient.newCall(BuildPostAlarmsRequest(mAlarms, this.authIdToken)).enqueue(new Callback() {
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

	private static Request BuildGetAlarmsRequest(String authId) {
		return new Request.Builder()
			.header("Authorization", "bearer " + authId)
			.header("Content-Type","application/json")
			.url("http://wakeuserapi.azurewebsites.net/v1/alarms")
			.build();
	}

	private static Request BuildPostAlarmsRequest(ObservableArrayList<Alarm> alarms, String authId) {
		List<AlarmDto> alarmDtos = new ArrayList();
		for (Alarm model : alarms) {
			alarmDtos.add(mapAlarm(model));
		}
		ObjectMapper objectMapper = new ObjectMapper();
		final ObjectWriter w = objectMapper.writer();
		try {
			byte[] json = w.writeValueAsBytes(alarmDtos);

			return new Request.Builder()
					.header("Authorization", "bearer " + authId)
					.header("Content-Type","application/json")
					.post(RequestBody.create(MEDIA_TYPE_JSON, json))
					.url("http://wakeuserapi.azurewebsites.net/v1/alarms")
					.build();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return new Request.Builder().build();
		}
	}

	private static Alarm mapAlarm(AlarmDto alarmDto) {
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

	private static AlarmDto mapAlarm(Alarm alarm) {
		return new AlarmDto(
				alarm.getUserID(),
				alarm.getLabel(),
				alarm.getSynchronized(),
				alarm.getEnabled(),
				alarm.getHour(),
				alarm.getMinute(),
				alarm.getIntegerDays(),
				alarm.getAudio(),
				alarm.getDuration(),
				alarm.getBrightness(),
				alarm.getVolume(),
				alarm.getAllowSnooze(),
				alarm.getCreatedAt()
		);
	}
}
