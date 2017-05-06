package me.joeleoli.hcfactions.command;

import com.doctordark.util.DurationFormatter;
import me.joeleoli.hcfactions.FactionsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import me.joeleoli.hcfactions.timer.PlayerTimer;

import java.util.Collections;
import java.util.List;

/**
 * Command used to check remaining Notch Apple cooldown time for {@link Player}.
 */
public class GoppleCommand implements CommandExecutor, TabCompleter {

	private final FactionsPlugin plugin;

	public GoppleCommand(FactionsPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "This command is only executable by players.");
			return true;
		}

		Player player = (Player) sender;

		PlayerTimer timer = plugin.getTimerManager().getGappleTimer();
		long remaining = timer.getRemaining(player);

		if (remaining <= 0L) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "Your " + timer.getDisplayName() + ChatColor.RED + " timer is currently not active.");
			return true;
		}

		sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.YELLOW + "Your " + timer.getDisplayName() + ChatColor.YELLOW + " timer is active for another " + ChatColor.BOLD + DurationFormatter.getRemaining(remaining, true, false) + ChatColor.YELLOW + '.');

		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return Collections.emptyList();
	}

}