package me.joeleoli.hcfactions.faction.argument;

import com.doctordark.util.command.CommandArgument;
import me.joeleoli.hcfactions.ConfigurationService;
import me.joeleoli.hcfactions.FactionsPlugin;
import me.joeleoli.hcfactions.faction.FactionMember;
import me.joeleoli.hcfactions.faction.struct.ChatChannel;
import me.joeleoli.hcfactions.faction.struct.Relation;
import me.joeleoli.hcfactions.faction.type.Faction;
import me.joeleoli.hcfactions.faction.type.PlayerFaction;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.joeleoli.hcfactions.faction.struct.Role;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Faction argument used to accept invitations from {@link Faction}s.
 */
public class FactionAcceptArgument extends CommandArgument {

	private final FactionsPlugin plugin;

	public FactionAcceptArgument(FactionsPlugin plugin) {
		super("accept", "Accept a join request from an existing faction.", new String[]{"join", "a"});
		this.plugin = plugin;
	}

	@Override
	public String getUsage(String label) {
		return '/' + label + ' ' + getName() + " <factionName>";
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(FactionsPlugin.PREFIX + "This command is only executable by players.");
			return true;
		}

		if (args.length < 2) {
			sender.sendMessage(FactionsPlugin.PREFIX + "Usage: " + getUsage(label));
			return true;
		}

		Player player = (Player) sender;

		if (plugin.getFactionManager().getPlayerFaction(player) != null) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "You are already in a faction.");
			return true;
		}

		Faction faction = plugin.getFactionManager().getContainingFaction(args[1]);

		if (faction == null) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "Faction named or containing member with IGN or UUID " + args[1] + " not found.");
			return true;
		}

		if (!(faction instanceof PlayerFaction)) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "You can only join player factions.");
			return true;
		}

		PlayerFaction targetFaction = (PlayerFaction) faction;

		if (targetFaction.getMembers().size() >= ConfigurationService.MAX_MEMBERS_PER_FACTION) {
			sender.sendMessage(FactionsPlugin.PREFIX + faction.getDisplayName(sender) + ChatColor.RED + " is full. Faction limits are at " + ConfigurationService.MAX_MEMBERS_PER_FACTION + '.');
			return true;
		}

		if (!targetFaction.isOpen() && !targetFaction.getInvitedPlayerNames().contains(player.getName())) {
			sender.sendMessage(FactionsPlugin.PREFIX + faction.getDisplayName(sender) + ChatColor.RED + " has not invited you.");
			return true;
		}

		if (targetFaction.addMember(player, player, player.getUniqueId(), new FactionMember(player, ChatChannel.PUBLIC, Role.MEMBER))) {
			targetFaction.broadcast(FactionsPlugin.PREFIX + Relation.MEMBER.toChatColour() + sender.getName() + ChatColor.GRAY + " has joined the faction.");
		}

		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (args.length != 2 || !(sender instanceof Player)) {
			return Collections.emptyList();
		}

		return plugin.getFactionManager().getFactions().stream().filter(faction -> faction instanceof PlayerFaction && ((PlayerFaction) faction).getInvitedPlayerNames().contains(sender.getName())).map(faction -> sender.getName()).collect(Collectors.toList());
	}

}