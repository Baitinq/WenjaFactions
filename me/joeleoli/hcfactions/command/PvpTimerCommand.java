package me.joeleoli.hcfactions.command;

import com.doctordark.util.BukkitUtils;
import com.doctordark.util.DurationFormatter;
import com.google.common.collect.ImmutableList;
import me.joeleoli.hcfactions.FactionsPlugin;
import me.joeleoli.hcfactions.timer.type.InvincibilityTimer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/**
 * Command used to manage the {@link InvincibilityTimer} of {@link Player}s.
 */
public class PvpTimerCommand implements CommandExecutor, TabCompleter {

	private static ImmutableList<String> COMPLETIONS = ImmutableList.of("enable", "time");
	private FactionsPlugin plugin;

	public PvpTimerCommand(FactionsPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "This command is only executable by players.");
			return true;
		}

		Player player = (Player) sender;
		InvincibilityTimer pvpTimer = plugin.getTimerManager().getInvincibilityTimer();

		if (args.length < 1) {
			printUsage(sender, label);
			return true;
		}

		if (args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("off")) {
			if (pvpTimer.getRemaining(player) <= 0L) {
				sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "Your " + pvpTimer.getDisplayName() + ChatColor.RED + " is currently not active.");
				return true;
			}

			pvpTimer.clearCooldown(player);
			return true;
		}

		if (args[0].equalsIgnoreCase("remaining") || args[0].equalsIgnoreCase("time") || args[0].equalsIgnoreCase("left") || args[0].equalsIgnoreCase("check")) {
			long remaining = pvpTimer.getRemaining(player);
			if (remaining <= 0L) {
				sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "Your " + pvpTimer.getDisplayName() + ChatColor.RED + " timer is currently not active.");
				return true;
			}

			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.YELLOW + "Your " + pvpTimer.getDisplayName() + ChatColor.YELLOW + " timer is active for another " + ChatColor.BOLD + DurationFormatter.getRemaining(remaining, true, false) + ChatColor.YELLOW + (pvpTimer.isPaused(player) ? " and is currently paused" : "") + '.');

			return true;
		}

		printUsage(sender, label);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return args.length == 1 ? BukkitUtils.getCompletions(args, COMPLETIONS) : Collections.emptyList();
	}

	/**
	 * Prints the usage of this command to a sender.
	 *
	 * @param sender the sender to print for
	 * @param label  the label used for command
	 */
	private void printUsage(CommandSender sender, String label) {
		sender.sendMessage(ChatColor.GOLD + BukkitUtils.STRAIGHT_LINE_DEFAULT);
		sender.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + "PVP HELP" + ChatColor.GOLD + " - " + ChatColor.GRAY + " (Page 1/1)");
		sender.sendMessage(ChatColor.YELLOW + "/" + label + " enable " + ChatColor.GOLD + "- " + ChatColor.GRAY + "Removes your PvP Timer" + ChatColor.GRAY);
		sender.sendMessage(ChatColor.YELLOW + "/" + label + " time " + ChatColor.GOLD + "- " + ChatColor.GRAY + "Check remaining PvP Timer" + ChatColor.GRAY);
		sender.sendMessage(ChatColor.YELLOW + "/lives " + ChatColor.GOLD + "- " + ChatColor.GRAY + "Lives and death-ban related commands.");
		sender.sendMessage(ChatColor.GOLD + BukkitUtils.STRAIGHT_LINE_DEFAULT);
	}

}