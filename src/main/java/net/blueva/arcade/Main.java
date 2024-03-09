package net.blueva.arcade;

import net.blueva.arcade.commands.main.CommandHandler;
import net.blueva.arcade.commands.main.command.ArcadeCommand;
import net.blueva.arcade.commands.main.subcommands.*;
import net.blueva.arcade.commands.main.tabcomplete.ArcadeTabComplete;
import net.blueva.arcade.configuration.ConfigManager;
import net.blueva.arcade.libraries.bStats.Metrics;
import net.blueva.arcade.libraries.placeholderapi.Placeholders;
import net.blueva.arcade.libraries.updatechecker.UpdateCheckSource;
import net.blueva.arcade.libraries.updatechecker.UpdateChecker;
import net.blueva.arcade.libraries.updatechecker.UserAgentBuilder;
import net.blueva.arcade.listeners.*;
import net.blueva.arcade.managers.*;
import net.blueva.arcade.managers.setup.SetupManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Main extends JavaPlugin implements Listener {

    //player admin arrays
    public Map<Player, Boolean> SetupProcess = new HashMap<>();
    public Map<Player, Integer> SetupArena = new HashMap<>();

    //config files
    public ConfigManager configManager;

    //settings.yml
    public FileConfiguration settings = null;
    public File settingsFile = null;

    //global.yml
    public FileConfiguration global = null;
    public File globalFile = null;

    //lang files
    public FileConfiguration language = null;
    public File languageFile = null;

    //license.yml
    public FileConfiguration license = null;
    public File licenseFile = null;

    //rewards.yml
    public FileConfiguration rewards = null;
    public File rewardsFile = null;

    //sounds.yml
    public FileConfiguration sounds = null;
    public File soundsFile = null;

    //signs.yml
    public FileConfiguration signs = null;
    public File signsFile = null;

    //user files
    public FileConfiguration user = null;
    public File userFile = null;

    //arena files
    public FileConfiguration arena = null;
    public File arenaFile = null;

    //arena files
    public FileConfiguration region = null;
    public File regionFile = null;

    //other things
    public String pluginversion = getDescription().getVersion();
    public static boolean placeholderapi = false;
    public String actualLang;
    public String langPath;
    private static Main plugin;
    public String bukkitVersion = Bukkit.getServer().getBukkitVersion();
    public static Main getPlugin() {
        return plugin;
    }

    // managers
    public SetupManager setupManager = null;
    public SignManager signManager = null;

    @Override
    public void onEnable() {
        plugin = this;

        configManager = new ConfigManager(this);
        registerConfigFiles();

        updateArenaList();

        configManager.saveSettings();
        configManager.reloadSettings();

        if(configManager.getSettings().getBoolean("metrics")) {
            int pluginId = 18078;
            new Metrics(this, pluginId);
        }

        if(configManager.getSettings().getBoolean("check_for_updates")) {
            new UpdateChecker(this, UpdateCheckSource.CUSTOM_URL, "https://blueva.net/api/arcade/version.txt")
                    .setFreeDownloadLink("https://builtbybit.com/resources/blue-arcade-8-minigames-party-games.28272/")
                    .setPaidDownloadLink("https://polymart.org/resource/blue-arcade-party-games.4146")
                    .setNameFreeVersion("BuiltByBit")
                    .setNamePaidVersion("Polymart")
                    .setChangelogLink("https://blueva.net/resources/resource/2-blue-arcade/?releases=all")
                    .setNotifyOpsOnJoin(true)
                    .setNotifyByPermissionOnJoin("bluearcade.admin")
                    .setUserAgent(new UserAgentBuilder().addPluginNameAndVersion())
                    .checkEveryXHours(12)
                    .checkNow();
        }

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new BlockBreakListener(this), this);
        pm.registerEvents(new BlockPlaceListener(this), this);
        pm.registerEvents(new EntityDamageByEntityListener(this), this);
        pm.registerEvents(new EntityDamageListener(this), this);
        pm.registerEvents(new FoodLevelChangeListener(), this);
        pm.registerEvents(new InventoryClickListener(this), this);
        pm.registerEvents(new PlayerChatListener(this), this);
        pm.registerEvents(new PlayerCommandPreprocessListener(this), this);
        pm.registerEvents(new PlayerDropItemListener(this), this);
        pm.registerEvents(new PlayerJoinListener(this), this);
        pm.registerEvents(new PlayerLeaveListener(this), this);
        pm.registerEvents(new PlayerMoveListener(this), this);
        pm.registerEvents(new PlayerInteractListener(this), this);
        pm.registerEvents(new EntityPickupItemListener(this), this);
        pm.registerEvents(new PlayerShearEntityListener(), this);
        pm.registerEvents(new ProjectileHitListener(), this);
        pm.registerEvents(new SignChangeListener(this), this);

        registerCommands();

        ScoreboardManager scoreboard = new ScoreboardManager(this);
        scoreboard.CreateAllScoreboards();

        setupManager = new SetupManager(this);
        signManager = new SignManager(this);

        updateCache();

        placeholderapi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;

        if(placeholderapi) {
            new Placeholders(this).register();

        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "  ____  _               _                      _");
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + " | __ )| |_   _  ___   / \\   _ __ ___ __ _  __| | ___");
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + " |  _ \\| | | | |/ _ \\ / _ \\ | '__/ __/ _` |/ _` |/ _ \\");
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + " | |_) | | |_| |  __// ___ \\| | | (_| (_| | (_| |  __/");
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + " |____/|_|\\____|\\___/_/   \\_|_|  \\___\\____|\\____|\\___|");
            Bukkit.getConsoleSender().sendMessage("");
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "V. " + pluginversion + " | Plugin enabled successfully | blueva.net");
            signManager.updateSignsTask();
        });
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "  ____  _               _                      _");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + " | __ )| |_   _  ___   / \\   _ __ ___ __ _  __| | ___");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + " |  _ \\| | | | |/ _ \\ / _ \\ | '__/ __/ _` |/ _` |/ _ \\");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + " | |_) | | |_| |  __// ___ \\| | | (_| (_| | (_| |  __/");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + " |____/|_|\\____|\\___/_/   \\_|_|  \\___\\____|\\____|\\___|");
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "V. " + pluginversion + " | Plugin disabled successfully | blueva.net");
    }

    public void registerCommands() {
        CommandHandler handler = new CommandHandler(this);

        //main command
        handler.register("arcade", new ArcadeCommand(this));

        // subcommands
        handler.register("goto", new GotoSubCommand(this));
        handler.register("help", new HelpSubCommand(this));
        handler.register("join", new JoinSubCommand(this));
        handler.register("leave", new LeaveSubCommand(this));
        handler.register("quickjoin", new QuickjoinSubCommand(this));
        handler.register("reload", new ReloadSubCommand(this));
        handler.register("remove", new RemoveSubCommand(this));
        handler.register("setmainlobby", new SetMainLobbySubCommand(this));
        handler.register("setup", new SetupSubCommand(this));

        Objects.requireNonNull(getCommand("arcade")).setExecutor(handler);
        Objects.requireNonNull(getCommand("arcade")).setTabCompleter(new ArcadeTabComplete());
    }

    public void registerConfigFiles() {
        configManager.generateFolders();

        configManager.registerSettings();

        actualLang = configManager.getSettings().getString("language");

        configManager.registerSounds();
        configManager.registerRewards();
        configManager.registerLang();
        configManager.registerGlobal();
        configManager.registerSigns();
    }

    public void updateArenaList() {
        File folder = new File(getDataFolder()+"/data/arenas");

        if(folder.isDirectory() && folder.canRead()) {
            File[] listOfFiles = folder.listFiles();

            ArenaManager.ArenaList.clear();

            if (listOfFiles != null) {
                for(File file : listOfFiles) {
                    ArenaManager.ArenaList.add(Integer.parseInt(file.getName().replace(".yml", "")));
                    CacheManager.Arenas.updateArenaBasic(this, Integer.parseInt(file.getName().replace(".yml", "")));
                }
            }

        }

        updateArenaStatus();
        registerArenaPlayerCount();
    }

    public void updateArenaStatus() {
        for(final Integer id : ArenaManager.ArenaList) {
            if(!ArenaManager.ArenaStatusInternal.containsKey(id)) {
                ArenaManager.ArenaStatusInternal.put(id, "Waiting");
            } else {
                ArenaManager.ArenaStatusInternal.replace(id, "Waiting");
            }
            if(!ArenaManager.ArenaStatus.containsKey(id)) {
                ArenaManager.ArenaStatus.put(id, configManager.getArena(id).getString("arena.basic.state"));
            } else {
                ArenaManager.ArenaStatusInternal.replace(id, configManager.getArena(id).getString("arena.basic.state"));
            }

            //Bukkit.getConsoleSender().sendMessage(id + ArenaManager.ArenaStatus.get(id) + " / " + ArenaManager.ArenaStatusInternal.get(id));
        }
    }

    public void registerArenaPlayerCount() {
        for(final int id : ArenaManager.ArenaList) {
            if(!ArenaManager.ArenaPlayersCount.containsKey(id)) {
                ArenaManager.ArenaPlayersCount.put(id, 0);
                ArenaManager.ArenaActualGame.put(id, "Lobby");
                ArenaManager.ArenaCountdown.put(id, 0);
            }
        }
    }

    public void updateCache() {
        CacheManager.Language.registerScoreboards(this);
        CacheManager.Settings.updateSettingsString(this);
        CacheManager.Sounds.updateSoundsString(this);
        CacheManager.Language.updateLanguageString(this);
        CacheManager.Language.updateItemsString(this);
        CacheManager.Language.updateScoreboardMiniDescriptions(this);
        CacheManager.Language.updateGUI(this);
        //updateArenaList();
    }

}