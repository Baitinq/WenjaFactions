package me.joeleoli.hcfactions.command;

import me.joeleoli.hcfactions.listener.Crowbar;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveCrowbarCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;

		if (cmd.getName().equalsIgnoreCase("crowgive")) {
			if (args.length == 0) {
				p.sendMessage(ChatColor.RED + "Usage: /" + label + " <PlayerName>");
				return true;
			}

			Player target = Bukkit.getServer().getPlayer(args[0]);

			if (target == null) {
				p.sendMessage(ChatColor.RED + "That player is not online! Try again later!");
				return true;
			}

			ItemStack stack = new Crowbar().getItemIfPresent();

			target.getInventory().addItem(stack);
			target.sendMessage(ChatColor.GREEN + "You were given a CROWBAR from " + sender.getName());

			p.sendMessage(ChatColor.GREEN + "You have given " + target.getName() + " a CROWBAR");
			return true;
		}

		return false;
	}

}