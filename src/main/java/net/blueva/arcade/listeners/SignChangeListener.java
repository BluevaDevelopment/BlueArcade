package net.blueva.arcade.listeners;

import net.blueva.arcade.Main;
import net.blueva.arcade.managers.ArenaManager;
import net.blueva.arcade.managers.CacheManager;
import net.blueva.arcade.utils.StringUtils;
import net.blueva.arcade.utils.SignsUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignChangeListener implements Listener {

    private final Main main;

    public SignChangeListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void SCL(SignChangeEvent event) {
        Player player = event.getPlayer();

        if(main.signManager.isRegisteredSign(event.getBlock().getLocation())) {
            event.setCancelled(true);
            Bukkit.getConsoleSender().sendMessage("registrado");
        }
        
        if(player.hasPermission("bluearcade.admin")) {
            if(event.getLine(0).equalsIgnoreCase("[BlueArcade]")) {
                if(event.getLine(1).equalsIgnoreCase("join")) {
                    if(org.apache.commons.lang.StringUtils.isNumeric(event.getLine(2))) {
                        Integer arenaid = Integer.parseInt(event.getLine(2));
                        if(ArenaManager.ArenaList.contains(Integer.valueOf(event.getLine(2)))) {
                            event.setLine(0, SignsUtil.format(CacheManager.Language.SIGNS_ARENA_LINE1, arenaid));
                            event.setLine(1, SignsUtil.format(CacheManager.Language.SIGNS_ARENA_LINE2, arenaid));
                            event.setLine(2, SignsUtil.format(CacheManager.Language.SIGNS_ARENA_LINE3, arenaid));
                            event.setLine(3, SignsUtil.format(CacheManager.Language.SIGNS_ARENA_LINE4, arenaid));

                            Integer totalSigns = main.configManager.getSigns().getInt("signs.total") + 1;
                            main.configManager.getSigns().set("signs.list.s" + totalSigns + ".info.type", "join");
                            main.configManager.saveSigns();
                            main.configManager.getSigns().set("signs.list.s" + totalSigns + ".info.arenaid", arenaid);
                            main.configManager.saveSigns();
                            main.configManager.getSigns().set("signs.list.s" + totalSigns + ".cords.world", event.getBlock().getLocation().getWorld().getName());
                            main.configManager.saveSigns();
                            main.configManager.getSigns().set("signs.list.s" + totalSigns + ".cords.x", event.getBlock().getLocation().getX());
                            main.configManager.saveSigns();
                            main.configManager.getSigns().set("signs.list.s" + totalSigns + ".cords.y", event.getBlock().getLocation().getY());
                            main.configManager.saveSigns();
                            main.configManager.getSigns().set("signs.list.s" + totalSigns + ".cords.z", event.getBlock().getLocation().getZ());
                            main.configManager.saveSigns();

                            main.configManager.getSigns().set("signs.total", totalSigns);
                            main.configManager.saveSigns();

                            main.configManager.reloadSigns();

                            StringUtils.sendMessage(player, player.getName(), CacheManager.Language.GLOBAL_SUCCESS_SIGN_ADDED
                                    .replace("{s_type}", "join")
                                    .replace("{s_x}", String.valueOf(event.getBlock().getLocation().getX()))
                                    .replace("{s_y}", String.valueOf(event.getBlock().getLocation().getY()))
                                    .replace("{s_z}", String.valueOf(event.getBlock().getLocation().getZ())));
                        } else {
                            event.getBlock().setType(Material.AIR);
                        }
                    } else {
                        event.getBlock().setType(Material.AIR);
                        event.setCancelled(true);
                        StringUtils.sendMessage(player, player.getName(), CacheManager.Language.GLOBAL_ERROR_ERROR_SIGN);
                    }
                } else if(event.getLine(1).equalsIgnoreCase("quickjoin")) {
                    if(org.apache.commons.lang.StringUtils.isNumeric(event.getLine(2))) {
                        event.setLine(0, SignsUtil.format(CacheManager.Language.SIGNS_QUICKJOIN_LINE1, null));
                        event.setLine(1, SignsUtil.format(CacheManager.Language.SIGNS_QUICKJOIN_LINE2, null));
                        event.setLine(2, SignsUtil.format(CacheManager.Language.SIGNS_QUICKJOIN_LINE3, null));
                        event.setLine(3, SignsUtil.format(CacheManager.Language.SIGNS_QUICKJOIN_LINE4, null));

                        Integer totalSigns = main.configManager.getSigns().getInt("signs.total")+1;
                        main.configManager.getSigns().set("signs.list.s"+totalSigns+".info.type", "quickjoin");
                        main.configManager.saveSigns();
                        main.configManager.getSigns().set("signs.list.s"+totalSigns+".cords.world", event.getBlock().getLocation().getWorld().getName());
                        main.configManager.saveSigns();
                        main.configManager.getSigns().set("signs.list.s"+totalSigns+".cords.x", event.getBlock().getLocation().getX());
                        main.configManager.saveSigns();
                        main.configManager.getSigns().set("signs.list.s"+totalSigns+".cords.y", event.getBlock().getLocation().getY());
                        main.configManager.saveSigns();
                        main.configManager.getSigns().set("signs.list.s"+totalSigns+".cords.z", event.getBlock().getLocation().getZ());
                        main.configManager.saveSigns();

                        main.configManager.getSigns().set("signs.total", totalSigns);
                        main.configManager.saveSigns();

                        main.configManager.reloadSigns();

                        StringUtils.sendMessage(player, player.getName(), CacheManager.Language.GLOBAL_SUCCESS_SIGN_ADDED
                                .replace("{s_type}", "quickjoin")
                                .replace("{s_x}", String.valueOf(event.getBlock().getLocation().getX()))
                                .replace("{s_y}", String.valueOf(event.getBlock().getLocation().getY()))
                                .replace("{s_z}", String.valueOf(event.getBlock().getLocation().getZ())));
                    }
                } else {
                    event.getBlock().setType(Material.AIR);
                    event.setCancelled(true);
                    StringUtils.sendMessage(player, player.getName(), CacheManager.Language.GLOBAL_ERROR_ERROR_SIGN);
                }
            }
        }
    }
 }
