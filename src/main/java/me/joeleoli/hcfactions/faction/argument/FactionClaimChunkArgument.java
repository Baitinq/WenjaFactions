package me.joeleoli.hcfactions.faction.argument;

import com.doctordark.util.command.CommandArgument;
import me.joeleoli.hcfactions.FactionsPlugin;
import me.joeleoli.hcfactions.faction.claim.Claim;
import me.joeleoli.hcfactions.faction.claim.ClaimHandler;
import me.joeleoli.hcfactions.faction.struct.Role;
import me.joeleoli.hcfactions.faction.type.PlayerFaction;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class FactionClaimChunkArgument extends CommandArgument implements Listener {

	private static final int CHUNK_RADIUS = 7;
	private final FactionsPlugin plugin;

	public FactionClaimChunkArgument(FactionsPlugin plugin) {
		super("claimchunk", "Claim a chunk of land in the Wilderness.", new String[]{"chunkclaim"});
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

		if (playerFaction.isRaidable()) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "You cannot claim land for your faction while raidable.");
			return true;
		}

		if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "You must be an officer to claim land.");
			return true;
		}

		Location location = player.getLocation();
		plugin.getClaimHandler().tryPurchasing(player, new Claim(playerFaction, location.clone().add(CHUNK_RADIUS, ClaimHandler.MIN_CLAIM_HEIGHT, CHUNK_RADIUS), location.clone().add(-CHUNK_RADIUS, ClaimHandler.MAX_CLAIM_HEIGHT, -CHUNK_RADIUS)));

		return true;
	}

}