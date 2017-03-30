package com.lucerlabs.wake;

import android.databinding.ObservableArrayList;
import android.os.Handler;
import android.util.Log;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WakeCloudClient {
	private String authIdToken;
	public static final okhttp3.MediaType MEDIA_TYPE_JSON = okhttp3.MediaType.parse("application/json; charset=utf-8");
	private final OkHttpClient httpClient = new OkHttpClient();
	private final Handler UIThreadHandler;

	public WakeCloudClient(String token, Handler handler) {
		this.authIdToken = token;
		this.UIThreadHandler = handler;
	}

	public static abstract class AlarmsResponseTask {
		abstract void executeTask(AlarmBody alarm);
	}

	public static abstract class UserResponseTask {
		abstract void executeTask(UserDto user);
	}

	public static abstract class CreateSecondaryUserResponseTask {
		abstract void executeTask(UserDto code);
	}

	public static abstract class SecondaryUserGenerationResponseTask {
		abstract void executeTask(SecondaryUserCodeDto codeBody);
	}

	public void getAlarmsAsync(final AlarmsResponseTask task) {
		httpClient.newCall(BuildGetAlarmsRequest(this.authIdToken)).enqueue(new Callback() {
			@Override
			public void onResponse(Call call, final Response response) throws IOException {
				Log.e("GET Alarms", Integer.toString(response.code()));
				if (response.isSuccessful()) {
					final String rawJsonData = response.body().string();
					ObjectMapper mapper = new ObjectMapper();
					// TODO: catch exceptions here

					// Deserialize the alarmBody here
					final AlarmBody result = mapper.readValue(rawJsonData, AlarmBody.class);
					UIThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							task.executeTask(result);
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

	public void getUserInfoAsync(final UserResponseTask task) {
		httpClient.newCall(BuildGetUserRequest(this.authIdToken)).enqueue(new Callback() {
			@Override
			public void onResponse(Call call, final Response response) throws IOException {
				Log.e("GET User Info", Integer.toString(response.code()));
				if (response.isSuccessful()) {
					final String rawJsonData = response.body().string();
					ObjectMapper mapper = new ObjectMapper();
					final UserDto user = mapper.readValue(rawJsonData, UserDto.class);

					UIThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							task.executeTask(user);
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

	public void postAlarmsAsync(ObservableArrayList<Alarm> alarms) {
		httpClient.newCall(BuildPostAlarmsRequest(alarms, this.authIdToken)).enqueue(new Callback() {
			@Override
			public void onResponse(Call call, final Response response) throws IOException {
				Log.e("POST Alarms", Integer.toString(response.code()));
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

	public void postNotificationRegistration(final String deviceId) {
		httpClient.newCall(BuildNotificationSubscriptionRequest(deviceId, this.authIdToken)).enqueue(new Callback() {
			@Override
			public void onResponse(Call call, final Response response) throws IOException {
				Log.e("POST ", "Notification Registration " + Integer.toString(response.code()));
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

	public void putUserAsync(UserDto user) {
		httpClient.newCall(BuildPutUserRequest(user, this.authIdToken)).enqueue(new Callback() {
			@Override
			public void onResponse(Call call, final Response response) throws IOException {
				Log.e("Put User Async: ", Integer.toString(response.code()));
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

	public void dismissAlarmsAsync() {
		httpClient.newCall(
			new Request.Builder()
					.header("Authorization", "bearer " + this.authIdToken)
					.header("Content-Type","application/json")
					.post(RequestBody.create(MEDIA_TYPE_JSON, ""))
					.url("http://wakeuserapi.azurewebsites.net/v1/alarms/stop")
					.build())
			.enqueue(new Callback() {
				@Override
				public void onResponse(Call call, final Response response) throws IOException {
					Log.e("Dismiss Alarm", Integer.toString(response.code()));
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

	public void getAlarmDemoAsync() {
		httpClient.newCall(
			new Request.Builder()
					.header("Authorization", "bearer " + this.authIdToken)
					.header("Content-Type","application/json")
					.url("http://wakeuserapi.azurewebsites.net/v1/devices/demo/port")
					.build())
			.enqueue(new Callback() {
				@Override
				public void onResponse(Call call, final Response response) throws IOException {
					Log.e("Alarm Demo", Integer.toString(response.code()));
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

	public void createSecondaryUserAsync(String code, final CreateSecondaryUserResponseTask task) {
		httpClient.newCall(
			new Request.Builder()
					.header("Authorization", "bearer " + this.authIdToken)
					.header("Content-Type","application/json")
					.post(RequestBody.create(MEDIA_TYPE_JSON, ""))
					.url("http://wakeuserapi.azurewebsites.net/v1/users/code/" + code)
					.build())
			.enqueue(new Callback() {
				@Override
				public void onResponse(Call call, final Response response) throws IOException {
					Log.e("Second User Code", Integer.toString(response.code()));
					if (response.isSuccessful()) {
						final String rawJsonData = response.body().string();
						ObjectMapper mapper = new ObjectMapper();
						final UserDto user = mapper.readValue(rawJsonData, UserDto.class);

						UIThreadHandler.post(new Runnable() {
							@Override
							public void run() {
								task.executeTask(user);
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

	public void generateSecondaryUserAsync(final SecondaryUserGenerationResponseTask task) {
		httpClient.newCall(
				new Request.Builder()
						.header("Authorization", "bearer " + this.authIdToken)
						.header("Content-Type","application/json")
						.url("http://wakeuserapi.azurewebsites.net/v1/users/code")
						.build())
				.enqueue(new Callback() {
					@Override
					public void onResponse(Call call, final Response response) throws IOException {
						Log.e("HTTP STATUS CODE", Integer.toString(response.code()));
						if (response.isSuccessful()) {
							final String rawJsonData = response.body().string();
							ObjectMapper mapper = new ObjectMapper();
							final SecondaryUserCodeDto codeBody = mapper.readValue(rawJsonData, SecondaryUserCodeDto.class);

							UIThreadHandler.post(new Runnable() {
								@Override
								public void run() {
									task.executeTask(codeBody);
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

	private static Request BuildNotificationSubscriptionRequest(String deviceId, String authId) {
		NotificationRegistrationDto dto = new NotificationRegistrationDto("android", deviceId);
		ObjectMapper objectMapper = new ObjectMapper();
		final ObjectWriter w = objectMapper.writer();
		try {
			byte[] json = w.writeValueAsBytes(dto);

			return new Request.Builder()
					.header("Authorization", "bearer " + authId)
					.header("Content-Type","application/json")
					.post(RequestBody.create(MEDIA_TYPE_JSON, json))
					.url("http://wakeuserapi.azurewebsites.net/v1/users/register")
					.build();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return new Request.Builder().build();
		}
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
