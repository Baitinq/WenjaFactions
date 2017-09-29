package com.doctordark.util.command;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class CommandArgument {

	private String name;
	protected boolean isPlayerOnly;
	protected String description;
	protected String permission;
	protected String[] aliases;

	public CommandArgument(String name, String description) {
		this(name, description, (String) null);
	}

	public CommandArgument(String name, String description, String permission) {
		this(name, description, permission, ArrayUtils.EMPTY_STRING_ARRAY);
	}

	public CommandArgument(String name, String description, String[] aliases) {
		this(name, description, null, aliases);
	}

	public CommandArgument(String name, String description, String permission, String[] aliases) {
		this.isPlayerOnly = false;
		this.name = name;
		this.description = description;
		this.permission = permission;
		this.aliases = Arrays.copyOf(aliases, aliases.length);
	}

	public String getName() {
		return this.name;
	}

	public boolean isPlayerOnly() {
		return this.isPlayerOnly;
	}

	public String getDescription() {
		return this.description;
	}

	public String getPermission() {
		return this.permission;
	}

	public String[] getAliases() {
		if (this.aliases == null) {
			this.aliases = ArrayUtils.EMPTY_STRING_ARRAY;
		}

		return Arrays.copyOf(this.aliases, this.aliases.length);
	}

	public abstract String getUsage(String p0);

	public abstract boolean onCommand(CommandSender p0, Command p1, String p2, String[] p3);

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return Collections.emptyList();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof CommandArgument)) {
			return false;
		}

		CommandArgument that = (CommandArgument) o;

		Label_0054:
		{
			if (this.name != null) {
				if (this.name.equals(that.name)) {
					break Label_0054;
				}
			} else if (that.name == null) {
				break Label_0054;
			}
			return false;
		}

		Label_0087:
		{
			if (this.description != null) {
				if (this.description.equals(that.description)) {
					break Label_0087;
				}
			} else if (that.description == null) {
				break Label_0087;
			}
			return false;
		}

		if (this.permission != null) {
			if (this.permission.equals(that.permission)) {
				return Arrays.equals(this.aliases, that.aliases);
			}
		} else if (that.permission == null) {
			return Arrays.equals(this.aliases, that.aliases);
		}
		return false;
	}

	@Override
	public int hashCode() {
		int result = (this.name != null) ? this.name.hashCode() : 0;
		result = 31 * result + ((this.description != null) ? this.description.hashCode() : 0);
		result = 31 * result + ((this.permission != null) ? this.permission.hashCode() : 0);
		result = 31 * result + ((this.aliases != null) ? Arrays.hashCode(this.aliases) : 0);
		return result;
	}

}