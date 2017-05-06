package me.joeleoli.hcfactions.command;

import me.joeleoli.hcfactions.deathban.DeathBan;
import me.joeleoli.hcfactions.FactionsPlugin;
import me.joeleoli.hcfactions.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class StaffReviveCommand implements CommandExecutor, TabCompleter {

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 1) {
			sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
			return true;
		}

		OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

		if (!target.hasPlayedBefore() && !target.isOnline()) {
			sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' has not played before, therefore they're not death-banned.");
			return true;
		}

		UUID targetUUID = target.getUniqueId();

		if(FactionsPlugin.getInstance().getDeathBanManager().getDeathBans().containsKey(targetUUID)) {
			DeathBan deathBan = FactionsPlugin.getInstance().getDeathBanManager().getDeathBans().get(targetUUID);
			FactionsPlugin.getInstance().getDeathBanManager().removeDeathBan(targetUUID);
			FactionsPlugin.getInstance().getLogManager().formatMessage("Staff revived player: " + target.getName(), sender.getName());
			MessageUtils.sendMessageToStaff(ChatColor.GRAY + "[" + ChatColor.RED + "!" + ChatColor.GRAY + "] " + ChatColor.YELLOW + deathBan.getPlayer().getName() + ChatColor.LIGHT_PURPLE + " has been revived by " + ChatColor.YELLOW + sender.getName());
		} else {
			sender.sendMessage(ChatColor.RED + target.getName() + " is not currently death-banned.");
		}

		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return Collections.emptyList();
	}

}