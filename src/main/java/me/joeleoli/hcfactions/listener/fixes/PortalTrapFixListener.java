package me.joeleoli.hcfactions.listener.fixes;

import com.doctordark.util.Color;
import me.joeleoli.hcfactions.FactionsPlugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Listener that prevents {@link Player}s from being trapped in portals.
 */
public class PortalTrapFixListener implements Listener {

	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		if (e.getClickedBlock() == null) return;
		if (FactionsPlugin.getInstance().getFactionManager().getFactionAt(e.getClickedBlock().getLocation()).isSafezone()) return;
		if (e.getClickedBlock().getType() == Material.PORTAL) {
			e.getClickedBlock().setType(Material.AIR);
			e.getPlayer().sendMessage(Color.translate("&eYou have &cdisabled &ethis portal&e."));
		}
	}
	
}