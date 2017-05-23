package com.lucerlabs.wake;

import android.content.res.Resources;

public enum WakeStatus {
	OFFLINE,
	ERROR,
	STARTUP,
	IDLE,
	SCANNING,
	ALARMING,
	SNOOZING,
	HIBERNATING,
	RECONNECTING,
	UNKNOWN;

	public String toString() {
		return this.name();
	}

	public static WakeStatus parseWakeStatus(String value) {
		switch (value) {
			case "Offline": return WakeStatus.OFFLINE;
			case "Error": return WakeStatus.ERROR;
			case "Startup": return WakeStatus.STARTUP;
			case "Idle": return WakeStatus.IDLE;
			case "Scanning": return WakeStatus.SCANNING;
			case "Alarming": return WakeStatus.ALARMING;
			case "Snoozing": return WakeStatus.SNOOZING;
			case "Hibernating": return WakeStatus.HIBERNATING;
			case "Reconnecting": return WakeStatus.RECONNECTING;
			default:return WakeStatus.UNKNOWN;
		}
	}

	public static String getWakeStatusMessage(WakeStatus status, Resources resources) {
		switch(status) {
			case OFFLINE: return resources.getString(R.string.offline_wake_device_status_message);
			case ERROR: return resources.getString(R.string.error_wake_device_status_message);
			case STARTUP: return resources.getString(R.string.startup_wake_device_status_message);
			case IDLE: return resources.getString(R.string.idle_wake_device_status_message);
			case SCANNING: return resources.getString(R.string.scanning_wake_device_status_message);
			case ALARMING: return resources.getString(R.string.alarming_wake_device_status_message);
			case SNOOZING: return resources.getString(R.string.snoozing_wake_device_status_message);
			case HIBERNATING: return resources.getString(R.string.hibernating_wake_device_status_message);
			case RECONNECTING: return resources.getString(R.string.reconnecting_wake_device_status_message);
			case UNKNOWN: return resources.getString(R.string.unknownn_wake_device_status_message);
			default: return resources.getString(R.string.unknownn_wake_device_status_message);
		}
	}
}
