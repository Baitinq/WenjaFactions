package me.joeleoli.hcfactions.economy;

import com.doctordark.internal.com.doctordark.base.BaseConstants;
import com.doctordark.util.BukkitUtils;
import com.doctordark.util.JavaUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;

import me.joeleoli.hcfactions.FactionsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Command used to check a players' balance.
 */
public class EconomyCommand implements CommandExecutor, TabCompleter {
	private static final ImmutableList<String> COMPLETIONS;
	private static final ImmutableList<String> GIVE;
	private static final ImmutableList<String> TAKE;

	static {
		TAKE = ImmutableList.of("take", "negate", "minus", "subtract");
		GIVE = ImmutableList.of("give", "add");
		COMPLETIONS = ImmutableList.of("add", "set", "take");
	}

	private final FactionsPlugin plugin;

	public EconomyCommand(final FactionsPlugin plugin) {
		this.plugin = plugin;
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {

		final boolean hasStaffPermission = sender.hasPermission(command.getPermission() + ".staff");
		OfflinePlayer target;

		if (args.length > 0 && hasStaffPermission) {
			if (args[0].equalsIgnoreCase("all") || args[0].equalsIgnoreCase("*") && hasStaffPermission) {
				Integer amount = null;

				try {
					amount = Integer.valueOf(args[2]);
				}
				catch (Exception e) {
					e.printStackTrace();
				}

				if (amount == null) {
					sender.sendMessage(ChatColor.RED + "Invalid value.");
					return true;
				}

				for (UUID user : plugin.getPlayerManager().getAllData().keySet()) {
					this.plugin.getEconomyManager().addBalance(user, amount);
				}

				Bukkit.broadcastMessage(ChatColor.YELLOW + sender.getName() + " GAVE ALL PLAYERS " + amount + "");
				return true;
			}

			target = BukkitUtils.offlinePlayerWithNameOrUUID(args[0]);
		} else {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <playerName>");
				return true;
			}

			target = (OfflinePlayer) sender;
		}

		if (!target.hasPlayedBefore() && !target.isOnline()) {
			sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, args[0]));
			return true;
		}

		final UUID uuid = target.getUniqueId();
		final int balance = this.plugin.getEconomyManager().getBalance(uuid);

		if (args.length < 2 || !hasStaffPermission) {
			sender.sendMessage(ChatColor.YELLOW + (sender.equals(target) ? "Your balance" : ("Balance of " + target.getName())) + " is " + ChatColor.GREEN + EconomyManager.ECONOMY_SYMBOL + balance + ChatColor.YELLOW + '.');
			return true;
		}

		if (GIVE.contains(args[1].toLowerCase())) {
			if (args.length < 3) {
				sender.sendMessage(ChatColor.RED + "Usage: /" + label + ' ' + target.getName() + ' ' + args[1] + " <amount>");
				return true;
			}

			Integer amount = null;

			try {
				amount = Integer.valueOf(args[2]);
			}
			catch (Exception e) {
				e.printStackTrace();
			}

			if (amount == null) {
				sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a valid number.");
				return true;
			}

			final int newBalance = this.plugin.getEconomyManager().addBalance(uuid, amount);

			sender.sendMessage(new String[]{ChatColor.YELLOW + "Added " + EconomyManager.ECONOMY_SYMBOL + JavaUtils.format(amount) + " to balance of " + target.getName() + '.', ChatColor.YELLOW + "Balance of " + target.getName() + " is now " + EconomyManager.ECONOMY_SYMBOL + newBalance + '.'});
			return true;
		} else if (TAKE.contains(args[1].toLowerCase())) {
			if (args.length < 3) {
				sender.sendMessage(ChatColor.RED + "Usage: /" + label + ' ' + target.getName() + ' ' + args[1] + " <amount>");
				return true;
			}

			Integer amount = null;

			try {
				amount = Integer.valueOf(args[2]);
			}
			catch (Exception e) {
				e.printStackTrace();
			}

			if (amount == null) {
				sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a valid number.");
				return true;
			}

			final int newBalance = this.plugin.getEconomyManager().subtractBalance(uuid, amount);

			sender.sendMessage(new String[]{ChatColor.YELLOW + "Taken " + EconomyManager.ECONOMY_SYMBOL + JavaUtils.format(amount) + " from balance of " + target.getName() + '.', ChatColor.YELLOW + "Balance of " + target.getName() + " is now " + EconomyManager.ECONOMY_SYMBOL + newBalance + '.'});
			return true;
		} else {
			if (!args[1].equalsIgnoreCase("set")) {
				sender.sendMessage(ChatColor.GOLD + (sender.equals(target) ? "Your balance" : ("Balance of " + target.getName())) + " is " + ChatColor.WHITE + EconomyManager.ECONOMY_SYMBOL + balance + ChatColor.GOLD + '.');
				return true;
			}

			if (args.length < 3) {
				sender.sendMessage(ChatColor.RED + "Usage: /" + label + ' ' + target.getName() + ' ' + args[1] + " <amount>");
				return true;
			}

			Integer amount = null;

			try {
				amount = Integer.valueOf(args[2]);
			}
			catch (Exception e) {
				e.printStackTrace();
			}

			if (amount == null) {
				sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a valid number.");
				return true;
			}

			final int newBalance = this.plugin.getEconomyManager().setBalance(uuid, amount);

			sender.sendMessage(ChatColor.YELLOW + "Set balance of " + target.getName() + " to " + EconomyManager.ECONOMY_SYMBOL + JavaUtils.format(newBalance) + '.');
			return true;
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
		return (args.length == 2) ? BukkitUtils.getCompletions(args, COMPLETIONS) : (args.length == 1) ? null : Collections.emptyList();
	}

}