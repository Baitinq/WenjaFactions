package me.joeleoli.hcfactions.faction.type;

import me.joeleoli.hcfactions.ConfigurationService;
import org.bukkit.command.CommandSender;

import java.util.Map;

/**
 * Represents the {@link WildernessFaction}.
 */
public class WildernessFaction extends Faction {

	public WildernessFaction() {
		super("Wilderness");
	}

	public WildernessFaction(Map<String, Object> map) {
		super(map);
	}

	@Override
	public String getDisplayName(CommandSender sender) {
		return ConfigurationService.WILDERNESS_COLOUR + getName();
	}
}
