package me.joeleoli.hcfactions.faction.argument.staff;

import com.doctordark.util.JavaUtils;
import com.doctordark.util.command.CommandArgument;
import me.joeleoli.hcfactions.FactionsPlugin;
import me.joeleoli.hcfactions.faction.FactionManager;
import me.joeleoli.hcfactions.faction.type.Faction;
import me.joeleoli.hcfactions.faction.type.PlayerFaction;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FactionSetLivesArgument extends CommandArgument {

	private final FactionsPlugin plugin;

	public FactionSetLivesArgument(FactionsPlugin plugin) {
		super("setlives", "Sets the lives of a faction", new String[]{"setfactionlives"});
		this.plugin = plugin;
		this.permission = "hcf.command.faction.argument." + getName();
	}

	@Override
	public String getUsage(String label) {
		return '/' + label + ' ' + getName() + " <playerName|factionName> <lives>";
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 3) {
			sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
			return true;
		}

		int newLives = (int) JavaUtils.parse(args[2]);

		if (newLives < 0) {
			sender.sendMessage(ChatColor.RED + "Faction lives cannot be negative.");
			return true;
		}

		if (newLives > FactionManager.MAX_LIVES) {
			sender.sendMessage(ChatColor.RED + "Cannot set faction lives above " + FactionManager.MAX_LIVES + ".");
			return true;
		}

		Faction faction = plugin.getFactionManager().getContainingFaction(args[1]);

		if (faction == null) {
			sender.sendMessage(ChatColor.RED + "Faction named or containing member with IGN or UUID " + args[1] + " not found.");
			return true;
		}

		if (!(faction instanceof PlayerFaction)) {
			sender.sendMessage(ChatColor.RED + "This type of faction does not use lives.");
			return true;
		}

		PlayerFaction playerFaction = (PlayerFaction) faction;
		int previousLives = playerFaction.getLives();
		playerFaction.setLives(newLives);

		Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Set faction lives of " + faction.getName() + (previousLives > 0L ? " from " + DurationFormatUtils.formatDurationWords(previousLives, true, true) : "") + " to " + DurationFormatUtils.formatDurationWords(newLives, true, true) + '.');

		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (args.length != 2) {
			return Collections.emptyList();
		} else if (args[1].isEmpty()) {
			return null;
		} else {
			List<String> results = new ArrayList<>(plugin.getFactionManager().getFactionNameMap().keySet());
			Player senderPlayer = sender instanceof Player ? ((Player) sender) : null;
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				// Make sure the player can see.
				if (senderPlayer == null || senderPlayer.canSee(player)) {
					results.add(player.getName());
				}
			}

			return results;
		}
	}
}
