package net.blueva.arcade.managers.setup;

import net.blueva.arcade.Main;
import net.blueva.arcade.managers.CacheManager;
import net.blueva.arcade.utils.BlocksUtil;
import net.blueva.arcade.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class ExplodingSheepSetupManager {
    public static void finishSetup(Player player, Integer arenaid, Main main) {
        World world = Bukkit.getWorld(main.configManager.getArena(arenaid).getString("arena.mini_games.exploding_sheep.basic.world"));
        double boundsminx = main.configManager.getArena(arenaid).getDouble("arena.mini_games.exploding_sheep.bounds.min.x");
        double boundsminy = main.configManager.getArena(arenaid).getDouble("arena.mini_games.exploding_sheep.bounds.min.y");
        double boundsminz = main.configManager.getArena(arenaid).getDouble("arena.mini_games.exploding_sheep.bounds.min.z");
        Location boundsmin = new Location(world, boundsminx, boundsminy, boundsminz);

        double boundsmaxx = main.configManager.getArena(arenaid).getDouble("arena.mini_games.exploding_sheep.bounds.max.x");
        double boundsmaxy = main.configManager.getArena(arenaid).getDouble("arena.mini_games.exploding_sheep.bounds.max.y");
        double boundsmaxz = main.configManager.getArena(arenaid).getDouble("arena.mini_games.exploding_sheep.bounds.max.z");
        Location boundsmax = new Location(world, boundsmaxx, boundsmaxy, boundsmaxz);

        List<String> blocks = BlocksUtil.generateBlockList(boundsmin, boundsmax);
        main.configManager.getRegion("exploding_sheep", arenaid).set("blocks", blocks);
        main.configManager.saveRegion();

        main.setupManager.actualStepGame.replace(player, 0);
        main.setupManager.selectedGame.remove(player);

        main.setupManager.maingame(player, arenaid);



        Integer cg = main.configManager.getArena(arenaid).getInt("arena.basic.configured_games")+1;
        main.configManager.getArena(arenaid).set("arena.basic.configured_games", cg);
        main.configManager.saveArena(arenaid);
        main.configManager.getArena(arenaid).set("arena.mini_games.exploding_sheep.basic.enabled", true);
        main.configManager.saveArena(arenaid);
        main.configManager.getArena(arenaid).set("arena.mini_games.exploding_sheep.basic.death_block",
                main.configManager.getSettings().getString("game.global.default_death_block"));;
        main.configManager.saveArena(arenaid);
        main.configManager.getArena(arenaid).set("arena.mini_games.exploding_sheep.basic.explosion_level", 2.5);
        main.configManager.saveArena(arenaid);
        main.configManager.reloadArena(arenaid);

        StringUtils.sendMessage(player, player.getName(), CacheManager.Language.GLOBAL_SUCCESS_MINI_GAME_SET.replace("{game}", "Exploding Sheep"));
    }
}
