package net.blueva.arcade.managers;

import net.blueva.arcade.Main;
import net.blueva.arcade.utils.StringUtils;
import net.blueva.arcade.utils.SignsUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class SignManager {
    private final Main main;

    public SignManager(Main main) {
        this.main = main;
    }

    private ConfigurationSection getSignsSection() {
        return main.configManager.getSigns().getConfigurationSection("signs.list");
    }

    private ConfigurationSection getSignSection(Location location) {
        ConfigurationSection signsSection = main.configManager.getSigns().getConfigurationSection("signs.list");
        if (signsSection == null) {
            return null;
        }

        for (String key : signsSection.getKeys(false)) {
            ConfigurationSection signSection = signsSection.getConfigurationSection(key);
            if (signSection == null) {
                continue;
            }

            String worldName = signSection.getString("cords.world");
            double x = signSection.getDouble("cords.x");
            double y = signSection.getDouble("cords.y");
            double z = signSection.getDouble("cords.z");

            if (Objects.requireNonNull(location.getWorld()).getName().equals(worldName)
                    && location.getX() == x
                    && location.getY() == y
                    && location.getZ() == z) {
                return signSection;
            }
        }

        return null;
    }

    public boolean isRegisteredSign(Location location) {
        return getSignSection(location) != null;
    }

    public Integer arenaIDFromSign(Location location) {
        ConfigurationSection signSection = getSignSection(location);
        if (signSection == null) {
            return -1;
        }

        return signSection.getInt("info.arenaid");
    }

    public String getSignType(Location location) {
        ConfigurationSection signSection = getSignSection(location);
        if (signSection == null) {
            return null;
        }

        return signSection.getString("info.type");
    }


    public void removeRegisteredSign(Player player, Location location) {
        ConfigurationSection sign = getSignSection(location);
        if(sign != null) {
            String signName = sign.getName();
            main.configManager.getSigns().set("signs.list."+signName, null);
            main.configManager.saveSigns();
            main.configManager.reloadSigns();
            StringUtils.sendMessage(player, player.getName(), CacheManager.Language.GLOBAL_SUCCESS_SIGN_REMOVED
                    .replace("{s_type}", "join")
                    .replace("{s_x}", String.valueOf(location.getX()))
                    .replace("{s_y}", String.valueOf(location.getY()))
                    .replace("{s_z}", String.valueOf(location.getZ())));
        }
    }

    public void updateSignsTask() {
        BukkitRunnable signUpdateTask = new BukkitRunnable() {
            @Override
            public void run() {
                updateAllRegisteredSigns();
            }
        };
        signUpdateTask.runTaskTimer(main, 0L, 60L);
    }

    public void updateAllRegisteredSigns() {
        ConfigurationSection signsSection = getSignsSection();
        if (signsSection == null) {
            return;
        }

        for (String key : signsSection.getKeys(false)) {
            ConfigurationSection signSection = signsSection.getConfigurationSection(key);
            if (signSection == null) {
                continue;
            }

            String worldName = signSection.getString("cords.world");
            if(worldName != null) {
                double x = signSection.getDouble("cords.x");
                double y = signSection.getDouble("cords.y");
                double z = signSection.getDouble("cords.z");

                Location location = new Location(Bukkit.getWorld(worldName), x, y, z);
                if (location.getBlock().getState() instanceof Sign) {
                    updateSign((Sign) location.getBlock().getState(), arenaIDFromSign(location));
                }
            }
        }
    }

    private void updateSign(Sign sign, int arenaID) {
        if (sign == null) {
            return;
        }

        sign.getSide(Side.FRONT).setLine(0, SignsUtil.format(CacheManager.Language.SIGNS_ARENA_LINE1, arenaID));
        sign.getSide(Side.FRONT).setLine(1, SignsUtil.format(CacheManager.Language.SIGNS_ARENA_LINE2, arenaID));
        sign.getSide(Side.FRONT).setLine(2, SignsUtil.format(CacheManager.Language.SIGNS_ARENA_LINE3, arenaID));
        sign.getSide(Side.FRONT).setLine(3, SignsUtil.format(CacheManager.Language.SIGNS_ARENA_LINE4, arenaID));
        sign.update();
    }


}
