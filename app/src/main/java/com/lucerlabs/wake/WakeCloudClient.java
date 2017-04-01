package com.lucerlabs.wake;

import android.databinding.ObservableArrayList;
import android.os.Handler;
import android.util.Log;

import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.result.Delegation;
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
	private String refreshToken;
	public static final okhttp3.MediaType MEDIA_TYPE_JSON = okhttp3.MediaType.parse("application/json; charset=utf-8");
	private final OkHttpClient httpClient = new OkHttpClient();
	private final Handler UIThreadHandler;
	private AuthenticationAPIClient auth0Client;

	public WakeCloudClient(String token, String refreshToken, Handler handler) {
		this.authIdToken = token;
		this.UIThreadHandler = handler;
		this.refreshToken = refreshToken;
	}

	public static abstract class ResponseTask<T> {
		abstract void executeTask(T body);
		abstract void onError(int errorCode);
	}

	private <T> void makeWebRequest(final Request request, final OkHttpClient client, final ResponseTask<T> task, final Handler responseHandler, final Class<T> classOfT) {
		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onResponse(Call call, final Response response) throws IOException {
				final int responseCode = response.code();
				if (responseCode == 200) {
					final String rawJsonData = response.body().string();
					ObjectMapper mapper = new ObjectMapper();
					// TODO: catch exceptions here

					// Deserialize the responseBody here
					final T result = mapper.readValue(rawJsonData, classOfT);
					responseHandler.post(new Runnable() {
						@Override
						public void run() {
							task.executeTask(result);
						}
					});
				} else if (responseCode == 401) {
					auth0Client.delegationWithRefreshToken(refreshToken)
						.start(new BaseCallback<Delegation, AuthenticationException>() {
							@Override
							public void onSuccess(Delegation payload) {
								authIdToken = payload.getIdToken();
								Request newRequest = request.newBuilder().header("Authorization", "bearer " + authIdToken).build();
								client.newCall(newRequest).enqueue(new Callback() {
									@Override
									public void onResponse(Call call, final Response response) throws IOException {
										final int responseCode = response.code();
										if (responseCode == 200) {
											final String rawJsonData = response.body().string();
											ObjectMapper mapper = new ObjectMapper();
											// TODO: catch exceptions here

											// Deserialize the responseBody here
											final T result = mapper.readValue(rawJsonData, classOfT);
											responseHandler.post(new Runnable() {
												@Override
												public void run() {
													task.executeTask(result);
												}
											});
										} else {
											responseHandler.post(new Runnable() {
												@Override
												public void run() {
													task.onError(responseCode);
												}
											});
										}
									}

									@Override
									public void onFailure(Call call, IOException e) {
										e.printStackTrace();
									}
								});
							}

							@Override
							public void onFailure(AuthenticationException error) {
								responseHandler.post(new Runnable() {
									@Override
									public void run() {
										task.onError(responseCode);
									}
								});
							}
						});
				} else {
					responseHandler.post(new Runnable() {
						@Override
						public void run() {
							task.onError(responseCode);
						}
					});
				}
			}

			@Override
			public void onFailure(Call call, IOException e) {
				e.printStackTrace();
			}
		});
	}

	private void makeWebRequest(final Request request, final OkHttpClient client) {
		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onResponse(Call call, final Response response) throws IOException {
				int responseCode = response.code();
				if (responseCode == 401) {
					auth0Client.delegationWithRefreshToken(refreshToken)
						.start(new BaseCallback<Delegation, AuthenticationException>() {
							@Override
							public void onSuccess(Delegation payload) {
								authIdToken = payload.getIdToken();
								Request newRequest = request.newBuilder().header("Authorization", "bearer " + authIdToken).build();
								client.newCall(newRequest).enqueue(new Callback() {
									@Override
									public void onResponse(Call call, final Response response) throws IOException {
										int responseCode = response.code();
										if (responseCode != 200) {
											// TODO: report error
										}
									}

									@Override
									public void onFailure(Call call, IOException e) {
										e.printStackTrace();
									}
								});
							}

							@Override
							public void onFailure(AuthenticationException error) {
							}
						});
				} else {
					// TODO: report error
				}
			}

			@Override
			public void onFailure(Call call, IOException e) {
				e.printStackTrace();
			}
		});
	}

	public void getAlarmsAsync(final ResponseTask task) {
		makeWebRequest(BuildGetAlarmsRequest(this.authIdToken), this.httpClient, task, this.UIThreadHandler, AlarmBody.class);
	}

	public void getUserInfoAsync(final ResponseTask task) {
		makeWebRequest(BuildGetUserRequest(this.authIdToken), this.httpClient, task, this.UIThreadHandler, UserDto.class);
	}

	public void postAlarmsAsync(ObservableArrayList<Alarm> alarms) {
		makeWebRequest(BuildPostAlarmsRequest(alarms, this.authIdToken), this.httpClient);
	}

	public void postNotificationRegistration(final String deviceId) {
		makeWebRequest(BuildNotificationSubscriptionRequest(deviceId, this.authIdToken), this.httpClient);
	}

	public void putUserAsync(UserDto user) {
		makeWebRequest(BuildPutUserRequest(user, this.authIdToken), this.httpClient);
	}

	public void dismissAlarmsAsync() {
		makeWebRequest(
			new Request.Builder()
				.header("Authorization", "bearer " + this.authIdToken)
				.header("Content-Type","application/json")
				.post(RequestBody.create(MEDIA_TYPE_JSON, ""))
				.url("http://wakeuserapi.azurewebsites.net/v1/alarms/stop")
				.build(),
			this.httpClient);
	}

	public void getAlarmDemoAsync() {
		makeWebRequest(
			new Request.Builder()
					.header("Authorization", "bearer " + this.authIdToken)
					.header("Content-Type","application/json")
					.url("http://wakeuserapi.azurewebsites.net/v1/devices/demo/port")
					.build(),
			this.httpClient);
	}

	public void createSecondaryUserAsync(String code, final ResponseTask task) {
		makeWebRequest(
			new Request.Builder()
				.header("Authorization", "bearer " + this.authIdToken)
				.header("Content-Type","application/json")
				.post(RequestBody.create(MEDIA_TYPE_JSON, ""))
				.url("http://wakeuserapi.azurewebsites.net/v1/users/code/" + code)
				.build(),
			this.httpClient,
			task,
			this.UIThreadHandler,
			UserDto.class);
	}

	public void getAudioTracksAsync(final ResponseTask task) {
		makeWebRequest(
			new Request.Builder()
					.header("Authorization", "bearer " + this.authIdToken)
					.header("Content-Type","application/json")
					.url("http://wakeuserapi.azurewebsites.net/v1/alarms/audio")
					.build(),
			this.httpClient,
			task,
			this.UIThreadHandler,
			UserDto.class);
	}

	public void generateSecondaryUserAsync(final ResponseTask task) {
		makeWebRequest(
			new Request.Builder()
				.header("Authorization", "bearer " + this.authIdToken)
				.header("Content-Type","application/json")
				.url("http://wakeuserapi.azurewebsites.net/v1/users/code")
				.build(),
			this.httpClient,
			task,
			this.UIThreadHandler,
				SecondaryUserCodeDto.class);
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
