package net.blueva.arcade.managers.minigames;

import net.blueva.arcade.Main;
import net.blueva.arcade.managers.ArenaManager;
import net.blueva.arcade.managers.CacheManager;
import net.blueva.arcade.managers.PlayerManager;
import net.blueva.arcade.managers.SoundsManager;
import net.blueva.arcade.utils.StringUtils;
import net.blueva.arcade.utils.ScoreboardUtil;
import net.blueva.arcade.utils.SyncUtil;
import net.blueva.arcade.utils.TitlesUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.util.HashMap;

public class TrafficLightManager {
    private static final HashMap<Integer, BukkitTask> StartTasks = new HashMap<>();
    private static final HashMap<Integer, BukkitTask> GameTasks = new HashMap<>();
    public static final HashMap<Integer, String> ArenaLightStatus = new HashMap<>();
    private static final HashMap<Integer, Integer> ArenaLightTime = new HashMap<>();
    public static void Start(Integer arenaid, Main main) {
        ArenaManager.teleportPlayers(arenaid, "traffic_light", main);
        ArenaManager.sendDescription(arenaid, "traffic_light", main);
        startCountdown(arenaid, main);

        ArenaLightStatus.put(arenaid, "GREEN");
        ArenaLightTime.put(arenaid, 6);
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

    private static void startCountdown(Integer arenaid, Main main) {
        ArenaManager.ArenaActualGame.replace(arenaid, "StartingTrafficLight");

        for(final Player players : Bukkit.getOnlinePlayers()) {
            if(PlayerManager.PlayerStatus.containsKey(players) && PlayerManager.PlayerArena.containsKey(players)) {
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
                                            .replace("{game_display_name}", CacheManager.Language.MINI_GAMES_TRAFFIC_LIGHT_DISPLAY_NAME)
                                            .replace("{time}", String.valueOf(time)),
                                    CacheManager.Language.TITLES_STARTING_GAME_SUBTITLE
                                            .replace("{game_display_name}", CacheManager.Language.MINI_GAMES_TRAFFIC_LIGHT_DISPLAY_NAME)
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
                    startGreen(arenaid);
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
        ArenaManager.ArenaActualGame.replace(arenaid, "TrafficLight");

        for(final Player players : Bukkit.getOnlinePlayers()) {
            if(PlayerManager.PlayerStatus.containsKey(players) && PlayerManager.PlayerArena.containsKey(players)) {
                if (PlayerManager.PlayerStatus.get(players).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(players).equals(arenaid)) {
                    PlayerManager.PlayerMuted.replace(players.getPlayer(), 0);
                    SyncUtil.setFlying(main, false, players);
                    SyncUtil.setSpeed(main, 0.2f, players);
                    SoundsManager.playSounds(players, CacheManager.Sounds.SOUNDS_STARTING_GAME_START);
                    TitlesUtil.sendTitle(players,
                            CacheManager.Language.TITLES_GAME_STARTED_TITLE
                                    .replace("{game_display_name}", CacheManager.Language.MINI_GAMES_TRAFFIC_LIGHT_DISPLAY_NAME),
                            CacheManager.Language.TITLES_GAME_STARTED_SUBTITLE
                                    .replace("{game_display_name}", CacheManager.Language.MINI_GAMES_TRAFFIC_LIGHT_DISPLAY_NAME), 0, 0, 20);

                }
            }
        }

        GameTasks.remove(arenaid);
        GameTasks.put(arenaid, Bukkit.getScheduler().runTaskTimerAsynchronously(main, new Runnable() {

            int time = main.configManager.getArena(arenaid).getInt("arena.mini_games.traffic_light.basic.time", 60);
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

                if(this.time <= 0){
                    cancelGameTask(arenaid);
                }

                if(ArenaManager.ArenaStatusInternal.get(arenaid).equalsIgnoreCase("Waiting")) {
                    cancelGameTask(arenaid);
                }

                ArenaManager.ArenaCountdown.replace(arenaid, time);

                if (this.time <= 0) {
                    cancelGameTask(arenaid);
                    ArenaManager.sendSummaryGame(arenaid, main);
                    removeMaps(arenaid);
                    return;
                }

                if(ArenaManager.areAllPlayersSpectators(arenaid)) {
                    cancelGameTask(arenaid);
                    ArenaManager.sendSummaryGame(arenaid, main);
                    removeMaps(arenaid);
                    return;

                }

                if(ArenaManager.ArenaPodium.containsKey(arenaid) && ArenaManager.ArenaPodium.get(arenaid).containsKey(1) && ArenaManager.ArenaPodium.get(arenaid).containsKey(2) && ArenaManager.ArenaPodium.get(arenaid).containsKey(3)) {
                    cancelGameTask(arenaid);
                    ArenaManager.sendSummaryGame(arenaid, main);
                    removeMaps(arenaid);
                    return;
                }

                startGreen(arenaid);


                if(ArenaLightTime.get(arenaid).equals(0)) {
                    changeLightStatus(arenaid);
                }

                int LocalLightTime = ArenaLightTime.get(arenaid)-1;
                ArenaLightTime.replace(arenaid, LocalLightTime);

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

    private static void changeLightStatus(int arenaid) {
        if(ArenaLightStatus.get(arenaid).equalsIgnoreCase("GREEN")) {
            ArenaLightStatus.replace(arenaid, "YELLOW");
            ArenaLightTime.replace(arenaid, StringUtils.generateRandomNumber(1, 3));

            for(final Player all : Bukkit.getOnlinePlayers()) {
                if(PlayerManager.PlayerStatus.containsKey(all) && PlayerManager.PlayerArena.containsKey(all)) {
                    if (PlayerManager.PlayerStatus.get(all).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(all).equals(arenaid)) {
                        all.getInventory().clear();
                        giveItemsYellow(all);
                    }
                }
            }

        } else if(ArenaLightStatus.get(arenaid).equalsIgnoreCase("YELLOW")) {
            ArenaLightStatus.replace(arenaid, "RED");
            ArenaLightTime.replace(arenaid, StringUtils.generateRandomNumber(1, 8));

            for(final Player all : Bukkit.getOnlinePlayers()) {
                if(PlayerManager.PlayerStatus.containsKey(all) && PlayerManager.PlayerArena.containsKey(all)) {
                    if (PlayerManager.PlayerStatus.get(all).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(all).equals(arenaid)) {
                        all.getInventory().clear();
                        giveItemsRed(all);
                    }
                }
            }
        }else if(ArenaLightStatus.get(arenaid).equalsIgnoreCase("RED")) {
            ArenaLightStatus.replace(arenaid, "GREEN");
            ArenaLightTime.replace(arenaid, StringUtils.generateRandomNumber(3, 6));

            for(final Player all : Bukkit.getOnlinePlayers()) {
                if(PlayerManager.PlayerStatus.containsKey(all) && PlayerManager.PlayerArena.containsKey(all)) {
                    if (PlayerManager.PlayerStatus.get(all).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(all).equals(arenaid)) {
                        all.getInventory().clear();
                        giveItemsGreen(all);
                    }
                }
            }
        }
    }

    private static void startGreen(int arenaid) {
        for(final Player allp : Bukkit.getOnlinePlayers()) {
            if(PlayerManager.PlayerStatus.containsKey(allp) && PlayerManager.PlayerArena.containsKey(allp)) {
                if (PlayerManager.PlayerStatus.get(allp).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(allp).equals(arenaid)) {
                    if(ArenaLightStatus.get(arenaid).equalsIgnoreCase("GREEN")) {
                        giveItemsGreen(allp);
                    }
                }
            }
        }
    }

    private static void giveItemsRed(Player player) {
        try {
            ItemStack wool = new ItemStack(Material.RED_WOOL, 1);

            for (int i = 0; i < 9; i++) {
                player.getInventory().setItem(i, wool);
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }


    private static void giveItemsYellow(Player player) {
        try {
            ItemStack wool = new ItemStack(Material.YELLOW_WOOL, 1);

            for (int i = 0; i < 9; i++) {
                player.getInventory().setItem(i, wool);
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private static void giveItemsGreen(Player player) {
        try {
            ItemStack wool = new ItemStack(Material.LIME_WOOL, 1);

            for (int i = 0; i < 9; i++) {
                player.getInventory().setItem(i, wool);
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private static void removeMaps(int arenaid) {
        ArenaLightStatus.remove(arenaid);
        ArenaLightTime.remove(arenaid);
    }

    public static String getLightColor(int arenaid) {
        String arenaLight = ArenaLightStatus.get(arenaid);
        String uc = "██████";
        if(arenaLight != null) {
            if(arenaLight.equalsIgnoreCase("GREEN")) {
                return ChatColor.GREEN + uc;
            } else if(arenaLight.equalsIgnoreCase("YELLOW")) {
                return ChatColor.YELLOW + uc;
            } if(arenaLight.equalsIgnoreCase("RED")) {
                return ChatColor.RED + uc;
            }
        }
        return ChatColor.WHITE + uc;
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
