package com.lucerlabs.wake;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceStatusDto {

	@JsonProperty("status")
	private String status;

	@JsonProperty("batteryVoltage")
	private double batteryVoltage;

	@JsonProperty("powerInVoltage")
	private double powerInVoltage;

	@JsonProperty("isPluggedIn")
	private boolean isPluggedIn;

	@JsonProperty("isCharging")
	private boolean isCharging;

	@JsonProperty("isSleeping")
	private boolean isSleeping;

	@JsonProperty("isReconnecting")
	private boolean isReconnecting;

	@JsonProperty("nextAlarm")
	private String nextAlarm;


	public DeviceStatusDto() {
	}

	/**
	 *
	 * @param status
	 * @param batteryVoltage
	 * @param powerInVoltage
	 * @param isPluggedIn
	 * @param isCharging
	 * @param isSleeping
	 * @param isReconnecting
	 */
	public DeviceStatusDto(
		String status,
		double batteryVoltage,
		double powerInVoltage,
		boolean isPluggedIn,
		boolean isCharging,
		boolean isSleeping,
		boolean isReconnecting,
		String nextAlarm) {
		this.status = status;
		this.batteryVoltage = batteryVoltage;
		this.powerInVoltage = powerInVoltage;
		this.isPluggedIn = isPluggedIn;
		this.isCharging = isCharging;
		this.isSleeping = isSleeping;
		this.isReconnecting = isReconnecting;
		this.nextAlarm = nextAlarm;
	}

	/**
	 *
	 * @return
	 * The status
	 */
	@JsonProperty("status")
	public String getStatus() {
		return this.status;
	}

	@JsonProperty("batteryVoltage")
	public double getBatteryVoltage() {
		return this.batteryVoltage;
	}

	@JsonProperty("powerInVoltage")
	public double getPowerInVoltage() {
		return this.powerInVoltage;
	}

	@JsonProperty("isPluggedIn")
	public boolean IsPluggedIn() {
		return this.isPluggedIn;
	}

	@JsonProperty("isCharging")
	public boolean IsCharging() {
		return this.isCharging;
	}

	@JsonProperty("isSleeping")
	public boolean IsSleeping() {
		return this.isSleeping;
	}

	@JsonProperty("isReconnecting")
	public boolean IsReconnecting() {
		return this.isReconnecting;
	}

	@JsonProperty("nextAlarm")
	public String getNextAlarm() {
		return this.nextAlarm;
	}
}

