package net.blueva.arcade.configuration;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import net.blueva.arcade.Main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

public class ConfigValidator {

    private final Main main;

    public ConfigValidator(Main main) {
        this.main = main;
    }

    public void check(String file) {
        validateConfig(file);
    }

    public void checkAll() {
        validateConfig("settings");
    }

    private void validateConfig(@NotNull String file) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");

        InputStream is = null;

        if(file.equalsIgnoreCase("settings") || file.equalsIgnoreCase("nicks") ||
                file.equalsIgnoreCase("messages")| file.equalsIgnoreCase("license") ||
                file.equalsIgnoreCase("interactions") || file.equalsIgnoreCase("events")) {
            is = main.getResource("com/blueva/arcade/configuration/files/"+file+".yml");
        } else if(file.equalsIgnoreCase("chat") || file.equalsIgnoreCase("cnicks") ||
                file.equalsIgnoreCase("session")| file.equalsIgnoreCase("uuid")) {
            if(file.equalsIgnoreCase("cnicks")) {
                is = main.getResource("com/blueva/arcade/configuration/files/cache/nicks.yml");
            } else {
                is = main.getResource("red/blueva/arcade/configuration/files/cache/"+file+".yml");
            }
        } else if (file.equalsIgnoreCase("lang")) {
            is = main.getResource("red/blueva/arcade/configuration/files/language/"+main.actualLang+".yml");
        }

        assert is != null;
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(new InputStreamReader(is));

        Set<String> pluginKeys = configuration.getKeys(true);
        Set<String> configKeys = null;

        //Main files
        if(file.equalsIgnoreCase("settings")) {
            configKeys = main.configManager.getSettings().getKeys(true);
        }
        if(file.equalsIgnoreCase("rewards")) {
            configKeys = main.configManager.getRewards().getKeys(true);
        }

        //lang file
        if(file.equalsIgnoreCase("lang")) {
            configKeys = main.configManager.getLang().getKeys(true);
        }

        for (String s : pluginKeys) {
            assert configKeys != null;
            if (!configKeys.contains(s)) {
                //Bukkit.getConsoleSender().sendMessage(MessagesUtil.format(main.configManager.getLang().getString("console.info.invalid_config").replace("{config_file}", file)));
                //Bukkit.getConsoleSender().sendMessage(main.);

                File backupFolder = new File("plugins/BlueArcade/backup");
                if (!backupFolder.exists()) {
                    backupFolder.mkdir();
                }

                Path source = null;
                Path dest = null;
                File source2 = null;

                if (file.equalsIgnoreCase("lang")){
                    source = Paths.get("plugins/BlueArcade/language/"+main.actualLang+".yml");
                    dest = Paths.get("plugins/BlueArcade/backup/"+main.actualLang+"_" + format.format(date) + ".yml");
                    source2 = new File("plugins/BlueArcade/language/"+main.actualLang+".yml");
                } else {
                    source = Paths.get("plugins/BlueArcade/"+file+".yml");
                    dest = Paths.get("plugins/BlueArcade/backup/"+file+"_" + format.format(date) + ".yml");
                    source2 = new File("plugins/BlueArcade/"+file+".yml");
                }

                try {
                    Files.copy(source, dest);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                source2.delete();

                //Register config
                //Main files
                if(file.equalsIgnoreCase("settings")) {
                    main.configManager.registerSettings();
                }
                if(file.equalsIgnoreCase("rewards")) {
                    main.configManager.registerRewards();
                }

                //lang file
                if(file.equalsIgnoreCase("lang")) {
                    main.configManager.registerLang();
                }

                return;
            }
        }
    }
}
