package me.joeleoli.hcfactions.command;

import me.joeleoli.hcfactions.FactionsPlugin;
import me.joeleoli.hcfactions.deathban.DeathBan;
import me.joeleoli.hcfactions.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class DeathInfoCommand implements CommandExecutor {

	public String getUsage(String label) {
		return String.valueOf('/') + label + ' ' + " <playerName>";
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player) sender;

		if(args.length == 0) {
			player.sendMessage(ChatColor.RED + "You didn't specify a player.");
			player.sendMessage(ChatColor.RED + "Usage: /deathinfo <playerName>");
			return true;
		}

		OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

		if(target == null) {
			player.sendMessage(ChatColor.RED + "That player could not be found.");
			return true;
		}

		if(!FactionsPlugin.getInstance().getDeathBanManager().getDeathBans().containsKey(target.getUniqueId())) {
			player.sendMessage(ChatColor.RED + "That player is not currently death-banned.");
			return true;
		}

		DeathBan deathBan = FactionsPlugin.getInstance().getDeathBanManager().getDeathBans().get(target.getUniqueId());

		player.sendMessage(ChatColor.GRAY + " * " + ChatColor.GOLD + "DeathInfo of " + ChatColor.AQUA + target.getName());
		player.sendMessage(ChatColor.GRAY + "Location: " + ChatColor.GOLD + ("x: " + Math.round(deathBan.getLocation().getX()) + ", y: " + Math.round(deathBan.getLocation().getY()) + ", z: " + Math.round(deathBan.getLocation().getZ()) + ")"));
		player.sendMessage(ChatColor.GRAY + "Occured: " + ChatColor.GOLD + TimeUtils.getDurationBreakdown(System.currentTimeMillis() - deathBan.getTimestamp().getTime()) + " ago");
		player.sendMessage(ChatColor.GRAY + "Expiration: " + ChatColor.GOLD + TimeUtils.getDurationBreakdown(Math.abs(System.currentTimeMillis() - deathBan.getTimestamp().getTime())));

		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return Collections.emptyList();
	}

}