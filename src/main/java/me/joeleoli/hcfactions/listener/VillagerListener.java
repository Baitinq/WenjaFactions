package me.joeleoli.hcfactions.listener;

import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class VillagerListener implements Listener {

	@EventHandler
	public void onInteract(PlayerInteractEntityEvent event) {
		if (!(event.getRightClicked() instanceof Villager)) return;

		event.setCancelled(true);
	}

}