package me.joeleoli.hcfactions.command;

import com.doctordark.internal.com.doctordark.base.BaseConstants;
import com.doctordark.util.BukkitUtils;
import me.joeleoli.hcfactions.FactionsPlugin;
import me.joeleoli.hcfactions.file.DataFactory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import me.joeleoli.hcfactions.player.PlayerData;

import java.util.UUID;

public class LivesCommand implements Listener, CommandExecutor {

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			PlayerData playerData = FactionsPlugin.getInstance().getPlayerManager().getPlayerData(player);

			if (args.length == 0) {
				player.sendMessage(ChatColor.GOLD + BukkitUtils.STRAIGHT_LINE_DEFAULT);
				player.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + "LIVES HELP" + ChatColor.GOLD + " - " + ChatColor.GRAY + " (Page 1/1)");
				player.sendMessage(ChatColor.YELLOW + "/lives check" + ChatColor.GOLD + " - " + ChatColor.GRAY + "Check your lives.");
				player.sendMessage(ChatColor.YELLOW + "/lives revive <player>" + ChatColor.GOLD + " - " + ChatColor.GRAY + "Revive a player.");

				if (player.hasPermission("hcf.command.lives.staff")) {
					player.sendMessage(ChatColor.YELLOW + "/lives add <player> <amount>" + ChatColor.GOLD + " - " + ChatColor.GRAY + "Give a player lives." + ChatColor.GRAY);
				}

				player.sendMessage(ChatColor.GOLD + BukkitUtils.STRAIGHT_LINE_DEFAULT);
				return true;
			}

			if (args[0].equalsIgnoreCase("check")) {
				player.sendMessage(ChatColor.YELLOW + "You currently have " + ChatColor.AQUA + (playerData.getLives() == 1 ? "1" + ChatColor.YELLOW + " lives." : "" + playerData.getLives() + ChatColor.YELLOW + " lives."));
				return true;
			} else if (args[0].equalsIgnoreCase("revive")) {
				if(!(playerData.getLives() > 0)) {
					player.sendMessage(ChatColor.RED + "You do not have any lives to revive a player.");
					return true;
				}
				
				if (args.length == 1) {
					player.sendMessage(ChatColor.RED + "You need to specify a player.");
					return true;
				}

				OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

				if (!target.hasPlayedBefore() && !target.isOnline()) {
					player.sendMessage(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND);
					return true;
				}

				UUID targetUUID = target.getUniqueId();

				if (FactionsPlugin.getInstance().getDeathBanManager().getDeathBans().containsKey(targetUUID)) {
					FactionsPlugin.getInstance().getDeathBanManager().removeDeathBan(targetUUID);

					playerData.setLives(playerData.getLives() - 1);
					player.sendMessage(ChatColor.YELLOW + "You have revived " + target.getName() + ChatColor.YELLOW + ".");
					
					FactionsPlugin.getInstance().getLogManager().formatMessage("Live revived player: " + target.getName(), sender.getName());
				} else {
					sender.sendMessage(ChatColor.RED + target.getName() + " is not currently death-banned.");
				}
			} else if (args[0].equalsIgnoreCase("add")) {
				if (!player.hasPermission("hcf.command.lives.staff")) {
					player.sendMessage(ChatColor.GOLD + BukkitUtils.STRAIGHT_LINE_DEFAULT);
					player.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + "LIVES HELP" + ChatColor.GOLD + " - " + ChatColor.GRAY + " (Page 1/1)");
					player.sendMessage(ChatColor.YELLOW + "/lives check" + ChatColor.GOLD + " - " + ChatColor.GRAY + "Check your lives." + ChatColor.GRAY);
					player.sendMessage(ChatColor.YELLOW + "/lives revive <player>" + ChatColor.GOLD + " - " + ChatColor.GRAY + "Revive a player." + ChatColor.GRAY);
					player.sendMessage(ChatColor.GOLD + BukkitUtils.STRAIGHT_LINE_DEFAULT);
					return true;
				}

				if(args.length == 1) {
					player.sendMessage(ChatColor.RED + "LIVES - You didn't specify a player.");
					return true;
				}

				if(args.length == 2) {
					player.sendMessage(ChatColor.RED + "LIVES - You didn't specify an amount.");
					return true;
				}

				int amount = Integer.valueOf(args[2]);

				if(Bukkit.getPlayer(args[1]) == null) {
					DataFactory.addLives(Bukkit.getPlayer(args[1]), amount);
					player.sendMessage(ChatColor.YELLOW + "LIVES - Added " + ChatColor.AQUA + amount + ChatColor.YELLOW + " lives to " + args[1] + ".");
				} else {
					PlayerData targetData = FactionsPlugin.getInstance().getPlayerManager().getPlayerData(Bukkit.getPlayer(args[1]));
					targetData.setLives(targetData.getLives() + amount);
					player.sendMessage(ChatColor.YELLOW + "LIVES - Added " + ChatColor.AQUA + amount + ChatColor.YELLOW + " lives to " + args[1] + ".");
				}
			} else {
				sender.sendMessage(ChatColor.GOLD + BukkitUtils.STRAIGHT_LINE_DEFAULT);
				sender.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + "LIVES HELP" + ChatColor.GOLD + " - " + ChatColor.GRAY + " (Page 1/1)");
				sender.sendMessage(ChatColor.YELLOW + "/lives check" + ChatColor.GOLD + " - " + ChatColor.GRAY + "Check your lives." + ChatColor.GRAY);
				sender.sendMessage(ChatColor.YELLOW + "/lives revive <player>" + ChatColor.GOLD + " - " + ChatColor.GRAY + "Revive a player." + ChatColor.GRAY);
				sender.sendMessage(ChatColor.GOLD + BukkitUtils.STRAIGHT_LINE_DEFAULT);
			}
		} else {
			if(args[0].equalsIgnoreCase("add")) {
				if(args.length == 1) {
					sender.sendMessage(ChatColor.RED + "LIVES - You didn't specify a player.");
					return true;
				}

				if(args.length == 2) {
					sender.sendMessage(ChatColor.RED + "LIVES - You didn't specify an amount.");
					return true;
				}

				int amount = Integer.valueOf(args[2]);

				if(Bukkit.getPlayer(args[1]) == null) {
					DataFactory.addLives(Bukkit.getPlayer(args[1]), amount);
					sender.sendMessage(ChatColor.YELLOW + "LIVES - Added " + ChatColor.AQUA + amount + ChatColor.YELLOW + " lives to " + args[1] + ".");
				} else {
					PlayerData targetData = FactionsPlugin.getInstance().getPlayerManager().getPlayerData(Bukkit.getPlayer(args[1]));
					targetData.setLives(targetData.getLives() + amount);
					sender.sendMessage(ChatColor.YELLOW + "LIVES - Added " + ChatColor.AQUA + amount + ChatColor.YELLOW + " lives to " + args[1] + ".");
				}
				
				FactionsPlugin.getInstance().getLogManager().formatMessage("Gave " + amount + " lives to player: " + Bukkit.getOfflinePlayer(args[1]).getName(), sender.getName());
			} else {
				sender.sendMessage(ChatColor.YELLOW + "LIVES - /lives add <amount>");
			}
		}

		return true;
	}

}