package me.joeleoli.hcfactions.command;

import me.joeleoli.hcfactions.FactionsPlugin;
import me.joeleoli.hcfactions.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class ClaimCommand implements Listener, CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "Only players can execute this command.");
			return true;
		}

		Player player = (Player) sender;
		PlayerData data = FactionsPlugin.getInstance().getPlayerManager().getPlayerData(player);

		if(data == null) {
			player.sendMessage(ChatColor.RED + "Failed to retrieve your reclaim items.");
			return true;
		}

		if(data.isReclaimed()) {
			player.sendMessage(ChatColor.RED + "You have already reclaimed. If this is an error, please join our teamspeak, ts.wenjapvp.net.");
			return true;
		}

		String group = FactionsPlugin.getInstance().getPermissionsService().getPlayerPrimaryGroup(player.getUniqueId());

		switch(group.toLowerCase()) {
			case "premium":
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "bc &6" + sender.getName() + " &7has reclaimed their &6Premium &7rank.");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lives add " + sender.getName() + " 4");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "crate givekey " + sender.getName() + " Tier1 2");
				break;
			case "vip":
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "bc &6" + sender.getName() + " &7has reclaimed their &6VIP &7rank.");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lives add " + sender.getName() + " 8");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "crate givekey " + sender.getName() + " Tier1 3");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "crate givekey " + sender.getName() + " Tier2 1");
				break;
			case "mvp":
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "bc &6" + sender.getName() + " &7has reclaimed their &6MVP &7rank.");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lives add " + sender.getName() + " 12");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "crate givekey " + sender.getName() + " Tier2 2");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "crate givekey " + sender.getName() + " Tier3 1");
				break;
			case "pro":
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "bc &6" + sender.getName() + " &7has reclaimed their &6Pro &7rank.");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lives add " + sender.getName() + " 20");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "crate givekey " + sender.getName() + " Tier2 3");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "crate givekey " + sender.getName() + " Tier3 2");
				break;
			case "elite":
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "bc &6" + sender.getName() + " &7has reclaimed their &6Elite &7rank.");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lives add " + sender.getName() + " 40");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "crate givekey " + sender.getName() + " Tier2 4");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "crate givekey " + sender.getName() + " Tier3 3");
				break;
			case "owner":
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "bc &6" + sender.getName() + " &7has reclaimed their &6Owner &7rank.");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lives add " + sender.getName() + " 2222");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "crate givekey " + sender.getName() + " Tier2 64");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "crate givekey " + sender.getName() + " Tier3 64");
				break;
			case "developer":
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "bc &6" + sender.getName() + " &7has reclaimed their &6Developer &7rank.");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lives add " + sender.getName() + " 2222");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "crate givekey " + sender.getName() + " Tier2 64");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "crate givekey " + sender.getName() + " Tier3 64");
				break;
			default:
				player.sendMessage(ChatColor.RED + "You do not have anything to reclaim...");
				return true;
		}

		data.setReclaimed(true);
		data.save();

		return true;
	}

}