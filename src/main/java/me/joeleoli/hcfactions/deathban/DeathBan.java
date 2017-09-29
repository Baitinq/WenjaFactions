package me.joeleoli.hcfactions.deathban;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.sql.Timestamp;

public class DeathBan {

	@Getter private OfflinePlayer player;
	@Getter private OfflinePlayer killer;
	@Getter private Location location;
	@Getter private Timestamp timestamp;
	@Getter private Timestamp expire;

	public DeathBan(OfflinePlayer player, OfflinePlayer killer, Location location, Timestamp timestamp, Timestamp expire) {
		this.player = player;
		this.killer = killer;
		this.location = location;
		this.timestamp = timestamp;
		this.expire = expire;
	}

}