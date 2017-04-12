package me.joeleoli.hcfactions.faction.argument.staff;

import com.doctordark.util.JavaUtils;
import com.doctordark.util.command.CommandArgument;
import com.google.common.collect.ImmutableList;
import me.joeleoli.hcfactions.ConfigurationService;
import me.joeleoli.hcfactions.FactionsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.joeleoli.hcfactions.economy.EconomyManager;
import me.joeleoli.hcfactions.faction.type.PlayerFaction;
import me.joeleoli.hcfactions.utils.MessageUtils;

import java.util.Collections;
import java.util.List;

public class FactionGiveBalArgument extends CommandArgument {

	private static final ImmutableList<String> COMPLETIONS = ImmutableList.of("all");
	private final FactionsPlugin plugin;

	public FactionGiveBalArgument(FactionsPlugin plugin) {
		super("givebal", "Gives balance to a player's faction.", new String[]{"w"});
		this.plugin = plugin;
	}

	@Override
	public String getUsage(String label) {
		return "/f givebal <player> <amount>";
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can update the faction balance.");
			return true;
		}

		Player player = (Player) sender;

		if (!player.hasPermission("hcf.command.givebal")) {
			player.sendMessage("Unknown command. Type \"/help\" for help.");
			return true;
		}

		if (args.length < 3) {
			sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
			return true;
		}

		if (Bukkit.getPlayer(args[1]) == null) {
			sender.sendMessage(ChatColor.RED + "That player is not online.");
			return true;
		}

		Player target = Bukkit.getPlayerExact(args[1]);
		PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(target.getUniqueId());

		if (playerFaction == null) {
			sender.sendMessage(ChatColor.RED + "That player is not in a faction.");
			return true;
		}

		if (!args[2].chars().allMatch(Character::isDigit)) {
			sender.sendMessage(ChatColor.RED + "You provided an invalid integer.");
			return true;
		}

		int factionBalance = playerFaction.getBalance();
		int addedBalance = Integer.valueOf(args[2]);
		int newBalance = factionBalance + addedBalance;

		playerFaction.setBalance(newBalance);
		playerFaction.broadcast(ConfigurationService.TEAMMATE_COLOUR + player.getName() + ChatColor.YELLOW + " has given your faction " + ChatColor.GREEN + EconomyManager.ECONOMY_SYMBOL + JavaUtils.format(addedBalance) + ChatColor.YELLOW + ".");

		for (Player all : Bukkit.getOnlinePlayers()) {
			if (all.hasPermission("utilities.player.staff") || all.isOp()) {
				sender.sendMessage(MessageUtils.color("&7[&c!&7] " + player.getName() + " &ahas given the faction " + ChatColor.YELLOW + playerFaction.getName() + " $" + addedBalance + "."));
			}
		}

		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return args.length == 2 ? COMPLETIONS : Collections.emptyList();
	}
}
