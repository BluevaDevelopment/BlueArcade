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
import java.util.HashMap;

public class KnockBackManager {
    private static final HashMap<Integer, BukkitTask> StartTasks = new HashMap<>();
    private static final HashMap<Integer, BukkitTask> GameTasks = new HashMap<>();
    public static final HashMap<Player, Integer> PlayerKills = new HashMap<>();
    public static final HashMap<Player, Player> lastDamager = new HashMap<>();

    public static void Start(Integer arenaid, Main main) {
        ArenaManager.teleportPlayers(arenaid, "knock_back", main);
        ArenaManager.sendDescription(arenaid, "knock_back", main);
        giveItems(arenaid);
        startCountdown(arenaid, main);
        registerPlayers(arenaid);
    }

    public static void finishPlayer(Integer arenaid, Player player, Boolean dead) {
        if(PlayerManager.PlayerInGameStatus.containsKey(player)) {
            if(PlayerManager.PlayerInGameStatus.get(player).equalsIgnoreCase("Playing")) {
                ArenaManager.addPlayerToPodiumInReverseOrder(arenaid, player, ArenaManager.ArenaPlayerCountCache.get(arenaid));
                PlayerManager.PlayerInGameStatus.replace(player, "SPECTATOR");
                player.setGameMode(GameMode.SPECTATOR);
                player.getInventory().clear();
                if(dead) {
                    Player lD = lastDamager.get(player);
                    PlayerKills.replace(lD, PlayerKills.get(lD)+1);
                    SoundsManager.playSounds(player, CacheManager.Sounds.SOUNDS_IN_GAME_DEAD);
                    TitlesUtil.sendTitle(player,
                            CacheManager.Language.TITLES_YOU_DIED_TITLE
                                    .replace("{place}", String.valueOf(ArenaManager.getPlayerPositionOnPodium(arenaid, player))),
                            CacheManager.Language.TITLES_YOU_DIED_SUBTITLE
                                    .replace("{place}", String.valueOf(ArenaManager.getPlayerPositionOnPodium(arenaid, player)))
                            , 0, 80, 20);
                } else {
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
    }

    public static void finishPlayerSync(int arenaid, Player winner, Boolean dead) {
        Bukkit.getScheduler().runTask(Main.getPlugin(), () -> {
            finishPlayer(arenaid, winner, dead);
        });
    }

    private static void startCountdown(Integer arenaid, @NotNull Main main) {
        ArenaManager.ArenaActualGame.replace(arenaid, "StartingKnockBack");

        StartTasks.remove(arenaid);
        StartTasks.put(arenaid, Bukkit.getScheduler().runTaskTimerAsynchronously(main, new Runnable() {

            int time = CacheManager.Settings.GAME_GLOBAL_GAME_COUNTDOWN;
            @Override
            public void run() {
                for(final Player players : Bukkit.getOnlinePlayers()) {
                    if(PlayerManager.PlayerStatus.containsKey(players)) {
                        if (PlayerManager.PlayerStatus.get(players).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(players).equals(arenaid)) {
                            SoundsManager.playSounds(players, CacheManager.Sounds.SOUNDS_STARTING_GAME_COUNTDOWN);
                            TitlesUtil.sendTitle(players, CacheManager.Language.TITLES_STARTING_GAME_TITLE
                                            .replace("{game_display_name}", CacheManager.Language.MINI_GAMES_KNOCK_BACK_DISPLAY_NAME)
                                            .replace("{time}", String.valueOf(time)),
                                    CacheManager.Language.TITLES_STARTING_GAME_SUBTITLE
                                            .replace("{game_display_name}", CacheManager.Language.MINI_GAMES_KNOCK_BACK_DISPLAY_NAME)
                                            .replace("{time}", String.valueOf(time)), 0, 20, 5);
                        }
                    }
                }

                if(this.time <= 0){
                    BukkitTask task = StartTasks.get(arenaid);
                    if(task != null) {
                        task.cancel();
                        StartTasks.remove(arenaid);
                    }
                    startGame(arenaid, main);
                    return;
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
        ArenaManager.ArenaActualGame.replace(arenaid, "KnockBack");

        for(final Player players : Bukkit.getOnlinePlayers()) {
            if(PlayerManager.PlayerStatus.containsKey(players) && PlayerManager.PlayerMuted.containsKey(players)) {
                if (PlayerManager.PlayerStatus.get(players).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(players).equals(arenaid)) {
                    PlayerManager.PlayerMuted.replace(players.getPlayer(), 0);
                    SyncUtil.setFlying(main, false, players);
                    SoundsManager.playSounds(players, CacheManager.Sounds.SOUNDS_STARTING_GAME_START);
                    TitlesUtil.sendTitle(players,
                            CacheManager.Language.TITLES_GAME_STARTED_TITLE
                                    .replace("{game_display_name}", CacheManager.Language.MINI_GAMES_KNOCK_BACK_DISPLAY_NAME),
                            CacheManager.Language.TITLES_GAME_STARTED_SUBTITLE
                                    .replace("{game_display_name}", CacheManager.Language.MINI_GAMES_KNOCK_BACK_DISPLAY_NAME), 0, 0, 20);

                }
            }
        }

        GameTasks.remove(arenaid);
        GameTasks.put(arenaid, Bukkit.getScheduler().runTaskTimerAsynchronously(main, new Runnable() {

            int time = main.configManager.getArena(arenaid).getInt("arena.mini_games.knock_back.basic.time", 60);
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
                    removeData(arenaid);
                    return;
                }

                if(checkWinner(arenaid)) {
                    cancelGameTask(arenaid);

                    Bukkit.getScheduler().runTaskLaterAsynchronously(main, () -> {
                        ArenaManager.sendSummaryGame(arenaid, main);
                        removeData(arenaid);
                    }, 10L);
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
        ItemStack stick = new ItemStack(Material.STICK, 1);
        ItemMeta stickmeta = stick.getItemMeta();
        stickmeta.addEnchant(Enchantment.KNOCKBACK, 3, true);
        stickmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        stick.setItemMeta(stickmeta);

        for(final Player allp : Bukkit.getOnlinePlayers()) {
            if(PlayerManager.PlayerStatus.containsKey(allp) && PlayerManager.PlayerArena.containsKey(allp)) {
                if(PlayerManager.PlayerStatus.get(allp).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(allp).equals(arenaid)) {
                    allp.getInventory().setItem(0, stick);
                }
            }
        }
    }

    // METODO ACTUALIZADO, NO PROBADO
    private static boolean checkWinner(int arenaid) {
        int nonSpectatorCount = 0;
        Player winner = null;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (PlayerManager.PlayerArena.containsKey(player) && PlayerManager.PlayerArena.get(player).equals(arenaid)) {
                if (!PlayerManager.PlayerInGameStatus.get(player).equalsIgnoreCase("SPECTATOR")) {
                    nonSpectatorCount++;
                    if (nonSpectatorCount > 1) {
                        // Hay más de un jugador no espectador, no hay un ganador claro
                        return false;
                    }
                    winner = player;
                }
            }
        }

        if (nonSpectatorCount == 1 && winner != null) {
            finishPlayerSync(arenaid, winner, false);
            return true;
        }

        return false;
    }


    private static void registerPlayers(int arenaid) {
        for(final Player players : Bukkit.getOnlinePlayers()) {
            if(PlayerManager.PlayerStatus.containsKey(players) && PlayerManager.PlayerArena.containsKey(players)) {
                if(PlayerManager.PlayerStatus.get(players).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(players).equals(arenaid)) {
                    PlayerKills.put(players, 0);
                    lastDamager.put(players, players);
                }
            }
        }
    }

    private static void removeData(int arenaid) {
        for(final Player players : Bukkit.getOnlinePlayers()) {
            if(PlayerManager.PlayerStatus.containsKey(players) && PlayerManager.PlayerArena.containsKey(players)) {
                if(PlayerManager.PlayerStatus.get(players).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(players).equals(arenaid)) {
                    PlayerKills.remove(players);
                    lastDamager.remove(players);
                }
            }
        }
    }

    public static int getKills(Player player) {
        if (PlayerKills.containsKey(player)) {
            Integer kills = PlayerKills.get(player);
            if (kills == null) {
                return 0;
            }
            return kills;
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
