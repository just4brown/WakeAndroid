package com.lucerlabs.wake;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SecondaryUserCodeDto {

	@JsonProperty("code")
	private String code;

	public SecondaryUserCodeDto() {
	}

	/**
	 *
	 * @param code
	 */
	public SecondaryUserCodeDto(String code) {
		this.code = code;
	}

	/**
	 *
	 * @return
	 * The alarms
	 */
	@JsonProperty("alarms")
	public String getCode() {
		return this.code;
	}
}

