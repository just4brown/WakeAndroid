package com.lucerlabs.wake;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserBody {

	@JsonProperty("alarms")
	private UserDto user;

	public UserBody() {
	}

	/**
	 *
	 * @param user
	 */
	public UserBody(UserDto user) {
		this.user = user;
	}

	/**
	 *
	 * @return
	 * The alarms
	 */
	@JsonProperty("user")
	public UserDto getUser() {
		return this.user;
	}
}
