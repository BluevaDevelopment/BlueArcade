package net.blueva.arcade.managers.setup;

import net.blueva.arcade.Main;
import net.blueva.arcade.managers.CacheManager;
import net.blueva.arcade.utils.StringUtils;
import org.bukkit.entity.Player;

public class KnockBackSetupManager {
    public static void finishSetup(Player player, Integer arenaid, Main main) {
        main.setupManager.actualStepGame.replace(player, 0);
        main.setupManager.selectedGame.remove(player);

        main.setupManager.maingame(player, arenaid);

        Integer cg = main.configManager.getArena(arenaid).getInt("arena.basic.configured_games")+1;
        main.configManager.getArena(arenaid).set("arena.basic.configured_games", cg);
        main.configManager.saveArena(arenaid);
        main.configManager.getArena(arenaid).set("arena.mini_games.knock_back.basic.enabled", true);
        main.configManager.saveArena(arenaid);
        main.configManager.getArena(arenaid).set("arena.mini_games.knock_back.basic.death_block",
                main.configManager.getSettings().getString("game.global.default_death_block"));
        main.configManager.saveArena(arenaid);
        main.configManager.reloadArena(arenaid);

        StringUtils.sendMessage(player, player.getName(), CacheManager.Language.GLOBAL_SUCCESS_MINI_GAME_SET.replace("{game}", "Knock Back"));
    }
}
