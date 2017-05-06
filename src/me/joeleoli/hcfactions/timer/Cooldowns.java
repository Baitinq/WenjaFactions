package me.joeleoli.hcfactions.timer;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class Cooldowns {

	private static HashMap<String, HashMap<UUID, Long>> cooldown = new HashMap<>();

	public static void createCooldown(String k) {
		if (Cooldowns.cooldown.containsKey(k)) throw new IllegalArgumentException("Cooldown already exists.");
		
		Cooldowns.cooldown.put(k, new HashMap<>());
	}

	public static void addCooldown(String k, Player p, int seconds) {
		if (!Cooldowns.cooldown.containsKey(k)) throw new IllegalArgumentException(k + " does not exist");

		Cooldowns.cooldown.get(k).put(p.getUniqueId(), System.currentTimeMillis() + seconds * 1000L);
	}

	public static boolean isOnCooldown(String k, Player p) {
		return Cooldowns.cooldown.containsKey(k) && Cooldowns.cooldown.get(k).containsKey(p.getUniqueId()) && System.currentTimeMillis() <= Cooldowns.cooldown.get(k).get(p.getUniqueId());
	}

	public static long getCooldownForPlayerLong(String k, Player p) {
		return (int) (Cooldowns.cooldown.get(k).get(p.getUniqueId()) - System.currentTimeMillis());
	}

}