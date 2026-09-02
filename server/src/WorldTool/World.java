package WorldTool;

import Base.*;
import Element.Entity;
import EntityType.Item;
import EntityType.Player;
import EntityType.Zombie;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.lang.Math.abs;
import static java.lang.Math.max;

public class World implements Runnable {
    // 定义世界线程
    private Thread thread = new Thread(this);
    // 定义管理员列表
    private List<String> opList = new CopyOnWriteArrayList<>();
    // 定义封禁玩家列表
    private List<String> banList = new CopyOnWriteArrayList<>();
    // 定义封禁ip列表
    private List<String> banIpList = new CopyOnWriteArrayList<>();
    // 定义保护区列表
    private List<Area> areaProtectedList = new CopyOnWriteArrayList<>();
    // 定义世界背景透明度
    private double worldTimeAlphaDouble = 0;
    // 定义实体列表扩展长度
    private int entityListExtension = 1;
    // 定义世界大小(方块数)
    int width = 200;
    int height = 100;
    int voidSize = 10;
    // 定义方块大小
    int blockSize = 50;
    // 定义亮度值
    double gama = 0.25;
    // 定义世界出生点
    int xSpawn = 50;
    int ySpawn = (height - 5) * 50;
    // 定义世界重力加速度
    double gravity = 0.5;
    // 定义世界空气阻力
    double airResistance = 0.5;
    // 定义世界怪物数量上限
    private int mobAmountMax = 10;
    // 定义世界当前怪物数量
    private int mobAmount = 0;
    // 定义世界难度
    // peaceful easy normal hard
    private String difficulty = "normal";
    // 定义世界时间(0~120000)
    private int time = 60000;
    // 定义世界实体列表
    private List<Entity> entityList = new CopyOnWriteArrayList<>();
    // 定义世界玩家列表(包含离线玩家)
    private List<Player> playerList = new CopyOnWriteArrayList<>();
    // 定义世界所有方块ID列表，第一格为y，第二格为x
    private int[][] blockIdList = new int[height][width];
    // 定义当前视野内方块光照强度
    private int[][] lightIntensity = null;
    // 定义玩家刷怪距离
    private int xVision = 1500;
    private int yVision = 1500;

    private World() {
    }

    private static class WorldInstance {
        private static final World world = new World();
    }

