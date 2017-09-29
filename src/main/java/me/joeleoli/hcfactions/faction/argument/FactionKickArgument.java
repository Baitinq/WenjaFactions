package me.joeleoli.hcfactions.faction.argument;

import com.doctordark.util.command.CommandArgument;
import me.joeleoli.hcfactions.ConfigurationService;
import me.joeleoli.hcfactions.FactionsPlugin;
import me.joeleoli.hcfactions.faction.FactionMember;
import me.joeleoli.hcfactions.faction.type.PlayerFaction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.joeleoli.hcfactions.faction.struct.Role;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class FactionKickArgument extends CommandArgument {

	private final FactionsPlugin plugin;

	public FactionKickArgument(FactionsPlugin plugin) {
		super("kick", "Kick a player from the faction.");
		this.plugin = plugin;
		this.aliases = new String[]{"kickmember", "kickplayer"};
	}

	@Override
	public String getUsage(String label) {
		return '/' + label + ' ' + getName() + " <playerName>";
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "Only players can kick from a faction.");
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

		if (playerFaction.isRaidable() && !plugin.getEotwHandler().isEndOfTheWorld()) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "You cannot kick players while your faction is raidable.");
			return true;
		}

		FactionMember targetMember = playerFaction.getMember(args[1]);

		if (targetMember == null) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "Your faction does not have a member named '" + args[1] + "'.");
			return true;
		}

		Role selfRole = playerFaction.getMember(player.getUniqueId()).getRole();

		if (!(selfRole == Role.CAPTAIN || selfRole == Role.COLEADER || selfRole == Role.LEADER)) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "You must be a faction officer to kick members.");
			return true;
		}

		Role targetRole = targetMember.getRole();

		if (targetRole == Role.LEADER) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "You cannot kick the faction leader.");
			return true;
		}

		if (targetRole == Role.CAPTAIN && selfRole == Role.CAPTAIN) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "You must be a faction leader to kick captains.");
			return true;
		}

		Player onlineTarget = targetMember.toOnlinePlayer();
		if (playerFaction.removeMember(sender, onlineTarget, targetMember.getUniqueId(), true, true)) {
			if (onlineTarget != null) {
				onlineTarget.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED.toString() + ChatColor.BOLD + "You were kicked from the faction by " + sender.getName() + '.');
			}

			playerFaction.broadcast(FactionsPlugin.PREFIX + ConfigurationService.ENEMY_COLOUR + targetMember.getName() + ChatColor.YELLOW + " has been kicked by " + ConfigurationService.TEAMMATE_COLOUR + playerFaction.getMember(player).getRole().getAstrix() + sender.getName() + ChatColor.YELLOW + '.');
		}

		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (args.length != 2 || !(sender instanceof Player)) {
			return Collections.emptyList();
		}

		Player player = (Player) sender;
		PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);
		if (playerFaction == null) {
			return Collections.emptyList();
		}

		Role memberRole = playerFaction.getMember(player.getUniqueId()).getRole();
		if (memberRole == Role.MEMBER) {
			return Collections.emptyList();
		}

		List<String> results = new ArrayList<>();
		for (UUID entry : playerFaction.getMembers().keySet()) {
			Role targetRole = playerFaction.getMember(entry).getRole();
			if (targetRole == Role.LEADER || (targetRole == Role.CAPTAIN && memberRole != Role.LEADER)) {
				continue;
			}

			OfflinePlayer target = Bukkit.getOfflinePlayer(entry);
			String targetName = target.getName();
			if (targetName != null && !results.contains(targetName)) {
				results.add(targetName);
			}
		}

		return results;
	}
}
