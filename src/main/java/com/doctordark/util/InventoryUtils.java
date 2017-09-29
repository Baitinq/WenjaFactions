package com.doctordark.util;

import com.google.common.base.Preconditions;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Map;
import java.util.Set;

public class InventoryUtils {

	public static int DEFAULT_INVENTORY_WIDTH = 9;
	public static int MINIMUM_INVENTORY_HEIGHT = 1;
	public static int MINIMUM_INVENTORY_SIZE = 9;
	public static int MAXIMUM_INVENTORY_HEIGHT = 6;
	public static int MAXIMUM_INVENTORY_SIZE = 54;
	public static int MAXIMUM_SINGLE_CHEST_SIZE = 27;
	public static int MAXIMUM_DOUBLE_CHEST_SIZE = 54;

	public static ItemStack[] deepClone(ItemStack[] origin) {
		Preconditions.checkNotNull((Object) origin, "Origin cannot be null");
		ItemStack[] cloned = new ItemStack[origin.length];
		for (int i = 0; i < origin.length; ++i) {
			ItemStack next = origin[i];
			cloned[i] = ((next == null) ? null : next.clone());
		}
		return cloned;
	}

	public static int getSafestInventorySize(int initialSize) {
		return (initialSize + 8) / 9 * 9;
	}

	public static void removeItem(Inventory inventory, Material type, short data, int quantity) {
		ItemStack[] contents = inventory.getContents();
		boolean compareDamage = type.getMaxDurability() == 0;
		for (int i = quantity; i > 0; --i) {
			for (ItemStack content : contents) {
				if (content != null) {
					if (content.getType() == type) {
						if (!compareDamage || content.getData().getData() == data) {
							if (content.getAmount() <= 1) {
								inventory.removeItem(content);
								break;
							}
							content.setAmount(content.getAmount() - 1);
							break;
						}
					}
				}
			}
		}
	}

	public static int countAmount(Inventory inventory, Material type, short data) {
		ItemStack[] contents = inventory.getContents();
		boolean compareDamage = type.getMaxDurability() == 0;
		int counter = 0;
		for (ItemStack item : contents) {
			if (item != null) {
				if (item.getType() == type) {
					if (!compareDamage || item.getData().getData() == data) {
						counter += item.getAmount();
					}
				}
			}
		}
		return counter;
	}

	public static boolean isEmpty(Inventory inventory) {
		return isEmpty(inventory, true);
	}

	public static boolean isEmpty(Inventory inventory, boolean checkArmour) {
		boolean result = true;
		ItemStack[] contents2;
		ItemStack[] contents = contents2 = inventory.getContents();
		for (ItemStack content : contents2) {
			if (content != null && content.getType() != Material.AIR) {
				result = false;
				break;
			}
		}
		if (!result) {
			return false;
		}
		if (checkArmour && inventory instanceof PlayerInventory) {
			ItemStack[] armorContents;
			contents = (armorContents = ((PlayerInventory) inventory).getArmorContents());
			for (ItemStack content : armorContents) {
				if (content != null && content.getType() != Material.AIR) {
					result = false;
					break;
				}
			}
		}
		return result;
	}

	public static boolean clickedTopInventory(InventoryDragEvent event) {
		InventoryView view = event.getView();
		Inventory topInventory = view.getTopInventory();
		if (topInventory == null) {
			return false;
		}
		boolean result = false;
		Set<Map.Entry<Integer, ItemStack>> entrySet = event.getNewItems().entrySet();
		int size = topInventory.getSize();
		for (Map.Entry<Integer, ItemStack> entry : entrySet) {
			if (entry.getKey() < size) {
				result = true;
				break;
			}
		}
		return result;
	}

}