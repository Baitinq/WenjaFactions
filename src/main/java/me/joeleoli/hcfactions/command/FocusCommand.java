package me.joeleoli.hcfactions.command;

import me.joeleoli.hcfactions.FactionsPlugin;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import me.joeleoli.hcfactions.faction.type.Faction;
import me.joeleoli.hcfactions.faction.type.PlayerFaction;

import java.util.Set;
import java.util.UUID;

public class FocusCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}

		Player player = (Player) sender;
		PlayerFaction playerFaction = FactionsPlugin.getInstance().getFactionManager().getPlayerFaction(player);

		if (playerFaction == null) {
			player.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "You are not in a faction");
			return true;
		}
		
		if (args.length == 0) {
			player.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "Usage: /focus <player>");
			return true;
		}
		
		String key = StringUtils.join(args).replace(" ", "");
		PlayerFaction playerFaction2 = FactionsPlugin.getInstance().getFactionManager().getPlayerFaction(player);
		
		if (playerFaction2 == null) {
			player.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "That player is not online");
			return true;
		}

		if (playerFaction2.getFocused() == null) {
			Player playerMatch = Bukkit.getPlayer(key);
			PlayerFaction toFocus;
			
			if (playerMatch == null) {
				Faction faction = FactionsPlugin.getInstance().getFactionManager().getFaction(key);
				
				if (faction == null || !(faction instanceof PlayerFaction)) {
					player.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "That player is not in a faction");
					return true;
				}
				
				toFocus = (PlayerFaction) faction;
			} else {
				toFocus = FactionsPlugin.getInstance().getFactionManager().getPlayerFaction(playerMatch);
				
				if (toFocus == null) {
					Faction faction = FactionsPlugin.getInstance().getFactionManager().getFaction(key);
					
					if (faction == null || !(faction instanceof PlayerFaction)) {
						player.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "That player is not part of any faction");
						return true;
					}
					
					toFocus = (PlayerFaction) faction;
				}
			}
			
			if (playerFaction2.equals(toFocus)) {
				player.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "You cannot focus yourself");
				return true;
			}
			
			playerFaction2.broadcast(FactionsPlugin.PREFIX + "&b" + key + " &ehas been &6focused &eby &b" + player.getName());
			PlayerFaction ToFocus = toFocus;
			
			new BukkitRunnable() {
				public void run() {
					focus(playerFaction2, ToFocus, false);
				}
			}.runTaskAsynchronously(FactionsPlugin.getInstance());

			new BukkitRunnable() {
				@Override
				public void run() {
					new BukkitRunnable() {
						public void run() {
							focus(playerFaction, null, true);
						}
					}.runTaskAsynchronously(FactionsPlugin.getInstance());
					playerFaction.setFocused(null);
				}
			}.runTaskLater(FactionsPlugin.getInstance(), 20 * 60 * 10);
		} else {
			if (playerFaction.getFocused() != null) {
				playerFaction.broadcast(FactionsPlugin.PREFIX + "&b" + player.getName() + " &ehas unfocused the target");
			}

			new BukkitRunnable() {
				public void run() {
					focus(playerFaction, null, true);
				}
			}.runTaskAsynchronously(FactionsPlugin.getInstance());
			playerFaction.setFocused(null);
		}

		return false;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		PlayerFaction playerFaction = FactionsPlugin.getInstance().getFactionManager().getPlayerFaction(player.getUniqueId());
		UUID focused = playerFaction.getFocused();

		if (focused != null) {
			focus(playerFaction, (PlayerFaction) FactionsPlugin.getInstance().getFactionManager().getFaction(playerFaction.getFocused()), false);
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		PlayerFaction playerFaction = FactionsPlugin.getInstance().getFactionManager().getPlayerFaction(player.getUniqueId());

		if (playerFaction == null) return;
		for (Faction faction : FactionsPlugin.getInstance().getFactionManager().getFactions()) {
			if (faction instanceof PlayerFaction) {
				if (((PlayerFaction) faction).getFocused() == null) continue;
				if (((PlayerFaction) faction).getFocused().equals(player.getUniqueId())) {
					focus((PlayerFaction) faction, playerFaction, true);
				}
			}
		}
	}

	private void focus(PlayerFaction playerFaction, PlayerFaction toFocus, boolean unfocus) {
		if (!unfocus) {
			playerFaction.setFocused(toFocus.getUniqueID());
		}
		for (Player player : playerFaction.getOnlinePlayers()) {
			Scoreboard scoreboard = player.getScoreboard();
			if (scoreboard != Bukkit.getScoreboardManager().getMainScoreboard()) {
				Team team = scoreboard.getTeam("focus");
				if (team != null) {
					Set<OfflinePlayer> offlinePlayers = team.getPlayers();
					for (OfflinePlayer offlinePlayer : offlinePlayers) {
						Player other = offlinePlayer.getPlayer();
						if (other != null) {
							FactionsPlugin.getInstance().getScoreboardHandler().getPlayerBoard(player.getUniqueId()).addUpdate(other);
						}
					}
					team.unregister();
				} else {
					team = scoreboard.registerNewTeam("focus");
					team.setPrefix(ChatColor.LIGHT_PURPLE.toString());
				}
				if (unfocus) {
					break;
				}
				for (Player playerToFocus : toFocus.getOnlinePlayers()) {
					team.addEntry(playerToFocus.getName());
				}
			}
		}
	}
	
}