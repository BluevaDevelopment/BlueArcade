package net.blueva.arcade.utils;

import net.blueva.arcade.Main;
import net.blueva.arcade.managers.SerializeManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public class InventoryUtil {
    public static ItemStack[] getInventoryContent(String config) throws IOException {
        return SerializeManager.itemStackArrayFromBase64(config);
    }

    public static void SaveInventory(Main main, Player player) {
        String inventory = SerializeManager.playerInventoryToBase64(player.getInventory());
        main.configManager.getUser(player.getUniqueId()).set("inventory", inventory);
        main.configManager.saveUser(player.getUniqueId());
        main.configManager.reloadUser(player.getUniqueId());

    }

    public static void removeItems(Inventory inventory, Material type, int amount) {
        if (amount <= 0) return;
        int size = inventory.getSize();
        for (int slot = 0; slot < size; slot++) {
            ItemStack is = inventory.getItem(slot);
            if (is == null) continue;
            if (type == is.getType()) {
                int newAmount = is.getAmount() - amount;
                if (newAmount > 0) {
                    is.setAmount(newAmount);
                    break;
                } else {
                    inventory.clear(slot);
                    amount = -newAmount;
                    if (amount == 0) break;
                }
            }
        }
    }

    public static void addItems(Inventory inventory, Material type, int amount) {
        if (amount <= 0) return;
        int size = inventory.getSize();
        for (int slot = 0; slot < size; slot++) {
            ItemStack is = inventory.getItem(slot);
            if (is == null) {
                is = new ItemStack(type, amount);
                inventory.setItem(slot, is);
                break;
            }
            if (type == is.getType() && is.getMaxStackSize() >= is.getAmount() + amount) {
                is.setAmount(is.getAmount() + amount);
                break;
            }
        }
    }
}
