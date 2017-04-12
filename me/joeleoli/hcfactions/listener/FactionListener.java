package me.joeleoli.hcfactions.listener;

import com.google.common.base.Optional;
import me.joeleoli.hcfactions.FactionsPlugin;
import me.joeleoli.hcfactions.faction.type.Faction;
import me.joeleoli.hcfactions.faction.event.*;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import me.joeleoli.hcfactions.event.faction.KothFaction;
import me.joeleoli.hcfactions.faction.struct.RegenStatus;
import me.joeleoli.hcfactions.faction.struct.Relation;
import me.joeleoli.hcfactions.faction.type.PlayerFaction;
import me.joeleoli.hcfactions.utils.MessageUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class FactionListener implements Listener {

	private static long FACTION_JOIN_WAIT_MILLIS = TimeUnit.SECONDS.toMillis(30L);

	private static String LAND_CHANGED_META_KEY = "landChangedMessage";
	private static long LAND_CHANGE_MSG_THRESHOLD = 225L;

	private FactionsPlugin plugin;

	public FactionListener(FactionsPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onFactionRenameMonitor(FactionRenameEvent event) {
		Faction faction = event.getFaction();

		if (faction instanceof KothFaction) {
			((KothFaction) faction).getCaptureZone().setName(event.getNewName());
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onFactionCreate(FactionCreateEvent event) {
		Faction faction = event.getFaction();

		Player player = (Player)event.getSender();

		long difference = (plugin.getPlayerManager().getPlayerData(player).getLastFactionAction() - System.currentTimeMillis()) + FACTION_JOIN_WAIT_MILLIS;

		if (difference > 0L && !player.hasPermission("hcf.faction.argument.staff.forcejoin")) {
			event.setCancelled(true);
			player.sendMessage(FactionsPlugin.PREFIX + "You must wait another " + DurationFormatUtils.formatDurationWords(difference, true, true) + " before joining another faction.");
		} else {
			plugin.getPlayerManager().getPlayerData(player).setLastFactionAction(System.currentTimeMillis());
		}

		if (faction instanceof PlayerFaction) {
			CommandSender sender = event.getSender();

			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				String msg = FactionsPlugin.PREFIX + MessageUtils.color("&eThe faction &c" + (p == null ? faction.getName() : faction.getName()) + " &ehas been &acreated &eby " + (sender instanceof Player ? MessageUtils.getFormattedName((Player)sender) : sender.getName()) + "&e.");
				p.sendMessage(msg);
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onFactionRemove(FactionRemoveEvent event) {
		Faction faction = event.getFaction();

		Player player = (Player)event.getSender();

		long difference = (plugin.getPlayerManager().getPlayerData(player).getLastFactionAction() - System.currentTimeMillis()) + FACTION_JOIN_WAIT_MILLIS;

		if (difference > 0L && !player.hasPermission("hcf.faction.argument.staff.forcermove")) {
			event.setCancelled(true);
			player.sendMessage(FactionsPlugin.PREFIX + "You must wait another " + DurationFormatUtils.formatDurationWords(difference, true, true) + " before joining another faction.");
		} else {
			plugin.getPlayerManager().getPlayerData(player).setLastFactionAction(System.currentTimeMillis());
		}

		if (faction instanceof PlayerFaction) {
			CommandSender sender = event.getSender();

			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				String msg = FactionsPlugin.PREFIX + MessageUtils.color("&eThe faction &c" + (p == null ? faction.getName() : faction.getName()) + " &ehas been &cdisbanded &eby " + (sender instanceof Player ? MessageUtils.getFormattedName((Player)sender) : sender.getName()) + "&e.");
				p.sendMessage(msg);
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onFactionRename(FactionRenameEvent event) {
		Faction faction = event.getFaction();

		Player player = (Player)event.getSender();

		long difference = (plugin.getPlayerManager().getPlayerData(player).getLastFactionAction() - System.currentTimeMillis()) + FACTION_JOIN_WAIT_MILLIS;

		if (difference > 0L) {
			event.setCancelled(true);
			player.sendMessage(FactionsPlugin.PREFIX + "You must wait another " + DurationFormatUtils.formatDurationWords(difference, true, true) + " before joining another faction.");
		} else {
			plugin.getPlayerManager().getPlayerData(player).setLastFactionAction(System.currentTimeMillis());
		}

		if (faction instanceof PlayerFaction) {
			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				Relation relation = faction.getRelation(p);
				String msg = FactionsPlugin.PREFIX + MessageUtils.color("&eThe faction &c" + (p == null ? faction.getName() : faction.getName()) + " &ehas been &brenamed &eto " + relation.toChatColour() + event.getNewName() + "&e.");
				p.sendMessage(msg);
			}
		}
	}

	private long getLastLandChangedMeta(Player player) {
		List<MetadataValue> value = player.getMetadata(LAND_CHANGED_META_KEY);

		long millis = System.currentTimeMillis();
		long remaining = value == null || value.isEmpty() ? 0L : value.get(0).asLong() - millis;

		if (remaining <= 0L) { // update the metadata.
			player.setMetadata(LAND_CHANGED_META_KEY, new FixedMetadataValue(plugin, millis + LAND_CHANGE_MSG_THRESHOLD));
		}

		return remaining;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onCaptureZoneEnter(CaptureZoneEnterEvent event) {
		Player player = event.getPlayer();
		player.sendMessage(ChatColor.YELLOW + "Now entering cap zone: " + event.getCaptureZone().getDisplayName() + ChatColor.YELLOW + '(' + event.getFaction().getName() + ChatColor.YELLOW + ')');

		if (getLastLandChangedMeta(player) > 0L) return;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onCaptureZoneLeave(CaptureZoneLeaveEvent event) {
		Player player = event.getPlayer();
		player.sendMessage(ChatColor.YELLOW + "Now leaving cap zone: " + event.getCaptureZone().getDisplayName() + ChatColor.YELLOW + '(' + event.getFaction().getName() + ChatColor.YELLOW + ')');

		if (getLastLandChangedMeta(player) > 0L) return;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	private void onPlayerClaimEnter(PlayerClaimEnterEvent event) {
		Faction toFaction = event.getToFaction();

		if (toFaction.isSafezone()) {
			Player player = event.getPlayer();
			player.setHealth(player.getMaxHealth());
			player.setFoodLevel(20);
			player.setFireTicks(0);
			player.setSaturation(4.0F);
		}

		Player player = event.getPlayer();

		if (getLastLandChangedMeta(player) > 0L) return;

		player.sendMessage(ChatColor.YELLOW + "Entering " + toFaction.getDisplayName(player) + ChatColor.YELLOW + "'s territory.");
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerLeftFaction(PlayerLeftFactionEvent event) {
		Optional<Player> optionalPlayer = event.getPlayer();

		if (optionalPlayer.isPresent()) {
			plugin.getPlayerManager().getPlayerData(optionalPlayer.get()).setLastFactionAction(System.currentTimeMillis());
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerPreFactionJoin(PlayerJoinFactionEvent event) {
		Faction faction = event.getFaction();
		Optional<Player> optionalPlayer = event.getPlayer();

		if (faction instanceof PlayerFaction && optionalPlayer.isPresent()) {
			Player player = optionalPlayer.get();
			PlayerFaction playerFaction = (PlayerFaction) faction;

			if (!plugin.getEotwHandler().isEndOfTheWorld() && playerFaction.getRegenStatus() == RegenStatus.PAUSED) {
				event.setCancelled(true);
				player.sendMessage(FactionsPlugin.PREFIX + "You cannot join factions that are on DTR freeze.");
				return;
			}

			long difference = (plugin.getPlayerManager().getPlayerData(player).getLastFactionAction() - System.currentTimeMillis()) + FACTION_JOIN_WAIT_MILLIS;

			if (difference > 0L && !player.hasPermission("hcf.faction.argument.staff.forcejoin")) {
				event.setCancelled(true);
				player.sendMessage(FactionsPlugin.PREFIX + "You must wait another " + DurationFormatUtils.formatDurationWords(difference, true, true) + " before joining another faction.");
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onFactionLeave(PlayerLeaveFactionEvent event) {
		if (event.isForce() || event.isKick()) return;

		Faction faction = event.getFaction();

		if (faction instanceof PlayerFaction) {
			Optional<Player> optional = event.getPlayer();

			if (optional.isPresent()) {
				Player player = optional.get();

				if (plugin.getFactionManager().getFactionAt(player.getLocation()) == faction) {
					event.setCancelled(true);
					player.sendMessage(FactionsPlugin.PREFIX + "You cannot leave your faction while still in it's territory.");
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);

		if (playerFaction != null) {
			playerFaction.printDetails(player);
			playerFaction.broadcast(ChatColor.DARK_GREEN + "Member Online: " + ChatColor.GREEN + playerFaction.getMember(player).getRole().getAstrix() + player.getName() + ChatColor.GOLD + ".", player.getUniqueId());
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);

		if (playerFaction != null) {
			playerFaction.broadcast(ChatColor.RED + "Member Offline: " + ChatColor.GREEN + playerFaction.getMember(player).getRole().getAstrix() + player.getName() + ChatColor.GOLD + ".");
		}
	}

}