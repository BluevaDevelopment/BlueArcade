package net.blueva.arcade.listeners;

import net.blueva.arcade.managers.CacheManager;
import net.blueva.arcade.managers.PlayerManager;
import net.blueva.arcade.utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import net.blueva.arcade.Main;

public class BlockPlaceListener implements Listener {
    private Main main;

    public BlockPlaceListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void BPL(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        if(PlayerManager.PlayerStatus.containsKey(p)) {
            if(PlayerManager.PlayerStatus.get(p).equals("Playing")) {
                event.setCancelled(true);
            }
        }
        if(main.SetupProcess.containsKey(p) && main.SetupProcess.get(p).equals(true) && main.SetupArena.containsKey(p)) {
            Integer arenaid = main.SetupArena.get(p);
            event.setCancelled(true);
            if(main.configManager.getArena(main.SetupArena.get(p)).getInt("arena.basic.setup_step") == 1) {
                if(event.getPlayer().getInventory().getHeldItemSlot() == 0) {
                    Location l = event.getBlock().getLocation();
                    String world = l.getWorld().getName();
                    double x = l.getX() + 0.5;
                    double y = l.getY();
                    double z = l.getZ() + 0.5;
                    float yaw = event.getPlayer().getLocation().getYaw();
                    float pitch = 0;

                    main.configManager.getArena(arenaid).set("arena.waiting_lobby.world", world);
                    main.configManager.saveArena(arenaid);
                    main.configManager.getArena(arenaid).set("arena.waiting_lobby.x", x);
                    main.configManager.saveArena(arenaid);
                    main.configManager.getArena(arenaid).set("arena.waiting_lobby.y", y);
                    main.configManager.saveArena(arenaid);
                    main.configManager.getArena(arenaid).set("arena.waiting_lobby.z", z);
                    main.configManager.saveArena(arenaid);
                    main.configManager.getArena(arenaid).set("arena.waiting_lobby.yaw", yaw);
                    main.configManager.saveArena(arenaid);
                    main.configManager.getArena(arenaid).set("arena.waiting_lobby.pitch", pitch);
                    main.configManager.saveArena(arenaid);

                    main.configManager.reloadArena(arenaid);

                    StringUtils.sendMessage(p, p.getName(), CacheManager.Language.GLOBAL_SUCCESS_WAITING_LOBBY_SET
                            .replace("{arena_id}", String.valueOf(arenaid))
                            .replace("{wl_world}", world)
                            .replace("{wl_x}", String.valueOf(x))
                            .replace("{wl_y}", String.valueOf(y))
                            .replace("{wl_z}", String.valueOf(z)));
                }
            } else if(main.configManager.getArena(main.SetupArena.get(p)).getInt("arena.basic.setup_step") == 4) {
                if(main.setupManager.selectedGame.containsKey(p)) {
                    if(main.setupManager.actualStepGame.get(p) == 6) {
                        Location l = event.getBlock().getLocation();
                        String world = l.getWorld().getName();
                        double x = l.getX() + 0.5;
                        double y = l.getY();
                        double z = l.getZ() + 0.5;
                        float yaw = event.getPlayer().getLocation().getYaw();
                        float pitch = 0;
                        if(event.getPlayer().getInventory().getHeldItemSlot() == 0) {
                            Integer spawn = main.configManager.getArena(arenaid).getInt("arena.mini_games."+main.setupManager.selectedGame.get(p)+".spawns.total")+1;
                            main.configManager.getArena(arenaid).set("arena.mini_games."+main.setupManager.selectedGame.get(p)+".spawns.total", spawn);
                            main.configManager.saveArena(arenaid);
                            main.configManager.getArena(arenaid).set("arena.mini_games."+main.setupManager.selectedGame.get(p)+".spawns.list.s"+spawn+".x", x);
                            main.configManager.saveArena(arenaid);
                            main.configManager.getArena(arenaid).set("arena.mini_games."+main.setupManager.selectedGame.get(p)+".spawns.list.s"+spawn+".y", y);
                            main.configManager.saveArena(arenaid);
                            main.configManager.getArena(arenaid).set("arena.mini_games."+main.setupManager.selectedGame.get(p)+".spawns.list.s"+spawn+".z", z);
                            main.configManager.saveArena(arenaid);
                            main.configManager.getArena(arenaid).set("arena.mini_games."+main.setupManager.selectedGame.get(p)+".spawns.list.s"+spawn+".yaw", yaw);
                            main.configManager.saveArena(arenaid);
                            main.configManager.getArena(arenaid).set("arena.mini_games."+main.setupManager.selectedGame.get(p)+".spawns.list.s"+spawn+".pitch", pitch);
                            main.configManager.saveArena(arenaid);
                            main.configManager.getArena(arenaid).set("arena.mini_games."+main.setupManager.selectedGame.get(p)+".basic.world", world);
                            main.configManager.saveArena(arenaid);

                            main.configManager.reloadArena(arenaid);

                            StringUtils.sendMessage(p, p.getName(), CacheManager.Language.GLOBAL_SUCCESS_SPAWN_ADDED
                                    .replace("{spawn}", String.valueOf(spawn))
                                    .replace("{game}", main.setupManager.selectedGame.get(p))
                                    .replace("{arena_id}", String.valueOf(arenaid)));
                        }
                    }
                }
            }
        }
    }
}
