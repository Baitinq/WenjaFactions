package me.joeleoli.hcfactions.event.tracker;

import me.joeleoli.hcfactions.FactionsPlugin;
import me.joeleoli.hcfactions.event.CaptureZone;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import me.joeleoli.hcfactions.utils.DateTimeFormats;
import me.joeleoli.hcfactions.event.EventTimer;
import me.joeleoli.hcfactions.event.EventType;
import me.joeleoli.hcfactions.event.faction.EventFaction;
import me.joeleoli.hcfactions.event.faction.KothFaction;

import java.util.concurrent.TimeUnit;

@Deprecated
public class KothTracker implements EventTracker {

	public static final long DEFAULT_CAP_MILLIS = TimeUnit.MINUTES.toMillis(15L);
	/**
	 * Minimum time the KOTH has to be controlled before this tracker will announce when control has
	 * been lost.
	 */
	private static final long MINIMUM_CONTROL_TIME_ANNOUNCE = TimeUnit.SECONDS.toMillis(25L);

	private final FactionsPlugin plugin;

	public KothTracker(FactionsPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public EventType getEventType() {
		return EventType.KOTH;
	}

	@Override
	public void tick(EventTimer eventTimer, EventFaction eventFaction) {
		CaptureZone captureZone = ((KothFaction) eventFaction).getCaptureZone();
		captureZone.updateScoreboardRemaining();
		long remainingMillis = captureZone.getRemainingCaptureMillis();
		if (remainingMillis <= 0L) {
			plugin.getTimerManager().getEventTimer().handleWinner(captureZone.getCappingPlayer());
			eventTimer.clearCooldown();
			return;
		}

		if (remainingMillis == captureZone.getDefaultCaptureMillis()) return;

		int remainingSeconds = (int) (remainingMillis / 1000L);
		if (remainingSeconds > 0 && remainingSeconds % 30 == 0) {
			Bukkit.broadcastMessage(ChatColor.GOLD + "[" + eventFaction.getEventType().getDisplayName() + "] " + ChatColor.YELLOW + "Someone is controlling " + ChatColor.RED + captureZone.getDisplayName() + ChatColor.YELLOW + ". " + ChatColor.RED + '(' + DateTimeFormats.KOTH_FORMAT.format(remainingMillis) + ')');
		}
	}

	@Override
	public void onContest(EventFaction eventFaction, EventTimer eventTimer) {
		Bukkit.broadcastMessage(ChatColor.GOLD + "[" + eventFaction.getEventType().getDisplayName() + "] " + ChatColor.RED + eventFaction.getName() + ChatColor.YELLOW + " can now be contested. " + ChatColor.RED + '(' + DateTimeFormats.KOTH_FORMAT.format(eventTimer.getRemaining()) + ')');
	}

	@Override
	public boolean onControlTake(Player player, CaptureZone captureZone) {
		player.sendMessage(ChatColor.RED + "You are now in control of " + ChatColor.RED + captureZone.getDisplayName() + ChatColor.RED + '.');
		return true;
	}

	@Override
	public void onControlLoss(Player player, CaptureZone captureZone, EventFaction eventFaction) {
		player.sendMessage(ChatColor.RED + "You are no longer in control of " + ChatColor.RED + captureZone.getDisplayName() + ChatColor.DARK_AQUA + '.');

		// Only broadcast if the KOTH has been controlled for at least 25 seconds to prevent spam.
		long remainingMillis = captureZone.getRemainingCaptureMillis();
		if (remainingMillis > 0L && captureZone.getDefaultCaptureMillis() - remainingMillis > MINIMUM_CONTROL_TIME_ANNOUNCE) {
			Bukkit.broadcastMessage(ChatColor.GOLD + "[" + eventFaction.getEventType().getDisplayName() + "] " + ChatColor.RED + captureZone.getDisplayName() + ChatColor.YELLOW + " has been knocked!" + " (" + captureZone.getScoreboardRemaining() + ')');
		}
	}

	@Override
	public void stopTiming() {
	}
}
