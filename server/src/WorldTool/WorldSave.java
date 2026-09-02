package WorldTool;

import Base.Area;
import Element.Entity;
import EntityType.Item;
import EntityType.Player;
import EntityType.Zombie;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

public class WorldSave {
    public static String path = System.getProperty("user.dir") + "/data/world/";
    public static String pathOp = System.getProperty("user.dir") + "/data/ops/";
    public static String pathBan = System.getProperty("user.dir") + "/data/bans/";

    public static void saveWorld(String name, World world) {
        saveOp(name, world);
        saveBan(name, world);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(path + name + ".txt"));
            writer.write("width = " + world.getWidth() + "\n");
            writer.write("height = " + world.getHeight() + "\n");
            writer.write("blockIdList = {\n");
            int[][] blockIdList = world.getBlockIdList();
            for (int y = 0; y < world.getHeight(); y++) {
                writer.write("[");
                for (int x = 0; x < world.getWidth(); x++) {
                    writer.write(Integer.toString(blockIdList[y][x]));
                    if (x != world.getWidth() - 1) {
                        writer.write(",");
                    }
                }
                writer.write("]\n");
            }
            writer.write("}\n");
            writer.write("voidSize = " + world.getVoidSize() + "\n");
            writer.write("gama = " + world.getGama() + "\n");
            writer.write("difficulty = " + world.getDifficulty() + "\n");
            writer.write("xSpawn = " + world.getxSpawn() + "\n");
            writer.write("ySpawn = " + world.getySpawn() + "\n");
            writer.write("blockSize = " + world.getBlockSize() + "\n");
            writer.write("gravity = " + world.getGravity() + "\n");
            writer.write("airResistance = " + world.getAirResistance() + "\n");
            writer.write("time = " + world.getTime() + "\n");
            // 更新在线玩家信息
            for (Entity entity : world.getEntityList()) {
                if (entity instanceof Player) {
                    Player playerInEntityList = (Player) entity;
                    for (Player playerInPlayerList : world.getPlayerList()) {
                        if (Objects.equals(playerInEntityList.getName(), playerInPlayerList.getName())) {
                            world.getPlayerList().remove(playerInPlayerList);
                            world.getPlayerList().add(playerInEntityList);
                        }
                    }
                }
            }
            // 保存世界保护区信息
            for (Area area : World.getInstance().getAreaProtectedList()) {
                writer.write("areaProtected = {\n");
                writer.write("x1 = " + area.getX1() + "\n");
                writer.write("y1 = " + area.getY1() + "\n");
                writer.write("x2 = " + area.getX2() + "\n");
                writer.write("y2 = " + area.getY2() + "\n");
                writer.write("}\n");
            }
            // 保存所有玩家信息
            for (Player player : world.getPlayerList()) {
                writer.write("player = {\n");
                writer.write("name = " + player.getName() + "\n");
                writer.write("x = " + player.getX() + "\n");
                writer.write("y = " + player.getY() + "\n");
                writer.write("xSpawn = " + player.getxSpawn() + "\n");
                writer.write("ySpawn = " + player.getySpawn() + "\n");
                writer.write("gameMode = " + player.getGameMode() + "\n");
                writer.write("health = " + player.getHealth() + "\n");
                writer.write("flying = " + player.isFlying() + "\n");
                writer.write("autoJump = " + player.isAutoJump() + "\n");
                writer.write("keepInventory = " + player.isKeepInventory() + "\n");
                writer.write("password = " + player.getPassword() + "\n");
                writer.write("xHome = " + player.getxHome() + "\n");
                writer.write("yHome = " + player.getyHome() + "\n");
                writer.write("ItemBarAmount =\n[");
                for (int i = 0; i < 36; i++) {
                    writer.write(Integer.toString(player.getItemBarAmount()[i]));
                    if (i != 35) {
                        writer.write(",");
                    }
                }
                writer.write("]\n");
                writer.write("ItemBarId =\n[");
                for (int i = 0; i < 36; i++) {
                    writer.write(Integer.toString(player.getItemBarId()[i]));
                    if (i != 35) {
                        writer.write(",");
                    }
                }
                writer.write("]\n");
                writer.write("}\n");

            }
            // 保存其它实体信息
            for (Entity entity : world.getEntityList()) {
                if (entity instanceof Item) {
                    Item item = (Item) entity;
                    writer.write("Item = {\n");
                    writer.write("x = " + item.getX() + "\n");
                    writer.write("y = " + item.getY() + "\n");
                    writer.write("id = " + item.getId() + "\n");
                    writer.write("amount = " + item.getAmount() + "\n");
                    writer.write("timeNoCollect = " + item.getTimeNoCollect() + "\n");
                    writer.write("}\n");
                } else if (entity instanceof Zombie) {
                    Zombie zombie = (Zombie) entity;
                    writer.write("Zombie = {\n");
                    writer.write("health = " + zombie.getHealth() + "\n");
                    writer.write("x = " + zombie.getX() + "\n");
                    writer.write("y = " + zombie.getY() + "\n");
                    writer.write("}\n");
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close(); // 在finally块中确保关闭文件流
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void saveOp(String name, World world) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(pathOp + name + ".txt"));
            for (String string : world.getOpList()) {
                writer.write("op = " + string + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close(); // 在finally块中确保关闭文件流
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void saveBan(String name, World world) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(pathBan + name + ".txt"));
            for (String string : world.getBanList()) {
                writer.write("ban = " + string + "\n");
            }
            for (String string : world.getBanIpList()) {
                writer.write("banip = " + string + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close(); // 在finally块中确保关闭文件流
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
