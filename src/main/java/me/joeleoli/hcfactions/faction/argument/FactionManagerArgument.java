package me.joeleoli.hcfactions.faction.argument;

import com.doctordark.util.Color;
import com.doctordark.util.command.CommandArgument;
import me.joeleoli.hcfactions.FactionsPlugin;
import me.joeleoli.hcfactions.faction.type.Faction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import me.joeleoli.hcfactions.faction.struct.Role;
import me.joeleoli.hcfactions.faction.type.PlayerFaction;

import java.util.Arrays;

/**
 * Faction argument used to accept invitations from {@link Faction}s.
 */
public class FactionManagerArgument extends CommandArgument implements Listener {

	private final FactionsPlugin plugin;
	public Inventory factionManager;

	public FactionManagerArgument(FactionsPlugin plugin) {
		super("manage", "Manage your faction using a GUI");
		this.plugin = plugin;
	}

	@Override
	public String getUsage(String label) {
		return '/' + label;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = (Player) sender;
		PlayerFaction playerFaction = FactionsPlugin.getInstance().getFactionManager().getPlayerFaction(player);
		this.factionManager = Bukkit.createInventory(null, 36, "Faction Managment");

		if (playerFaction == null) {
			player.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "You don't have a faction");
			return true;
		}

		if (playerFaction.getMember(player).getRole() == Role.LEADER) {
			player.openInventory(factionManager);

			for (Player p : playerFaction.getOnlinePlayers()) {
				ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
				ItemMeta meta = skull.getItemMeta();
				meta.setLore((Arrays.asList(Color.translate("&eDo &6&lRIGHT CLICK &eto &6&lDEMOTE &ethis player."), Color.translate(("&eDo &6&lLEFT CLICK &eto &6&lPROMOTE&e this player.")), Color.translate("&eDo &6&lMIDDLE CLICK &eto make &6&lLEADER&e this player."))));
				meta.setDisplayName(p.getName());
				skull.setItemMeta(meta);
				factionManager.addItem(skull);
			}
		} else {
			player.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "You are not a leader.");
		}
		return false;
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		ItemStack clicked = event.getCurrentItem();
		Inventory inventory = event.getInventory();

		if (inventory.getName().equals("Faction Managment")) {

			if (clicked.getType() == Material.SKULL_ITEM) {
				if (event.getClick() == ClickType.LEFT) {
					Bukkit.dispatchCommand(player, "f promote " + clicked.getItemMeta().getDisplayName());
					event.setCancelled(true);
				}
				if (event.getClick() == ClickType.MIDDLE) {
					Bukkit.dispatchCommand(player, "f leader " + clicked.getItemMeta().getDisplayName());
					event.setCancelled(true);
				}
				if (event.getClick() == ClickType.RIGHT) {
					Bukkit.dispatchCommand(player, "f demote " + clicked.getItemMeta().getDisplayName());
					event.setCancelled(true);
				}
			}
		}
	}

}