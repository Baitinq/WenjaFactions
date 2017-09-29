package me.joeleoli.hcfactions.command;

import me.joeleoli.hcfactions.ConfigurationService;
import me.joeleoli.hcfactions.FactionsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;
import me.joeleoli.hcfactions.faction.struct.RegenStatus;
import me.joeleoli.hcfactions.faction.type.PlayerFaction;

import java.util.Collections;
import java.util.List;

public class RegenCommand implements CommandExecutor, TabCompleter {

	private FactionsPlugin plugin;

	public RegenCommand(FactionsPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "This command is only executable by players.");
			return true;
		}

		Player player = (Player) sender;
		PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);

		if (playerFaction == null) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "You are not in a faction.");
			return true;
		}

		RegenStatus regenStatus = playerFaction.getRegenStatus();

		switch (regenStatus) {
			case FULL:
				sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "Your faction currently has full DTR.");
				return true;
			case PAUSED:
				sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "Your faction is currently on DTR freeze for another " + ChatColor.WHITE + DurationFormatUtils.formatDurationWords(playerFaction.getRemainingRegenerationTime(), true, true) + ChatColor.RED + '.');

				return true;
			case REGENERATING:
				sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.YELLOW + "Your faction currently has " + ChatColor.YELLOW + regenStatus.getSymbol() + ' ' + playerFaction.getDeathsUntilRaidable() + ChatColor.YELLOW + " DTR and is regenerating at a rate of " + ChatColor.GOLD + ConfigurationService.DTR_INCREMENT_BETWEEN_UPDATES + ChatColor.YELLOW + " every " + ChatColor.GOLD + ConfigurationService.DTR_WORDS_BETWEEN_UPDATES + ChatColor.YELLOW + ". Your ETA for maximum DTR is " + ChatColor.RED + DurationFormatUtils.formatDurationWords(getRemainingRegenMillis(playerFaction), true, true) + ChatColor.YELLOW + '.');

				return true;
		}

		sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "Unrecognised regeneration status, please inform an Developer or an System Admin.");
		return true;
	}

	public long getRemainingRegenMillis(PlayerFaction faction) {
		long millisPassedSinceLastUpdate = System.currentTimeMillis() - faction.getLastDtrUpdateTimestamp();
		double dtrRequired = faction.getMaximumDeathsUntilRaidable() - faction.getDeathsUntilRaidable();
		return (long) ((ConfigurationService.DTR_MILLIS_BETWEEN_UPDATES / ConfigurationService.DTR_INCREMENT_BETWEEN_UPDATES) * dtrRequired) - millisPassedSinceLastUpdate;
	}

	@Override
	public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
		return Collections.emptyList();
	}

}