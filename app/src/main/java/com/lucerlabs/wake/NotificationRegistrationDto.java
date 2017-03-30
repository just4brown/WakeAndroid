package com.lucerlabs.wake;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationRegistrationDto {

	@JsonProperty("provider")
	private String provider;

	@JsonProperty("deviceID")
	private String deviceId;

	public NotificationRegistrationDto(String provider, String deviceId) {
		this.provider = provider;
		this.deviceId = deviceId;
	}

	@JsonProperty("provider")
	public String getProvider() {
		return this.provider;
	}

	@JsonProperty("deviceID")
	public String getDeviceId() {
		return this.deviceId;
	}
}

