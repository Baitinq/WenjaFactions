package me.joeleoli.hcfactions.player;

import me.joeleoli.hcfactions.FactionsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager implements Listener {

    private Map<UUID, PlayerData> playerData;
    
    public PlayerManager() {
        this.playerData = new HashMap<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerData data = new PlayerData(player);
            this.playerData.put(player.getUniqueId(), data);
        }

        Bukkit.getPluginManager().registerEvents(this, FactionsPlugin.getInstance());
    }

    public Map<UUID, PlayerData> getAllData() {
        return playerData;
    }
    
    public PlayerData getPlayerData(Player player) {
        return playerData.get(player.getUniqueId());
    }

    public void savePlayerData() {
        for(PlayerData data : playerData.values()) {
            data.save();
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        playerData.put(player.getUniqueId(), new PlayerData(player));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if(playerData.containsKey(player.getUniqueId())) {
            playerData.get(player.getUniqueId()).save();

            new BukkitRunnable() {
                public void run() {
                    playerData.remove(player.getUniqueId());
                }
            }.runTaskLater(FactionsPlugin.getInstance(), 2L);
        }
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        Player player = event.getPlayer();

        if(playerData.containsKey(player.getUniqueId())) {
            playerData.get(player.getUniqueId()).save();

            new BukkitRunnable() {
                public void run() {
                    playerData.remove(player.getUniqueId());
                }
            }.runTaskLater(FactionsPlugin.getInstance(), 2L);
        }
    }

}