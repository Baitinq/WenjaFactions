package me.joeleoli.hcfactions.command;

import me.joeleoli.hcfactions.FactionsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OresCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = (Player) sender;

		if ((cmd.getName().equalsIgnoreCase("ores")) && ((sender instanceof Player)) && (args.length > 1)) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "Usage: /ores <player>");
			return true;
		}

		if (args.length == 0) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "Usage: /ores <player>");
			return true;
		}

		Player target = Bukkit.getServer().getPlayer(args[0]);

		if ((args.length == 1) && (target == null)) {
			player.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "That player is not online.");
			return true;
		}

		sender.sendMessage(ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + "--------------------------------------------------");
		sender.sendMessage(ChatColor.GRAY + "Ores mined by: " + target.getDisplayName());
		sender.sendMessage(ChatColor.AQUA + "Diamond Ore: " + ChatColor.RESET + target.getStatistic(Statistic.MINE_BLOCK, Material.DIAMOND_ORE));
		sender.sendMessage(ChatColor.GREEN + "Emerald Ore: " + ChatColor.RESET + target.getStatistic(Statistic.MINE_BLOCK, Material.EMERALD_ORE));
		sender.sendMessage(ChatColor.GRAY + "Iron Ore: " + ChatColor.RESET + target.getStatistic(Statistic.MINE_BLOCK, Material.IRON_ORE));
		sender.sendMessage(ChatColor.GOLD + "Gold Ore: " + ChatColor.RESET + target.getStatistic(Statistic.MINE_BLOCK, Material.GOLD_ORE));
		sender.sendMessage(ChatColor.RED + "Redstone Ore: " + ChatColor.RESET + target.getStatistic(Statistic.MINE_BLOCK, Material.REDSTONE_ORE));
		sender.sendMessage(ChatColor.BLACK + "Coal Ore: " + ChatColor.RESET + target.getStatistic(Statistic.MINE_BLOCK, Material.COAL_ORE));
		sender.sendMessage(ChatColor.BLUE + "Lapis Ore: " + ChatColor.RESET + target.getStatistic(Statistic.MINE_BLOCK, Material.LAPIS_ORE));
		sender.sendMessage(ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + "--------------------------------------------------");

		return false;
	}
}
