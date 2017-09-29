package com.doctordark.internal.com.doctordark.base;

import com.doctordark.util.PersistableLocation;
import com.doctordark.util.SignHandler;
import com.doctordark.util.chat.Lang;
import com.doctordark.util.cuboid.Cuboid;
import com.doctordark.util.cuboid.NamedCuboid;
import com.doctordark.util.itemdb.ItemDb;
import com.doctordark.util.itemdb.SimpleItemDb;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Random;

public class BasePlugin {

	private static BasePlugin plugin = new BasePlugin();

	private Random random;
	private ItemDb itemDb;
	private SignHandler signHandler;
	private JavaPlugin javaPlugin;

	private BasePlugin() {
		this.random = new Random();
	}

	public static BasePlugin getPlugin() {
		return BasePlugin.plugin;
	}

	public void init(JavaPlugin plugin) {
		this.javaPlugin = plugin;

		ConfigurationSerialization.registerClass(PersistableLocation.class);
		ConfigurationSerialization.registerClass(Cuboid.class);
		ConfigurationSerialization.registerClass(NamedCuboid.class);

		this.itemDb = new SimpleItemDb(plugin);
		this.signHandler = new SignHandler(plugin);

		try {
			Lang.initialize("en_US");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void disable() {
		this.signHandler.cancelTasks(null);
		this.javaPlugin = null;
		BasePlugin.plugin = null;
	}

	public Random getRandom() {
		return this.random;
	}

	public ItemDb getItemDb() {
		return this.itemDb;
	}

	public SignHandler getSignHandler() {
		return this.signHandler;
	}

	public JavaPlugin getJavaPlugin() {
		return this.javaPlugin;
	}

}
