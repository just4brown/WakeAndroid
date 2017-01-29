package com.lucerlabs.wake;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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
