package net.blueva.arcade.utils;

import net.blueva.arcade.managers.ArenaManager;
import net.blueva.arcade.managers.CacheManager;
import org.bukkit.ChatColor;

public class SignsUtil {
    public static String format(String text, Integer arenaid) {
        if(arenaid != null) {
            text = text.replace("{arena_status}", getStatus(arenaid));
            text = text.replace("{arena_displayname}", CacheManager.Arenas.ARENA_BASIC_STRING.get(arenaid).get("display_name"));
            text = text.replace("{players}", String.valueOf(ArenaManager.ArenaPlayersCount.get(arenaid)));
            text = text.replace("{players_max}", String.valueOf(CacheManager.Arenas.ARENA_BASIC_INTEGER.get(arenaid).get("max_players")));
        }
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    private static String getStatus(Integer arenaid) {
        if(arenaid == null ) return "";

        if (ArenaManager.ArenaStatus.get(arenaid).equalsIgnoreCase("Disabled")) {
            return "-";
        }

        String statusMessage = "";

        if(ArenaManager.ArenaActualGame.get(arenaid).equalsIgnoreCase("Lobby")) {
            statusMessage = "&aWaiting";
        } else if(ArenaManager.ArenaActualGame.get(arenaid).equalsIgnoreCase("Starting")) {
            statusMessage = "&eStarting";
        } else {
            statusMessage = "&cIn Game";
        }

        return statusMessage;
    }
}
