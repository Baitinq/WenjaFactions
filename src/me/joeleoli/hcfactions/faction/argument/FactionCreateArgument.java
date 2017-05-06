package me.joeleoli.hcfactions.faction.argument;

import com.doctordark.util.JavaUtils;
import com.doctordark.util.command.CommandArgument;
import me.joeleoli.hcfactions.ConfigurationService;
import me.joeleoli.hcfactions.FactionsPlugin;
import me.joeleoli.hcfactions.faction.type.Faction;
import me.joeleoli.hcfactions.faction.type.PlayerFaction;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Faction argument used to create a new {@link Faction}.
 */
public class FactionCreateArgument extends CommandArgument {

	private final FactionsPlugin plugin;

	public FactionCreateArgument(FactionsPlugin plugin) {
		super("create", "Create a faction.", new String[]{"make", "define"});
		this.plugin = plugin;
	}

	@Override
	public String getUsage(String label) {
		return '/' + label + ' ' + getName() + " <factionName>";
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "This command may only be executed by players.");
			return true;
		}

		if (args.length < 2) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "Usage: " + getUsage(label));
			return true;
		}

		String name = args[1];

		if (ConfigurationService.DISALLOWED_FACTION_NAMES.contains(name.toLowerCase())) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "'" + name + "' is a blocked faction name.");
			return true;
		}

		if (name.length() < ConfigurationService.FACTION_NAME_CHARACTERS_MIN) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "Faction names must have at least " + ConfigurationService.FACTION_NAME_CHARACTERS_MIN + " characters.");
			return true;
		}

		if (name.length() > ConfigurationService.FACTION_NAME_CHARACTERS_MAX) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "Faction names cannot be longer than " + ConfigurationService.FACTION_NAME_CHARACTERS_MAX + " characters.");
			return true;
		}

		if (!JavaUtils.isAlphanumeric(name)) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "Faction names may only be alphanumeric.");
			return true;
		}

		if (plugin.getFactionManager().getFaction(name) != null) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "Faction '" + name + "' already exists.");
			return true;
		}

		if (plugin.getFactionManager().getPlayerFaction((Player) sender) != null) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "You are already in a faction.");
			return true;
		}

		plugin.getFactionManager().createFaction(new PlayerFaction(name), sender);
		return true;
	}
}
