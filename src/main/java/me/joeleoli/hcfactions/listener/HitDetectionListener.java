package me.joeleoli.hcfactions.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import me.joeleoli.hcfactions.FactionsPlugin;

public class HitDetectionListener implements Listener {

	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, FactionsPlugin.getInstance());

		for (Player player : Bukkit.getOnlinePlayers()) {
			player.setMaximumNoDamageTicks(19);
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		event.getPlayer().setMaximumNoDamageTicks(19);
	}

}