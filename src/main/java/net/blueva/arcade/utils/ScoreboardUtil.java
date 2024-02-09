package net.blueva.arcade.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.blueva.arcade.Main;
import net.blueva.arcade.managers.ArenaManager;
import net.blueva.arcade.managers.CacheManager;
import net.blueva.arcade.managers.PlayerManager;
import net.blueva.arcade.managers.minigames.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardUtil {
    public static List<String> format(@NotNull Main main, Player player, @NotNull List<String> text) {
        List<String> formattedText = new ArrayList<>();

        for (String line : text) {
            line = line
                    .replace("{arena}", CacheManager.Arenas.ARENA_BASIC_STRING.get(PlayerManager.PlayerArena.get(player)).get("display_name"))
                    .replace("{time}", StringUtils.convertSecondsInMinutes(ArenaManager.ArenaCountdown.getOrDefault(PlayerManager.PlayerArena.get(player), 0)))
                    .replace("{player}", player.getName())
                    .replace("{game_display_name}", CacheManager.Arenas.ARENA_GAME_DISPLAY_NAME.get(PlayerManager.PlayerArena.get(player)) != null ? CacheManager.Arenas.ARENA_GAME_DISPLAY_NAME.get(PlayerManager.PlayerArena.get(player)) : "None")
                    .replace("{stars}", String.valueOf(0))
                    .replace("{players}", ArenaManager.ArenaPlayersCount.get(PlayerManager.PlayerArena.get(player)).toString())
                    .replace("{max_players}", String.valueOf(CacheManager.Arenas.ARENA_BASIC_INTEGER.get(PlayerManager.PlayerArena.get(player)).get("max_players")))
                    .replace("{min_players}", String.valueOf(CacheManager.Arenas.ARENA_BASIC_INTEGER.get(PlayerManager.PlayerArena.get(player)).get("min_players")))
                    .replace("{round}", String.valueOf(ArenaManager.ArenaRound.get(PlayerManager.PlayerArena.get(player))))
                    .replace("{round_max}", String.valueOf(CacheManager.Arenas.ARENA_BASIC_INTEGER.get(PlayerManager.PlayerArena.get(player)).get("rounds")))
                    .replace("{place_1}", ArenaManager.getPlayerNameAtPosition(PlayerManager.PlayerArena.get(player), 1))
                    .replace("{place_2}", ArenaManager.getPlayerNameAtPosition(PlayerManager.PlayerArena.get(player), 2))
                    .replace("{place_3}", ArenaManager.getPlayerNameAtPosition(PlayerManager.PlayerArena.get(player), 3))
                    .replace("{stars_1}", String.valueOf(ArenaManager.getStarsForPlayer(PlayerManager.PlayerArena.get(player), ArenaManager.getPlayerAtPosition(PlayerManager.PlayerArena.get(player), 1))))
                    .replace("{stars_2}", String.valueOf(ArenaManager.getStarsForPlayer(PlayerManager.PlayerArena.get(player), ArenaManager.getPlayerAtPosition(PlayerManager.PlayerArena.get(player), 2))))
                    .replace("{stars_3}", String.valueOf(ArenaManager.getStarsForPlayer(PlayerManager.PlayerArena.get(player), ArenaManager.getPlayerAtPosition(PlayerManager.PlayerArena.get(player), 3))))
                    .replace("{place_player}", StringUtils.convertNumberToEmoji(ArenaManager.getPlayerPositionOnPodium(PlayerManager.PlayerArena.get(player), player)))
                    .replace("{stars_player}", String.valueOf(ArenaManager.getStarsForPlayer(PlayerManager.PlayerArena.get(player), player)))
                    .replace("{mini_description}", CacheManager.Language.getScoreboardMiniDescription(ArenaManager.ArenaActualGame.get(PlayerManager.PlayerArena.get(player))))
                    .replace("{kills_oitc}", String.valueOf(OneInTheChamberManager.getKills(player)))
                    .replace("{deaths_oitc}", String.valueOf(OneInTheChamberManager.getDeaths(player)))
                    .replace("{kills_kb}", String.valueOf(KnockBackManager.getKills(player)))
                    .replace("{light_color}", TrafficLightManager.getLightColor(PlayerManager.PlayerArena.get(player)))
                    .replace("{sheared_sheep}", String.valueOf(ExplodingSheepManager.getShearedSheep(player)))
                    .replace("{player_tagged}", TNTTagManager.playerIsTagged(player));

            if (Main.placeholderapi) {
                line = PlaceholderAPI.setPlaceholders(player, ChatColor.translateAlternateColorCodes('&', line));
            }

            formattedText.add(ChatColor.translateAlternateColorCodes('&', line));
        }

        return formattedText;
    }

    public static String format(@NotNull Main main, Player player, @NotNull String text) {
        text = text
                .replace("{arena}", CacheManager.Arenas.ARENA_BASIC_STRING.get(PlayerManager.PlayerArena.get(player)).get("display_name"))
                .replace("{time}", StringUtils.convertSecondsInMinutes(ArenaManager.ArenaCountdown.getOrDefault(PlayerManager.PlayerArena.get(player), 0)))
                .replace("{player}", player.getName())
                .replace("{game_display_name}", CacheManager.Arenas.ARENA_GAME_DISPLAY_NAME.get(PlayerManager.PlayerArena.get(player)) != null ? CacheManager.Arenas.ARENA_GAME_DISPLAY_NAME.get(PlayerManager.PlayerArena.get(player)) : "None")
                .replace("{stars}", String.valueOf(0))
                .replace("{players}", ArenaManager.ArenaPlayersCount.get(PlayerManager.PlayerArena.get(player)).toString())
                .replace("{max_players}", String.valueOf(CacheManager.Arenas.ARENA_BASIC_INTEGER.get(PlayerManager.PlayerArena.get(player)).get("max_players")))
                .replace("{min_players}", String.valueOf(CacheManager.Arenas.ARENA_BASIC_INTEGER.get(PlayerManager.PlayerArena.get(player)).get("min_players")))
                .replace("{round}", String.valueOf(ArenaManager.ArenaRound.get(PlayerManager.PlayerArena.get(player))))
                .replace("{round_max}", String.valueOf(CacheManager.Arenas.ARENA_BASIC_INTEGER.get(PlayerManager.PlayerArena.get(player)).get("rounds")))
                .replace("{place_1}", ArenaManager.getPlayerNameAtPosition(PlayerManager.PlayerArena.get(player), 1))
                .replace("{place_2}", ArenaManager.getPlayerNameAtPosition(PlayerManager.PlayerArena.get(player), 2))
                .replace("{place_3}", ArenaManager.getPlayerNameAtPosition(PlayerManager.PlayerArena.get(player), 3))
                .replace("{stars_1}", String.valueOf(ArenaManager.getStarsForPlayer(PlayerManager.PlayerArena.get(player), ArenaManager.getPlayerAtPosition(PlayerManager.PlayerArena.get(player), 1))))
                .replace("{stars_2}", String.valueOf(ArenaManager.getStarsForPlayer(PlayerManager.PlayerArena.get(player), ArenaManager.getPlayerAtPosition(PlayerManager.PlayerArena.get(player), 2))))
                .replace("{stars_3}", String.valueOf(ArenaManager.getStarsForPlayer(PlayerManager.PlayerArena.get(player), ArenaManager.getPlayerAtPosition(PlayerManager.PlayerArena.get(player), 3))))
                .replace("{place_player}", StringUtils.convertNumberToEmoji(ArenaManager.getPlayerPositionOnPodium(PlayerManager.PlayerArena.get(player), player)))
                .replace("{stars_player}", String.valueOf(ArenaManager.getStarsForPlayer(PlayerManager.PlayerArena.get(player), player)))
                .replace("{mini_description}", CacheManager.Language.getScoreboardMiniDescription(ArenaManager.ArenaActualGame.get(PlayerManager.PlayerArena.get(player))))
                .replace("{kills_oitc}", String.valueOf(OneInTheChamberManager.getKills(player)))
                .replace("{deaths_oitc}", String.valueOf(OneInTheChamberManager.getDeaths(player)))
                .replace("{kills_kb}", String.valueOf(KnockBackManager.getKills(player)))
                .replace("{light_color}", TrafficLightManager.getLightColor(PlayerManager.PlayerArena.get(player)))
                .replace("{sheared_sheep}", String.valueOf(ExplodingSheepManager.getShearedSheep(player)));
        if(Main.placeholderapi) {
            return PlaceholderAPI.setPlaceholders(player, ChatColor.translateAlternateColorCodes('&', text));
        }

        return ChatColor.translateAlternateColorCodes('&', text);
    }

}
