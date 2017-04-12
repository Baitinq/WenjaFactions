package me.joeleoli.hcfactions.faction.argument;

import com.doctordark.internal.com.doctordark.base.BaseConstants;
import com.doctordark.util.command.CommandArgument;
import me.joeleoli.hcfactions.ConfigurationService;
import me.joeleoli.hcfactions.FactionsPlugin;
import me.joeleoli.hcfactions.faction.FactionMember;
import me.joeleoli.hcfactions.faction.type.PlayerFaction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.joeleoli.hcfactions.deathban.DeathBan;
import me.joeleoli.hcfactions.faction.struct.Role;

import java.util.UUID;

public class FactionReviveArgument extends CommandArgument {

	private FactionsPlugin plugin;

	public FactionReviveArgument(FactionsPlugin plugin) {
		super("revive", "Revive a player using faction lives.");
		this.plugin = plugin;
	}

	@Override
	public String getUsage(String label) {
		return '/' + label + ' ' + getName();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "This command is only executable by players.");
			return true;
		}

		if (args.length < 2) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "Invalid usage: /f revive <player>");
			return true;
		}

		OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
		if (!target.hasPlayedBefore() && !target.isOnline()) {
			sender.sendMessage(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND);
			return true;
		}

		Player player = (Player) sender;
		PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);

		if (playerFaction == null) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "You are not in a faction.");
			return true;
		}

		FactionMember factionMember = playerFaction.getMember(player);

		if (factionMember.getRole() == Role.MEMBER) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "You must be a faction officer to revive players.");
			return true;
		}

		if (playerFaction.getMember(target.getName()) == null) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "That player is not in your faction!");
			return true;
		}

		if (playerFaction.getLives() <= 0) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "Your faction doesn't have enough lives to revive that member!");
			return true;
		}

		if (playerFaction.getLives() - 1 < 0) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "Your faction doesn't have enough lives to revive that member!");
			return true;
		}

		UUID targetUUID = target.getUniqueId();

		if(FactionsPlugin.getInstance().getDeathBanManager().getDeathBans().containsKey(targetUUID)) {
			DeathBan deathBan = FactionsPlugin.getInstance().getDeathBanManager().getDeathBans().get(targetUUID);

			if (!(System.currentTimeMillis() - deathBan.getExpire().getTime() >= 0)) {
				FactionsPlugin.getInstance().getDeathBanManager().removeDeathBan(targetUUID);
			} else {
				if (sender instanceof Player) {
					playerFaction.setLives(playerFaction.getLives() - 1);
					playerFaction.broadcast(FactionsPlugin.PREFIX + ConfigurationService.TEAMMATE_COLOUR + factionMember.getRole().getAstrix() + sender.getName() + ChatColor.RED + " has revived " + target.getName() + " using 1 faction life.");
				}

				FactionsPlugin.getInstance().getDeathBanManager().removeDeathBan(targetUUID);
			}
		} else {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + target.getName() + " is not death-banned.");
		}

		return true;
	}

}