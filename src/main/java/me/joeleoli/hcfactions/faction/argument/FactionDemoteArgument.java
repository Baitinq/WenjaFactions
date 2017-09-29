package me.joeleoli.hcfactions.faction.argument;

import com.doctordark.util.command.CommandArgument;
import me.joeleoli.hcfactions.FactionsPlugin;
import me.joeleoli.hcfactions.faction.FactionMember;
import me.joeleoli.hcfactions.faction.struct.Relation;
import me.joeleoli.hcfactions.faction.type.Faction;
import me.joeleoli.hcfactions.faction.type.PlayerFaction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.joeleoli.hcfactions.faction.struct.Role;

import java.util.*;

/**
 * Faction argument used to demote players to members in {@link Faction}s.
 */
public class FactionDemoteArgument extends CommandArgument {

	private final FactionsPlugin plugin;

	public FactionDemoteArgument(FactionsPlugin plugin) {
		super("demote", "Demotes a player to a member.", new String[]{"uncaptain", "delcaptain", "delofficer"});
		this.plugin = plugin;
	}

	@Override
	public String getUsage(String label) {
		return '/' + label + ' ' + getName() + " <playerName>";
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "This command is only executable by players.");
			return true;
		}

		if (args.length < 2) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "Usage: " + getUsage(label));
			return true;
		}

		Player player = (Player) sender;
		PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);

		if (playerFaction == null) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "You are not in a faction.");
			return true;
		}

		if (playerFaction.getMember(player.getUniqueId()).getRole() != Role.LEADER) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "You must be a officer to edit the faction roster.");
			return true;
		}

		@SuppressWarnings("deprecation")
        FactionMember targetMember = playerFaction.getMember(args[1]);

		if (targetMember == null) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "That player is not in your faction.");
			return true;
		}

		Role role = targetMember.getRole();
		if (role == Role.MEMBER) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "That player is already the lowest rank possible.");
			return true;
		}

		if (role == Role.CAPTAIN) {
			role = Role.MEMBER;
		} else if (role == Role.COLEADER) {
			role = Role.CAPTAIN;
		}

		targetMember.setRole(role);
		playerFaction.broadcast(FactionsPlugin.PREFIX + Relation.MEMBER.toChatColour() + targetMember.getName() + ChatColor.YELLOW + " has been demoted to a faction " + role.name().toLowerCase() + ".");
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (args.length != 2 || !(sender instanceof Player)) {
			return Collections.emptyList();
		}

		Player player = (Player) sender;
		PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);
		if (playerFaction == null || (playerFaction.getMember(player.getUniqueId()).getRole() != Role.LEADER)) {
			return Collections.emptyList();
		}

		List<String> results = new ArrayList<>();
		Collection<UUID> keySet = playerFaction.getMembers().keySet();
		for (UUID entry : keySet) {
			OfflinePlayer target = Bukkit.getOfflinePlayer(entry);
			String targetName = target.getName();
			if (targetName != null && playerFaction.getMember(target.getUniqueId()).getRole() == Role.CAPTAIN) {
				results.add(targetName);
			}
		}

		return results;
	}
}
