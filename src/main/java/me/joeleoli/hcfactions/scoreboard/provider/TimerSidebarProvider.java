package me.joeleoli.hcfactions.scoreboard.provider;

import com.doctordark.util.BukkitUtils;
import com.doctordark.util.DurationFormatter;
import me.joeleoli.hcfactions.FactionsPlugin;
import me.joeleoli.hcfactions.event.eotw.EotwHandler;
import me.joeleoli.hcfactions.pvpclass.bard.BardClass;
import me.joeleoli.hcfactions.scoreboard.SidebarProvider;
import me.joeleoli.hcfactions.ConfigurationService;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import me.joeleoli.hcfactions.utils.DateTimeFormats;
import me.joeleoli.hcfactions.event.EventTimer;
import me.joeleoli.hcfactions.event.faction.ConquestFaction;
import me.joeleoli.hcfactions.event.faction.EventFaction;
import me.joeleoli.hcfactions.event.faction.KothFaction;
import me.joeleoli.hcfactions.event.tracker.ConquestTracker;
import me.joeleoli.hcfactions.faction.type.PlayerFaction;
import me.joeleoli.hcfactions.pvpclass.PvpClass;
import me.joeleoli.hcfactions.pvpclass.archer.ArcherClass;
import me.joeleoli.hcfactions.scoreboard.SidebarEntry;
import me.joeleoli.hcfactions.event.sotw.SotwTimer;
import me.joeleoli.hcfactions.timer.PlayerTimer;

import java.util.*;

public class TimerSidebarProvider implements SidebarProvider {

	protected static String STRAIGHT_LINE = BukkitUtils.STRAIGHT_LINE_DEFAULT.substring(0, 14);

	private FactionsPlugin plugin;

	public TimerSidebarProvider(FactionsPlugin plugin) {
		this.plugin = plugin;
	}

	private static String handleBardFormat(long millis) {
		return DateTimeFormats.REMAINING_SECONDS_TRAILING.get().format(millis * 0.001);
	}

	public SidebarEntry add(String s) {

		if (s.length() < 10) {
			return new SidebarEntry(s);
		}

		if (s.length() > 10 && s.length() < 20) {
			return new SidebarEntry(s.substring(0, 10), s.substring(10, s.length()), "");
		}

		if (s.length() > 20) {
			return new SidebarEntry(s.substring(0, 10), s.substring(10, 20), s.substring(20, s.length()));
		}

		return null;
	}

	@Override
	public String getTitle() {
		return ConfigurationService.SCOREBOARD_TITLE;
	}

