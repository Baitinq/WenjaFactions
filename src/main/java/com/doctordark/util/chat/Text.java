package com.doctordark.util.chat;

import net.minecraft.server.v1_7_R4.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Text extends ChatComponentText {

	public Text(String string) {
		super(string);
	}

	public Text append(Object object) {
		return this.append(String.valueOf(object));
	}

	public Text append(String text) {
		return (Text) this.a(text);
	}

	public Text append(IChatBaseComponent node) {
		return (Text) this.addSibling(node);
	}

	public Text append(IChatBaseComponent... nodes) {
		for (IChatBaseComponent node : nodes) {
			this.addSibling(node);
		}
		return this;
	}

	public Text setBold(boolean bold) {
		this.getChatModifier().setBold(bold);
		return this;
	}

	public Text setColor(ChatColor color) {
		this.getChatModifier().setColor(EnumChatFormat.valueOf(color.name()));
		return this;
	}

	public Text setClick(ClickAction action, String value) {
		this.getChatModifier().setChatClickable(new ChatClickable(action.getNMS(), value));
		return this;
	}

	public Text setHover(HoverAction action, IChatBaseComponent value) {
		this.getChatModifier().a(new ChatHoverable(action.getNMS(), value));
		return this;
	}

	public Text setHoverText(String text) {
		return this.setHover(HoverAction.SHOW_TEXT, new Text(text));
	}

	public Text reset() {
		ChatUtil.reset(this);
		return this;
	}

	public IChatBaseComponent f() {
		return this.h();
	}

	public void send(CommandSender sender) {
		ChatUtil.send(sender, this);
	}

	public void broadcast() {
		this.broadcast(null);
	}

	public void broadcast(String permission) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (permission == null || player.hasPermission(permission)) this.send(player);
		}

		this.send(Bukkit.getConsoleSender());
	}

}