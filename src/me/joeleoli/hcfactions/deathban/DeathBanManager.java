package me.joeleoli.hcfactions.deathban;

import lombok.Getter;
import me.joeleoli.hcfactions.FactionsPlugin;
import me.joeleoli.hcfactions.file.DataFactory;
import me.joeleoli.hcfactions.file.FileConfig;
import me.joeleoli.hcfactions.utils.LocationUtils;
import me.joeleoli.hcfactions.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Timestamp;
import java.util.*;

public class DeathBanManager implements Listener {

	private FileConfig config;
	private @Getter Map<UUID, DeathBan> deathBans;
	private List<UUID> rejoin;

	public DeathBanManager() {
		this.config = new FileConfig("deathbans.yml");
		this.deathBans = new HashMap<>();
		this.rejoin = new ArrayList<>();

		loadDeathBans();

		Bukkit.getPluginManager().registerEvents(this, FactionsPlugin.getInstance());
	}

	private void loadDeathBans() {
		if(config.getConfig().contains("deathbans")) {
			for(String key : config.getConfig().getConfigurationSection("deathbans").getKeys(false)) {
				UUID uuid = UUID.fromString(key);
				OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
				OfflinePlayer killer = config.getConfig().getString("deathbans." + player.getUniqueId() + ".killer").equalsIgnoreCase("none") ? null : Bukkit.getOfflinePlayer(config.getConfig().getString("deathbans." + uuid.toString() + ".killer"));
				Location location = LocationUtils.getLocation(config.getConfig().getString("deathbans." + uuid.toString() + ".location"));
				Timestamp timestamp = new Timestamp(config.getConfig().getLong("deathbans." + uuid.toString() + ".timestamp"));
				Timestamp expire = new Timestamp(config.getConfig().getLong("deathbans." + uuid.toString() + ".expire"));

				DeathBan deathBan = new DeathBan(player, killer, location, timestamp, expire);

				deathBans.put(UUID.fromString(key), deathBan);
			}
		}
	}

	public void applyDeathBan(DeathBan deathBan) {
		Player player = Bukkit.getPlayer(deathBan.getPlayer().getUniqueId());
		if(player != null && player.hasPermission("network.staff")) return;

		deathBans.put(deathBan.getPlayer().getUniqueId(), deathBan);

		config.getConfig().set("deathbans." + deathBan.getPlayer().getUniqueId().toString() + ".killer", deathBan.getKiller() == null ? "none" : deathBan.getKiller().getUniqueId().toString());
		config.getConfig().set("deathbans." + deathBan.getPlayer().getUniqueId().toString() + ".location", LocationUtils.getString(deathBan.getLocation()));
		config.getConfig().set("deathbans." + deathBan.getPlayer().getUniqueId().toString() + ".timestamp", deathBan.getTimestamp());
		config.getConfig().set("deathbans." + deathBan.getPlayer().getUniqueId().toString() + ".expire", deathBan.getExpire());
		config.save();

		int minutes = (int) (deathBan.getExpire().getTime() - deathBan.getTimestamp().getTime()) / 60000;

		Bukkit.getPlayer(deathBan.getPlayer().getUniqueId()).kickPlayer(ChatColor.GRAY + "You have been death-banned! Come back in " + ChatColor.RED + minutes + ChatColor.GRAY + "minutes!\n\nYou were killed by " + ChatColor.RED + (deathBan.getKiller() != null ? deathBan.getKiller().getName() : "Environment") + "\n" + ChatColor.GRAY + "Time of Death: " + ChatColor.RED + deathBan.getTimestamp().toString());

		FactionsPlugin.getInstance().getLogManager().formatMessage("Death-banned", deathBan.getPlayer().getName() + " - " + deathBan.getKiller().getName());
	}

	public void removeDeathBan(UUID uuid) {
		deathBans.remove(uuid);
		config.getConfig().set("deathbans." + uuid.toString(), null);
		config.save();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLogin(AsyncPlayerPreLoginEvent event) {
		if(deathBans.containsKey(event.getUniqueId())) {
			DeathBan deathBan = deathBans.get(event.getUniqueId());

			if (!(System.currentTimeMillis() - deathBan.getExpire().getTime() >= 0)) {
				if(DataFactory.getLives(deathBan.getPlayer()) > 0) {
					if(rejoin.contains(event.getUniqueId())) {
						removeDeathBan(deathBan.getPlayer().getUniqueId());

						new BukkitRunnable() {
							public void run() {
								Player player = Bukkit.getPlayer(event.getUniqueId());

								if (player != null) {
									player.sendMessage(ChatColor.GREEN + "You have used a live to be revived.");
								}
							}
						}.runTaskLater(FactionsPlugin.getInstance(), 10L);
					} else {
						event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, ChatColor.RED + "You are death-banned for another " + ChatColor.YELLOW + TimeUtils.getDurationBreakdown(Math.abs(System.currentTimeMillis() - deathBan.getExpire().getTime())) + ChatColor.RED + ". You were killed by " + ChatColor.RED + (deathBan.getKiller() == null ? "Environment" : deathBan.getKiller().getName()) + ChatColor.GRAY + ". Time of Death: " + ChatColor.RED + deathBan.getTimestamp().toString() + ". Rejoin within 15 seconds to use a live.");
						rejoin.add(event.getUniqueId());

						new BukkitRunnable() {
							public void run() {
								if(rejoin.contains(event.getUniqueId())) {
									rejoin.remove(event.getUniqueId());
								}
							}
						}.runTaskLater(FactionsPlugin.getInstance(), 20L * 15);
					}
				} else {
					event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, ChatColor.RED + "You are death-banned for another " + ChatColor.YELLOW + TimeUtils.getDurationBreakdown(Math.abs(System.currentTimeMillis() - deathBan.getExpire().getTime())) + ChatColor.RED + ". You were killed by " + ChatColor.RED + (deathBan.getKiller() == null ? "Environment" : deathBan.getKiller().getName()) + ChatColor.GRAY + ". Time of Death: " + ChatColor.RED + deathBan.getTimestamp().toString() + ". Purchase lives at store.wenjapvp.net.");
				}
			} else {
				removeDeathBan(event.getUniqueId());
			}
		}
	}

}