package com.lucerlabs.wake;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

	@JsonProperty("userID")
	private int userID;
	@JsonProperty("name")
	private String name;
	@JsonProperty("coreID")
	private String coreID;
	@JsonProperty("timezone")
	private String timezone;
	@JsonProperty("particleToken")
	private String particleToken;
	@JsonProperty("sideOfBed")
	private String sideOfBed;
	@JsonProperty("isPrimaryUser")
	private boolean isPrimaryUser;
	@JsonProperty("hasSecondaryUser")
	private boolean hasSecondaryUser;
	@JsonProperty("isRegistered")
	private boolean isRegistered;
	@JsonProperty("doesReceivePromotions")
	private boolean doesReceivePromotions;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	/**
	 * No args constructor for use in serialization
	 *
	 */
	public UserDto() {
	}

	/**
	 *
	 * @param hasSecondaryUser
	 * @param userID
	 * @param particleToken
	 * @param timezone
	 * @param doesReceivePromotions
	 * @param name
	 * @param isPrimaryUser
	 * @param sideOfBed
	 * @param isRegistered
	 * @param coreID
	 */
	public UserDto(int userID, String name, String coreID, String timezone, String particleToken, String sideOfBed, boolean isPrimaryUser, boolean hasSecondaryUser, boolean isRegistered, boolean doesReceivePromotions) {
		super();
		this.userID = userID;
		this.name = name;
		this.coreID = coreID;
		this.timezone = timezone;
		this.particleToken = particleToken;
		this.sideOfBed = sideOfBed;
		this.isPrimaryUser = isPrimaryUser;
		this.hasSecondaryUser = hasSecondaryUser;
		this.isRegistered = isRegistered;
		this.doesReceivePromotions = doesReceivePromotions;
	}

	@JsonProperty("userID")
	public int getUserID() {
		return userID;
	}

	@JsonProperty("userID")
	public void setUserID(int userID) {
		this.userID = userID;
	}

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("coreID")
	public String getCoreID() {
		return coreID;
	}

	@JsonProperty("coreID")
	public void setCoreID(String coreID) {
		this.coreID = coreID;
	}

	@JsonProperty("timezone")
	public String getTimezone() {
		return timezone;
	}

	@JsonProperty("timezone")
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	@JsonProperty("particleToken")
	public String getParticleToken() {
		return particleToken;
	}

	@JsonProperty("particleToken")
	public void setParticleToken(String particleToken) {
		this.particleToken = particleToken;
	}

	@JsonProperty("sideOfBed")
	public String getSideOfBed() {
		return sideOfBed;
	}

	@JsonProperty("sideOfBed")
	public void setSideOfBed(String sideOfBed) {
		this.sideOfBed = sideOfBed;
	}

	@JsonProperty("isPrimaryUser")
	public boolean isPrimaryUser() {
		return isPrimaryUser;
	}

	@JsonProperty("isPrimaryUser")
	public void setIsPrimaryUser(boolean isPrimaryUser) {
		this.isPrimaryUser = isPrimaryUser;
	}

	@JsonProperty("hasSecondaryUser")
	public boolean hasSecondaryUser() {
		return hasSecondaryUser;
	}

	@JsonProperty("hasSecondaryUser")
	public void setHasSecondaryUser(boolean hasSecondaryUser) {
		this.hasSecondaryUser = hasSecondaryUser;
	}

	@JsonProperty("isRegistered")
	public boolean isRegistered() {
		return isRegistered;
	}

	@JsonProperty("isRegistered")
	public void setIsRegistered(boolean isRegistered) {
		this.isRegistered = isRegistered;
	}

	@JsonProperty("doesReceivePromotions")
	public boolean doesReceivePromotions() {
		return doesReceivePromotions;
	}

	@JsonProperty("doesReceivePromotions")
	public void setDoesReceivePromotions(boolean doesReceivePromotions) {
		this.doesReceivePromotions = doesReceivePromotions;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

}
