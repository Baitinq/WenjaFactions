package me.joeleoli.hcfactions.utils;

import java.util.concurrent.TimeUnit;

import me.joeleoli.hcfactions.utils.DateTimeFormats;
import org.apache.commons.lang.time.DurationFormatUtils;

public class DurationFormatter {

	private static final long MINUTE = TimeUnit.MINUTES.toMillis(1L);
	private static final long HOUR = TimeUnit.HOURS.toMillis(1L);

	public static String getRemaining(long millis, boolean milliseconds) {
		return getRemaining(millis, milliseconds, true);
	}

	public static String getRemaining(long duration, boolean milliseconds, boolean trail) {
		if (milliseconds && duration < MINUTE) {
			return (trail ? DateTimeFormats.REMAINING_SECONDS_TRAILING : DateTimeFormats.REMAINING_SECONDS).get().format(duration * 0.001) + 's';
		} else {
			return DurationFormatUtils.formatDuration(duration, (duration >= HOUR ? "HH:" : "") + "mm:ss");
		}
	}

}