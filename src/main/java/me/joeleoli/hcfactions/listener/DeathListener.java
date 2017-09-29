package me.joeleoli.hcfactions.listener;

import com.doctordark.util.JavaUtils;
import me.joeleoli.hcfactions.FactionsPlugin;
import net.minecraft.server.v1_7_R4.EntityLightning;
import net.minecraft.server.v1_7_R4.PacketPlayOutSpawnEntityWeather;
import net.minecraft.server.v1_7_R4.WorldServer;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import me.joeleoli.hcfactions.ConfigurationService;
import me.joeleoli.hcfactions.faction.struct.Role;
import me.joeleoli.hcfactions.faction.type.Faction;
import me.joeleoli.hcfactions.faction.type.PlayerFaction;
import me.joeleoli.hcfactions.deathban.DeathBan;
import me.joeleoli.hcfactions.player.PlayerData;
import me.joeleoli.hcfactions.player.PlayerInv;
import org.bukkit.inventory.ItemStack;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DeathListener implements Listener {

	private FactionsPlugin plugin;

	private static long BASE_REGEN_DELAY = TimeUnit.MINUTES.toMillis(40L);
	public static HashMap<UUID, PlayerInv[]> playerInventories;

	static {
		playerInventories = new HashMap<>();
	}

	public DeathListener(FactionsPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onDeath(PlayerDeathEvent event) {
		Player dead = event.getEntity();
		PlayerData deadData = plugin.getPlayerManager().getPlayerData(dead);

		deadData.setDeaths(deadData.getDeaths() + 1);
		deadData.save();

		Player killer = event.getEntity().getKiller();

		if (killer != null) {
			PlayerData killerData = plugin.getPlayerManager().getPlayerData(killer);
			killerData.setKills(killerData.getKills() + 1);
			killerData.save();
		}

		FileConfiguration config = FactionsPlugin.getInstance().getConfig();

		long duration = config.getLong("deathban-times.default");

		if(config.contains("deathban-times")) {
			for(String key : config.getConfigurationSection("deathban-times").getKeys(false)) {
				if(key.equalsIgnoreCase("default")) continue;

				if(dead.hasPermission(config.getString("deathban-times." + key + ".permission"))) {
					duration = config.getLong("deathban-times." + key + ".ticks");
				}

				break;
			}
		}

		FactionsPlugin.getInstance().getDeathBanManager().applyDeathBan(new DeathBan(dead, killer, dead.getLocation(), new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis() + duration)));
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);
		World.Environment environment = player.getLocation().getWorld().getEnvironment();

		player.getInventory().setArmorContents(new ItemStack[4]);
		player.getInventory().setContents(new ItemStack[36]);
		player.saveData();

		if (playerFaction != null) {
			Faction factionAt = plugin.getFactionManager().getFactionAt(player.getLocation());
			double dtrLoss = (environment == World.Environment.NETHER || environment == World.Environment.THE_END) ? 0.5 : (1.0D * factionAt.getDtrLossMultiplier());
			double newDtr = playerFaction.setDeathsUntilRaidable(playerFaction.getDeathsUntilRaidable() - dtrLoss);

			Role role = playerFaction.getMember(player.getUniqueId()).getRole();
			playerFaction.setRemainingRegenerationTime(BASE_REGEN_DELAY + (playerFaction.getOnlinePlayers().size() * TimeUnit.MINUTES.toMillis(2L)));
			playerFaction.broadcast(ChatColor.GOLD + "Member Death: " + ConfigurationService.TEAMMATE_COLOUR + role.getAstrix() + player.getName() + ChatColor.GOLD + ". " + "DTR: (" + ChatColor.WHITE + JavaUtils.format(newDtr, 2) + '/' + JavaUtils.format(playerFaction.getMaximumDeathsUntilRaidable(), 2) + ChatColor.GOLD + ").");
		}

		Integer balance = 0;

		if (plugin.getEconomyManager().getBalance(player.getUniqueId()) > 0 && player.getKiller() != null) {
			balance = plugin.getEconomyManager().getBalance(player.getUniqueId()) % 10;
			plugin.getEconomyManager().addBalance(player.getKiller().getUniqueId(), balance);
			plugin.getEconomyManager().subtractBalance(player.getUniqueId(), balance);
		}

		if (player.getKiller() != null && player.getKiller() != null) {
			player.getKiller().sendMessage(FactionsPlugin.PREFIX + ChatColor.YELLOW + "You earned " + ChatColor.RED + ChatColor.BOLD + "$250" + ChatColor.YELLOW + " for killing " + ChatColor.RED + player.getName() + "");
			plugin.getEconomyManager().addBalance(player.getKiller().getUniqueId(), 250);
		}

		if (Bukkit.spigot().getTPS()[0] > 15) {
			Location location = player.getLocation();
			WorldServer worldServer = ((CraftWorld) location.getWorld()).getHandle();

			EntityLightning entityLightning = new EntityLightning(worldServer, location.getX(), location.getY(), location.getZ(), false);
			PacketPlayOutSpawnEntityWeather packet = new PacketPlayOutSpawnEntityWeather(entityLightning);

			for (Player target : Bukkit.getOnlinePlayers()) {
				((CraftPlayer) target).getHandle().playerConnection.sendPacket(packet);
				target.playSound(target.getLocation(), Sound.AMBIENCE_THUNDER, 1.0F, 1.0F);
			}
		}
	}

}