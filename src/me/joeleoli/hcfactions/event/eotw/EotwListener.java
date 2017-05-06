package me.joeleoli.hcfactions.event.eotw;

import me.joeleoli.hcfactions.FactionsPlugin;
import me.joeleoli.hcfactions.faction.event.FactionClaimChangeEvent;
import me.joeleoli.hcfactions.faction.event.FactionCreateEvent;
import me.joeleoli.hcfactions.faction.event.cause.ClaimChangeCause;
import me.joeleoli.hcfactions.faction.type.Faction;
import me.joeleoli.hcfactions.faction.type.PlayerFaction;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listener used to handle events for if EOTW is active.
 */
public class EotwListener implements Listener {

	private final FactionsPlugin plugin;

	public EotwListener(FactionsPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		EotwHandler.EotwRunnable runnable = plugin.getEotwHandler().getRunnable();
		if (runnable != null) runnable.handleDisconnect(event.getPlayer());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerKick(PlayerKickEvent event) {
		EotwHandler.EotwRunnable runnable = plugin.getEotwHandler().getRunnable();
		if (runnable != null) runnable.handleDisconnect(event.getPlayer());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent event) {
		EotwHandler.EotwRunnable runnable = plugin.getEotwHandler().getRunnable();
		if (runnable != null) runnable.handleDisconnect(event.getEntity());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onFactionCreate(FactionCreateEvent event) {
		if (plugin.getEotwHandler().isEndOfTheWorld()) {
			Faction faction = event.getFaction();

			if (faction instanceof PlayerFaction) {
				event.setCancelled(true); event.getSender().sendMessage(ChatColor.RED + "Player based factions cannot be created during EOTW.");
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onFactionClaimChange(FactionClaimChangeEvent event) {
		if (plugin.getEotwHandler().isEndOfTheWorld() && event.getCause() == ClaimChangeCause.CLAIM) {
			Faction faction = event.getClaimableFaction();
			if (faction instanceof PlayerFaction) {
				event.setCancelled(true);
				event.getSender().sendMessage(ChatColor.RED + "Player based faction land cannot be claimed during EOTW.");
			}
		}
	}
}
