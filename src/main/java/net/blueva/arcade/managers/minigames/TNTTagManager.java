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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;

public class TNTTagManager {

    private static final HashMap<Player, Boolean> playerHasTNT = new HashMap<>();
    private static final HashMap<Integer, Integer> arenaTaggedPlayers = new HashMap<>();
    private static final HashMap<Integer, Integer> arenaNonSpectatorsPlayers = new HashMap<>();
    private static final HashMap<Integer, BukkitTask> StartTasks = new HashMap<>();
    private static final HashMap<Integer, BukkitTask> GameTasks = new HashMap<>();
    public static void Start(Integer arenaid, Main main) {
        ArenaManager.teleportPlayers(arenaid, "tnt_tag", main);
        ArenaManager.sendDescription(arenaid, "tnt_tag", main);
        startCountdown(arenaid, main);
    }

    public static void finishPlayer(Integer arenaid, Player player) {
        if(PlayerManager.PlayerInGameStatus.containsKey(player)) {
            if(PlayerManager.PlayerInGameStatus.get(player).equalsIgnoreCase("Playing")) {
                ArenaManager.addPlayerToPodiumInReverseOrder(arenaid, player, ArenaManager.ArenaPlayerCountCache.get(arenaid));
                PlayerManager.PlayerInGameStatus.replace(player, "SPECTATOR");
                player.setGameMode(GameMode.SPECTATOR);
                player.getInventory().clear();
                SoundsManager.playSounds(Main.getPlugin(), player, CacheManager.Sounds.SOUNDS_IN_GAME_DEAD);
            }
        }
    }

