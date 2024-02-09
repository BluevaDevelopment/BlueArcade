package net.blueva.arcade.managers.minigames;

import net.blueva.arcade.Main;
import net.blueva.arcade.ObjectResolver;
import net.blueva.arcade.managers.ArenaManager;
import net.blueva.arcade.managers.CacheManager;
import net.blueva.arcade.managers.PlayerManager;
import net.blueva.arcade.managers.SoundsManager;
import net.blueva.arcade.utils.*;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.util.HashMap;

public class SpleefManager {
    private static final HashMap<Integer, BukkitTask> StartTasks = new HashMap<>();
    private static final HashMap<Integer, BukkitTask> GameTasks = new HashMap<>();
    public static void Start(Integer arenaid, Main main) {
        regenerateFloor(arenaid, main);

        ArenaManager.teleportPlayers(arenaid, "spleef", main);
        ArenaManager.sendDescription(arenaid, "spleef", main);
        giveItems(arenaid, main);
        startCountdown(arenaid, main);
    }

    public static void finishPlayer(Integer arenaid, Player player) {
        if(PlayerManager.PlayerInGameStatus.containsKey(player)) {
            if(PlayerManager.PlayerInGameStatus.get(player).equalsIgnoreCase("Playing")) {
                ArenaManager.addPlayerToPodiumInReverseOrder(arenaid, player, ArenaManager.ArenaPlayersCount.get(arenaid));
                PlayerManager.PlayerInGameStatus.replace(player, "SPECTATOR");
                player.setGameMode(GameMode.SPECTATOR);
                player.getInventory().clear();
                SoundsManager.playSounds(Main.getPlugin(), player, CacheManager.Sounds.SOUNDS_IN_GAME_DEAD);
            }
        }
    }

    public static void finishPlayerSync(int arenaid, Player winner) {
        Bukkit.getScheduler().runTask(Main.getPlugin(), () -> finishPlayer(arenaid, winner));
    }

    private static void startCountdown(Integer arenaid, Main main) {
        ArenaManager.ArenaActualGame.replace(arenaid, "StartingSpleef");

        StartTasks.remove(arenaid);
        StartTasks.put(arenaid, Bukkit.getScheduler().runTaskTimerAsynchronously(main, new Runnable() {

            int time = CacheManager.Settings.GAME_GLOBAL_GAME_COUNTDOWN;
            boolean shouldCancel = false; // agregar variable booleana
            @Override
            public void run() {
                if (shouldCancel) { // verificar si la tarea debe cancelarse
                    BukkitTask task = StartTasks.get(arenaid);
                    if(task != null) {
                        task.cancel();
                        StartTasks.remove(arenaid);
                    }
                    return; // salir del método run()
                }

                for(final Player players : Bukkit.getOnlinePlayers()) {
                    if(PlayerManager.PlayerStatus.containsKey(players) && PlayerManager.PlayerArena.containsKey(players)) {
                        if (PlayerManager.PlayerStatus.get(players).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(players).equals(arenaid)) {
                            SoundsManager.playSounds(main, players, CacheManager.Sounds.SOUNDS_STARTING_GAME_COUNTDOWN);
                            TitlesUtil.sendTitle(players, CacheManager.Language.TITLES_STARTING_GAME_TITLE
                                            .replace("{game_display_name}", CacheManager.Language.MINI_GAMES_SPLEEF_DISPLAY_NAME)
                                            .replace("{time}", String.valueOf(time)),
                                    CacheManager.Language.TITLES_STARTING_GAME_SUBTITLE
                                            .replace("{game_display_name}", CacheManager.Language.MINI_GAMES_SPLEEF_DISPLAY_NAME)
                                            .replace("{time}", String.valueOf(time)), 0, 20, 5);
                        }
                    }
                }

                if(this.time <= 0){
                    shouldCancel = true;
                    BukkitTask task = StartTasks.get(arenaid);
                    if(task != null) {
                        task.cancel();
                        StartTasks.remove(arenaid);
                    }
                    startGame(arenaid, main);
                }

                if(ArenaManager.ArenaStatusInternal.get(arenaid).equalsIgnoreCase("Waiting")) {
                    BukkitTask task = StartTasks.get(arenaid);
                    if(task != null) {
                        task.cancel();
                        StartTasks.remove(arenaid);
                    }
                }

                ArenaManager.ArenaCountdown.replace(arenaid, time);

                this.time--;
            }
        }, 0L, 20L));
    }

