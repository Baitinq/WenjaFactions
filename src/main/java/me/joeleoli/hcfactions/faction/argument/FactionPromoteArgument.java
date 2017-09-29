package me.joeleoli.hcfactions.faction.argument;

import com.doctordark.util.command.CommandArgument;
import me.joeleoli.hcfactions.FactionsPlugin;
import me.joeleoli.hcfactions.faction.FactionMember;
import me.joeleoli.hcfactions.faction.type.PlayerFaction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.joeleoli.hcfactions.faction.struct.Relation;
import me.joeleoli.hcfactions.faction.struct.Role;

import java.util.*;

public class FactionPromoteArgument extends CommandArgument {

	private final FactionsPlugin plugin;

	public FactionPromoteArgument(FactionsPlugin plugin) {
		super("promote", "Promotes a player to a captain.");
		this.plugin = plugin;
		this.aliases = new String[]{"captain", "officer", "mod", "moderator"};
	}

	@Override
	public String getUsage(String label) {
		return '/' + label + ' ' + getName() + " <playerName>";
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "Only players can set faction captains.");
			return true;
		}

		if (args.length < 2) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "Usage: " + getUsage(label));
			return true;
		}

		Player player = (Player) sender;
		UUID uuid = player.getUniqueId();

		PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(uuid);

		if (playerFaction == null) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "You are not in a faction.");
			return true;
		}

		if (playerFaction.getMember(uuid).getRole() != Role.LEADER) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "You must be a faction leader to assign members as a captain.");
			return true;
		}

		FactionMember targetMember = playerFaction.getMember(args[1]);

		if (targetMember == null) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "That player is not in your faction.");
			return true;
		}

		Role role = targetMember.getRole();
		if (role == Role.COLEADER) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "That player is already the highest rank possible.");
			return true;
		}

		if (role == Role.MEMBER) {
			role = Role.CAPTAIN;
		} else if (role == Role.CAPTAIN) {
			role = Role.COLEADER;
		}

		targetMember.setRole(role);
		playerFaction.broadcast(FactionsPlugin.PREFIX + Relation.MEMBER.toChatColour() + role.getAstrix() + targetMember.getName() + ChatColor.YELLOW + " has been assigned as a faction " + role.name().toLowerCase() + ".");
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (args.length != 2 || !(sender instanceof Player)) {
			return Collections.emptyList();
		}

		Player player = (Player) sender;
		PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);
		if (playerFaction == null || playerFaction.getMember(player.getUniqueId()).getRole() != Role.LEADER) {
			return Collections.emptyList();
		}

		List<String> results = new ArrayList<>();
		for (Map.Entry<UUID, FactionMember> entry : playerFaction.getMembers().entrySet()) {
			if (entry.getValue().getRole() == Role.MEMBER) {
				OfflinePlayer target = Bukkit.getOfflinePlayer(entry.getKey());
				String targetName = target.getName();
				if (targetName != null) {
					results.add(targetName);
				}
			}
		}

		return results;
	}

}