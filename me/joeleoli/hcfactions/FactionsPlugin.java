package me.joeleoli.hcfactions;

import com.doctordark.internal.com.doctordark.base.BasePlugin;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import lombok.Getter;
import me.joeleoli.hcfactions.combatlog.CombatLogListener;
import me.joeleoli.hcfactions.economy.*;
import me.joeleoli.hcfactions.event.CaptureZone;
import me.joeleoli.hcfactions.event.EventExecutor;
import me.joeleoli.hcfactions.event.EventScheduler;
import me.joeleoli.hcfactions.event.crate.KeyManager;
import me.joeleoli.hcfactions.event.eotw.EotwCommand;
import me.joeleoli.hcfactions.event.eotw.EotwHandler;
import me.joeleoli.hcfactions.event.koth.KothExecutor;
import me.joeleoli.hcfactions.faction.FactionExecutor;
import me.joeleoli.hcfactions.faction.FactionManager;
import me.joeleoli.hcfactions.faction.FactionMember;
import me.joeleoli.hcfactions.faction.FlatFileFactionManager;
import me.joeleoli.hcfactions.faction.claim.Claim;
import me.joeleoli.hcfactions.faction.claim.ClaimHandler;
import me.joeleoli.hcfactions.faction.claim.Subclaim;
import me.joeleoli.hcfactions.faction.type.*;
import me.joeleoli.hcfactions.event.sotw.SotwCommand;
import me.joeleoli.hcfactions.timer.Cooldowns;
import me.joeleoli.hcfactions.timer.TimerExecutor;
import me.joeleoli.hcfactions.timer.TimerManager;
import me.joeleoli.hcfactions.visualise.ProtocolLibHook;
import me.joeleoli.hcfactions.visualise.WallBorderListener;
import me.joeleoli.hcfactions.command.*;
import me.joeleoli.hcfactions.listener.*;
import me.joeleoli.hcfactions.listener.fixes.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.tyrannyofheaven.bukkit.zPermissions.ZPermissionsService;
import me.joeleoli.hcfactions.combatlog.CustomEntityRegistration;
import me.joeleoli.hcfactions.event.conquest.ConquestExecutor;
import me.joeleoli.hcfactions.event.crate.KeyListener;
import me.joeleoli.hcfactions.event.eotw.EotwListener;
import me.joeleoli.hcfactions.event.faction.CapturableFaction;
import me.joeleoli.hcfactions.event.faction.ConquestFaction;
import me.joeleoli.hcfactions.event.faction.KothFaction;
import me.joeleoli.hcfactions.faction.argument.FactionClaimChunkArgument;
import me.joeleoli.hcfactions.faction.argument.FactionManagerArgument;
import me.joeleoli.hcfactions.faction.claim.ClaimWandListener;
import me.joeleoli.hcfactions.pvpclass.PvpClassManager;
import me.joeleoli.hcfactions.pvpclass.bard.EffectRestorer;
import me.joeleoli.hcfactions.scoreboard.ScoreboardHandler;
import me.joeleoli.hcfactions.event.sotw.SotwListener;
import me.joeleoli.hcfactions.event.sotw.SotwTimer;
import me.joeleoli.hcfactions.visualise.VisualiseHandler;
import me.joeleoli.hcfactions.listener.EventSignListener;
import me.joeleoli.hcfactions.command.MapKitCommand;
import me.joeleoli.hcfactions.deathban.DeathBanManager;
import me.joeleoli.hcfactions.player.PlayerManager;
import me.joeleoli.hcfactions.utils.LogManager;

