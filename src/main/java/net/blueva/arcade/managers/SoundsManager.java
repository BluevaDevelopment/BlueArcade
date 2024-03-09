package net.blueva.arcade.managers;

import net.blueva.arcade.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Objects;

public class SoundsManager {
    public static void playSounds(Player player, String sound) {
        if (Objects.equals(sound, "")) {
            return;
        }

        Sound soundEnum;
        try {
            soundEnum = Sound.valueOf(sound);
        } catch (IllegalArgumentException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&c[BlueArcade] The sound " + sound + " specified in sounds.yml does not exist."));
            return;
        }

        player.playSound(player.getLocation(), soundEnum, 1.0f, 1.0f);
    }

    public static void copySounds(Main main) {
        main.configManager.getSounds().set("sounds.starting_arena.countdown", "");
        main.configManager.saveSounds();
        main.configManager.reloadSounds();
        main.configManager.getSounds().set("sounds.starting_arena.notify_countdown", "ENTITY_EXPERIENCE_ORB_PICKUP");
        main.configManager.saveSounds();
        main.configManager.reloadSounds();
        main.configManager.getSounds().set("sounds.starting_game.teleport", "ENTITY_ENDERMAN_TELEPORT");
        main.configManager.saveSounds();
        main.configManager.reloadSounds();
        main.configManager.getSounds().set("sounds.starting_game.countdown", "ENTITY_CHICKEN_EGG");
        main.configManager.saveSounds();
        main.configManager.reloadSounds();
        main.configManager.getSounds().set("sounds.starting_game.start", "ENTITY_PLAYER_LEVELUP");
        main.configManager.saveSounds();
        main.configManager.reloadSounds();
        main.configManager.getSounds().set("sounds.in_game.classified", "UI_TOAST_CHALLENGE_COMPLETE");
        main.configManager.saveSounds();
        main.configManager.reloadSounds();
        main.configManager.getSounds().set("sounds.in_game.finish", "ENTITY_EXPERIENCE_ORB_PICKUP");
        main.configManager.saveSounds();
        main.configManager.reloadSounds();
        main.configManager.getSounds().set("sounds.in_game.dead", "ENTITY_ENDER_DRAGON_HURT");
        main.configManager.saveSounds();
        main.configManager.reloadSounds();
        main.configManager.getSounds().set("sounds.in_game.respawn", "ENTITY_ENDERMAN_TELEPORT");
        main.configManager.saveSounds();
        main.configManager.reloadSounds();
        main.configManager.getSounds().set("sounds.in_game.game_over", "ENTITY_WITHER_DEATH");
        main.configManager.saveSounds();
        main.configManager.reloadSounds();
    }
























}
