package net.blueva.arcade.listeners;

import net.blueva.arcade.managers.ArenaManager;
import net.blueva.arcade.managers.PlayerManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import net.blueva.arcade.Main;
import org.bukkit.inventory.ItemStack;

public class BlockBreakListener implements Listener {
    private final Main main;

    public BlockBreakListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void BBL(BlockBreakEvent event) {
        Player p = event.getPlayer();

        if(p.hasPermission("bluearcade.admin")) {
            if(event.getBlock().getType() == Material.OAK_SIGN ||
                    event.getBlock().getType() == Material.OAK_WALL_SIGN) {
                main.signManager.removeRegisteredSign(p, event.getBlock().getLocation());
            }
        }

        if(PlayerManager.PlayerStatus.containsKey(p)) {
            if(PlayerManager.PlayerStatus.get(p).equalsIgnoreCase("Playing")) {
                if(ArenaManager.ArenaActualGame.get(PlayerManager.PlayerArena.get(p)).equalsIgnoreCase("Spleef")) {
                    ItemStack snowballStack = new ItemStack(Material.SNOWBALL);
                    p.getInventory().addItem(snowballStack);
                    if(event.getBlock().getType().equals(Material.SNOW_BLOCK)) {
                        Block block = event.getBlock();
                        block.setType(Material.AIR);
                    }
                }

                event.setCancelled(true);
            }
        }

        if(main.SetupProcess.containsKey(p) && main.SetupProcess.get(p).equals(true)) {
            event.setCancelled(true);
        }
    }
}
