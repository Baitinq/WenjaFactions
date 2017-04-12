package me.joeleoli.hcfactions.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;

public class ElevatorListener implements Listener {

	@EventHandler
	public void onMinecart(VehicleEnterEvent event) {
		if (!(event.getVehicle() instanceof Minecart) || !(event.getEntered() instanceof Player)) return;

		Player p = (Player) event.getEntered();
		Location l = event.getVehicle().getLocation();
		Location loc = new Location(p.getWorld(), (double) l.getBlockX(), (double) l.getBlockY(), (double) l.getBlockZ());
		Material m = loc.getBlock().getType();

		if (!m.equals(Material.FENCE_GATE) && !m.equals(Material.SIGN_POST)) return;

		event.setCancelled(true);

		if (!p.isSneaking()) return;

		p.teleport(this.teleportSpot(loc, loc.getBlockY(), 254));
	}

	public Location teleportSpot(Location loc, int min, int max) {
		int k = min;

		while (k < max) {
			Material m1 = new Location(loc.getWorld(), (double) loc.getBlockX(), (double) k + 0.5D, (double) loc.getBlockZ()).getBlock().getType();
			Material m2 = new Location(loc.getWorld(), (double) loc.getBlockX(), (double) (k + 1) + 0.5D, (double) loc.getBlockZ()).getBlock().getType();

			if (m1.equals(Material.AIR) && m2.equals(Material.AIR)) {
				return new Location(loc.getWorld(), (double) loc.getBlockX(), (double) k + 0.5D, (double) loc.getBlockZ());
			}

			++k;
		}

		return new Location(loc.getWorld(), (double) loc.getBlockX(), (double) loc.getWorld().getHighestBlockYAt(loc.getBlockX(), loc.getBlockZ()) + 0.5D, (double) loc.getBlockZ());
	}

}