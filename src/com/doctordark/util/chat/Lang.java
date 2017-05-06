package com.doctordark.util.chat;

import com.google.common.base.MoreObjects;

import net.minecraft.server.v1_7_R4.Item;

import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_7_R4.potion.CraftPotionEffectType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Deprecated
public class Lang {

	private static Pattern PATTERN = Pattern.compile("^\\s*([\\w\\d\\.]+)\\s*=\\s*(.*)\\s*$");
	private static String HASH_17 = "03f31164d234f10a3230611656332f1756e570a9";
	private static Map<String, String> translations;

	public static void initialize(String lang) throws IOException {
		translations = new HashMap<>();

		if (HASH_17.length() >= 2) {
			String url = "http://resources.download.minecraft.net/" + HASH_17.substring(0, 2) + "/" + HASH_17;

			try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(url).openStream(), StandardCharsets.UTF_8))) {
				String line;

				while ((line = reader.readLine()) != null) {
					line = line.trim();

					if (line.contains("=")) {
						Matcher matcher = Lang.PATTERN.matcher(line);

						if (!matcher.matches()) {
							continue;
						}

						Lang.translations.put(matcher.group(1), matcher.group(2));
					}
				}
			}
		}
	}

	public static String translatableFromStack(ItemStack stack) {
		net.minecraft.server.v1_7_R4.ItemStack nms = CraftItemStack.asNMSCopy(stack);
		Item item = nms.getItem();
		return item.a(nms) + ".name";
	}

	public static String fromStack(ItemStack stack) {
		String node = translatableFromStack(stack);
		return MoreObjects.firstNonNull(Lang.translations.get(node), node);
	}

	public static String translatableFromEnchantment(Enchantment ench) {
		net.minecraft.server.v1_7_R4.Enchantment nms = net.minecraft.server.v1_7_R4.Enchantment.byId[ench.getId()];

		return (nms == null) ? ench.getName() : nms.a();
	}

	public static String fromEnchantment(Enchantment enchantment) {
		String node = translatableFromEnchantment(enchantment);
		String trans = Lang.translations.get(node);

		return (trans != null) ? trans : node;
	}

	public static String translatableFromPotionEffectType(PotionEffectType effectType) {
		CraftPotionEffectType craftType = (CraftPotionEffectType) PotionEffectType.getById(effectType.getId());

		return craftType.getHandle().a();
	}

	public static String fromPotionEffectType(PotionEffectType effectType) {
		String node = translatableFromPotionEffectType(effectType);
		String val = Lang.translations.get(node);

		if (val == null) {
			return node;
		}

		return val;
	}

	public static String translate(String key, Object... args) {
		return String.format(Lang.translations.get(key), args);
	}

}