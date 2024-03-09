package net.blueva.arcade.listeners;

import net.blueva.arcade.managers.CacheManager;
import net.blueva.arcade.managers.PlayerManager;
import net.blueva.arcade.utils.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import net.blueva.arcade.Main;

public class InventoryClickListener implements Listener {
    private final Main main;

    public InventoryClickListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void PICL(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        if(PlayerManager.PlayerStatus.containsKey(p)) {
            if(PlayerManager.PlayerStatus.get(p).equals("Playing")) {
                if((event.getInventory().getType().equals(InventoryType.PLAYER) || event.getInventory().getType().equals(InventoryType.CRAFTING))) {
                    event.getSlotType();
                    if (event.getCurrentItem() != null) {
                        if (event.getCurrentItem().getType().equals(Material.AIR)) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }

        if(main.SetupProcess.containsKey(p) && main.SetupProcess.get(p).equals(true)) {
            event.setCancelled(true);

            //check setup minigames gui
            String smgname = StringUtils.formatMessage(p.getName(), CacheManager.Language.GUI_SETUP_SELECT_GAME_TITLE);
            String smgnameM = ChatColor.stripColor(smgname);

            if(ChatColor.stripColor(event.getView().getTitle()).equals(smgnameM)) {
                if(event.getSlot() == 10) {
                    main.setupManager.selectedGame.remove(p);
                    main.setupManager.selectedGame.put(p, "race");

                    if(main.configManager.getArena(main.SetupArena.get(p)).getInt("arena.basic.configured_games") != 0) {
                        Integer cg = main.configManager.getArena(main.SetupArena.get(p)).getInt("arena.basic.configured_games")-1;
                        main.configManager.getArena(main.SetupArena.get(p)).set("arena.basic.configured_games", cg);
                        main.configManager.saveArena(main.SetupArena.get(p));
                    }
                    main.configManager.getArena(main.SetupArena.get(p)).set("arena.mini_games.race.basic.enabled", false);
                    main.configManager.saveArena(main.SetupArena.get(p));
                    main.configManager.reloadArena(main.SetupArena.get(p));

                    StringUtils.sendMessage(p, p.getName(), CacheManager.Language.GLOBAL_INFO_SELECTED_MINI_GAME_SETUP.replace("{mini_game}", "Race"));
                    p.closeInventory();

                    main.setupManager.next(p);
                } else if(event.getSlot() == 11) {
                    main.setupManager.selectedGame.remove(p);
                    main.setupManager.selectedGame.put(p, "spleef");

                    if(main.configManager.getArena(main.SetupArena.get(p)).getInt("arena.basic.configured_games") != 0) {
                        Integer cg = main.configManager.getArena(main.SetupArena.get(p)).getInt("arena.basic.configured_games")-1;
                        main.configManager.getArena(main.SetupArena.get(p)).set("arena.basic.configured_games", cg);
                        main.configManager.saveArena(main.SetupArena.get(p));
                    }
                    main.configManager.getArena(main.SetupArena.get(p)).set("arena.mini_games.spleef.basic.enabled", false);
                    main.configManager.saveArena(main.SetupArena.get(p));
                    main.configManager.reloadArena(main.SetupArena.get(p));

                    StringUtils.sendMessage(p, p.getName(), CacheManager.Language.GLOBAL_INFO_SELECTED_MINI_GAME_SETUP.replace("{mini_game}", "Spleef"));
                    p.closeInventory();

                    main.setupManager.next(p);
                } else if(event.getSlot() == 12) {
                    main.setupManager.selectedGame.remove(p);
                    main.setupManager.selectedGame.put(p, "snowball_fight");

                    if(main.configManager.getArena(main.SetupArena.get(p)).getInt("arena.basic.configured_games") != 0) {
                        Integer cg = main.configManager.getArena(main.SetupArena.get(p)).getInt("arena.basic.configured_games")-1;
                        main.configManager.getArena(main.SetupArena.get(p)).set("arena.basic.configured_games", cg);
                        main.configManager.saveArena(main.SetupArena.get(p));
                    }
                    main.configManager.getArena(main.SetupArena.get(p)).set("arena.mini_games.snowball_fight.basic.enabled", false);
                    main.configManager.saveArena(main.SetupArena.get(p));
                    main.configManager.reloadArena(main.SetupArena.get(p));

                    StringUtils.sendMessage(p, p.getName(), CacheManager.Language.GLOBAL_INFO_SELECTED_MINI_GAME_SETUP.replace("{mini_game}", "Snowball Fight"));
                    p.closeInventory();

                    main.setupManager.next(p);
                } else if(event.getSlot() == 13) {
                    main.setupManager.selectedGame.remove(p);
                    main.setupManager.selectedGame.put(p, "all_against_all");

                    if(main.configManager.getArena(main.SetupArena.get(p)).getInt("arena.basic.configured_games") != 0) {
                        Integer cg = main.configManager.getArena(main.SetupArena.get(p)).getInt("arena.basic.configured_games")-1;
                        main.configManager.getArena(main.SetupArena.get(p)).set("arena.basic.configured_games", cg);
                        main.configManager.saveArena(main.SetupArena.get(p));
                    }
                    main.configManager.getArena(main.SetupArena.get(p)).set("arena.mini_games.all_against_all.basic.enabled", false);
                    main.configManager.saveArena(main.SetupArena.get(p));
                    main.configManager.reloadArena(main.SetupArena.get(p));

                    StringUtils.sendMessage(p, p.getName(), CacheManager.Language.GLOBAL_INFO_SELECTED_MINI_GAME_SETUP.replace("{mini_game}", "All Against All"));
                    p.closeInventory();

                    main.setupManager.next(p);
                } else if(event.getSlot() == 14) {
                    main.setupManager.selectedGame.remove(p);
                    main.setupManager.selectedGame.put(p, "one_in_the_chamber");

                    if(main.configManager.getArena(main.SetupArena.get(p)).getInt("arena.basic.configured_games") != 0) {
                        Integer cg = main.configManager.getArena(main.SetupArena.get(p)).getInt("arena.basic.configured_games")-1;
                        main.configManager.getArena(main.SetupArena.get(p)).set("arena.basic.configured_games", cg);
                        main.configManager.saveArena(main.SetupArena.get(p));
                    }
                    main.configManager.getArena(main.SetupArena.get(p)).set("arena.mini_games.one_in_the_chamber.basic.enabled", false);
                    main.configManager.saveArena(main.SetupArena.get(p));
                    main.configManager.reloadArena(main.SetupArena.get(p));

                    StringUtils.sendMessage(p, p.getName(), CacheManager.Language.GLOBAL_INFO_SELECTED_MINI_GAME_SETUP.replace("{mini_game}", "One In The Chamber"));
                    p.closeInventory();

                    main.setupManager.next(p);
                } else if(event.getSlot() == 15) {
                    main.setupManager.selectedGame.remove(p);
                    main.setupManager.selectedGame.put(p, "traffic_light");

                    if(main.configManager.getArena(main.SetupArena.get(p)).getInt("arena.basic.configured_games") != 0) {
                        Integer cg = main.configManager.getArena(main.SetupArena.get(p)).getInt("arena.basic.configured_games")-1;
                        main.configManager.getArena(main.SetupArena.get(p)).set("arena.basic.configured_games", cg);
                        main.configManager.saveArena(main.SetupArena.get(p));
                    }
                    main.configManager.getArena(main.SetupArena.get(p)).set("arena.mini_games.traffic_light.basic.enabled", false);
                    main.configManager.saveArena(main.SetupArena.get(p));
                    main.configManager.reloadArena(main.SetupArena.get(p));

                    StringUtils.sendMessage(p, p.getName(), CacheManager.Language.GLOBAL_INFO_SELECTED_MINI_GAME_SETUP.replace("{mini_game}", "Traffic Light"));
                    p.closeInventory();

                    main.setupManager.next(p);
                } else if(event.getSlot() == 16) {
                    main.setupManager.selectedGame.remove(p);
                    main.setupManager.selectedGame.put(p, "minefield");

                    if(main.configManager.getArena(main.SetupArena.get(p)).getInt("arena.basic.configured_games") != 0) {
                        Integer cg = main.configManager.getArena(main.SetupArena.get(p)).getInt("arena.basic.configured_games")-1;
                        main.configManager.getArena(main.SetupArena.get(p)).set("arena.basic.configured_games", cg);
                        main.configManager.saveArena(main.SetupArena.get(p));
                    }
                    main.configManager.getArena(main.SetupArena.get(p)).set("arena.mini_games.minefield.basic.enabled", false);
                    main.configManager.saveArena(main.SetupArena.get(p));
                    main.configManager.reloadArena(main.SetupArena.get(p));

                    StringUtils.sendMessage(p, p.getName(), CacheManager.Language.GLOBAL_INFO_SELECTED_MINI_GAME_SETUP.replace("{mini_game}", "Minefield"));
                    p.closeInventory();

                    main.setupManager.next(p);
                } else if(event.getSlot() == 19) {
                    main.setupManager.selectedGame.remove(p);
                    main.setupManager.selectedGame.put(p, "exploding_sheep");

                    if(main.configManager.getArena(main.SetupArena.get(p)).getInt("arena.basic.configured_games") != 0) {
                        Integer cg = main.configManager.getArena(main.SetupArena.get(p)).getInt("arena.basic.configured_games")-1;
                        main.configManager.getArena(main.SetupArena.get(p)).set("arena.basic.configured_games", cg);
                        main.configManager.saveArena(main.SetupArena.get(p));
                    }
                    main.configManager.getArena(main.SetupArena.get(p)).set("arena.mini_games.exploding_sheep.basic.enabled", false);
                    main.configManager.saveArena(main.SetupArena.get(p));
                    main.configManager.reloadArena(main.SetupArena.get(p));

                    StringUtils.sendMessage(p, p.getName(),CacheManager.Language.GLOBAL_INFO_SELECTED_MINI_GAME_SETUP.replace("{mini_game}", "Exploding Sheep"));
                    p.closeInventory();

                    main.setupManager.next(p);
                } else if(event.getSlot() == 20) {
                    main.setupManager.selectedGame.remove(p);
                    main.setupManager.selectedGame.put(p, "tnt_tag");

                    if(main.configManager.getArena(main.SetupArena.get(p)).getInt("arena.basic.configured_games") != 0) {
                        Integer cg = main.configManager.getArena(main.SetupArena.get(p)).getInt("arena.basic.configured_games")-1;
                        main.configManager.getArena(main.SetupArena.get(p)).set("arena.basic.configured_games", cg);
                        main.configManager.saveArena(main.SetupArena.get(p));
                    }
                    main.configManager.getArena(main.SetupArena.get(p)).set("arena.mini_games.tnt_tag.basic.enabled", false);
                    main.configManager.saveArena(main.SetupArena.get(p));
                    main.configManager.reloadArena(main.SetupArena.get(p));

                    StringUtils.sendMessage(p, p.getName(),CacheManager.Language.GLOBAL_INFO_SELECTED_MINI_GAME_SETUP.replace("{mini_game}", "TNT Tag"));
                    p.closeInventory();

                    main.setupManager.next(p);
                } else if(event.getSlot() == 21) {
                    main.setupManager.selectedGame.remove(p);
                    main.setupManager.selectedGame.put(p, "red_alert");

                    if(main.configManager.getArena(main.SetupArena.get(p)).getInt("arena.basic.configured_games") != 0) {
                        Integer cg = main.configManager.getArena(main.SetupArena.get(p)).getInt("arena.basic.configured_games")-1;
                        main.configManager.getArena(main.SetupArena.get(p)).set("arena.basic.configured_games", cg);
                        main.configManager.saveArena(main.SetupArena.get(p));
                    }
                    main.configManager.getArena(main.SetupArena.get(p)).set("arena.mini_games.red_alert.basic.enabled", false);
                    main.configManager.saveArena(main.SetupArena.get(p));
                    main.configManager.reloadArena(main.SetupArena.get(p));

                    StringUtils.sendMessage(p, p.getName(),CacheManager.Language.GLOBAL_INFO_SELECTED_MINI_GAME_SETUP.replace("{mini_game}", "Red Alert"));
                    p.closeInventory();

                    main.setupManager.next(p);
                } else if(event.getSlot() == 22) {
                    main.setupManager.selectedGame.remove(p);
                    main.setupManager.selectedGame.put(p, "knock_back");

                    if(main.configManager.getArena(main.SetupArena.get(p)).getInt("arena.basic.configured_games") != 0) {
                        Integer cg = main.configManager.getArena(main.SetupArena.get(p)).getInt("arena.basic.configured_games")-1;
                        main.configManager.getArena(main.SetupArena.get(p)).set("arena.basic.configured_games", cg);
                        main.configManager.saveArena(main.SetupArena.get(p));
                    }
                    main.configManager.getArena(main.SetupArena.get(p)).set("arena.mini_games.knock_back.basic.enabled", false);
                    main.configManager.saveArena(main.SetupArena.get(p));
                    main.configManager.reloadArena(main.SetupArena.get(p));

                    StringUtils.sendMessage(p, p.getName(),CacheManager.Language.GLOBAL_INFO_SELECTED_MINI_GAME_SETUP.replace("{mini_game}", "Knock Back"));
                    p.closeInventory();

                    main.setupManager.next(p);
                } else if(event.getSlot() == 23) {
                    main.setupManager.selectedGame.remove(p);
                    main.setupManager.selectedGame.put(p, "fast_zone");

                    if(main.configManager.getArena(main.SetupArena.get(p)).getInt("arena.basic.configured_games") != 0) {
                        Integer cg = main.configManager.getArena(main.SetupArena.get(p)).getInt("arena.basic.configured_games")-1;
                        main.configManager.getArena(main.SetupArena.get(p)).set("arena.basic.configured_games", cg);
                        main.configManager.saveArena(main.SetupArena.get(p));
                    }
                    main.configManager.getArena(main.SetupArena.get(p)).set("arena.mini_games.fast_zone.basic.enabled", false);
                    main.configManager.saveArena(main.SetupArena.get(p));
                    main.configManager.reloadArena(main.SetupArena.get(p));

                    StringUtils.sendMessage(p, p.getName(),CacheManager.Language.GLOBAL_INFO_SELECTED_MINI_GAME_SETUP.replace("{mini_game}", "Fast Zone"));
                    p.closeInventory();

                    main.setupManager.next(p);
                }

                if(event.getSlot() == 49) {
                    p.closeInventory();
                }

                // 45, 46, 47, 48, 50, 41, 52, 53

                if(event.getSlot() == 45 ||
                        event.getSlot() == 46 ||
                        event.getSlot() == 47 ||
                        event.getSlot() == 48 ||
                        event.getSlot() == 50 ||
                        event.getSlot() == 41 ||
                        event.getSlot() == 52 ||
                        event.getSlot() == 53) {
                    main.setupManager.selectedGame.remove(p);
                    StringUtils.sendMessage(p, p.getName(), CacheManager.Language.GLOBAL_INFO_SELECTION_ELIMINATED_SETUP);
                    p.closeInventory();
                }
            }
        }
    }
}
