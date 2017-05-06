package me.joeleoli.hcfactions.listener;

import me.joeleoli.hcfactions.FactionsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class EndListener implements Listener {

	private Location endSpawnLocation = new Location(Bukkit.getWorld("world_the_end"), -47.0D, 59.0D, 76.0D);
	private Location endExitLocation = new Location(Bukkit.getWorld("world"), 0.0D, 78.0D, 200.0D);

	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, FactionsPlugin.getInstance());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onPlayerPortal(PlayerPortalEvent event) {
		if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
			if (event.getPlayer().getWorld().getEnvironment() == World.Environment.THE_END) {
				event.getPlayer().teleport(this.endExitLocation);
			} else if (event.getFrom().getWorld().getEnvironment() == World.Environment.NORMAL) {
				event.getPlayer().teleport(this.endSpawnLocation);
			}
			
			event.setCancelled(true);
		}
	}

}
