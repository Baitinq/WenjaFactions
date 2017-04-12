package me.joeleoli.hcfactions.faction.argument;

import com.doctordark.util.BukkitUtils;
import com.doctordark.util.JavaUtils;
import com.doctordark.util.command.CommandArgument;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import me.joeleoli.hcfactions.faction.FactionExecutor;
import me.joeleoli.hcfactions.faction.type.Faction;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.joeleoli.hcfactions.FactionsPlugin;

/**
 * Faction argument used to show help on how to use {@link Faction}s.
 */
public class FactionHelpArgument extends CommandArgument {

	private static final int HELP_PER_PAGE = 8;
	private final FactionExecutor executor;
	private ImmutableMultimap<Integer, String> pages;

	public FactionHelpArgument(FactionExecutor executor) {
		super("help", "View help on how to use factions.");
		this.executor = executor;
	}

	@Override
	public String getUsage(String label) {
		return '/' + label + ' ' + getName();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 2) {
			showPage(sender, label, 1);
			return true;
		}

		Integer page = JavaUtils.tryParseInt(args[1]);

		if (page == null) {
			sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid number.");
			return true;
		}

		showPage(sender, label, page);
		return true;
	}

	private void showPage(CommandSender sender, String label, int pageNumber) {
		if (pages == null) {
			boolean isPlayer = sender instanceof Player;
			int val = 1;
			int count = 0;
			Multimap<Integer, String> pages = ArrayListMultimap.create();

			for (CommandArgument argument : executor.getArguments()) {
				if (argument == this) continue;

				String permission = argument.getPermission();

				if (permission != null && !sender.hasPermission(permission)) continue;
				if (argument.isPlayerOnly() && !isPlayer) continue;

				count++;
				pages.get(val).add(ChatColor.YELLOW + "/" + label + ' ' + argument.getName() + ChatColor.GOLD + " - " + ChatColor.GRAY + argument.getDescription());

				if (count % HELP_PER_PAGE == 0) {
					val++;
				}
			}

			this.pages = ImmutableMultimap.copyOf(pages);
		}

		int totalPageCount = (pages.size() / HELP_PER_PAGE) + 1;

		if (pageNumber < 1) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "You cannot view a page less than 1.");
			return;
		}

		if (pageNumber > totalPageCount) {
			sender.sendMessage(FactionsPlugin.PREFIX + ChatColor.RED + "There are only " + totalPageCount + " pages.");
			return;
		}

		sender.sendMessage(ChatColor.GOLD + BukkitUtils.STRAIGHT_LINE_DEFAULT);
		sender.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + " FACTION HELP " + ChatColor.GOLD + "- " + ChatColor.GRAY + "(Page " + pageNumber + '/' + totalPageCount + ')');
		for (String message : pages.get(pageNumber)) {
			sender.sendMessage("  " + message);
		}

		sender.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + " * " + ChatColor.YELLOW + "You are currently on " + ChatColor.GRAY + "Page " + pageNumber + '/' + totalPageCount + ChatColor.GOLD + '.');
		sender.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + " * " + ChatColor.YELLOW + "To view other pages, use " + ChatColor.GRAY + '/' + label + ' ' + getName() + " <page#>" + ChatColor.YELLOW + '.');
		sender.sendMessage(ChatColor.GOLD + BukkitUtils.STRAIGHT_LINE_DEFAULT);
	}

}