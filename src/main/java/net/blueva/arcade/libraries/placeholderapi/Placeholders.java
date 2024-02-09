package net.blueva.arcade.libraries.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.blueva.arcade.Main;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class Placeholders extends PlaceholderExpansion {

    private final Main plugin;

    public Placeholders(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getAuthor() {
        return "Blueva";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "bluearcade";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.pluginversion;
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if(params.equalsIgnoreCase("player_games_played")){
            return String.valueOf(plugin.configManager.getUser(player.getUniqueId()).getInt("stats.games_played", 0));
        }

        if(params.equalsIgnoreCase("player_mini_games_played")) {
            return String.valueOf(plugin.configManager.getUser(player.getUniqueId()).getInt("stats.mini_games_played", 0));
        }

        if(params.equalsIgnoreCase("player_first_place")) {
            return String.valueOf(plugin.configManager.getUser(player.getUniqueId()).getInt("stats.first_place", 0));
        }

        if(params.equalsIgnoreCase("player_second_place")) {
            return String.valueOf(plugin.configManager.getUser(player.getUniqueId()).getInt("stats.second_place", 0));
        }

        if(params.equalsIgnoreCase("player_third_place")) {
            return String.valueOf(plugin.configManager.getUser(player.getUniqueId()).getInt("stats.third_place", 0));
        }

        if(params.equalsIgnoreCase("player_stars")) {
            return String.valueOf(plugin.configManager.getUser(player.getUniqueId()).getInt("stats.stars", 0));
        }

        if(params.equalsIgnoreCase("player_credits")) {
            return String.valueOf(plugin.configManager.getUser(player.getUniqueId()).getInt("stats.credits", 0));
        }

        if(params.equalsIgnoreCase("player_kills")) {
            return String.valueOf(plugin.configManager.getUser(player.getUniqueId()).getInt("stats.kills", 0));
        }

        if(params.equalsIgnoreCase("player_deaths")) {
            return String.valueOf(plugin.configManager.getUser(player.getUniqueId()).getInt("stats.deaths", 0));
        }

        return null; // Placeholder is unknown by the Expansion
    }
}