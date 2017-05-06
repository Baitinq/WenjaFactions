package me.joeleoli.hcfactions.faction.event;

import me.joeleoli.hcfactions.faction.claim.Claim;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import me.joeleoli.hcfactions.faction.event.cause.ClaimChangeCause;
import me.joeleoli.hcfactions.faction.type.ClaimableFaction;

import java.util.Collection;

/**
 * Faction event called when a {@link Claim} has been claimed by a {@link ClaimableFaction}.
 */
public class FactionClaimChangedEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final CommandSender sender;
	private final ClaimChangeCause cause;
	private final Collection<Claim> affectedClaims;

	public FactionClaimChangedEvent(CommandSender sender, ClaimChangeCause cause, Collection<Claim> affectedClaims) {
		this.sender = sender;
		this.cause = cause;
		this.affectedClaims = affectedClaims;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	/**
	 * Gets the {@link CommandSender} that made this claim.
	 *
	 * @return the claiming {@link CommandSender}, null if none
	 */
	public CommandSender getSender() {
		return sender;
	}

	/**
	 * Gets the {@link ClaimChangeCause} of this event.
	 *
	 * @return the {@link ClaimChangeCause}
	 */
	public ClaimChangeCause getCause() {
		return cause;
	}

	/**
	 * Gets the {@link Claim}s being affected.
	 *
	 * @return the {@link Claim}s being affected
	 */
	public Collection<Claim> getAffectedClaims() {
		return affectedClaims;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
