package me.joeleoli.hcfactions.command;

import com.doctordark.internal.com.doctordark.base.BaseConstants;
import com.doctordark.util.BukkitUtils;
import me.joeleoli.hcfactions.FactionsPlugin;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class PlayTimeCommand implements CommandExecutor {

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		OfflinePlayer target;

		if (args.length >= 1) {
			target = BukkitUtils.offlinePlayerWithNameOrUUID(args[0]);
		} else {
			if (!(sender instanceof Player)) {
				sender.sendMessage("You can only use this if you are a player!");
				return true;
			}
			
			target = (OfflinePlayer) sender;
		}

		if ((!target.hasPlayedBefore()) && (!target.isOnline())) {
			sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, new Object[]{args[0]}));
			return true;
		}

		sender.sendMessage(ChatColor.YELLOW + target.getName() + " has been playing for " + ChatColor.LIGHT_PURPLE + DurationFormatUtils.formatDurationWords(FactionsPlugin.getInstance().getPlayTimeManager().getTotalPlayTime(target.getUniqueId()), true, true) + ChatColor.YELLOW + " this map.");
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return args.length == 1 ? null : Collections.emptyList();
	}

}