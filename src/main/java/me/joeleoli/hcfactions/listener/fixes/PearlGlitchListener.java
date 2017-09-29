package me.joeleoli.hcfactions.listener.fixes;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import me.joeleoli.hcfactions.FactionsPlugin;
import me.joeleoli.hcfactions.faction.type.ClaimableFaction;
import me.joeleoli.hcfactions.faction.type.Faction;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.InventoryHolder;

public class PearlGlitchListener implements Listener {

	private ImmutableSet<Material> blockedPearlTypes = Sets.immutableEnumSet(Material.THIN_GLASS, Material.IRON_FENCE, Material.FENCE, Material.NETHER_FENCE, Material.FENCE_GATE, Material.ACACIA_STAIRS, Material.BIRCH_WOOD_STAIRS, Material.BRICK_STAIRS, Material.COBBLESTONE_STAIRS, Material.DARK_OAK_STAIRS, Material.JUNGLE_WOOD_STAIRS, Material.NETHER_BRICK_STAIRS, Material.QUARTZ_STAIRS, Material.SANDSTONE_STAIRS, Material.SMOOTH_STAIRS, Material.SPRUCE_WOOD_STAIRS, Material.WOOD_STAIRS);

	private FactionsPlugin plugin;

	public PearlGlitchListener(FactionsPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.hasItem() && event.getItem().getType() == Material.ENDER_PEARL) {
			Block block = event.getClickedBlock();
			
			if (block.getType().isSolid() && !(block.getState() instanceof InventoryHolder)) {
				Faction factionAt = FactionsPlugin.getInstance().getFactionManager().getFactionAt(block.getLocation());
				
				if (!(factionAt instanceof ClaimableFaction)) {
					return;
				}

				event.setCancelled(true);
				Player player = event.getPlayer();
				player.setItemInHand(event.getItem());
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPearlClip(PlayerTeleportEvent event) {
		if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
			Location to = event.getTo();
			
			if (blockedPearlTypes.contains(to.getBlock().getType())) {
				Player player = event.getPlayer();
				player.sendMessage(ChatColor.YELLOW + "You have been detected trying to pearl glitch, your pearl has been refunded.");
				plugin.getTimerManager().getEnderPearlTimer().refund(player);

				event.setCancelled(true);
				return;
			}

			to.setX(to.getBlockX() + 0.5);
			to.setZ(to.getBlockZ() + 0.5);
			event.setTo(to);
		}
	}
	
}