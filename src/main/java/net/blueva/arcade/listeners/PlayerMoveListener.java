package net.blueva.arcade.listeners;

import net.blueva.arcade.Main;
import net.blueva.arcade.managers.ArenaManager;
import net.blueva.arcade.managers.CacheManager;
import net.blueva.arcade.managers.PlayerManager;
import net.blueva.arcade.managers.SoundsManager;
import net.blueva.arcade.managers.minigames.*;
import net.blueva.arcade.utils.CuboidUtil;
import net.blueva.arcade.utils.TitlesUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class PlayerMoveListener implements Listener {
    private final Main main;

    public PlayerMoveListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void PML(PlayerMoveEvent event) {
        if (event.getTo().getBlockX() != event.getFrom().getBlockX() ||
                event.getTo().getBlockY() != event.getFrom().getBlockY() ||
                event.getTo().getBlockZ() != event.getFrom().getBlockZ()) {
            if (PlayerManager.PlayerStatus.containsKey(event.getPlayer()) && PlayerManager.PlayerInGameStatus.containsKey(event.getPlayer())) {
                if (PlayerManager.PlayerStatus.get(event.getPlayer()).equalsIgnoreCase("Playing")) {
                    if (PlayerManager.PlayerInGameStatus.get(event.getPlayer()).equalsIgnoreCase("Playing")) {
                        Block blockUnderFeet = event.getPlayer().getLocation().subtract(0, 1, 0).getBlock();

                        String actualGame = ArenaManager.ArenaActualGame.get(PlayerManager.PlayerArena.get(event.getPlayer()));
                        int playerArenaID = PlayerManager.PlayerArena.get(event.getPlayer());

                        Location boundsmin;
                        Location boundsmax;
                        Location finishmin;
                        Location finishmax;

                        if (actualGame.equalsIgnoreCase("StartingRace") ||
                                actualGame.equalsIgnoreCase("StartingTrafficLight") ||
                                actualGame.equalsIgnoreCase("StartingMinefield") ||
                                actualGame.equalsIgnoreCase("StartingFastZone")) {
                            event.getPlayer().teleport(event.getFrom());
                            //SoundsManager.playSounds(Main.getPlugin(), event.getPlayer(), CacheManager.Sounds.SOUNDS_IN_GAME_RESPAWN);
                        } else if (actualGame.equalsIgnoreCase("Race")) {
                            boundsmin = CacheManager.Bounds.getBounds(main, playerArenaID, "race", "min");
                            boundsmax = CacheManager.Bounds.getBounds(main, playerArenaID, "race", "max");
                            CuboidUtil racebounds = new CuboidUtil(boundsmin, boundsmax);

                            if (!racebounds.isIn(event.getPlayer()) || blockUnderFeet.getType() == Material.valueOf(CacheManager.Arenas.ARENA_MINIGAME_STRING.get(playerArenaID).get("race").get("death_block"))) {
                                event.getPlayer().teleport(ArenaManager.getRandomSpawn(main, playerArenaID, "race"));
                                SoundsManager.playSounds(Main.getPlugin(), event.getPlayer(), CacheManager.Sounds.SOUNDS_IN_GAME_RESPAWN);
                            }

                            // finish race
                            finishmin = CacheManager.Bounds.getFinishBounds(main, playerArenaID, "race", "min");
                            finishmax = CacheManager.Bounds.getFinishBounds(main, playerArenaID, "race", "max");
                            CuboidUtil racefinish = new CuboidUtil(finishmin, finishmax);

                            if (racefinish.isIn(event.getPlayer())) {
                                RaceManager.finishPlayer(playerArenaID, event.getPlayer());
                            }
                        } else if (actualGame.equalsIgnoreCase("Spleef")) {
                            // minigame bounds
                            boundsmin = CacheManager.Bounds.getBounds(main, playerArenaID, "spleef", "min");
                            boundsmax = CacheManager.Bounds.getBounds(main, playerArenaID, "spleef", "max");
                            CuboidUtil spleefbounds = new CuboidUtil(boundsmin, boundsmax);

                            if (!spleefbounds.isIn(event.getPlayer()) || blockUnderFeet.getType() == Material.valueOf(main.configManager.getArena(playerArenaID).getString("arena.mini_games.spleef.basic.death_block", main.configManager.getSettings().getString("game.global.default_death_block")))) {
                                SpleefManager.finishPlayer(playerArenaID, event.getPlayer());
                                TitlesUtil.sendTitle(event.getPlayer(),
                                        CacheManager.Language.TITLES_YOU_DIED_TITLE
                                                .replace("{place}", String.valueOf(ArenaManager.getPlayerPositionOnPodium(playerArenaID, event.getPlayer()))),
                                        CacheManager.Language.TITLES_YOU_DIED_SUBTITLE
                                                .replace("{place}", String.valueOf(ArenaManager.getPlayerPositionOnPodium(playerArenaID, event.getPlayer())))
                                        , 0, 80, 20);
                            }
                        } else if (actualGame.equalsIgnoreCase("SnowballFight")) {
                            // minigame bounds
                            boundsmin = CacheManager.Bounds.getBounds(main, playerArenaID, "snowball_fight", "min");
                            boundsmax = CacheManager.Bounds.getBounds(main, playerArenaID, "snowball_fight", "max");
                            CuboidUtil snowballfightbounds = new CuboidUtil(boundsmin, boundsmax);

                            if (!snowballfightbounds.isIn(event.getPlayer()) || blockUnderFeet.getType() == Material.valueOf(main.configManager.getArena(playerArenaID).getString("arena.mini_games.snowball_fight.basic.death_block", main.configManager.getSettings().getString("game.global.default_death_block")))) {
                                event.getPlayer().teleport(ArenaManager.getRandomSpawn(main, playerArenaID, "snowball_fight"));
                                SoundsManager.playSounds(Main.getPlugin(), event.getPlayer(), CacheManager.Sounds.SOUNDS_IN_GAME_RESPAWN);
                            }
                        } else if (actualGame.equalsIgnoreCase("AllAgainstAll")) {
                            // minigame bounds
                            boundsmin = CacheManager.Bounds.getBounds(main, playerArenaID, "all_against_all", "min");
                            boundsmax = CacheManager.Bounds.getBounds(main, playerArenaID, "all_against_all", "max");
                            CuboidUtil aaabounds = new CuboidUtil(boundsmin, boundsmax);

                            if (!aaabounds.isIn(event.getPlayer()) || blockUnderFeet.getType() == Material.valueOf(main.configManager.getArena(playerArenaID).getString("arena.mini_games.all_against_all.basic.death_block", main.configManager.getSettings().getString("game.global.default_death_block")))) {
                                event.getPlayer().teleport(ArenaManager.getRandomSpawn(main, playerArenaID, "all_against_all"));
                                SoundsManager.playSounds(Main.getPlugin(), event.getPlayer(), CacheManager.Sounds.SOUNDS_IN_GAME_RESPAWN);
                            }
                        } else if (actualGame.equalsIgnoreCase("OneInTheChamber")) {
                            // minigame bounds
                            boundsmin = CacheManager.Bounds.getBounds(main, playerArenaID, "one_in_the_chamber", "min");
                            boundsmax = CacheManager.Bounds.getBounds(main, playerArenaID, "one_in_the_chamber", "max");
                            CuboidUtil oitcbounds = new CuboidUtil(boundsmin, boundsmax);

                            if (!oitcbounds.isIn(event.getPlayer()) || blockUnderFeet.getType() == Material.valueOf(main.configManager.getArena(playerArenaID).getString("arena.mini_games.one_in_the_chamber.basic.death_block", main.configManager.getSettings().getString("game.global.default_death_block")))) {
                                event.getPlayer().teleport(ArenaManager.getRandomSpawn(main, playerArenaID, "one_in_the_chamber"));
                                SoundsManager.playSounds(Main.getPlugin(), event.getPlayer(), CacheManager.Sounds.SOUNDS_IN_GAME_RESPAWN);
                            }
                        } else if (actualGame.equalsIgnoreCase("TrafficLight")) {
                            // minigame bounds
                            boundsmin = CacheManager.Bounds.getBounds(main, playerArenaID, "traffic_light", "min");
                            boundsmax = CacheManager.Bounds.getBounds(main, playerArenaID, "traffic_light", "max");
                            CuboidUtil tlbounds = new CuboidUtil(boundsmin, boundsmax);

                            if (!tlbounds.isIn(event.getPlayer()) || blockUnderFeet.getType() == Material.valueOf(main.configManager.getArena(playerArenaID).getString("arena.mini_games.traffic_light.basic.death_block", main.configManager.getSettings().getString("game.global.default_death_block")))) {
                                //event.getPlayer().teleport(racebounds.getRandomLocation());
                                event.getPlayer().teleport(ArenaManager.getRandomSpawn(main, playerArenaID, "traffic_light"));
                                SoundsManager.playSounds(Main.getPlugin(), event.getPlayer(), CacheManager.Sounds.SOUNDS_IN_GAME_RESPAWN);
                            }

                            if (TrafficLightManager.ArenaLightStatus.get(playerArenaID).equalsIgnoreCase("RED")) {
                                if (event.getTo().getBlockX() > event.getFrom().getBlockX() || event.getTo().getBlockX() < event.getFrom().getBlockX() || event.getTo().getBlockZ() > event.getFrom().getBlockZ() || event.getTo().getBlockZ() < event.getFrom().getBlockZ()) {
                                    event.getPlayer().teleport(ArenaManager.getRandomSpawn(main, playerArenaID, "traffic_light"));
                                    SoundsManager.playSounds(Main.getPlugin(), event.getPlayer(), CacheManager.Sounds.SOUNDS_IN_GAME_RESPAWN);
                                }
                            }

                            // finish race
                            finishmin = CacheManager.Bounds.getFinishBounds(main, playerArenaID, "traffic_light", "min");
                            finishmax = CacheManager.Bounds.getFinishBounds(main, playerArenaID, "traffic_light", "max");
                            CuboidUtil racefinish = new CuboidUtil(finishmin, finishmax);

                            if (racefinish.isIn(event.getPlayer())) {
                                TrafficLightManager.finishPlayer(playerArenaID, event.getPlayer());
                            }
                        } else if (actualGame.equalsIgnoreCase("Minefield")) {
                            // minigame bounds
                            boundsmin = CacheManager.Bounds.getBounds(main, playerArenaID, "minefield", "min");
                            boundsmax = CacheManager.Bounds.getBounds(main, playerArenaID, "minefield", "max");
                            CuboidUtil minefieldbounds = new CuboidUtil(boundsmin, boundsmax);

                            if (!minefieldbounds.isIn(event.getPlayer()) || blockUnderFeet.getType() == Material.valueOf(main.configManager.getArena(playerArenaID).getString("arena.mini_games.minefield.basic.death_block", main.configManager.getSettings().getString("game.global.default_death_block")))) {
                                //event.getPlayer().teleport(racebounds.getRandomLocation());
                                event.getPlayer().teleport(ArenaManager.getRandomSpawn(main, PlayerManager.PlayerArena.get(event.getPlayer()), "minefield"));
                                SoundsManager.playSounds(Main.getPlugin(), event.getPlayer(), CacheManager.Sounds.SOUNDS_IN_GAME_RESPAWN);
                            }

                            // finish race
                            finishmin = CacheManager.Bounds.getFinishBounds(main, playerArenaID, "minefield", "min");
                            finishmax = CacheManager.Bounds.getFinishBounds(main, playerArenaID, "minefield", "max");
                            CuboidUtil racefinish = new CuboidUtil(finishmin, finishmax);

                            if (racefinish.isIn(event.getPlayer())) {
                                MinefieldManager.finishPlayer(playerArenaID, event.getPlayer());
                            }
                        } else if (actualGame.equalsIgnoreCase("ExplodingSheep")) {
                            boundsmin = CacheManager.Bounds.getBounds(main, playerArenaID, "exploding_sheep", "min");
                            boundsmax = CacheManager.Bounds.getBounds(main, playerArenaID, "exploding_sheep", "max");
                            CuboidUtil explodingsheepbounds = new CuboidUtil(boundsmin, boundsmax);

                            if (!explodingsheepbounds.isIn(event.getPlayer()) || blockUnderFeet.getType() == Material.valueOf(main.configManager.getArena(playerArenaID).getString("arena.mini_games.exploding_sheep.basic.death_block", main.configManager.getSettings().getString("game.global.default_death_block")))) {
                                ExplodingSheepManager.finishPlayer(playerArenaID, event.getPlayer(), true);
                                TitlesUtil.sendTitle(event.getPlayer(),
                                        CacheManager.Language.TITLES_YOU_DIED_TITLE
                                                .replace("{place}", String.valueOf(ArenaManager.getPlayerPositionOnPodium(playerArenaID, event.getPlayer()))),
                                        CacheManager.Language.TITLES_YOU_DIED_SUBTITLE
                                                .replace("{place}", String.valueOf(ArenaManager.getPlayerPositionOnPodium(playerArenaID, event.getPlayer())))
                                        , 0, 80, 20);
                            }
                        } else if (actualGame.equalsIgnoreCase("TNTTag")) {
                            // minigame bounds
                            boundsmin = CacheManager.Bounds.getBounds(main, playerArenaID, "tnt_tag", "min");
                            boundsmax = CacheManager.Bounds.getBounds(main, playerArenaID, "tnt_tag", "max");
                            CuboidUtil aaabounds = new CuboidUtil(boundsmin, boundsmax);

                            if (!aaabounds.isIn(event.getPlayer()) || blockUnderFeet.getType() == Material.valueOf(main.configManager.getArena(playerArenaID).getString("arena.mini_games.tnt_tag.basic.death_block", main.configManager.getSettings().getString("game.global.default_death_block")))) {
                                event.getPlayer().teleport(ArenaManager.getRandomSpawn(main, playerArenaID, "tnt_tag"));
                                SoundsManager.playSounds(Main.getPlugin(), event.getPlayer(), CacheManager.Sounds.SOUNDS_IN_GAME_RESPAWN);
                            }
                        } else if (actualGame.equalsIgnoreCase("RedAlert")) {
                            // minigame bounds
                            boundsmin = CacheManager.Bounds.getBounds(main, playerArenaID, "red_alert", "min");
                            boundsmax = CacheManager.Bounds.getBounds(main, playerArenaID, "red_alert", "max");
                            CuboidUtil redalertbounds = new CuboidUtil(boundsmin, boundsmax);

                            if (!redalertbounds.isIn(event.getPlayer()) || blockUnderFeet.getType() == Material.valueOf(main.configManager.getArena(playerArenaID).getString("arena.mini_games.red_alert.basic.death_block", main.configManager.getSettings().getString("game.global.default_death_block")))) {
                                RedAlertManager.finishPlayer(playerArenaID, event.getPlayer());
                                TitlesUtil.sendTitle(event.getPlayer(),
                                        CacheManager.Language.TITLES_YOU_DIED_TITLE
                                                .replace("{place}", String.valueOf(ArenaManager.getPlayerPositionOnPodium(playerArenaID, event.getPlayer()))),
                                        CacheManager.Language.TITLES_YOU_DIED_SUBTITLE
                                                .replace("{place}", String.valueOf(ArenaManager.getPlayerPositionOnPodium(playerArenaID, event.getPlayer())))
                                        , 0, 80, 20);
                            }
                        } else if (actualGame.equalsIgnoreCase("KnockBack")) {
                            // minigame bounds
                            boundsmin = CacheManager.Bounds.getBounds(main, playerArenaID, "knock_back", "min");
                            boundsmax = CacheManager.Bounds.getBounds(main, playerArenaID, "knock_back", "max");
                            CuboidUtil knockbackbounds = new CuboidUtil(boundsmin, boundsmax);

                            if (!knockbackbounds.isIn(event.getPlayer()) || blockUnderFeet.getType() == Material.valueOf(main.configManager.getArena(playerArenaID).getString("arena.mini_games.knock_back.basic.death_block", main.configManager.getSettings().getString("game.global.default_death_block")))) {
                                KnockBackManager.finishPlayer(playerArenaID, event.getPlayer(), true);
                            }
                        } else if (actualGame.equalsIgnoreCase("FastZone")) {
                            boundsmin = CacheManager.Bounds.getBounds(main, playerArenaID, "fast_zone", "min");
                            boundsmax = CacheManager.Bounds.getBounds(main, playerArenaID, "fast_zone", "max");
                            CuboidUtil fastzonebounds = new CuboidUtil(boundsmin, boundsmax);

                            if (!fastzonebounds.isIn(event.getPlayer()) || blockUnderFeet.getType() == Material.valueOf(CacheManager.Arenas.ARENA_MINIGAME_STRING.get(playerArenaID).get("fast_zone").get("death_block"))) {
                                event.getPlayer().teleport(ArenaManager.getRandomSpawn(main, playerArenaID, "fast_zone"));
                                SoundsManager.playSounds(Main.getPlugin(), event.getPlayer(), CacheManager.Sounds.SOUNDS_IN_GAME_RESPAWN);
                            }

                            Player player = event.getPlayer();
                            Location playerLocation = player.getLocation();
                            Vector direction = playerLocation.getDirection();
                            Location eyeLocation = playerLocation.add(0, player.getEyeHeight(), 0);
                            Location blockLocation = eyeLocation.add(direction);
                            if (blockLocation.getBlock().getType() != Material.AIR) {
                                event.getPlayer().teleport(ArenaManager.getRandomSpawn(main, playerArenaID, "fast_zone"));
                                SoundsManager.playSounds(Main.getPlugin(), event.getPlayer(), CacheManager.Sounds.SOUNDS_IN_GAME_RESPAWN);
                            }

                            // finish race
                            finishmin = CacheManager.Bounds.getFinishBounds(main, playerArenaID, "fast_zone", "min");
                            finishmax = CacheManager.Bounds.getFinishBounds(main, playerArenaID, "fast_zone", "max");
                            CuboidUtil fastzonefinish = new CuboidUtil(finishmin, finishmax);

                            if (fastzonefinish.isIn(event.getPlayer())) {
                                FastZoneManager.finishPlayer(playerArenaID, event.getPlayer());
                            }
                        }
                    }
                }
            }
        }
    }
}