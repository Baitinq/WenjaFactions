package me.joeleoli.hcfactions.faction.type;

import com.doctordark.util.BukkitUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Map;

/**
 * Represents the {@link SpawnFaction}.
 */
public class GlowstoneMountainFaction extends ClaimableFaction implements ConfigurationSerializable {

	public GlowstoneMountainFaction() {
		super("Glowstone");

		this.safezone = false;
	}

	public GlowstoneMountainFaction(Map<String, Object> map) {
		super(map);
	}

	@Override
	public String getDisplayName(CommandSender sender) {
		return ChatColor.GOLD + "Glowstone";
	}

	@Override
	public void printDetails(CommandSender sender) {
		sender.sendMessage(ChatColor.GOLD + BukkitUtils.STRAIGHT_LINE_DEFAULT);
		sender.sendMessage(' ' + getDisplayName(sender));
		sender.sendMessage(ChatColor.YELLOW + "  Location: " + ChatColor.RED + "(Nether, -250 | -250)");
		sender.sendMessage(ChatColor.GOLD + BukkitUtils.STRAIGHT_LINE_DEFAULT);
	}
}
