package me.joeleoli.hcfactions.command;

import me.joeleoli.hcfactions.FactionsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.joeleoli.hcfactions.player.PlayerData;
import me.joeleoli.hcfactions.utils.MessageUtils;

import java.util.Collections;
import java.util.List;

public class SaveDataCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to execute this command.");
            return true;
        }
        
        MessageUtils.sendMessageToStaff(ChatColor.GRAY + "[" + ChatColor.RED + "!" + ChatColor.GRAY + "] " + ChatColor.GREEN + "" + ChatColor.BOLD + "SAVE " + ChatColor.RESET + "" + ChatColor.GRAY + "This is for joeleoli, don't worry about it.");
        MessageUtils.sendMessageToStaff(ChatColor.GRAY + "[" + ChatColor.RED + "!" + ChatColor.GRAY + "] " + ChatColor.GREEN + "" + ChatColor.BOLD + "SAVE " + ChatColor.RESET + "" + ChatColor.GREEN + "Starting task...");

        for(PlayerData playerData : FactionsPlugin.getInstance().getPlayerManager().getAllData().values()) {
            try {
                playerData.save();
                MessageUtils.sendMessageToStaff(ChatColor.GREEN + "* Done with player data of " + playerData.getPlayer().getName());
            }
            catch (Exception e) {
                MessageUtils.sendMessageToStaff(ChatColor.RED + "* Failed with player data of " + playerData.getPlayer().getName());
            }
        }

        MessageUtils.sendMessageToStaff(ChatColor.GRAY + "[" + ChatColor.RED + "!" + ChatColor.GRAY + "] " + ChatColor.GREEN + "" + ChatColor.BOLD + "SAVE " + ChatColor.RESET + "" + ChatColor.GREEN + "Finished task...");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return Collections.emptyList();
    }

}