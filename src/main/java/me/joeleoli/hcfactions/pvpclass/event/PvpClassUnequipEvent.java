package me.joeleoli.hcfactions.pvpclass.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import me.joeleoli.hcfactions.pvpclass.PvpClass;

/**
 * Event called when a player unequips a {@link PvpClass}.
 */
public class PvpClassUnequipEvent extends PlayerEvent {

	private static final HandlerList handlers = new HandlerList();

	private final PvpClass pvpClass;

	public PvpClassUnequipEvent(Player player, PvpClass pvpClass) {
		super(player);
		this.pvpClass = pvpClass;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	/**
	 * Gets the {@link PvpClass} being unequipped.
	 *
	 * @return the unequipped {@link PvpClass}
	 */
	public PvpClass getPvpClass() {
		return pvpClass;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
