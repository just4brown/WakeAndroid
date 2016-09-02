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
public class AlarmDto {

	@JsonProperty("userID")
	private Integer userID;
	@JsonProperty("isSynchronized")
	private Boolean isSynchronized;
	@JsonProperty("isActive")
	private Boolean isActive;
	@JsonProperty("hour")
	private Integer hour;
	@JsonProperty("minute")
	private Integer minute;
	@JsonProperty("days")
	private List<String> days = new ArrayList<String>();
	@JsonProperty("audio")
	private Integer audio;
	@JsonProperty("duration")
	private Integer duration;
	@JsonProperty("brightness")
	private Integer brightness;
	@JsonProperty("volume")
	private Integer volume;
	@JsonProperty("allowSnooze")
	private Boolean allowSnooze;
	@JsonProperty("created_at")
	private String createdAt;

	/**
	 * No args constructor for use in serialization
	 *
	 */
	public AlarmDto() {
	}

	/**
	 *
	 * @param brightness
	 * @param minute
	 * @param isActive
	 * @param userID
	 * @param duration
	 * @param audio
	 * @param days
	 * @param createdAt
	 * @param isSynchronized
	 * @param volume
	 * @param allowSnooze
	 * @param hour
	 */
	public AlarmDto(Integer userID, Boolean isSynchronized, Boolean isActive, Integer hour, Integer minute, List<String> days, Integer audio, Integer duration, Integer brightness, Integer volume, Boolean allowSnooze, String createdAt) {
		this.userID = userID;
		this.isSynchronized = isSynchronized;
		this.isActive = isActive;
		this.hour = hour;
		this.minute = minute;
		this.days = days;
		this.audio = audio;
		this.duration = duration;
		this.brightness = brightness;
		this.volume = volume;
		this.allowSnooze = allowSnooze;
		this.createdAt = createdAt;
	}

	/**
	 *
	 * @return
	 * The userID
	 */
	@JsonProperty("userID")
	public Integer getUserID() {
		return userID;
	}

	/**
	 *
	 * @param userID
	 * The userID
	 */
	@JsonProperty("userID")
	public void setUserID(Integer userID) {
		this.userID = userID;
	}

	/**
	 *
	 * @return
	 * The isSynchronized
	 */
	@JsonProperty("isSynchronized")
	public Boolean getIsSynchronized() {
		return isSynchronized;
	}

	/**
	 *
	 * @param isSynchronized
	 * The isSynchronized
	 */
	@JsonProperty("isSynchronized")
	public void setIsSynchronized(Boolean isSynchronized) {
		this.isSynchronized = isSynchronized;
	}

	/**
	 *
	 * @return
	 * The isActive
	 */
	@JsonProperty("isActive")
	public Boolean getIsActive() {
		return isActive;
	}

	/**
	 *
	 * @param isActive
	 * The isActive
	 */
	@JsonProperty("isActive")
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	/**
	 *
	 * @return
	 * The hour
	 */
	@JsonProperty("hour")
	public Integer getHour() {
		return hour;
	}

	/**
	 *
	 * @param hour
	 * The hour
	 */
	@JsonProperty("hour")
	public void setHour(Integer hour) {
		this.hour = hour;
	}

	/**
	 *
	 * @return
	 * The minute
	 */
	@JsonProperty("minute")
	public Integer getMinute() {
		return minute;
	}

	/**
	 *
	 * @param minute
	 * The minute
	 */
	@JsonProperty("minute")
	public void setMinute(Integer minute) {
		this.minute = minute;
	}

	/**
	 *
	 * @return
	 * The days
	 */
	@JsonProperty("days")
	public List<String> getDays() {
		return days;
	}

	/**
	 *
	 * @param days
	 * The days
	 */
	@JsonProperty("days")
	public void setDays(List<String> days) {
		this.days = days;
	}

	/**
	 *
	 * @return
	 * The audio
	 */
	@JsonProperty("audio")
	public Integer getAudio() {
		return audio;
	}

	/**
	 *
	 * @param audio
	 * The audio
	 */
	@JsonProperty("audio")
	public void setAudio(Integer audio) {
		this.audio = audio;
	}

	/**
	 *
	 * @return
	 * The duration
	 */
	@JsonProperty("duration")
	public Integer getDuration() {
		return duration;
	}

	/**
	 *
	 * @param duration
	 * The duration
	 */
	@JsonProperty("duration")
	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	/**
	 *
	 * @return
	 * The brightness
	 */
	@JsonProperty("brightness")
	public Integer getBrightness() {
		return brightness;
	}

	/**
	 *
	 * @param brightness
	 * The brightness
	 */
	@JsonProperty("brightness")
	public void setBrightness(Integer brightness) {
		this.brightness = brightness;
	}

	/**
	 *
	 * @return
	 * The volume
	 */
	@JsonProperty("volume")
	public Integer getVolume() {
		return volume;
	}

	/**
	 *
	 * @param volume
	 * The volume
	 */
	@JsonProperty("volume")
	public void setVolume(Integer volume) {
		this.volume = volume;
	}

	/**
	 *
	 * @return
	 * The allowSnooze
	 */
	@JsonProperty("allowSnooze")
	public Boolean getAllowSnooze() {
		return allowSnooze;
	}

	/**
	 *
	 * @param allowSnooze
	 * The allowSnooze
	 */
	@JsonProperty("allowSnooze")
	public void setAllowSnooze(Boolean allowSnooze) {
		this.allowSnooze = allowSnooze;
	}

	/**
	 *
	 * @return
	 * The createdAt
	 */
	@JsonProperty("created_at")
	public String getCreatedAt() {
		return createdAt;
	}

	/**
	 *
	 * @param createdAt
	 * The created_at
	 */
	@JsonProperty("created_at")
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

}
