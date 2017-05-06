package com.doctordark.util.itemdb;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface ItemDb {

	void reloadItemDatabase();

	ItemStack getPotion(String p0);

	ItemStack getPotion(String p0, int p1);

	ItemStack getItem(String p0);

	ItemStack getItem(String p0, int p1);

	String getName(ItemStack p0);

	@Deprecated
	String getPrimaryName(ItemStack p0);

	String getNames(ItemStack p0);

	List<ItemStack> getMatching(Player p0, String[] p1);

}