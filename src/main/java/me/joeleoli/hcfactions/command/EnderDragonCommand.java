package me.joeleoli.hcfactions.command;

import com.doctordark.util.imagemessage.ImageChar;
import com.doctordark.util.imagemessage.ImageMessage;
import me.joeleoli.hcfactions.FactionsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;

public class EnderDragonCommand implements CommandExecutor, TabCompleter {

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if(player.getLocation().getWorld().getEnvironment() != World.Environment.THE_END) {
			sender.sendMessage(ChatColor.RED + "You must be in the end.");
			return true;
		}
		
		player.getWorld().spawnCreature(((Player) sender).getLocation(), EntityType.ENDER_DRAGON);
		
		for (Player on : Bukkit.getServer().getOnlinePlayers()) {
			for (int i = 0; i < 5; ++i) {
				on.sendMessage("");
			}
			
			try {
				BufferedImage imageToSend = ImageIO.read(FactionsPlugin.getInstance().getResource("enderdragon-art.png"));
				new ImageMessage(imageToSend, 15, ImageChar.BLOCK.getChar()).appendText("", "", "", "", "", "", ChatColor.RED + "[EnderDragon]", ChatColor.YELLOW + "Spawned").sendToPlayer(on);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			on.playSound(on.getLocation(), Sound.ENDERDRAGON_GROWL, 3.0f, 5.0f);
		}
		
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return Collections.emptyList();
	}
}
