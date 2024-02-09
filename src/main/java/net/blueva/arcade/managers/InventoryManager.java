package net.blueva.arcade.managers;

import net.blueva.arcade.Main;
import net.blueva.arcade.ObjectResolver;
import net.blueva.arcade.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class InventoryManager {

    private Main main;

    public InventoryManager(Main main) {
        this.main = main;
    }

    public void setupMinigames(Player p) {
        Inventory inv = Bukkit.createInventory(null, 54, StringUtils.formatMessage(p.getName(), CacheManager.Language.GUI_SETUP_SELECT_GAME_TITLE));
        
        // race
        ItemStack itemRace = new ItemStack(Material.EMERALD, 1);
        ItemMeta metaRace = itemRace.getItemMeta();
        metaRace.setDisplayName(StringUtils.formatMessage(p.getName(), CacheManager.Language.GUI_SETUP_SELECT_GAME_ITEMS_RACE_NAME));

        List<String> rawRaceLore = CacheManager.Language.GUI_SETUP_SELECT_GAME_ITEMS_RACE_LORE;
        List<String> raceLore = new ArrayList<>();
        for (String line : rawRaceLore) {
            raceLore.add(StringUtils.formatMessage(p.getName(), line));
        }

        metaRace.setLore(raceLore);
        itemRace.setItemMeta(metaRace);

        inv.setItem(10, itemRace);

        // spleef
        ItemStack itemSpleef = new ItemStack(Material.SNOW_BLOCK, 1);
        ItemMeta metaSpleef = itemSpleef.getItemMeta();

        metaSpleef.setDisplayName(StringUtils.formatMessage(p.getName(), CacheManager.Language.GUI_SETUP_SELECT_GAME_ITEMS_SPLEEF_NAME));

        List<String> rawSpleefLore = CacheManager.Language.GUI_SETUP_SELECT_GAME_ITEMS_SPLEEF_LORE;
        List<String> spleefLore = new ArrayList<>();
        for (String line : rawSpleefLore) {
            spleefLore.add(StringUtils.formatMessage(p.getName(), line));
        }

        metaSpleef.setLore(spleefLore);
        itemSpleef.setItemMeta(metaSpleef);

        inv.setItem(11, itemSpleef);

        // snowball fight
        ItemStack itemSnowballFight = new ItemStack(Material.valueOf(ObjectResolver.getItem.SNOWBALL()), 1);
        ItemMeta metaSnowballFight = itemSnowballFight.getItemMeta();

        metaSnowballFight.setDisplayName(StringUtils.formatMessage(p.getName(), CacheManager.Language.GUI_SETUP_SELECT_GAME_ITEMS_SNOWBALL_FIGHT_NAME));

        List<String> rawSnowballFightLore = CacheManager.Language.GUI_SETUP_SELECT_GAME_ITEMS_SNOWBALL_FIGHT_LORE;
        List<String> snowballFightLore = new ArrayList<>();
        for (String line : rawSnowballFightLore) {
            snowballFightLore.add(StringUtils.formatMessage(p.getName(), line));
        }

        metaSnowballFight.setLore(snowballFightLore);
        itemSnowballFight.setItemMeta(metaSnowballFight);

        inv.setItem(12, itemSnowballFight);

        // allagainstall
        ItemStack itemAllAgainstAll = new ItemStack(Material.IRON_SWORD, 1);
        ItemMeta metaAllAgainstAll = itemAllAgainstAll.getItemMeta();

        metaAllAgainstAll.setDisplayName(StringUtils.formatMessage(p.getName(), CacheManager.Language.GUI_SETUP_SELECT_GAME_ITEMS_ALL_AGAINST_ALL_NAME));

        List<String> rawAllAgainstAllLore = CacheManager.Language.GUI_SETUP_SELECT_GAME_ITEMS_ALL_AGAINST_ALL_LORE;
        List<String> AllAgainstAllLore = new ArrayList<>();
        for (String line : rawAllAgainstAllLore) {
            AllAgainstAllLore.add(StringUtils.formatMessage(p.getName(), line));
        }

        metaAllAgainstAll.setLore(AllAgainstAllLore);
        itemAllAgainstAll.setItemMeta(metaAllAgainstAll);

        inv.setItem(13, itemAllAgainstAll);

        // oneinthechamber
        ItemStack itemOneInTheChamber = new ItemStack(Material.BOW, 1);
        ItemMeta metaOneInTheChamber = itemOneInTheChamber.getItemMeta();

        metaOneInTheChamber.setDisplayName(StringUtils.formatMessage(p.getName(), CacheManager.Language.GUI_SETUP_SELECT_GAME_ITEMS_ONE_IN_THE_CHAMBER_NAME));

        List<String> rawOneInTheChamber = CacheManager.Language.GUI_SETUP_SELECT_GAME_ITEMS_ONE_IN_THE_CHAMBER_LORE;
        List<String> OneInTheChamberLore = new ArrayList<>();
        for (String line : rawOneInTheChamber) {
            OneInTheChamberLore.add(StringUtils.formatMessage(p.getName(), line));
        }

        metaOneInTheChamber.setLore(OneInTheChamberLore);
        itemOneInTheChamber.setItemMeta(metaOneInTheChamber);

        inv.setItem(14, itemOneInTheChamber);

        // trafficlight
        ItemStack itemTrafficLight = new ItemStack(Material.valueOf(ObjectResolver.getItem.RED_ROSE()), 1);
        ItemMeta metaTrafficLight = itemTrafficLight.getItemMeta();

        metaTrafficLight.setDisplayName(StringUtils.formatMessage(p.getName(), CacheManager.Language.GUI_SETUP_SELECT_GAME_ITEMS_TRAFFIC_LIGHT_NAME));

        List<String> rawTrafficLight = CacheManager.Language.GUI_SETUP_SELECT_GAME_ITEMS_TRAFFIC_LIGHT_LORE;
        List<String> TrafficLightLore = new ArrayList<>();
        for (String line : rawTrafficLight) {
            TrafficLightLore.add(StringUtils.formatMessage(p.getName(), line));
        }

        metaTrafficLight.setLore(TrafficLightLore);
        itemTrafficLight.setItemMeta(metaTrafficLight);

        inv.setItem(15, itemTrafficLight);

        // minefield
        ItemStack itemMinefield = new ItemStack(Material.valueOf(ObjectResolver.getBlock.GOLD_PLATE()), 1);
        ItemMeta metaMinefield = itemTrafficLight.getItemMeta();

        metaMinefield.setDisplayName(StringUtils.formatMessage(p.getName(), CacheManager.Language.GUI_SETUP_SELECT_GAME_ITEMS_MINEFIELD_NAME));

        List<String> rawMinefield = CacheManager.Language.GUI_SETUP_SELECT_GAME_ITEMS_MINEFIELD_LORE;
        List<String> MinefieldLore = new ArrayList<>();
        for (String line : rawMinefield) {
            MinefieldLore.add(StringUtils.formatMessage(p.getName(), line));
        }

        metaMinefield.setLore(MinefieldLore);
        itemMinefield.setItemMeta(metaMinefield);

        inv.setItem(16, itemMinefield);

        //fila 2
        // exploding sheep
        ItemStack itemExplodingSheep = new ItemStack(Material.SHEARS, 1);
        ItemMeta metaExplodingSheep = itemExplodingSheep.getItemMeta();

        metaExplodingSheep.setDisplayName(StringUtils.formatMessage(p.getName(), CacheManager.Language.GUI_SETUP_SELECT_GAME_ITEMS_EXPLODING_SHEEP_NAME));

        List<String> rawExplodingSheep = CacheManager.Language.GUI_SETUP_SELECT_GAME_ITEMS_EXPLODING_SHEEP_LORE;
        List<String> ExplodingSheepLore = new ArrayList<>();
        for (String line : rawExplodingSheep) {
            ExplodingSheepLore.add(StringUtils.formatMessage(p.getName(), line));
        }

        metaExplodingSheep.setLore(ExplodingSheepLore);
        itemExplodingSheep.setItemMeta(metaExplodingSheep);

        inv.setItem(19, itemExplodingSheep);

        // tnt tag
        ItemStack itemTNTTag = new ItemStack(Material.TNT, 1);
        ItemMeta metaTNTTag = itemTNTTag.getItemMeta();

        metaTNTTag.setDisplayName(StringUtils.formatMessage(p.getName(), CacheManager.Language.GUI_SETUP_SELECT_GAME_ITEMS_TNT_TAG_NAME));

        List<String> rawTNTTag = CacheManager.Language.GUI_SETUP_SELECT_GAME_ITEMS_TNT_TAG_LORE;
        List<String> TNTTagLore = new ArrayList<>();
        for (String line : rawTNTTag) {
            TNTTagLore.add(StringUtils.formatMessage(p.getName(), line));
        }

        metaTNTTag.setLore(TNTTagLore);
        itemTNTTag.setItemMeta(metaTNTTag);

        inv.setItem(20, itemTNTTag);

        // red alert
        ItemStack itemRedAlert = new ItemStack(Material.REDSTONE, 1);
        ItemMeta metaRedAlert = itemRedAlert.getItemMeta();

        metaRedAlert.setDisplayName(StringUtils.formatMessage(p.getName(), CacheManager.Language.GUI_SETUP_SELECT_GAME_ITEMS_RED_ALERT_NAME));

        List<String> rawRedAlert = CacheManager.Language.GUI_SETUP_SELECT_GAME_ITEMS_RED_ALERT_LORE;
        List<String> RedAlertLore = new ArrayList<>();
        for (String line : rawRedAlert) {
            RedAlertLore.add(StringUtils.formatMessage(p.getName(), line));
        }

        metaRedAlert.setLore(RedAlertLore);
        itemRedAlert.setItemMeta(metaRedAlert);

        inv.setItem(21, itemRedAlert);

        // knockback
        ItemStack itemKnockBack = new ItemStack(Material.STICK, 1);
        ItemMeta metaKnockBack = itemKnockBack.getItemMeta();

        metaKnockBack.setDisplayName(StringUtils.formatMessage(p.getName(), CacheManager.Language.GUI_SETUP_SELECT_GAME_ITEMS_KNOCK_BACK_NAME));

        List<String> rawKnockBack = CacheManager.Language.GUI_SETUP_SELECT_GAME_ITEMS_KNOCK_BACK_LORE;
        List<String> KnockBackLore = new ArrayList<>();
        for (String line : rawKnockBack) {
            KnockBackLore.add(StringUtils.formatMessage(p.getName(), line));
        }

        metaKnockBack.setLore(KnockBackLore);
        itemKnockBack.setItemMeta(metaKnockBack);

        inv.setItem(22, itemKnockBack);

        // Fast Zone
        ItemStack itemFastZone = new ItemStack(Material.ICE, 1);
        ItemMeta metaFastZone = itemKnockBack.getItemMeta();

        metaFastZone.setDisplayName(StringUtils.formatMessage(p.getName(), CacheManager.Language.GUI_SETUP_SELECT_GAME_ITEMS_FAST_ZONE_NAME));

        List<String> rawFastZone = CacheManager.Language.GUI_SETUP_SELECT_GAME_ITEMS_FAST_ZONE_LORE;
        List<String> FastZoneLore = new ArrayList<>();
        for (String line : rawFastZone) {
            FastZoneLore.add(StringUtils.formatMessage(p.getName(), line));
        }

        metaFastZone.setLore(FastZoneLore);
        itemFastZone.setItemMeta(metaFastZone);

        inv.setItem(23, itemFastZone);

        // leave
        ItemStack leave = new ItemStack(Material.BARRIER, 1);
        ItemMeta leavem = leave.getItemMeta();
        leavem.setDisplayName(StringUtils.formatMessage(p.getName(), CacheManager.Language.GUI_SETUP_SELECT_GAME_ITEMS_EXIT_NAME));

        List<String> rawExitLore = CacheManager.Language.GUI_SETUP_SELECT_GAME_ITEMS_EXIT_LORE;
        List<String> exitLore = new ArrayList<>();
        for (String line : rawExitLore) {
            exitLore.add(StringUtils.formatMessage(p.getName(), line));
        }

        leavem.setLore(exitLore);
        leave.setItemMeta(leavem);
        inv.setItem(49, leave);

        // remove selection
        ItemStack selection = new ItemStack(Material.valueOf(ObjectResolver.getItem.STAINED_GLASS_PANE("black")), 1, (short) 15);
        ItemMeta selectionmeta = selection.getItemMeta();
        selectionmeta.setDisplayName(StringUtils.formatMessage(p.getName(), CacheManager.Language.GUI_SETUP_SELECT_GAME_ITEMS_REMOVE_SELECTION_NAME));

        List<String> rawSelectionLore = CacheManager.Language.GUI_SETUP_SELECT_GAME_ITEMS_REMOVE_SELECTION_LORE;
        List<String> selectionLore = new ArrayList<>();
        for (String line : rawSelectionLore) {
            selectionLore.add(StringUtils.formatMessage(p.getName(), line));
        }

        selectionmeta.setLore(selectionLore);
        selection.setItemMeta(selectionmeta);

        inv.setItem(45, selection);
        inv.setItem(46, selection);
        inv.setItem(47, selection);
        inv.setItem(48, selection);
        inv.setItem(50, selection);
        inv.setItem(51, selection);
        inv.setItem(52, selection);
        inv.setItem(53, selection);

        p.openInventory(inv);
    }
}
