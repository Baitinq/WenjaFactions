package me.joeleoli.hcfactions.file;

import me.joeleoli.hcfactions.FactionsPlugin;
import me.joeleoli.hcfactions.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class DataFactory {

    public static Integer getLives(OfflinePlayer player) {
        if(Bukkit.getPlayer(player.getUniqueId()) != null) {
            return FactionsPlugin.getInstance().getPlayerManager().getPlayerData(player.getPlayer()).getLives();
        }

        File file = new File(FactionsPlugin.getInstance().getDataFolder() + "/playerdata/" + player.getUniqueId().toString() + ".yml");

        if(!file.exists()) {
            return 0;
        }

        YamlConfiguration config = new YamlConfiguration();

        try {
            config.load(file);
            return config.getInt("lives");
        }
        catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static void addLives(OfflinePlayer player, Integer add) {
        if(Bukkit.getPlayer(player.getUniqueId()) != null) {
            PlayerData playerData = FactionsPlugin.getInstance().getPlayerManager().getPlayerData(player.getPlayer());
            playerData.setLives(playerData.getLives());
            playerData.save();
        }

        File file = new File(FactionsPlugin.getInstance().getDataFolder() + "/playerdata/" + player.getUniqueId().toString() + ".yml");

        if(!file.exists()) return;

        YamlConfiguration config = new YamlConfiguration();

        try {
            config.load(file);
            config.set("lives", config.getInt("lives") + add);
            config.save(file);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Integer getKills(OfflinePlayer player) {
        if(Bukkit.getPlayer(player.getUniqueId()) != null) {
            return FactionsPlugin.getInstance().getPlayerManager().getPlayerData(player.getPlayer()).getKills();
        }

        File file = new File(FactionsPlugin.getInstance().getDataFolder() + "/playerdata/" + player.getUniqueId().toString() + ".yml");

        if(!file.exists()) {
            return 0;
        }

        YamlConfiguration config = new YamlConfiguration();

        try {
            config.load(file);
            return config.getInt("kills");
        }
        catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

}