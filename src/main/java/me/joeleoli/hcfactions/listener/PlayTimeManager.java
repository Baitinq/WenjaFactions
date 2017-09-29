package me.joeleoli.hcfactions.listener;

import com.doctordark.util.Config;
import net.minecraft.util.gnu.trove.map.TObjectLongMap;
import net.minecraft.util.gnu.trove.map.hash.TObjectLongHashMap;
import org.bukkit.Bukkit;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class PlayTimeManager implements Listener {
	private final TObjectLongMap<UUID> totalPlaytimeMap;
	private final TObjectLongMap<UUID> sessionTimestamps;
	private final Config config;

	public PlayTimeManager(final JavaPlugin plugin) {
		this.totalPlaytimeMap = new TObjectLongHashMap<UUID>();
		this.sessionTimestamps = new TObjectLongHashMap<UUID>();
		this.config = new Config(plugin, "play-times");
		this.reloadPlaytimeData();
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerJoin(final PlayerJoinEvent event) {
		this.sessionTimestamps.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerQuit(final PlayerQuitEvent event) {
		final UUID uuid = event.getPlayer().getUniqueId();
		this.totalPlaytimeMap.put(uuid, this.getTotalPlayTime(uuid));
		this.sessionTimestamps.remove(uuid);
	}

	public void reloadPlaytimeData() {
		final Object object = this.config.get("playing-times");
		if (object instanceof MemorySection) {
			final MemorySection section = (MemorySection) object;
			for (final Object id : section.getKeys(false)) {
				this.totalPlaytimeMap.put(UUID.fromString((String) id), this.config.getLong("playing-times." + id, 0L));
			}
		}
		final long millis = System.currentTimeMillis();
		for (Player target : Bukkit.getOnlinePlayers()) {
			this.sessionTimestamps.put(target.getUniqueId(), millis);
		}
	}

	public void savePlaytimeData() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			this.totalPlaytimeMap.put(player.getUniqueId(), this.getTotalPlayTime(player.getUniqueId()));
		}
		this.totalPlaytimeMap.forEachEntry((uuid, l) -> {
			this.config.set("playing-times." + uuid.toString(), l);
			return true;
		});
		this.config.save();
	}

	public long getSessionPlayTime(final UUID uuid) {
		final long session = this.sessionTimestamps.get(uuid);
		return (session != this.sessionTimestamps.getNoEntryValue()) ? (System.currentTimeMillis() - session) : 0L;
	}

	public long getPreviousPlayTime(final UUID uuid) {
		final long stamp = this.totalPlaytimeMap.get(uuid);
		return (stamp == this.totalPlaytimeMap.getNoEntryValue()) ? 0L : stamp;
	}

	public long getTotalPlayTime(final UUID uuid) {
		return this.getSessionPlayTime(uuid) + this.getPreviousPlayTime(uuid);
	}
}
