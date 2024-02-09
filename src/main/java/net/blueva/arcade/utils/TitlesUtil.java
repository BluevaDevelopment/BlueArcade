package net.blueva.arcade.utils;

import net.blueva.arcade.Main;
import net.blueva.arcade.version.v_1_8_R3;
import org.bukkit.entity.Player;

public class TitlesUtil {

    public static void sendTitle(Player player, String title, String subtitle, int fadein, int time, int fadeout) {
        title = StringUtils.formatMessage(player.getName(), title);
        subtitle = StringUtils.formatMessage(player.getName(), subtitle);

        if (Main.getPlugin().bukkitVersion.startsWith("1.8")) {
            v_1_8_R3.sendTitle(player, title, subtitle, fadein, time, fadeout);
        } else {
            player.sendTitle(title, subtitle, fadein, time, fadeout);
        }
    }

}
