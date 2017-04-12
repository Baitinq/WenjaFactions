package me.joeleoli.hcfactions.utils;

import java.text.DecimalFormat;
import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang.time.FastDateFormat;

public class DateTimeFormats {

	public static TimeZone SERVER_TIME_ZONE = TimeZone.getTimeZone("EST");
	public static ZoneId SERVER_ZONE_ID = SERVER_TIME_ZONE.toZoneId();
	public static FastDateFormat DAY_MTH_HR_MIN_SECS = FastDateFormat.getInstance("dd/MM HH:mm:ss", SERVER_TIME_ZONE, Locale.ENGLISH);
	public static FastDateFormat DAY_MTH_HR_MIN_AMPM = FastDateFormat.getInstance("dd/MM hh:mma", SERVER_TIME_ZONE, Locale.ENGLISH);
	public static FastDateFormat HR_MIN_AMPM = FastDateFormat.getInstance("hh:mma", SERVER_TIME_ZONE, Locale.ENGLISH);
	public static FastDateFormat HR_MIN_AMPM_TIMEZONE = FastDateFormat.getInstance("hh:mma z", SERVER_TIME_ZONE, Locale.ENGLISH);
	public static FastDateFormat KOTH_FORMAT = FastDateFormat.getInstance("m:ss", SERVER_TIME_ZONE, Locale.ENGLISH);

	public static ThreadLocal<DecimalFormat> REMAINING_SECONDS = new ThreadLocal<DecimalFormat>() {
		@Override
		protected DecimalFormat initialValue() {
			return new DecimalFormat("0.#");
		}
	};
	
	public static ThreadLocal<DecimalFormat> REMAINING_SECONDS_TRAILING = new ThreadLocal<DecimalFormat>() {
		@Override
		protected DecimalFormat initialValue() {
			return new DecimalFormat("0.0");
		}
	};
	
}