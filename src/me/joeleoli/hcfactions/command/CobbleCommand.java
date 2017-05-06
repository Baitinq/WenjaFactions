package me.joeleoli.hcfactions.command;

import com.google.common.collect.Sets;
import me.joeleoli.hcfactions.FactionsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Set;

public class CobbleCommand implements Listener, CommandExecutor {

	public static Set<Player> disabled = Sets.newHashSet();

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "Only players can execute this command.");
			return true;
		}

		Player player = (Player) sender;

		if (disabled.contains(player)) {
			disabled.remove(player);
			player.sendMessage(FactionsPlugin.PREFIX + ChatColor.GREEN + "You have enabled cobblestone pickups.");
		} else {
			disabled.add(player);
			player.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "You have disabled cobblestone pickups.");
		}

		return true;
	}

	@EventHandler
	public void onPlayerPickup(PlayerQuitEvent event) {
		disabled.remove(event.getPlayer());
	}

	@EventHandler
	public void onPlayerPickup(PlayerPickupItemEvent event) {
		Material type = event.getItem().getItemStack().getType();

		if (type == Material.STONE || type == Material.COBBLESTONE) {
			if (disabled.contains(event.getPlayer())) {
				event.setCancelled(true);
			}
		}
	}

}