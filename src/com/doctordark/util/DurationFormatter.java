package com.doctordark.util;

import me.joeleoli.hcfactions.utils.DateTimeFormats;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.time.DurationFormatUtils;

public class DurationFormatter {
	
	private static long MINUTE;
	private static long HOUR;

	static {
		MINUTE = TimeUnit.MINUTES.toMillis(1L);
		HOUR = TimeUnit.HOURS.toMillis(1L);
	}

	public static String getRemaining(long millis, boolean milliseconds) {
		return getRemaining(millis, milliseconds, true);
	}

	public static String getRemaining(long duration, boolean milliseconds, boolean trail) {
		if (milliseconds && duration < DurationFormatter.MINUTE) {
			return (trail ? DateTimeFormats.REMAINING_SECONDS_TRAILING : DateTimeFormats.REMAINING_SECONDS).get().format(duration * 0.001) + 's';
		}
		
		return DurationFormatUtils.formatDuration(duration, ((duration >= DurationFormatter.HOUR) ? "HH:" : "") + "mm:ss");
	}
	
}