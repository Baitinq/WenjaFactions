package me.joeleoli.hcfactions.listener;

import me.joeleoli.hcfactions.FactionsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class FastBrewingListener implements Listener {

	public FastBrewingListener(FactionsPlugin plugin) {
		final ShapedRecipe cmelon = new ShapedRecipe(new ItemStack(Material.SPECKLED_MELON, 1));
		cmelon.shape(new String[] { "AAA", "CBA", "AAA" }).setIngredient('B', Material.MELON).setIngredient('C', Material.GOLD_NUGGET);
		Bukkit.getServer().addRecipe(cmelon);
	}

	private void startUpdate(final Furnace tile, final int increase) {
		new BukkitRunnable() {
			public void run() {
				if ((tile.getCookTime() > 0) || (tile.getBurnTime() > 0)) {
					tile.setCookTime((short) (tile.getCookTime() + increase));
					tile.update();
				} else {
					cancel();
				}
			}
		}.runTaskTimer(FactionsPlugin.getInstance(), 1L, 1L);
	}

	@EventHandler
	public void onFurnaceBurn(FurnaceBurnEvent event) {
		Random RND = new Random();
		startUpdate((Furnace) event.getBlock().getState(), RND.nextBoolean() ? 1 : 2);
	}

}