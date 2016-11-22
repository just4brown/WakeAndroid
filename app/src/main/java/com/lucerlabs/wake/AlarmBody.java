package com.lucerlabs.wake;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlarmBody {

	@JsonProperty("alarms")
	private List<AlarmDto> alarms;

	public AlarmBody() {
	}

	/**
	 *
	 * @param alarms
	 */
	public AlarmBody(List<AlarmDto> alarms) {
		this.alarms = alarms;
	}

	/**
	 *
	 * @return
	 * The alarms
	 */
	@JsonProperty("alarms")
	public List<AlarmDto> getAlarms() {
		return this.alarms;
	}
}
