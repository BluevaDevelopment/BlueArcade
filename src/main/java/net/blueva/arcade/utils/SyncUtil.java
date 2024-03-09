package net.blueva.arcade.utils;

import net.blueva.arcade.Main;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.Random;

public class SyncUtil {
    public static void setGameMode(Main main, GameMode gamemode, Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                //your async code
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.setGameMode(gamemode);
                    }
                }.runTask(main);
            }
        }.runTaskAsynchronously(main);
    }

    public static void setFlying(Main main, boolean bool, Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                //your async code
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.setAllowFlight(bool);
                        player.setFlying(bool);
                    }
                }.runTask(main);
            }
        }.runTaskAsynchronously(main);
    }

    public static void setSpeed(Main main, float speed, Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                //your async code
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.setWalkSpeed(speed);
                    }
                }.runTask(main);
            }
        }.runTaskAsynchronously(main);
    }

    public static void removePotionEffects(Main main, Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                //your async code
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (PotionEffect effect : player.getActivePotionEffects()) {
                            player.removePotionEffect(effect.getType());
                        }
                    }
                }.runTask(main);
            }
        }.runTaskAsynchronously(main);

    }

    public static void spawnFireworks(Main main, Player player, Random random) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if(player != null) {
                    Location playerLocation = player.getLocation();
                    Firework firework = (Firework) Objects.requireNonNull(playerLocation.getWorld()).spawnEntity(playerLocation, EntityType.FIREWORK);
                    FireworkMeta fireworkMeta = firework.getFireworkMeta();

                    FireworkEffect effect = FireworkEffect.builder()
                            .withColor(StringUtils.generateRandomColor())
                            .withFade(StringUtils.generateRandomColor())
                            .with(FireworkEffect.Type.values()[random.nextInt(FireworkEffect.Type.values().length)])
                            .flicker(random.nextBoolean())
                            .trail(random.nextBoolean())
                            .build();

                    fireworkMeta.addEffect(effect);
                    firework.setFireworkMeta(fireworkMeta);
                }
            }
        }.runTaskLater(main, 0);
    }
}
