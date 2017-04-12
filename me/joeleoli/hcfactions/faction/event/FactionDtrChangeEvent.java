package me.joeleoli.hcfactions.faction.event;

import me.joeleoli.hcfactions.faction.struct.Raidable;
import me.joeleoli.hcfactions.faction.type.Faction;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when the DTR of a {@link Faction} is changed.
 */
public class FactionDtrChangeEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private final DtrUpdateCause cause;
	private final Raidable raidable;
	private final double originalDtr;
	private boolean cancelled;
	private double newDtr;

	public FactionDtrChangeEvent(DtrUpdateCause cause, Raidable raidable, double originalDtr, double newDtr) {
		this.cause = cause;
		this.raidable = raidable;
		this.originalDtr = originalDtr;
		this.newDtr = newDtr;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public DtrUpdateCause getCause() {
		return cause;
	}

	public Raidable getRaidable() {
		return raidable;
	}

	public double getOriginalDtr() {
		return originalDtr;
	}

	public double getNewDtr() {
		return newDtr;
	}

	public void setNewDtr(double newDtr) {
		this.newDtr = newDtr;
	}

	@Override
	public boolean isCancelled() {
		return cancelled || (Math.abs(newDtr - originalDtr) == 0);
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public enum DtrUpdateCause {
		REGENERATION, MEMBER_DEATH
	}
}
