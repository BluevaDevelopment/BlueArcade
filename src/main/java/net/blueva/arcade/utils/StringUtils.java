package net.blueva.arcade.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import net.blueva.arcade.Main;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class StringUtils {

    private static final Random random = new Random();

    public static void sendMessage(CommandSender sender, String player, String text) {
        if(!Objects.equals(text, "")) {
            sender.sendMessage(formatMessage(player, text));
        }
    }

    public static String formatMessage(String player, String text) {
        if(player != null) {
            if(Main.placeholderapi) {
                return PlaceholderAPI.setPlaceholders(Bukkit.getPlayer(player), ChatColor.translateAlternateColorCodes('&', text));
            }
        }

        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String convertNumberToEmoji(int number) {
        Map<Integer, String> emojiMap = new HashMap<Integer, String>() {{
            put(1, "①");
            put(2, "②");
            put(3, "③");
            put(4, "④");
            put(5, "⑤");
            put(6, "⑥");
            put(7, "⑦");
            put(8, "⑧");
            put(9, "⑨");
        }};

        return emojiMap.getOrDefault(number, String.valueOf(number));
    }

    public static String convertSecondsInMinutes(int time) {
        int minutes = time / 60;
        int seconds = time % 60;

        String formattedMinutes = String.format("%02d", minutes);
        String formattedSeconds = String.format("%02d", seconds);

        return formattedMinutes + ":" + formattedSeconds;
    }

    public static int generateRandomNumber(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    public static Color generateRandomColor() {
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        return Color.fromRGB(r, g, b);
    }

}