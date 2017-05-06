package me.joeleoli.hcfactions.event.tracker;

import me.joeleoli.hcfactions.event.CaptureZone;
import me.joeleoli.hcfactions.event.EventTimer;
import org.bukkit.entity.Player;
import me.joeleoli.hcfactions.event.EventType;
import me.joeleoli.hcfactions.event.faction.EventFaction;

/**
 * Tracker for handling event mini-games. NOTE: The methods here are called before they happen, so
 * the onControlLoss method for example would still have its' {@link CaptureZone} player unchanged.
 */
public interface EventTracker {

	EventType getEventType();

	/**
	 * Handles ticking every 5 seconds
	 *
	 * @param eventTimer   the timer
	 * @param eventFaction the faction
	 */
	void tick(EventTimer eventTimer, EventFaction eventFaction);

	void onContest(EventFaction eventFaction, EventTimer eventTimer);

	boolean onControlTake(Player player, CaptureZone captureZone);

	void onControlLoss(Player player, CaptureZone captureZone, EventFaction eventFaction);

	void stopTiming();
}
