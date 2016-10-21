package com.lucerlabs.wake;

public enum DayOfWeek {
	SUNDAY(0), MONDAY(1), TUESDAY(2), WEDNESDAY(3), THURSDAY(4), FRIDAY(5), SATURDAY(6);
	private int value;

	public String toString() {
		return this.name();
	}

	public static DayOfWeek getDayOfWeek(int value) {
		switch (value) {
			case 0: return DayOfWeek.SUNDAY;
			case 1: return DayOfWeek.MONDAY;
			case 2: return DayOfWeek.TUESDAY;
			case 3: return DayOfWeek.WEDNESDAY;
			case 4: return DayOfWeek.THURSDAY;
			case 5: return DayOfWeek.FRIDAY;
			case 6: return DayOfWeek.SATURDAY;
			default:return DayOfWeek.SUNDAY;
		}
	}

	private DayOfWeek(int value) {
		this.value = value;
	}
}
