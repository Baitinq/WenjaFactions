package me.joeleoli.hcfactions.faction.argument;

import com.doctordark.compat.com.google.common.collect.GuavaCompat;
import com.doctordark.util.command.CommandArgument;
import me.joeleoli.hcfactions.FactionsPlugin;
import me.joeleoli.hcfactions.faction.LandMap;
import me.joeleoli.hcfactions.faction.claim.Claim;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.joeleoli.hcfactions.visualise.VisualType;
import me.joeleoli.hcfactions.player.PlayerData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Faction argument used to view a interactive map of {@link Claim}s.
 */
public class FactionMapArgument extends CommandArgument {

	private FactionsPlugin plugin;

	public FactionMapArgument(FactionsPlugin plugin) {
		super("map", "View all claims around your chunk.");
		this.plugin = plugin;
	}

	@Override
	public String getUsage(String label) {
		return '/' + label + ' ' + getName() + " [factionName]";
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "This command is only executable by players.");
			return true;
		}

		Player player = (Player) sender;
		PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);

		VisualType visualType;

		if (args.length <= 1) {
			visualType = VisualType.CLAIM_MAP;
		} else if ((visualType = GuavaCompat.getIfPresent(VisualType.class, args[1]).orNull()) == null) {
			player.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "Visual type " + args[1] + " not found.");
			return true;
		}

		boolean newShowingMap = !playerData.isShowClaimMap();

		if (newShowingMap) {
			if (!LandMap.updateMap(player, plugin, visualType, true)) {
				return true;
			}
		} else {
			plugin.getVisualiseHandler().clearVisualBlocks(player, visualType, null);
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "Claim pillars are no longer shown.");
		}

		playerData.setShowClaimMap(newShowingMap);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (args.length != 2 || !(sender instanceof Player)) {
			return Collections.emptyList();
		}

		VisualType[] values = VisualType.values();
		List<String> results = new ArrayList<>(values.length);
		for (VisualType visualType : values) {
			results.add(visualType.name());
		}

		return results;
	}

}