package com.doctordark.util;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Config extends YamlConfiguration {

	private String fileName;
	private JavaPlugin plugin;

	public Config(JavaPlugin plugin, String fileName) {
		this(plugin, fileName, ".yml");
	}

	public Config(JavaPlugin plugin, String fileName, String fileExtension) {
		this.plugin = plugin;
		this.fileName = fileName + (fileName.endsWith(fileExtension) ? "" : fileExtension);
		this.createFile();
	}

	public String getFileName() {
		return this.fileName;
	}

	public JavaPlugin getPlugin() {
		return this.plugin;
	}

	private void createFile() {
		File folder = this.plugin.getDataFolder();

		try {
			File file = new File(folder, this.fileName);

			if (!file.exists()) {
				if (this.plugin.getResource(this.fileName) != null) {
					this.plugin.saveResource(this.fileName, false);
				} else {
					this.save(file);
				}
				this.load(file);
			} else {
				this.load(file);
				this.save(file);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void save() {
		File folder = this.plugin.getDataFolder();

		try {
			this.save(new File(folder, this.fileName));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Config)) return false;

		Config config = (Config) o;

		Label_0054:
		{
			if (this.fileName != null) {
				if (this.fileName.equals(config.fileName)) {
					break Label_0054;
				}
			} else if (config.fileName == null) {
				break Label_0054;
			}
			return false;
		}

		if (this.plugin != null) {
			if (!this.plugin.equals(config.plugin)) {
				return false;
			}
		} else if (config.plugin != null) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		int result = (this.fileName != null) ? this.fileName.hashCode() : 0;
		result = 31 * result + ((this.plugin != null) ? this.plugin.hashCode() : 0);
		return result;
	}

}