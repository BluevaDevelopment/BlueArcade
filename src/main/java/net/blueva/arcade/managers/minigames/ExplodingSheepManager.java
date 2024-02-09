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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.util.*;

public class ExplodingSheepManager {
    private static final HashMap<Integer, BukkitTask> StartTasks = new HashMap<>();
    private static final HashMap<Integer, BukkitTask> GameTasks = new HashMap<>();
    public static final HashMap<Player, Integer> PlayerShearedSheep = new HashMap<>();

    public static void Start(Integer arenaid, Main main) {
        ArenaManager.teleportPlayers(arenaid, "exploding_sheep", main);
        ArenaManager.sendDescription(arenaid, "exploding_sheep", main);
        giveItems(arenaid);
        startCountdown(arenaid, main);
        Bukkit.getScheduler().runTask(Main.getPlugin(), () -> regenerateGame(arenaid, main));
        registerPlayers(arenaid);
    }

    public static void finishPlayer(Integer arenaid, Player player, boolean reverse) {
        if(PlayerManager.PlayerInGameStatus.containsKey(player)) {
            if(PlayerManager.PlayerInGameStatus.get(player).equalsIgnoreCase("Playing")) {

                if(reverse) {
                    ArenaManager.addPlayerToPodiumInReverseOrder(arenaid, player, ArenaManager.ArenaPlayerCountCache.get(arenaid));
                    SoundsManager.playSounds(Main.getPlugin(), player, CacheManager.Sounds.SOUNDS_IN_GAME_DEAD);
                } else {
                    ArenaManager.addPlayerToPodium(arenaid, player, 1);
                }

                PlayerManager.PlayerInGameStatus.replace(player, "SPECTATOR");
                player.setGameMode(GameMode.SPECTATOR);
                player.getInventory().clear();
            }
        }
    }

    public static void finishPlayerSync(int arenaid, Player winner, boolean reverse) {
        Bukkit.getScheduler().runTask(Main.getPlugin(), () -> finishPlayer(arenaid, winner, reverse));
    }

    public static void executeFinishPlayerInOrder(Integer arenaid) {

        List<Player> players = new ArrayList<>(PlayerShearedSheep.keySet());
        players.sort(Comparator.comparingInt(PlayerShearedSheep::get).reversed());

        for (Player player : players) {
            if(PlayerManager.PlayerStatus.containsKey(player)) {
                if (PlayerManager.PlayerStatus.get(player).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(player).equals(arenaid)) {
                    finishPlayerSync(arenaid, player, false);
                }
            }
        }
    }