    public static void finishPlayerSync(int arenaid, Player winner) {
        new BukkitRunnable() {
            @Override
            public void run() {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        finishPlayer(arenaid, winner);
                        cancel();
                    }
                }.runTask(Main.getPlugin());
            }
        }.runTaskAsynchronously(Main.getPlugin());
    }

    private static void startCountdown(Integer arenaid, @NotNull Main main) {
        ArenaManager.ArenaActualGame.replace(arenaid, "StartingTNTTag");

        StartTasks.remove(arenaid);
        StartTasks.put(arenaid, Bukkit.getScheduler().runTaskTimerAsynchronously(main, new Runnable() {

            int time = CacheManager.Settings.GAME_GLOBAL_GAME_COUNTDOWN;
            @Override
            public void run() {
                for(final Player players : Bukkit.getOnlinePlayers()) {
                    if(PlayerManager.PlayerStatus.containsKey(players)) {
                        if (PlayerManager.PlayerStatus.get(players).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(players).equals(arenaid)) {
                            SoundsManager.playSounds(main, players, CacheManager.Sounds.SOUNDS_STARTING_GAME_COUNTDOWN);
                            TitlesUtil.sendTitle(players, CacheManager.Language.TITLES_STARTING_GAME_TITLE
                                            .replace("{game_display_name}", CacheManager.Language.MINI_GAMES_TNT_TAG_DISPLAY_NAME)
                                            .replace("{time}", String.valueOf(time)),
                                    CacheManager.Language.TITLES_STARTING_GAME_SUBTITLE
                                            .replace("{game_display_name}", CacheManager.Language.MINI_GAMES_TNT_TAG_DISPLAY_NAME)
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
        ArenaManager.ArenaActualGame.replace(arenaid, "TNTTag");

        for(final Player players : Bukkit.getOnlinePlayers()) {
            if(PlayerManager.PlayerStatus.containsKey(players) && PlayerManager.PlayerMuted.containsKey(players)) {
                if (PlayerManager.PlayerStatus.get(players).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(players).equals(arenaid)) {
                    PlayerManager.PlayerMuted.replace(players.getPlayer(), 0);
                    SyncUtil.setFlying(main, false, players);
                    SoundsManager.playSounds(main, players, CacheManager.Sounds.SOUNDS_STARTING_GAME_START);
                    TitlesUtil.sendTitle(players,
                            CacheManager.Language.TITLES_GAME_STARTED_TITLE
                                    .replace("{game_display_name}", CacheManager.Language.MINI_GAMES_TNT_TAG_DISPLAY_NAME),
                            CacheManager.Language.TITLES_GAME_STARTED_SUBTITLE
                                    .replace("{game_display_name}", CacheManager.Language.MINI_GAMES_TNT_TAG_DISPLAY_NAME), 0, 0, 20);

                }
            }
        }

        checkNonSpectators(arenaid, false);
        setTaggedPlayers(arenaid);

        GameTasks.remove(arenaid);
        GameTasks.put(arenaid, Bukkit.getScheduler().runTaskTimerAsynchronously(main, new Runnable() {

            int time = main.configManager.getArena(arenaid).getInt("arena.mini_games.tnt_tag.basic.time", 60);
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
                    removeTaggedPlayers(arenaid);
                    checkNonSpectators(arenaid, false);

                    time = main.configManager.getArena(arenaid).getInt("arena.mini_games.tnt_tag.basic.time", 60);

                    if(arenaNonSpectatorsPlayers.get(arenaid) != 1)  {
                        setTaggedPlayers(arenaid);
                    }
                }

                if(checkNonSpectators(arenaid, true) || arenaNonSpectatorsPlayers.get(arenaid).equals(0)) {
                    cancelGameTask(arenaid);

                    Bukkit.getScheduler().runTaskLaterAsynchronously(main, () -> {
                        ArenaManager.sendSummaryGame(arenaid, main);
                    }, 10L);
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

    private static void setTaggedPlayers(int arenaid) {
        checkPlayers(arenaid);

        int numTaggedPlayers = arenaTaggedPlayers.get(arenaid);
        HashMap<Player, Boolean> candidates = new HashMap<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (PlayerManager.PlayerStatus.containsKey(player) && PlayerManager.PlayerArena.containsKey(player) && PlayerManager.PlayerInGameStatus.containsKey(player)) {
                if (PlayerManager.PlayerStatus.get(player).equalsIgnoreCase("Playing")
                        && PlayerManager.PlayerArena.get(player).equals(arenaid)
                        && PlayerManager.PlayerInGameStatus.get(player).equalsIgnoreCase("PLAYING")) {
                    playerHasTNT.putIfAbsent(player, false);
                    candidates.put(player, true);
                }
            }
        }

        while (!candidates.isEmpty() && numTaggedPlayers > 0) {
            int randomIndex = (int) (Math.random() * candidates.size());
            Player randomPlayer = (Player) candidates.keySet().toArray()[randomIndex];
            playerHasTNT.replace(randomPlayer, true);
            candidates.remove(randomPlayer);
            numTaggedPlayers--;
            StringUtils.sendMessage(randomPlayer, randomPlayer.getName(), CacheManager.Language.GLOBAL_INFO_YOU_HAVE_TNT);

            changeInventory(randomPlayer);
        }
    }

    public static void changePlayerTagged(Player oldPlayer, Player newPlayer) {
        if (playerHasTNT.containsKey(oldPlayer) && playerHasTNT.get(oldPlayer)) {
            playerHasTNT.replace(oldPlayer, false);
        }

        playerHasTNT.replace(newPlayer, true);

        StringUtils.sendMessage(oldPlayer, oldPlayer.getName(), CacheManager.Language.GLOBAL_INFO_YOU_NO_HAVE_TNT);
        StringUtils.sendMessage(newPlayer, newPlayer.getName(), CacheManager.Language.GLOBAL_INFO_YOU_HAVE_TNT);

        changeInventory(newPlayer);
        oldPlayer.getInventory().clear();
    }

    /*private static void checkNonSpectatorsPlayers(int arenaid) {
        arenaNonSpectatorsPlayers.putIfAbsent(arenaid, 0);
        arenaNonSpectatorsPlayers.replace(arenaid, 0);
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(PlayerManager.PlayerStatus.containsKey(player) && PlayerManager.PlayerArena.containsKey(player)) {
                if(PlayerManager.PlayerStatus.get(player).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(player).equals(arenaid)) {
                    if(PlayerManager.PlayerInGameStatus.get(player).equalsIgnoreCase("PLAYING")) {
                        arenaNonSpectatorsPlayers.replace(arenaid, arenaNonSpectatorsPlayers.get(arenaid)+1);
                    }
                }
            }
        }
    }*/

    private static void checkPlayers(int arenaid) {
        arenaTaggedPlayers.putIfAbsent(arenaid, 0);
        if(arenaNonSpectatorsPlayers.get(arenaid) >= 20) {
            arenaTaggedPlayers.replace(arenaid, 5);
        } else if(arenaNonSpectatorsPlayers.get(arenaid) >= 15) {
            arenaTaggedPlayers.replace(arenaid, 4);
        } else if(arenaNonSpectatorsPlayers.get(arenaid) >= 10) {
            arenaTaggedPlayers.replace(arenaid, 3);
        } else if(arenaNonSpectatorsPlayers.get(arenaid) >= 5) {
            arenaTaggedPlayers.replace(arenaid, 2);
        } else {
            arenaTaggedPlayers.replace(arenaid, 1);
        }
    }

    private static void removeTaggedPlayers(int arenaid) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(PlayerManager.PlayerStatus.containsKey(player) && PlayerManager.PlayerArena.containsKey(player)) {
                if(PlayerManager.PlayerStatus.get(player).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(player).equals(arenaid)) {
                    if(PlayerManager.PlayerInGameStatus.get(player).equalsIgnoreCase("PLAYING")) {
                        if(playerHasTNT.containsKey(player) && playerHasTNT.get(player)) {
                            //player.getWorld().createExplosion(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), 2.5f, false, false);
                            finishPlayerSync(arenaid, player);
                        }
                    }
                }
            }
        }
    }

    private static boolean checkNonSpectators(int arenaid, boolean checkWinner) {
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

        if(checkWinner) {
            if (nonSpectatorCount == 1 && winner != null) {
                finishPlayerSync(arenaid, winner);
                return true;
            }
        }

        arenaNonSpectatorsPlayers.putIfAbsent(arenaid, 0);
        arenaNonSpectatorsPlayers.replace(arenaid, nonSpectatorCount);

        return false;
    }

    private static void changeInventory(Player player) {
        player.getInventory().clear();
        ItemStack tnt = new ItemStack(Material.TNT, 1);
        player.getInventory().setItem(0, tnt);
        player.getInventory().setItem(1, tnt);
        player.getInventory().setItem(2, tnt);
        player.getInventory().setItem(3, tnt);
        player.getInventory().setItem(4, tnt);
        player.getInventory().setItem(5, tnt);
        player.getInventory().setItem(6, tnt);
        player.getInventory().setItem(7, tnt);
        player.getInventory().setItem(8, tnt);

        player.getInventory().setHelmet(tnt);
    }

    public static String playerIsTagged(Player player) {
        if(playerHasTNT.containsKey(player) && playerHasTNT.get(player)) {
            return ChatColor.GREEN + "✔";
        }
        return ChatColor.RED + "✘";
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
