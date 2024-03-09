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
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class RedAlertManager {
    private static final HashMap<Integer, BukkitTask> StartTasks = new HashMap<>();
    private static final HashMap<Integer, BukkitTask> GameTasks = new HashMap<>();

    private static final HashMap<Integer, HashMap<String, Location>> arenaLocations = new HashMap<>();
    private static final HashMap<Integer, HashMap<String, Double>> gameOptions = new HashMap<>();
    private static final HashMap<Integer, Boolean> gameParticles = new HashMap<>();

    public static void Start(Integer arenaid, Main main) {
        regenerateFloor(arenaid, main, false);

        gameOptions
                .computeIfAbsent(arenaid, k -> new HashMap<>())
                .putIfAbsent("change_color_chance", main.configManager.getArena(arenaid).getDouble("arena.mini_games.red_alert.basic.change_color_chance"));

        gameOptions
                .computeIfAbsent(arenaid, k -> new HashMap<>())
                .putIfAbsent("increase_chance_every_second", main.configManager.getArena(arenaid).getDouble("arena.mini_games.red_alert.basic.increase_chance_every_second"));

        gameParticles.putIfAbsent(arenaid, main.configManager.getArena(arenaid).getBoolean("arena.mini_games.red_alert.basic.enable_particles"));

        ArenaManager.teleportPlayers(arenaid, "red_alert", main);
        ArenaManager.sendDescription(arenaid, "red_alert", main);
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
        ArenaManager.ArenaActualGame.replace(arenaid, "StartingRedAlert");

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
                                            .replace("{game_display_name}", CacheManager.Language.MINI_GAMES_RED_ALERT_DISPLAY_NAME)
                                            .replace("{time}", String.valueOf(time)),
                                    CacheManager.Language.TITLES_STARTING_GAME_SUBTITLE
                                            .replace("{game_display_name}", CacheManager.Language.MINI_GAMES_RED_ALERT_DISPLAY_NAME)
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
        ArenaManager.ArenaActualGame.replace(arenaid, "RedAlert");

        for(final Player players : Bukkit.getOnlinePlayers()) {
            if(PlayerManager.PlayerStatus.containsKey(players) && PlayerManager.PlayerArena.containsKey(players)) {
                if (PlayerManager.PlayerStatus.get(players).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(players).equals(arenaid)) {
                    PlayerManager.PlayerMuted.replace(players.getPlayer(), 0);
                    SyncUtil.setFlying(main, false, players);
                    SyncUtil.setGameMode(main, GameMode.SURVIVAL, players);
                    SoundsManager.playSounds(main, players, CacheManager.Sounds.SOUNDS_STARTING_GAME_START);
                    TitlesUtil.sendTitle(players,
                            CacheManager.Language.TITLES_GAME_STARTED_TITLE
                                    .replace("{game_display_name}", CacheManager.Language.MINI_GAMES_RED_ALERT_DISPLAY_NAME),
                            CacheManager.Language.TITLES_GAME_STARTED_SUBTITLE
                                    .replace("{game_display_name}", CacheManager.Language.MINI_GAMES_RED_ALERT_DISPLAY_NAME), 0, 0, 20);

                }
            }
        }

        GameTasks.remove(arenaid);
        GameTasks.put(arenaid, Bukkit.getScheduler().runTaskTimerAsynchronously(main, new Runnable() {

            int time = main.configManager.getArena(arenaid).getInt("arena.mini_games.red_alert.basic.time", 60);
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

                HashMap<String, Double> options = gameOptions.get(arenaid);
                HashMap<String, Location> locations = arenaLocations.get(arenaid);
                changeBlocks(main, arenaid, locations.get("min"), locations.get("max"), options.get("change_color_chance"));
                ArenaManager.ArenaCountdown.replace(arenaid, time);

                options.replace("change_color_chance", options.get("change_color_chance")+options.get("increase_chance_every_second"));

                if (this.time <= 0) {
                    cancelGameTask(arenaid);
                    ArenaManager.sendSummaryGame(arenaid, main);
                    regenerateFloor(arenaid, main, true);
                    options.remove("change_color_chance");
                    return;
                }

                if(checkWinner(arenaid)) {
                    cancelGameTask(arenaid);
                    ArenaManager.sendSummaryGame(arenaid, main);
                    regenerateFloor(arenaid, main, true);
                    options.remove("change_color_chance");
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

    private static void regenerateFloor(int arenaid, Main main, boolean delay) {
        World world = Bukkit.getWorld(Objects.requireNonNull(main.configManager.getArena(arenaid).getString("arena.mini_games.red_alert.basic.world")));

        double floorminx = main.configManager.getArena(arenaid).getDouble("arena.mini_games.red_alert.game.floor.bounds.min.x");
        double floorminy = main.configManager.getArena(arenaid).getDouble("arena.mini_games.red_alert.game.floor.bounds.min.y");
        double floorminz = main.configManager.getArena(arenaid).getDouble("arena.mini_games.red_alert.game.floor.bounds.min.z");
        Location floormin = new Location(world, floorminx, floorminy, floorminz);

        double floormaxx = main.configManager.getArena(arenaid).getDouble("arena.mini_games.red_alert.game.floor.bounds.max.x");
        double floormaxy = main.configManager.getArena(arenaid).getDouble("arena.mini_games.red_alert.game.floor.bounds.max.y");
        double floormaxz = main.configManager.getArena(arenaid).getDouble("arena.mini_games.red_alert.game.floor.bounds.max.z");
        Location floormax = new Location(world, floormaxx, floormaxy, floormaxz);

        arenaLocations
                .computeIfAbsent(arenaid, k -> new HashMap<>())
                .putIfAbsent("min", floormin);
        arenaLocations
                .computeIfAbsent(arenaid, k -> new HashMap<>())
                .putIfAbsent("max", floormax);

        int delayTicks = 0;
        if (delay) {
            delayTicks = CacheManager.Settings.GAME_GLOBAL_LIMBO_COUNTDOWN+5*20;
        }
        int finalDelayTicks = delayTicks;
        new BukkitRunnable() {
            @Override
            public void run() {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        BlocksUtil.setBlocks(floormin, floormax, Material.WHITE_WOOL);
                    }
                }.runTaskLater(main, finalDelayTicks);
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

    private static void changeBlocks(Main main, Integer arenaid, Location pointA, Location pointB, Double chance) {
        new BukkitRunnable() {
            @Override
            public void run() {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        int minX = Math.min(pointA.getBlockX(), pointB.getBlockX());
                        int minY = Math.min(pointA.getBlockY(), pointB.getBlockY());
                        int minZ = Math.min(pointA.getBlockZ(), pointB.getBlockZ());
                        int maxX = Math.max(pointA.getBlockX(), pointB.getBlockX());
                        int maxY = Math.max(pointA.getBlockY(), pointB.getBlockY());
                        int maxZ = Math.max(pointA.getBlockZ(), pointB.getBlockZ());

                        World world = pointA.getWorld();
                        Random random = new Random();

                        if(world != null) {
                            for (int x = minX; x <= maxX; x++) {
                                for (int y = minY; y <= maxY; y++) {
                                    for (int z = minZ; z <= maxZ; z++) {
                                        if (random.nextInt(100) < chance) {
                                            Block block = world.getBlockAt(x, y, z);
                                            if(block.getType() == Material.WHITE_WOOL
                                                    || block.getType() == Material.YELLOW_WOOL
                                                    || block.getType() == Material.ORANGE_WOOL
                                                    || block.getType() == Material.RED_WOOL) {
                                                BlockData data = block.getBlockData();
                                                if (data.getMaterial() == Material.WHITE_WOOL) {
                                                    block.setType(Material.YELLOW_WOOL);
                                                }
                                                if (data.getMaterial() == Material.YELLOW_WOOL) {
                                                    block.setType(Material.ORANGE_WOOL);
                                                }
                                                if (data.getMaterial() == Material.ORANGE_WOOL) {
                                                    block.setType(Material.RED_WOOL);
                                                }
                                                if (data.getMaterial() == Material.RED_WOOL) {
                                                    block.setType(Material.AIR);
                                                    if(gameParticles.get(arenaid)) {
                                                        block.getWorld().spawnParticle(Particle.FLAME, block.getLocation(), 5);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

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
