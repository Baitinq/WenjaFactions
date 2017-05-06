package me.joeleoli.hcfactions.timer.type;

import me.joeleoli.hcfactions.FactionsPlugin;
import me.joeleoli.hcfactions.timer.event.TimerExpireEvent;
import me.joeleoli.hcfactions.pvpclass.archer.ArcherClass;
import me.joeleoli.hcfactions.timer.PlayerTimer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ArcherTimer extends PlayerTimer implements Listener {
	private final FactionsPlugin plugin;
	private final Double ARCHER_DAMAGE;

	public ArcherTimer(FactionsPlugin plugin) {
		super("Archer Tag", TimeUnit.SECONDS.toMillis(6L));
		this.plugin = plugin;
		this.ARCHER_DAMAGE = Double.valueOf(0.15D);
	}

	public String getScoreboardPrefix() {
		return ChatColor.GOLD.toString() + ChatColor.BOLD;
	}

	@EventHandler
	public void onExpire(TimerExpireEvent e) {
		if ((e.getUserUUID().isPresent()) && (e.getTimer().equals(this))) {
			UUID userUUID = e.getUserUUID().get();
			Player player = Bukkit.getPlayer(userUUID);
			if (player == null) {
				return;
			}
			ArcherClass.TAGGED.remove(player.getUniqueId());
			for (Player players : Bukkit.getServer().getOnlinePlayers()) {
				this.plugin.getScoreboardHandler().getPlayerBoard(players.getUniqueId()).addUpdates(Bukkit.getServer().getOnlinePlayers());
			}
		}
	}

	@EventHandler
	public void onHit(EntityDamageByEntityEvent e) {
		if (((e.getEntity() instanceof Player)) && ((e.getDamager() instanceof Player))) {
			Player entity = (Player) e.getEntity();
			Entity damager = e.getDamager();

			if (getRemaining(entity) > 0L) {
				Double damage = Double.valueOf(e.getDamage() * this.ARCHER_DAMAGE.doubleValue());
				e.setDamage(e.getDamage() + damage.doubleValue());
			}
		}
		if (((e.getEntity() instanceof Player)) && ((e.getDamager() instanceof Arrow))) {
			Player entity = (Player) e.getEntity();
			Entity damager = (Player) ((Arrow) e.getDamager()).getShooter();

			if (((damager instanceof Player)) && (getRemaining(entity) > 0L)) {
				if (ArcherClass.TAGGED.get(entity.getUniqueId()).equals(damager.getUniqueId())) {
					setCooldown(entity, entity.getUniqueId());
				}

				Double damage = Double.valueOf(e.getDamage() * this.ARCHER_DAMAGE.doubleValue());
				e.setDamage(e.getDamage() + damage.doubleValue());
			}
		}
	}
}
