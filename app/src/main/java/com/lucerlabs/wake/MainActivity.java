package com.lucerlabs.wake;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.support.v4.widget.DrawerLayout;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.particle.android.sdk.accountsetup.*;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import io.particle.android.sdk.devicesetup.ParticleDeviceSetupLibrary;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.microsoft.windowsazure.notifications.NotificationsManager;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
		implements
		NavigationView.OnNavigationItemSelectedListener,
		AlarmsFragment.AlarmFragmentListener,
		SettingsFragment.SettingsFragmentListener,
		OnboardingFragment.OnboardingFragmentListener,
		PrimaryOnboardingFragment.PrimaryOnboardingFragmentListener,
		SecondaryOnboardingFragment.SecondaryOnboardingFragmentListener {

	private ObservableArrayList<Alarm> mAlarms;
	private final OkHttpClient httpClient = new OkHttpClient();
	public static final okhttp3.MediaType MEDIA_TYPE_JSON = okhttp3.MediaType.parse("application/json; charset=utf-8");
	private String authIdToken;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private ListView mDrawerList;
	private FloatingActionButton addAlarmButton;
	private UserDto mCurrentUser;

	// Notification stuff
	public static MainActivity mainActivity;
	public static Boolean isVisible = false;
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Bundle extras = getIntent().getExtras();
		//authIdToken = extras.getString("AUTH_ID_TOKEN");

		authIdToken = CredentialsManager.getCredentials(this).getIdToken();

		setContentView(R.layout.activity_main);

		mainActivity = this;
		NotificationsManager.handleNotifications(this, NotificationSettings.SenderId, WakeNotificationHandler.class);
		registerWithNotificationHubs();

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerToggle = new ActionBarDrawerToggle(
				this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mDrawerToggle.syncState();

		mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!mDrawerToggle.isDrawerIndicatorEnabled()) {
					onBackPressed();
				}
			}
		});

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);
		navigationView.setCheckedItem(R.id.nav_alarms);

		// Set user info in sidebar

		TextView mTextView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_name);
		mTextView.setText("Bro");
		mTextView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_email);
		mTextView.setText("Bro@bro.com");


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

		mAlarms = new ObservableArrayList<Alarm>();
		getFragmentManager().beginTransaction().add(R.id.frame_content, new AlarmsFragment()).commit();

		ParticleDeviceSetupLibrary.init(this.getApplicationContext(), MainActivity.class);
	}

	@Override
	protected void onStart() {
		super.onStart();
		isVisible = true;
		getUserInfoAsync();
	}

	@Override
	protected void onPause() {
		super.onPause();
		isVisible = false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		isVisible = true;
	}

	@Override
	protected void onStop() {
		super.onStop();
		isVisible = false;
	}

	public void ToastNotify(final String notificationMessage) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(MainActivity.this, notificationMessage, Toast.LENGTH_LONG).show();
			}
		});
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
		GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
		int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (apiAvailability.isUserResolvableError(resultCode)) {
				apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
						.show();
			} else {
				Log.i("info", "This device is not supported by Google Play Services.");
				ToastNotify("This device is not supported by Google Play Services.");
				finish();
			}
			return false;
		}
		return true;
	}

	public void registerWithNotificationHubs()
	{
		Log.i("info", " Registering with Notification Hubs");

		if (checkPlayServices()) {
			// Start IntentService to register this application with GCM.
			Intent intent = new Intent(this, RegistrationIntentService.class);
			startService(intent);
		}
	}

	@Override
	public ObservableArrayList<Alarm> getObservableAlarms() {
		return mAlarms;
	}

	@Override
	public void setNewFragment(Fragment fragment, boolean showBackButton) {
		mDrawerToggle.setDrawerIndicatorEnabled(false);
		FragmentTransaction newFragmentTransaction = getFragmentManager().beginTransaction().replace(R.id.frame_content, fragment);
		addAlarmButton.setVisibility(View.INVISIBLE);
		if (showBackButton) {
			newFragmentTransaction.addToBackStack(null);
			mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
			mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		}

		newFragmentTransaction.commit();
	}

	@Override
	public void startParticleSetup() {
		ParticleDeviceSetupLibrary.startDeviceSetup(this, new WakeParticleSetupCompleteIntentBuilder());
	}

	@Override
	public void postSideOfBedPreference(String sideOfBed) {
		String sideOfBedValue = "";
		if (sideOfBed.contentEquals("Left side")) {
			sideOfBedValue = "S";
		} else if (sideOfBed.contentEquals("Right side")) {
			sideOfBedValue = "P";
		} else if (sideOfBed.contentEquals("I have the bed to myself")) {
			sideOfBedValue = "N";
		}

		if (!sideOfBedValue.isEmpty()) {
			mCurrentUser.setSideOfBed(sideOfBedValue);
			putUserAsync();
		}
	}

	@Override
	public void postTimezone(String timezone) {
		mCurrentUser.setTimezone(timezone);
		putUserAsync();
	}

	@Override
	public View.OnClickListener finishOnboardingClickListener() {
		return new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
				LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
//		View editorView = inflater.inflate(R.layout.label_edit_dialog, null);
//		dialogBuilder.setView(editorView);
				//final EditText input = (EditText) editorView.findViewById(R.id.input);


				dialogBuilder
						.setMessage("test")
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										dialog.cancel();
									}
								});

				dialogBuilder.create();

				// Check Wake Device

				if(mCurrentUser.getCoreID() == null || mCurrentUser.getCoreID() == ""){

				}

				// Check Side of Bed

				if(mCurrentUser.getSideOfBed() == null || mCurrentUser.getSideOfBed() == ""){

				}

				// Check time zone

				if(mCurrentUser.getTimezone() == null || mCurrentUser.getTimezone() == ""){

				}

				// if (currentUser.hasParticleCcode == null || currentUser.sideOfBed == null) {
					// show error dialog "not connected to wake" || "must select bedside"
					// buildOnboardingErrorDialog
					// show it
				// }

				// since timezone is automatically detected, we won't require the user to select a timezone
				// don't forget to post the timezone though if its still null
				

			}
		};



		// TODO: show popup confirming to user that he/she agrees to terms and conditions
	}

	@Override
	public View.OnClickListener goBackClickListener() {
		return new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		};
	}

	@Override
	public View.OnClickListener getSignOutButtonListener() {
		return new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!mDrawerToggle.isDrawerIndicatorEnabled()) {
					onBackPressed();
				}
			}
		};
	}

	@Override
	public View.OnClickListener getPrimaryUserSelectedListener() {
		return new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				setNewFragment(new PrimaryOnboardingFragment(), true);
			}
		};
	}

	@Override
	public View.OnClickListener getSecondaryUserSelectedListener() {
		return new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				setNewFragment(new SecondaryOnboardingFragment(), true);
			}
		};
	}

	@Override
	public void onBackPressed() {
		mDrawerToggle.setDrawerIndicatorEnabled(true);
		mDrawerToggle.setHomeAsUpIndicator(0);
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
		DrawerLayout drawer = mDrawerLayout;
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
		}else if (id == R.id.nav_sign_out) {
			addAlarmButton.setVisibility(View.INVISIBLE);
			CredentialsManager.deleteCredentials(getApplicationContext());
			startActivity(new Intent(this, LoginActivity.class));
		}



		mDrawerLayout.closeDrawer(GravityCompat.START);
		return true;
	}

	@Override
	public void postAlarms() {
		postAlarmsAsync();
	}

	public void getAlarmsAsync() {
		httpClient.newCall(BuildGetAlarmsRequest(this.authIdToken)).enqueue(new Callback() {
			@Override
			public void onResponse(Call call, final Response response) throws IOException {
				Log.e("HTTP STATUS CODE", Integer.toString(response.code()));
				if (response.isSuccessful()) {
					final String rawJsonData = response.body().string();
					ObjectMapper mapper = new ObjectMapper();
					// TODO: catch exceptions here

					// Deserialize the alarmBody here
					final AlarmBody result = mapper.readValue(rawJsonData, AlarmBody.class);

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mAlarms.clear();
							for (AlarmDto alarmDto : result.getAlarms()) {
								mAlarms.add(mapAlarm(alarmDto));
							}
						}
					});
				}
				else {
					Log.e("onResponse fail: ", response.body().string());
					throw new IOException("Http failure");
				}
			}

			@Override
			public void onFailure(Call call, IOException e) {
				e.printStackTrace();
			}
		});
	}

	public void getUserInfoAsync() {
		httpClient.newCall(BuildGetUserRequest(this.authIdToken)).enqueue(new Callback() {
			@Override
			public void onResponse(Call call, final Response response) throws IOException {
				Log.e("HTTP STATUS CODE", Integer.toString(response.code()));
				if (response.isSuccessful()) {
					final String rawJsonData = response.body().string();
					ObjectMapper mapper = new ObjectMapper();
					final UserDto user = mapper.readValue(rawJsonData, UserDto.class);

					mCurrentUser = user;

					if (user.isRegistered()) {
						// TODO: set user name/email/icon in nav drawer
						getAlarmsAsync();
					}

					else {
						// init onboarding
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								getSupportActionBar().setTitle("Setup");
								setNewFragment(new OnboardingFragment(), false);
								mDrawerToggle.setDrawerIndicatorEnabled(false);
								// TODO: reset the title again back to "wake"
							}
						});

					}
				}
				else {
					Log.e("onResponse fail: ", response.body().string());
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
				Log.e("HTTP STATUS CODE", Integer.toString(response.code()));
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

	public void putUserAsync() {
		httpClient.newCall(BuildPutUserRequest(mCurrentUser, this.authIdToken)).enqueue(new Callback() {
			@Override
			public void onResponse(Call call, final Response response) throws IOException {
				Log.e("HTTP STATUS CODE", Integer.toString(response.code()));
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

	private static Request BuildGetUserRequest(String authId) {
		return new Request.Builder()
			.header("Authorization", "bearer " + authId)
			.header("Content-Type","application/json")
			.url("http://wakeuserapi.azurewebsites.net/v1/users")
			.build();
	}

	private static Request BuildPutUserRequest(UserDto user, String authId) {
		ObjectMapper objectMapper = new ObjectMapper();
		final ObjectWriter w = objectMapper.writer();
		UserBody body = new UserBody(user);
		try {
			byte[] json = w.writeValueAsBytes(body);

			return new Request.Builder()
					.header("Authorization", "bearer " + authId)
					.header("Content-Type","application/json")
					.put(RequestBody.create(MEDIA_TYPE_JSON, json))
					.url("http://wakeuserapi.azurewebsites.net/v1/users")
					.build();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return new Request.Builder().build();
		}
	}

	private static Request BuildPostAlarmsRequest(ObservableArrayList<Alarm> alarms, String authId) {
		List<AlarmDto> alarmDtos = new ArrayList();
		for (Alarm model : alarms) {
			alarmDtos.add(mapAlarm(model));
		}

		AlarmBody body = new AlarmBody(alarmDtos);
		ObjectMapper objectMapper = new ObjectMapper();
		final ObjectWriter w = objectMapper.writer();
		try {
			byte[] json = w.writeValueAsBytes(body);

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

	private void buildOnboardingErrorDialog(Context context, String errorText) {

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		LayoutInflater inflater = LayoutInflater.from(context);
//		View editorView = inflater.inflate(R.layout.label_edit_dialog, null);
//		dialogBuilder.setView(editorView);
		//final EditText input = (EditText) editorView.findViewById(R.id.input);


		dialogBuilder
				.setMessage(errorText)
				.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

		dialogBuilder.create();

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
