package com.doctordark.util.chat;

import net.minecraft.server.v1_7_R4.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class Trans extends ChatMessage {

	public Trans(String string, Object... objects) {
		super(string, objects);
	}

	public static Trans fromItemStack(ItemStack stack) {
		return ChatUtil.fromItemStack(stack);
	}

	public IChatBaseComponent f() {
		return this.h();
	}

	public Trans append(Object object) {
		return this.append(String.valueOf(object));
	}

	public Trans append(String text) {
		return (Trans) this.a(text);
	}

	public Trans append(IChatBaseComponent node) {
		return (Trans) this.addSibling(node);
	}

	public Trans append(IChatBaseComponent... nodes) {
		for (IChatBaseComponent node : nodes) {
			this.addSibling(node);
		}
		return this;
	}

	public Trans appendItem(ItemStack stack) {
		return this.append(ChatUtil.fromItemStack(stack));
	}

	public Trans localText(ItemStack stack) {
		return this.append(ChatUtil.localFromItem(stack));
	}

	public Trans setBold(boolean bold) {
		this.getChatModifier().setBold(bold);
		return this;
	}

	public Trans setItalic(boolean italic) {
		this.getChatModifier().setItalic(italic);
		return this;
	}

	public Trans setUnderline(boolean underline) {
		this.getChatModifier().setUnderline(underline);
		return this;
	}

	public Trans setRandom(boolean random) {
		this.getChatModifier().setRandom(random);
		return this;
	}

	public Trans setStrikethrough(boolean strikethrough) {
		this.getChatModifier().setStrikethrough(strikethrough);
		return this;
	}

	public Trans setColor(ChatColor color) {
		this.getChatModifier().setColor(EnumChatFormat.valueOf(color.name()));
		return this;
	}

	public Trans setClick(ClickAction action, String value) {
		this.getChatModifier().setChatClickable(new ChatClickable(action.getNMS(), value));
		return this;
	}

	public Trans setHover(HoverAction action, IChatBaseComponent value) {
		this.getChatModifier().a(new ChatHoverable(action.getNMS(), value));
		return this;
	}

	public Trans setHoverText(String text) {
		return this.setHover(HoverAction.SHOW_TEXT, new Text(text));
	}

	public Trans reset() {
		ChatUtil.reset(this);
		return this;
	}

	public String toRawText() {
		return this.c();
	}

	public void send(CommandSender sender) {
		ChatUtil.send(sender, this);
	}

}