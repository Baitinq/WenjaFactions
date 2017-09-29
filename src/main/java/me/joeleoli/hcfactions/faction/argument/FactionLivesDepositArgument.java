package me.joeleoli.hcfactions.faction.argument;

import com.doctordark.util.command.CommandArgument;
import me.joeleoli.hcfactions.ConfigurationService;
import me.joeleoli.hcfactions.FactionsPlugin;
import me.joeleoli.hcfactions.faction.FactionMember;
import me.joeleoli.hcfactions.faction.type.PlayerFaction;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactionLivesDepositArgument extends CommandArgument {

	private final FactionsPlugin plugin;

	public FactionLivesDepositArgument(FactionsPlugin plugin) {
		super("depositlives", "Deposit lives into your faction");
		this.plugin = plugin;
	}

	public static boolean isInteger(String s) {
		return isInteger(s, 10);
	}

	public static boolean isInteger(String s, int radix) {
		if (s.isEmpty()) return false;
		for (int i = 0; i < s.length(); i++) {
			if (i == 0 && s.charAt(i) == '-') {
				if (s.length() == 1) return false;
				else continue;
			}
			if (Character.digit(s.charAt(i), radix) < 0) return false;
		}
		return true;
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
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "Invalid usage: /f depositlives <amount>");
			return true;
		}

		if (!isInteger(args[1])) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "Lives must be a number!");
			return true;
		}

		int amount = Integer.parseInt(args[1]);
		Player player = (Player) sender;
		PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);

		if (playerFaction == null) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "You are not in a faction.");
			return true;
		}

		if (playerFaction.isRaidable() && !plugin.getEotwHandler().isEndOfTheWorld()) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "You cannot deposit lives into your faction when raidable.");
			return true;
		}

		int currentLives = plugin.getPlayerManager().getPlayerData(player).getLives();

		if (currentLives <= 0) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "You do not have enough lives to deposit.");
			return true;
		}

		if (currentLives - amount < 0) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "Your lives after this transaction would be less than 0.");
			return true;
		}

		FactionMember factionMember = playerFaction.getMember(player);

		playerFaction.setLives(playerFaction.getLives() + amount);
		plugin.getPlayerManager().getPlayerData(player).setLives(plugin.getPlayerManager().getPlayerData(player).getLives() - amount);
		sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.GREEN + "You have deposited " + amount + " live(s) into the faction.");
		playerFaction.broadcast(FactionsPlugin.PREFIX + ConfigurationService.TEAMMATE_COLOUR + sender.getName() + ChatColor.YELLOW + " has deposited " + ChatColor.GRAY + amount + " live(s) " + ChatColor.YELLOW + "into the faction.");
		return true;
	}
}
