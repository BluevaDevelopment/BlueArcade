package net.blueva.arcade.listeners;

import net.blueva.arcade.Main;
import net.blueva.arcade.commands.main.subcommands.QuickjoinSubCommand;
import net.blueva.arcade.managers.ArenaManager;
import net.blueva.arcade.managers.CacheManager;
import net.blueva.arcade.managers.InventoryManager;
import net.blueva.arcade.managers.PlayerManager;
import net.blueva.arcade.utils.InventoryUtil;
import net.blueva.arcade.utils.StringUtils;
import net.blueva.arcade.utils.VersionUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class PlayerInteractListener implements Listener {
    private final Main main;

    public PlayerInteractListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void PIL(@NotNull PlayerInteractEvent event) throws IOException {
        Player p = event.getPlayer();
        Location playerLocation = p.getLocation();

        if(p.hasPermission("bluearcade.join")) {
            if(event.getClickedBlock() != null) {
                if (event.getClickedBlock().getType() == Material.OAK_SIGN ||
                        event.getClickedBlock().getType() == Material.OAK_WALL_SIGN) {
                    if(main.signManager.isRegisteredSign(event.getClickedBlock().getLocation())) {
                        if(p.getGameMode() == GameMode.CREATIVE) {
                            if(p.hasPermission("bluearcade.admin")) {
                                if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
                                    return;
                                }
                            }
                        }
                        if(main.signManager.getSignType(event.getClickedBlock().getLocation()).equalsIgnoreCase("join")) {
                            Integer arenaid = main.signManager.arenaIDFromSign(event.getClickedBlock().getLocation());
                            PlayerManager.JoinPlayer(main, arenaid, p);
                        } else if (main.signManager.getSignType(event.getClickedBlock().getLocation()).equalsIgnoreCase("quickjoin")) {
                            int arenaid = QuickjoinSubCommand.quickjoin();
                            PlayerManager.JoinPlayer(main, arenaid, p);
                        }
                    }
                }
            }
        }

        if(PlayerManager.PlayerStatus.containsKey(event.getPlayer())) {
            if(PlayerManager.PlayerStatus.get(event.getPlayer()).equalsIgnoreCase("Playing")) {
                String playerActualGame = ArenaManager.ArenaActualGame.get(PlayerManager.PlayerArena.get(event.getPlayer()));

                if(playerActualGame.equalsIgnoreCase("Lobby")) {
                    if (p.getInventory().getHeldItemSlot() == 8) {
                        PlayerManager.LeavePlayer(main, PlayerManager.PlayerArena.get(event.getPlayer()), p, true);
                    }
                }

                if(playerActualGame.equalsIgnoreCase("Minefield")) {
                    if(event.getAction() == Action.PHYSICAL) {
                        if(event.getClickedBlock().getType() == Material.STONE_PRESSURE_PLATE) {
                            playerLocation.getWorld().createExplosion(playerLocation.getX(), playerLocation.getY(), playerLocation.getZ(), 4.0f, false, false);
                            event.getPlayer().teleport(ArenaManager.getRandomSpawn(main, PlayerManager.PlayerArena.get(event.getPlayer()), "minefield"));
                            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, () -> event.getClickedBlock().setType(Material.AIR), 5);
                        }
                    }
                }

                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    event.setCancelled(true);
                }
            }
        }


        String world = "";
        double x = 0;
        double y = 0;
        double z = 0;

        if(main.SetupProcess.containsKey(p) && main.SetupProcess.get(p).equals(true) && main.SetupArena.containsKey(p)) {
            Integer arenaid = main.SetupArena.get(p);

            if(p.getInventory().getHeldItemSlot() == 5) {
                main.setupManager.cancel(p);
            } else if (p.getInventory().getHeldItemSlot() == 6) {
                main.setupManager.pause(p);
            } else if (p.getInventory().getHeldItemSlot() == 7) {
                main.setupManager.previous(p);
            } else if (p.getInventory().getHeldItemSlot() == 8) {
                main.setupManager.next(p);
            }

            if(main.configManager.getArena(main.SetupArena.get(p)).getInt("arena.basic.setup_step") == 2) {
                if(p.getInventory().getHeldItemSlot() == 0) {
                    if(event.getAction().name().contains("RIGHT_CLICK")) {
                        if(VersionUtil.getQuantityItemsMainHand(p) < 64) {
                            InventoryUtil.addItems(p.getInventory(), Material.SLIME_BALL, 1);
                            main.configManager.getArena(arenaid).set("arena.basic.min_players", VersionUtil.getQuantityItemsMainHand(p));
                            main.configManager.saveArena(arenaid);
                            main.configManager.reloadArena(arenaid);
                        }
                    } else if(event.getAction().name().contains("LEFT_CLICK")) {
                        if(VersionUtil.getQuantityItemsMainHand(p) > 1) {
                            InventoryUtil.removeItems(p.getInventory(), Material.SLIME_BALL, 1);
                            main.configManager.getArena(arenaid).set("arena.basic.min_players", VersionUtil.getQuantityItemsMainHand(p));
                            main.configManager.saveArena(arenaid);
                            main.configManager.reloadArena(arenaid);
                        }
                    }
                } else if (p.getInventory().getHeldItemSlot() == 1) {
                    if(event.getAction().name().contains("RIGHT_CLICK")) {
                        if(VersionUtil.getQuantityItemsMainHand(p) < 64) {
                            InventoryUtil.addItems(p.getInventory(), Material.REDSTONE, 1);
                            main.configManager.getArena(arenaid).set("arena.basic.max_players", VersionUtil.getQuantityItemsMainHand(p));
                            main.configManager.saveArena(arenaid);
                            main.configManager.reloadArena(arenaid);
                        }
                    } else if(event.getAction().name().contains("LEFT_CLICK")) {
                        if(VersionUtil.getQuantityItemsMainHand(p) > 1) {
                            InventoryUtil.removeItems(p.getInventory(), Material.REDSTONE, 1);
                            main.configManager.getArena(arenaid).set("arena.basic.max_players", VersionUtil.getQuantityItemsMainHand(p));
                            main.configManager.saveArena(arenaid);
                            main.configManager.reloadArena(arenaid);
                        }
                    }
                }
            } else if(main.configManager.getArena(main.SetupArena.get(p)).getInt("arena.basic.setup_step") == 3) {
                if(p.getInventory().getHeldItemSlot() == 0) {
                    if(PlayerManager.PlayerChat.get(p).equals(false)) {
                        PlayerManager.PlayerChat.replace(p, true);
                    }
                    StringUtils.sendMessage(p, p.getName(), CacheManager.Language.GLOBAL_INFO_SET_DISPLAY_NAME);
                }
            } else if(main.configManager.getArena(main.SetupArena.get(p)).getInt("arena.basic.setup_step") == 4) {
                if(main.setupManager.actualStepGame.get(p).equals(0)) {
                    if(p.getInventory().getHeldItemSlot() == 0) {
                        InventoryManager inv = new InventoryManager(main);
                        inv.setupMinigames(p);

                    }
                } else if (main.setupManager.selectedGame.containsKey(p)){
                    if(main.setupManager.actualStepGame.get(p) == 5) {
                        if(event.getPlayer().getInventory().getHeldItemSlot() == 0) {
                            if(event.getAction().name().contains("LEFT_CLICK")) {
                                if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
                                    Block clickedBlock = event.getClickedBlock();
                                    if(clickedBlock != null) {
                                        x = clickedBlock.getX();
                                        y = clickedBlock.getY();
                                        z = clickedBlock.getZ();
                                        world = clickedBlock.getWorld().getName();
                                    }
                                }
                                if(event.getAction() == Action.LEFT_CLICK_AIR) {
                                    Location clickedLocation = event.getPlayer().getLocation();
                                    x = clickedLocation.getX();
                                    y = clickedLocation.getY();
                                    z = clickedLocation.getZ();
                                    world = clickedLocation.getWorld().getName();
                                }

                                main.configManager.getArena(arenaid).set("arena.mini_games."+main.setupManager.selectedGame.get(p)+".basic.world", world);
                                main.configManager.saveArena(arenaid);
                                main.configManager.getArena(arenaid).set("arena.mini_games."+main.setupManager.selectedGame.get(p)+".bounds.min.x", x);
                                main.configManager.saveArena(arenaid);
                                main.configManager.getArena(arenaid).set("arena.mini_games."+main.setupManager.selectedGame.get(p)+".bounds.min.y", y);
                                main.configManager.saveArena(arenaid);
                                main.configManager.getArena(arenaid).set("arena.mini_games."+main.setupManager.selectedGame.get(p)+".bounds.min.z", z);
                                main.configManager.saveArena(arenaid);

                                main.configManager.reloadArena(arenaid);

                                StringUtils.sendMessage(event.getPlayer(), event.getPlayer().getName(), CacheManager.Language.GLOBAL_SUCCESS_MAGIC_STICK_POS
                                        .replace("{pos}", "min")
                                        .replace("{ms_world}", world)
                                        .replace("{ms_x}", String.valueOf(x))
                                        .replace("{ms_y}", String.valueOf(y))
                                        .replace("{ms_z}", String.valueOf(z)));

                            }
                            if(event.getAction().name().contains("RIGHT_CLICK")) {
                                if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                                    Block clickedBlock = event.getClickedBlock();
                                    if(clickedBlock != null) {
                                        x = clickedBlock.getX();
                                        y = clickedBlock.getY();
                                        z = clickedBlock.getZ();
                                        world = clickedBlock.getWorld().getName();
                                    }
                                }
                                if(event.getAction() == Action.RIGHT_CLICK_AIR) {
                                    Location clickedLocation = event.getPlayer().getLocation();
                                    x = clickedLocation.getX();
                                    y = clickedLocation.getY();
                                    z = clickedLocation.getZ();
                                    world = clickedLocation.getWorld().getName();
                                }

                                main.configManager.getArena(arenaid).set("arena.mini_games."+main.setupManager.selectedGame.get(p)+".basic.world", world);
                                main.configManager.saveArena(arenaid);
                                main.configManager.getArena(arenaid).set("arena.mini_games."+main.setupManager.selectedGame.get(p)+".bounds.max.x", x);
                                main.configManager.saveArena(arenaid);
                                main.configManager.getArena(arenaid).set("arena.mini_games."+main.setupManager.selectedGame.get(p)+".bounds.max.y", y);
                                main.configManager.saveArena(arenaid);
                                main.configManager.getArena(arenaid).set("arena.mini_games."+main.setupManager.selectedGame.get(p)+".bounds.max.z", z);
                                main.configManager.saveArena(arenaid);

                                main.configManager.reloadArena(arenaid);

                                StringUtils.sendMessage(event.getPlayer(), event.getPlayer().getName(), CacheManager.Language.GLOBAL_SUCCESS_MAGIC_STICK_POS
                                        .replace("{pos}", "max")
                                        .replace("{ms_world}", world)
                                        .replace("{ms_x}", String.valueOf(x))
                                        .replace("{ms_y}", String.valueOf(y))
                                        .replace("{ms_z}", String.valueOf(z)));
                            }


                        }
                    } else if(main.setupManager.actualStepGame.get(p) == 6) {
                        if(event.getPlayer().getInventory().getHeldItemSlot() == 0) {
                            if(event.getAction().name().contains("LEFT_CLICK")) {
                                if(main.configManager.getArena(arenaid).getInt("arena.mini_games."+main.setupManager.selectedGame.get(p)+".spawns.total") != 0) {
                                    Integer spawn = main.configManager.getArena(arenaid).getInt("arena.mini_games."+main.setupManager.selectedGame.get(p)+".spawns.total");
                                    main.configManager.getArena(arenaid).set("arena.mini_games."+main.setupManager.selectedGame.get(p)+".spawns.list.s"+spawn, "");
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.getArena(arenaid).set("arena.mini_games."+main.setupManager.selectedGame.get(p)+".spawns.total", spawn-1);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.reloadArena(arenaid);

                                    StringUtils.sendMessage(p, p.getName(), CacheManager.Language.GLOBAL_SUCCESS_SPAWN_REMOVED
                                            .replace("{spawn}", String.valueOf(spawn))
                                            .replace("{game}", main.setupManager.selectedGame.get(p))
                                            .replace("{arena_id}", Integer.toString(arenaid)));
                                }
                            }
                        }
                    }else if (main.setupManager.actualStepGame.get(p) == 7) {
                        if(p.getInventory().getHeldItemSlot() == 0) {
                            if(event.getAction().name().contains("RIGHT_CLICK")) {
                                if(VersionUtil.getQuantityItemsMainHand(p) < 64) {
                                    InventoryUtil.addItems(p.getInventory(), Material.CLOCK, 1);
                                    main.configManager.getArena(arenaid).set("arena.mini_games."+main.setupManager.selectedGame.get(p)+".basic.time", VersionUtil.getQuantityItemsMainHand(p)*60);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.reloadArena(arenaid);
                                }
                            } else if(event.getAction().name().contains("LEFT_CLICK")) {
                                if(VersionUtil.getQuantityItemsMainHand(p) > 1) {
                                    InventoryUtil.removeItems(p.getInventory(), Material.CLOCK, 1);
                                    main.configManager.getArena(arenaid).set("arena.mini_games."+main.setupManager.selectedGame.get(p)+".basic.time", VersionUtil.getQuantityItemsMainHand(p)*60);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.reloadArena(arenaid);
                                }
                            }
                        }
                    } else if (main.setupManager.actualStepGame.get(p) == 8) {
                        if(event.getPlayer().getInventory().getHeldItemSlot() == 0) {

                            // RACE

                            if(main.setupManager.selectedGame.get(p).equalsIgnoreCase("race")) {
                                if(event.getAction().name().contains("LEFT_CLICK")) {
                                    if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
                                        Block clickedBlock = event.getClickedBlock();
                                        if(clickedBlock != null) {
                                            x = clickedBlock.getX();
                                            y = clickedBlock.getY();
                                            z = clickedBlock.getZ();
                                            world = clickedBlock.getWorld().getName();
                                        }
                                    }
                                    if(event.getAction() == Action.LEFT_CLICK_AIR) {
                                        Location clickedLocation = event.getPlayer().getLocation();
                                        x = clickedLocation.getX();
                                        y = clickedLocation.getY();
                                        z = clickedLocation.getZ();
                                        world = clickedLocation.getWorld().getName();
                                    }

                                    main.configManager.getArena(arenaid).set("arena.mini_games.race.basic.world", world);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.getArena(arenaid).set("arena.mini_games.race.game.finish_line.bounds.min.x", x);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.getArena(arenaid).set("arena.mini_games.race.game.finish_line.bounds.min.y", y);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.getArena(arenaid).set("arena.mini_games.race.game.finish_line.bounds.min.z", z);
                                    main.configManager.saveArena(arenaid);

                                    main.configManager.reloadArena(arenaid);

                                    StringUtils.sendMessage(event.getPlayer(), event.getPlayer().getName(), CacheManager.Language.GLOBAL_SUCCESS_MAGIC_STICK_POS
                                            .replace("{pos}", "min")
                                            .replace("{ms_world}", world)
                                            .replace("{ms_x}", String.valueOf(x))
                                            .replace("{ms_y}", String.valueOf(y))
                                            .replace("{ms_z}", String.valueOf(z)));

                                }
                                if(event.getAction().name().contains("RIGHT_CLICK")) {
                                    if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                                        Block clickedBlock = event.getClickedBlock();
                                        if(clickedBlock != null) {
                                            x = clickedBlock.getX();
                                            y = clickedBlock.getY();
                                            z = clickedBlock.getZ();
                                            world = clickedBlock.getWorld().getName();
                                        }
                                    }
                                    if(event.getAction() == Action.RIGHT_CLICK_AIR) {
                                        Location clickedLocation = event.getPlayer().getLocation();
                                        x = clickedLocation.getX();
                                        y = clickedLocation.getY();
                                        z = clickedLocation.getZ();
                                        world = clickedLocation.getWorld().getName();
                                    }

                                    main.configManager.getArena(arenaid).set("arena.mini_games.race.basic.world", world);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.getArena(arenaid).set("arena.mini_games.race.game.finish_line.bounds.max.x", x);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.getArena(arenaid).set("arena.mini_games.race.game.finish_line.bounds.max.y", y);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.getArena(arenaid).set("arena.mini_games.race.game.finish_line.bounds.max.z", z);
                                    main.configManager.saveArena(arenaid);

                                    main.configManager.reloadArena(arenaid);

                                    StringUtils.sendMessage(event.getPlayer(), event.getPlayer().getName(), CacheManager.Language.GLOBAL_SUCCESS_MAGIC_STICK_POS
                                            .replace("{pos}", "max")
                                            .replace("{ms_world}", world)
                                            .replace("{ms_x}", String.valueOf(x))
                                            .replace("{ms_y}", String.valueOf(y))
                                            .replace("{ms_z}", String.valueOf(z)));
                                }

                            }

                            // SPLEEF

                            if(main.setupManager.selectedGame.get(p).equalsIgnoreCase("spleef")) {
                                if(event.getAction().name().contains("LEFT_CLICK")) {
                                    if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
                                        Block clickedBlock = event.getClickedBlock();
                                        if(clickedBlock != null) {
                                            x = clickedBlock.getX();
                                            y = clickedBlock.getY();
                                            z = clickedBlock.getZ();
                                            world = clickedBlock.getWorld().getName();
                                        }
                                    }
                                    if(event.getAction() == Action.LEFT_CLICK_AIR) {
                                        Location clickedLocation = event.getPlayer().getLocation();
                                        x = clickedLocation.getX();
                                        y = clickedLocation.getY();
                                        z = clickedLocation.getZ();
                                        world = clickedLocation.getWorld().getName();
                                    }

                                    main.configManager.getArena(arenaid).set("arena.mini_games.spleef.basic.world", world);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.getArena(arenaid).set("arena.mini_games.spleef.game.floor.bounds.min.x", x);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.getArena(arenaid).set("arena.mini_games.spleef.game.floor.bounds.min.y", y);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.getArena(arenaid).set("arena.mini_games.spleef.game.floor.bounds.min.z", z);
                                    main.configManager.saveArena(arenaid);

                                    main.configManager.reloadArena(arenaid);

                                    StringUtils.sendMessage(event.getPlayer(), event.getPlayer().getName(), CacheManager.Language.GLOBAL_SUCCESS_MAGIC_STICK_POS
                                            .replace("{pos}", "min")
                                            .replace("{ms_world}", world)
                                            .replace("{ms_x}", String.valueOf(x))
                                            .replace("{ms_y}", String.valueOf(y))
                                            .replace("{ms_z}", String.valueOf(z)));

                                }
                                if(event.getAction().name().contains("RIGHT_CLICK")) {
                                    if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                                        Block clickedBlock = event.getClickedBlock();
                                        if(clickedBlock != null) {
                                            x = clickedBlock.getX();
                                            y = clickedBlock.getY();
                                            z = clickedBlock.getZ();
                                            world = clickedBlock.getWorld().getName();
                                        }
                                    }
                                    if(event.getAction() == Action.RIGHT_CLICK_AIR) {
                                        Location clickedLocation = event.getPlayer().getLocation();
                                        x = clickedLocation.getX();
                                        y = clickedLocation.getY();
                                        z = clickedLocation.getZ();
                                        world = clickedLocation.getWorld().getName();
                                    }

                                    main.configManager.getArena(arenaid).set("arena.mini_games.spleef.basic.world", world);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.getArena(arenaid).set("arena.mini_games.spleef.game.floor.bounds.max.x", x);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.getArena(arenaid).set("arena.mini_games.spleef.game.floor.bounds.max.y", y);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.getArena(arenaid).set("arena.mini_games.spleef.game.floor.bounds.max.z", z);
                                    main.configManager.saveArena(arenaid);

                                    main.configManager.reloadArena(arenaid);

                                    StringUtils.sendMessage(event.getPlayer(), event.getPlayer().getName(), CacheManager.Language.GLOBAL_SUCCESS_MAGIC_STICK_POS
                                            .replace("{pos}", "max")
                                            .replace("{ms_world}", world)
                                            .replace("{ms_x}", String.valueOf(x))
                                            .replace("{ms_y}", String.valueOf(y))
                                            .replace("{ms_z}", String.valueOf(z)));
                                }
                            }

                            // TRAFFIC LIGHT
                            if(main.setupManager.selectedGame.get(p).equalsIgnoreCase("traffic_light")) {
                                if(event.getAction().name().contains("LEFT_CLICK")) {
                                    if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
                                        Block clickedBlock = event.getClickedBlock();
                                        if(clickedBlock != null) {
                                            x = clickedBlock.getX();
                                            y = clickedBlock.getY();
                                            z = clickedBlock.getZ();
                                            world = clickedBlock.getWorld().getName();
                                        }
                                    }
                                    if(event.getAction() == Action.LEFT_CLICK_AIR) {
                                        Location clickedLocation = event.getPlayer().getLocation();
                                        x = clickedLocation.getX();
                                        y = clickedLocation.getY();
                                        z = clickedLocation.getZ();
                                        world = clickedLocation.getWorld().getName();
                                    }

                                    main.configManager.getArena(arenaid).set("arena.mini_games.traffic_light.basic.world", world);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.getArena(arenaid).set("arena.mini_games.traffic_light.game.finish_line.bounds.min.x", x);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.getArena(arenaid).set("arena.mini_games.traffic_light.game.finish_line.bounds.min.y", y);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.getArena(arenaid).set("arena.mini_games.traffic_light.game.finish_line.bounds.min.z", z);
                                    main.configManager.saveArena(arenaid);

                                    main.configManager.reloadArena(arenaid);

                                    StringUtils.sendMessage(event.getPlayer(), event.getPlayer().getName(), CacheManager.Language.GLOBAL_SUCCESS_MAGIC_STICK_POS
                                            .replace("{pos}", "min")
                                            .replace("{ms_world}", world)
                                            .replace("{ms_x}", String.valueOf(x))
                                            .replace("{ms_y}", String.valueOf(y))
                                            .replace("{ms_z}", String.valueOf(z)));

                                }
                                if(event.getAction().name().contains("RIGHT_CLICK")) {
                                    if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                                        Block clickedBlock = event.getClickedBlock();
                                        if(clickedBlock != null) {
                                            x = clickedBlock.getX();
                                            y = clickedBlock.getY();
                                            z = clickedBlock.getZ();
                                            world = clickedBlock.getWorld().getName();
                                        }
                                    }
                                    if(event.getAction() == Action.RIGHT_CLICK_AIR) {
                                        Location clickedLocation = event.getPlayer().getLocation();
                                        x = clickedLocation.getX();
                                        y = clickedLocation.getY();
                                        z = clickedLocation.getZ();
                                        world = clickedLocation.getWorld().getName();
                                    }

                                    main.configManager.getArena(arenaid).set("arena.mini_games.traffic_light.basic.world", world);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.getArena(arenaid).set("arena.mini_games.traffic_light.game.finish_line.bounds.max.x", x);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.getArena(arenaid).set("arena.mini_games.traffic_light.game.finish_line.bounds.max.y", y);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.getArena(arenaid).set("arena.mini_games.traffic_light.game.finish_line.bounds.max.z", z);
                                    main.configManager.saveArena(arenaid);

                                    main.configManager.reloadArena(arenaid);

                                    StringUtils.sendMessage(event.getPlayer(), event.getPlayer().getName(), CacheManager.Language.GLOBAL_SUCCESS_MAGIC_STICK_POS
                                            .replace("{pos}", "max")
                                            .replace("{ms_world}", world)
                                            .replace("{ms_x}", String.valueOf(x))
                                            .replace("{ms_y}", String.valueOf(y))
                                            .replace("{ms_z}", String.valueOf(z)));
                                }

                            }

                            // MINEFIELD
                            if(main.setupManager.selectedGame.get(p).equalsIgnoreCase("minefield")) {
                                if(event.getAction().name().contains("LEFT_CLICK")) {
                                    if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
                                        Block clickedBlock = event.getClickedBlock();
                                        if(clickedBlock != null) {
                                            x = clickedBlock.getX();
                                            y = clickedBlock.getY();
                                            z = clickedBlock.getZ();
                                            world = clickedBlock.getWorld().getName();
                                        }
                                    }
                                    if(event.getAction() == Action.LEFT_CLICK_AIR) {
                                        Location clickedLocation = event.getPlayer().getLocation();
                                        x = clickedLocation.getX();
                                        y = clickedLocation.getY();
                                        z = clickedLocation.getZ();
                                        world = clickedLocation.getWorld().getName();
                                    }

                                    main.configManager.getArena(arenaid).set("arena.mini_games.minefield.basic.world", world);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.getArena(arenaid).set("arena.mini_games.minefield.game.finish_line.bounds.min.x", x);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.getArena(arenaid).set("arena.mini_games.minefield.game.finish_line.bounds.min.y", y);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.getArena(arenaid).set("arena.mini_games.minefield.game.finish_line.bounds.min.z", z);
                                    main.configManager.saveArena(arenaid);

                                    main.configManager.reloadArena(arenaid);

                                    StringUtils.sendMessage(event.getPlayer(), event.getPlayer().getName(), CacheManager.Language.GLOBAL_SUCCESS_MAGIC_STICK_POS
                                            .replace("{pos}", "min")
                                            .replace("{ms_world}", world)
                                            .replace("{ms_x}", String.valueOf(x))
                                            .replace("{ms_y}", String.valueOf(y))
                                            .replace("{ms_z}", String.valueOf(z)));

                                }
                                if(event.getAction().name().contains("RIGHT_CLICK")) {
                                    if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                                        Block clickedBlock = event.getClickedBlock();
                                        if(clickedBlock != null) {
                                            x = clickedBlock.getX();
                                            y = clickedBlock.getY();
                                            z = clickedBlock.getZ();
                                            world = clickedBlock.getWorld().getName();
                                        }
                                    }
                                    if(event.getAction() == Action.RIGHT_CLICK_AIR) {
                                        Location clickedLocation = event.getPlayer().getLocation();
                                        x = clickedLocation.getX();
                                        y = clickedLocation.getY();
                                        z = clickedLocation.getZ();
                                        world = clickedLocation.getWorld().getName();
                                    }

                                    main.configManager.getArena(arenaid).set("arena.mini_games.minefield.basic.world", world);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.getArena(arenaid).set("arena.mini_games.minefield.game.finish_line.bounds.max.x", x);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.getArena(arenaid).set("arena.mini_games.minefield.game.finish_line.bounds.max.y", y);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.getArena(arenaid).set("arena.mini_games.minefield.game.finish_line.bounds.max.z", z);
                                    main.configManager.saveArena(arenaid);

                                    main.configManager.reloadArena(arenaid);

                                    StringUtils.sendMessage(event.getPlayer(), event.getPlayer().getName(), CacheManager.Language.GLOBAL_SUCCESS_MAGIC_STICK_POS
                                            .replace("{pos}", "max")
                                            .replace("{ms_world}", world)
                                            .replace("{ms_x}", String.valueOf(x))
                                            .replace("{ms_y}", String.valueOf(y))
                                            .replace("{ms_z}", String.valueOf(z)));
                                }

                            }

                            // RED ALERT

                            if(main.setupManager.selectedGame.get(p).equalsIgnoreCase("red_alert")) {
                                if(event.getAction().name().contains("LEFT_CLICK")) {
                                    if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
                                        Block clickedBlock = event.getClickedBlock();
                                        if(clickedBlock != null) {
                                            x = clickedBlock.getX();
                                            y = clickedBlock.getY();
                                            z = clickedBlock.getZ();
                                            world = clickedBlock.getWorld().getName();
                                        }
                                    }
                                    if(event.getAction() == Action.LEFT_CLICK_AIR) {
                                        Location clickedLocation = event.getPlayer().getLocation();
                                        x = clickedLocation.getX();
                                        y = clickedLocation.getY();
                                        z = clickedLocation.getZ();
                                        world = clickedLocation.getWorld().getName();
                                    }

                                    main.configManager.getArena(arenaid).set("arena.mini_games.red_alert.basic.world", world);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.getArena(arenaid).set("arena.mini_games.red_alert.game.floor.bounds.min.x", x);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.getArena(arenaid).set("arena.mini_games.red_alert.game.floor.bounds.min.y", y);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.getArena(arenaid).set("arena.mini_games.red_alert.game.floor.bounds.min.z", z);
                                    main.configManager.saveArena(arenaid);

                                    main.configManager.reloadArena(arenaid);

                                    StringUtils.sendMessage(event.getPlayer(), event.getPlayer().getName(), CacheManager.Language.GLOBAL_SUCCESS_MAGIC_STICK_POS
                                            .replace("{pos}", "min")
                                            .replace("{ms_world}", world)
                                            .replace("{ms_x}", String.valueOf(x))
                                            .replace("{ms_y}", String.valueOf(y))
                                            .replace("{ms_z}", String.valueOf(z)));

                                }
                                if(event.getAction().name().contains("RIGHT_CLICK")) {
                                    if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                                        Block clickedBlock = event.getClickedBlock();
                                        if(clickedBlock != null) {
                                            x = clickedBlock.getX();
                                            y = clickedBlock.getY();
                                            z = clickedBlock.getZ();
                                            world = clickedBlock.getWorld().getName();
                                        }
                                    }
                                    if(event.getAction() == Action.RIGHT_CLICK_AIR) {
                                        Location clickedLocation = event.getPlayer().getLocation();
                                        x = clickedLocation.getX();
                                        y = clickedLocation.getY();
                                        z = clickedLocation.getZ();
                                        world = clickedLocation.getWorld().getName();
                                    }

                                    main.configManager.getArena(arenaid).set("arena.mini_games.red_alert.basic.world", world);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.getArena(arenaid).set("arena.mini_games.red_alert.game.floor.bounds.max.x", x);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.getArena(arenaid).set("arena.mini_games.red_alert.game.floor.bounds.max.y", y);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.getArena(arenaid).set("arena.mini_games.red_alert.game.floor.bounds.max.z", z);
                                    main.configManager.saveArena(arenaid);

                                    main.configManager.reloadArena(arenaid);

                                    StringUtils.sendMessage(event.getPlayer(), event.getPlayer().getName(), CacheManager.Language.GLOBAL_SUCCESS_MAGIC_STICK_POS
                                            .replace("{pos}", "max")
                                            .replace("{ms_world}", world)
                                            .replace("{ms_x}", String.valueOf(x))
                                            .replace("{ms_y}", String.valueOf(y))
                                            .replace("{ms_z}", String.valueOf(z)));
                                }
                            }

                            // FAST ZONE

                            if(main.setupManager.selectedGame.get(p).equalsIgnoreCase("fast_zone")) {
                                if(event.getAction().name().contains("LEFT_CLICK")) {
                                    if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
                                        Block clickedBlock = event.getClickedBlock();
                                        if(clickedBlock != null) {
                                            x = clickedBlock.getX();
                                            y = clickedBlock.getY();
                                            z = clickedBlock.getZ();
                                            world = clickedBlock.getWorld().getName();
                                        }
                                    }
                                    if(event.getAction() == Action.LEFT_CLICK_AIR) {
                                        Location clickedLocation = event.getPlayer().getLocation();
                                        x = clickedLocation.getX();
                                        y = clickedLocation.getY();
                                        z = clickedLocation.getZ();
                                        world = clickedLocation.getWorld().getName();
                                    }

                                    main.configManager.getArena(arenaid).set("arena.mini_games.fast_zone.basic.world", world);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.getArena(arenaid).set("arena.mini_games.fast_zone.game.finish_line.bounds.min.x", x);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.getArena(arenaid).set("arena.mini_games.fast_zone.game.finish_line.bounds.min.y", y);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.getArena(arenaid).set("arena.mini_games.fast_zone.game.finish_line.bounds.min.z", z);
                                    main.configManager.saveArena(arenaid);

                                    main.configManager.reloadArena(arenaid);

                                    StringUtils.sendMessage(event.getPlayer(), event.getPlayer().getName(), CacheManager.Language.GLOBAL_SUCCESS_MAGIC_STICK_POS
                                            .replace("{pos}", "min")
                                            .replace("{ms_world}", world)
                                            .replace("{ms_x}", String.valueOf(x))
                                            .replace("{ms_y}", String.valueOf(y))
                                            .replace("{ms_z}", String.valueOf(z)));

                                }
                                if(event.getAction().name().contains("RIGHT_CLICK")) {
                                    if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                                        Block clickedBlock = event.getClickedBlock();
                                        if(clickedBlock != null) {
                                            x = clickedBlock.getX();
                                            y = clickedBlock.getY();
                                            z = clickedBlock.getZ();
                                            world = clickedBlock.getWorld().getName();
                                        }
                                    }
                                    if(event.getAction() == Action.RIGHT_CLICK_AIR) {
                                        Location clickedLocation = event.getPlayer().getLocation();
                                        x = clickedLocation.getX();
                                        y = clickedLocation.getY();
                                        z = clickedLocation.getZ();
                                        world = clickedLocation.getWorld().getName();
                                    }

                                    main.configManager.getArena(arenaid).set("arena.mini_games.fast_zone.basic.world", world);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.getArena(arenaid).set("arena.mini_games.fast_zone.game.finish_line.bounds.max.x", x);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.getArena(arenaid).set("arena.mini_games.fast_zone.game.finish_line.bounds.max.y", y);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.getArena(arenaid).set("arena.mini_games.fast_zone.game.finish_line.bounds.max.z", z);
                                    main.configManager.saveArena(arenaid);

                                    main.configManager.reloadArena(arenaid);

                                    StringUtils.sendMessage(event.getPlayer(), event.getPlayer().getName(), CacheManager.Language.GLOBAL_SUCCESS_MAGIC_STICK_POS
                                            .replace("{pos}", "max")
                                            .replace("{ms_world}", world)
                                            .replace("{ms_x}", String.valueOf(x))
                                            .replace("{ms_y}", String.valueOf(y))
                                            .replace("{ms_z}", String.valueOf(z)));
                                }

                            }
                        }
                    } else if (main.setupManager.actualStepGame.get(p) == 9) {
                        if(event.getPlayer().getInventory().getHeldItemSlot() == 0) {
                            if(main.setupManager.selectedGame.get(p).equalsIgnoreCase("minefield")) {
                                if(event.getAction().name().contains("LEFT_CLICK")) {
                                    if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
                                        Block clickedBlock = event.getClickedBlock();
                                        if(clickedBlock != null) {
                                            x = clickedBlock.getX();
                                            y = clickedBlock.getY();
                                            z = clickedBlock.getZ();
                                            world = clickedBlock.getWorld().getName();
                                        }
                                    }
                                    if(event.getAction() == Action.LEFT_CLICK_AIR) {
                                        StringUtils.sendMessage(event.getPlayer(), event.getPlayer().getName(), CacheManager.Language.GLOBAL_ERROR_AIR_SELECTION_ERROR);
                                    }

                                    main.configManager.getArena(arenaid).set("arena.mini_games.minefield.basic.world", world);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.getArena(arenaid).set("arena.mini_games.minefield.game.floor.bounds.min.x", x);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.getArena(arenaid).set("arena.mini_games.minefield.game.floor.bounds.min.y", y);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.getArena(arenaid).set("arena.mini_games.minefield.game.floor.bounds.min.z", z);
                                    main.configManager.saveArena(arenaid);

                                    main.configManager.reloadArena(arenaid);

                                    StringUtils.sendMessage(event.getPlayer(), event.getPlayer().getName(), CacheManager.Language.GLOBAL_SUCCESS_MAGIC_STICK_POS
                                            .replace("{pos}", "min")
                                            .replace("{ms_world}", world)
                                            .replace("{ms_x}", String.valueOf(x))
                                            .replace("{ms_y}", String.valueOf(y))
                                            .replace("{ms_z}", String.valueOf(z)));

                                }
                                if(event.getAction().name().contains("RIGHT_CLICK")) {
                                    if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                                        Block clickedBlock = event.getClickedBlock();
                                        if(clickedBlock != null) {
                                            x = clickedBlock.getX();
                                            y = clickedBlock.getY();
                                            z = clickedBlock.getZ();
                                            world = clickedBlock.getWorld().getName();
                                        }
                                    }
                                    if(event.getAction() == Action.RIGHT_CLICK_AIR) {
                                        StringUtils.sendMessage(event.getPlayer(), event.getPlayer().getName(), CacheManager.Language.GLOBAL_ERROR_AIR_SELECTION_ERROR);
                                    }

                                    main.configManager.getArena(arenaid).set("arena.mini_games.minefield.basic.world", world);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.getArena(arenaid).set("arena.mini_games.minefield.game.floor.bounds.max.x", x);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.getArena(arenaid).set("arena.mini_games.minefield.game.floor.bounds.max.y", y);
                                    main.configManager.saveArena(arenaid);
                                    main.configManager.getArena(arenaid).set("arena.mini_games.minefield.game.floor.bounds.max.z", z);
                                    main.configManager.saveArena(arenaid);

                                    main.configManager.reloadArena(arenaid);

                                    StringUtils.sendMessage(event.getPlayer(), event.getPlayer().getName(), CacheManager.Language.GLOBAL_SUCCESS_MAGIC_STICK_POS
                                            .replace("{pos}", "max")
                                            .replace("{ms_world}", world)
                                            .replace("{ms_x}", String.valueOf(x))
                                            .replace("{ms_y}", String.valueOf(y))
                                            .replace("{ms_z}", String.valueOf(z)));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}