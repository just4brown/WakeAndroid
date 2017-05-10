package com.lucerlabs.wake;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.databinding.ObservableArrayList;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.support.v4.widget.DrawerLayout;
import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.result.Credentials;
import com.auth0.android.result.UserProfile;

import java.util.Set;
import java.util.TimeZone;

import io.particle.android.sdk.devicesetup.ParticleDeviceSetupLibrary;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.games.event.Events;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.iid.FirebaseInstanceId;
import com.microsoft.windowsazure.messaging.NotificationHub;
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
	private String authIdToken;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private ListView mDrawerList;
	private FloatingActionButton addAlarmButton;
	private UserDto mCurrentUser;
	private AlertDialog mErrorDialog;
	private AlertDialog mConfirmationDialog;
	private boolean mIsOnboarding;
	private SharedPreferences mSharedPreferences;
	private WakeCloudClient wakeCloud;
	private MediaPlayer mPlayer;
	private FirebaseAnalytics mFirebaseAnalytics;
	private BroadcastReceiver mNotificationReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String category = intent.getStringExtra("category");
			if (category != null) {
				if (category.contentEquals("BACKUP_ALARM")) {
					startPlayer();
				}
			}
		}
	};

	// Notification stuff
	public static MainActivity mainActivity;
	public static Boolean isVisible = false;
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	private final static float MAX_VOLUME = 1.0f;

	private AuthenticationAPIClient client;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Thread.UncaughtExceptionHandler defaultAppExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, Throwable throwable) {
				String userId = "user not set";
				if (mCurrentUser != null) {
					userId = Integer.toString(mCurrentUser.getUserID());
				}
				FirebaseCrash.log("UserId: " + userId);
				FirebaseCrash.report(throwable);
				if (defaultAppExceptionHandler != null) {
					defaultAppExceptionHandler.uncaughtException(thread, throwable);
				} else {
					System.exit(2);
				}

			}
		});

		mFirebaseAnalytics = mFirebaseAnalytics.getInstance(this);

		Credentials credentials = CredentialsManager.getCredentials(this);
		if (credentials == null) {
			doSignOut();
		}
		authIdToken = credentials.getIdToken();
		if (authIdToken == null) {
			doSignOut();
		}

		Auth0 auth0 = new Auth0(getString(R.string.auth0_client_id), getString(R.string.auth0_domain));
		client = new AuthenticationAPIClient(auth0);
		wakeCloud = new WakeCloudClient(this.authIdToken, credentials.getRefreshToken(), new Handler(Looper.getMainLooper()), client);
		setContentView(R.layout.activity_main);

		mainActivity = this;
		NotificationsManager.handleNotifications(this, NotificationSettings.SenderId, WakeNotificationHandler.class);

		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

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

		mErrorDialog = new AlertDialog.Builder(this).create();
		mConfirmationDialog = buildConfirmationDialog(this);
		mAlarms = new ObservableArrayList<Alarm>();
		mIsOnboarding = false;

		ParticleDeviceSetupLibrary.init(this.getApplicationContext());
		mPlayer = new MediaPlayer();
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		isVisible = true;
		if (mCurrentUser == null) {
			getAuth0UserAysnc();
			getUserInfoAsync();
		} else {
			registerWithNotificationHubs();
			checkDeviceStatusAsync();
		}
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
		registerReceiver(mNotificationReceiver, new IntentFilter("alarmStatus"));
	}

	@Override
	protected void onStop() {
		super.onStop();
		isVisible = false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mPlayer.isPlaying()) {
			mPlayer.stop();
			mPlayer.release();
			mPlayer = null;
		}
		unregisterReceiver(mNotificationReceiver);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		this.setIntent(intent);
		String deviceId = intent.getStringExtra("configuredDeviceId");

		if (deviceId != null && deviceId.length() > 0) {
			postDeviceId(deviceId);
		}

		String notificationCategory = intent.getStringExtra("ALARM");
		if (notificationCategory != null) {
			if (notificationCategory.contentEquals("dismiss")) {
				this.dismissAlarms();
			}
		}
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
		String FCM_token = FirebaseInstanceId.getInstance().getToken();
		postNotificationRegistration(FCM_token);
	}

	@Override
	public ObservableArrayList<Alarm> getObservableAlarms() {
		return mAlarms;
	}

	@Override
	public void setNewFragment(Fragment fragment, boolean showBackButton, String tag) {
		mDrawerToggle.setDrawerIndicatorEnabled(false);
		FragmentTransaction newFragmentTransaction = getFragmentManager().beginTransaction().replace(R.id.frame_content, fragment, tag);
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

	public void postDeviceId(String deviceId) {
		if (deviceId == null || deviceId.length() == 0) {
			Log.e("ERROR", " emty deviceId");
		}

		mCurrentUser.setCoreID(deviceId);
		Log.e("Particle Device Id", deviceId);
		if (!mIsOnboarding) {
			putUserAsync();
		}
	}

	@Override
	public void postSideOfBedPreference(String sideOfBed) {
		String sideOfBedValue = "";
		if (sideOfBed.contentEquals("Left side")) {
			sideOfBedValue = "P";
		} else if (sideOfBed.contentEquals("Right side")) {
			sideOfBedValue = "S";
		} else if (sideOfBed.contentEquals("I have the bed to myself")) {
			sideOfBedValue = "N";
		}

		if (!sideOfBedValue.isEmpty()) {
			mCurrentUser.setSideOfBed(sideOfBedValue);
			if (!mIsOnboarding) {
				putUserAsync();
			}
		}
	}

	@Override
	public void postTimezone(String timezone) {
		mCurrentUser.setTimezone(timezone);
		if (!mIsOnboarding) {
			putUserAsync();
		}
	}

	@Override
	public void finishOnboarding() {
		if (mCurrentUser.getCoreID() == null || mCurrentUser.getCoreID().isEmpty()) {
			mErrorDialog.setTitle("Error");
			mErrorDialog.setMessage(getResources().getString(R.string.error_no_core_id));
			mErrorDialog.show();
			return;
		}

		if (mCurrentUser.getSideOfBed() == null || mCurrentUser.getSideOfBed().isEmpty()){
			mErrorDialog.setTitle("Error");
			mErrorDialog.setMessage(getResources().getString(R.string.error_no_side_of_bed));
			mErrorDialog.show();
			return;
		}

		if (mCurrentUser.getTimezone() == null || mCurrentUser.getTimezone().isEmpty()){
			mCurrentUser.setTimezone(TimeZone.getDefault().getID());
		}

		Bundle params = new Bundle();
		params.putString("coreId", mCurrentUser.getCoreID());
		params.putString("userId", Integer.toString((mCurrentUser.getUserID())));
		mFirebaseAnalytics.logEvent("PrimaryUserRegistration", params);

		mCurrentUser.setIsRegistered(true);
		mCurrentUser.setIsPrimaryUser(true);

		putUserAsync();
		mConfirmationDialog.show();
	}

	@Override
	public void submitSecondaryUserCode(String code) {
		if (code == null || code.isEmpty()) {
			mErrorDialog.setTitle("Error");
			mErrorDialog.setMessage(getResources().getString(R.string.error_no_code));
			mErrorDialog.show();
			return;
		}

		createSecondaryUserAsync(code);
	}

	@Override
	public void goBack() {
		onBackPressed();
	}

	@Override
	public void selectPrimaryUserOnboarding() {
		PrimaryOnboardingFragment primaryOnboarding = new PrimaryOnboardingFragment();
		primaryOnboarding.setFragmentListener(mainActivity);
		primaryOnboarding.setPreferenceListener(mainActivity);
		setNewFragment(primaryOnboarding, true, "primaryOnboarding");
	}

	@Override
	public void selectSecondaryUserOnboarding() {
		SecondaryOnboardingFragment secondaryOnboarding = new SecondaryOnboardingFragment();
		secondaryOnboarding.setFragmentListener(mainActivity);
		setNewFragment(secondaryOnboarding, true, "secondaryOnboarding");
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
			SettingsFragment settingsFragment = new SettingsFragment();
			settingsFragment.setPreferenceListener(this);
			getFragmentManager().beginTransaction().replace(R.id.frame_content, settingsFragment).commit();
		} else if (id == R.id.nav_alarms) {
			addAlarmButton.setVisibility(View.VISIBLE);
			AlarmsFragment alarmsFragment = new AlarmsFragment();
			alarmsFragment.setFragmentListener(this);
			getFragmentManager().beginTransaction().replace(R.id.frame_content, alarmsFragment).commit();
		}else if (id == R.id.nav_sign_out) {
			addAlarmButton.setVisibility(View.INVISIBLE);
			doSignOut();
		}

		mDrawerLayout.closeDrawer(GravityCompat.START);
		return true;
	}

	@Override
	public void postAlarms() {
		postAlarmsAsync();
	}

	@Override
	public void dismissAlarms() {
		this.dismissAlarmsAsync();
		if (mPlayer.isPlaying()) {
			mPlayer.stop();
		}
	}

	@Override
	public void runDemo() {
		getAlarmDemoAsync();
	}

	@Override
	public UserDto getCurrentUser() {
		return mCurrentUser;
	}

	@Override
	public void generateSecondaryUserCode() {
		generateSecondaryUserAsync();
	}

	private void getAlarmsAsync() {
		wakeCloud.getAlarmsAsync(new WakeCloudClient.ResponseTask<AlarmBody>() {
			@Override
			public void executeTask(AlarmBody alarm) {
				mAlarms.clear();
				for (AlarmDto alarmDto : alarm.getAlarms()) {
					mAlarms.add(mapAlarm(alarmDto));
				}
			}

			@Override
			void onError(int code) {
				FirebaseCrash.log("getAlarms error: " + code);
			}
		});
	}

	private void getAudioTracksAsync() {
		wakeCloud.getAudioTracksAsync(new WakeCloudClient.ResponseTask<AudioTracksBody>() {
			@Override
			public void executeTask(AudioTracksBody body) {
				// TODO update UI with tracks
			}

			@Override
			void onError(int code) {
				FirebaseCrash.log("getAudioTracks error: " + code);
			}
		});
	}

	public void getUserInfoAsync() {
		wakeCloud.getUserInfoAsync(new WakeCloudClient.ResponseTask<UserDto>() {
			@Override
			void executeTask(UserDto user) {
				mCurrentUser = user;

				if (user.isRegistered()) {
					getAlarmsAsync();
					registerWithNotificationHubs();
					initAlarmFragment();
				}
				else {
					// init onboarding
					mIsOnboarding = true;
					getSupportActionBar().setTitle("Setup");
					OnboardingFragment onboardingFragment = new OnboardingFragment();
					onboardingFragment.setOnboardingFragmentListener(mainActivity);
					setNewFragment(onboardingFragment, false, "baseOnboarding");
					mDrawerToggle.setDrawerIndicatorEnabled(false);
				}
			}

			@Override
			void onError(int code) {
				FirebaseCrash.log("getUser error: " + code);
			}
		});
	}

	public void getAuth0UserAysnc(){
		client.userInfo(CredentialsManager.getCredentials(this).getAccessToken())
				.start(new BaseCallback<UserProfile, AuthenticationException>() {
					@Override
					public void onSuccess(final UserProfile payload) {

						final UserProfile profile = payload;
						String userString = profile.getExtraInfo().get("user").toString();

						MainActivity.this.runOnUiThread(new Runnable() {
							public void run() {
								NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
								TextView mTextView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_name);
								mTextView.setText(profile.getName());
								mTextView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_email);
								mTextView.setText(profile.getEmail());
							}
						});
					}

					@Override
					public void onFailure(AuthenticationException error) {
						MainActivity.this.runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(MainActivity.this, "Session Expired, please Log In", Toast.LENGTH_SHORT).show();
							}
						});
						CredentialsManager.deleteCredentials(getApplicationContext());

					}
				});
	}

	public void postAlarmsAsync() {
		wakeCloud.postAlarmsAsync(this.mAlarms);
		System.out.println("Alarm 0 Audio " + this.mAlarms.get(0).getAudio());
	}

	public void postNotificationRegistration(final String deviceId) {
		wakeCloud.postNotificationRegistration(deviceId);
	}

	public void putUserAsync() {
		wakeCloud.putUserAsync(this.mCurrentUser);
	}

	public void dismissAlarmsAsync() {
		wakeCloud.dismissAlarmsAsync();
	}

	public void getAlarmDemoAsync() {
		wakeCloud.getAlarmDemoAsync();
	}

	public void createSecondaryUserAsync(String code) {
		wakeCloud.createSecondaryUserAsync(code, new WakeCloudClient.ResponseTask<UserDto>() {
			@Override
			public void executeTask(UserDto user) {
				mCurrentUser = user;
				mConfirmationDialog.show();
			}

			@Override
			void onError(int code) {
				FirebaseCrash.log("createSecondaryUser error: " + code);
			}
		});
	}

	public void generateSecondaryUserAsync() {
		wakeCloud.generateSecondaryUserAsync(new WakeCloudClient.ResponseTask<SecondaryUserCodeDto>() {
			@Override
			public void executeTask(SecondaryUserCodeDto codeBody) {
				mErrorDialog.setTitle("Secondary User Code");
				mErrorDialog.setMessage(codeBody.getCode());
				mErrorDialog.show();
			}

			@Override
			void onError(int code) {
				FirebaseCrash.log("generateSecondaryUserCode error: " + code);
			}
		});
	}

	public void checkDeviceStatusAsync() {
		wakeCloud.getDeviceStatusAsync(new WakeCloudClient.ResponseTask<DeviceStatusDto>() {
			@Override
			public void executeTask(DeviceStatusDto deviceStatus) {
				TextView statusTextView = (TextView ) findViewById(R.id.device_status_text);
				statusTextView.setText("Wake Device status: " + deviceStatus.getStatus());
			}

			@Override
			void onError(int code) {
				FirebaseCrash.log("checkDeviceStatus error: " + code);
			}
		});
	}

	@Override
	public void doSignOut() {
		CredentialsManager.deleteCredentials(getApplicationContext());
		mCurrentUser = null;
		authIdToken = null;
		startActivity(new Intent(this, LoginActivity.class));
		finish();
	}

	private void initAlarmFragment() {
		mIsOnboarding = false;
		mDrawerToggle.setDrawerIndicatorEnabled(false);
		AlarmsFragment alarmsFragment = new AlarmsFragment();
		alarmsFragment.setFragmentListener(this);
		FragmentTransaction newFragmentTransaction = getFragmentManager().beginTransaction().replace(R.id.frame_content, alarmsFragment);
		addAlarmButton.setVisibility(View.VISIBLE);
		getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		mDrawerToggle.setDrawerIndicatorEnabled(true);
		mDrawerToggle.setHomeAsUpIndicator(0);
		getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
		newFragmentTransaction.commit();
	}

	private AlertDialog buildConfirmationDialog(Context context) {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

		return dialogBuilder
			.setTitle(getResources().getString(R.string.more_info_section_terms))
			.setMessage(getResources().getString(R.string.terms_confirmation_statement))
			.setPositiveButton("Agree",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						initAlarmFragment();
						dialog.cancel();
					}
				})
			.setNegativeButton("Disagree",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						doSignOut();
						dialog.cancel();
					}
				})
			.create();
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

	private void startPlayer() {
		try {
			// Player setup is here
			String ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString();
			mPlayer.setDataSource(this, Uri.parse(ringtone));
			mPlayer.setLooping(true);
			mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
			mPlayer.setVolume(MAX_VOLUME, MAX_VOLUME);
			mPlayer.prepare();
			mPlayer.start();

		} catch (Exception e) {
			if (mPlayer.isPlaying()) {
				mPlayer.stop();
			}
		}
	}
}
