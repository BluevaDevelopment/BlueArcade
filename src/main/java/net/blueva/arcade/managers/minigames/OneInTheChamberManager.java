package net.blueva.arcade.managers.minigames;

import net.blueva.arcade.Main;
import net.blueva.arcade.managers.ArenaManager;
import net.blueva.arcade.managers.CacheManager;
import net.blueva.arcade.managers.PlayerManager;
import net.blueva.arcade.managers.SoundsManager;
import net.blueva.arcade.utils.ScoreboardUtil;
import net.blueva.arcade.utils.StringUtils;
import net.blueva.arcade.utils.SyncUtil;
import net.blueva.arcade.utils.TitlesUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

public class OneInTheChamberManager {
    private static final HashMap<Integer, BukkitTask> StartTasks = new HashMap<>();
    private static final HashMap<Integer, BukkitTask> GameTasks = new HashMap<>();
    private static final HashMap<Player, BukkitTask> PlayerTasks = new HashMap<>();
    public static final HashMap<Player, Integer> PlayerKills = new HashMap<>();
    public static final HashMap<Player, Integer> PlayerDeaths = new HashMap<>();
    public static void Start(Integer arenaid, Main main) {
        ArenaManager.teleportPlayers(arenaid, "one_in_the_chamber", main);
        ArenaManager.sendDescription(arenaid, "one_in_the_chamber", main);
        giveItems(arenaid);
        startCountdown(arenaid, main);
        registerPlayers(arenaid);
    }

