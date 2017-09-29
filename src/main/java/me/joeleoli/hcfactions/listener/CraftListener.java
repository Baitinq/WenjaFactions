package me.joeleoli.hcfactions.listener;

import com.doctordark.util.InventoryUtils;
import me.joeleoli.hcfactions.FactionsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

public class CraftListener implements Listener {

	public CraftListener() {
		ItemStack melon = new ItemStack(Material.SPECKLED_MELON, 1);

		ShapelessRecipe precipe = new ShapelessRecipe(melon);
		precipe.addIngredient(Material.MELON);
		precipe.addIngredient(Material.GOLD_NUGGET);
		Bukkit.getServer().addRecipe(precipe);

		Bukkit.getPluginManager().registerEvents(this, FactionsPlugin.getInstance());
	}

	@EventHandler
	public void onCraft(PrepareItemCraftEvent e) {
		for(HumanEntity p : e.getViewers()) {
			if(p instanceof Player) {
				Material item = e.getRecipe().getResult().getType();

				if(e.getRecipe().getResult().getType() == item) {
					if(InventoryUtils.countAmount(e.getInventory(), Material.GOLD_NUGGET, (short) 0) > 1 && InventoryUtils.countAmount(e.getInventory(), Material.MELON, (short) 0) == 1) {
						e.getInventory().setResult(new ItemStack(Material.AIR));
						((Player)p).sendMessage(ChatColor.RED + "Wait! " + ChatColor.YELLOW + "You can craft glistering melons with only 1 golden nugget!");
					}
				}
			}
		}
	}

}