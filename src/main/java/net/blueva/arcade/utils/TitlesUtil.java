package net.blueva.arcade.utils;

import org.bukkit.entity.Player;

public class TitlesUtil {

    public static void sendTitle(Player player, String title, String subtitle, int fadein, int time, int fadeout) {
        title = StringUtils.formatMessage(player.getName(), title);
        subtitle = StringUtils.formatMessage(player.getName(), subtitle);

        player.sendTitle(title, subtitle, fadein, time, fadeout);
    }

}
