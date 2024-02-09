package net.blueva.arcade.managers.setup;

import net.blueva.arcade.ObjectResolver;
import net.blueva.arcade.managers.CacheManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.blueva.arcade.Main;
import net.blueva.arcade.utils.StringUtils;

public class RedAlertSetupManager {

    public static void eighthStep(Player player, Integer arenaid, Main main) {

        // AQUI  HAY QUE ESTABLECER EL SUELO

        main.setupManager.actualStepGame.replace(player, 8);

        StringUtils.sendMessage(player, player.getName(), CacheManager.Language.GLOBAL_INFO_SETUP_RED_ALERT_STEP_1);

        ItemStack stickfloor = new ItemStack(Material.STICK, 1);
        ItemMeta stickfloormeta = stickfloor.getItemMeta();
        stickfloormeta.addEnchant(Enchantment.DURABILITY, 1, true);
        stickfloormeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        stickfloormeta.setDisplayName(StringUtils.formatMessage(player.getName(), CacheManager.Language.ITEMS_SETUP_MAGIC_STICK).replace("{arena_id}", String.valueOf(arenaid)));
        stickfloor.setItemMeta(stickfloormeta);
        player.getInventory().setItem(0, stickfloor);

        ItemStack empty = new ItemStack(Material.valueOf(ObjectResolver.getItem.STAINED_GLASS_PANE("gray")), 1, (short) 7);
        ItemMeta emptymeta = empty.getItemMeta();
        emptymeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', " &7 "));
        empty.setItemMeta(emptymeta);
        player.getInventory().setItem(1, empty);
        player.getInventory().setItem(2, empty); // slot 3
        player.getInventory().setItem(3, empty); // slot 4
        player.getInventory().setItem(4, empty);// slot 5


        SetupManager.setDefaultItems(main, player, arenaid);
    }

    public static void finishSetup(Player player, Integer arenaid, Main main) {
        main.setupManager.actualStepGame.replace(player, 0);
        main.setupManager.selectedGame.remove(player);

        main.setupManager.maingame(player, arenaid);

        Integer cg = main.configManager.getArena(arenaid).getInt("arena.basic.configured_games")+1;
        main.configManager.getArena(arenaid).set("arena.basic.configured_games", cg);
        main.configManager.saveArena(arenaid);
        main.configManager.getArena(arenaid).set("arena.mini_games.red_alert.basic.enabled", true);
        main.configManager.saveArena(arenaid);
        main.configManager.getArena(arenaid).set("arena.mini_games.red_alert.basic.death_block",
                main.configManager.getSettings().getString("game.global.default_death_block"));
        main.configManager.saveArena(arenaid);
        main.configManager.getArena(arenaid).set("arena.mini_games.red_alert.basic.enable_particles", true);
        main.configManager.saveArena(arenaid);
        main.configManager.getArena(arenaid).set("arena.mini_games.red_alert.basic.change_color_chance", 0.5);
        main.configManager.saveArena(arenaid);
        main.configManager.getArena(arenaid).set("arena.mini_games.red_alert.basic.increase_chance_every_second", 0.1);
        main.configManager.saveArena(arenaid);
        main.configManager.reloadArena(arenaid);

        StringUtils.sendMessage(player, player.getName(), CacheManager.Language.GLOBAL_SUCCESS_MINI_GAME_SET.replace("{game}", "Red Alert"));
    }

}