    private static void startCountdown(Integer arenaid, Main main) {
        ArenaManager.ArenaActualGame.replace(arenaid, "StartingExplodingSheep");

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
                                            .replace("{game_display_name}", CacheManager.Language.MINI_GAMES_EXPLODING_SHEEP_DISPLAY_NAME)
                                            .replace("{time}", String.valueOf(time)),
                                    CacheManager.Language.TITLES_STARTING_GAME_SUBTITLE
                                            .replace("{game_display_name}", CacheManager.Language.MINI_GAMES_EXPLODING_SHEEP_DISPLAY_NAME)
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
        ArenaManager.ArenaActualGame.replace(arenaid, "ExplodingSheep");

        for(final Player players : Bukkit.getOnlinePlayers()) {
            if(PlayerManager.PlayerStatus.containsKey(players) && PlayerManager.PlayerMuted.containsKey(players)) {
                if (PlayerManager.PlayerStatus.get(players).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(players).equals(arenaid)) {
                    PlayerManager.PlayerMuted.replace(players.getPlayer(), 0);
                    SyncUtil.setFlying(main, false, players);
                    SoundsManager.playSounds(main, players, CacheManager.Sounds.SOUNDS_STARTING_GAME_START);
                    TitlesUtil.sendTitle(players,
                            CacheManager.Language.TITLES_GAME_STARTED_TITLE
                                    .replace("{game_display_name}", CacheManager.Language.MINI_GAMES_EXPLODING_SHEEP_DISPLAY_NAME),
                            CacheManager.Language.TITLES_GAME_STARTED_SUBTITLE
                                    .replace("{game_display_name}", CacheManager.Language.MINI_GAMES_EXPLODING_SHEEP_DISPLAY_NAME), 0, 0, 20);

                }
            }
        }

        GameTasks.remove(arenaid);
        GameTasks.put(arenaid, Bukkit.getScheduler().runTaskTimerAsynchronously(main, new Runnable() {

            int time = main.configManager.getArena(arenaid).getInt("arena.mini_games.exploding_sheep.basic.time", 60);
            @Override
            public void run() {
                World world = Bukkit.getWorld(main.configManager.getArena(arenaid).getString("arena.mini_games.exploding_sheep.basic.world"));
                double boundsminx = main.configManager.getArena(arenaid).getDouble("arena.mini_games.exploding_sheep.bounds.min.x");
                double boundsminy = main.configManager.getArena(arenaid).getDouble("arena.mini_games.exploding_sheep.bounds.min.y");
                double boundsminz = main.configManager.getArena(arenaid).getDouble("arena.mini_games.exploding_sheep.bounds.min.z");
                Location boundsmin = new Location(world, boundsminx, boundsminy, boundsminz);

                double boundsmaxx = main.configManager.getArena(arenaid).getDouble("arena.mini_games.exploding_sheep.bounds.max.x");
                double boundsmaxy = main.configManager.getArena(arenaid).getDouble("arena.mini_games.exploding_sheep.bounds.max.y");
                double boundsmaxz = main.configManager.getArena(arenaid).getDouble("arena.mini_games.exploding_sheep.bounds.max.z");
                Location boundsmax = new Location(world, boundsmaxx, boundsmaxy, boundsmaxz);

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
                    Bukkit.getScheduler().runTaskLater(main, () -> killSheepInRegion(boundsmin, boundsmax), 10L);
                    Bukkit.getScheduler().runTaskLater(main, () -> regenerateGame(arenaid, main), CacheManager.Settings.GAME_GLOBAL_LIMBO_COUNTDOWN+5*20);
                    removeData(arenaid);
                    return;
                }

                if(checkWinner(arenaid)) {
                    cancelGameTask(arenaid);
                    Bukkit.getScheduler().runTaskLaterAsynchronously(main, () -> {
                        ArenaManager.sendSummaryGame(arenaid, main);
                        killSheepInRegion(boundsmin, boundsmax);
                    }, 10L);
                    removeData(arenaid);
                    regenerateGame(arenaid, main);
                    return;

                }
                Bukkit.getScheduler().runTaskLater(main, () -> generateSheep(boundsmin, boundsmax, ArenaManager.ArenaPlayersCount.get(arenaid)), 10L);

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

    private static void giveItems(int arenaid) {
        ItemStack shears = new ItemStack(Material.SHEARS, 1);
        ItemMeta shearsmeta = shears.getItemMeta();
        shearsmeta.addEnchant(Enchantment.DIG_SPEED, 5, true);
        shearsmeta.addEnchant(Enchantment.DURABILITY, 3, true);
        shearsmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        shears.setItemMeta(shearsmeta);

        for(final Player allp : Bukkit.getOnlinePlayers()) {
            if(PlayerManager.PlayerStatus.containsKey(allp) && PlayerManager.PlayerArena.containsKey(allp)) {
                if(PlayerManager.PlayerStatus.get(allp).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(allp).equals(arenaid)) {
                    allp.getInventory().setItem(0, shears);
                }
            }
        }
    }

    private static void regenerateGame(int arenaid, Main main) {
        int delayTicks = CacheManager.Settings.GAME_GLOBAL_LIMBO_COUNTDOWN+5*20;
        new BukkitRunnable() {
            @Override
            public void run() {
                List<String> blocks = BlocksUtil.loadBlocks(main, "exploding_sheep", arenaid);
                Map<Location, Material> blockMap = BlocksUtil.getBlockMap(blocks);
                BlocksUtil.setRegionBlocks(blockMap);
            }
        }.runTaskLater(main, delayTicks);
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
            finishPlayerSync(arenaid, winner, false);
            SoundsManager.playSounds(Main.getPlugin(), winner, CacheManager.Sounds.SOUNDS_IN_GAME_CLASSIFIED);
            return true;
        }

        return false;
    }

    private static void generateSheep(Location boundsmin, Location boundsmax, int cantidadOvejas) {
        World world = boundsmin.getWorld();
        Random random = new Random();

        for (int i = 0; i < cantidadOvejas; i++) {
            double x = boundsmin.getX() + (boundsmax.getX() - boundsmin.getX()) * random.nextDouble();
            double y = boundsmin.getY() + (boundsmax.getY() - boundsmin.getY()) * random.nextDouble();
            double z = boundsmin.getZ() + (boundsmax.getZ() - boundsmin.getZ()) * random.nextDouble();

            y = world.getHighestBlockYAt(new Location(world, x, y, z)) + 3;

            Location sheepLocation = new Location(world, x, y, z);

            Location blockBelow = sheepLocation.clone().subtract(0, 1, 0);
            if (blockBelow.getBlock().getType() != Material.BARRIER) {
                Sheep sheep = (Sheep) world.spawnEntity(sheepLocation, EntityType.SHEEP);
                sheep.setColor(DyeColor.values()[random.nextInt(DyeColor.values().length)]);
                if (!Main.getPlugin().bukkitVersion.startsWith("1.8")) {
                    sheep.setInvulnerable(true);
                }
            }
        }
    }


    private static void killSheepInRegion(Location boundsmin, Location boundsmax) {
        Bukkit.getScheduler().runTask(Main.getPlugin(), () -> {
            for(Entity e : BlocksUtil.getNearbyEntities(boundsmin, boundsmax, 20)) {
                if(e.getType().equals(EntityType.SHEEP)) {
                    e.remove();
                }
            }
        });
    }

    private static void registerPlayers(int arenaid) {
        for(final Player players : Bukkit.getOnlinePlayers()) {
            if(PlayerManager.PlayerStatus.containsKey(players)) {
                if(PlayerManager.PlayerStatus.get(players).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(players).equals(arenaid)) {
                    PlayerShearedSheep.put(players, 0);
                }
            }
        }
    }

    private static void removeData(int arenaid) {
        for(final Player players : Bukkit.getOnlinePlayers()) {
            if(PlayerManager.PlayerStatus.containsKey(players) && PlayerManager.PlayerArena.containsKey(players)) {
                if(PlayerManager.PlayerStatus.get(players).equalsIgnoreCase("Playing") && PlayerManager.PlayerArena.get(players).equals(arenaid)) {
                    PlayerShearedSheep.remove(players);
                }
            }
        }
    }

    public static int getShearedSheep(Player player) {
        Integer sheep = PlayerShearedSheep.get(player);
        if(sheep == null) {
            return 0;
        }
        return sheep;
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
