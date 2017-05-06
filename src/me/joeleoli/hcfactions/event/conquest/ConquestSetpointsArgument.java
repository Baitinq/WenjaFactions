package me.joeleoli.hcfactions.event.conquest;

import com.doctordark.util.JavaUtils;
import com.doctordark.util.command.CommandArgument;
import me.joeleoli.hcfactions.ConfigurationService;
import me.joeleoli.hcfactions.FactionsPlugin;
import me.joeleoli.hcfactions.faction.type.Faction;
import me.joeleoli.hcfactions.faction.type.PlayerFaction;
import me.joeleoli.hcfactions.event.EventType;
import me.joeleoli.hcfactions.event.tracker.ConquestTracker;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ConquestSetpointsArgument extends CommandArgument {

	private final FactionsPlugin plugin;

	public ConquestSetpointsArgument(FactionsPlugin plugin) {
		super("setpoints", "Sets the points of a faction in the Conquest event", "hcf.command.conquest.argument.setpoints");
		this.plugin = plugin;
	}

	@Override
	public String getUsage(String label) {
		return '/' + label + ' ' + getName() + " <factionName> <amount>";
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 3) {
			sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
			return true;
		}

		Faction faction = plugin.getFactionManager().getFaction(args[1]);

		if (!(faction instanceof PlayerFaction)) {
			sender.sendMessage(ChatColor.RED + "Faction " + args[1] + " is either not found or is not a player faction.");
			return true;
		}

		Integer amount = JavaUtils.tryParseInt(args[2]);

		if (amount == null) {
			sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a number.");
			return true;
		}

		if (amount > ConfigurationService.CONQUEST_REQUIRED_WIN_POINTS) {
			sender.sendMessage(ChatColor.RED + "Maximum points for Conquest is " + ConfigurationService.CONQUEST_REQUIRED_WIN_POINTS + '.');
			return true;
		}

		PlayerFaction playerFaction = (PlayerFaction) faction;
		((ConquestTracker) EventType.CONQUEST.getEventTracker()).setPoints(playerFaction, amount);

		Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Set the points of faction " + playerFaction.getName() + " to " + amount + '.');
		return true;
	}
}