    @Override
    public void run() {
        while (true) {
            // 更新玩家周围区块
            updatePlayerAroundBlockIdList();
            // 更新世界怪物
            updateMobs();
            // 更新世界怪物数量
            updateMobAmount();
            // 更新世界时间
            updateTime();
            // 更新草坪蔓延
            updateGrassSpread();
            // 更新世界时间值
            updateWorldTimeAlphaDouble();
            // 世界中玩家吸附掉落物
            playerAttractItem();
            // 世界中玩家拾取掉落物
            playerCollectItem();
            if (time % 5000 == 0) {
                WorldSave.saveWorld("world", World.getInstance());
                TCPServer.broadcastToAllClients("/save\n");
                System.out.println("[/localhost] World saved");
            }
            for (Entity entity : entityList) {
                entity.updateCollisionLocation();
                entity.updateCenterLocation();
                if (entity instanceof Player) {
                    Player player = (Player) entity;
                    // 渲染光照
                    renderLightIntensity((player.getxCenter() - 1500) / 50, (player.getxCenter() + 1500) / 50);
                    for (int i = 0; i < 36; i++) {
                        if(player.getItemBarAmount()[i] < 0) player.getItemBarAmount()[i] = 0;
                    }
                } else if (entity instanceof Zombie) {
                    Zombie zombie = (Zombie) entity;
                    zombie.updateZombieData();
                    zombie.updateEntityData();
                } else if (entity instanceof Item) {
                    Item item = (Item) entity;
                    item.updateEntityData();
                    item.updateItemData();
                }
            }
            // 向玩家更新信息
            if (!TCPServer.clientList.isEmpty()) {
                TCPServer.broadcastToAllClients("/updateAlive 1200\n");
                TCPServer.broadcastToAllClients("/updateTime " + time + "\n");
            }
            // 定义线程每9ms执行一次
            try {
                Thread.sleep(9);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // 更新玩家周围区块
    public void updatePlayerAroundBlockIdList() {
        for (Entity entity : World.getInstance().getEntityList()) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                float xPlayerFloat = (player.getX()+10) / (blockSize * 50f);
                float yPlayerFloat = (player.getY()+ (float) 95 /2) / (blockSize * 50f);
                int xDelta = 0;
                int yDelta = 0;
                if (xPlayerFloat - (int) xPlayerFloat >= 0.5) xDelta = 1;
                else xDelta = -1;
                if (yPlayerFloat - (int) yPlayerFloat <= 0.5) yDelta = 1;
                else yDelta = -1;
                int xPlayer = (int) xPlayerFloat;
                int yPlayer = (int) yPlayerFloat;

                if (player.getBlockIdListLoaded()[yPlayer][xPlayer] != 1) {
                    int x1 = xPlayer * 50;
                    int y1 = yPlayer * 50;
                    int x2 = xPlayer * 50 + 50;
                    int y2 = yPlayer * 50 + 50;
                    int[][] blockIdListRange = new int[x2 - x1 + 1][y2 - y1 + 1];
                    int j = 0;
                    for (int y = y1; y < y2; y++) {
                        int i = 0;
                        for (int x = x1; x < x2; x++) {
                            blockIdListRange[j][i] = blockIdList[y][x];
                            i++;
                        }
                        j++;
                    }
                    for (Client client : TCPServer.clientList) {
                        if (client.getPlayerName() != null && client.getPlayerName().equals(player.getName())) {
                            TCPServer.broadcastToSingleClient("/updateBlockIdListRange " + x1 + " " + y1 + " " + x2 + " " + y2 + " " + StringConversion.intDoubleArrayToString(blockIdListRange) + "\n", client.getSocket());
                        }
                    }
                    player.getBlockIdListLoaded()[yPlayer][xPlayer] = 1;
                }
                if (xPlayer + xDelta >= 0 && xPlayer + xDelta < (World.getInstance().getWidth() - 1) / 50 && player.getBlockIdListLoaded()[yPlayer][xPlayer + xDelta] != 1) {
                    int x1 = (xPlayer + xDelta) * 50;
                    int y1 = yPlayer * 50;
                    int x2 = (xPlayer + xDelta) * 50 + 50;
                    int y2 = yPlayer * 50 + 50;
                    int[][] blockIdListRange = new int[x2 - x1 + 1][y2 - y1 + 1];
                    int j = 0;
                    for (int y = y1; y < y2; y++) {
                        int i = 0;
                        for (int x = x1; x < x2; x++) {
                            blockIdListRange[j][i] = blockIdList[y][x];
                            i++;
                        }
                        j++;
                    }
                    for (Client client : TCPServer.clientList) {
                        if (client.getPlayerName() != null && client.getPlayerName().equals(player.getName())) {
                            TCPServer.broadcastToSingleClient("/updateBlockIdListRange " + x1 + " " + y1 + " " + x2 + " " + y2 + " " + StringConversion.intDoubleArrayToString(blockIdListRange) + "\n", client.getSocket());
                        }
                    }
                    player.getBlockIdListLoaded()[yPlayer][xPlayer + xDelta] = 1;
                }
                if (yPlayer + yDelta >= 0 && yPlayer + yDelta < (World.getInstance().getHeight() - 1) / 50 && player.getBlockIdListLoaded()[yPlayer + yDelta][xPlayer] != 1) {
                    int x1 = xPlayer * 50;
                    int y1 = (yPlayer + yDelta) * 50;
                    int x2 = xPlayer * 50 + 50;
                    int y2 = (yPlayer + yDelta) * 50 + 50;
                    int[][] blockIdListRange = new int[x2 - x1 + 1][y2 - y1 + 1];
                    int j = 0;
                    for (int y = y1; y < y2; y++) {
                        int i = 0;
                        for (int x = x1; x < x2; x++) {
                            blockIdListRange[j][i] = blockIdList[y][x];
                            i++;
                        }
                        j++;
                    }
                    for (Client client : TCPServer.clientList) {
                        if (client.getPlayerName() != null && client.getPlayerName().equals(player.getName())) {
                            TCPServer.broadcastToSingleClient("/updateBlockIdListRange " + x1 + " " + y1 + " " + x2 + " " + y2 + " " + StringConversion.intDoubleArrayToString(blockIdListRange) + "\n", client.getSocket());
                        }
                    }
                    player.getBlockIdListLoaded()[yPlayer + yDelta][xPlayer] = 1;
                }
                if (xPlayer + xDelta >= 0 && xPlayer + xDelta < (World.getInstance().getWidth() - 1) / 50 && yPlayer + yDelta >= 0 && yPlayer + yDelta < (World.getInstance().getHeight() - 1) / 50 && player.getBlockIdListLoaded()[yPlayer + yDelta][xPlayer + xDelta] != 1) {
                    int x1 = (xPlayer + xDelta) * 50;
                    int y1 = (yPlayer + yDelta) * 50;
                    int x2 = (xPlayer + xDelta) * 50 + 50;
                    int y2 = (yPlayer + yDelta) * 50 + 50;
                    int[][] blockIdListRange = new int[x2 - x1 + 1][y2 - y1 + 1];
                    int j = 0;
                    for (int y = y1; y < y2; y++) {
                        int i = 0;
                        for (int x = x1; x < x2; x++) {
                            blockIdListRange[j][i] = blockIdList[y][x];
                            i++;
                        }
                        j++;
                    }
                    for (Client client : TCPServer.clientList) {
                        if (client.getPlayerName() != null && client.getPlayerName().equals(player.getName())) {
                            TCPServer.broadcastToSingleClient("/updateBlockIdListRange " + x1 + " " + y1 + " " + x2 + " " + y2 + " " + StringConversion.intDoubleArrayToString(blockIdListRange) + "\n", client.getSocket());
                        }
                    }
                    player.getBlockIdListLoaded()[yPlayer + yDelta][xPlayer + xDelta] = 1;
                }
            }
        }
    }

    // 世界中玩家拾取掉落物
    public void playerCollectItem() {
        // 遍历所有玩家
        for (Entity entityPlayer : entityList) {
            if (entityPlayer instanceof Player && !entityPlayer.isDead()) {
                Player player = (Player) entityPlayer;
                boolean logined = false;
                for (Client client : TCPServer.clientList) {
                    if (client.getPlayerName() != null && client.getPlayerName().equals(player.getName()))
                        logined = client.isCanOperate();
                }
                if (logined) {
                    // 遍历所有掉落物
                    for (Entity entityItem : entityList) {
                        if (entityItem instanceof Item) {
                            Item item = (Item) entityItem;
                            if (item.getTimeNoCollect() == 0) {
                                // 计算掉落物与玩家欧氏距离
                                double itemCenterX = item.getxCenter();
                                double itemCenterY = item.getyCenter();
                                double playerCenterX = player.getxCenter();
                                double playerCenterY = player.getyDown() - player.getHeight() * 0.25;
                                if (calculateDoubleDistance(itemCenterX, itemCenterY, playerCenterX, playerCenterY) < 25) {
                                    if (player.canLoadItem(item.getId())) {
                                        int amountLeft = player.getItem(item.getId(), item.getAmount(), 36, true);
                                        if (amountLeft != 0) {
                                            TCPServer.broadcastToAllClients("/updateSummonItem " + item.getX() + " " + item.getY() + " " + item.getId() + " " + amountLeft + " " + 0 + "\n");
                                        }
                                        TCPServer.broadcastToAllClients("/updateCollectItem " + player.getName() + " " + item.getId() + " " + item.getAmount() + "\n");
                                        TCPServer.broadcastToAllClients("/updateRemoveItem " + item.getIdCode() + "\n");
                                        entityList.remove(item);
                                        item = null;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // 世界中玩家吸附掉落物
    public void playerAttractItem() {
        // 遍历所有玩家
        for (Entity entityPlayer : World.getInstance().getEntityList()) {
            if (entityPlayer instanceof Player) {
                Player player = (Player) entityPlayer;
                // 遍历所有掉落物
                for (Entity entityItem : World.getInstance().getEntityList()) {
                    if (entityItem instanceof Item) {
                        Item item = (Item) entityItem;
                        if (item.getTimeNoCollect() == 0 && !player.isDead()) {
                            // 计算掉落物与玩家欧氏距离
                            double itemCenterX = item.getxCenter();
                            double playerCenterX = player.getxCenter();
                            double distance = abs(itemCenterX - playerCenterX);
                            if (item.getyDown() <= player.getyDown() + 5 && item.getyUp() >= player.getyUp() && distance < 80) {
                                item.setAttractSpeedX(1);
                                if (distance < 2) item.setAttractSpeedX(0);
                                else item.setAttractSpeedX(item.getAttractSpeedX() + 1 / (distance * 0.5));
                                if (item.isCanRight() && itemCenterX < playerCenterX)
                                    item.setX(item.getX() + (int) item.getAttractSpeedX());
                                else if (item.isCanLeft()) item.setX(item.getX() - (int) item.getAttractSpeedX());
                                else item.setAttractSpeedX(0);
                            } else {
                                item.setAttractSpeedX(0);
                            }
                        }
                    }
                }
            }
        }
    }

    // 更新世界怪物
    public void updateMobs() {
        if (Objects.equals(difficulty, "peaceful")) {
            for (Entity entity : entityList) {
                if (entity instanceof Zombie) {
                    TCPServer.broadcastToAllClients("/updateRemoveZombie " + entity.getIdCode() + "\n");
                    entityList.remove(entity);
                }
            }
        } else {
            if (time >= 105000 || time <= 25000 && mobAmount < IDIndex.difficultyToMobAmountMax(difficulty)) {
                for (Entity entity : entityList) {
                    if (entity instanceof Player) {
                        Player player = (Player) entity;
                        Random random = new Random();
                        int randomNumber = random.nextInt(IDIndex.difficultyToRandom(difficulty));
                        if (randomNumber == 0) {
                            int x = random.nextInt(xVision / 25) - xVision / 50;
                            int locationY = player.getyCenter() / 50;
                            int locationX = player.getxCenter() / 50 + x;
                            if (locationX <= 0) locationX = 1;
                            else if (locationX >= width - 1) locationX = width - 2;
                            for (int y = 20; y < height; y++) {
                                if (!(blockIdList[y][locationX] == -1 || IDIndex.blockIdToIsUnTouchable(blockIdList[y][locationX]))) {
                                    locationY = y - 2;
                                    break;
                                }
                            }
                            if (lightIntensity[locationY][locationX] <= 150) {
                                Zombie zombie = new Zombie(World.getInstance().getEntityList().size() - 1 + World.getInstance().getEntityListExtension(), locationX * 50, locationY * 50, 20);
                                TCPServer.broadcastToAllClients("/updateSummonZombie " + (World.getInstance().getEntityList().size() - 1 + World.getInstance().getEntityListExtension()) + " " + locationX * 50 + " " + locationY * 50 + " " + 20 + "\n");
                                World.getInstance().getEntityList().add(zombie);
                            }
                        }
                    }
                }
            }
        }
    }

    // 更新世界怪物数量
    public void updateMobAmount() {
        mobAmount = 0;
        for (Entity entity : entityList) {
            if (entity instanceof Zombie) mobAmount++;
        }
    }

    // 更新世界时间
    public void updateTime() {
        time++;
        if (time >= 120000) time = 0;
    }

    // 渲染光照
    public void renderLightIntensity(int xLeftSide, int xRightSide) {
        updateWorldTimeAlphaDouble();
        // 自然光初次渲染
        for (int x = xLeftSide; x <= xRightSide; x++) {
            if (x <= 0 || x >= getWidth() - 1) continue;
            double worldTimeAlphaDoubleTmp = worldTimeAlphaDouble;
            double brightValue = 1 - getGama();
            if (brightValue < 0) brightValue = 0;
            else if (brightValue > 1) brightValue = 1;
            if (worldTimeAlphaDoubleTmp >= brightValue)
                worldTimeAlphaDoubleTmp = brightValue;
            int lightBlocked = (int) (worldTimeAlphaDoubleTmp * 255);
            for (int y = 1; y <= getHeight() - 2; y++) {
                lightIntensity[y][x] = 255 - lightBlocked;
                if (getBlockIdList()[y][x] != -1) {
                    if (getBlockIdList()[y][x] == 7)
                        lightBlocked += (int) (17 * (brightValue));
                    else if (getBlockIdList()[y][x] != 17)
                        lightBlocked += (int) (50 * (brightValue));
                    if (lightBlocked > 255) lightBlocked = 255;
                    else if (lightBlocked < 0) lightBlocked = 0;
                }
            }
        }
        // 更新视野内光源
        for (int x = xLeftSide; x <= xRightSide; x++) {
            for (int y = 1; y <= getHeight() - 2; y++) {
                if (x < 0 || x >= getWidth()) continue;
                if (IDIndex.blockIdToIsLightSource(getBlockIdList()[y][x]))
                    lightIntensity[y][x] = 1255;
            }
        }
        // 光线衍射
        for (int y = 1; y <= getHeight() - 2; y++) {
            for (int x = xLeftSide; x <= xRightSide; x++) {
                if (x <= 0 || x >= getWidth() - 1) continue;
                if (lightIntensity[y][x - 1] > lightIntensity[y][x] || lightIntensity[y][x + 1] > lightIntensity[y][x]) {
                    if (lightIntensity[y][x - 1] < 0) lightIntensity[y][x - 1] = 0;
                    if (lightIntensity[y][x + 1] < 0) lightIntensity[y][x + 1] = 0;
                    if (lightIntensity[y][x] < 0) lightIntensity[y][x] = 0;
                    int light1 = (int) (lightIntensity[y][x - 1] * 0.8);
                    int light2 = (int) (lightIntensity[y][x + 1] * 0.8);
                    if (getBlockIdList()[y][x - 1] == 7)
                        light1 = (int) (lightIntensity[y][x - 1] * 0.3);
                    else if (getBlockIdList()[y][x - 1] != -1 && !IDIndex.blockIdToIsLightSource(getBlockIdList()[y][x - 1]))
                        light1 = (int) (lightIntensity[y][x - 1] * 0.3);
                    if (getBlockIdList()[y][x + 1] == 7)
                        light2 = (int) (lightIntensity[y][x + 1] * 0.3);
                    else if (getBlockIdList()[y][x + 1] != -1 && !IDIndex.blockIdToIsLightSource(getBlockIdList()[y][x + 1]))
                        light2 = (int) (lightIntensity[y][x + 1] * 0.3);
                    int light3 = max(lightIntensity[y][x], max(light1, light2));
                    lightIntensity[y][x] = light3;
                }
            }
            for (int x = xRightSide; x >= xLeftSide; x--) {
                if (x <= 0 || x >= getWidth() - 1) continue;
                if (lightIntensity[y][x - 1] > lightIntensity[y][x] || lightIntensity[y][x + 1] > lightIntensity[y][x]) {
                    if (lightIntensity[y][x - 1] < 0) lightIntensity[y][x - 1] = 0;
                    else if (lightIntensity[y][x + 1] < 0) lightIntensity[y][x + 1] = 0;
                    int light1 = (int) (lightIntensity[y][x - 1] * 0.8);
                    int light2 = (int) (lightIntensity[y][x + 1] * 0.8);
                    if (getBlockIdList()[y][x - 1] == 7)
                        light1 = (int) (lightIntensity[y][x - 1] * 0.3);
                    else if (getBlockIdList()[y][x - 1] != -1 && !IDIndex.blockIdToIsLightSource(getBlockIdList()[y][x - 1]))
                        light1 = (int) (lightIntensity[y][x - 1] * 0.3);
                    if (getBlockIdList()[y][x + 1] == 7)
                        light2 = (int) (lightIntensity[y][x + 1] * 0.3);
                    else if (getBlockIdList()[y][x + 1] != -1 && !IDIndex.blockIdToIsLightSource(getBlockIdList()[y][x + 1]))
                        light2 = (int) (lightIntensity[y][x + 1] * 0.3);
                    int light3 = max(lightIntensity[y][x], max(light1, light2));
                    lightIntensity[y][x] = light3;
                }
            }
        }
        for (int x = xLeftSide; x <= xRightSide; x++) {
            if (x < 0 || x >= getWidth()) continue;
            for (int y = 1; y < getHeight() - 1; y++) {
                if (lightIntensity[y - 1][x] > lightIntensity[y][x] || lightIntensity[y + 1][x] > lightIntensity[y][x]) {
                    if (lightIntensity[y - 1][x] < 0) lightIntensity[y - 1][x] = 0;
                    if (lightIntensity[y + 1][x] < 0) lightIntensity[y + 1][x] = 0;
                    if (lightIntensity[y][x] < 0) lightIntensity[y][x] = 0;
                    int light1 = (int) (lightIntensity[y - 1][x] * 0.8);
                    int light2 = (int) (lightIntensity[y + 1][x] * 0.8);
                    if (getBlockIdList()[y - 1][x] == 7)
                        light1 = (int) (lightIntensity[y - 1][x] * 0.3);
                    else if (getBlockIdList()[y - 1][x] != -1 && !IDIndex.blockIdToIsLightSource(getBlockIdList()[y - 1][x]))
                        light1 = (int) (lightIntensity[y - 1][x] * 0.3);
                    if (getBlockIdList()[y + 1][x] == 7)
                        light2 = (int) (lightIntensity[y + 1][x] * 0.3);
                    else if (getBlockIdList()[y + 1][x] != -1 && !IDIndex.blockIdToIsLightSource(getBlockIdList()[y + 1][x]))
                        light2 = (int) (lightIntensity[y + 1][x] * 0.3);
                    int light3 = max(lightIntensity[y][x], max(light1, light2));
                    lightIntensity[y][x] = light3;
                }
            }
            for (int y = getHeight() - 2; y > 0; y--) {
                if (lightIntensity[y - 1][x] > lightIntensity[y][x] || lightIntensity[y + 1][x] > lightIntensity[y][x]) {
                    if (lightIntensity[y - 1][x] < 0) lightIntensity[y - 1][x] = 0;
                    if (lightIntensity[y + 1][x] < 0) lightIntensity[y + 1][x] = 0;
                    if (lightIntensity[y][x] < 0) lightIntensity[y][x] = 0;
                    int light1 = (int) (lightIntensity[y - 1][x] * 0.8);
                    int light2 = (int) (lightIntensity[y + 1][x] * 0.8);
                    if (getBlockIdList()[y - 1][x] == 7)
                        light1 = (int) (lightIntensity[y - 1][x] * 0.3);
                    else if (getBlockIdList()[y - 1][x] != -1)
                        light1 = (int) (lightIntensity[y - 1][x] * 0.3);
                    if (getBlockIdList()[y + 1][x] == 7)
                        light2 = (int) (lightIntensity[y + 1][x] * 0.3);
                    else if (getBlockIdList()[y + 1][x] != -1)
                        light2 = (int) (lightIntensity[y + 1][x] * 0.3);
                    int light3 = max(lightIntensity[y][x], max(light1, light2));
                    lightIntensity[y][x] = light3;
                }
            }
        }
        for (int y = 0; y < getHeight(); y++) {
            for (int x = xLeftSide; x <= xRightSide; x++) {
                if (x <= 0 || x >= getWidth() - 1) continue;
                if (lightIntensity[y][x - 1] > lightIntensity[y][x] || lightIntensity[y][x + 1] > lightIntensity[y][x]) {
                    if (lightIntensity[y][x - 1] < 0) lightIntensity[y][x - 1] = 0;
                    if (lightIntensity[y][x + 1] < 0) lightIntensity[y][x + 1] = 0;
                    if (lightIntensity[y][x] < 0) lightIntensity[y][x] = 0;
                    int light1 = (int) (lightIntensity[y][x - 1] * 0.8);
                    int light2 = (int) (lightIntensity[y][x + 1] * 0.8);
                    if (getBlockIdList()[y][x - 1] == 7)
                        light1 = (int) (lightIntensity[y][x - 1] * 0.3);
                    else if (getBlockIdList()[y][x - 1] != -1 && !IDIndex.blockIdToIsLightSource(getBlockIdList()[y][x - 1]))
                        light1 = (int) (lightIntensity[y][x - 1] * 0.3);
                    if (getBlockIdList()[y][x + 1] == 7)
                        light2 = (int) (lightIntensity[y][x + 1] * 0.3);
                    else if (getBlockIdList()[y][x + 1] != -1 && !IDIndex.blockIdToIsLightSource(getBlockIdList()[y][x + 1]))
                        light2 = (int) (lightIntensity[y][x + 1] * 0.3);
                    int light3 = max(lightIntensity[y][x], max(light1, light2));
                    lightIntensity[y][x] = light3;
                }
            }
            for (int x = xRightSide; x >= xLeftSide; x--) {
                if (x <= 0 || x >= getWidth() - 1) continue;
                if (lightIntensity[y][x - 1] > lightIntensity[y][x] || lightIntensity[y][x + 1] > lightIntensity[y][x]) {
                    if (lightIntensity[y][x - 1] < 0) lightIntensity[y][x - 1] = 0;
                    else if (lightIntensity[y][x + 1] < 0) lightIntensity[y][x + 1] = 0;
                    int light1 = (int) (lightIntensity[y][x - 1] * 0.8);
                    int light2 = (int) (lightIntensity[y][x + 1] * 0.8);
                    if (getBlockIdList()[y][x - 1] == 7)
                        light1 = (int) (lightIntensity[y][x - 1] * 0.3);
                    else if (getBlockIdList()[y][x - 1] != -1 && !IDIndex.blockIdToIsLightSource(getBlockIdList()[y][x - 1]))
                        light1 = (int) (lightIntensity[y][x - 1] * 0.3);
                    if (getBlockIdList()[y][x + 1] == 7)
                        light2 = (int) (lightIntensity[y][x + 1] * 0.3);
                    else if (getBlockIdList()[y][x + 1] != -1 && !IDIndex.blockIdToIsLightSource(getBlockIdList()[y][x + 1]))
                        light2 = (int) (lightIntensity[y][x + 1] * 0.3);
                    int light3 = max(lightIntensity[y][x], max(light1, light2));
                    lightIntensity[y][x] = light3;
                }
            }
        }
    }

    // 更新玩家渲染距离内草坪蔓延消亡
    public void updateGrassSpread() {
        for (Entity entity : World.getInstance().getEntityList()) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                int xLeftVision = (player.getxCenter() - xVision) / 50;
                int xRightVision = (player.getxCenter() + xVision) / 50;
                int yUpVision = (player.getyCenter() - yVision) / 50;
                int yDownVision = (player.getyCenter() + yVision) / 50;
                if (xLeftVision < 0) xLeftVision = 0;
                if (xRightVision >= World.getInstance().getWidth()) xRightVision = World.getInstance().getWidth() - 1;
                if (yUpVision < 0) yUpVision = 0;
                if (yDownVision >= World.getInstance().getHeight()) yDownVision = World.getInstance().getHeight() - 1;
                for (int i = xLeftVision; i <= xRightVision; i++) {
                    for (int j = yUpVision; j <= yDownVision; j++) {
                        boolean canSpread = false;
                        boolean canDisappear = false;
                        if (j - 1 >= 0 && World.getInstance().getBlockIdList()[j][i] == 0 && (World.getInstance().getBlockIdList()[j - 1][i] == -1 || IDIndex.blockIdToIsTool(World.getInstance().getBlockIdList()[j - 1][i]) || IDIndex.blockIdToIsTorchLike(World.getInstance().getBlockIdList()[j - 1][i]))) {
                            for (int k = -1; k <= 1; k++) {
                                if (j + k < 0 || j + k > World.getInstance().getHeight() - 1) continue;
                                if (i - 1 >= 0 && World.getInstance().getBlockIdList()[j + k][i - 1] == 1)
                                    canSpread = true;
                                else if (i + 1 <= World.getInstance().getWidth() - 1 && World.getInstance().getBlockIdList()[j + k][i + 1] == 1)
                                    canSpread = true;
                            }
                        } else if (World.getInstance().getBlockIdList()[j][i] == 1 && World.getInstance().getBlockIdList()[j - 1][i] != -1 && !IDIndex.blockIdToIsTool(World.getInstance().getBlockIdList()[j - 1][i]) && !IDIndex.blockIdToIsTorchLike(World.getInstance().getBlockIdList()[j - 1][i])) {
                            canDisappear = true;
                        }
                        if (World.getInstance().getBlockIdList()[j][i] == 0 && canSpread) {
                            Random random = new Random();
                            int randomNumber = random.nextInt(1000);
                            if (randomNumber == 0) {
                                World.getInstance().getBlockIdList()[j][i] = 1;
                                TCPServer.broadcastToAllClients("/updateBlockIdListSingleNoSound " + i + " " + j + " " + 1 + "\n");
                            }
                        }
                        if (World.getInstance().getBlockIdList()[j][i] == 1 && canDisappear) {
                            Random random = new Random();
                            int randomNumber = random.nextInt(1000);
                            if (randomNumber == 0) {
                                World.getInstance().getBlockIdList()[j][i] = 0;
                                TCPServer.broadcastToAllClients("/updateBlockIdListSingleNoSound " + i + " " + j + " " + 0 + "\n");
                            }
                        }
                    }
                }
            }
        }
    }

    // 世界中合并掉落物
    public void assembleItem() {
        // 定义当前掉落物列表
        List<Item> itemList = new ArrayList<>();
        // 遍历所有掉落物，生成当前掉落物列表

        for (Entity entityItem : entityList) {
            if (entityItem instanceof Item) {
                Item item = (Item) entityItem;
                itemList.add(item);
            }
        }
        // 遍历当前所有掉落物
        for (int i = 0; i < itemList.size(); i++) {
            Item item1 = itemList.get(i);
            // 避免重复遍历掉落物
            for (int j = i + 1; j < itemList.size(); j++) {
                Item item2 = itemList.get(j);
                // 如果掉落物有相同id
                if (item1 != null && item1 != item2 && item1.getId() == item2.getId()) {
                    // 计算欧式距离
                    double item2CenterX = item1.getxCenter();
                    double item2CenterY = item1.getyCenter();
                    double item3CenterX = item2.getxCenter();
                    double item3CenterY = item2.getyCenter();
                    // 如果欧氏距离小于30且堆叠后数量小于最大堆叠数
                    if (calculateDoubleDistance(item2CenterX, item2CenterY, item3CenterX, item3CenterY) < 30 && item1.getAmount() + item2.getAmount() <= IDIndex.blockIdToMaxAmount(item1.getId())) {
                        // 生成新堆叠掉落物
                        int idCode = World.getInstance().getEntityList().size() - 1 + World.getInstance().getEntityListExtension();
                        TCPServer.broadcastToAllClients("/updateRemoveItem " + item1.getIdCode() + "\n");
                        TCPServer.broadcastToAllClients("/updateRemoveItem " + item2.getIdCode() + "\n");
                        TCPServer.broadcastToAllClients("/updateSummonItem " + idCode + " " + ((item1.getX() + item2.getX()) / 2) + " " + ((item1.getY() + item2.getY()) / 2) + " " + item1.getId() + " " + (item1.getAmount() + item2.getAmount()) + " " + 0 + "\n");
                        // 移除旧掉落物
                        entityList.remove(item1);
                        entityList.remove(item2);
                        Item newItem = new Item(idCode, (item1.getX() + item2.getX()) / 2, (item1.getY() + item2.getY()) / 2, item1.getId(), item1.getAmount() + item2.getAmount(), 0);
                    }
                }
            }
        }
    }

    // 计算整型欧氏距离
    public static double calculateIntDistance(int x1, int y1, int x2, int y2) {
        double deltaX = x2 - x1;
        double deltaY = y2 - y1;
        // 使用欧几里德距离公式计算距离
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        return distance;
    }

    // 计算浮点型欧氏距离
    public static double calculateDoubleDistance(double x1, double y1, double x2, double y2) {
        double deltaX = x2 - x1;
        double deltaY = y2 - y1;
        // 使用欧几里德距离公式计算距离
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        return distance;
    }

    public void updateWorldTimeAlphaDouble() {
        if (World.getInstance().getTime() >= 0 && World.getInstance().getTime() < 25000)
            worldTimeAlphaDouble = 1;
        else if (World.getInstance().getTime() >= 25000 && World.getInstance().getTime() < 40000)
            worldTimeAlphaDouble = 1 - ((World.getInstance().getTime() - 25000) / 15000.0);
        else if (World.getInstance().getTime() >= 40000 && World.getInstance().getTime() < 90000)
            worldTimeAlphaDouble = 0;
        else if (World.getInstance().getTime() >= 90000 && World.getInstance().getTime() <= 105000)
            worldTimeAlphaDouble = 1 - ((105000 - World.getInstance().getTime()) / 15000.0);
        else if (World.getInstance().getTime() >= 105000 && World.getInstance().getTime() <= 120000)
            worldTimeAlphaDouble = 1;
    }

    public static World getInstance() {
        return WorldInstance.world;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getVoidSize() {
        return voidSize;
    }

    public void setVoidSize(int voidSize) {
        this.voidSize = voidSize;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    public double getGama() {
        return gama;
    }

    public void setGama(double gama) {
        this.gama = gama;
    }

    public int getxSpawn() {
        return xSpawn;
    }

    public void setxSpawn(int xSpawn) {
        this.xSpawn = xSpawn;
    }

    public int getySpawn() {
        return ySpawn;
    }

    public void setySpawn(int ySpawn) {
        this.ySpawn = ySpawn;
    }

    public double getGravity() {
        return gravity;
    }

    public void setGravity(double gravity) {
        this.gravity = gravity;
    }

    public double getAirResistance() {
        return airResistance;
    }

    public void setAirResistance(double airResistance) {
        this.airResistance = airResistance;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public List<Entity> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<Entity> entityList) {
        this.entityList = entityList;
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(List<Player> playerList) {
        this.playerList = playerList;
    }

    public int[][] getBlockIdList() {
        return blockIdList;
    }

    public void setBlockIdList(int[][] blockIdList) {
        this.blockIdList = blockIdList;
    }

    public void setBlockIdListSingle(int x, int y, int id) {
        this.blockIdList[y][x] = id;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public int getMobAmountMax() {
        return mobAmountMax;
    }

    public void setMobAmountMax(int mobAmountMax) {
        this.mobAmountMax = mobAmountMax;
    }

    public int getMobAmount() {
        return mobAmount;
    }

    public void setMobAmount(int mobAmount) {
        this.mobAmount = mobAmount;
    }

    public int getxVision() {
        return xVision;
    }

    public void setxVision(int xVision) {
        this.xVision = xVision;
    }

    public List<String> getOpList() {
        return opList;
    }

    public void setOpList(List<String> opList) {
        this.opList = opList;
    }

    public double getWorldTimeAlphaDouble() {
        return worldTimeAlphaDouble;
    }

    public void setWorldTimeAlphaDouble(double worldTimeAlphaDouble) {
        this.worldTimeAlphaDouble = worldTimeAlphaDouble;
    }

    public int[][] getLightIntensity() {
        return lightIntensity;
    }

    public void setLightIntensity(int[][] lightIntensity) {
        this.lightIntensity = lightIntensity;
    }

    public int getEntityListExtension() {
        return entityListExtension;
    }

    public void setEntityListExtension(int entityListExtension) {
        this.entityListExtension = entityListExtension;
    }

    public List<String> getBanList() {
        return banList;
    }

    public void setBanList(List<String> banList) {
        this.banList = banList;
    }

    public List<String> getBanIpList() {
        return banIpList;
    }

    public void setBanIpList(List<String> banIpList) {
        this.banIpList = banIpList;
    }

    public int getyVision() {
        return yVision;
    }

    public void setyVision(int yVision) {
        this.yVision = yVision;
    }

    public List<Area> getAreaProtectedList() {
        return areaProtectedList;
    }

    public void setAreaProtectedList(List<Area> areaProtectedList) {
        this.areaProtectedList = areaProtectedList;
    }
}
