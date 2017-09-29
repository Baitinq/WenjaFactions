package me.joeleoli.hcfactions.faction.argument;

import com.doctordark.util.command.CommandArgument;
import me.joeleoli.hcfactions.faction.claim.ClaimHandler;
import me.joeleoli.hcfactions.FactionsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import me.joeleoli.hcfactions.faction.type.PlayerFaction;

import java.util.UUID;

public class FactionClaimArgument extends CommandArgument {

	private final FactionsPlugin plugin;

	public FactionClaimArgument(FactionsPlugin plugin) {
		super("claim", "Claim land in the Wilderness.", new String[]{"claimland"});
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
		UUID uuid = player.getUniqueId();

		PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(uuid);

		if (playerFaction == null) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "You are not in a faction.");
			return true;
		}

		if (playerFaction.isRaidable()) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "You cannot claim land for your faction while raidable.");
			return true;
		}

		PlayerInventory inventory = player.getInventory();

		if (inventory.contains(ClaimHandler.CLAIM_WAND)) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "You already have a claiming wand in your inventory.");
			return true;
		}

		if (!inventory.addItem(ClaimHandler.CLAIM_WAND).isEmpty()) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "Your inventory is full. Please empty a slot and re-try.");
			return true;
		}

		sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.GOLD + "The claiming wand has been added to your inventory, read the lore to understand how to claim. " + "Alternatively" + ChatColor.GOLD + " you can use " + ChatColor.WHITE + ChatColor.BOLD + '/' + label + " claimchunk" + ChatColor.GOLD + '.');

		return true;
	}
}
