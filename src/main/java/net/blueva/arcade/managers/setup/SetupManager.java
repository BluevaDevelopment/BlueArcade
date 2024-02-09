package net.blueva.arcade.managers.setup;

import net.blueva.arcade.Main;
import net.blueva.arcade.ObjectResolver;
import net.blueva.arcade.managers.ArenaManager;
import net.blueva.arcade.managers.CacheManager;
import net.blueva.arcade.utils.InventoryUtil;
import net.blueva.arcade.utils.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static net.blueva.arcade.utils.InventoryUtil.getInventoryContent;

public class SetupManager {

    private final Main main ;

    public SetupManager(Main main) {
        this.main = main;
    }

    public HashMap<Player, String> selectedGame = new HashMap<>();
    public HashMap<Player, Integer> actualStepGame = new HashMap<>();

    public void cancel(Player player) {
        if(main.SetupProcess.containsKey((player))) {
            if(!main.SetupProcess.containsValue(false)) {
                main.configManager.getArenaFile(main.SetupArena.get(player)).delete();
                main.updateArenaList();

                actualStepGame.remove(player);

                player.getInventory().clear();
                main.getServer().getScheduler().scheduleSyncDelayedTask(main, () -> {
                    main.SetupArena.remove(player);
                    main.SetupProcess.remove(player);

                    try {
                        player.getInventory().setContents(getInventoryContent(main.configManager.getUser(player.getUniqueId()).getString("inventory")));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }, 10);

                StringUtils.sendMessage(player, player.getName(), CacheManager.Language.GLOBAL_INFO_CANCELLED_SETUP);
            } else {
                StringUtils.sendMessage(player, player.getName(), CacheManager.Language.GLOBAL_ERROR_NO_SETUP_CANCEL);
            }
        } else {
            StringUtils.sendMessage(player, player.getName(), CacheManager.Language.GLOBAL_ERROR_NO_SETUP_CANCEL);
        }
    }

    public void pause(Player player) {
        if(main.SetupProcess.containsKey((player))) {
            if(!main.SetupProcess.containsValue(false)) {
                player.getInventory().clear();
                main.getServer().getScheduler().scheduleSyncDelayedTask(main, () -> {
                    main.SetupArena.remove(player);
                    main.SetupProcess.remove(player);
                    selectedGame.remove(player);
                    actualStepGame.remove(player);
                }, 10);
                try {
                    player.getInventory().setContents(InventoryUtil.getInventoryContent(main.configManager.getUser(player.getUniqueId()).getString("inventory")));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                StringUtils.sendMessage(player, player.getName(), CacheManager.Language.GLOBAL_INFO_PAUSED_SETUP);
            } else {
                StringUtils.sendMessage(player, player.getName(), CacheManager.Language.GLOBAL_ERROR_NO_SETUP_PAUSE);
            }
        } else {
            StringUtils.sendMessage(player, player.getName(), CacheManager.Language.GLOBAL_ERROR_NO_SETUP_PAUSE);
        }
    }

    public void start(@NotNull Player player, Integer arenaid) {
        String playerstring = player.getName();

        if(org.apache.commons.lang.StringUtils.isNumeric(String.valueOf(arenaid))) {

            if(!ArenaManager.ArenaList.contains(arenaid)) {
                if(main.configManager.getArena(arenaid).getInt("arena.basic.setup_step") == 0 && !main.SetupProcess.containsKey(player) && !main.SetupArena.containsKey(player)) {
                    main.SetupProcess.put(player, true);
                    main.SetupArena.put(player, arenaid);
                    InventoryUtil.SaveInventory(main, player);
                    player.getInventory().clear();
                } else {
                    return;
                }

                main.configManager.registerArena(arenaid);
                main.configManager.getArena(arenaid).set("arena.basic.id", arenaid);
                main.configManager.saveArena(arenaid);

                main.updateArenaList();

                actualStepGame.put(player, 0);

                File regionsf = new File(main.getDataFolder()+"/data/regions/"+arenaid);
                if(!regionsf.exists()) {
                    regionsf.mkdirs();
                }

                StringUtils.sendMessage(player, playerstring, CacheManager.Language.GLOBAL_SUCCESS_ARENA_CREATED.replace("{arena_id}", Integer.toString(arenaid)));
                firstStep(player, arenaid);
            } else if (main.configManager.getArena(arenaid).getInt("arena.basic.setup_step") != 0){
                main.SetupProcess.put(player, true);
                main.SetupArena.put(player, arenaid);
                InventoryUtil.SaveInventory(main, player);
                player.getInventory().clear();
                if(main.configManager.getArena(arenaid).getInt("arena.basic.setup_step") == 1) {
                    firstStep(player, arenaid);
                } else if(main.configManager.getArena(arenaid).getInt("arena.basic.setup_step") == 2) {
                    secondStep(player, arenaid);
                } else if(main.configManager.getArena(arenaid).getInt("arena.basic.setup_step") == 3) {
                    thirdStep(player, arenaid);
                } else if(main.configManager.getArena(arenaid).getInt("arena.basic.setup_step") == 4) {
                    fourthStep(player, arenaid);
                }
                actualStepGame.put(player, 0);
            } else {
                StringUtils.sendMessage(player, playerstring, CacheManager.Language.GLOBAL_ERROR_ID_TAKEN);
            }
        } else {
            StringUtils.sendMessage(player, playerstring, CacheManager.Language.GLOBAL_ERROR_ONLY_NUMBERS);
        }
    }

    public void next(@NotNull Player player) {
        int arenaid = main.SetupArena.get(player);

        if(org.apache.commons.lang.StringUtils.isNumeric(String.valueOf(arenaid))) {
            if(ArenaManager.ArenaList.contains(arenaid)) {
                if(main.SetupProcess.containsKey(player) && main.SetupArena.containsKey(player)) {
                    if(main.configManager.getArena(arenaid).getString("arena.basic.setup_step").equals("1")) {
                        if(!main.configManager.getArena(arenaid).getString("arena.waiting_lobby.world").equals("")
                                && main.configManager.getArena(arenaid).getDouble("arena.waiting_lobby.yaw") != 0) {
                            player.getInventory().clear();
                            secondStep(player, arenaid);
                        } else {
                            StringUtils.sendMessage(player, player.getName(), CacheManager.Language.GLOBAL_ERROR_FINISH_FIRST);
                        }
                    } else if (main.configManager.getArena(arenaid).getString("arena.basic.setup_step").equals("2")) {
                        player.getInventory().clear();
                        thirdStep(player, arenaid);
                    } else if (main.configManager.getArena(arenaid).getString("arena.basic.setup_step").equals("3")) {
                        if(!main.configManager.getArena(arenaid).getString("arena.basic.display_name").equals("")) {
                            player.getInventory().clear();
                            fourthStep(player, arenaid);
                        } else {
                            StringUtils.sendMessage(player, player.getName(), CacheManager.Language.GLOBAL_ERROR_FINISH_FIRST);
                        }
                    } else if (main.configManager.getArena(arenaid).getString("arena.basic.setup_step").equals("4")) {
                        if (main.setupManager.actualStepGame.get(player).equals(0)) {
                            if(selectedGame.containsKey(player)) {
                                fifthStep(player, arenaid, selectedGame.get(player));
                                return;
                            }
                            if(main.configManager.getArena(arenaid).getInt("arena.basic.configured_games") != 0) {
                                finish(player, arenaid);
                            } else {
                                StringUtils.sendMessage(player, player.getName(), CacheManager.Language.GLOBAL_ERROR_AT_LEAST_ONE_GAME);
                            }
                        } else if (main.setupManager.actualStepGame.get(player).equals(5)) {
                            if(main.configManager.getArena(arenaid).getDouble("arena.mini_games."+selectedGame.get(player)+".bounds.min.x") != 0
                            || main.configManager.getArena(arenaid).getDouble("arena.mini_games."+selectedGame.get(player)+".bounds.min.y") != 0
                            || main.configManager.getArena(arenaid).getDouble("arena.mini_games."+selectedGame.get(player)+".bounds.min.z") != 0
                            || main.configManager.getArena(arenaid).getDouble("arena.mini_games."+selectedGame.get(player)+".bounds.max.x") != 0
                            || main.configManager.getArena(arenaid).getDouble("arena.mini_games."+selectedGame.get(player)+".bounds.max.y") != 0
                            || main.configManager.getArena(arenaid).getDouble("arena.mini_games."+selectedGame.get(player)+".bounds.max.z") != 0) {
                                sixthStep(player, arenaid, selectedGame.get(player));
                            } else {
                                StringUtils.sendMessage(player, player.getName(), CacheManager.Language.GLOBAL_ERROR_FINISH_FIRST);
                            }
                        } else if (main.setupManager.actualStepGame.get(player).equals(6)) {
                            if(main.configManager.getArena(arenaid).getInt("arena.basic.max_players") <= main.configManager.getArena(arenaid).getInt("arena.mini_games."+selectedGame.get(player)+".spawns.total")) {
                                seventhStep(player, arenaid, selectedGame.get(player));
                            } else {
                                StringUtils.sendMessage(player, player.getName(), CacheManager.Language.GLOBAL_ERROR_MINIMUM_SPAWNS.replace("{minimum_spawns}", String.valueOf(main.configManager.getArena(arenaid).getInt("arena.mini_games."+selectedGame.get(player)+".spawns.total"))));
                            }

                        } else if (main.setupManager.actualStepGame.get(player).equals(7)) {
                            if(selectedGame.containsKey(player)) {
                                if(selectedGame.get(player).equalsIgnoreCase("race")) {
                                    RaceSetupManager.eighthStep(player, arenaid, main);
                                } else if(selectedGame.get(player).equalsIgnoreCase("spleef")) {
                                    SpleefSetupManager.eighthStep(player, arenaid, main);
                                } else if (selectedGame.get(player).equalsIgnoreCase("all_against_all")) {
                                    AllAgainstAllSetupManager.finishSetup(player, arenaid, main);
                                } else if (selectedGame.get(player).equalsIgnoreCase("snowball_fight")) {
                                    SnowballFightSetup.finishSetup(player, arenaid, main);
                                } else if (selectedGame.get(player).equalsIgnoreCase("one_in_the_chamber")) {
                                    OneInTheChamberSetupManager.finishSetup(player, arenaid, main);
                                } else if (selectedGame.get(player).equalsIgnoreCase("traffic_light")) {
                                    TrafficLightSetupManager.eighthStep(player, arenaid, main);
                                } else if (selectedGame.get(player).equalsIgnoreCase("minefield")) {
                                    MinefieldSetupManager.eighthStep(player, arenaid, main);
                                } else if (selectedGame.get(player).equalsIgnoreCase("exploding_sheep")) {
                                    ExplodingSheepSetupManager.finishSetup(player, arenaid, main);
                                } else if (selectedGame.get(player).equalsIgnoreCase("tnt_tag")) {
                                    TNTTagSetupManager.finishSetup(player, arenaid, main);
                                } else if (selectedGame.get(player).equalsIgnoreCase("red_alert")) {
                                    RedAlertSetupManager.eighthStep(player, arenaid, main);
                                } else if (selectedGame.get(player).equalsIgnoreCase("knock_back")) {
                                    KnockBackSetupManager.finishSetup(player, arenaid, main);
                                } else if (selectedGame.get(player).equalsIgnoreCase("fast_zone")) {
                                    FastZoneSetupManager.eighthStep(player, arenaid, main);
                                }
                            }
                        } else if (main.setupManager.actualStepGame.get(player).equals(8)) {
                            if(selectedGame.get(player).equalsIgnoreCase("race")) {
                                if(main.configManager.getArena(arenaid).getDouble("arena.mini_games.race.game.finish_line.bounds.min.x") != 0
                                        || main.configManager.getArena(arenaid).getDouble("arena.mini_games.race.game.finish_line.bounds.min.y") != 0
                                        || main.configManager.getArena(arenaid).getDouble("arena.mini_games.race.game.finish_line.bounds.min.z") != 0
                                        || main.configManager.getArena(arenaid).getDouble("arena.mini_games.race.game.finish_line.bounds.max.x") != 0
                                        || main.configManager.getArena(arenaid).getDouble("arena.mini_games.race.game.finish_line.bounds.max.y") != 0
                                        || main.configManager.getArena(arenaid).getDouble("arena.mini_games.race.game.finish_line.bounds.max.z") != 0) {
                                    RaceSetupManager.finishSetup(player, arenaid, main);
                                }

                            } else if(selectedGame.get(player).equalsIgnoreCase("spleef")) {
                                if(main.configManager.getArena(arenaid).getDouble("arena.mini_games.spleef.game.floor.bounds.min.x") != 0
                                        || main.configManager.getArena(arenaid).getDouble("arena.mini_games.spleef.game.floor.bounds.min.y") != 0
                                        || main.configManager.getArena(arenaid).getDouble("arena.mini_games.spleef.game.floor.bounds.min.z") != 0
                                        || main.configManager.getArena(arenaid).getDouble("arena.mini_games.spleef.game.floor.bounds.max.x") != 0
                                        || main.configManager.getArena(arenaid).getDouble("arena.mini_games.spleef.game.floor.bounds.max.y") != 0
                                        || main.configManager.getArena(arenaid).getDouble("arena.mini_games.spleef.game.floor.bounds.max.z") != 0) {
                                    SpleefSetupManager.finishSetup(player, arenaid, main);
                                }

                            } else if (selectedGame.get(player).equalsIgnoreCase("traffic_light")) {
                                if(main.configManager.getArena(arenaid).getDouble("arena.mini_games.traffic_light.game.finish_line.bounds.min.x") != 0
                                        || main.configManager.getArena(arenaid).getDouble("arena.mini_games.traffic_light.game.finish_line.bounds.min.y") != 0
                                        || main.configManager.getArena(arenaid).getDouble("arena.mini_games.traffic_light.game.finish_line.bounds.min.z") != 0
                                        || main.configManager.getArena(arenaid).getDouble("arena.mini_games.traffic_light.game.finish_line.bounds.max.x") != 0
                                        || main.configManager.getArena(arenaid).getDouble("arena.mini_games.traffic_light.game.finish_line.bounds.max.y") != 0
                                        || main.configManager.getArena(arenaid).getDouble("arena.mini_games.traffic_light.game.finish_line.bounds.max.z") != 0) {
                                    TrafficLightSetupManager.finishSetup(player, arenaid, main);
                                }
                            } else if(selectedGame.get(player).equalsIgnoreCase("minefield")) {
                                if(main.configManager.getArena(arenaid).getDouble("arena.mini_games.minefield.game.finish_line.bounds.min.x") != 0
                                        || main.configManager.getArena(arenaid).getDouble("arena.mini_games.minefield.game.finish_line.bounds.min.y") != 0
                                        || main.configManager.getArena(arenaid).getDouble("arena.mini_games.minefield.game.finish_line.bounds.min.z") != 0
                                        || main.configManager.getArena(arenaid).getDouble("arena.mini_games.minefield.game.finish_line.bounds.max.x") != 0
                                        || main.configManager.getArena(arenaid).getDouble("arena.mini_games.minefield.game.finish_line.bounds.max.y") != 0
                                        || main.configManager.getArena(arenaid).getDouble("arena.mini_games.minefield.game.finish_line.bounds.max.z") != 0) {
                                    MinefieldSetupManager.ninthStep(player, arenaid, main);
                                }

                            } else if(selectedGame.get(player).equalsIgnoreCase("red_alert")) {
                                if(main.configManager.getArena(arenaid).getDouble("arena.mini_games.red_alert.game.floor.bounds.min.x") != 0
                                        || main.configManager.getArena(arenaid).getDouble("arena.mini_games.red_alert.game.floor.bounds.min.y") != 0
                                        || main.configManager.getArena(arenaid).getDouble("arena.mini_games.red_alert.game.floor.bounds.min.z") != 0
                                        || main.configManager.getArena(arenaid).getDouble("arena.mini_games.red_alert.game.floor.bounds.max.x") != 0
                                        || main.configManager.getArena(arenaid).getDouble("arena.mini_games.red_alert.game.floor.bounds.max.y") != 0
                                        || main.configManager.getArena(arenaid).getDouble("arena.mini_games.red_alert.game.floor.bounds.max.z") != 0) {
                                    RedAlertSetupManager.finishSetup(player, arenaid, main);
                                }

                            } else if(selectedGame.get(player).equalsIgnoreCase("fast_zone")) {
                                if(main.configManager.getArena(arenaid).getDouble("arena.mini_games.fast_zone.game.finish_line.bounds.min.x") != 0
                                        || main.configManager.getArena(arenaid).getDouble("arena.mini_games.fast_zone.game.finish_line.bounds.min.y") != 0
                                        || main.configManager.getArena(arenaid).getDouble("arena.mini_games.fast_zone.game.finish_line.bounds.min.z") != 0
                                        || main.configManager.getArena(arenaid).getDouble("arena.mini_games.fast_zone.game.finish_line.bounds.max.x") != 0
                                        || main.configManager.getArena(arenaid).getDouble("arena.mini_games.fast_zone.game.finish_line.bounds.max.y") != 0
                                        || main.configManager.getArena(arenaid).getDouble("arena.mini_games.fast_zone.game.finish_line.bounds.max.z") != 0) {
                                    FastZoneSetupManager.finishSetup(player, arenaid, main);
                                }
                            }
                        } else if (main.setupManager.actualStepGame.get(player).equals(9)) {
                            if(selectedGame.get(player).equalsIgnoreCase("minefield")) {
                                if(main.configManager.getArena(arenaid).getDouble("arena.mini_games.minefield.game.floor.bounds.min.x") != 0
                                        || main.configManager.getArena(arenaid).getDouble("arena.mini_games.minefield.game.floor.bounds.min.y") != 0
                                        || main.configManager.getArena(arenaid).getDouble("arena.mini_games.minefield.game.floor.bounds.min.z") != 0
                                        || main.configManager.getArena(arenaid).getDouble("arena.mini_games.minefield.game.floor.bounds.max.x") != 0
                                        || main.configManager.getArena(arenaid).getDouble("arena.mini_games.minefield.game.floor.bounds.max.y") != 0
                                        || main.configManager.getArena(arenaid).getDouble("arena.mini_games.minefield.game.floor.bounds.max.z") != 0) {
                                    MinefieldSetupManager.finishSetup(player, arenaid, main);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void finish(Player player, Integer arenaid) {
        if(org.apache.commons.lang.StringUtils.isNumeric(String.valueOf(arenaid))) {
            if(ArenaManager.ArenaList.contains(arenaid)) {
                if (main.SetupProcess.containsKey(player) && main.SetupArena.containsKey(player)) {
                    if(main.configManager.getArena(arenaid).getInt("arena.basic.configured_games") >= 1) {
                        main.configManager.getArena(arenaid).set("arena.basic.state", "ENABLED");
                        main.configManager.saveArena(arenaid);
                        main.configManager.reloadArena(arenaid);

                        StringUtils.sendMessage(player, player.getName(), CacheManager.Language.GLOBAL_SUCCESS_SETUP_FINISHED);

                        player.getInventory().clear();
                        main.getServer().getScheduler().scheduleSyncDelayedTask(main, () -> {
                            main.SetupArena.remove(player);
                            main.SetupProcess.remove(player);
                            selectedGame.remove(player);
                            actualStepGame.remove(player);

                            try {
                                player.getInventory().setContents(InventoryUtil.getInventoryContent(main.configManager.getUser(player.getUniqueId()).getString("inventory")));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }, 10);
                    }
                }
            }
        }
    }

    public void previous(@NotNull Player player) {
        int arenaid = main.SetupArena.get(player);
        if(org.apache.commons.lang.StringUtils.isNumeric(String.valueOf(arenaid))) {
            if(ArenaManager.ArenaList.contains(arenaid)) {
                if(main.SetupProcess.containsKey(player) && main.SetupArena.containsKey(player)) {
                    if(main.configManager.getArena(arenaid).getString("arena.basic.setup_step").equals("1")) {
                        cancel(player);
                    } else if(main.configManager.getArena(arenaid).getString("arena.basic.setup_step").equals("2")) {
                        player.getInventory().clear();
                        firstStep(player, arenaid);
                    } else if(main.configManager.getArena(arenaid).getString("arena.basic.setup_step").equals("3")) {
                        player.getInventory().clear();
                        secondStep(player, arenaid);
                    } else if(main.configManager.getArena(arenaid).getString("arena.basic.setup_step").equals("4")) {
                        if(actualStepGame.get(player).equals(0)) {
                            player.getInventory().clear();
                            thirdStep(player, arenaid);
                        } else if(actualStepGame.get(player).equals(5)) {
                            player.getInventory().clear();
                            fourthStep(player, arenaid);
                        } else if(actualStepGame.get(player).equals(6)) {
                            player.getInventory().clear();
                            fifthStep(player, arenaid, selectedGame.get(player));
                        } else if(actualStepGame.get(player).equals(7)) {
                            player.getInventory().clear();
                            sixthStep(player, arenaid, selectedGame.get(player));
                        }
                    }
                }
            }
        }

    }

    public void maingame(Player player, Integer arenaid) {
        player.getInventory().clear();
        fourthStep(player, arenaid);
    }

    public static void setDefaultItems(Main main, Player player, Integer arenaid) {
        ItemStack cancel = new ItemStack(Material.valueOf(ObjectResolver.getBlock.WOOL("red")), 1, (short) 14);
        ItemMeta cancelmeta = cancel.getItemMeta();
        cancelmeta.setDisplayName(StringUtils.formatMessage(player.getName(), CacheManager.Language.ITEMS_SETUP_CANCEL_SETUP.replace("{arena_id}", String.valueOf(arenaid))));
        cancel.setItemMeta(cancelmeta);
        player.getInventory().setItem(5, cancel);

        ItemStack pause = new ItemStack(Material.valueOf(ObjectResolver.getBlock.WOOL("orange")), 1, (short) 1);
        ItemMeta pausemeta = pause.getItemMeta();
        pausemeta.setDisplayName(StringUtils.formatMessage(player.getName(), CacheManager.Language.ITEMS_SETUP_PAUSE_SETUP.replace("{arena_id}", String.valueOf(arenaid))));
        pause.setItemMeta(pausemeta);
        player.getInventory().setItem(6, pause);

        ItemStack previous = new ItemStack(Material.valueOf(ObjectResolver.getBlock.WOOL("yellow")), 1, (short) 4);
        ItemMeta previousmeta = previous.getItemMeta();
        previousmeta.setDisplayName(StringUtils.formatMessage(player.getName(), CacheManager.Language.ITEMS_SETUP_PREVIOUS_STEP.replace("{arena_id}", String.valueOf(arenaid))));
        previous.setItemMeta(previousmeta);
        player.getInventory().setItem(7, previous);

        ItemStack next = new ItemStack(Material.valueOf(ObjectResolver.getBlock.WOOL("lime")), 1, (short) 5);
        ItemMeta nextmeta = next.getItemMeta();
        nextmeta.setDisplayName(StringUtils.formatMessage(player.getName(), CacheManager.Language.ITEMS_SETUP_NEXT_STEP.replace("{arena_id}", String.valueOf(arenaid))));
        next.setItemMeta(nextmeta);
        player.getInventory().setItem(8, next);
    }

    private void firstStep(@NotNull Player player, Integer arenaid) {
        main.configManager.getArena(arenaid).set("arena.basic.setup_step", 1);
        main.configManager.saveArena(arenaid);
        main.configManager.reloadArena(arenaid);

        StringUtils.sendMessage(player, player.getName(), CacheManager.Language.GLOBAL_INFO_SETUP_STEP_1);

        ItemStack setlobby = new ItemStack(Material.BEACON, 1);
        ItemMeta setlobbymeta = setlobby.getItemMeta();
        setlobbymeta.addEnchant(Enchantment.DURABILITY, 1, true);
        setlobbymeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        setlobbymeta.setDisplayName(StringUtils.formatMessage(player.getName(), CacheManager.Language.ITEMS_SETUP_SET_WAITING_LOBBY.replace("{arena_id}", String.valueOf(arenaid))));
        setlobby.setItemMeta(setlobbymeta);
        player.getInventory().setItem(0, setlobby);

        ItemStack empty = new ItemStack(Material.valueOf(ObjectResolver.getItem.STAINED_GLASS_PANE("gray")), 1, (short) 7);
        ItemMeta emptymeta = empty.getItemMeta();
        emptymeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', " &7 "));
        empty.setItemMeta(emptymeta);
        player.getInventory().setItem(1, empty); // slot 2
        player.getInventory().setItem(2, empty); // slot 3
        player.getInventory().setItem(3, empty); // slot 4
        player.getInventory().setItem(4, empty);// slot 5

        setDefaultItems(main, player, arenaid);
    }

    private void secondStep(@NotNull Player player, Integer arenaid) {
        main.configManager.getArena(arenaid).set("arena.basic.setup_step", 2);
        main.configManager.saveArena(arenaid);
        main.configManager.reloadArena(arenaid);

        StringUtils.sendMessage(player, player.getName(), CacheManager.Language.GLOBAL_INFO_SETUP_STEP_2);

        ItemStack minplayers = new ItemStack(Material.SLIME_BALL, 6);
        ItemMeta minplayersmeta = minplayers.getItemMeta();
        minplayersmeta.addEnchant(Enchantment.DURABILITY, 1, true);
        minplayersmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        minplayersmeta.setDisplayName(StringUtils.formatMessage(player.getName(), CacheManager.Language.ITEMS_SETUP_MINIMUM_PLAYERS.replace("{arena_id}", String.valueOf(arenaid))));
        minplayers.setItemMeta(minplayersmeta);
        player.getInventory().setItem(0, minplayers);

        ItemStack maxplayers = new ItemStack(Material.REDSTONE, 12);
        ItemMeta maxplayersmeta = maxplayers.getItemMeta();
        maxplayersmeta.addEnchant(Enchantment.DURABILITY, 1, true);
        maxplayersmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        maxplayersmeta.setDisplayName(StringUtils.formatMessage(player.getName(), CacheManager.Language.ITEMS_SETUP_MAXIMUM_PLAYERS.replace("{arena_id}", String.valueOf(arenaid))));
        maxplayers.setItemMeta(maxplayersmeta);
        player.getInventory().setItem(1, maxplayers);

        ItemStack empty = new ItemStack(Material.valueOf(ObjectResolver.getItem.STAINED_GLASS_PANE("gray")), 1, (short) 7);
        ItemMeta emptymeta = empty.getItemMeta();
        emptymeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', " &7 "));
        empty.setItemMeta(emptymeta);
        player.getInventory().setItem(2, empty); // slot 3
        player.getInventory().setItem(3, empty); // slot 4
        player.getInventory().setItem(4, empty);// slot 5


        setDefaultItems(main, player, arenaid);
    }

    private void thirdStep(@NotNull Player player, Integer arenaid) {
        main.configManager.getArena(arenaid).set("arena.basic.setup_step", 3);
        main.configManager.saveArena(arenaid);
        main.configManager.reloadArena(arenaid);

        StringUtils.sendMessage(player, player.getName(), CacheManager.Language.GLOBAL_INFO_SETUP_STEP_3);

        ItemStack minplayers = new ItemStack(Material.NAME_TAG, 1);
        ItemMeta minplayersmeta = minplayers.getItemMeta();
        minplayersmeta.addEnchant(Enchantment.DURABILITY, 1, true);
        minplayersmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        minplayersmeta.setDisplayName(StringUtils.formatMessage(player.getName(), CacheManager.Language.ITEMS_SETUP_DISPLAY_NAME.replace("{arena_id}", String.valueOf(arenaid))));
        minplayers.setItemMeta(minplayersmeta);
        player.getInventory().setItem(0, minplayers);

        ItemStack empty = new ItemStack(Material.valueOf(ObjectResolver.getItem.STAINED_GLASS_PANE("gray")), 1, (short) 7);
        ItemMeta emptymeta = empty.getItemMeta();
        emptymeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', " &7 "));
        empty.setItemMeta(emptymeta);
        player.getInventory().setItem(1, empty);
        player.getInventory().setItem(2, empty); // slot 3
        player.getInventory().setItem(3, empty); // slot 4
        player.getInventory().setItem(4, empty);// slot 5


        setDefaultItems(main, player, arenaid);
    }

    private void fourthStep(@NotNull Player player, Integer arenaid) {
        main.configManager.getArena(arenaid).set("arena.basic.setup_step", 4);
        main.configManager.saveArena(arenaid);
        main.configManager.reloadArena(arenaid);

        actualStepGame.replace(player, 0);

        StringUtils.sendMessage(player, player.getName(), CacheManager.Language.GLOBAL_INFO_SETUP_STEP_4);

        ItemStack minplayers = new ItemStack(Material.NETHER_STAR, 1);
        ItemMeta minplayersmeta = minplayers.getItemMeta();
        minplayersmeta.addEnchant(Enchantment.DURABILITY, 1, true);
        minplayersmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        minplayersmeta.setDisplayName(StringUtils.formatMessage(player.getName(), CacheManager.Language.ITEMS_SETUP_SELECT_MINI_GAME.replace("{arena_id}", String.valueOf(arenaid))));
        minplayers.setItemMeta(minplayersmeta);
        player.getInventory().setItem(0, minplayers);

        ItemStack empty = new ItemStack(Material.valueOf(ObjectResolver.getItem.STAINED_GLASS_PANE("gray")), 1, (short) 7);
        ItemMeta emptymeta = empty.getItemMeta();
        emptymeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', " &7 "));
        empty.setItemMeta(emptymeta);
        player.getInventory().setItem(1, empty);
        player.getInventory().setItem(2, empty); // slot 3
        player.getInventory().setItem(3, empty); // slot 4
        player.getInventory().setItem(4, empty);// slot 5


        setDefaultItems(main, player, arenaid);
    }

    private void fifthStep(@NotNull Player player, Integer arenaid, String minigame) {
        actualStepGame.replace(player, 5);

        StringUtils.sendMessage(player, player.getName(), CacheManager.Language.GLOBAL_INFO_SETUP_STEP_5);

        ItemStack stickbounds = new ItemStack(Material.STICK, 1);
        ItemMeta stickboundsmeta = stickbounds.getItemMeta();
        stickboundsmeta.addEnchant(Enchantment.DURABILITY, 1, true);
        stickboundsmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        stickboundsmeta.setDisplayName(StringUtils.formatMessage(player.getName(), CacheManager.Language.ITEMS_SETUP_MAGIC_STICK.replace("{arena_id}", String.valueOf(arenaid))));
        stickbounds.setItemMeta(stickboundsmeta);
        player.getInventory().setItem(0, stickbounds);

        ItemStack empty = new ItemStack(Material.valueOf(ObjectResolver.getItem.STAINED_GLASS_PANE("gray")), 1, (short) 7);
        ItemMeta emptymeta = empty.getItemMeta();
        emptymeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', " &7 "));
        empty.setItemMeta(emptymeta);
        player.getInventory().setItem(1, empty);
        player.getInventory().setItem(2, empty); // slot 3
        player.getInventory().setItem(3, empty); // slot 4
        player.getInventory().setItem(4, empty);// slot 5

        setDefaultItems(main, player, arenaid);
    }

    private void sixthStep(Player player, Integer arenaid, String minigame) {
        actualStepGame.replace(player, 6);

        StringUtils.sendMessage(player, player.getName(), CacheManager.Language.GLOBAL_INFO_SETUP_STEP_6);

        ItemStack spawnpoints = new ItemStack(Material.BEACON, 1);
        ItemMeta spawnpointsmeta = spawnpoints.getItemMeta();
        spawnpointsmeta.addEnchant(Enchantment.DURABILITY, 1, true);
        spawnpointsmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        spawnpointsmeta.setDisplayName(StringUtils.formatMessage(player.getName(), CacheManager.Language.ITEMS_SETUP_SPAWNING_POINTS.replace("{arena_id}", String.valueOf(arenaid))));
        spawnpoints.setItemMeta(spawnpointsmeta);
        player.getInventory().setItem(0, spawnpoints);

        ItemStack empty = new ItemStack(Material.valueOf(ObjectResolver.getItem.STAINED_GLASS_PANE("gray")), 1, (short) 7);
        ItemMeta emptymeta = empty.getItemMeta();
        emptymeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', " &7 "));
        empty.setItemMeta(emptymeta);
        player.getInventory().setItem(1, empty); // slot 2
        player.getInventory().setItem(2, empty); // slot 3
        player.getInventory().setItem(3, empty); // slot 4
        player.getInventory().setItem(4, empty);// slot 5

        setDefaultItems(main, player, arenaid);
    }

    private void seventhStep(Player player, Integer arenaid, String minigame) {
        actualStepGame.replace(player, 7);

        StringUtils.sendMessage(player, player.getName(), CacheManager.Language.GLOBAL_INFO_SETUP_STEP_7);

        ItemStack time = new ItemStack(Material.valueOf(ObjectResolver.getItem.CLOCK()), 1);
        ItemMeta timemeta = time.getItemMeta();
        timemeta.addEnchant(Enchantment.DURABILITY, 1, true);
        timemeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        timemeta.setDisplayName(StringUtils.formatMessage(player.getName(), CacheManager.Language.ITEMS_SETUP_SET_TIME.replace("{arena_id}", String.valueOf(arenaid))));
        time.setItemMeta(timemeta);
        player.getInventory().setItem(0, time);

        ItemStack empty = new ItemStack(Material.valueOf(ObjectResolver.getItem.STAINED_GLASS_PANE("gray")), 1, (short) 7);
        ItemMeta emptymeta = empty.getItemMeta();
        emptymeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', " &7 "));
        empty.setItemMeta(emptymeta);
        player.getInventory().setItem(1, empty); // slot 2
        player.getInventory().setItem(2, empty); // slot 3
        player.getInventory().setItem(3, empty); // slot 4
        player.getInventory().setItem(4, empty);// slot 5

        setDefaultItems(main, player, arenaid);

        main.configManager.getArena(arenaid).set("arena.mini_games."+main.setupManager.selectedGame.get(player)+".basic.time", 60);
        main.configManager.saveArena(arenaid);
        main.configManager.reloadArena(arenaid);
    }
}
