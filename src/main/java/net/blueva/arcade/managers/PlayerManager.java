package net.blueva.arcade.managers;

import net.blueva.arcade.utils.InventoryUtil;
import net.blueva.arcade.utils.SyncUtil;
import net.blueva.arcade.utils.TitlesUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import net.blueva.arcade.Main;
import net.blueva.arcade.utils.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PlayerManager {
    public static ArrayList<Player> PlayerList = new ArrayList<>();
    public static Map<Player, String> PlayerStatus = new HashMap<>(); // NotPlaying, Playing
    public static Map<Player, Integer> PlayerArena = new HashMap<>();
    public static Map<Player, Integer> PlayerMuted = new HashMap<>();
    public static Map<Player, Boolean> PlayerChat = new HashMap<>();
    public static Map<Player, String> PlayerInGameStatus = new HashMap<>(); // PLAYING, SPECTATOR


    public static void JoinPlayer(@NotNull Main main, Integer arenaid, Player player) {
        if(PlayerStatus.get(player).equalsIgnoreCase("NotPlaying")) {
            if(ArenaManager.ArenaStatus.get(arenaid).equalsIgnoreCase("Enabled")) {
                if(ArenaManager.ArenaStatusInternal.get(arenaid).equalsIgnoreCase("Waiting")) {
                    if(ArenaManager.ArenaPlayersCount.get(arenaid) <= CacheManager.Arenas.ARENA_BASIC_INTEGER.get(arenaid).get("max_players")) {
                        ArenaManager.ArenaPlayers.put(arenaid, player);
                        PlayerArena.put(player, arenaid);
                        ArenaManager.ArenaStars.computeIfAbsent(arenaid, l -> new HashMap<>()).put(player, 0);
                        PlayerStatus.replace(player.getPlayer(), "Playing");
                        PlayerInGameStatus.put(player, "Playing");
                        ArenaManager.ArenaPlayersCount.replace(arenaid, ArenaManager.ArenaPlayersCount.get(arenaid)+1);

                        StringUtils.sendMessage(player, player.getName(), CacheManager.Language.GLOBAL_INFO_YOU_JOIN
                                .replace("{arena_display_name}", CacheManager.Arenas.ARENA_BASIC_STRING.get(arenaid).get("display_name")));

                        TitlesUtil.sendTitle(player, CacheManager.Language.TITLES_JOIN_TITLE, CacheManager.Language.TITLES_JOIN_SUBTITLE, 20, 80, 20);

                        // Waiting lobby
                        World world = Bukkit.getWorld(Objects.requireNonNull(main.configManager.getArena(arenaid).getString("arena.waiting_lobby.world")));
                        double x = main.configManager.getArena(arenaid).getDouble("arena.waiting_lobby.x");
                        double y = main.configManager.getArena(arenaid).getDouble("arena.waiting_lobby.y");
                        double z = main.configManager.getArena(arenaid).getDouble("arena.waiting_lobby.z");
                        double yaw = main.configManager.getArena(arenaid).getDouble("arena.waiting_lobby.yaw");
                        double pitch = main.configManager.getArena(arenaid).getDouble("arena.waiting_lobby.pitch");

                        Location lobby = new Location(world, x, y, z, (float) yaw, (float) pitch);
                        player.teleport(lobby);
                        player.setGameMode(GameMode.ADVENTURE);
                        player.setFoodLevel(20);
                        player.setHealth(20);
                        SyncUtil.setFlying(main, false, player);

                        InventoryUtil.SaveInventory(main, player);
                        player.getInventory().clear();

                        player.getInventory().setHeldItemSlot(4);

                        ItemStack leave = new ItemStack(Material.RED_BED, 1);
                        ItemMeta leavemeta = leave.getItemMeta();
                        leavemeta.setDisplayName(StringUtils.formatMessage(player.getName(), CacheManager.Language.ITEMS_GAME_LEAVE_GAME));
                        leave.setItemMeta(leavemeta);
                        player.getInventory().setItem(8, leave);

                        for(Player p2 : Bukkit.getOnlinePlayers()) {
                            if(PlayerStatus.get(p2).equalsIgnoreCase("Playing")) {
                                if(PlayerArena.get(p2).equals(arenaid)) {
                                    StringUtils.sendMessage(p2, player.getName(), CacheManager.Language.GLOBAL_INFO_PLAYER_JOINED
                                            .replace("{player}", player.getName())
                                            .replace("{players}", ArenaManager.ArenaPlayersCount.get(arenaid).toString())
                                            .replace("{max_players}", CacheManager.Arenas.ARENA_BASIC_INTEGER.get(arenaid).get("max_players").toString()));
                                }
                            }
                        }

                        if(CacheManager.Arenas.ARENA_BASIC_INTEGER.get(arenaid).get("min_players") <= ArenaManager.ArenaPlayersCount.get(arenaid)) {
                            ArenaManager.startArena(main, arenaid, player);
                        }
                    } else {
                        StringUtils.sendMessage(player, player.getName(), CacheManager.Language.GLOBAL_ERROR_ARENA_FULL
                                .replace("{arena_display_name}", CacheManager.Arenas.ARENA_BASIC_STRING.get(arenaid).get("display_name")));

                    }
                } else {
                    StringUtils.sendMessage(player, player.getName(), CacheManager.Language.GLOBAL_ERROR_ARENA_STARTED
                            .replace("{arena_display_name}", CacheManager.Arenas.ARENA_BASIC_STRING.get(arenaid).get("display_name")));

                }
            } else {
                StringUtils.sendMessage(player, player.getName(), CacheManager.Language.GLOBAL_ERROR_ARENA_DISABLED
                        .replace("{arena_id}", String.valueOf(CacheManager.Arenas.ARENA_BASIC_INTEGER.get(arenaid).get("id"))));
            }
        } else {
            StringUtils.sendMessage(player, player.getName(), CacheManager.Language.GLOBAL_ERROR_ALREADY_IN_GAME
                    .replace("{arena_display_name}", CacheManager.Arenas.ARENA_BASIC_STRING.get(arenaid).get("display_name")));
        }
    }
    public static void LeavePlayer(@NotNull Main main, Integer arenaid, Player player, boolean notifyPlayers) throws IOException {
        if(PlayerStatus.get(player).equalsIgnoreCase("Playing")) {
            StringUtils.sendMessage(player, player.getName(), CacheManager.Language.GLOBAL_INFO_YOU_LEFT
                    .replace("{arena_display_name}", CacheManager.Arenas.ARENA_BASIC_STRING.get(arenaid).get("display_name")));

            PlayerStatus.replace(player.getPlayer(), "NotPlaying");

            ArenaManager.ArenaPlayersCount.replace(arenaid, ArenaManager.ArenaPlayersCount.get(arenaid)-1);
            ArenaManager.ArenaStars.remove(arenaid, player);
            ArenaManager.ArenaPlayers.remove(arenaid, player);
            PlayerArena.remove(player, arenaid);
            PlayerInGameStatus.remove(player);

            // Main lobby
            Bukkit.getScheduler().runTask(Main.getPlugin(), () -> {
                String worldName = main.configManager.getGlobal().getString("cords.spawn.world");
                if(worldName != null) {
                    World world = Bukkit.getWorld(worldName);
                    double x = main.configManager.getGlobal().getDouble("cords.spawn.x");
                    double y = main.configManager.getGlobal().getDouble("cords.spawn.y");
                    double z = main.configManager.getGlobal().getDouble("cords.spawn.z");
                    double yaw = main.configManager.getGlobal().getDouble("cords.spawn.yaw");
                    double pitch = main.configManager.getGlobal().getDouble("cords.spawn.pitch");

                    player.teleport(new Location(world, x, y, z, (float) yaw, (float) pitch));
                }
            });
            SyncUtil.setFlying(main, false, player);
            SyncUtil.setGameMode(main, GameMode.SURVIVAL, player);
            player.getInventory().setContents(InventoryUtil.getInventoryContent(main.configManager.getUser(player.getUniqueId()).getString("inventory")));

            if(notifyPlayers) {
                for(Player p2 : Bukkit.getOnlinePlayers()) {
                    if(PlayerStatus.get(p2).equalsIgnoreCase("Playing")) {
                        if(PlayerArena.get(p2).equals(arenaid)) {
                            if(!player.getName().equals(p2.getName())) {
                                StringUtils.sendMessage(p2, player.getName(), CacheManager.Language.GLOBAL_INFO_PLAYER_LEAVES
                                        .replace("{player}", player.getName())
                                        .replace("{players}", ArenaManager.ArenaPlayersCount.get(arenaid).toString())
                                        .replace("{max_players}", CacheManager.Arenas.ARENA_BASIC_INTEGER.get(arenaid).get("max_players").toString()));
                            }
                        }
                    }
                }
            }
        } else {
            StringUtils.sendMessage(player, player.getName(), CacheManager.Language.GLOBAL_ERROR_NOT_IN_GAME);
        }
    }
}
