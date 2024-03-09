package net.blueva.arcade.managers;

import fr.mrmicky.fastboard.FastBoard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import net.blueva.arcade.Main;
import net.blueva.arcade.utils.ScoreboardUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ScoreboardManager {
    int taskID;
    private final Main main;
    private final Map<UUID, FastBoard> boards = new HashMap<>();
    public ScoreboardManager(Main main) {
        this.main = main;
    }

    public void CreateAllScoreboards() {
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        taskID = scheduler.scheduleSyncRepeatingTask(main, () -> {
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(PlayerManager.PlayerStatus.get(player).equals("Playing")) {
                    if(!boards.containsKey(player.getUniqueId())) {
                        FastBoard board = new FastBoard(player);
                        this.boards.put(player.getUniqueId(), board);
                    }
                }


                UpdateScoreboard(player, boards.get(player.getUniqueId()));
            }

        }, 0L, 10L);
    }

    public void UpdateScoreboard(Player player, FastBoard board) {

        if(PlayerManager.PlayerStatus.containsKey(player)) {
            if(PlayerManager.PlayerStatus.get(player).equals("Playing")) {
                String PlayerActualGame = ArenaManager.ArenaActualGame.get(PlayerManager.PlayerArena.get(player));
                if(PlayerActualGame.equalsIgnoreCase("Lobby")) {
                    board.updateTitle(ScoreboardUtil.format(player, CacheManager.Language.getScoreboardTitle("lobby_waiting")));
                    List<String> sblobbywaiting = CacheManager.Language.getScoreboardLines("lobby_waiting");
                    board.updateLines(ScoreboardUtil.format(player, sblobbywaiting));
                } else if (PlayerActualGame.equalsIgnoreCase("Starting")) {
                    board.updateTitle(ScoreboardUtil.format(player, CacheManager.Language.getScoreboardTitle("lobby_starting")));
                    List<String> sblobbystarting = CacheManager.Language.getScoreboardLines("lobby_starting");
                    board.updateLines(ScoreboardUtil.format(player, sblobbystarting));
                } else if (PlayerActualGame.equalsIgnoreCase("Limbo")) {
                    board.updateTitle(ScoreboardUtil.format(player, CacheManager.Language.getScoreboardTitle("limbo")));
                    List<String> sblimbo = CacheManager.Language.getScoreboardLines("limbo");
                    board.updateLines(ScoreboardUtil.format(player, sblimbo));
                } else if (PlayerActualGame.equalsIgnoreCase("Finish")) {
                    board.updateTitle(ScoreboardUtil.format(player, CacheManager.Language.getScoreboardTitle("finish")));
                    List<String> sbfinish = CacheManager.Language.getScoreboardLines("finish");
                    board.updateLines(ScoreboardUtil.format(player, sbfinish));
                } else if (PlayerActualGame.toLowerCase().contains("starting")) {
                    board.updateTitle(ScoreboardUtil.format(player, CacheManager.Language.getScoreboardTitle("in_game_starting")));
                    List<String> starting = CacheManager.Language.getScoreboardLines("in_game_starting");
                    board.updateLines(ScoreboardUtil.format(player, starting));
                } else if (PlayerActualGame.equalsIgnoreCase("Race")
                        || PlayerActualGame.equalsIgnoreCase("Spleef")
                        || PlayerActualGame.equalsIgnoreCase("SnowballFight")
                        || PlayerActualGame.equalsIgnoreCase("AllAgainstAll")
                        || PlayerActualGame.equalsIgnoreCase("Minefield")
                        || PlayerActualGame.equalsIgnoreCase("RedAlert")
                        || PlayerActualGame.equalsIgnoreCase("FastZone")) {
                    board.updateTitle(ScoreboardUtil.format(player, CacheManager.Language.getScoreboardTitle("in_game_playing_global")));
                    List<String> sbglobal = CacheManager.Language.getScoreboardLines("in_game_playing_global");
                    board.updateLines(ScoreboardUtil.format(player, sbglobal));
                } else if (PlayerActualGame.equalsIgnoreCase("OneInTheChamber")) {
                    board.updateTitle(ScoreboardUtil.format(player, CacheManager.Language.getScoreboardTitle("in_game_playing_one_in_the_chamber")));
                    List<String> sboitc = CacheManager.Language.getScoreboardLines("in_game_playing_one_in_the_chamber");
                    board.updateLines(ScoreboardUtil.format(player, sboitc));
                } else if (PlayerActualGame.equalsIgnoreCase("TrafficLight")) {
                    board.updateTitle(ScoreboardUtil.format(player, CacheManager.Language.getScoreboardTitle("in_game_playing_traffic_light")));
                    List<String> sbtrafficlight = CacheManager.Language.getScoreboardLines("in_game_playing_traffic_light");
                    board.updateLines(ScoreboardUtil.format(player, sbtrafficlight));
                } else if (PlayerActualGame.equalsIgnoreCase("ExplodingSheep")) {
                    board.updateTitle(ScoreboardUtil.format(player, CacheManager.Language.getScoreboardTitle("in_game_playing_exploding_sheep")));
                    List<String> sbexplodingsheep = CacheManager.Language.getScoreboardLines("in_game_playing_exploding_sheep");
                    board.updateLines(ScoreboardUtil.format(player, sbexplodingsheep));
                } else if (PlayerActualGame.equalsIgnoreCase("TNTTag")) {
                    board.updateTitle(ScoreboardUtil.format(player, CacheManager.Language.getScoreboardTitle("in_game_playing_tnt_tag")));
                    List<String> sbtnttag = CacheManager.Language.getScoreboardLines("in_game_playing_tnt_tag");
                    board.updateLines(ScoreboardUtil.format(player, sbtnttag));
                } else if (PlayerActualGame.equalsIgnoreCase("KnockBack")) {
                    board.updateTitle(ScoreboardUtil.format(player, CacheManager.Language.getScoreboardTitle("in_game_playing_knock_back")));
                    List<String> sbknockback = CacheManager.Language.getScoreboardLines("in_game_playing_knock_back");
                    board.updateLines(ScoreboardUtil.format(player, sbknockback));
                }
            } else {
                if(board != null) {
                    BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
                    scheduler.runTaskLater(main, () -> {
                        if(!board.isDeleted()) {
                            board.delete();
                        }
                        boards.remove(player.getUniqueId());
                    }, 20L);
                }
            }
        }
    }
}
