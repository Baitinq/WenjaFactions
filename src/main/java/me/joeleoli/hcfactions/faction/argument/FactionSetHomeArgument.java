package me.joeleoli.hcfactions.faction.argument;

import com.doctordark.util.command.CommandArgument;
import me.joeleoli.hcfactions.ConfigurationService;
import me.joeleoli.hcfactions.FactionsPlugin;
import me.joeleoli.hcfactions.faction.FactionMember;
import me.joeleoli.hcfactions.faction.claim.Claim;
import me.joeleoli.hcfactions.faction.type.PlayerFaction;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.joeleoli.hcfactions.faction.struct.Role;

public class FactionSetHomeArgument extends CommandArgument {

	private final FactionsPlugin plugin;

	public FactionSetHomeArgument(FactionsPlugin plugin) {
		super("sethome", "Sets the faction home location.");
		this.plugin = plugin;
	}

	@Override
	public String getUsage(String label) {
		return '/' + label + ' ' + getName();
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

		FactionMember factionMember = playerFaction.getMember(player);

		if (factionMember.getRole() == Role.MEMBER) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "You must be a faction officer to set the home.");
			return true;
		}

		Location location = player.getLocation();

		boolean insideTerritory = false;
		for (Claim claim : playerFaction.getClaims()) {
			if (claim.contains(location)) {
				insideTerritory = true;
				break;
			}
		}

		if (!insideTerritory) {
			player.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "You may only set your home in your territory.");
			return true;
		}

		playerFaction.setHome(location);
		playerFaction.broadcast(FactionsPlugin.PREFIX + ConfigurationService.TEAMMATE_COLOUR + sender.getName() + ChatColor.YELLOW + " has updated the faction home point.");
		return true;
	}
}