import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class FactionsPlugin extends JavaPlugin {

	@Getter public static FactionsPlugin instance;
	@Getter public ZPermissionsService permissionsService;
	@Getter public PlayTimeManager playTimeManager;
	@Getter public ClaimHandler claimHandler;
	@Getter public EconomyManager economyManager;
	@Getter public DeathBanManager deathBanManager;
	@Getter public EffectRestorer effectRestorer;
	@Getter public EotwHandler eotwHandler;
	@Getter public SotwTimer sotwTimer;
	@Getter public EventScheduler eventScheduler;
	@Getter public FactionManager factionManager;
	@Getter public KeyManager keyManager;
	@Getter public PvpClassManager pvpClassManager;
	@Getter public ScoreboardHandler scoreboardHandler;
	@Getter public TimerManager timerManager;
	@Getter public PlayerManager playerManager;
	@Getter public VisualiseHandler visualiseHandler;
	@Getter public WorldEditPlugin worldEdit;
	@Getter public LogManager logManager;

	public static String PREFIX = ChatColor.GOLD + "" + ChatColor.BOLD + "WENJA" + ChatColor.GRAY + " » ";

	@Override
	public void onEnable() {
		instance = this;

		TimeZone.setDefault(TimeZone.getTimeZone("US/Eastern"));

		BasePlugin.getPlugin().init(this);
		Cooldowns.createCooldown("friend_cooldown");

		ProtocolLibHook.hook(this);
		CustomEntityRegistration.registerCustomEntities();

		try {
			permissionsService = Bukkit.getServicesManager().load(ZPermissionsService.class);
		} catch (NoClassDefFoundError e) {
			getLogger().severe("Could not find zPermissions-1.3-SNAPSHOT, disabled FactionsPlugin!");
			Bukkit.getPluginManager().disablePlugin(this);
		}

		Plugin wep = getServer().getPluginManager().getPlugin("WorldEdit");
		worldEdit = wep instanceof WorldEditPlugin && wep.isEnabled() ? (WorldEditPlugin) wep : null;

		ConfigurationService.init(this);

		effectRestorer = new EffectRestorer(this);
		deathBanManager = new DeathBanManager();
		logManager = new LogManager(this);

		registerConfiguration();
		registerCommands();
		registerManagers();
		registerListeners();

		new BukkitRunnable() {
			@Override
			public void run() {
				getServer().broadcast(ChatColor.GREEN.toString() + ChatColor.BOLD + "Saving!" + "\n" + ChatColor.GREEN + "Saved all faction and player data to the database.", "hcf.seesaves");
				saveData();
			}
		}.runTaskTimerAsynchronously(this, TimeUnit.MINUTES.toMillis(10L), TimeUnit.MINUTES.toMillis(10L));
	}

	private void saveData() {
		this.economyManager.saveEconomyData();
		this.factionManager.saveFactionData();
		this.keyManager.saveKeyData();
		this.timerManager.saveTimerData();
		this.playerManager.savePlayerData();
		this.playTimeManager.savePlaytimeData();
	}

	@Override
	public void onDisable() {
		CustomEntityRegistration.unregisterCustomEntities();
		CombatLogListener.removeCombatLoggers();

		this.pvpClassManager.onDisable();
		this.scoreboardHandler.clearBoards();

		this.saveData();

		instance = null;
	}

	private void registerConfiguration() {
		ConfigurationSerialization.registerClass(CaptureZone.class);
		ConfigurationSerialization.registerClass(Claim.class);
		ConfigurationSerialization.registerClass(Subclaim.class);
		ConfigurationSerialization.registerClass(ClaimableFaction.class);
		ConfigurationSerialization.registerClass(ConquestFaction.class);
		ConfigurationSerialization.registerClass(CapturableFaction.class);
		ConfigurationSerialization.registerClass(KothFaction.class);
		ConfigurationSerialization.registerClass(EndPortalFaction.class);
		ConfigurationSerialization.registerClass(Faction.class);
		ConfigurationSerialization.registerClass(FactionMember.class);
		ConfigurationSerialization.registerClass(PlayerFaction.class);
		ConfigurationSerialization.registerClass(RoadFaction.class);
		ConfigurationSerialization.registerClass(SpawnFaction.class);
		ConfigurationSerialization.registerClass(GlowstoneMountainFaction.class);
		ConfigurationSerialization.registerClass(RoadFaction.NorthRoadFaction.class);
		ConfigurationSerialization.registerClass(RoadFaction.EastRoadFaction.class);
		ConfigurationSerialization.registerClass(RoadFaction.SouthRoadFaction.class);
		ConfigurationSerialization.registerClass(RoadFaction.WestRoadFaction.class);
	}

	private void registerListeners() {
		PluginManager manager = this.getServer().getPluginManager();
		manager.registerEvents(this.playTimeManager = new PlayTimeManager(this), this);
		manager.registerEvents(new CombatLogListener(this), this);
		manager.registerEvents(new HitDetectionListener(), this);
		manager.registerEvents(new FactionManagerArgument(this), this);
		manager.registerEvents(new BlockHitFixListener(), this);
		manager.registerEvents(new BlockJumpGlitchFixListener(), this);
		manager.registerEvents(new BoatGlitchFixListener(), this);
		manager.registerEvents(new BookDechantListener(), this);
		manager.registerEvents(new FactionClaimChunkArgument(this), this);
		manager.registerEvents(new BorderListener(), this);
		manager.registerEvents(new CobbleCommand(), this);
		manager.registerEvents(new BottledExpListener(), this);
		manager.registerEvents(new PortalTrapFixListener(), this);
		manager.registerEvents(new ChatListener(this), this);
		manager.registerEvents(new ClaimWandListener(this), this);
		manager.registerEvents(new CoreListener(this), this);
		manager.registerEvents(new ElevatorListener(), this);
		manager.registerEvents(new CrowbarListener(this), this);
		manager.registerEvents(new DeathListener(this), this);
		manager.registerEvents(new DeathMessageListener(this), this);
		manager.registerEvents(new DeathSignListener(this), this);
		manager.registerEvents(new EnchantLimitListener(), this);
		manager.registerEvents(new EnderChestRemovalListener(), this);
		manager.registerEvents(new EntityLimitListener(), this);
		manager.registerEvents(new EotwListener(this), this);
		manager.registerEvents(new EventSignListener(), this);
		manager.registerEvents(new ExpMultiplierListener(), this);
		manager.registerEvents(new FactionListener(this), this);
		manager.registerEvents(new FastBrewingListener(this), this);
		manager.registerEvents(new InfinityArrowFixListener(), this);
		manager.registerEvents(new KeyListener(this), this);
		manager.registerEvents(new PearlGlitchListener(this), this);
		manager.registerEvents(new PortalListener(this), this);
		manager.registerEvents(new PotionLimitListener(), this);
		manager.registerEvents(new ProtectionListener(this), this);
		manager.registerEvents(new ShopSignListener(this), this);
		manager.registerEvents(new SkullListener(), this);
		manager.registerEvents(new SotwListener(this), this);
		manager.registerEvents(new BeaconStrengthFixListener(), this);
		manager.registerEvents(new VoidGlitchFixListener(), this);
		manager.registerEvents(new UnRepairableListener(), this);
		manager.registerEvents(new WallBorderListener(this), this);
		manager.registerEvents(new AutoSmeltOreListener(), this);
		manager.registerEvents(new WorldListener(this), this);
		manager.registerEvents(new VillagerListener(), this);
		manager.registerEvents(new SignSubclaimListener(this), this);
		manager.registerEvents(new EndListener(), this);
		manager.registerEvents(new CraftListener(), this);
	}

	private void registerCommands() {
		getCommand("ores").setExecutor(new OresCommand());
		getCommand("friend").setExecutor(new FriendCommand());
		getCommand("spawn").setExecutor(new SpawnCommand(this));
		getCommand("enderdragon").setExecutor(new EnderDragonCommand());
		getCommand("playtime").setExecutor(new PlayTimeCommand());
		getCommand("cobble").setExecutor(new CobbleCommand());
		getCommand("crowgive").setExecutor(new GiveCrowbarCommand());
		getCommand("conquest").setExecutor(new ConquestExecutor(this));
		getCommand("economy").setExecutor(new EconomyCommand(this));
		getCommand("eotw").setExecutor(new EotwCommand(this));
		getCommand("event").setExecutor(new EventExecutor(this));
		getCommand("hcfhelp").setExecutor(new HCFHelpCommand());
		getCommand("faction").setExecutor(new FactionExecutor(this));
		getCommand("gopple").setExecutor(new GoppleCommand(this));
		getCommand("koth").setExecutor(new KothExecutor(this));
		getCommand("location").setExecutor(new LocationCommand(this));
		getCommand("logout").setExecutor(new LogoutCommand(this));
		getCommand("mapkit").setExecutor(new MapKitCommand(this));
		getCommand("pay").setExecutor(new PayCommand(this));
		getCommand("pvptimer").setExecutor(new PvpTimerCommand(this));
		getCommand("regen").setExecutor(new RegenCommand(this));
		getCommand("servertime").setExecutor(new ServerTimeCommand());
		getCommand("sotw").setExecutor(new SotwCommand(this));
		getCommand("spawncannon").setExecutor(new SpawnCannonCommand(this));
		getCommand("staffrevive").setExecutor(new StaffReviveCommand());
		getCommand("timer").setExecutor(new TimerExecutor(this));
		getCommand("focus").setExecutor(new FocusCommand());
		getCommand("lives").setExecutor(new LivesCommand());
		getCommand("savedata").setExecutor(new SaveDataCommand());
		getCommand("deathinfo").setExecutor(new DeathInfoCommand());
		getCommand("claim").setExecutor(new ClaimCommand());

		Map<String, Map<String, Object>> map = getDescription().getCommands();

		for (Map.Entry<String, Map<String, Object>> entry : map.entrySet()) {
			PluginCommand command = getCommand(entry.getKey());
			command.setPermission("hcf.command." + entry.getKey());
			command.setPermissionMessage(ChatColor.RED + "You do not have permissions to execute this command.");
		}
	}

	private void registerManagers() {
		this.claimHandler = new ClaimHandler(this);
		this.economyManager = new FlatFileEconomyManager(this);
		this.eotwHandler = new EotwHandler(this);
		this.eventScheduler = new EventScheduler(this);
		this.factionManager = new FlatFileFactionManager(this);
		this.keyManager = new KeyManager(this);
		this.pvpClassManager = new PvpClassManager(this);
		this.sotwTimer = new SotwTimer();
		this.timerManager = new TimerManager(this);
		this.scoreboardHandler = new ScoreboardHandler(this);
		this.playerManager = new PlayerManager();
		this.visualiseHandler = new VisualiseHandler();
	}

}
