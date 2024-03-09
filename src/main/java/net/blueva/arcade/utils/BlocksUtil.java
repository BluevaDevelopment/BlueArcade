package net.blueva.arcade.utils;

import net.blueva.arcade.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import java.util.*;

public class BlocksUtil {

    public static void setBlocks(Location pointA, Location pointB, Material... materials) {
        int minX = Math.min(pointA.getBlockX(), pointB.getBlockX());
        int minY = Math.min(pointA.getBlockY(), pointB.getBlockY());
        int minZ = Math.min(pointA.getBlockZ(), pointB.getBlockZ());
        int maxX = Math.max(pointA.getBlockX(), pointB.getBlockX());
        int maxY = Math.max(pointA.getBlockY(), pointB.getBlockY());
        int maxZ = Math.max(pointA.getBlockZ(), pointB.getBlockZ());

        World world = pointA.getWorld();
        Random random = new Random();

        if(world != null) {
            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        Block block = world.getBlockAt(x, y, z);
                        Material randomMaterial = materials[random.nextInt(materials.length)];
                        block.setType(randomMaterial);
                    }
                }
            }
        }
    }

    public static Entity[] getNearbyEntities(Location location1, Location location2, int margin) {
        int minX = Math.min(location1.getBlockX(), location2.getBlockX()) - margin;
        int minY = Math.min(location1.getBlockY(), location2.getBlockY()) - margin;
        int minZ = Math.min(location1.getBlockZ(), location2.getBlockZ()) - margin;
        int maxX = Math.max(location1.getBlockX(), location2.getBlockX()) + margin;
        int maxY = Math.max(location1.getBlockY(), location2.getBlockY()) + margin;
        int maxZ = Math.max(location1.getBlockZ(), location2.getBlockZ()) + margin;

        int chunkRadiusX = ((maxX - minX) / 16) + 1;
        int chunkRadiusZ = ((maxZ - minZ) / 16) + 1;
        int chunkRadius = Math.max(chunkRadiusX, chunkRadiusZ);

        HashSet<Entity> radiusEntities = new HashSet<>();
        for (int chX = -chunkRadius; chX <= chunkRadius; chX++) {
            for (int chZ = -chunkRadius; chZ <= chunkRadius; chZ++) {
                int x = minX + (chX * 16);
                int z = minZ + (chZ * 16);
                for (Entity e : new Location(location1.getWorld(), x, minY, z).getChunk().getEntities()) {
                    if(!e.isDead()) {
                        Location loc = e.getLocation();
                        if (loc.getX() >= minX && loc.getX() <= maxX &&
                                loc.getY() >= minY && loc.getY() <= maxY &&
                                loc.getZ() >= minZ && loc.getZ() <= maxZ) {
                            radiusEntities.add(e);
                        }
                    }
                }
            }
        }

        Entity[] entities = new Entity[radiusEntities.size()];
        radiusEntities.toArray(entities);
        return entities;
    }


    public static List<String> generateBlockList(Location location1, Location location2) {
        List<String> blockList = new ArrayList<>();

        int x1 = Math.min(location1.getBlockX(), location2.getBlockX());
        int y1 = Math.min(location1.getBlockY(), location2.getBlockY());
        int z1 = Math.min(location1.getBlockZ(), location2.getBlockZ());
        int x2 = Math.max(location1.getBlockX(), location2.getBlockX());
        int y2 = Math.max(location1.getBlockY(), location2.getBlockY());
        int z2 = Math.max(location1.getBlockZ(), location2.getBlockZ());

        World world = location1.getWorld();

        if(world != null) {
            for (int x = x1; x <= x2; x++) {
                for (int y = y1; y <= y2; y++) {
                    for (int z = z1; z <= z2; z++) {
                        Block block = world.getBlockAt(x, y, z);
                        Material material = block.getType();

                        if (material != Material.AIR && material != Material.BARRIER) {
                            String blockData = block.getType().toString();
                            blockList.add(world.getName() + "," + x + "," + y + "," + z + "," + blockData);
                        }
                    }
                }
            }
        }

        return blockList;
    }


    public static List<String> loadBlocks(Main main, String region, int arenaid) {
        List<String> blockList = main.configManager.getRegion(region, arenaid).getStringList("blocks");
        return new ArrayList<>(blockList);
    }

    public static Map<Location, Material> getBlockMap(List<String> blockList) {
        Map<Location, Material> blockMap = new HashMap<>();

        for (String blockData : blockList) {
            String[] data = blockData.split(",");
            String worldName = data[0];
            int x = Integer.parseInt(data[1]);
            int y = Integer.parseInt(data[2]);
            int z = Integer.parseInt(data[3]);
            Material blockType = Material.getMaterial(data[4]);

            Location loc = new Location(Bukkit.getWorld(worldName), x, y, z);
            Block block = loc.getBlock();

            if(blockType != null) {
                block.setType(blockType);
                blockMap.put(loc, blockType);
            }
        }

        return blockMap;
    }

    public static void setRegionBlocks(Map<Location, Material> blockMap) {
        for (Map.Entry<Location, Material> entry : blockMap.entrySet()) {
            Location loc = entry.getKey();
            Material material = entry.getValue();
            if(material != null) {
                Block block = loc.getBlock();
                if (block.getType() != material) {
                    block.setType(material);
                }
            }
        }
    }
}
