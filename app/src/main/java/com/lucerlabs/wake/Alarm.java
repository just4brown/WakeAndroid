package com.lucerlabs.wake;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Alarm {
	private Integer hour;
	private Integer minute;
	private Boolean enabled;
	private Integer userID;
	private Boolean isSynchronized;
	private List<DayOfWeek> days;
	private Integer audio;
	private Integer duration;
	private Integer brightness;
	private Integer volume;
	private Boolean allowSnooze;
	private String createdAt;
	private String label;

	public Alarm(
			Integer hour,
			Integer minute,
			Boolean enabled,
			Integer userID,
			Boolean isSynchronized,
			List<Integer> days,
			Integer audio,
			Integer duration,
			Integer brightness,
			Integer volume,
			Boolean allowSnooze,
			String createdAt,
			String label) {
		this.label = label;
		this.hour = hour;
		this.minute = minute;
		this.enabled = enabled;
		this.userID = userID;
		this.isSynchronized = isSynchronized;
		this.audio = audio;
		this.duration = duration;
		this.brightness = brightness;
		this.volume = volume;
		this.allowSnooze = allowSnooze;
		this.createdAt = createdAt;
		setDays(days);
	}

	public Alarm(String label, int hour, int minute, boolean enabled) {
		this.label = label;
		this.hour = hour;
		this.minute = minute;
		this.enabled = enabled;
		this.isSynchronized = false;
		this.days = Arrays.asList(DayOfWeek.getDayOfWeek(1));
		this.audio = 3;
		this.duration = 5;
		this.brightness = 9;
		this.volume = 9;
		this.allowSnooze = false;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int mHour) {
		this.hour = mHour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int mMinute) {
		this.minute = mMinute;
	}

	public Integer getUserID() {
		return userID;
	}

	public Boolean getSynchronized() {
		return isSynchronized;
	}

	public List<Integer> getIntegerDays() {
		List convertedDays = new ArrayList<Integer>();
		for (DayOfWeek d : this.days) {
			convertedDays.add(d.ordinal());
		}
		return convertedDays;
	}

	public List<DayOfWeek> getDays() {
		return this.days;
	}

	public Integer getAudio() {
		return audio;
	}

	public Integer getDuration() {
		return duration;
	}

	public Integer getBrightness() {
		return brightness;
	}

	public Integer getVolume() {
		return volume;
	}

	public Boolean getAllowSnooze() {
		return allowSnooze;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public String getLabel() {
		return label;
	}

	public void setHour(Integer hour) {
		this.hour = hour;
	}

	public void setMinute(Integer minute) {
		this.minute = minute;
	}

	public void setUserID(Integer userID) {
		this.userID = userID;
	}

	public void setSynchronized(Boolean aSynchronized) {
		isSynchronized = aSynchronized;
	}

	public void setDays(List<Integer> days) {
		this.days = new ArrayList<DayOfWeek>();
		for (Integer day : days) {
			this.days.add(DayOfWeek.getDayOfWeek(day));
		}
	}

	public void addDay(DayOfWeek day) {
		if (!this.days.contains(day)) {
			this.days.add(day);
		}
	}

	public void removeDay(DayOfWeek day) {
		this.days.remove(day);
	}

	public void setAudio(Integer audio) {
		this.audio = audio;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public void setBrightness(Integer brightness) {
		this.brightness = brightness;
	}

	public void setVolume(Integer volume) {
		this.volume = volume;
	}

	public void setAllowSnooze(Boolean allowSnooze) {
		this.allowSnooze = allowSnooze;
	}


	public void setLabel(String label) {
		this.label = label;
	}
}