    private static void startGame(Integer arenaid, Main main) {
        ArenaManager.ArenaActualGame.replace(arenaid, "Spleef");

        for(final Player players : Bukkit.getOnlinePlayers()) {
            if(PlayerManager.PlayerStatus.containsKey(players) && PlayerManager.PlayerArena.containsKey(players)) {
                if (PlayerManager.PlayerStatus.get(players).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(players).equals(arenaid)) {
                    PlayerManager.PlayerMuted.replace(players.getPlayer(), 0);
                    SyncUtil.setFlying(main, false, players);
                    SyncUtil.setGameMode(main, GameMode.SURVIVAL, players);
                    SoundsManager.playSounds(main, players, CacheManager.Sounds.SOUNDS_STARTING_GAME_START);
                    TitlesUtil.sendTitle(players,
                            CacheManager.Language.TITLES_GAME_STARTED_TITLE
                                    .replace("{game_display_name}", CacheManager.Language.MINI_GAMES_SPLEEF_DISPLAY_NAME),
                            CacheManager.Language.TITLES_GAME_STARTED_SUBTITLE
                                    .replace("{game_display_name}", CacheManager.Language.MINI_GAMES_SPLEEF_DISPLAY_NAME), 0, 0, 20);

                }
            }
        }

        GameTasks.remove(arenaid);
        GameTasks.put(arenaid, Bukkit.getScheduler().runTaskTimerAsynchronously(main, new Runnable() {

            int time = main.configManager.getArena(arenaid).getInt("arena.mini_games.spleef.basic.time", 60);
            @Override
            public void run() {
                if(checkMinimumPlayers(arenaid)) {
                    try {
                        ArenaManager.stopArena(main, arenaid);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    cancelGameTask(arenaid);
                    return;
                }

                if(ArenaManager.ArenaStatusInternal.get(arenaid).equalsIgnoreCase("Waiting")) {
                    cancelGameTask(arenaid);
                }

                ArenaManager.ArenaCountdown.replace(arenaid, time);

                if (this.time <= 0) {
                    cancelGameTask(arenaid);
                    ArenaManager.sendSummaryGame(arenaid, main);
                    regenerateFloor(arenaid, main);
                    return;
                }

                if(checkWinner(arenaid)) {
                    cancelGameTask(arenaid);
                    ArenaManager.sendSummaryGame(arenaid, main);
                    regenerateFloor(arenaid, main);
                    return;
                }

                for(final Player players : Bukkit.getOnlinePlayers()) {
                    if(PlayerManager.PlayerStatus.containsKey(players) && PlayerManager.PlayerArena.containsKey(players)) {
                        if (PlayerManager.PlayerStatus.get(players).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(players).equals(arenaid)) {
                            players.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ScoreboardUtil.format(main, players, CacheManager.Language.ACTION_BAR_IN_GAME_GLOBAL)));
                        }
                    }
                }

                this.time--;
            }
        }, 0L, 20L));
    }

    private static void giveItems(int arenaid, Main main) {
        ItemStack shovel = new ItemStack(Material.valueOf(ObjectResolver.getItem.DIAMOND_SHOVEL()), 1);
        ItemMeta shovelmeta = shovel.getItemMeta();
        shovelmeta.addEnchant(Enchantment.DIG_SPEED, 5, true);
        shovelmeta.addEnchant(Enchantment.DURABILITY, 3, true);
        shovelmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        shovel.setItemMeta(shovelmeta);

        for(final Player allp : Bukkit.getOnlinePlayers()) {
            if(PlayerManager.PlayerStatus.containsKey(allp) && PlayerManager.PlayerArena.containsKey(allp)) {
                if(PlayerManager.PlayerStatus.get(allp).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(allp).equals(arenaid)) {
                    allp.getInventory().setItem(0, shovel);
                }
            }

        }
    }

    private static void regenerateFloor(int arenaid, Main main) {
        World world = Bukkit.getWorld(main.configManager.getArena(arenaid).getString("arena.mini_games.spleef.basic.world"));

        double floorminx = main.configManager.getArena(arenaid).getDouble("arena.mini_games.spleef.game.floor.bounds.min.x");
        double floorminy = main.configManager.getArena(arenaid).getDouble("arena.mini_games.spleef.game.floor.bounds.min.y");
        double floorminz = main.configManager.getArena(arenaid).getDouble("arena.mini_games.spleef.game.floor.bounds.min.z");
        Location floormin = new Location(world, floorminx, floorminy, floorminz);

        double floormaxx = main.configManager.getArena(arenaid).getDouble("arena.mini_games.spleef.game.floor.bounds.max.x");
        double floormaxy = main.configManager.getArena(arenaid).getDouble("arena.mini_games.spleef.game.floor.bounds.max.y");
        double floormaxz = main.configManager.getArena(arenaid).getDouble("arena.mini_games.spleef.game.floor.bounds.max.z");
        Location floormax = new Location(world, floormaxx, floormaxy, floormaxz);


        int delayTicks = CacheManager.Settings.GAME_GLOBAL_LIMBO_COUNTDOWN+5*20;
        new BukkitRunnable() {
            @Override
            public void run() {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        BlocksUtil.setBlocks(floormin, floormax, Material.SNOW_BLOCK);
                    }
                }.runTaskLater(main, delayTicks);
            }
        }.runTaskLaterAsynchronously(main, 0);

    }

    private static boolean checkWinner(int arenaid) {
        int nonSpectatorCount = 0;
        Player winner = null;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (PlayerManager.PlayerArena.containsKey(player) && PlayerManager.PlayerArena.get(player).equals(arenaid)) {
                if (!PlayerManager.PlayerInGameStatus.get(player).equalsIgnoreCase("SPECTATOR")) {
                    nonSpectatorCount++;
                    winner = player;
                }
            }
        }

        if (nonSpectatorCount == 1 && winner != null) {
            finishPlayerSync(arenaid, winner);
            SoundsManager.playSounds(Main.getPlugin(), winner, CacheManager.Sounds.SOUNDS_IN_GAME_CLASSIFIED);
            return true;
        }

        return false;
    }

    public static boolean checkMinimumPlayers(Integer arenaid) {
        if(CacheManager.Arenas.ARENA_BASIC_INTEGER.get(arenaid).get("min_players") > ArenaManager.ArenaPlayersCount.get(arenaid)) {
            for(final Player allp : Bukkit.getOnlinePlayers()) {
                if(PlayerManager.PlayerStatus.containsKey(allp) && PlayerManager.PlayerArena.containsKey(allp)) {
                    if(PlayerManager.PlayerStatus.get(allp).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(allp).equals(arenaid)) {
                        StringUtils.sendMessage(allp, allp.getName(), CacheManager.Language.GLOBAL_ERROR_ARENA_STOPPED
                                .replace("{min_players}", String.valueOf(CacheManager.Arenas.ARENA_BASIC_INTEGER.get(arenaid).get("min_players"))));
                        ArenaManager.ArenaActualGame.replace(arenaid, "Lobby");
                    }
                }
            }
            return true;
        }
        return false;
    }

    public static void cancelGameTask(Integer arenaid) {
        BukkitTask task = GameTasks.get(arenaid);
        if(task != null) {
            task.cancel();
            GameTasks.remove(arenaid);
        }
    }
    
}
