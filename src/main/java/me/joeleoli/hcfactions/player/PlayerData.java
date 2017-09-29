package me.joeleoli.hcfactions.player;

import lombok.Getter;
import lombok.Setter;
import me.joeleoli.hcfactions.FactionsPlugin;
import me.joeleoli.hcfactions.file.FileConfig;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerData {

    @Getter private Player player;
    private FileConfig playerFile;

    @Getter private Set<UUID> factionChatSpying = new HashSet<>();

    @Getter @Setter private boolean hidingScoreboard;
    @Getter @Setter private boolean hidingChat;
    @Getter @Setter private boolean hidingMessages;
    @Getter @Setter private boolean hidingDuels;
    @Getter @Setter private boolean showClaimMap;
    @Getter @Setter private boolean reclaimed;

    @Getter @Setter private int kills;
    @Getter @Setter private int deaths;
    @Getter @Setter private int lives;
    @Getter @Setter private int spawnTokens;

    @Getter @Setter private long lastFactionAction;

    public PlayerData(Player player) {
        this.player = player;
        this.playerFile = new FileConfig(new File(FactionsPlugin.getInstance().getDataFolder() + "/playerdata/"), player.getUniqueId().toString() + ".yml");

        this.hidingChat = false;
        this.hidingMessages = false;
        this.hidingScoreboard = false;
        this.hidingDuels = false;

        this.kills = 0;
        this.deaths = 0;
        this.lives = 0;
        this.spawnTokens = 0;

        this.load();
    }

    public void load() {
        this.hidingMessages = playerFile.getConfig().contains("hiding-msgs") && playerFile.getConfig().getBoolean("hiding-msgs");
        this.hidingChat = playerFile.getConfig().contains("hiding-chat") && playerFile.getConfig().getBoolean("hiding-chat");
        this.hidingDuels = playerFile.getConfig().contains("hiding-duels") && playerFile.getConfig().getBoolean("hiding-duels");
        this.kills = playerFile.getConfig().getInt("kills");
        this.deaths = playerFile.getConfig().getInt("deaths");
        this.lives = playerFile.getConfig().getInt("lives");
        this.spawnTokens = playerFile.getConfig().getInt("spawn-tokens");
        this.reclaimed = playerFile.getConfig().getBoolean("reclaimed");
    }
    
    public void save() {
        playerFile.getConfig().set("hiding-msgs", this.hidingMessages);
        playerFile.getConfig().set("hiding-chat", this.hidingChat);
        playerFile.getConfig().set("hiding-duels", this.hidingDuels);
        playerFile.getConfig().set("kills", this.kills);
        playerFile.getConfig().set("deaths", this.deaths);
        playerFile.getConfig().set("lives", this.lives);
        playerFile.getConfig().set("spawn-tokens", this.spawnTokens);
        playerFile.getConfig().set("reclaimed", this.reclaimed);
        playerFile.save();
    }

}