package me.joeleoli.hcfactions.faction.argument;

import com.doctordark.util.command.CommandArgument;
import com.google.common.collect.ImmutableList;
import me.joeleoli.hcfactions.FactionsPlugin;
import me.joeleoli.hcfactions.faction.FactionMember;
import me.joeleoli.hcfactions.faction.type.PlayerFaction;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.joeleoli.hcfactions.faction.struct.Role;

import java.util.Collections;
import java.util.List;

public class FactionUnsubclaimArgument extends CommandArgument {

	private static final ImmutableList<String> COMPLETIONS = ImmutableList.of("all");
	private final FactionsPlugin plugin;

	public FactionUnsubclaimArgument(FactionsPlugin plugin) {
		super("unsubclaim", "Removes subclaims from your faction.");
		this.plugin = plugin;
	}

	@Override
	public String getUsage(String label) {
		return '/' + label + ' ' + getName() + " [all]";
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "Only players can un-claim land from a faction.");
			return true;
		}

		Player player = (Player) sender;
		PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);

		if (playerFaction == null) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "You are not in a faction.");
			return true;
		}

		FactionMember factionMember = playerFaction.getMember(player);

		if (factionMember.getRole() != Role.LEADER) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "You must be a faction leader to delete subclaims.");
			return true;
		}

		sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "Please use /" + label + " <subclaim> <remove> for now.");
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return args.length == 2 ? COMPLETIONS : Collections.emptyList();
	}
}
