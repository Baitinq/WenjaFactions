package me.joeleoli.hcfactions.listener;

import com.doctordark.util.BukkitUtils;
import me.joeleoli.hcfactions.FactionsPlugin;
import me.joeleoli.hcfactions.faction.struct.Raidable;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.joeleoli.hcfactions.faction.type.ClaimableFaction;
import me.joeleoli.hcfactions.faction.type.Faction;
import me.joeleoli.hcfactions.faction.type.PlayerFaction;
import me.joeleoli.hcfactions.faction.type.WarzoneFaction;

public class CoreListener implements Listener {

	private FactionsPlugin plugin;

	public CoreListener(FactionsPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (player.getWorld().getEnvironment() == World.Environment.NETHER && event.getBlock().getState() instanceof CreatureSpawner && !player.hasPermission(ProtectionListener.PROTECTION_BYPASS_PERMISSION)) {

			event.setCancelled(true);
			player.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "You may not break spawners in the nether.");
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (player.getWorld().getEnvironment() == World.Environment.NETHER && event.getBlock().getState() instanceof CreatureSpawner && !player.hasPermission(ProtectionListener.PROTECTION_BYPASS_PERMISSION)) {

			event.setCancelled(true);
			player.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "You may not place spawners in the nether.");
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);
		
		FactionsPlugin.getInstance().getLogManager().formatMessage("Player Login - IP: " + event.getPlayer().getAddress().getAddress().toString(), event.getPlayer().getName());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerKickEvent event) {
		event.setLeaveMessage(null);
	}

	@EventHandler
	void onTarg(EntityTargetEvent event) {
		if (event.getEntityType() == EntityType.CREEPER) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onStickyPistonExtend(BlockPistonExtendEvent event) {
		Block block = event.getBlock();
		Block targetBlock = block.getRelative(event.getDirection(), event.getLength() + 1);

		if (targetBlock.isEmpty() || targetBlock.isLiquid()) {
			Faction targetFaction = this.plugin.getFactionManager().getFactionAt(targetBlock.getLocation());

			if (targetFaction instanceof Raidable && !((Raidable) targetFaction).isRaidable() && !targetFaction.equals(this.plugin.getFactionManager().getFactionAt(block))) {
				event.setCancelled(true);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);

		Player player = event.getPlayer();
		plugin.getVisualiseHandler().clearVisualBlocks(player, null, null, false);
		plugin.getPlayerManager().getPlayerData(player).setShowClaimMap(false);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBlockBurn(BlockBurnEvent event) {
		Faction factionAt = this.plugin.getFactionManager().getFactionAt(event.getBlock().getLocation());
		if (factionAt instanceof WarzoneFaction || (factionAt instanceof Raidable && !((Raidable) factionAt).isRaidable())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBlockFade(BlockFadeEvent event) {
		Faction factionAt = this.plugin.getFactionManager().getFactionAt(event.getBlock().getLocation());
		if (factionAt instanceof ClaimableFaction && !(factionAt instanceof PlayerFaction)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onLeavesDelay(LeavesDecayEvent event) {
		Faction factionAt = this.plugin.getFactionManager().getFactionAt(event.getBlock().getLocation());
		if (factionAt instanceof ClaimableFaction && !(factionAt instanceof PlayerFaction)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBlockForm(BlockFormEvent event) {
		Faction factionAt = this.plugin.getFactionManager().getFactionAt(event.getBlock().getLocation());
		if (factionAt instanceof ClaimableFaction && !(factionAt instanceof PlayerFaction)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onEntityChangeBlock(EntityChangeBlockEvent event) {
		Entity entity = event.getEntity();

		if (entity instanceof LivingEntity && !attemptBuild(entity, event.getBlock().getLocation(), null)) {
			event.setCancelled(true);
		}
	}

	private boolean attemptBuild(Entity entity, Location location, Object object) {
		return false;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
		Entity remover = event.getRemover();

		if (remover instanceof Player && !attemptBuild(remover, event.getEntity().getLocation(), ChatColor.YELLOW + "You may not build in the territory of %1$s" + ChatColor.YELLOW + '.')) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onHangingPlace(HangingPlaceEvent event) {
		if (!attemptBuild(event.getPlayer(), event.getEntity().getLocation(), ChatColor.YELLOW + "You may not build in the territory of %1$s" + ChatColor.YELLOW + '.')) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onHangingDamageByEntity(EntityDamageByEntityEvent event) {
		Entity entity = event.getEntity();

		if (entity instanceof Hanging) {
			Player attacker = BukkitUtils.getFinalAttacker(event, false);

			if (!attemptBuild(attacker, entity.getLocation(), ChatColor.YELLOW + "You may not build in the territory of %1$s" + ChatColor.YELLOW + '.')) {
				event.setCancelled(true);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		plugin.getVisualiseHandler().clearVisualBlocks(player, null, null, false);
		plugin.getPlayerManager().getPlayerData(player).setShowClaimMap(false);
	}

}