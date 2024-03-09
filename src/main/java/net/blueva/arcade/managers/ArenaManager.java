package net.blueva.arcade.managers;

import net.blueva.arcade.Main;
import net.blueva.arcade.managers.minigames.*;
import net.blueva.arcade.utils.StringUtils;
import net.blueva.arcade.utils.SyncUtil;
import net.blueva.arcade.utils.TitlesUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.util.*;

public class ArenaManager {

    //arena lists
    public static ArrayList<Integer> ArenaList = new ArrayList<>();
    public static Map<Integer, String> ArenaStatusInternal = new HashMap<>(); //Waiting, Running, Restarting
    public static Map<Integer, String> ArenaStatus = new HashMap<>(); //Disabled, Enabled
    public static Map<Integer, String> ArenaActualGame = new HashMap<>(); //Lobby, Starting, Race, ...
    public static Map<Integer, Player> ArenaPlayers = new HashMap<>();
    public static Map<Integer, Integer> ArenaPlayersCount = new HashMap<>();
    public static Map<Integer, Integer> ArenaPlayerCountCache = new HashMap<>();
    public static Map<Integer, Integer> ArenaCountdown = new HashMap<>();
    public static Map<Integer, Integer> ArenaRound = new HashMap<>();
    public static Map<Integer, Map<Integer, Player>> ArenaPodium = new HashMap<>();
    public static Map<Integer, Map<Player, Integer>> ArenaStars = new HashMap<>();
    private static final Map<Integer, List<String>> ArenaMinigames = new HashMap<>();

    //other methods
    private static final Random random = new Random();
    private static final HashMap<Integer, BukkitTask> StartTasks = new HashMap<>();
    private static final HashMap<Integer, BukkitTask> LimboTasks = new HashMap<>();
    private static final HashMap<Integer, BukkitTask> EndTasks = new HashMap<>();

