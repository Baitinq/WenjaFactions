package me.joeleoli.hcfactions.command;

import com.doctordark.internal.com.doctordark.base.BaseConstants;
import me.joeleoli.hcfactions.FactionsPlugin;
import me.joeleoli.hcfactions.timer.Cooldowns;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class FriendCommand implements CommandExecutor {

	public String getUsage(String label) {
		return String.valueOf('/') + label + ' ' + "revive <playerName>";
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player p = (Player) sender;

		if (args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
			return true;
		}

		if (Cooldowns.isOnCooldown("friend_cooldown", p)) {
			p.sendMessage(ChatColor.RED + "You still have an " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "Medic" + ChatColor.RED + " cooldown for another " + Cooldowns.getCooldownForPlayerLong("friend_cooldown", p) + ChatColor.RED + '.');
			return true;
		}

		OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

		if (!target.hasPlayedBefore() && !target.isOnline()) {
			sender.sendMessage(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND);
			return true;
		}

		UUID targetUUID = target.getUniqueId();

		if(FactionsPlugin.getInstance().getDeathBanManager().getDeathBans().containsKey(targetUUID)) {
			Cooldowns.addCooldown("friend_cooldown", p, 5400);

			sender.sendMessage(ChatColor.YELLOW + "You have revived " + target.getName() + ChatColor.YELLOW + ".");
			Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + p.getDisplayName() + ChatColor.translateAlternateColorCodes('&', " &7used their rank to revive &d" + target.getName() + "&7."));

			FactionsPlugin.getInstance().getLogManager().formatMessage("Friend revived player: " + target.getName(), sender.getName());
		} else {
			sender.sendMessage(ChatColor.RED + target.getName() + " is not death-banned.");
		}

		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return Collections.emptyList();
	}

}