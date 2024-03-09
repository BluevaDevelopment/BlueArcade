package net.blueva.arcade.utils;

import net.blueva.arcade.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public final class VersionUtil {
    public static String getHandItemName(Player p) {
        ItemStack item;
        item = p.getInventory().getItemInMainHand();

        if (item.hasItemMeta() && Objects.requireNonNull(item.getItemMeta()).hasDisplayName()) {
            return item.getItemMeta().getDisplayName();
        }
        return "";
    }

    public static Material getHandItemType(Player p) {
        return p.getInventory().getItemInMainHand().getType();
    }

    public static Integer getQuantityItemsMainHand(Player player) {
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();;
        return itemInMainHand.getAmount();
    }

}
