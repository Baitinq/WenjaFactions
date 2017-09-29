package me.joeleoli.hcfactions.command;

import me.joeleoli.hcfactions.FactionsPlugin;
import me.joeleoli.hcfactions.utils.MessageUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class HCFHelpCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender p, Command command, String label, String[] args) {
		if (!(p instanceof Player)) {
			p.sendMessage(ChatColor.RED + "This command is only executable by players.");
			return true;
		}

		if(FactionsPlugin.getInstance().getConfig().contains("help")) {
			for(String line : FactionsPlugin.getInstance().getConfig().getStringList("help")) {
				p.sendMessage(MessageUtils.color(line));
			}
		}

		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return Collections.emptyList();
	}

}