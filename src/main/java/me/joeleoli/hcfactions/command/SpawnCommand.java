package me.joeleoli.hcfactions.command;

import com.doctordark.util.BukkitUtils;
import me.joeleoli.hcfactions.FactionsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SpawnCommand implements CommandExecutor, TabCompleter {

	FactionsPlugin plugin;

	public SpawnCommand(FactionsPlugin plugin) {
		super();
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
			return true;
		}

		Player player = (Player) sender;
		World world = player.getWorld();
		Location spawn = world.getSpawnLocation().clone().add(0.5, 0.5, 0.5);

		if (!sender.hasPermission(command.getPermission() + ".teleport")) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "You cannot teleport to spawn. You must travel to " + ChatColor.GRAY + '(' + spawn.getBlockX() + ", " + spawn.getBlockZ() + ')');
			return true;
		}

		if (args.length > 0) {
			world = Bukkit.getWorld(args[0]);

			if (world == null) {
				sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "There is not a world named " + args[0] + '.');
				return true;
			}

			spawn = world.getSpawnLocation().clone().add(0.5, 0.0, 0.5);
		}

		player.teleport(spawn, PlayerTeleportEvent.TeleportCause.COMMAND);
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (args.length != 1 || !sender.hasPermission(command.getPermission() + ".teleport")) {
			return Collections.emptyList();
		}

		return BukkitUtils.getCompletions(args, Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList()));
	}

}