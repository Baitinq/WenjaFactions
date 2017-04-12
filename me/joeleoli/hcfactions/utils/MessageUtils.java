package me.joeleoli.hcfactions.utils;

import me.joeleoli.hcfactions.FactionsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MessageUtils {

	public static void sendMessageToStaff(String msg) {
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(player.hasPermission("network.staff")) {
				player.sendMessage(msg);
			}
		}
	}

	public static String color(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}

	public static String getFormattedName(Player player) {
		return color(FactionsPlugin.getInstance().getPermissionsService().getPlayerPrefix(player.getUniqueId()) + player.getName());
	}
}
