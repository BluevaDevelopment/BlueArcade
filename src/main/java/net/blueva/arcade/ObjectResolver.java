package net.blueva.arcade;

public class ObjectResolver {
    static Main main = Main.getPlugin();
    static String version = main.bukkitVersion;
    public static class getItem {
        public static String STAINED_GLASS_PANE (String color) {
            if(version.contains("1.16") || version.contains("1.18") || version.contains("1.19") || version.contains("1.20")) {
                if(color.equalsIgnoreCase("gray")){
                    return "GRAY_STAINED_GLASS_PANE";
                } else if(color.equalsIgnoreCase("black")){
                    return "BLACK_STAINED_GLASS_PANE";
                }
            }
            return "STAINED_GLASS_PANE";
        }

        public static String SNOWBALL () {
            if(version.contains("1.16") || version.contains("1.18") || version.contains("1.19") || version.contains("1.20")) {
                return "SNOWBALL";
            }
            return "SNOW_BALL";
        }

        public static String RED_ROSE() {
            if(version.contains("1.16") || version.contains("1.18") || version.contains("1.19") || version.contains("1.20")) {
                return "POPPY";
            }
            return "RED_ROSE";
        }

        public static String CLOCK() {
            if(version.contains("1.16") || version.contains("1.18") || version.contains("1.19") || version.contains("1.20")) {
                return "CLOCK";
            }
            return "WATCH";
        }

        public static String DIAMOND_SHOVEL() {
            if(version.contains("1.16") || version.contains("1.18") || version.contains("1.19") || version.contains("1.20")) {
                return "DIAMOND_SHOVEL";
            }
            return "DIAMOND_SPADE";
        }

        public static String BED() {
            if(version.contains("1.16") || version.contains("1.18") || version.contains("1.19") || version.contains("1.20")) {
                return "RED_BED";
            }
            return "BED";
        }
    }

    public static class getBlock {
        public static String WOOL (String color) {
            if(version.contains("1.16") || version.contains("1.18") || version.contains("1.19") || version.contains("1.20")) {
                if(color.equalsIgnoreCase("red")) {
                    return "RED_WOOL";
                }
                if(color.equalsIgnoreCase("orange")) {
                    return "ORANGE_WOOL";
                }
                if(color.equalsIgnoreCase("yellow")) {
                    return "YELLOW_WOOL";
                }
                if(color.equalsIgnoreCase("lime")) {
                    return "LIME_WOOL";
                }
                if(color.equalsIgnoreCase("white")) {
                    return "WHITE_WOOL";
                }
            }
            return "WOOL";
        }

        public static String GOLD_PLATE () {
            if(version.contains("1.16") || version.contains("1.18") || version.contains("1.19") || version.contains("1.20")) {
                return "LIGHT_WEIGHTED_PRESSURE_PLATE";
            }
            return "GOLD_PLATE";
        }

        public static String STONE_PLATE () {
            if(version.contains("1.16") || version.contains("1.18") || version.contains("1.19") || version.contains("1.20")) {
                return "STONE_PRESSURE_PLATE";
            }
            return "STONE_PLATE";
        }
    }
}
