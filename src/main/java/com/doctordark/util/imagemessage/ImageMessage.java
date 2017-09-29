package com.doctordark.util.imagemessage;

import com.google.common.base.Preconditions;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;
import org.bukkit.entity.Player;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

public class ImageMessage {

	private static char TRANSPARENT_CHAR = ' ';
	private static Color[] colors = new Color[]{new Color(0, 0, 0), new Color(0, 0, 170), new Color(0, 170, 0), new Color(0, 170, 170), new Color(170, 0, 0), new Color(170, 0, 170), new Color(255, 170, 0), new Color(170, 170, 170), new Color(85, 85, 85), new Color(85, 85, 255), new Color(85, 255, 85), new Color(85, 255, 255), new Color(255, 85, 85), new Color(255, 85, 255), new Color(255, 255, 85), new Color(255, 255, 255)};

	private String[] lines;

	private ImageMessage(String... lines) throws IllegalArgumentException {
		Preconditions.checkNotNull((Object) lines, "Lines cannot be null");
		this.lines = lines;
	}

	public ImageMessage(BufferedImage image, int height, char imageCharacter) throws IllegalArgumentException {
		this(toImageMessage(toColourArray(image, height), imageCharacter));
	}

	public static ImageMessage newInstance(BufferedImage image, int height, char imageCharacter) throws IllegalArgumentException {
		Preconditions.checkNotNull((Object) image, "Image cannot be null");
		Preconditions.checkArgument(height >= 0, "Height must be positive");

		return new ImageMessage(image, height, imageCharacter);
	}

	private static ChatColor[][] toColourArray(BufferedImage image, int height) {
		double ratio = image.getHeight() / image.getWidth();
		BufferedImage resizedImage = resizeImage(image, (int) (height / ratio), height);
		ChatColor[][] chatImage = new ChatColor[resizedImage.getWidth()][resizedImage.getHeight()];

		for (int x = 0; x < resizedImage.getWidth(); ++x) {
			for (int y = 0; y < resizedImage.getHeight(); ++y) {
				ChatColor closest = getClosestChatColor(new Color(resizedImage.getRGB(x, y), true));
				chatImage[x][y] = closest;
			}
		}

		return chatImage;
	}

	private static String[] toImageMessage(ChatColor[][] colors, char imageCharacter) {
		String[] results = new String[colors[0].length];

		for (int i = 0; i < colors[0].length; ++i) {
			StringBuilder line = new StringBuilder();

			for (ChatColor[] color : colors) {
				ChatColor current = color[i];
				line.append((current != null) ? (current.toString() + imageCharacter) : ImageMessage.TRANSPARENT_CHAR);
			}
			results[i] = line.toString() + ChatColor.RESET;
		}

		return results;
	}

	private static BufferedImage resizeImage(BufferedImage image, int width, int height) {
		AffineTransform transform = new AffineTransform();
		transform.scale(width / image.getWidth(), height / image.getHeight());
		return new AffineTransformOp(transform, 1).filter(image, null);
	}

	private static double getDistance(Color c1, Color c2) {
		int red = c1.getRed() - c2.getRed();
		int green = c1.getGreen() - c2.getGreen();
		int blue = c1.getBlue() - c2.getBlue();
		double redMean = (c1.getRed() + c2.getRed()) / 2.0;
		double weightRed = 2.0 + redMean / 256.0;
		double weightGreen = 4.0;
		double weightBlue = 2.0 + (255.0 - redMean) / 256.0;

		return weightRed * red * red + weightGreen * green * green + weightBlue * blue * blue;
	}

	private static boolean areIdentical(Color c1, Color c2) {
		return Math.abs(c1.getRed() - c2.getRed()) <= 5 && Math.abs(c1.getGreen() - c2.getGreen()) <= 5 && Math.abs(c1.getBlue() - c2.getBlue()) <= 5;
	}

	private static ChatColor getClosestChatColor(Color color) {
		if (color.getAlpha() < 128) return null;

		for (int i = 0; i < ImageMessage.colors.length; ++i) {
			if (areIdentical(ImageMessage.colors[i], color)) return ChatColor.values()[i];
		}

		int index = 0;
		double best = -1.0;

		for (int j = 0; j < ImageMessage.colors.length; ++j) {
			double distance = getDistance(color, ImageMessage.colors[j]);

			if (distance < best || best == -1.0) {
				best = distance;
				index = j;
			}
		}

		return ChatColor.values()[index];
	}

	public ImageMessage appendText(String... text) {
		for (int i = 0; i < Math.min(text.length, this.lines.length); ++i) {
			StringBuilder sb = new StringBuilder();
			String[] lines = this.lines;
			int n = i;
			lines[n] = sb.append(lines[n]).append(' ').append(text[i]).toString();
		}

		return this;
	}

	private String center(String string, int length) {
		if (string.length() > length) {
			return string.substring(0, length);
		}

		if (string.length() == length) {
			return string;
		}

		return Strings.repeat(' ', (length - string.length()) / 2) + string;
	}

	public String[] getLines() {
		return Arrays.copyOf(this.lines, this.lines.length);
	}

	public void sendToPlayer(Player player) {
		player.sendMessage(this.lines);
	}

}