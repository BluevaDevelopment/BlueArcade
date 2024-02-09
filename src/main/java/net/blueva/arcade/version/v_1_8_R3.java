package net.blueva.arcade.version;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class v_1_8_R3 {
    public static void sendTitle(Player player, String title, String subtitle, int fadein, int time, int fadeout) {
        CraftPlayer craftplayer = (CraftPlayer) player;
        IChatBaseComponent tituloComponente = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + title + "\"}");
        IChatBaseComponent subtituloComponente = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + subtitle + "\"}");
        PacketPlayOutTitle tituloPaquete = new PacketPlayOutTitle(EnumTitleAction.TITLE, tituloComponente);
        PacketPlayOutTitle subtituloPaquete = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, subtituloComponente);
        PacketPlayOutTitle fadeinPaquete = new PacketPlayOutTitle(fadein, time, fadeout);
        craftplayer.getHandle().playerConnection.sendPacket(tituloPaquete);
        craftplayer.getHandle().playerConnection.sendPacket(subtituloPaquete);
        craftplayer.getHandle().playerConnection.sendPacket(fadeinPaquete);
    }
}