    public static void finishPlayer(Integer arenaid, Player player) {
        if(PlayerManager.PlayerInGameStatus.containsKey(player)) {
            if(PlayerManager.PlayerInGameStatus.get(player).equalsIgnoreCase("Playing")) {
                ArenaManager.addPlayerToPodium(arenaid, player, 1);
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

    public static void executeFinishPlayerInOrder(Integer arenaid) {

        List<Player> players = new ArrayList<>(PlayerKills.keySet());
        players.sort(Comparator.comparingInt(PlayerKills::get).reversed());

        for (Player player : players) {
            if(PlayerManager.PlayerStatus.containsKey(player) && PlayerManager.PlayerArena.containsKey(player)) {
                if (PlayerManager.PlayerStatus.get(player).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(player).equals(arenaid)) {
                    finishPlayer(arenaid, player);
                }
            }
        }
    }

    public static void deathPlayer(Player player, Main main) {
        if(PlayerManager.PlayerInGameStatus.containsKey(player)) {
            if(PlayerManager.PlayerInGameStatus.get(player).equalsIgnoreCase("Playing")) {
                SyncUtil.setGameMode(main, GameMode.SPECTATOR, player);
                player.getInventory().clear();
                SoundsManager.playSounds(player, CacheManager.Sounds.SOUNDS_IN_GAME_DEAD);
                PlayerTasks.remove(player);
                PlayerTasks.put(player, Bukkit.getScheduler().runTaskLater(main, () -> {
                    player.teleport(Objects.requireNonNull(ArenaManager.getRandomSpawn(main, PlayerManager.PlayerArena.get(player), "one_in_the_chamber")));
                    player.setGameMode(GameMode.SURVIVAL);
                    giveItems(player);
                    SoundsManager.playSounds(player, CacheManager.Sounds.SOUNDS_IN_GAME_RESPAWN);
                }, 60L));
            }
        }
    }


    private static void startCountdown(Integer arenaid, @NotNull Main main) {
        ArenaManager.ArenaActualGame.replace(arenaid, "StartingOneInTheChamber");

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
                                            .replace("{game_display_name}", CacheManager.Language.MINI_GAMES_ONE_IN_THE_CHAMBER_DISPLAY_NAME)
                                            .replace("{time}", String.valueOf(time)),
                                    CacheManager.Language.TITLES_STARTING_GAME_SUBTITLE
                                            .replace("{game_display_name}", CacheManager.Language.MINI_GAMES_ONE_IN_THE_CHAMBER_DISPLAY_NAME)
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

    private static void startGame(Integer arenaid, @NotNull Main main) {
        ArenaManager.ArenaActualGame.replace(arenaid, "OneInTheChamber");

        for(final Player players : Bukkit.getOnlinePlayers()) {
            if(PlayerManager.PlayerStatus.containsKey(players) && PlayerManager.PlayerArena.containsKey(players)) {
                if (PlayerManager.PlayerStatus.get(players).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(players).equals(arenaid)) {
                    PlayerManager.PlayerMuted.replace(players.getPlayer(), 0);
                    SyncUtil.setFlying(main, false, players);
                    SoundsManager.playSounds(players, CacheManager.Sounds.SOUNDS_STARTING_GAME_START);
                    TitlesUtil.sendTitle(players,
                            CacheManager.Language.TITLES_GAME_STARTED_TITLE
                                    .replace("{game_display_name}", CacheManager.Language.MINI_GAMES_ONE_IN_THE_CHAMBER_DISPLAY_NAME),
                            CacheManager.Language.TITLES_GAME_STARTED_SUBTITLE
                                    .replace("{game_display_name}", CacheManager.Language.MINI_GAMES_ONE_IN_THE_CHAMBER_DISPLAY_NAME), 0, 0, 20);

                }
            }
        }

        GameTasks.remove(arenaid);
        GameTasks.put(arenaid, Bukkit.getScheduler().runTaskTimerAsynchronously(main, new Runnable() {

            int time = main.configManager.getArena(arenaid).getInt("arena.mini_games.one_in_the_chamber.basic.time", 60);
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
                    executeFinishPlayerInOrder(arenaid);
                    ArenaManager.sendSummaryGame(arenaid, main);
                    removeData(arenaid);
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

    private static void giveItems(int arenaid) {
        for(final Player players : Bukkit.getOnlinePlayers()) {
            if(PlayerManager.PlayerStatus.containsKey(players) && PlayerManager.PlayerArena.containsKey(players)) {
                if(PlayerManager.PlayerStatus.get(players).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(players).equals(arenaid)) {
                    giveItems(players);
                }
            }
        }
    }

    private static void giveItems(Player player) {
        ItemStack bow = new ItemStack(Material.BOW, 1);
        ItemStack arrow = new ItemStack(Material.ARROW, 1);

        ItemStack sword = new ItemStack(Material.IRON_SWORD, 1);
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        swordMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        sword.setItemMeta(swordMeta);

        player.getInventory().addItem(bow);
        player.getInventory().addItem(sword);
        player.getInventory().addItem(arrow);
    }

    public static void addArrow(Player player) {
        ItemStack arrow = new ItemStack(Material.ARROW, 1);
        player.getInventory().addItem(arrow);
    }

    private static void registerPlayers(int arenaid) {
        for(final Player players : Bukkit.getOnlinePlayers()) {
            if(PlayerManager.PlayerStatus.containsKey(players) && PlayerManager.PlayerArena.containsKey(players)) {
                if(PlayerManager.PlayerStatus.get(players).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(players).equals(arenaid)) {
                    PlayerKills.put(players, 0);
                    PlayerDeaths.put(players, 0);
                }
            }
        }
    }

    private static void removeData(int arenaid) {
        for(final Player players : Bukkit.getOnlinePlayers()) {
            if(PlayerManager.PlayerStatus.containsKey(players) && PlayerManager.PlayerArena.containsKey(players)) {
                if(PlayerManager.PlayerStatus.get(players).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(players).equals(arenaid)) {
                    PlayerKills.remove(players);
                    PlayerDeaths.remove(players);
                }
            }
        }
    }

    public static int getDeaths(Player player) {
        if (PlayerDeaths.containsKey(player)) {
            Integer deaths = PlayerDeaths.get(player);
            if(deaths == null) {
                return 0;
            }
            return deaths;
        }
        return 0;
    }

    public static int getKills(Player player) {
        if (PlayerKills.containsKey(player)) {
            Integer deaths = PlayerKills.get(player);
            if (deaths == null) {
                return 0;
            }
            return deaths;
        }
        return 0;
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
