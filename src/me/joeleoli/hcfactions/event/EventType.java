package me.joeleoli.hcfactions.event;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import me.joeleoli.hcfactions.FactionsPlugin;
import me.joeleoli.hcfactions.event.tracker.EventTracker;
import me.joeleoli.hcfactions.event.tracker.KothTracker;
import me.joeleoli.hcfactions.event.tracker.ConquestTracker;

public enum EventType {
	CONQUEST("Conquest", new ConquestTracker(FactionsPlugin.getInstance())), KOTH("KOTH", new KothTracker(FactionsPlugin.getInstance()));

	private static final ImmutableMap<String, EventType> byDisplayName;

	static {
		ImmutableMap.Builder<String, EventType> builder = new ImmutableBiMap.Builder<>();
		for (EventType eventType : values()) {
			builder.put(eventType.displayName.toLowerCase(), eventType);
		}

		byDisplayName = builder.build();
	}

	private final EventTracker eventTracker;
	private final String displayName;

	EventType(String displayName, EventTracker eventTracker) {
		this.displayName = displayName;
		this.eventTracker = eventTracker;
	}

	@Deprecated
	public static EventType getByDisplayName(String name) {
		return byDisplayName.get(name.toLowerCase());
	}

	public EventTracker getEventTracker() {
		return eventTracker;
	}

	public String getDisplayName() {
		return displayName;
	}
}
