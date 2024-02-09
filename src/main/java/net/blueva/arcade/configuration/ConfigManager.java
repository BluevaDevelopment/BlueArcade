package net.blueva.arcade.configuration;

import net.blueva.arcade.Main;
import net.blueva.arcade.configuration.updater.ConfigUpdater;
import net.blueva.arcade.managers.SoundsManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ConfigManager {

    private final Main main;

    public ConfigManager(Main main) {
        this.main = main;
    }

    public void generateFolders() {
        if(!main.getDataFolder().exists()) {
            main.getDataFolder().mkdirs();
        }

        // Language folder
        File languagef = new File(main.getDataFolder()+"/language");
        if(!languagef.exists()) {
            languagef.mkdirs();

            GenerateLanguages gl = new GenerateLanguages(main);
            gl.generate();
        }

        // Backups folder
        File backupf = new File(main.getDataFolder()+"/backup");
        if(!backupf.exists()) {
            backupf.mkdirs();
        }

        // Data folder
        File dataf = new File(main.getDataFolder()+"/data");
        if(!dataf.exists()) {
            dataf.mkdirs();
        }

        // arenas folder
        File arenasf = new File(main.getDataFolder()+"/data/arenas");
        if(!arenasf.exists()) {
            arenasf.mkdirs();
        }

        // users folder
        File usersf = new File(main.getDataFolder()+"/data/users");
        if(!usersf.exists()) {
            usersf.mkdirs();
        }

        // regions folder
        File regionsf = new File(main.getDataFolder()+"/data/regions");
        if(!regionsf.exists()) {
            regionsf.mkdirs();
        }

    }

    //settings.yml file
    public FileConfiguration getSettings() {
        if(main.settings == null) {
            reloadSettings();
        }
        return main.settings;
    }

    public void reloadSettings(){
        if(main.settings == null){
            main.settingsFile = new File(main.getDataFolder(),"settings.yml");
        }
        main.settings = YamlConfiguration.loadConfiguration(main.settingsFile);
        Reader defConfigStream;
        defConfigStream = new InputStreamReader(main.getResource("net/blueva/arcade/configuration/files/settings.yml"), StandardCharsets.UTF_8);
        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
        main.settings.setDefaults(defConfig);
    }

    public void saveSettings(){
        try{
            main.settings.save(main.settingsFile);
            ConfigUpdater.update(main, "net/blueva/arcade/configuration/files/settings.yml", new File(main.getDataFolder()+"/settings.yml"));
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void registerSettings(){
        main.settingsFile = new File(main.getDataFolder(),"settings.yml");
        if(!main.settingsFile.exists()){
            this.getSettings().options().copyDefaults(true);
            saveSettings();
        }
    }

    //rewards.yml file
    public FileConfiguration getRewards() {
        if(main.rewards == null) {
            reloadRewards();
        }
        return main.rewards;
    }

    public void reloadRewards(){
        if(main.rewards == null){
            main.rewardsFile = new File(main.getDataFolder(),"rewards.yml");
        }
        main.rewards = YamlConfiguration.loadConfiguration(main.rewardsFile);
        Reader defConfigStream;
        defConfigStream = new InputStreamReader(main.getResource("net/blueva/arcade/configuration/files/rewards.yml"), StandardCharsets.UTF_8);
        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
        main.rewards.setDefaults(defConfig);
    }

    public void saveRewards(){
        try{
            main.rewards.save(main.rewardsFile);
            ConfigUpdater.update(main, "net/blueva/arcade/configuration/files/rewards.yml", new File(main.getDataFolder()+"/rewards.yml"));
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void registerRewards(){
        main.rewardsFile = new File(main.getDataFolder(),"rewards.yml");
        if(!main.rewardsFile.exists()){
            this.getRewards().options().copyDefaults(true);
            saveRewards();
        }
    }

    //sounds.yml file
    public FileConfiguration getSounds() {
        if(main.sounds == null) {
            reloadSounds();
        }
        return main.sounds;
    }

    public void reloadSounds(){
        if(main.sounds == null){
            main.soundsFile = new File(main.getDataFolder(),"sounds.yml");
        }
        main.sounds = YamlConfiguration.loadConfiguration(main.soundsFile);
        Reader defConfigStream;
        defConfigStream = new InputStreamReader(main.getResource("net/blueva/arcade/configuration/files/sounds.yml"), StandardCharsets.UTF_8);
        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
        main.sounds.setDefaults(defConfig);
    }

    public void saveSounds(){
        try{
            main.sounds.save(main.soundsFile);
            ConfigUpdater.update(main, "net/blueva/arcade/configuration/files/sounds.yml", new File(main.getDataFolder()+"/sounds.yml"));
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void registerSounds(){
        main.soundsFile = new File(main.getDataFolder(),"sounds.yml");
        if(!main.soundsFile.exists()){
            this.getSounds().options().copyDefaults(true);
            saveSounds();
            SoundsManager.copySounds(main);
        }
    }

    //global.yml file
    public FileConfiguration getGlobal() {
        if(main.global == null) {
            reloadGlobal();
        }
        return main.global;
    }

    public void reloadGlobal(){
        if(main.global == null){
            main.globalFile = new File(main.getDataFolder()+"/data/","global.yml");
        }
        main.global = YamlConfiguration.loadConfiguration(main.globalFile);
        Reader defConfigStream;
        defConfigStream = new InputStreamReader(main.getResource("net/blueva/arcade/configuration/files/data/global.yml"), StandardCharsets.UTF_8);
        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
        main.global.setDefaults(defConfig);
    }

    public void saveGlobal(){
        try{
            main.global.save(main.globalFile);
            ConfigUpdater.update(main, "net/blueva/arcade/configuration/files/data/global.yml", new File(main.getDataFolder()+"/data"+"/global.yml"));
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void registerGlobal(){
        main.globalFile = new File(main.getDataFolder()+"/data/","global.yml");
        if(!main.globalFile.exists()){
            this.getGlobal().options().copyDefaults(true);
            saveGlobal();
        }
    }

    //signs.yml file
    public FileConfiguration getSigns() {
        if(main.signs == null) {
            reloadSigns();
        }
        return main.signs;
    }

    public void reloadSigns(){
        if(main.signs == null){
            main.signsFile = new File(main.getDataFolder()+"/data/","signs.yml");
        }
        main.signs = YamlConfiguration.loadConfiguration(main.signsFile);
        Reader defConfigStream;
        defConfigStream = new InputStreamReader(main.getResource("net/blueva/arcade/configuration/files/data/signs.yml"), StandardCharsets.UTF_8);
        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
        main.signs.setDefaults(defConfig);
    }

    public void saveSigns(){
        try{
            main.signs.save(main.signsFile);
            //ConfigUpdater.update(main, "net/blueva/arcade/configuration/files/data/signs.yml", new File(main.getDataFolder()+"/data/signs.yml"));
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void registerSigns(){
        main.signsFile = new File(main.getDataFolder()+"/data/","signs.yml");
        if(!main.signsFile.exists()){
            this.getSigns().options().copyDefaults(true);
            saveSigns();
        }
    }

    //actual lang manager
    //lang.yml file
    public FileConfiguration getLang() {
        if(main.language == null) {
            reloadLang();
        }
        return main.language;
    }

    public void reloadLang(){
        if(main.language == null){
            main.languageFile = new File(main.getDataFolder()+"/language/",main.actualLang+".yml");
        }
        main.language = YamlConfiguration.loadConfiguration(main.languageFile);
        Reader defConfigStream;
        defConfigStream = new InputStreamReader(main.getResource("net/blueva/arcade/configuration/files/language/"+main.actualLang+".yml"), StandardCharsets.UTF_8);
        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
        main.language.setDefaults(defConfig);
    }

    public void saveLang(){
        try{
            main.language.save(main.languageFile);
            ConfigUpdater.update(main, "net/blueva/arcade/configuration/files/language/"+main.actualLang+".yml", new File(main.getDataFolder()+"/language/"+main.actualLang+".yml"));
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void registerLang(){
        main.languageFile = new File(main.getDataFolder()+"/language/",main.actualLang+".yml");
        if(!main.languageFile.exists()){
            this.getLang().options().copyDefaults(true);
            main.langPath = getLang().getCurrentPath();
            saveLang();
        }
    }

    //individual user file
    public FileConfiguration getUser(UUID uuid) {
        reloadUser(uuid);
        return main.user;
    }

    public void reloadUser(UUID uuid){
        main.userFile = new File(main.getDataFolder()+"/data/users/",uuid+".yml");
        main.user = YamlConfiguration.loadConfiguration(main.userFile);
        Reader defConfigStream;
        defConfigStream = new InputStreamReader(main.getResource("net/blueva/arcade/configuration/files/data/userdatadefault.yml"), StandardCharsets.UTF_8);
        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
        main.user.setDefaults(defConfig);
    }

    public void saveUser(UUID uuid){
        try{
            main.user.save(main.userFile);
            //ConfigUpdater.update(main, "net/blueva/arcade/configuration/files/data/userdatadefault.yml", new File(main.getDataFolder()+"/data/"+userid+".yml"));
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void registerUser(UUID uuid){
        main.userFile = new File(main.getDataFolder()+"/data/users/",uuid+".yml");
        if(!main.userFile.exists()){
            this.getUser(uuid).options().copyDefaults(true);
            saveUser(uuid);
        }
    }

    //individual arena files
    public FileConfiguration getArena(int arenaid) {
        reloadArena(arenaid);
        return main.arena;
    }

    public File getArenaFile(int arenaid){
        reloadArena(arenaid);
        return main.arenaFile;
    }

    public void reloadArena(int arenaid){
        main.arenaFile = new File(main.getDataFolder()+"/data/arenas/",arenaid+".yml");
        main.arena = YamlConfiguration.loadConfiguration(main.arenaFile);
        Reader defConfigStream;
        defConfigStream = new InputStreamReader(main.getResource("net/blueva/arcade/configuration/files/data/arenadatadefault.yml"), StandardCharsets.UTF_8);
        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
        main.arena.setDefaults(defConfig);
    }

    public void saveArena(int arenaid){
        try{
            main.arena.save(main.arenaFile);
            //ConfigUpdater.update(main, "net/blueva/arcade/configuration/files/data/arenadatadefault.yml", new File(main.getDataFolder()+"/data/arenas/"+arenaid+".yml"), Collections.singletonList("mini_games"));
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void registerArena(int arenaid){
        main.arenaFile = new File(main.getDataFolder()+"/data/arenas/",arenaid+".yml");
        if(!main.arenaFile.exists()){
            this.getArena(arenaid).options().copyDefaults(true);
            saveArena(arenaid);
        }
    }

    // individual region files
    public FileConfiguration getRegion(String region, int arenaid) {
        reloadRegion(region, arenaid);
        return main.region;
    }

    public File getRegionFile(String region, int arenaid){
        reloadRegion(region, arenaid);
        return main.regionFile;
    }

    public void reloadRegion(String region, int arenaid){
        main.regionFile = new File(main.getDataFolder()+"/data/regions/"+arenaid+"/",region+".yml");
        main.region = YamlConfiguration.loadConfiguration(main.regionFile);
        Reader defConfigStream;
        defConfigStream = new InputStreamReader(main.getResource("net/blueva/arcade/configuration/files/data/regiondatadefault.yml"), StandardCharsets.UTF_8);
        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
        main.region.setDefaults(defConfig);
    }

    public void saveRegion(){
        try{
            main.region.save(main.regionFile);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void registerRegion(String region, int arenaid){
        main.regionFile = new File(main.getDataFolder()+"/data/regions/"+arenaid+"/" ,region+".yml");
        if(!main.regionFile.exists()){
            this.getRegion(region, arenaid).options().copyDefaults(true);
            saveRegion();
        }
    }



}
