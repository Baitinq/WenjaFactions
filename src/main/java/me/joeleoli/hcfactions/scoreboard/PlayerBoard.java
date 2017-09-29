package me.joeleoli.hcfactions.scoreboard;

import me.joeleoli.hcfactions.ConfigurationService;
import me.joeleoli.hcfactions.FactionsPlugin;
import me.joeleoli.hcfactions.faction.type.PlayerFaction;
import me.joeleoli.hcfactions.pvpclass.archer.ArcherClass;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerBoard {

	private final AtomicBoolean removed = new AtomicBoolean(false);
	private final Team members;
	private final Team neutrals;
	private final Team allies;
	private final Team archers;
	private final BufferedObjective bufferedObjective;
	private final Scoreboard scoreboard;
	private final Player player;
	private final FactionsPlugin plugin;
	private boolean sidebarVisible = false;
	private SidebarProvider defaultProvider;
	private SidebarProvider temporaryProvider;
	private BukkitRunnable runnable;

	public PlayerBoard(FactionsPlugin plugin, Player player) {
		this.plugin = plugin;
		this.player = player;

		this.scoreboard = plugin.getServer().getScoreboardManager().getNewScoreboard();
		this.bufferedObjective = new BufferedObjective(scoreboard);

		this.members = scoreboard.registerNewTeam("members");
		this.members.setPrefix(ConfigurationService.TEAMMATE_COLOUR.toString());
		this.members.setCanSeeFriendlyInvisibles(true);

		this.neutrals = scoreboard.registerNewTeam("neutrals");
		this.neutrals.setPrefix(ConfigurationService.ENEMY_COLOUR.toString());

		(this.archers = this.scoreboard.registerNewTeam("archers")).setPrefix(ChatColor.DARK_RED.toString());

		this.allies = scoreboard.registerNewTeam("enemies");
		this.allies.setPrefix(ConfigurationService.ALLY_COLOUR.toString());

		//  this.archerTagged = this.scoreboard.registerNewTeam("archerTagged").setPrefix(ChatColor.DARK_RED.toString());

		player.setScoreboard(scoreboard);
	}

	/**
	 * Removes this {@link PlayerBoard}.
	 */
	public void remove() {
		if(!this.removed.getAndSet(true) && scoreboard != null) {
			for (Team team : scoreboard.getTeams()) {
				team.unregister();
			}

			for (Objective objective : scoreboard.getObjectives()) {
				objective.unregister();
			}
		}
	}

	public Player getPlayer() {
		return this.player;
	}

	public Scoreboard getScoreboard() {
		return this.scoreboard;
	}

	public boolean isSidebarVisible() {
		return this.sidebarVisible;
	}

	public void setSidebarVisible(boolean visible) {
		this.sidebarVisible = visible;
		this.bufferedObjective.setDisplaySlot(visible ? DisplaySlot.SIDEBAR : null);
	}

	public void setDefaultSidebar(final SidebarProvider provider, long updateInterval) {
		if(provider != this.defaultProvider) {
			this.defaultProvider = provider;
			if(this.runnable != null) {
				this.runnable.cancel();
			}

			if(provider == null) {
				this.scoreboard.clearSlot(DisplaySlot.SIDEBAR);
				return;
			}

			(this.runnable = new BukkitRunnable() {
				@Override
				public void run() {
					if(removed.get()) {
						cancel();
						return;
					}

					if(provider == defaultProvider) {
						updateObjective();
					}
				}
			}).runTaskTimerAsynchronously(plugin, updateInterval, updateInterval);
		}
	}

	public void setTemporarySidebar(final SidebarProvider provider, final long expiration) {
		if(this.removed.get()) {
			throw new IllegalStateException("Cannot update whilst board is removed");
		}

		this.temporaryProvider = provider;
		this.updateObjective();
		new BukkitRunnable() {
			@Override
			public void run() {
				if(removed.get()) {
					cancel();
					return;
				}

				if(temporaryProvider == provider) {
					temporaryProvider = null;
					updateObjective();
				}
			}
		}.runTaskLaterAsynchronously(plugin, expiration);
	}

	private void updateObjective() {
		if(this.removed.get()) {
			throw new IllegalStateException("Cannot update whilst board is removed");
		}

		SidebarProvider provider = this.temporaryProvider != null ? this.temporaryProvider : this.defaultProvider;
		if(provider == null) {
			this.bufferedObjective.setVisible(false);
		} else {
			this.bufferedObjective.setTitle(provider.getTitle());
			this.bufferedObjective.setAllLines(provider.getLines(player));
			this.bufferedObjective.flip();
		}
	}

	public void addUpdate(Player target) {
		this.addUpdates(Collections.singleton(target));
	}

	public void addUpdates(Iterable<? extends Player> updates) {
		if(this.removed.get()) {
			throw new IllegalStateException("Cannot update whilst board is removed");
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				PlayerFaction playerFaction = null;
				boolean hasRun = false;
				for (Player update : updates) {
					if(members.hasPlayer(update)) members.removePlayer(update);
					if(neutrals.hasPlayer(update)) neutrals.removePlayer(update);
					if(allies.hasPlayer(update)) allies.removePlayer(update);
					if(archers.hasPlayer(update)) archers.removePlayer(update);

					if(player.equals(update)) {
						if(ArcherClass.TAGGED.containsKey(update.getUniqueId())) {
							archers.addPlayer(update);
						}

						members.addPlayer(update);
					} else {
						if(!hasRun) {
							playerFaction = plugin.getFactionManager().getPlayerFaction(player);
							hasRun = true;
						}

						if(ArcherClass.TAGGED.containsKey(update.getUniqueId())) {
							archers.addPlayer(update);
						} else {
							PlayerFaction targetFaction;

							if((playerFaction == null) || ((targetFaction = plugin.getFactionManager().getPlayerFaction(update)) == null)) {
								neutrals.addPlayer(update);
							} else {
								if(playerFaction.equals(targetFaction)) {
									members.addPlayer(update);
								} else if(playerFaction.getAllied().contains(targetFaction.getUniqueID())) {
									allies.addPlayer(update);
								} else {
									neutrals.addPlayer(update);
								}
							}
						}
					}
				}
			}
		}.runTaskAsynchronously(this.plugin);
	}
}
