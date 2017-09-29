package me.joeleoli.hcfactions.faction.argument;

import com.doctordark.util.command.CommandArgument;
import me.joeleoli.hcfactions.FactionsPlugin;
import me.joeleoli.hcfactions.faction.struct.Relation;
import me.joeleoli.hcfactions.faction.struct.Role;
import me.joeleoli.hcfactions.faction.type.PlayerFaction;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FactionLeaveArgument extends CommandArgument {

	private final FactionsPlugin plugin;

	public FactionLeaveArgument(FactionsPlugin plugin) {
		super("leave", "Leave your current faction.");
		this.plugin = plugin;
	}

	@Override
	public String getUsage(String label) {
		return '/' + label + ' ' + getName();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "Only players can leave faction.");
			return true;
		}

		Player player = (Player) sender;
		PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);

		if (playerFaction == null) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "You are not in a faction.");
			return true;
		}

		UUID uuid = player.getUniqueId();
		if (playerFaction.getMember(uuid).getRole() == Role.LEADER) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "You cannot leave factions as a leader. Either use " + ChatColor.GOLD + '/' + label + " disband" + ChatColor.RED + " or " + ChatColor.GOLD + '/' + label + " leader" + ChatColor.RED + '.');

			return true;
		}

		if (playerFaction.removeMember(player, player, player.getUniqueId(), false, false)) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.YELLOW + "Successfully left the faction.");
			playerFaction.broadcast(FactionsPlugin.PREFIX + Relation.ENEMY.toChatColour() + sender.getName() + ChatColor.YELLOW + " has left the faction.");
		}

		return true;
	}
}
