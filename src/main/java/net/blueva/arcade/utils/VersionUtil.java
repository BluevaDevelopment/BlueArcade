package net.blueva.arcade.utils;

import net.blueva.arcade.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class VersionUtil {
    public static String getHandItemName(Player p) {
        ItemStack item;
        if (Main.getPlugin().bukkitVersion.startsWith("1.8")) {
            item = p.getItemInHand();
        } else {
            item = p.getInventory().getItemInMainHand();
        }

        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            return item.getItemMeta().getDisplayName();
        }
        return "";
    }

    public static Material getHandItemType(Player p) {
        if(Main.getPlugin().bukkitVersion.startsWith("1.8")) {
            return p.getItemInHand().getType();
        }
        return p.getInventory().getItemInMainHand().getType();
    }

    public static Integer getQuantityItemsMainHand(Player player) {
        ItemStack itemInMainHand = null;

        if (Main.getPlugin().bukkitVersion.startsWith("1.8")) {
            itemInMainHand = player.getItemInHand();
        } else {
            itemInMainHand = player.getInventory().getItemInMainHand();
        }

        if (itemInMainHand == null) {
            return 0;
        }

        return itemInMainHand.getAmount();
    }

}