    public static void startArena(Main main, int arenaid, Player player)  {
        if(ArenaActualGame.get(PlayerManager.PlayerArena.get(player)).equalsIgnoreCase("Lobby")) {
            ArenaActualGame.replace(PlayerManager.PlayerArena.get(player), "Starting");

            StartTasks.remove(arenaid);
            StartTasks.put(arenaid, Bukkit.getScheduler().runTaskTimerAsynchronously(main, new Runnable() {

                int time = CacheManager.Settings.GAME_GLOBAL_LOBBY_COUNTDOWN;
                @Override
                public void run() {
                    if(this.time <= 0){
                        BukkitTask task = StartTasks.get(arenaid);
                        if(task != null) {
                            task.cancel();
                            StartTasks.remove(arenaid);
                        }
                        nextGame(arenaid, main);
                    }

                    ArenaCountdown.replace(arenaid, time);

                    SoundsManager.playSounds(player, CacheManager.Sounds.SOUNDS_STARTING_ARENA_COUNTDOWN);

                    List<Integer> notifyTimes = CacheManager.Settings.GAME_GLOBAL_COUNTDOWN_NOTIFICATIONS;

                    for(final Player allp : Bukkit.getOnlinePlayers()) {
                        if(PlayerManager.PlayerStatus.containsKey(allp) && PlayerManager.PlayerArena.containsKey(allp)) {
                            if(PlayerManager.PlayerStatus.get(allp).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(allp).equals(arenaid)) {
                                if(notifyTimes.contains(time)) {
                                    SoundsManager.playSounds(player, CacheManager.Sounds.SOUNDS_STARTING_ARENA_NOTIFY_COUNTDOWN);
                                    StringUtils.sendMessage(allp, player.getName(), CacheManager.Language.GLOBAL_INFO_LOBBY_COUNTDOWN
                                            .replace("{time}", String.valueOf(time)));
                                    TitlesUtil.sendTitle(allp, CacheManager.Language.TITLES_STARTING_ARENA_TITLE.replace("{time}", String.valueOf(time)),
                                            CacheManager.Language.TITLES_STARTING_ARENA_SUBTITLE.replace("{time}", String.valueOf(time)), 0, 60, 20);
                                }
                                allp.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(StringUtils.formatMessage(player.getName(), CacheManager.Language.ACTION_BAR_LOBBY_STARTING
                                        .replace("{time}", String.valueOf(time)))));
                            }
                        }
                    }

                    if(CacheManager.Arenas.ARENA_BASIC_INTEGER.get(arenaid).get("min_players") > ArenaManager.ArenaPlayersCount.get(arenaid)) {
                        for(final Player allp : Bukkit.getOnlinePlayers()) {
                            if(PlayerManager.PlayerStatus.containsKey(allp) && PlayerManager.PlayerArena.containsKey(allp)) {
                                if(PlayerManager.PlayerStatus.get(allp).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(allp).equals(arenaid)) {
                                    StringUtils.sendMessage(allp, player.getName(), CacheManager.Language.GLOBAL_ERROR_ARENA_STOPPED
                                            .replace("{min_players}", String.valueOf(CacheManager.Arenas.ARENA_BASIC_INTEGER.get(arenaid).get("min_players"))));
                                    ArenaActualGame.replace(arenaid, "Lobby");
                                }
                            }
                        }

                        BukkitTask task = StartTasks.get(arenaid);
                        if(task != null) {
                            task.cancel();
                            StartTasks.remove(arenaid);
                        }

                        return;
                    }

                    this.time--;
                }
            }, 0L, 20L));
        }
    }

    public static void stopArena(Main main, int arenaid) throws IOException {
        ArenaStatusInternal.replace(arenaid, "Waiting");
        ArenaActualGame.replace(arenaid, "Lobby");
        ArenaRound.remove(arenaid);
        ArenaPodium.remove(arenaid);
        ArenaStars.remove(arenaid);

        CacheManager.Bounds.removeBoundsCache(arenaid);

        for(final Player player : Bukkit.getOnlinePlayers()) {
            if(PlayerManager.PlayerStatus.containsKey(player) && PlayerManager.PlayerArena.containsKey(player)) {
                if(PlayerManager.PlayerStatus.get(player).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(player).equals(arenaid)) {
                    PlayerManager.LeavePlayer(main, arenaid, player, false);
                }
            }
        }

        BukkitTask task = null;
        task = StartTasks.get(arenaid);
        if(task != null) {
            task.cancel();
            StartTasks.remove(arenaid);
        }
        task = LimboTasks.get(arenaid);
        if(task != null) {
            task.cancel();
            LimboTasks.remove(arenaid);
        }
        task = EndTasks.get(arenaid);
        if(task != null) {
            task.cancel();
            EndTasks.remove(arenaid);
        }
    }

    private static void nextGame(int arenaid, Main main) {
        ArenaStatusInternal.replace(arenaid, "Running");

        if(!ArenaRound.containsKey(arenaid)) {
            ArenaRound.put(arenaid, 1);
        } else {
            ArenaRound.replace(arenaid, ArenaRound.get(arenaid)+1);
        }

        if(ArenaRound.get(arenaid) > CacheManager.Arenas.ARENA_BASIC_INTEGER.get(arenaid).get("rounds")) {
            sendSummaryFinal(arenaid, main);
            return;
        }

        if(ArenaManager.ArenaStatusInternal.get(arenaid).equalsIgnoreCase("Running") && CacheManager.Arenas.ARENA_BASIC_INTEGER.get(arenaid).get("min_players") > ArenaManager.ArenaPlayersCount.get(arenaid)) {
            sendSummaryFinal(arenaid, main);

            for(final Player allp : Bukkit.getOnlinePlayers()) {
                if(PlayerManager.PlayerStatus.containsKey(allp) && PlayerManager.PlayerArena.containsKey(allp)) {
                    StringUtils.sendMessage(allp, allp.getName(), CacheManager.Language.GLOBAL_ERROR_ARENA_STOPPED
                            .replace("{min_players}", String.valueOf(CacheManager.Arenas.ARENA_BASIC_INTEGER.get(arenaid).get("min_players"))));
                }
            }

            return;
        }

        if(!ArenaMinigames.containsKey(arenaid) || ArenaMinigames.get(arenaid).isEmpty()) {
            startGames(arenaid, main);
        }

        String randomgame = String.valueOf(ArenaMinigames.get(arenaid).get(random.nextInt(ArenaMinigames.get(arenaid).size())));
        ArenaMinigames.get(arenaid).remove(randomgame);

        clearPodium(arenaid);

        ArenaPlayerCountCache.remove(arenaid);
        ArenaPlayerCountCache.put(arenaid, ArenaPlayersCount.get(arenaid));

        if(randomgame.equalsIgnoreCase("race")) {
            CacheManager.Bounds.saveBounds(main, arenaid, "race");
            CacheManager.Bounds.saveFinishBounds(main, arenaid, "race");
            RaceManager.Start(arenaid, main);
        } else if (randomgame.equalsIgnoreCase("spleef")) {
            CacheManager.Bounds.saveBounds(main, arenaid, "spleef");
            SpleefManager.Start(arenaid, main);
        } else if (randomgame.equalsIgnoreCase("snowball_fight")) {
            CacheManager.Bounds.saveBounds(main, arenaid, "snowball_fight");
            SnowballFightManager.Start(arenaid, main);
        } else if (randomgame.equalsIgnoreCase("all_against_all")) {
            CacheManager.Bounds.saveBounds(main, arenaid, "all_against_all");
            AllAgainstAllManager.Start(arenaid, main);
        } else if (randomgame.equalsIgnoreCase("one_in_the_chamber")) {
            CacheManager.Bounds.saveBounds(main, arenaid, "one_in_the_chamber");
            OneInTheChamberManager.Start(arenaid, main);
        } else if (randomgame.equals("traffic_light")) {
            CacheManager.Bounds.saveBounds(main, arenaid, "traffic_light");
            CacheManager.Bounds.saveFinishBounds(main, arenaid, "traffic_light");
            TrafficLightManager.Start(arenaid, main);
        } else if (randomgame.equals("minefield")) {
            CacheManager.Bounds.saveBounds(main, arenaid, "minefield");
            CacheManager.Bounds.saveFinishBounds(main, arenaid, "minefield");
            MinefieldManager.Start(arenaid, main);
        } else if (randomgame.equals("exploding_sheep")) {
            CacheManager.Bounds.saveBounds(main, arenaid, "exploding_sheep");
            ExplodingSheepManager.Start(arenaid, main);
        }  else if (randomgame.equals("tnt_tag")) {
            CacheManager.Bounds.saveBounds(main, arenaid, "tnt_tag");
            TNTTagManager.Start(arenaid, main);
        } else if (randomgame.equals("red_alert")) {
            CacheManager.Bounds.saveBounds(main, arenaid, "red_alert");
            RedAlertManager.Start(arenaid, main);
        } else if (randomgame.equals("knock_back")) {
            CacheManager.Bounds.saveBounds(main, arenaid, "knock_back");
            KnockBackManager.Start(arenaid, main);
        } else if (randomgame.equals("fast_zone")) {
            CacheManager.Bounds.saveBounds(main, arenaid, "fast_zone");
            CacheManager.Bounds.saveFinishBounds(main, arenaid, "fast_zone");
            FastZoneManager.Start(arenaid, main);
        }

        for(final Player allp : Bukkit.getOnlinePlayers()) {
            if(PlayerManager.PlayerStatus.containsKey(allp) && PlayerManager.PlayerArena.containsKey(allp)) {
                SoundsManager.playSounds(allp, CacheManager.Sounds.SOUNDS_STARTING_GAME_TELEPORT);
            }
        }

        CacheManager.Arenas.updateDisplayName(arenaid);
    }


    private static void startGames(int arenaid, Main main) {
        ConfigurationSection cs = main.configManager.getArena(arenaid).getConfigurationSection("arena.mini_games");
        if(cs != null) {
            List<String> mini_games_list = new ArrayList<>();
            for(Object mini_game : cs.getKeys(false)) {
                if(main.configManager.getArena(arenaid).getBoolean("arena.mini_games."+mini_game+".basic.enabled")) {
                    mini_games_list.add(mini_game.toString());
                }
            }
            ArenaMinigames.put(arenaid, mini_games_list);
        }
    }

    public static void startLimbo(int arenaid, Main main) {
        ArenaActualGame.replace(arenaid, "Limbo");

        if(CacheManager.Settings.GAME_GLOBAL_FLIGHT_ON_FINISH) {
            for(Player limbop : Bukkit.getOnlinePlayers()) {
                if(PlayerManager.PlayerStatus.containsKey(limbop) && PlayerManager.PlayerArena.containsKey(limbop)) {
                    if(PlayerManager.PlayerStatus.get(limbop).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(limbop).equals(arenaid)) {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
                            limbop.setGameMode(GameMode.ADVENTURE);
                            limbop.setVelocity(limbop.getLocation().getDirection().multiply(5).setY(5));
                            SyncUtil.setFlying(main, true, limbop);
                            //limbop.playSound(limbop.getLocation(), Sound.ENTITY_WITHER_DEATH, 3.0F, 0.5F);
                        });
                    }
                }
            }
        }
        LimboTasks.remove(arenaid);
        LimboTasks.put(arenaid, Bukkit.getScheduler().runTaskTimerAsynchronously(main, new Runnable() {
            int time = CacheManager.Settings.GAME_GLOBAL_LIMBO_COUNTDOWN;
            boolean shouldCancel = false; // agregar variable booleana
            @Override
            public void run() {
                if (shouldCancel) { // verificar si la tarea debe cancelarse
                    BukkitTask task = LimboTasks.get(arenaid);
                    if(task != null) {
                        task.cancel();
                        LimboTasks.remove(arenaid);
                    }
                    return; // salir del método run()
                }

                if(this.time <= 0){
                    shouldCancel = true; // establecer shouldCancel a true

                    if(CacheManager.Settings.GAME_GLOBAL_FLIGHT_ON_FINISH) {
                        for(Player limbop : Bukkit.getOnlinePlayers()) {
                            if(PlayerManager.PlayerStatus.containsKey(limbop) && PlayerManager.PlayerArena.containsKey(limbop)) {
                                if(PlayerManager.PlayerStatus.get(limbop).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(limbop).equals(arenaid)) {
                                    Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
                                        SyncUtil.setFlying(main, false, limbop);
                                    });
                                }
                            }
                        }
                    }

                    nextGame(arenaid, main);
                }

                ArenaCountdown.replace(arenaid, time);

                this.time--;
            }
        }, 0L, 20L));
    }

    public static void sendDescription(Integer arenaid, String game, Main main) {
        List<String> description = CacheManager.Language.getGameDescription(game);
        for(Player descp : Bukkit.getOnlinePlayers()) {
            String player = descp.getName();
            if(PlayerManager.PlayerStatus.containsKey(descp) && PlayerManager.PlayerArena.containsKey(descp)) {
                if(PlayerManager.PlayerStatus.get(descp).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(descp).equals(arenaid)) {
                    assert description != null;
                    for (String message : description) {
                        StringUtils.sendMessage(descp, player, message.replace("{game_display_name}", Objects.requireNonNull(main.configManager.getLang().getString("mini_games." + game + ".display_name"))));
                    }
                    SyncUtil.removePotionEffects(main, descp);
                    SyncUtil.setFlying(main, false, descp);
                    descp.setFoodLevel(20);
                    descp.setHealth(20);
                    SyncUtil.setGameMode(main, GameMode.ADVENTURE, descp);
                }
            }
        }
    }

    public static void teleportPlayers(Integer arena, String game, Main main) {
        int totalspawns = main.configManager.getArena(arena).getInt("arena.mini_games."+game+".spawns.total");
        int usedspawns = 1;
        for(final Player allp : Bukkit.getOnlinePlayers()) {
            if(PlayerManager.PlayerStatus.containsKey(allp) && PlayerManager.PlayerArena.containsKey(allp)) {
                if(PlayerManager.PlayerStatus.get(allp).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(allp).equals(arena)) {
                    if(usedspawns >= totalspawns) {
                        usedspawns = 1;
                    }

                    int finalUsedspawns = usedspawns;
                    Bukkit.getScheduler().runTask(Main.getPlugin(), () -> {
                        World world = Bukkit.getWorld(Objects.requireNonNull(main.configManager.getArena(arena).getString("arena.mini_games." + game + ".basic.world")));
                        double x = main.configManager.getArena(arena).getDouble("arena.mini_games."+game+".spawns.list.s"+ finalUsedspawns +".x");
                        double y = main.configManager.getArena(arena).getDouble("arena.mini_games."+game+".spawns.list.s"+ finalUsedspawns +".y");
                        double z = main.configManager.getArena(arena).getDouble("arena.mini_games."+game+".spawns.list.s"+ finalUsedspawns +".z");
                        double yaw = main.configManager.getArena(arena).getDouble("arena.mini_games."+game+".spawns.list.s"+ finalUsedspawns +".yaw");
                        double pitch = main.configManager.getArena(arena).getDouble("arena.mini_games."+game+".spawns.list.s"+ finalUsedspawns +".pitch");
                        allp.teleport(new Location(world, x, y, z, (float) yaw, (float) pitch));
                    });

                    SyncUtil.setFlying(main, false, allp);
                    allp.getInventory().clear();

                    usedspawns = usedspawns+1;

                    PlayerManager.PlayerMuted.replace(allp.getPlayer(), 1);
                }
            }
        }

    }

    public static Location getRandomSpawn(Main main, Integer arenaid, String game) {
        String worldName = main.configManager.getArena(arenaid).getString("arena.mini_games." + game + ".basic.world");
        World world = null;
        if(worldName != null) {
            world = Bukkit.getWorld(worldName);
        }

        ConfigurationSection spawns = main.configManager.getArena(arenaid).getConfigurationSection("arena.mini_games." + game + ".spawns");

        int totalSpawns = 0;
        if(spawns != null) {
            totalSpawns = spawns.getInt("total");
        }

        if (totalSpawns == 0) {
            return null;
        }

        String spawnName = "list.s" + (new Random().nextInt(totalSpawns) + 1);
        double x = spawns.getDouble(spawnName + ".x");
        double y = spawns.getDouble(spawnName + ".y");
        double z = spawns.getDouble(spawnName + ".z");
        float yaw = (float) spawns.getDouble(spawnName + ".yaw");
        float pitch = (float) spawns.getDouble(spawnName + ".pitch");

        return new Location(world, x, y, z, yaw, pitch);
    }


    public static void addPlayerToPodium(int arenaid, Player player, int position) {
        Map<Integer, Player> podium = ArenaPodium.computeIfAbsent(arenaid, k -> new HashMap<>());

        while (podium.containsKey(position)) {
            position++;
        }

        podium.put(position, player);
    }

    public static void addPlayerToPodiumInReverseOrder(int arenaid, Player player, int maxPlayers) {
        Map<Integer, Player> podium = ArenaPodium.computeIfAbsent(arenaid, k -> new HashMap<>());

        if (podium.containsValue(player)) {
            return;
        }

        int position = maxPlayers;
        while (podium.containsKey(position)) {
            position--;
        }

        podium.put(position, player);
    }

    public static int getPlayerPositionOnPodium(int arenaid, Player player) {
        Map<Integer, Player> podio = ArenaPodium.get(arenaid);

        if (podio != null) {
            for (Map.Entry<Integer, Player> entry : podio.entrySet()) {
                if (entry.getValue().equals(player)) {
                    return entry.getKey();
                }
            }
        }

        return 0;
    }

    public static void clearPodium(int arenaid) {
        ArenaPodium.remove(arenaid);
    }

    public static Player getPlayerAtPosition(int arenaId, int position) {
        Map<Player, Integer> arenaMap = ArenaStars.getOrDefault(arenaId, new HashMap<>());
        List<Player> players = new ArrayList<>(arenaMap.keySet());
        players.sort((p1, p2) -> arenaMap.get(p2) - arenaMap.get(p1));
        if (position > 0 && position <= players.size()) {
            return players.get(position - 1);
        }
        return null;
    }

    public static String getPlayerNameAtPosition(int arenaId, int position) {
        Map<Player, Integer> arenaMap = ArenaStars.getOrDefault(arenaId, new HashMap<>());

        if (arenaMap.isEmpty()) {
            return "-";
        }

        if (position <= 0 || position > arenaMap.size()) {
            return "-";
        }

        Optional<Player> player = arenaMap.entrySet().stream()
                .sorted(Map.Entry.<Player, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .skip(position - 1)
                .findFirst();

        return player.map(Player::getName).orElse("-");
    }

    public static String getPodiumName(Integer arenaid, int position) {
        if (!ArenaPodium.containsKey(arenaid) || ArenaPodium.get(arenaid).get(position) == null) {
            return "-";
        } else {
            return ArenaPodium.get(arenaid).get(position).getName();
        }
    }

    public static int getStarsForPlayer(int arenaid, Player player) {
        Map<Player, Integer> arenaMap = ArenaStars.getOrDefault(arenaid, new HashMap<>());
        return arenaMap.getOrDefault(player, 0);
    }

    public static boolean areAllPlayersSpectators(int arenaid) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if(PlayerManager.PlayerArena.containsKey(player) && PlayerManager.PlayerInGameStatus.containsKey(player)) {
                if (PlayerManager.PlayerArena.get(player).equals(arenaid)) {
                    if (!PlayerManager.PlayerInGameStatus.get(player).equalsIgnoreCase("SPECTATOR")) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static void giveRewards(Main main, Integer arenaid, Boolean round) {
        if(round) {
            if(main.configManager.getRewards().getBoolean("rewards.round.enabled")) {
                Player p_first = Bukkit.getPlayer(getPodiumName(arenaid, 1));
                if(p_first != null) {
                    List<String> c_first = main.configManager.getRewards().getStringList("rewards.round.first");
                    for(String command : c_first) {
                        command = command.replace("{player}", p_first.getName());
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                    }
                }

                Player p_second = Bukkit.getPlayer(getPodiumName(arenaid, 2));
                if(p_second != null) {
                    List<String> c_second = main.configManager.getRewards().getStringList("rewards.round.second");
                    for(String command : c_second) {
                        command = command.replace("{player}", p_second.getName());
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                    }
                }

                Player p_third = Bukkit.getPlayer(getPodiumName(arenaid, 3));
                if(p_third != null) {
                    List<String> c_third = main.configManager.getRewards().getStringList("rewards.round.third");
                    for(String command : c_third) {
                        command = command.replace("{player}", p_third.getName());
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                    }
                }

                for(Player player : Bukkit.getOnlinePlayers()) {
                    if(PlayerManager.PlayerStatus.containsKey(player)) {
                        if(PlayerManager.PlayerStatus.get(player).equalsIgnoreCase("Playing")) {
                            if(player != p_first || player != p_second || player != p_third){
                                List<String> c_participation = main.configManager.getRewards().getStringList("rewards.round.participation");
                                for(String command : c_participation) {
                                    command = command.replace("{player}", player.getName());
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                                }
                            }
                        }
                    }
                }
            }
        } else {
            if(main.configManager.getRewards().getBoolean("rewards.final.enabled")) {
                Player p_first = getPlayerAtPosition(arenaid, 1);
                if(p_first != null) {
                    List<String> c_first = main.configManager.getRewards().getStringList("rewards.final.first");
                    for(String command : c_first) {
                        command = command.replace("{player}", p_first.getName());
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                    }
                }

                Player p_second = getPlayerAtPosition(arenaid, 2);
                if(p_second != null) {
                    List<String> c_second = main.configManager.getRewards().getStringList("rewards.final.second");
                    for(String command : c_second) {
                        command = command.replace("{player}", p_second.getName());
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                    }
                }

                Player p_third = getPlayerAtPosition(arenaid, 3);
                if(p_third != null) {
                    List<String> c_third = main.configManager.getRewards().getStringList("rewards.final.third");
                    for(String command : c_third) {
                        command = command.replace("{player}", p_third.getName());
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                    }
                }

                for(Player player : Bukkit.getOnlinePlayers()) {
                    if(PlayerManager.PlayerStatus.containsKey(player)) {
                        if(PlayerManager.PlayerStatus.get(player).equalsIgnoreCase("Playing")) {
                            if(player != p_first || player != p_second || player != p_third){
                                List<String> c_participation = main.configManager.getRewards().getStringList("rewards.final.participation");
                                for(String command : c_participation) {
                                    command = command.replace("{player}", player.getName());
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void sendSummaryGame(int arenaid, Main main) {
        if(CacheManager.Arenas.ARENA_BASIC_INTEGER.get(arenaid).get("rounds") == 1) {
            sendSummaryFinal(arenaid, main);
            return;
        }

        startLimbo(arenaid, main);

        List<String> summarygame = CacheManager.Language.SUMMARY_MINI_GAME;

        giveStars(arenaid, main);

        Bukkit.getScheduler().runTask(main, () -> giveRewards(main, arenaid, true));

        for(Player player : Bukkit.getOnlinePlayers()) {
            if(PlayerManager.PlayerStatus.containsKey(player) && PlayerManager.PlayerArena.containsKey(player)) {
                if(PlayerManager.PlayerStatus.get(player).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(player).equals(arenaid)) {

                    SoundsManager.playSounds(player, CacheManager.Sounds.SOUNDS_IN_GAME_GAME_OVER);

                    int mini_games_played = main.configManager.getUser(player.getUniqueId()).getInt("stats.mini_games_played")+1;
                    main.configManager.getUser(player.getUniqueId()).set("stats.mini_games_played", mini_games_played);
                    main.configManager.saveUser(player.getUniqueId());
                    main.configManager.reloadUser(player.getUniqueId());

                    PlayerManager.PlayerInGameStatus.replace(player, "PLAYING");
                    Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
                        for(String message : summarygame) {
                            StringUtils.sendMessage(player, player.getName(), message
                                    .replace("{place_1}", getPodiumName(arenaid, 1))
                                    .replace("{place_2}", getPodiumName(arenaid, 2))
                                    .replace("{place_3}", getPodiumName(arenaid, 3))
                                    .replace("{credits_earned}", "0")
                                    .replace("{stars_earned}", String.valueOf(ArenaStars.get(arenaid).get(player)))
                                    .replace("{place_player}", String.valueOf(getPlayerPositionOnPodium(arenaid, player))));
                        }
                    });
                }
            }
        }
    }

    public static void sendSummaryFinal(int arenaid, Main main) {
        savePodiumStats(arenaid, main);
        endArena(arenaid, main);

        ArenaActualGame.replace(arenaid, "Finish");

        giveRewards(main, arenaid, false);

        List<String> summaryfinal = CacheManager.Language.SUMMARY_FINAL;
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(PlayerManager.PlayerStatus.containsKey(player) && PlayerManager.PlayerArena.containsKey(player)) {
                if(PlayerManager.PlayerStatus.get(player).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(player).equals(arenaid)) {
                    int games_played = main.configManager.getUser(player.getUniqueId()).getInt("stats.games_played")+1;
                    main.configManager.getUser(player.getUniqueId()).set("stats.games_played", games_played);
                    main.configManager.saveUser(player.getUniqueId());
                    main.configManager.reloadUser(player.getUniqueId());

                    Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
                        for(String message : summaryfinal) {
                            StringUtils.sendMessage(player, player.getName(), message
                                    .replace("{player}", player.getName())
                                    .replace("{place_1}", getPlayerNameAtPosition(arenaid, 1))
                                    .replace("{place_2}", getPlayerNameAtPosition(arenaid, 2))
                                    .replace("{place_3}", getPlayerNameAtPosition(arenaid, 3))
                                    .replace("{stars_1}", String.valueOf(getStarsForPlayer(arenaid, getPlayerAtPosition(arenaid, 1))))
                                    .replace("{stars_2}", String.valueOf(getStarsForPlayer(arenaid, getPlayerAtPosition(arenaid, 2))))
                                    .replace("{stars_3}", String.valueOf(getStarsForPlayer(arenaid, getPlayerAtPosition(arenaid, 3))))
                                    .replace("{place_player}", StringUtils.convertNumberToEmoji(ArenaManager.getPlayerPositionOnPodium(arenaid, player)))
                                    .replace("{stars_player}", String.valueOf(ArenaManager.getStarsForPlayer(arenaid, player))));
                        }
                    });
                }
            }
        }
    }

    private static void savePodiumStats(Integer arenaid, Main main) {
        String firstPlace = getPlayerNameAtPosition(arenaid, 1);
        String secondPlace = getPlayerNameAtPosition(arenaid, 2);
        String thirdPlace = getPlayerNameAtPosition(arenaid, 3);

        int playerFirst = main.configManager.getUser(Objects.requireNonNull(Bukkit.getPlayer(firstPlace)).getUniqueId()).getInt("stats.first_place")+1;
        main.configManager.getUser(Objects.requireNonNull(Bukkit.getPlayer(firstPlace)).getUniqueId()).set("stats.first_place", playerFirst);
        main.configManager.saveUser(Objects.requireNonNull(Bukkit.getPlayer(firstPlace)).getUniqueId());
        main.configManager.reloadUser(Objects.requireNonNull(Bukkit.getPlayer(firstPlace)).getUniqueId());

        if(secondPlace.equals("-")) {
            return;
        }
        int playerSecond = main.configManager.getUser(Objects.requireNonNull(Bukkit.getPlayer(secondPlace)).getUniqueId()).getInt("stats.second_place")+1;
        main.configManager.getUser(Objects.requireNonNull(Bukkit.getPlayer(secondPlace)).getUniqueId()).set("stats.second_place", playerSecond);
        main.configManager.saveUser(Objects.requireNonNull(Bukkit.getPlayer(secondPlace)).getUniqueId());
        main.configManager.reloadUser(Objects.requireNonNull(Bukkit.getPlayer(secondPlace)).getUniqueId());

        if(thirdPlace.equals("-")) {
            return;
        }
        int playerThird = main.configManager.getUser(Objects.requireNonNull(Bukkit.getPlayer(thirdPlace)).getUniqueId()).getInt("stats.third_place")+1;
        main.configManager.getUser(Objects.requireNonNull(Bukkit.getPlayer(thirdPlace)).getUniqueId()).set("stats.third_place", playerThird);
        main.configManager.saveUser(Objects.requireNonNull(Bukkit.getPlayer(thirdPlace)).getUniqueId());
        main.configManager.reloadUser(Objects.requireNonNull(Bukkit.getPlayer(thirdPlace)).getUniqueId());

    }

    private static void giveStars(int arenaid, Main main) {

        if(ArenaManager.ArenaPodium.containsKey(arenaid)) {
            if(ArenaManager.ArenaPodium.get(arenaid).containsKey(1)) {
                Player p1 = ArenaManager.ArenaPodium.get(arenaid).get(1);
                Integer s1 = ArenaManager.ArenaStars.get(arenaid).get(p1);
                ArenaStars.get(arenaid).replace(p1, s1+3);

                main.configManager.getUser(p1.getUniqueId()).set("stats.stars", s1+3);
                main.configManager.saveUser(p1.getUniqueId());
                main.configManager.reloadUser(p1.getUniqueId());
            }

            if (ArenaManager.ArenaPodium.get(arenaid).containsKey(2)) {
                Player p2 = ArenaManager.ArenaPodium.get(arenaid).get(2);
                Integer s2 = ArenaManager.ArenaStars.get(arenaid).get(p2);
                ArenaStars.get(arenaid).replace(p2, s2+2);

                main.configManager.getUser(p2.getUniqueId()).set("stats.stars", s2+2);
                main.configManager.saveUser(p2.getUniqueId());
                main.configManager.reloadUser(p2.getUniqueId());
            }

            if(ArenaManager.ArenaPodium.get(arenaid).containsKey(3)) {
                Player p3 = ArenaManager.ArenaPodium.get(arenaid).get(3);
                Integer s3 = ArenaManager.ArenaStars.get(arenaid).get(p3);
                ArenaStars.get(arenaid).replace(p3, s3+1);

                main.configManager.getUser(p3.getUniqueId()).set("stats.stars", s3+1);
                main.configManager.saveUser(p3.getUniqueId());
                main.configManager.reloadUser(p3.getUniqueId());
            }
        }
    }

    private static void endArena(int arenaid, Main main) {
        EndTasks.remove(arenaid);
        EndTasks.put(arenaid, Bukkit.getScheduler().runTaskTimerAsynchronously(main, new Runnable() {
            int time = 15;
            @Override
            public void run() {

                if(this.time <= 0){
                    BukkitTask task = EndTasks.get(arenaid);
                    if(task != null) {
                        task.cancel();
                        EndTasks.remove(arenaid);
                    }
                    try {
                        stopArena(main, arenaid);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                if(CacheManager.Settings.GAME_GLOBAL_SPAWN_FIREWORKS_WINNERS) {
                    Player firstPlace = Bukkit.getPlayer(getPlayerNameAtPosition(arenaid, 1));
                    if(PlayerManager.PlayerStatus.containsKey(firstPlace) && PlayerManager.PlayerArena.containsKey(firstPlace)) {
                        if(PlayerManager.PlayerStatus.get(firstPlace).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(firstPlace).equals(arenaid)) {
                            SyncUtil.spawnFireworks(main, firstPlace, random);
                        }
                    }

                    Player secondPlace = Bukkit.getPlayer(getPlayerNameAtPosition(arenaid, 2));
                    if(PlayerManager.PlayerStatus.containsKey(secondPlace) && PlayerManager.PlayerArena.containsKey(secondPlace)) {
                        if(PlayerManager.PlayerStatus.get(secondPlace).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(secondPlace).equals(arenaid)) {
                            SyncUtil.spawnFireworks(main, secondPlace, random);
                        }
                    }

                    Player thirdPlace = Bukkit.getPlayer(getPlayerNameAtPosition(arenaid, 3));
                    if(PlayerManager.PlayerStatus.containsKey(thirdPlace) && PlayerManager.PlayerArena.containsKey(thirdPlace)) {
                        if(PlayerManager.PlayerStatus.get(thirdPlace).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(thirdPlace).equals(arenaid)) {
                            SyncUtil.spawnFireworks(main, thirdPlace, random);
                        }
                    }
                }

                ArenaCountdown.replace(arenaid, time);

                this.time--;
            }
        }, 0L, 20L));
    }
}
