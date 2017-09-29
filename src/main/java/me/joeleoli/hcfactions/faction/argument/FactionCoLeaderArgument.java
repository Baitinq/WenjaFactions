package me.joeleoli.hcfactions.faction.argument;

import com.doctordark.util.command.CommandArgument;
import me.joeleoli.hcfactions.FactionsPlugin;
import me.joeleoli.hcfactions.faction.FactionMember;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.joeleoli.hcfactions.faction.struct.Relation;
import me.joeleoli.hcfactions.faction.struct.Role;
import me.joeleoli.hcfactions.faction.type.PlayerFaction;

public class FactionCoLeaderArgument extends CommandArgument {

	private FactionsPlugin main;

	public FactionCoLeaderArgument() {
		super("coleader", "make a player a co-leader.");
		main = FactionsPlugin.getInstance();
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
		PlayerFaction playerFaction = main.getFactionManager().getPlayerFaction(player);

		if (playerFaction == null) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "You are not in a faction.");
			return true;
		}

		if (playerFaction.getMember(player.getUniqueId()).getRole() != Role.LEADER) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "You must be a leader to edit the faction roster.");
			return true;
		}

		@SuppressWarnings("deprecation")
        FactionMember targetMember = playerFaction.getMember(args[1]);

		if (targetMember == null) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "That player is not in your faction.");
			return true;
		}

		Role role = targetMember.getRole();
		if (role == Role.COLEADER) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "That player is already a co-leader.");
			return true;
		}

		targetMember.setRole(Role.COLEADER);
		playerFaction.broadcast(FactionsPlugin.PREFIX + Relation.MEMBER.toChatColour() + targetMember.getName() + ChatColor.YELLOW + " has been promoted to a faction coleader.");
		return true;
	}
}