	@Override
	public List<SidebarEntry> getLines(Player player) {
		List<SidebarEntry> lines = new ArrayList<>();

		EotwHandler.EotwRunnable eotwRunnable = plugin.getEotwHandler().getRunnable();

		if (eotwRunnable != null) {
			long remaining = eotwRunnable.getMillisUntilStarting();

			if (remaining > 0L) {
				lines.add(new SidebarEntry(ChatColor.RED.toString() + ChatColor.BOLD, "EOTW" + ChatColor.RED + " starts", " in " + ChatColor.BOLD + DurationFormatter.getRemaining(remaining, true)));
			} else if ((remaining = eotwRunnable.getMillisUntilCappable()) > 0L) {
				lines.add(new SidebarEntry(ChatColor.RED.toString() + ChatColor.BOLD, "EOTW" + ChatColor.RED + " cappable", " in " + ChatColor.BOLD + DurationFormatter.getRemaining(remaining, true)));
			}
		}

		SotwTimer.SotwRunnable sotwRunnable = plugin.getSotwTimer().getSotwRunnable();

		if (sotwRunnable != null) {
			lines.add(new SidebarEntry(ChatColor.GREEN.toString() + ChatColor.BOLD, "SOTW " + ChatColor.GREEN + "ends in ", ChatColor.BOLD + DurationFormatter.getRemaining(sotwRunnable.getRemaining(), true)));
		}

		EventTimer eventTimer = plugin.getTimerManager().getEventTimer();
		List<SidebarEntry> conquestLines = null;

		EventFaction eventFaction = eventTimer.getEventFaction();

		if (eventFaction instanceof KothFaction) {
			lines.add(new SidebarEntry(eventTimer.getScoreboardPrefix(), eventFaction.getScoreboardName() + ChatColor.GRAY, ": " + ChatColor.GOLD + DurationFormatter.getRemaining(eventTimer.getRemaining(), true)));
		} else if (eventFaction instanceof ConquestFaction) {
			ConquestFaction conquestFaction = (ConquestFaction) eventFaction;

			conquestLines = new ArrayList<>();
			conquestLines.add(new SidebarEntry(ChatColor.GOLD.toString(), ChatColor.BOLD + "Conquest Event", ""));
			conquestLines.add(new SidebarEntry(" " + ChatColor.RED.toString() + conquestFaction.getRed().getScoreboardRemaining(), ChatColor.GOLD + " | ", ChatColor.YELLOW.toString() + conquestFaction.getYellow().getScoreboardRemaining()));
			conquestLines.add(new SidebarEntry(" " + ChatColor.GREEN.toString() + conquestFaction.getGreen().getScoreboardRemaining(), ChatColor.GOLD + " | " + ChatColor.RESET, ChatColor.AQUA.toString() + conquestFaction.getBlue().getScoreboardRemaining()));

			ConquestTracker conquestTracker = (ConquestTracker) conquestFaction.getEventType().getEventTracker();
			int count = 0;

			for (Map.Entry<PlayerFaction, Integer> entry : conquestTracker.getFactionPointsMap().entrySet()) {
				String factionName = entry.getKey().getName();

				if (factionName.length() > 14) factionName = factionName.substring(0, 14);

				for (int i = 0; i < 3; i++) {
					conquestLines.add(new SidebarEntry(ChatColor.GOLD.toString() + ChatColor.BOLD + " " + count++ + ". ", ChatColor.YELLOW + factionName, ChatColor.GRAY + ": " + ChatColor.WHITE + entry.getValue()));
				}

				if (++count == 3) break;
			}
		}

		PvpClass pvpClass = plugin.getPvpClassManager().getEquippedClass(player);

		if (pvpClass != null) {
			if (pvpClass instanceof BardClass) {
				BardClass bardClass = (BardClass) pvpClass;
				lines.add(new SidebarEntry(ChatColor.AQUA + ChatColor.BOLD.toString(), "Bard Energy", ChatColor.GRAY + ": " + ChatColor.GOLD + handleBardFormat(bardClass.getEnergyMillis(player))));

				long remaining = bardClass.getRemainingBuffDelay(player);

				if (remaining > 0) {
					lines.add(new SidebarEntry(ChatColor.AQUA + ChatColor.BOLD.toString(), "Buff Cooldown", ChatColor.GRAY + ": " + ChatColor.GOLD + DurationFormatter.getRemaining(remaining, true)));
				}
			}

			if ((pvpClass instanceof ArcherClass)) {
				UUID uuid = player.getUniqueId();

				long timestamp = ArcherClass.archerSpeedCooldowns.get(uuid);
				long millis = System.currentTimeMillis();
				long remaining = timestamp == ArcherClass.archerSpeedCooldowns.getNoEntryValue() ? -1L : timestamp - millis;

				if (remaining > 0L) {
					lines.add(new SidebarEntry(ChatColor.YELLOW + ChatColor.BOLD.toString(), "Delay", ChatColor.GRAY + ": " + ChatColor.GOLD + DurationFormatter.getRemaining(remaining, true)));
				}
			}
		}

		Collection<me.joeleoli.hcfactions.timer.Timer> timers = plugin.getTimerManager().getTimers();

		for (me.joeleoli.hcfactions.timer.Timer timer : timers) {
			if (timer instanceof PlayerTimer) {
				PlayerTimer playerTimer = (PlayerTimer) timer;
				long remaining = playerTimer.getRemaining(player);

				if (remaining <= 0) continue;

				String timerName = playerTimer.getName();
				if (timerName.length() > 14) timerName = timerName.substring(0, timerName.length());
				lines.add(new SidebarEntry(playerTimer.getScoreboardPrefix(), timerName + ChatColor.GRAY, ": " + ChatColor.GOLD + DurationFormatter.getRemaining(remaining, true)));
			}
		}

		if (conquestLines != null && !conquestLines.isEmpty()) {
			if (!lines.isEmpty()) {
				conquestLines.add(new SidebarEntry("", "", ""));
			}

			conquestLines.addAll(lines);
			lines = conquestLines;
		}
		if (!lines.isEmpty()) {
			lines.add(0, new SidebarEntry(ChatColor.GRAY, STRAIGHT_LINE, STRAIGHT_LINE));
			lines.add(lines.size(), new SidebarEntry(ChatColor.GRAY, ChatColor.STRIKETHROUGH + STRAIGHT_LINE, STRAIGHT_LINE));
		}

		return lines;
	}
}
