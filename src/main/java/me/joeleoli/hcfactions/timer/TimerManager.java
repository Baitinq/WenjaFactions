package me.joeleoli.hcfactions.timer;

import com.doctordark.util.Config;
import lombok.Data;
import lombok.Getter;
import me.joeleoli.hcfactions.FactionsPlugin;
import me.joeleoli.hcfactions.timer.type.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import me.joeleoli.hcfactions.event.EventTimer;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class TimerManager implements Listener {

	@Getter public CombatTimer combatTimer;
	@Getter public GappleTimer gappleTimer;
	@Getter public ArcherTimer archerTimer;
	@Getter public TeleportTimer teleportTimer;
	@Getter private LogoutTimer logoutTimer;
	@Getter private EnderPearlTimer enderPearlTimer;
	@Getter private EventTimer eventTimer;
	@Getter private AppleTimer appleTimer;
	@Getter private InvincibilityTimer invincibilityTimer;
	@Getter private ClassLoad pvpClassWarmupTimer;
	@Getter private StuckTimer stuckTimer;
	@Getter private Set<Timer> timers = new LinkedHashSet<>();

	private JavaPlugin plugin;
	private Config config;

	public TimerManager(FactionsPlugin plugin) {
		(this.plugin = plugin).getServer().getPluginManager().registerEvents(this, plugin);
		this.registerTimer(this.enderPearlTimer = new EnderPearlTimer(plugin));
		this.registerTimer(this.logoutTimer = new LogoutTimer());
		this.registerTimer(this.gappleTimer = new GappleTimer(plugin));
		this.registerTimer(this.stuckTimer = new StuckTimer());
		this.registerTimer(this.invincibilityTimer = new InvincibilityTimer(plugin));
		this.registerTimer(this.combatTimer = new CombatTimer(plugin));
		this.registerTimer(this.teleportTimer = new TeleportTimer(plugin));
		this.registerTimer(this.eventTimer = new EventTimer(plugin));
		this.registerTimer(this.pvpClassWarmupTimer = new ClassLoad(plugin));
		this.registerTimer(this.archerTimer = new ArcherTimer(plugin));
		this.registerTimer(this.appleTimer = new AppleTimer(plugin));
		this.reloadTimerData();
	}

	public void registerTimer(Timer timer) {
		this.timers.add(timer);

		if (timer instanceof Listener) {
			this.plugin.getServer().getPluginManager().registerEvents((Listener) timer, this.plugin);
		}
	}

	public void unregisterTimer(Timer timer) {
		this.timers.remove(timer);
	}

	/**
	 * Reloads the {@link Timer} data from storage.
	 */
	public void reloadTimerData() {
		this.config = new Config(plugin, "timers");
		for (Timer timer : this.timers) {
			timer.load(this.config);
		}
	}

	/**
	 * Saves the {@link Timer} data to storage.
	 */
	public void saveTimerData() {
		for (Timer timer : this.timers) {
			timer.onDisable(this.config);
		}

		this.config.save();
	}

}