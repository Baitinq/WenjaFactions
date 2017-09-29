package me.joeleoli.hcfactions.faction.event;

import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import me.joeleoli.hcfactions.event.CaptureZone;
import me.joeleoli.hcfactions.event.faction.CapturableFaction;

/**
 * Faction event called when a player enters an event capture zone.
 */
public class CaptureZoneEnterEvent extends FactionEvent implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private final CaptureZone captureZone;
	private final Player player;
	private boolean cancelled;

	public CaptureZoneEnterEvent(Player player, CapturableFaction capturableFaction, CaptureZone captureZone) {
		super(capturableFaction);

		Preconditions.checkNotNull(player, "Player cannot be null");
		Preconditions.checkNotNull(captureZone, "Capture zone cannot be null");

		this.captureZone = captureZone;
		this.player = player;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public CapturableFaction getFaction() {
		return (CapturableFaction) super.getFaction();
	}

	/**
	 * Gets the capture zone of the faction the player is entering.
	 *
	 * @return the entered capture zone
	 */
	public CaptureZone getCaptureZone() {
		return captureZone;
	}

	/**
	 * Gets the player entering this capture zone.
	 *
	 * @return the player entering capture zone
	 */
	public Player getPlayer() {
		return player;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
