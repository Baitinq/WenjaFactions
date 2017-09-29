package me.joeleoli.hcfactions.event.argument;

import com.doctordark.util.command.CommandArgument;
import com.google.common.primitives.Ints;

import me.joeleoli.hcfactions.FactionsPlugin;
import me.joeleoli.hcfactions.event.crate.EventKey;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import me.joeleoli.hcfactions.event.EventType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An {@link CommandArgument} used for setting the loot of an {@link EventKey}.
 */
public class EventSetLootArgument extends CommandArgument {

	private final FactionsPlugin plugin;

	public EventSetLootArgument(FactionsPlugin plugin) {
		super("setloottable", "Sets the loot table of an event key at a specific slot");
		this.plugin = plugin;
		this.aliases = new String[]{"setloot"};
		this.permission = "hcf.command.event.argument." + getName();
	}

	@Override
	public String getUsage(String label) {
		return '/' + label + ' ' + getName() + " <eventType> <inventoryNumber>";
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
			return true;
		}

		if (args.length < 3) {
			sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
			return true;
		}

		EventType eventType = EventType.getByDisplayName(args[1]);

		if (eventType == null) {
			sender.sendMessage(ChatColor.RED + "There is not an event type named " + args[1] + '.');
			return true;
		}

		Integer index = null;

		try {
			index = Integer.valueOf(args[2]);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		if (index == null) {
			sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a number.");
			return true;
		}

		List<Inventory> inventories = plugin.getKeyManager().getEventKey().getInventories(eventType);
		int size = inventories.size();

		if (index < 1) {
			sender.sendMessage(ChatColor.RED + "You cannot edit an inventory less than 1.");
			return true;
		}

		if (index > size) {
			sender.sendMessage(ChatColor.RED + "There are only " + size + " possible loot inventories for " + eventType.getDisplayName() + ChatColor.RED + ". Use " + ChatColor.YELLOW + '/' + label + " addloottable " + eventType.name() + ChatColor.RED + " to add another.");

			return true;
		}

		((Player) sender).openInventory(inventories.get(--index));
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (args.length != 2) {
			return Collections.emptyList();
		}

		EventType[] values = EventType.values();
		List<String> results = new ArrayList<>(values.length);
		for (EventType eventType : values) {
			results.add(eventType.name());
		}

		return results;
	}
}
