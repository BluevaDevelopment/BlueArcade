package net.blueva.arcade.managers.minigames;

import net.blueva.arcade.Main;
import net.blueva.arcade.managers.ArenaManager;
import net.blueva.arcade.managers.CacheManager;
import net.blueva.arcade.managers.PlayerManager;
import net.blueva.arcade.managers.SoundsManager;
import net.blueva.arcade.utils.*;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class MinefieldManager {
    private static final HashMap<Integer, BukkitTask> StartTasks = new HashMap<>();
    private static final HashMap<Integer, BukkitTask> GameTasks = new HashMap<>();
    public static void Start(Integer arenaid, Main main) {
        ArenaManager.teleportPlayers(arenaid, "minefield", main);
        ArenaManager.sendDescription(arenaid, "minefield", main);
        startCountdown(arenaid, main);
        removePlates(arenaid, main);
    }

    public static void finishPlayer(Integer arenaid, Player player) {
        if(PlayerManager.PlayerInGameStatus.containsKey(player)) {
            if(PlayerManager.PlayerInGameStatus.get(player).equalsIgnoreCase("Playing")) {
                ArenaManager.addPlayerToPodium(arenaid, player, 1);
                PlayerManager.PlayerInGameStatus.replace(player, "SPECTATOR");
                player.setGameMode(GameMode.SPECTATOR);
                SoundsManager.playSounds(player, CacheManager.Sounds.SOUNDS_IN_GAME_CLASSIFIED);
                TitlesUtil.sendTitle(player,
                        CacheManager.Language.TITLES_CLASSIFIED_TITLE
                                .replace("{place}", String.valueOf(ArenaManager.getPlayerPositionOnPodium(arenaid, player))),
                        CacheManager.Language.TITLES_CLASSIFIED_SUBTITLE
                                .replace("{place}", String.valueOf(ArenaManager.getPlayerPositionOnPodium(arenaid, player)))
                        , 0, 80, 20);
            }
        }
    }

    private static void startCountdown(Integer arenaid, @NotNull Main main) {
        ArenaManager.ArenaActualGame.replace(arenaid, "StartingMinefield");

        for(final Player players : Bukkit.getOnlinePlayers()) {
            if(PlayerManager.PlayerStatus.containsKey(players)) {
                if (PlayerManager.PlayerStatus.get(players).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(players).equals(arenaid)) {
                    SyncUtil.setSpeed(main, 0.0f, players);
                }
            }
        }

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
                            SoundsManager.playSounds(players, CacheManager.Sounds.SOUNDS_STARTING_GAME_COUNTDOWN);
                            TitlesUtil.sendTitle(players, CacheManager.Language.TITLES_STARTING_GAME_TITLE
                                            .replace("{game_display_name}", CacheManager.Language.MINI_GAMES_MINEFIELD_DISPLAY_NAME)
                                            .replace("{time}", String.valueOf(time)),
                                    CacheManager.Language.TITLES_STARTING_GAME_SUBTITLE
                                            .replace("{game_display_name}", CacheManager.Language.MINI_GAMES_MINEFIELD_DISPLAY_NAME)
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
                    setPlates(arenaid, main);
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

    private static void startGame(Integer arenaid, @NotNull Main main) {
        ArenaManager.ArenaActualGame.replace(arenaid, "Minefield");

        for(final Player players : Bukkit.getOnlinePlayers()) {
            if(PlayerManager.PlayerStatus.containsKey(players) && PlayerManager.PlayerMuted.containsKey(players)) {
                if (PlayerManager.PlayerStatus.get(players).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(players).equals(arenaid)) {
                    PlayerManager.PlayerMuted.replace(players.getPlayer(), 0);
                    SyncUtil.setFlying(main, false, players);
                    SyncUtil.setSpeed(main, 0.2f, players);
                    SoundsManager.playSounds(players, CacheManager.Sounds.SOUNDS_STARTING_GAME_START);
                    TitlesUtil.sendTitle(players,
                            CacheManager.Language.TITLES_GAME_STARTED_TITLE
                                    .replace("{game_display_name}", CacheManager.Language.MINI_GAMES_MINEFIELD_DISPLAY_NAME),
                            CacheManager.Language.TITLES_GAME_STARTED_SUBTITLE
                                    .replace("{game_display_name}", CacheManager.Language.MINI_GAMES_MINEFIELD_DISPLAY_NAME), 0, 0, 20);

                }
            }
        }

        GameTasks.remove(arenaid);
        GameTasks.put(arenaid, Bukkit.getScheduler().runTaskTimerAsynchronously(main, new Runnable() {

            int time = main.configManager.getArena(arenaid).getInt("arena.mini_games.minefield.basic.time", 60);
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
                    return;
                }

                ArenaManager.ArenaCountdown.replace(arenaid, time);

                if (this.time <= 0) {
                    cancelGameTask(arenaid);
                    ArenaManager.sendSummaryGame(arenaid, main);
                    removePlates(arenaid, main);
                    return;
                }

                if(ArenaManager.areAllPlayersSpectators(arenaid)) {
                    cancelGameTask(arenaid);
                    ArenaManager.sendSummaryGame(arenaid, main);
                    removePlates(arenaid, main);
                    return;

                }

                if(ArenaManager.ArenaPodium.containsKey(arenaid) && ArenaManager.ArenaPodium.get(arenaid).containsKey(1) && ArenaManager.ArenaPodium.get(arenaid).containsKey(2) && ArenaManager.ArenaPodium.get(arenaid).containsKey(3)) {
                    cancelGameTask(arenaid);
                    ArenaManager.sendSummaryGame(arenaid, main);
                    removePlates(arenaid, main);
                    return;
                }

                for(final Player players : Bukkit.getOnlinePlayers()) {
                    if(PlayerManager.PlayerStatus.containsKey(players) && PlayerManager.PlayerArena.containsKey(players)) {
                        if (PlayerManager.PlayerStatus.get(players).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(players).equals(arenaid)) {
                            players.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ScoreboardUtil.format(players, CacheManager.Language.ACTION_BAR_IN_GAME_GLOBAL)));
                        }
                    }
                }

                this.time--;
            }
        }, 0L, 20L));
    }

    private static void setPlates(int arenaid, Main main) {
        World world = Bukkit.getWorld(Objects.requireNonNull(main.configManager.getArena(arenaid).getString("arena.mini_games.minefield.basic.world")));

        double floorminx = main.configManager.getArena(arenaid).getDouble("arena.mini_games.minefield.game.floor.bounds.min.x");
        double floorminy = main.configManager.getArena(arenaid).getDouble("arena.mini_games.minefield.game.floor.bounds.min.y") + 1;
        double floorminz = main.configManager.getArena(arenaid).getDouble("arena.mini_games.minefield.game.floor.bounds.min.z");
        Location floormin = new Location(world, floorminx, floorminy, floorminz);

        double floormaxx = main.configManager.getArena(arenaid).getDouble("arena.mini_games.minefield.game.floor.bounds.max.x");
        double floormaxy = main.configManager.getArena(arenaid).getDouble("arena.mini_games.minefield.game.floor.bounds.max.y") + 1;
        double floormaxz = main.configManager.getArena(arenaid).getDouble("arena.mini_games.minefield.game.floor.bounds.max.z");
        Location floormax = new Location(world, floormaxx, floormaxy, floormaxz);


        new BukkitRunnable() {
            @Override
            public void run() {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        BlocksUtil.setBlocks(floormin, floormax, Material.STONE_PRESSURE_PLATE, Material.AIR);
                    }
                }.runTask(main);
            }
        }.runTaskAsynchronously(main);

    }

    private static void removePlates(int arenaid, Main main) {
        World world = Bukkit.getWorld(Objects.requireNonNull(main.configManager.getArena(arenaid).getString("arena.mini_games.minefield.basic.world")));

        double floorminx = main.configManager.getArena(arenaid).getDouble("arena.mini_games.minefield.game.floor.bounds.min.x");
        double floorminy = main.configManager.getArena(arenaid).getDouble("arena.mini_games.minefield.game.floor.bounds.min.y") + 1;
        double floorminz = main.configManager.getArena(arenaid).getDouble("arena.mini_games.minefield.game.floor.bounds.min.z");
        Location floormin = new Location(world, floorminx, floorminy, floorminz);

        double floormaxx = main.configManager.getArena(arenaid).getDouble("arena.mini_games.minefield.game.floor.bounds.max.x");
        double floormaxy = main.configManager.getArena(arenaid).getDouble("arena.mini_games.minefield.game.floor.bounds.max.y") + 1;
        double floormaxz = main.configManager.getArena(arenaid).getDouble("arena.mini_games.minefield.game.floor.bounds.max.z");
        Location floormax = new Location(world, floormaxx, floormaxy, floormaxz);


        new BukkitRunnable() {
            @Override
            public void run() {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        BlocksUtil.setBlocks(floormin, floormax, Material.AIR);
                    }
                }.runTask(main);
            }
        }.runTaskAsynchronously(main);
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
