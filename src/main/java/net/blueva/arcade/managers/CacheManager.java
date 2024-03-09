package net.blueva.arcade.managers;

import net.blueva.arcade.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class CacheManager {

    public static class Arenas {

        public static Map<Integer, Map<String, String>> ARENA_BASIC_STRING = new HashMap<>();
        public static Map<Integer, Map<String, Integer>> ARENA_BASIC_INTEGER = new HashMap<>();
        public static Map<Integer, String> ARENA_WAITING_LOBBY_WORLD = new HashMap<>();
        public static Map<Integer, Map<String, Integer>> ARENA_WAITING_LOBBY_INT = new HashMap<>();
        public static Map<Integer, Map<String, Map<String, String>>> ARENA_MINIGAME_STRING = new HashMap<>();
        public static Map<Integer, String> ARENA_GAME_DISPLAY_NAME = new HashMap<>();

        public static void updateArenaBasic(Main main, int arenaid) {
            if (!ARENA_BASIC_STRING.containsKey(arenaid)) {
                ARENA_BASIC_STRING.put(arenaid, new HashMap<>());
            }
            if(!ARENA_BASIC_INTEGER.containsKey(arenaid)) {
                ARENA_BASIC_INTEGER.put(arenaid, new HashMap<>());
            }
            if(!ARENA_WAITING_LOBBY_WORLD.containsKey(arenaid)) {
                ARENA_WAITING_LOBBY_WORLD.put(arenaid, ""); // sin usar
            }
            if(!ARENA_WAITING_LOBBY_INT.containsKey(arenaid)) {
                ARENA_WAITING_LOBBY_INT.put(arenaid, new HashMap<>()); // sin usar
            }
            if(!ARENA_MINIGAME_STRING.containsKey(arenaid)) {
                ARENA_MINIGAME_STRING.put(arenaid, new HashMap<>());
            }

            Map<String, Integer> arenaBasicInt = ARENA_BASIC_INTEGER.get(arenaid);
            if(!arenaBasicInt.containsKey("id")) {
                arenaBasicInt.put("id", main.configManager.getArena(arenaid).getInt("arena.basic.id"));
                arenaBasicInt.put("min_players", main.configManager.getArena(arenaid).getInt("arena.basic.min_players"));
                arenaBasicInt.put("max_players", main.configManager.getArena(arenaid).getInt("arena.basic.max_players"));
                arenaBasicInt.put("configured_games", main.configManager.getArena(arenaid).getInt("arena.basic.configured_games"));
                arenaBasicInt.put("setup_step", main.configManager.getArena(arenaid).getInt("arena.basic.setup_step"));
                arenaBasicInt.put("rounds", main.configManager.getArena(arenaid).getInt("arena.basic.rounds"));
            }

            Map<String, String> arenaBasicString = ARENA_BASIC_STRING.get(arenaid);
            if(!arenaBasicString.containsKey("state")) {
                arenaBasicString.put("state", main.configManager.getArena(arenaid).getString("arena.basic.state"));
                arenaBasicString.put("display_name", main.configManager.getArena(arenaid).getString("arena.basic.display_name"));
            }

            Map<String, Map<String, String>> arenaMinigameString = ARENA_MINIGAME_STRING.get(arenaid);
            if(main.configManager.getArena(arenaid).getConfigurationSection("arena.mini_games") != null) {
                ConfigurationSection cs = main.configManager.getArena(arenaid).getConfigurationSection("arena.mini_games");
                if(cs != null) {
                    for(String mini_game : cs.getKeys(false)) {
                        if(main.configManager.getArena(arenaid).getBoolean("arena.mini_games."+mini_game+".basic.enabled")) {
                            if(!arenaMinigameString.containsKey(mini_game)) {
                                arenaMinigameString.put(mini_game, new HashMap<>());
                            }

                            Map<String, String> arenaMiniGameFinal = arenaMinigameString.get(mini_game);
                            if(!arenaMiniGameFinal.containsKey(mini_game)) {
                                arenaMiniGameFinal.put("death_block", main.configManager.getArena(arenaid).getString("arena.mini_games."+mini_game+".basic.death_block"));
                            }
                        }
                    }
                }
            }
        }

        public static void updateDisplayName(int arenaid) {
            if (!ARENA_GAME_DISPLAY_NAME.containsKey(arenaid)) {
                ARENA_GAME_DISPLAY_NAME.put(arenaid, "None");
            }

            String arenaStatus = ArenaManager.ArenaActualGame.get(arenaid);
            if(arenaStatus.equalsIgnoreCase("StartingRace") ||
                    arenaStatus.equalsIgnoreCase("Race")) {
                ARENA_GAME_DISPLAY_NAME.replace(arenaid, Language.MINI_GAMES_RACE_DISPLAY_NAME);
            } else if(arenaStatus.equalsIgnoreCase("StartingSpleef") ||
                    arenaStatus.equalsIgnoreCase("Spleef")) {
                ARENA_GAME_DISPLAY_NAME.replace(arenaid, Language.MINI_GAMES_SPLEEF_DISPLAY_NAME);
            } else if(arenaStatus.equalsIgnoreCase("StartingSnowballFight") ||
                    arenaStatus.equalsIgnoreCase("SnowballFight")) {
                ARENA_GAME_DISPLAY_NAME.replace(arenaid, Language.MINI_GAMES_SNOWBALL_FIGHT_DISPLAY_NAME);
            } else if(arenaStatus.equalsIgnoreCase("StartingAllAgainstAll") ||
                    arenaStatus.equalsIgnoreCase("AllAgainstAll")) {
                ARENA_GAME_DISPLAY_NAME.replace(arenaid, Language.MINI_GAMES_ALL_AGAINST_ALL_DISPLAY_NAME);
            } else if(arenaStatus.equalsIgnoreCase("StartingOneInTheChamber") ||
                    arenaStatus.equalsIgnoreCase("OneInTheChamber")) {
                ARENA_GAME_DISPLAY_NAME.replace(arenaid, Language.MINI_GAMES_ONE_IN_THE_CHAMBER_DISPLAY_NAME);
            } else if(arenaStatus.equalsIgnoreCase("StartingTrafficLight") ||
                    arenaStatus.equalsIgnoreCase("TrafficLight")) {
                ARENA_GAME_DISPLAY_NAME.replace(arenaid, Language.MINI_GAMES_TRAFFIC_LIGHT_DISPLAY_NAME);
            } else if(arenaStatus.equalsIgnoreCase("StartingMinefield") ||
                    arenaStatus.equalsIgnoreCase("Minefield")) {
                ARENA_GAME_DISPLAY_NAME.replace(arenaid, Language.MINI_GAMES_MINEFIELD_DISPLAY_NAME);
            } else if(arenaStatus.equalsIgnoreCase("StartingExplodingSheep") ||
                    arenaStatus.equalsIgnoreCase("ExplodingSheep")) {
                ARENA_GAME_DISPLAY_NAME.replace(arenaid, Language.MINI_GAMES_EXPLODING_SHEEP_DISPLAY_NAME);
            } else if(arenaStatus.equalsIgnoreCase("StartingTNTTag") ||
                    arenaStatus.equalsIgnoreCase("TNTTag")) {
                ARENA_GAME_DISPLAY_NAME.replace(arenaid, Language.MINI_GAMES_TNT_TAG_DISPLAY_NAME);
            } else if(arenaStatus.equalsIgnoreCase("StartingRedAlert") ||
                    arenaStatus.equalsIgnoreCase("RedAlert")) {
                ARENA_GAME_DISPLAY_NAME.replace(arenaid, Language.MINI_GAMES_RED_ALERT_DISPLAY_NAME);
            } else if(arenaStatus.equalsIgnoreCase("StartingKnockBack") ||
                    arenaStatus.equalsIgnoreCase("KnockBack")) {
                ARENA_GAME_DISPLAY_NAME.replace(arenaid, Language.MINI_GAMES_KNOCK_BACK_DISPLAY_NAME);
            } else if(arenaStatus.equalsIgnoreCase("StartingFastZone") ||
                    arenaStatus.equalsIgnoreCase("FastZone")) {
                ARENA_GAME_DISPLAY_NAME.replace(arenaid, Language.MINI_GAMES_FAST_ZONE_DISPLAY_NAME);
            }
        }

    }

    public static class Settings {
        public static int NO_MODIFY = 0;
        public static boolean METRICS;
        public static String LANGUAGE = "";
        public static boolean CHECK_FOR_UPDATES;
        public static boolean SPAWN_ON_JOIN;
        public static int GAME_GLOBAL_LOBBY_COUNTDOWN = 0;
        public static int GAME_GLOBAL_GAME_COUNTDOWN = 0;
        public static int GAME_GLOBAL_LIMBO_COUNTDOWN;
        public static List<Integer> GAME_GLOBAL_COUNTDOWN_NOTIFICATIONS = new ArrayList<>();
        public static int GAME_GLOBAL_DEFAULT_ROUNDS_PER_ARENA = 0;
        public static boolean GAME_GLOBAL_FLIGHT_ON_FINISH;
        public static boolean GAME_GLOBAL_SPAWN_FIREWORKS_WINNERS;
        public static boolean GAME_GLOBAL_CANCEL_COMMANDS_ENABLED;
        public static List<String> GAME_GLOBAL_CANCEL_COMMANDS_ALLOWED_COMMANDS = new ArrayList<>();

        public static void updateSettingsString (Main main) {
            NO_MODIFY = main.configManager.getSettings().getInt("no_modify");
            METRICS = main.configManager.getSettings().getBoolean("metrics");
            LANGUAGE = main.configManager.getSettings().getString("language");
            CHECK_FOR_UPDATES = main.configManager.getSettings().getBoolean("check_for_updates");
            SPAWN_ON_JOIN = main.configManager.getSettings().getBoolean("spawn_on_join");
            GAME_GLOBAL_LOBBY_COUNTDOWN = main.configManager.getSettings().getInt("game.global.lobby_countdown");
            GAME_GLOBAL_GAME_COUNTDOWN = main.configManager.getSettings().getInt("game.global.game_countdown");
            GAME_GLOBAL_LIMBO_COUNTDOWN = main.configManager.getSettings().getInt("game.global.limbo_countdown");
            GAME_GLOBAL_COUNTDOWN_NOTIFICATIONS = main.configManager.getSettings().getIntegerList("game.global.countdown_notifications");
            GAME_GLOBAL_DEFAULT_ROUNDS_PER_ARENA = main.configManager.getSettings().getInt("game.global.default_rounds_per_arena");
            GAME_GLOBAL_FLIGHT_ON_FINISH = main.configManager.getSettings().getBoolean("game.global.flight_on_finish");
            GAME_GLOBAL_SPAWN_FIREWORKS_WINNERS = main.configManager.getSettings().getBoolean("game.global.spawn_fireworks_winners");
            GAME_GLOBAL_CANCEL_COMMANDS_ENABLED = main.configManager.getSettings().getBoolean("game.global.cancel_commands.enabled");
            GAME_GLOBAL_CANCEL_COMMANDS_ALLOWED_COMMANDS = main.configManager.getSettings().getStringList("game.global.cancel_commands.allowed_commands");

        }

    }

    public static class Sounds {
        public static String SOUNDS_STARTING_ARENA_COUNTDOWN;
        public static String SOUNDS_STARTING_ARENA_NOTIFY_COUNTDOWN;
        public static String SOUNDS_STARTING_GAME_TELEPORT;
        public static String SOUNDS_STARTING_GAME_COUNTDOWN;
        public static String SOUNDS_STARTING_GAME_START;
        public static String SOUNDS_IN_GAME_CLASSIFIED;
        public static String SOUNDS_IN_GAME_FINISH;
        public static String SOUNDS_IN_GAME_DEAD;
        public static String SOUNDS_IN_GAME_RESPAWN;
        public static String SOUNDS_IN_GAME_GAME_OVER;

        public static void updateSoundsString(Main main) {
            SOUNDS_STARTING_ARENA_COUNTDOWN = main.configManager.getSounds().getString("sounds.starting_arena.countdown");
            SOUNDS_STARTING_ARENA_NOTIFY_COUNTDOWN = main.configManager.getSounds().getString("sounds.starting_arena.notify_countdown");
            SOUNDS_STARTING_GAME_TELEPORT = main.configManager.getSounds().getString("sounds.starting_game.teleport");
            SOUNDS_STARTING_GAME_COUNTDOWN = main.configManager.getSounds().getString("sounds.starting_game.countdown");
            SOUNDS_STARTING_GAME_START = main.configManager.getSounds().getString("sounds.starting_game.start");
            SOUNDS_IN_GAME_CLASSIFIED = main.configManager.getSounds().getString("sounds.in_game.classified");
            SOUNDS_IN_GAME_FINISH = main.configManager.getSounds().getString("sounds.in_game.finish");
            SOUNDS_IN_GAME_DEAD = main.configManager.getSounds().getString("sounds.in_game.dead");
            SOUNDS_IN_GAME_RESPAWN = main.configManager.getSounds().getString("sounds.in_game.respawn");
            SOUNDS_IN_GAME_GAME_OVER = main.configManager.getSounds().getString("sounds.in_game.game_over");
        }

    }

    public static class Bounds {
        private static final Map<Integer, Map<String, Map<String, Double>>> ArenaBounds = new HashMap<>();
        private static final Map<Integer, Map<String, Map<String, Double>>> ArenaFinishBounds = new HashMap<>();
        private static final Map<Integer, Map<String, String>> ArenaWorld = new HashMap<>();

        public static void saveBounds(Main main, int arenaid, String minigame) {
            if (!ArenaBounds.containsKey(arenaid)) {
                ArenaBounds.put(arenaid, new HashMap<>());
            }
            if(!ArenaWorld.containsKey(arenaid)) {
                ArenaWorld.put(arenaid, new HashMap<>());
            }

            Map<String, String> arenaWorld = ArenaWorld.get(arenaid);
            if(!arenaWorld.containsKey(minigame)) {
                String world = main.configManager.getArena(arenaid).getString("arena.mini_games."+minigame+".basic.world");
                arenaWorld.put(minigame, world);
            }

            Map<String, Map<String, Double>> arenaBounds = ArenaBounds.get(arenaid);
            if (!arenaBounds.containsKey(minigame)) {
                double boundsminx = main.configManager.getArena(arenaid).getDouble("arena.mini_games."+minigame+".bounds.min.x");
                double boundsminy = main.configManager.getArena(arenaid).getDouble("arena.mini_games."+minigame+".bounds.min.y");
                double boundsminz = main.configManager.getArena(arenaid).getDouble("arena.mini_games."+minigame+".bounds.min.z");
                double boundsmaxx = main.configManager.getArena(arenaid).getDouble("arena.mini_games."+minigame+".bounds.max.x");
                double boundsmaxy = main.configManager.getArena(arenaid).getDouble("arena.mini_games."+minigame+".bounds.max.y");
                double boundsmaxz = main.configManager.getArena(arenaid).getDouble("arena.mini_games."+minigame+".bounds.max.z");

                Map<String, Double> minigameBounds = new HashMap<>();
                minigameBounds.put("min.x", boundsminx);
                minigameBounds.put("min.y", boundsminy);
                minigameBounds.put("min.z", boundsminz);
                minigameBounds.put("max.x", boundsmaxx);
                minigameBounds.put("max.y", boundsmaxy);
                minigameBounds.put("max.z", boundsmaxz);

                arenaBounds.put(minigame, minigameBounds);
            }
        }

        public static Location getBounds(Main main, int arenaid, String minigame, String bound) {
            if (ArenaBounds.containsKey(arenaid)) {
                Map<String, Double> boundMap = ArenaBounds.get(arenaid).get(minigame);
                if (boundMap != null) {
                    double x = boundMap.get(bound+".x");
                    double y = boundMap.get(bound+".y");
                    double z = boundMap.get(bound+".z");
                    World world = Bukkit.getWorld(ArenaWorld.get(arenaid).get(minigame));
                    return new Location(world, x, y, z);
                }
            }

            // Si no se encuentra en el HashMap, se lee de la configuración
            Bukkit.getConsoleSender().sendMessage("Error al leer los bordes precargados [Arena: "+ arenaid+ "] [Minijuego: "+ minigame +"]");
            double x = main.configManager.getArena(arenaid).getDouble("arena.mini_games."+minigame+".bounds."+bound+".x");
            double y = main.configManager.getArena(arenaid).getDouble("arena.mini_games."+minigame+".bounds."+bound+".y");
            double z = main.configManager.getArena(arenaid).getDouble("arena.mini_games."+minigame+".bounds."+bound+".z");
            World world = Bukkit.getWorld(Objects.requireNonNull(main.configManager.getArena(arenaid).getString("arena.mini_games." + minigame + ".basic.world")));
            return new Location(world, x, y, z);
        }

        public static void saveFinishBounds(Main main, int arenaid, String minigame) {
            if (!ArenaFinishBounds.containsKey(arenaid)) {
                ArenaFinishBounds.put(arenaid, new HashMap<>());
            }
            if(!ArenaWorld.containsKey(arenaid)) {
                ArenaWorld.put(arenaid, new HashMap<>());
            }

            Map<String, String> arenaWorld = ArenaWorld.get(arenaid);
            if(!arenaWorld.containsKey(minigame)) {
                String world = main.configManager.getArena(arenaid).getString("arena.mini_games."+minigame+".basic.world");
                arenaWorld.put(minigame, world);
            }

            Map<String, Map<String, Double>> arenaBounds = ArenaFinishBounds.get(arenaid);
            if (!arenaBounds.containsKey(minigame)) {
                double boundsminx = main.configManager.getArena(arenaid).getDouble("arena.mini_games." + minigame + ".game.finish_line.bounds.min.x");
                double boundsminy = main.configManager.getArena(arenaid).getDouble("arena.mini_games." + minigame + ".game.finish_line.bounds.min.y");
                double boundsminz = main.configManager.getArena(arenaid).getDouble("arena.mini_games." + minigame + ".game.finish_line.bounds.min.z");
                double boundsmaxx = main.configManager.getArena(arenaid).getDouble("arena.mini_games." + minigame + ".game.finish_line.bounds.max.x");
                double boundsmaxy = main.configManager.getArena(arenaid).getDouble("arena.mini_games." + minigame + ".game.finish_line.bounds.max.y");
                double boundsmaxz = main.configManager.getArena(arenaid).getDouble("arena.mini_games." + minigame + ".game.finish_line.bounds.max.z");

                Map<String, Double> minigameBounds = new HashMap<>();
                minigameBounds.put("min.x", boundsminx);
                minigameBounds.put("min.y", boundsminy);
                minigameBounds.put("min.z", boundsminz);
                minigameBounds.put("max.x", boundsmaxx);
                minigameBounds.put("max.y", boundsmaxy);
                minigameBounds.put("max.z", boundsmaxz);

                arenaBounds.put(minigame, minigameBounds);
            }
        }

        public static Location getFinishBounds(Main main, int arenaid, String minigame, String bound) {
            if (ArenaFinishBounds.containsKey(arenaid)) {
                Map<String, Double> boundMap = ArenaFinishBounds.get(arenaid).get(minigame);
                if (boundMap != null) {
                    double x = boundMap.get(bound+".x");
                    double y = boundMap.get(bound+".y");
                    double z = boundMap.get(bound+".z");
                    World world = Bukkit.getWorld(ArenaWorld.get(arenaid).get(minigame));
                    return new Location(world, x, y, z);
                }
            }

            // Si no se encuentra en el HashMap, se lee de la configuración
            Bukkit.getConsoleSender().sendMessage("Error al leer los bordes precargados [Arena: "+ arenaid+ "] [Minijuego: "+ minigame +"]");
            double x = main.configManager.getArena(arenaid).getDouble("arena.mini_games." + minigame + ".game.finish_line.bounds." + bound + ".x");
            double y = main.configManager.getArena(arenaid).getDouble("arena.mini_games." + minigame + ".game.finish_line.bounds." + bound + ".y");
            double z = main.configManager.getArena(arenaid).getDouble("arena.mini_games." + minigame + ".game.finish_line.bounds." + bound + ".z");
            World world = Bukkit.getWorld(Objects.requireNonNull(main.configManager.getArena(arenaid).getString("arena.mini_games." + minigame + ".basic.world")));
            return new Location(world, x, y, z);
        }

        public static void removeBoundsCache(int arenaid) {
            ArenaBounds.remove(arenaid);
            ArenaFinishBounds.remove(arenaid);
        }
    }

    public static class Language {
        public static String LANGUAGE = "";
        public static String GLOBAL_SUCCESS_ARENA_CREATED = "";
        public static String GLOBAL_SUCCESS_ARENA_REMOVED = "";
        public static String GLOBAL_SUCCESS_DISPLAY_NAME_SET = "";
        public static String GLOBAL_SUCCESS_MAIN_LOBBY_SET = "";
        public static String GLOBAL_SUCCESS_WAITING_LOBBY_SET = "";
        public static String GLOBAL_SUCCESS_MAGIC_STICK_POS = "";
        public static String GLOBAL_SUCCESS_MIN_PLAYERS_SET = "";
        public static String GLOBAL_SUCCESS_MAX_PLAYERS_SET = "";
        public static String GLOBAL_SUCCESS_MINI_GAME_SET = "";
        public static String GLOBAL_SUCCESS_SPAWN_ADDED = "";
        public static String GLOBAL_SUCCESS_SPAWN_REMOVED = "";
        public static String GLOBAL_SUCCESS_SETUP_FINISHED = "";
        public static String GLOBAL_SUCCESS_SIGN_ADDED = "";
        public static String GLOBAL_SUCCESS_SIGN_REMOVED = "";
        public static String GLOBAL_SUCCESS_TELEPORTED_TO_GAME = "";
        public static String GLOBAL_SUCCESS_RELOADED_FILE = "";
        public static String GLOBAL_SUCCESS_ALL_FILES_RELOADED = "";
        public static String GLOBAL_ERROR_ID_TAKEN = "";
        public static String GLOBAL_ERROR_UNKNOWN_ID = "";
        public static String GLOBAL_ERROR_NO_ARENA = "";
        public static String GLOBAL_ERROR_ONLY_NUMBERS = "";
        public static String GLOBAL_ERROR_ONLY_NUMBERS_ARENA = "";
        public static String GLOBAL_ERROR_ONLY_PLAYERS = "";
        public static String GLOBAL_ERROR_ERROR_DELETING_FILE = "";
        public static String GLOBAL_ERROR_ALREADY_IN_GAME = "";
        public static String GLOBAL_ERROR_NOT_IN_GAME = "";
        public static String GLOBAL_ERROR_NO_MINI_GAME = "";
        public static String GLOBAL_ERROR_ARENA_STARTED = "";
        public static String GLOBAL_ERROR_ARENA_FULL = "";
        public static String GLOBAL_ERROR_ARENA_DISABLED = "";
        public static String GLOBAL_ERROR_ARENA_STOPPED = "";
        public static String GLOBAL_ERROR_BOUNDS_SAME_WORLD = "";
        public static String GLOBAL_ERROR_CHAT_MUTED = "";
        public static String GLOBAL_ERROR_NOT_ALLOWED_COMMAND = "";
        public static String GLOBAL_ERROR_AT_LEAST_ONE_GAME = "";
        public static String GLOBAL_ERROR_MINIMUM_SPAWNS = "";
        public static String GLOBAL_ERROR_FINISH_FIRST = "";
        public static String GLOBAL_ERROR_NO_SETUP_PAUSE = "";
        public static String GLOBAL_ERROR_NO_SETUP_CANCEL = "";
        public static String GLOBAL_ERROR_AIR_SELECTION_ERROR = "";
        public static String GLOBAL_ERROR_PROCESS_CANCELLED = "";
        public static String GLOBAL_ERROR_PLAYER_OFFLINE = "";
        public static String GLOBAL_ERROR_INSUFFICIENT_PERMISSIONS = "";
        public static String GLOBAL_ERROR_ERROR_SIGN = "";
        public static String GLOBAL_ERROR_ERROR_RELOADING = "";
        public static String GLOBAL_ERROR_UNKNOWN_COMMAND = "";
        public static String GLOBAL_INFO_SETS_SPAWN = "";
        public static String GLOBAL_INFO_YOU_JOIN = "";
        public static String GLOBAL_INFO_YOU_LEFT = "";
        public static String GLOBAL_INFO_PLAYER_JOINED = "";
        public static String GLOBAL_INFO_PLAYER_LEAVES = "";
        public static String GLOBAL_INFO_LOBBY_COUNTDOWN = "";
        public static String GLOBAL_INFO_YOU_HAVE_TNT = "";
        public static String GLOBAL_INFO_YOU_NO_HAVE_TNT = "";
        public static String GLOBAL_INFO_PLAYER_HAS_THE_TNT = "";
        public static String GLOBAL_INFO_CANCELLED_SETUP = "";
        public static String GLOBAL_INFO_PAUSED_SETUP = "";
        public static String GLOBAL_INFO_SELECTED_MINI_GAME_SETUP = "";
        public static String GLOBAL_INFO_SELECTION_ELIMINATED_SETUP = "";
        public static String GLOBAL_INFO_SET_DISPLAY_NAME = "";
        public static String GLOBAL_INFO_SETUP_STEP_1 = "";
        public static String GLOBAL_INFO_SETUP_STEP_2 = "";
        public static String GLOBAL_INFO_SETUP_STEP_3 = "";
        public static String GLOBAL_INFO_SETUP_STEP_4 = "";
        public static String GLOBAL_INFO_SETUP_STEP_5 = "";
        public static String GLOBAL_INFO_SETUP_STEP_6 = "";
        public static String GLOBAL_INFO_SETUP_STEP_7 = "";
        public static String GLOBAL_INFO_SETUP_RACE_STEP_1 = "";
        public static String GLOBAL_INFO_SETUP_FAST_ZONE_STEP_1 = "";
        public static String GLOBAL_INFO_SETUP_SPLEEF_STEP_1 = "";
        public static String GLOBAL_INFO_SETUP_TRAFFIC_LIGHT_STEP_1 = "";
        public static String GLOBAL_INFO_SETUP_MINEFIELD_STEP_1 = "";
        public static String GLOBAL_INFO_SETUP_MINEFIELD_STEP_2 = "";
        public static String GLOBAL_INFO_SETUP_RED_ALERT_STEP_1 = "";
        public static String GLOBAL_OTHER_USE_GOTO_SUBCOMMAND = "";
        public static String GLOBAL_OTHER_USE_HELP_SUBCOMMAND = "";
        public static String GLOBAL_OTHER_USE_JOIN_SUBCOMMAND = "";
        public static String GLOBAL_OTHER_USE_QUICKJOIN_SUBCOMMAND = "";
        public static String GLOBAL_OTHER_USE_LEAVE_SUBCOMMAND = "";
        public static String GLOBAL_OTHER_USE_REMOVE_SUBCOMMAND = "";
        public static String GLOBAL_OTHER_USE_SETUP_SUBCOMMAND = "";
        public static String GLOBAL_OTHER_USE_RELOAD_SUBCOMMAND = "";

        public static List<String> SUMMARY_MINI_GAME = new ArrayList<>();
        public static List<String> SUMMARY_FINAL = new ArrayList<>();

        public static String MINI_GAMES_RACE_DISPLAY_NAME = "";
        public static List<String> MINI_GAME_RACE_DESCRIPTION = new ArrayList<>();
        public static String MINI_GAMES_SPLEEF_DISPLAY_NAME = "";
        public static List<String> MINI_GAME_SPLEEF_DESCRIPTION = new ArrayList<>();
        public static String MINI_GAMES_SNOWBALL_FIGHT_DISPLAY_NAME = "";
        public static List<String> MINI_GAME_SNOWBALL_FIGHT_DESCRIPTION = new ArrayList<>();
        public static String MINI_GAMES_ALL_AGAINST_ALL_DISPLAY_NAME = "";
        public static List<String> MINI_GAME_ALL_AGAINST_ALL_DESCRIPTION = new ArrayList<>();
        public static String MINI_GAMES_ONE_IN_THE_CHAMBER_DISPLAY_NAME = "";
        public static List<String> MINI_GAME_ONE_IN_THE_CHAMBER_DESCRIPTION = new ArrayList<>();
        public static String MINI_GAMES_TRAFFIC_LIGHT_DISPLAY_NAME = "";
        public static List<String> MINI_GAME_TRAFFIC_LIGHT_DESCRIPTION = new ArrayList<>();
        public static String MINI_GAMES_MINEFIELD_DISPLAY_NAME = "";
        public static List<String> MINI_GAME_MINEFIELD_DESCRIPTION = new ArrayList<>();
        public static String MINI_GAMES_EXPLODING_SHEEP_DISPLAY_NAME = "";
        public static List<String> MINI_GAME_EXPLODING_SHEEP_DESCRIPTION = new ArrayList<>();
        public static String MINI_GAMES_TNT_TAG_DISPLAY_NAME = "";
        public static List<String> MINI_GAME_TNT_TAG_DESCRIPTION = new ArrayList<>();
        public static String MINI_GAMES_RED_ALERT_DISPLAY_NAME = "";
        public static List<String> MINI_GAME_RED_ALERT_DESCRIPTION = new ArrayList<>();
        public static String MINI_GAMES_KNOCK_BACK_DISPLAY_NAME = "";
        public static List<String> MINI_GAME_KNOCK_BACK_DESCRIPTION = new ArrayList<>();
        public static String MINI_GAMES_FAST_ZONE_DISPLAY_NAME = "";
        public static List<String> MINI_GAME_FAST_ZONE_DESCRIPTION = new ArrayList<>();

        public static String ACTION_BAR_LOBBY_STARTING = "";
        public static String ACTION_BAR_IN_GAME_GLOBAL = "";

        public static String TITLES_JOIN_TITLE = "";
        public static String TITLES_JOIN_SUBTITLE = "";
        public static String TITLES_STARTING_ARENA_TITLE = "";
        public static String TITLES_STARTING_ARENA_SUBTITLE = "";
        public static String TITLES_STARTING_GAME_TITLE = "";
        public static String TITLES_STARTING_GAME_SUBTITLE = "";
        public static String TITLES_GAME_STARTED_TITLE = "";
        public static String TITLES_GAME_STARTED_SUBTITLE = "";
        public static String TITLES_YOU_DIED_TITLE = "";
        public static String TITLES_YOU_DIED_SUBTITLE = "";
        public static String TITLES_CLASSIFIED_TITLE = "";
        public static String TITLES_CLASSIFIED_SUBTITLE = "";

        public static String SIGNS_ARENA_LINE1 = "";
        public static String SIGNS_ARENA_LINE2 = "";
        public static String SIGNS_ARENA_LINE3 = "";
        public static String SIGNS_ARENA_LINE4 = "";
        public static String SIGNS_QUICKJOIN_LINE1 = "";
        public static String SIGNS_QUICKJOIN_LINE2 = "";
        public static String SIGNS_QUICKJOIN_LINE3 = "";
        public static String SIGNS_QUICKJOIN_LINE4 = "";
        public static String SIGNS_LEAVE_LINE1 = "";
        public static String SIGNS_LEAVE_LINE2 = "";
        public static String SIGNS_LEAVE_LINE3 = "";
        public static String SIGNS_LEAVE_LINE4 = "";


        public static List<String> COMMANDS_BLUEARCADE_INFO = new ArrayList<>();
        public static List<String> COMMANDS_BLUEARCADE_HELP = new ArrayList<>();
        public static List<String> COMMANDS_BLUEARCADE_ADMINHELP = new ArrayList<>();

        public static void updateLanguageString(Main main) {
            LANGUAGE = main.configManager.getLang().getString("language");
            GLOBAL_SUCCESS_ARENA_CREATED = main.configManager.getLang().getString("global.success.arena_created");
            GLOBAL_SUCCESS_ARENA_REMOVED = main.configManager.getLang().getString("global.success.arena_removed");
            GLOBAL_SUCCESS_DISPLAY_NAME_SET = main.configManager.getLang().getString("global.success.display_name_set");
            GLOBAL_SUCCESS_MAIN_LOBBY_SET = main.configManager.getLang().getString("global.success.main_lobby_set");
            GLOBAL_SUCCESS_WAITING_LOBBY_SET = main.configManager.getLang().getString("global.success.waiting_lobby_set");
            GLOBAL_SUCCESS_MAGIC_STICK_POS = main.configManager.getLang().getString("global.success.magic_stick_pos");
            GLOBAL_SUCCESS_MIN_PLAYERS_SET = main.configManager.getLang().getString("global.success.min_players_set");
            GLOBAL_SUCCESS_MAX_PLAYERS_SET = main.configManager.getLang().getString("global.success.max_players_set");
            GLOBAL_SUCCESS_MINI_GAME_SET = main.configManager.getLang().getString("global.success.mini_game_set");
            GLOBAL_SUCCESS_SPAWN_ADDED = main.configManager.getLang().getString("global.success.spawn_added");
            GLOBAL_SUCCESS_SPAWN_REMOVED = main.configManager.getLang().getString("global.success.spawn_removed");
            GLOBAL_SUCCESS_SETUP_FINISHED = main.configManager.getLang().getString("global.success.setup_finished");
            GLOBAL_SUCCESS_SIGN_ADDED = main.configManager.getLang().getString("global.success.sign_added");
            GLOBAL_SUCCESS_SIGN_REMOVED = main.configManager.getLang().getString("global.success.sign_removed");
            GLOBAL_SUCCESS_TELEPORTED_TO_GAME = main.configManager.getLang().getString("global.success.teleported_to_game");
            GLOBAL_SUCCESS_RELOADED_FILE = main.configManager.getLang().getString("global.success.reloaded_file");
            GLOBAL_SUCCESS_ALL_FILES_RELOADED = main.configManager.getLang().getString("global.success.all_files_reloaded");
            GLOBAL_ERROR_ID_TAKEN = main.configManager.getLang().getString("global.error.id_taken");
            GLOBAL_ERROR_UNKNOWN_ID = main.configManager.getLang().getString("global.error.unknown_id");
            GLOBAL_ERROR_NO_ARENA = main.configManager.getLang().getString("global.error.no_arena");
            GLOBAL_ERROR_ONLY_NUMBERS = main.configManager.getLang().getString("global.error.only_numbers");
            GLOBAL_ERROR_ONLY_NUMBERS_ARENA = main.configManager.getLang().getString("global.error.only_numbers_arena");
            GLOBAL_ERROR_ONLY_PLAYERS = main.configManager.getLang().getString("global.error.only_players");
            GLOBAL_ERROR_ERROR_DELETING_FILE = main.configManager.getLang().getString("global.error.error_deleting_file");
            GLOBAL_ERROR_ALREADY_IN_GAME = main.configManager.getLang().getString("global.error.already_in_game");
            GLOBAL_ERROR_NOT_IN_GAME = main.configManager.getLang().getString("global.error.not_in_game");
            GLOBAL_ERROR_NO_MINI_GAME = main.configManager.getLang().getString("global.error.no_mini_game");
            GLOBAL_ERROR_ARENA_STARTED = main.configManager.getLang().getString("global.error.arena_started");
            GLOBAL_ERROR_ARENA_FULL = main.configManager.getLang().getString("global.error.arena_full");
            GLOBAL_ERROR_ARENA_DISABLED = main.configManager.getLang().getString("global.error.arena_disabled");
            GLOBAL_ERROR_ARENA_STOPPED = main.configManager.getLang().getString("global.error.arena_stopped");
            GLOBAL_ERROR_BOUNDS_SAME_WORLD = main.configManager.getLang().getString("global.error.bounds_same_world");
            GLOBAL_ERROR_CHAT_MUTED = main.configManager.getLang().getString("global.error.chat_muted");
            GLOBAL_ERROR_NOT_ALLOWED_COMMAND = main.configManager.getLang().getString("global.error.not_allowed_command");
            GLOBAL_ERROR_AT_LEAST_ONE_GAME = main.configManager.getLang().getString("global.error.at_least_one_game");
            GLOBAL_ERROR_MINIMUM_SPAWNS = main.configManager.getLang().getString("global.error.minimum_spawns");
            GLOBAL_ERROR_FINISH_FIRST = main.configManager.getLang().getString("global.error.finish_first");
            GLOBAL_ERROR_NO_SETUP_PAUSE = main.configManager.getLang().getString("global.error.no_setup_pause");
            GLOBAL_ERROR_NO_SETUP_CANCEL = main.configManager.getLang().getString("global.error.no_setup_cancel");
            GLOBAL_ERROR_AIR_SELECTION_ERROR = main.configManager.getLang().getString("global.error.air_selection_error");
            GLOBAL_ERROR_PROCESS_CANCELLED = main.configManager.getLang().getString("global.error.process_cancelled");
            GLOBAL_ERROR_PLAYER_OFFLINE = main.configManager.getLang().getString("global.error.player_offline");
            GLOBAL_ERROR_INSUFFICIENT_PERMISSIONS = main.configManager.getLang().getString("global.error.insufficient_permissions");
            GLOBAL_ERROR_ERROR_SIGN = main.configManager.getLang().getString("global.error.error_sign");
            GLOBAL_ERROR_ERROR_RELOADING = main.configManager.getLang().getString("global.error.error_reloading");
            GLOBAL_ERROR_UNKNOWN_COMMAND = main.configManager.getLang().getString("global.error.unknown_command");
            GLOBAL_INFO_SETS_SPAWN = main.configManager.getLang().getString("global.info.sets_spawn");
            GLOBAL_INFO_YOU_JOIN = main.configManager.getLang().getString("%%__USER__%%");
            GLOBAL_INFO_YOU_JOIN = main.configManager.getLang().getString("%%__RESOURCE__%%");
            GLOBAL_INFO_YOU_JOIN = main.configManager.getLang().getString("%%__NONCE__%%");
            GLOBAL_INFO_YOU_JOIN = main.configManager.getLang().getString("global.info.you_join");
            GLOBAL_INFO_YOU_LEFT = main.configManager.getLang().getString("global.info.you_left");
            GLOBAL_INFO_PLAYER_JOINED = main.configManager.getLang().getString("global.info.player_joined");
            GLOBAL_INFO_PLAYER_LEAVES = main.configManager.getLang().getString("global.info.player_leaves");
            GLOBAL_INFO_LOBBY_COUNTDOWN = main.configManager.getLang().getString("global.info.lobby_countdown");
            GLOBAL_INFO_YOU_HAVE_TNT = main.configManager.getLang().getString("global.info.you_have_tnt");
            GLOBAL_INFO_YOU_NO_HAVE_TNT = main.configManager.getLang().getString("global.info.you_no_have_tnt");
            GLOBAL_INFO_PLAYER_HAS_THE_TNT = main.configManager.getLang().getString("global.info.player_has_the_tnt");
            GLOBAL_INFO_CANCELLED_SETUP = main.configManager.getLang().getString("global.info.cancelled_setup");
            GLOBAL_INFO_PAUSED_SETUP = main.configManager.getLang().getString("global.info.paused_setup");
            GLOBAL_INFO_SELECTED_MINI_GAME_SETUP = main.configManager.getLang().getString("global.info.selected_mini_game_setup");
            GLOBAL_INFO_SELECTION_ELIMINATED_SETUP = main.configManager.getLang().getString("global.info.selection_eliminated_setup");
            GLOBAL_INFO_SET_DISPLAY_NAME = main.configManager.getLang().getString("global.info.set_display_name");
            GLOBAL_INFO_SETUP_STEP_1 = main.configManager.getLang().getString("global.info.setup_step_1");
            GLOBAL_INFO_SETUP_STEP_2 = main.configManager.getLang().getString("global.info.setup_step_2");
            GLOBAL_INFO_SETUP_STEP_3 = main.configManager.getLang().getString("global.info.setup_step_3");
            GLOBAL_INFO_SETUP_STEP_4 = main.configManager.getLang().getString("global.info.setup_step_4");
            GLOBAL_INFO_SETUP_STEP_5 = main.configManager.getLang().getString("global.info.setup_step_5");
            GLOBAL_INFO_SETUP_STEP_6 = main.configManager.getLang().getString("global.info.setup_step_6");
            GLOBAL_INFO_SETUP_STEP_7 = main.configManager.getLang().getString("global.info.setup_step_7");
            GLOBAL_INFO_SETUP_RACE_STEP_1 = main.configManager.getLang().getString("global.info.setup_race_step_1");
            GLOBAL_INFO_SETUP_FAST_ZONE_STEP_1 = main.configManager.getLang().getString("global.info.setup_fast_zone_step_1");
            GLOBAL_INFO_SETUP_SPLEEF_STEP_1 = main.configManager.getLang().getString("global.info.setup_spleef_step_1");
            GLOBAL_INFO_SETUP_TRAFFIC_LIGHT_STEP_1 = main.configManager.getLang().getString("global.info.setup_traffic_light_step_1");
            GLOBAL_INFO_SETUP_MINEFIELD_STEP_1 = main.configManager.getLang().getString("global.info.setup_minefield_step_1");
            GLOBAL_INFO_SETUP_MINEFIELD_STEP_2 = main.configManager.getLang().getString("global.info.setup_minefield_step_2");
            GLOBAL_INFO_SETUP_RED_ALERT_STEP_1 = main.configManager.getLang().getString("global.info.setup_red_alert_step_1");
            GLOBAL_OTHER_USE_GOTO_SUBCOMMAND = main.configManager.getLang().getString("global.other.use_goto_subcommand");
            GLOBAL_OTHER_USE_HELP_SUBCOMMAND = main.configManager.getLang().getString("global.other.use_help_subcommand");
            GLOBAL_OTHER_USE_JOIN_SUBCOMMAND = main.configManager.getLang().getString("global.other.use_join_subcommand");
            GLOBAL_OTHER_USE_QUICKJOIN_SUBCOMMAND = main.configManager.getLang().getString("global.other.use_quickjoin_subcommand");
            GLOBAL_OTHER_USE_LEAVE_SUBCOMMAND = main.configManager.getLang().getString("global.other.use_leave_subcommand");
            GLOBAL_OTHER_USE_REMOVE_SUBCOMMAND = main.configManager.getLang().getString("global.other.use_remove_subcommand");
            GLOBAL_OTHER_USE_SETUP_SUBCOMMAND = main.configManager.getLang().getString("global.other.use_setup_subcommand");
            GLOBAL_OTHER_USE_RELOAD_SUBCOMMAND = main.configManager.getLang().getString("global.other.use_reload_subcommand");

            SUMMARY_MINI_GAME = main.configManager.getLang().getStringList("summary.mini_game");
            SUMMARY_FINAL = main.configManager.getLang().getStringList("summary.final");

            MINI_GAMES_RACE_DISPLAY_NAME = main.configManager.getLang().getString("mini_games.race.display_name");
            MINI_GAME_RACE_DESCRIPTION = main.configManager.getLang().getStringList("mini_games.race.description");
            MINI_GAMES_SPLEEF_DISPLAY_NAME = main.configManager.getLang().getString("mini_games.spleef.display_name");
            MINI_GAME_SPLEEF_DESCRIPTION = main.configManager.getLang().getStringList("mini_games.spleef.description");
            MINI_GAMES_SNOWBALL_FIGHT_DISPLAY_NAME = main.configManager.getLang().getString("mini_games.snowball_fight.display_name");
            MINI_GAME_SNOWBALL_FIGHT_DESCRIPTION = main.configManager.getLang().getStringList("mini_games.snowball_fight.description");
            MINI_GAMES_ALL_AGAINST_ALL_DISPLAY_NAME = main.configManager.getLang().getString("mini_games.all_against_all.display_name");
            MINI_GAME_ALL_AGAINST_ALL_DESCRIPTION = main.configManager.getLang().getStringList("mini_games.all_against_all.description");
            MINI_GAMES_ONE_IN_THE_CHAMBER_DISPLAY_NAME = main.configManager.getLang().getString("mini_games.one_in_the_chamber.display_name");
            MINI_GAME_ONE_IN_THE_CHAMBER_DESCRIPTION = main.configManager.getLang().getStringList("mini_games.one_in_the_chamber.description");
            MINI_GAMES_TRAFFIC_LIGHT_DISPLAY_NAME = main.configManager.getLang().getString("mini_games.traffic_light.display_name");
            MINI_GAME_TRAFFIC_LIGHT_DESCRIPTION = main.configManager.getLang().getStringList("mini_games.traffic_light.description");
            MINI_GAMES_MINEFIELD_DISPLAY_NAME = main.configManager.getLang().getString("mini_games.minefield.display_name");
            MINI_GAME_MINEFIELD_DESCRIPTION = main.configManager.getLang().getStringList("mini_games.minefield.description");
            MINI_GAMES_EXPLODING_SHEEP_DISPLAY_NAME = main.configManager.getLang().getString("mini_games.exploding_sheep.display_name");
            MINI_GAME_EXPLODING_SHEEP_DESCRIPTION = main.configManager.getLang().getStringList("mini_games.exploding_sheep.description");
            MINI_GAME_TNT_TAG_DESCRIPTION = main.configManager.getLang().getStringList("mini_games.tnt_tag.description");
            MINI_GAMES_TNT_TAG_DISPLAY_NAME = main.configManager.getLang().getString("mini_games.tnt_tag.display_name");
            MINI_GAME_RED_ALERT_DESCRIPTION = main.configManager.getLang().getStringList("mini_games.red_alert.description");
            MINI_GAMES_RED_ALERT_DISPLAY_NAME = main.configManager.getLang().getString("mini_games.red_alert.display_name");
            MINI_GAME_KNOCK_BACK_DESCRIPTION = main.configManager.getLang().getStringList("mini_games.knock_back.description");
            MINI_GAMES_KNOCK_BACK_DISPLAY_NAME = main.configManager.getLang().getString("mini_games.knock_back.display_name");
            MINI_GAME_FAST_ZONE_DESCRIPTION = main.configManager.getLang().getStringList("mini_games.fast_zone.description");
            MINI_GAMES_FAST_ZONE_DISPLAY_NAME = main.configManager.getLang().getString("mini_games.fast_zone.display_name");

            ACTION_BAR_LOBBY_STARTING = main.configManager.getLang().getString("action_bar.lobby.starting");
            ACTION_BAR_IN_GAME_GLOBAL = main.configManager.getLang().getString("action_bar.in_game.global");

            TITLES_JOIN_TITLE = main.configManager.getLang().getString("titles.join.title");
            TITLES_JOIN_SUBTITLE = main.configManager.getLang().getString("titles.join.subtitle");
            TITLES_STARTING_ARENA_TITLE = main.configManager.getLang().getString("titles.starting_arena.title");
            TITLES_STARTING_ARENA_SUBTITLE = main.configManager.getLang().getString("titles.starting_arena.subtitle");
            TITLES_STARTING_GAME_TITLE = main.configManager.getLang().getString("titles.starting_game.title");
            TITLES_STARTING_GAME_SUBTITLE = main.configManager.getLang().getString("titles.starting_game.subtitle");
            TITLES_GAME_STARTED_TITLE = main.configManager.getLang().getString("titles.game_started.title");
            TITLES_GAME_STARTED_SUBTITLE = main.configManager.getLang().getString("titles.game_started.subtitle");
            TITLES_YOU_DIED_TITLE = main.configManager.getLang().getString("titles.you_died.title");
            TITLES_YOU_DIED_SUBTITLE = main.configManager.getLang().getString("titles.you_died.subtitle");
            TITLES_CLASSIFIED_TITLE = main.configManager.getLang().getString("titles.classified.title");
            TITLES_CLASSIFIED_SUBTITLE = main.configManager.getLang().getString("titles.classified.subtitle");

            SIGNS_ARENA_LINE1 = main.configManager.getLang().getString("signs.arena.line1");
            SIGNS_ARENA_LINE2 = main.configManager.getLang().getString("signs.arena.line2");
            SIGNS_ARENA_LINE3 = main.configManager.getLang().getString("signs.arena.line3");
            SIGNS_ARENA_LINE4 = main.configManager.getLang().getString("signs.arena.line4");
            SIGNS_QUICKJOIN_LINE1 = main.configManager.getLang().getString("signs.quickjoin.line1");
            SIGNS_QUICKJOIN_LINE2 = main.configManager.getLang().getString("signs.quickjoin.line2");
            SIGNS_QUICKJOIN_LINE3 = main.configManager.getLang().getString("signs.quickjoin.line3");
            SIGNS_QUICKJOIN_LINE4 = main.configManager.getLang().getString("signs.quickjoin.line4");
            SIGNS_LEAVE_LINE1 = main.configManager.getLang().getString("signs.leave.line1");
            SIGNS_LEAVE_LINE2 = main.configManager.getLang().getString("signs.leave.line2");
            SIGNS_LEAVE_LINE3 = main.configManager.getLang().getString("signs.leave.line3");
            SIGNS_LEAVE_LINE4 = main.configManager.getLang().getString("signs.leave.line4");

            COMMANDS_BLUEARCADE_INFO = main.configManager.getLang().getStringList("commands.bluearcade.info");
            COMMANDS_BLUEARCADE_HELP = main.configManager.getLang().getStringList("commands.bluearcade.help");
            COMMANDS_BLUEARCADE_ADMINHELP = main.configManager.getLang().getStringList("commands.bluearcade.adminhelp");
        }

        public static List<String> getGameDescription(String game) {
            if(game.equalsIgnoreCase("race")) {
                return MINI_GAME_RACE_DESCRIPTION;
            } else if(game.equalsIgnoreCase("spleef")) {
                return MINI_GAME_SPLEEF_DESCRIPTION;
            } else if(game.equalsIgnoreCase("snowball_fight")) {
                return MINI_GAME_SNOWBALL_FIGHT_DESCRIPTION;
            } else if(game.equalsIgnoreCase("all_against_all")) {
                return MINI_GAME_ALL_AGAINST_ALL_DESCRIPTION;
            } else if(game.equalsIgnoreCase("one_in_the_chamber")) {
                return MINI_GAME_ONE_IN_THE_CHAMBER_DESCRIPTION;
            } else if (game.equalsIgnoreCase("traffic_light")) {
                return MINI_GAME_TRAFFIC_LIGHT_DESCRIPTION;
            } else if (game.equalsIgnoreCase("minefield")) {
                return MINI_GAME_MINEFIELD_DESCRIPTION;
            } else if (game.equalsIgnoreCase("exploding_sheep")) {
                return MINI_GAME_EXPLODING_SHEEP_DESCRIPTION;
            } else if (game.equalsIgnoreCase("tnt_tag")) {
                return MINI_GAME_TNT_TAG_DESCRIPTION;
            } else if (game.equalsIgnoreCase("red_alert")) {
                return MINI_GAME_RED_ALERT_DESCRIPTION;
            } else if (game.equalsIgnoreCase("knock_back")) {
                return MINI_GAME_KNOCK_BACK_DESCRIPTION;
            } else if (game.equalsIgnoreCase("fast_zone")) {
                return MINI_GAME_FAST_ZONE_DESCRIPTION;
            }
            return null;
        }

        // ITEMS:
        public static String ITEMS_GAME_VOTE_GAME = "";
        public static String ITEMS_GAME_LEAVE_GAME = "";
        public static String ITEMS_SETUP_CANCEL_SETUP = "";
        public static String ITEMS_SETUP_PAUSE_SETUP = "";
        public static String ITEMS_SETUP_PREVIOUS_STEP = "";
        public static String ITEMS_SETUP_PREVIOUS_STEP_1 = "";
        public static String ITEMS_SETUP_NEXT_STEP = "";
        public static String ITEMS_SETUP_SET_WAITING_LOBBY = "";
        public static String ITEMS_SETUP_MINIMUM_PLAYERS = "";
        public static String ITEMS_SETUP_MAXIMUM_PLAYERS = "";
        public static String ITEMS_SETUP_DISPLAY_NAME = "";
        public static String ITEMS_SETUP_SELECT_MINI_GAME = "";
        public static String ITEMS_SETUP_MINIMUM_BORDER = "";
        public static String ITEMS_SETUP_MAXIMUM_BORDER = "";
        public static String ITEMS_SETUP_SPAWNING_POINTS = "";
        public static String ITEMS_SETUP_SET_TIME = "";
        public static String ITEMS_SETUP_MAGIC_STICK = "";

        public static void updateItemsString(Main main) {
            ITEMS_GAME_VOTE_GAME = main.configManager.getLang().getString("items.game.vote_game");
            ITEMS_GAME_LEAVE_GAME = main.configManager.getLang().getString("items.game.leave_game");
            ITEMS_SETUP_CANCEL_SETUP = main.configManager.getLang().getString("items.setup.cancel_setup");
            ITEMS_SETUP_PAUSE_SETUP = main.configManager.getLang().getString("items.setup.pause_setup");
            ITEMS_SETUP_PREVIOUS_STEP = main.configManager.getLang().getString("items.setup.previous_step");
            ITEMS_SETUP_PREVIOUS_STEP_1 = main.configManager.getLang().getString("items.setup.previous_step_1");
            ITEMS_SETUP_NEXT_STEP = main.configManager.getLang().getString("items.setup.next_step");
            ITEMS_SETUP_SET_WAITING_LOBBY = main.configManager.getLang().getString("items.setup.set_waiting_lobby");
            ITEMS_SETUP_MINIMUM_PLAYERS = main.configManager.getLang().getString("items.setup.minimum_players");
            ITEMS_SETUP_MAXIMUM_PLAYERS = main.configManager.getLang().getString("items.setup.maximum_players");
            ITEMS_SETUP_DISPLAY_NAME = main.configManager.getLang().getString("items.setup.display_name");
            ITEMS_SETUP_SELECT_MINI_GAME = main.configManager.getLang().getString("items.setup.select_mini_game");
            ITEMS_SETUP_MINIMUM_BORDER = main.configManager.getLang().getString("items.setup.minimum_border");
            ITEMS_SETUP_MAXIMUM_BORDER = main.configManager.getLang().getString("items.setup.maximum_border");
            ITEMS_SETUP_SPAWNING_POINTS = main.configManager.getLang().getString("items.setup.spawning_points");
            ITEMS_SETUP_SET_TIME = main.configManager.getLang().getString("items.setup.set_time");
            ITEMS_SETUP_MAGIC_STICK = main.configManager.getLang().getString("items.setup.magic_stick");
        }

        // GUI:
        public static String GUI_SETUP_SELECT_GAME_TITLE = "";
        public static String GUI_SETUP_SELECT_GAME_ITEMS_EXIT_NAME = "";
        public static List<String> GUI_SETUP_SELECT_GAME_ITEMS_EXIT_LORE = new ArrayList<>();
        public static String GUI_SETUP_SELECT_GAME_ITEMS_REMOVE_SELECTION_NAME = "";
        public static List<String> GUI_SETUP_SELECT_GAME_ITEMS_REMOVE_SELECTION_LORE = new ArrayList<>();
        public static String GUI_SETUP_SELECT_GAME_ITEMS_RACE_NAME = "";
        public static List<String> GUI_SETUP_SELECT_GAME_ITEMS_RACE_LORE = new ArrayList<>();
        public static String GUI_SETUP_SELECT_GAME_ITEMS_SPLEEF_NAME = "";
        public static List<String> GUI_SETUP_SELECT_GAME_ITEMS_SPLEEF_LORE = new ArrayList<>();
        public static String GUI_SETUP_SELECT_GAME_ITEMS_SNOWBALL_FIGHT_NAME = "";
        public static List<String> GUI_SETUP_SELECT_GAME_ITEMS_SNOWBALL_FIGHT_LORE = new ArrayList<>();
        public static String GUI_SETUP_SELECT_GAME_ITEMS_ALL_AGAINST_ALL_NAME = "";
        public static List<String> GUI_SETUP_SELECT_GAME_ITEMS_ALL_AGAINST_ALL_LORE = new ArrayList<>();
        public static String GUI_SETUP_SELECT_GAME_ITEMS_ONE_IN_THE_CHAMBER_NAME = "";
        public static List<String> GUI_SETUP_SELECT_GAME_ITEMS_ONE_IN_THE_CHAMBER_LORE = new ArrayList<>();
        public static String GUI_SETUP_SELECT_GAME_ITEMS_TRAFFIC_LIGHT_NAME = "";
        public static List<String> GUI_SETUP_SELECT_GAME_ITEMS_TRAFFIC_LIGHT_LORE = new ArrayList<>();
        public static String GUI_SETUP_SELECT_GAME_ITEMS_MINEFIELD_NAME = "";
        public static List<String> GUI_SETUP_SELECT_GAME_ITEMS_MINEFIELD_LORE = new ArrayList<>();
        public static String GUI_SETUP_SELECT_GAME_ITEMS_EXPLODING_SHEEP_NAME = "";
        public static List<String> GUI_SETUP_SELECT_GAME_ITEMS_EXPLODING_SHEEP_LORE = new ArrayList<>();
        public static String GUI_SETUP_SELECT_GAME_ITEMS_TNT_TAG_NAME = "";
        public static List<String> GUI_SETUP_SELECT_GAME_ITEMS_TNT_TAG_LORE = new ArrayList<>();
        public static String GUI_SETUP_SELECT_GAME_ITEMS_RED_ALERT_NAME = "";
        public static List<String> GUI_SETUP_SELECT_GAME_ITEMS_RED_ALERT_LORE = new ArrayList<>();
        public static String GUI_SETUP_SELECT_GAME_ITEMS_KNOCK_BACK_NAME = "";
        public static List<String> GUI_SETUP_SELECT_GAME_ITEMS_KNOCK_BACK_LORE = new ArrayList<>();
        public static String GUI_SETUP_SELECT_GAME_ITEMS_FAST_ZONE_NAME = "";
        public static List<String> GUI_SETUP_SELECT_GAME_ITEMS_FAST_ZONE_LORE = new ArrayList<>();

        public static void updateGUI(Main main) {
            GUI_SETUP_SELECT_GAME_TITLE = main.configManager.getLang().getString("gui.setup.select_game.title");
            GUI_SETUP_SELECT_GAME_ITEMS_EXIT_NAME = main.configManager.getLang().getString("gui.setup.select_game.items.exit.name");
            GUI_SETUP_SELECT_GAME_ITEMS_EXIT_LORE = main.configManager.getLang().getStringList("gui.setup.select_game.items.exit.lore");
            GUI_SETUP_SELECT_GAME_ITEMS_REMOVE_SELECTION_NAME = main.configManager.getLang().getString("gui.setup.select_game.items.remove_selection.name");
            GUI_SETUP_SELECT_GAME_ITEMS_REMOVE_SELECTION_LORE = main.configManager.getLang().getStringList("gui.setup.select_game.items.remove_selection.lore");
            GUI_SETUP_SELECT_GAME_ITEMS_RACE_NAME = main.configManager.getLang().getString("gui.setup.select_game.items.race.name");
            GUI_SETUP_SELECT_GAME_ITEMS_RACE_LORE = main.configManager.getLang().getStringList("gui.setup.select_game.items.race.lore");
            GUI_SETUP_SELECT_GAME_ITEMS_SPLEEF_NAME = main.configManager.getLang().getString("gui.setup.select_game.items.spleef.name");
            GUI_SETUP_SELECT_GAME_ITEMS_SPLEEF_LORE = main.configManager.getLang().getStringList("gui.setup.select_game.items.spleef.lore");
            GUI_SETUP_SELECT_GAME_ITEMS_SNOWBALL_FIGHT_NAME = main.configManager.getLang().getString("gui.setup.select_game.items.snowball_fight.name");
            GUI_SETUP_SELECT_GAME_ITEMS_SNOWBALL_FIGHT_LORE = main.configManager.getLang().getStringList("gui.setup.select_game.items.snowball_fight.lore");
            GUI_SETUP_SELECT_GAME_ITEMS_ALL_AGAINST_ALL_NAME = main.configManager.getLang().getString("gui.setup.select_game.items.all_against_all.name");
            GUI_SETUP_SELECT_GAME_ITEMS_ALL_AGAINST_ALL_LORE = main.configManager.getLang().getStringList("gui.setup.select_game.items.all_against_all.lore");
            GUI_SETUP_SELECT_GAME_ITEMS_ONE_IN_THE_CHAMBER_NAME = main.configManager.getLang().getString("gui.setup.select_game.items.one_in_the_chamber.name");
            GUI_SETUP_SELECT_GAME_ITEMS_ONE_IN_THE_CHAMBER_LORE = main.configManager.getLang().getStringList("gui.setup.select_game.items.one_in_the_chamber.lore");
            GUI_SETUP_SELECT_GAME_ITEMS_TRAFFIC_LIGHT_NAME = main.configManager.getLang().getString("gui.setup.select_game.items.traffic_light.name");
            GUI_SETUP_SELECT_GAME_ITEMS_TRAFFIC_LIGHT_LORE = main.configManager.getLang().getStringList("gui.setup.select_game.items.traffic_light.lore");
            GUI_SETUP_SELECT_GAME_ITEMS_MINEFIELD_NAME = main.configManager.getLang().getString("gui.setup.select_game.items.minefield.name");
            GUI_SETUP_SELECT_GAME_ITEMS_MINEFIELD_LORE = main.configManager.getLang().getStringList("gui.setup.select_game.items.minefield.lore");
            GUI_SETUP_SELECT_GAME_ITEMS_EXPLODING_SHEEP_NAME = main.configManager.getLang().getString("gui.setup.select_game.items.exploding_sheep.name");
            GUI_SETUP_SELECT_GAME_ITEMS_EXPLODING_SHEEP_LORE = main.configManager.getLang().getStringList("gui.setup.select_game.items.exploding_sheep.lore");
            GUI_SETUP_SELECT_GAME_ITEMS_TNT_TAG_NAME = main.configManager.getLang().getString("gui.setup.select_game.items.tnt_tag.name");
            GUI_SETUP_SELECT_GAME_ITEMS_TNT_TAG_LORE = main.configManager.getLang().getStringList("gui.setup.select_game.items.tnt_tag.lore");
            GUI_SETUP_SELECT_GAME_ITEMS_RED_ALERT_NAME = main.configManager.getLang().getString("gui.setup.select_game.items.red_alert.name");
            GUI_SETUP_SELECT_GAME_ITEMS_RED_ALERT_LORE = main.configManager.getLang().getStringList("gui.setup.select_game.items.red_alert.lore");
            GUI_SETUP_SELECT_GAME_ITEMS_KNOCK_BACK_NAME = main.configManager.getLang().getString("gui.setup.select_game.items.knock_back.name");
            GUI_SETUP_SELECT_GAME_ITEMS_KNOCK_BACK_LORE = main.configManager.getLang().getStringList("gui.setup.select_game.items.knock_back.lore");
            GUI_SETUP_SELECT_GAME_ITEMS_FAST_ZONE_NAME = main.configManager.getLang().getString("gui.setup.select_game.items.fast_zone.name");
            GUI_SETUP_SELECT_GAME_ITEMS_FAST_ZONE_LORE = main.configManager.getLang().getStringList("gui.setup.select_game.items.fast_zone.lore");
        }

        // SCOREBOARDS:
        private static final HashMap<String, List<String>> PluginScoreboards = new HashMap<>();
        private static final HashMap<String, String> PluginScoreboardsTitles = new HashMap<>();

        public static String SCOREBOARDS_IN_GAME_GLOBAL_PLAYING_MINI_DESCRIPTION_RACE = "";
        public static String SCOREBOARDS_IN_GAME_GLOBAL_PLAYING_MINI_DESCRIPTION_SPLEEF = "";
        public static String SCOREBOARDS_IN_GAME_GLOBAL_PLAYING_MINI_DESCRIPTION_SNOWBALL_FIGHT = "";
        public static String SCOREBOARDS_IN_GAME_GLOBAL_PLAYING_MINI_DESCRIPTION_ALL_AGAINST_ALL = "";
        public static String SCOREBOARDS_IN_GAME_GLOBAL_PLAYING_MINI_DESCRIPTION_MINEFIELD = "";
        public static String SCOREBOARDS_IN_GAME_GLOBAL_PLAYING_MINI_DESCRIPTION_RED_ALERT = "";
        public static String SCOREBOARDS_IN_GAME_GLOBAL_PLAYING_MINI_DESCRIPTION_FAST_ZONE = "";

        public static void updateScoreboardMiniDescriptions(Main main) {
            SCOREBOARDS_IN_GAME_GLOBAL_PLAYING_MINI_DESCRIPTION_RACE = main.configManager.getLang().getString("scoreboards.in_game.global.playing.mini_description.race");
            SCOREBOARDS_IN_GAME_GLOBAL_PLAYING_MINI_DESCRIPTION_SPLEEF = main.configManager.getLang().getString("scoreboards.in_game.global.playing.mini_description.spleef");
            SCOREBOARDS_IN_GAME_GLOBAL_PLAYING_MINI_DESCRIPTION_SNOWBALL_FIGHT = main.configManager.getLang().getString("scoreboards.in_game.global.playing.mini_description.snowball_fight");
            SCOREBOARDS_IN_GAME_GLOBAL_PLAYING_MINI_DESCRIPTION_ALL_AGAINST_ALL = main.configManager.getLang().getString("scoreboards.in_game.global.playing.mini_description.all_against_all");
            SCOREBOARDS_IN_GAME_GLOBAL_PLAYING_MINI_DESCRIPTION_MINEFIELD = main.configManager.getLang().getString("scoreboards.in_game.global.playing.mini_description.minefield");
            SCOREBOARDS_IN_GAME_GLOBAL_PLAYING_MINI_DESCRIPTION_RED_ALERT = main.configManager.getLang().getString("scoreboards.in_game.global.playing.mini_description.red_alert");
            SCOREBOARDS_IN_GAME_GLOBAL_PLAYING_MINI_DESCRIPTION_FAST_ZONE = main.configManager.getLang().getString("scoreboards.in_game.global.playing.mini_description.fast_zone");
        }

        public static String getScoreboardMiniDescription(String game) {
            if(game.equalsIgnoreCase("race")) {
                return SCOREBOARDS_IN_GAME_GLOBAL_PLAYING_MINI_DESCRIPTION_RACE;
            } else if(game.equalsIgnoreCase("spleef")) {
                return SCOREBOARDS_IN_GAME_GLOBAL_PLAYING_MINI_DESCRIPTION_SPLEEF;
            } else if(game.equalsIgnoreCase("snowballfight")) {
                return SCOREBOARDS_IN_GAME_GLOBAL_PLAYING_MINI_DESCRIPTION_SNOWBALL_FIGHT;
            } else if (game.equalsIgnoreCase("allagainstall")) {
                return SCOREBOARDS_IN_GAME_GLOBAL_PLAYING_MINI_DESCRIPTION_ALL_AGAINST_ALL;
            } else if (game.equalsIgnoreCase("minefield")) {
                return SCOREBOARDS_IN_GAME_GLOBAL_PLAYING_MINI_DESCRIPTION_MINEFIELD;
            } else if (game.equalsIgnoreCase("redalert")) {
                return SCOREBOARDS_IN_GAME_GLOBAL_PLAYING_MINI_DESCRIPTION_RED_ALERT;
            } else if (game.equalsIgnoreCase("fastzone")) {
                return SCOREBOARDS_IN_GAME_GLOBAL_PLAYING_MINI_DESCRIPTION_FAST_ZONE;
            }
            return "";
        }

        public static void registerScoreboards(Main main) {
            List<String> lobbyWaiting = main.configManager.getLang().getStringList("scoreboards.lobby.waiting.list");
            List<String> lobbyStarting = main.configManager.getLang().getStringList("scoreboards.lobby.starting.list");
            List<String> limbo = main.configManager.getLang().getStringList("scoreboards.limbo.list");
            List<String> inGameStarting = main.configManager.getLang().getStringList("scoreboards.in_game.starting.list");
            List<String> inGamePlayingGlobal = main.configManager.getLang().getStringList("scoreboards.in_game.global.playing.list");
            List<String> inGamePlayingOneInTheChamber = main.configManager.getLang().getStringList("scoreboards.in_game.one_in_the_chamber.playing.list");
            List<String> inGamePlayingTrafficLight = main.configManager.getLang().getStringList("scoreboards.in_game.traffic_light.playing.list");
            List<String> inGamePlayingExplodingSheep = main.configManager.getLang().getStringList("scoreboards.in_game.exploding_sheep.playing.list");
            List<String> inGamePlayingTNTTag = main.configManager.getLang().getStringList("scoreboards.in_game.tnt_tag.playing.list");
            List<String> inGamePlayingRedAlert = main.configManager.getLang().getStringList("scoreboards.in_game.red_alert.playing.list");
            List<String> inGamePlayingKnockBack = main.configManager.getLang().getStringList("scoreboards.in_game.knock_back.playing.list");
            List<String> finish = main.configManager.getLang().getStringList("scoreboards.finish.list");

            PluginScoreboards.clear();

            PluginScoreboards.put("lobby_waiting", lobbyWaiting);
            PluginScoreboards.put("lobby_starting", lobbyStarting);
            PluginScoreboards.put("limbo", limbo);
            PluginScoreboards.put("in_game_starting", inGameStarting);
            PluginScoreboards.put("in_game_playing_global", inGamePlayingGlobal);
            PluginScoreboards.put("in_game_playing_one_in_the_chamber", inGamePlayingOneInTheChamber);
            PluginScoreboards.put("in_game_playing_traffic_light", inGamePlayingTrafficLight);
            PluginScoreboards.put("in_game_playing_exploding_sheep", inGamePlayingExplodingSheep);
            PluginScoreboards.put("in_game_playing_tnt_tag", inGamePlayingTNTTag);
            PluginScoreboards.put("in_game_playing_red_alert", inGamePlayingRedAlert);
            PluginScoreboards.put("in_game_playing_knock_back", inGamePlayingKnockBack);
            PluginScoreboards.put("finish", finish);

            PluginScoreboardsTitles.clear();

            PluginScoreboardsTitles.put("lobby_waiting", main.configManager.getLang().getString("scoreboards.lobby.waiting.title"));
            PluginScoreboardsTitles.put("lobby_starting", main.configManager.getLang().getString("scoreboards.lobby.starting.title"));
            PluginScoreboardsTitles.put("limbo", main.configManager.getLang().getString("scoreboards.limbo.title"));
            PluginScoreboardsTitles.put("in_game_starting", main.configManager.getLang().getString("scoreboards.in_game.starting.title"));
            PluginScoreboardsTitles.put("in_game_playing_global", main.configManager.getLang().getString("scoreboards.in_game.global.playing.title"));
            PluginScoreboardsTitles.put("in_game_playing_one_in_the_chamber", main.configManager.getLang().getString("scoreboards.in_game.one_in_the_chamber.playing.title"));
            PluginScoreboardsTitles.put("in_game_playing_traffic_light", main.configManager.getLang().getString("scoreboards.in_game.traffic_light.playing.title"));
            PluginScoreboardsTitles.put("in_game_playing_exploding_sheep", main.configManager.getLang().getString("scoreboards.in_game.exploding_sheep.playing.title"));
            PluginScoreboardsTitles.put("in_game_playing_tnt_tag", main.configManager.getLang().getString("scoreboards.in_game.tnt_tag.playing.title"));
            PluginScoreboardsTitles.put("in_game_playing_red_alert", main.configManager.getLang().getString("scoreboards.in_game.red_alert.playing.title"));
            PluginScoreboardsTitles.put("in_game_playing_knock_back", main.configManager.getLang().getString("scoreboards.in_game.knock_back.playing.title"));
            PluginScoreboardsTitles.put("in_game_playing_fast_zone", main.configManager.getLang().getString("scoreboards.in_game.fast_zone.playing.title"));
            PluginScoreboardsTitles.put("finish", main.configManager.getLang().getString("scoreboards.finish.title"));
        }

        public static List<String> getScoreboardLines(String scoreboardName) {
            return PluginScoreboards.getOrDefault(scoreboardName, null);
        }

        public static String getScoreboardTitle(String scoreboardName) {
            return PluginScoreboardsTitles.getOrDefault(scoreboardName, "");
        }
    }
}