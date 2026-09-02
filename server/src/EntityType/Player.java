package EntityType;

import Base.IDIndex;
import Element.Entity;
import WorldTool.World;

public class Player extends Entity {
    // ★定义玩家名
    private String name = null;
    // 定义玩家模式
    // survival creative
    private String gameMode = "survival";
    // 定义玩家朝向
    // left right
    private String faceTo = "left";
    // 定义玩家当前运动状态
    // stand    walk    run
    private String moveState = "stand";
    // 定义玩家当前工具状态
    // hand pickaxe axe shovel
    private String toolState = "hand";
    // 定义玩家是否死亡物品不掉落
    private boolean keepInventory = true;
    // 定义玩家是否开启自动跳跃
    private boolean autoJump = true;
    // 定义走路跑步计时器，用于播放运动动画
    private int walkTimer = 0;
    private int runTimer = 0;
    // 定义点击计时器，用于播放手部动画
    private int clickTimer = 0;
    // 定义玩家选中的物品栏
    private int itemBarChosen = 0;
    // 定义玩家物品栏
    private int[] itemBarId = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    // 定义玩家物品栏数量
    private int[] itemBarAmount = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    // 定义玩家密码
    private String password = null;
    // 定义玩家的家
    private int xHome, yHome;
    // 定义玩家加载地形列表
    private int[][] blockIdListLoaded = new int[(World.getInstance().getHeight() - 1) / 50][(World.getInstance().getWidth() - 1) / 50];


    public Player() {
        setIdCode(-2);
        // 设置实体类型为玩家
        setType("player");
        // 设置玩家可以复活
        setCanRespawn(true);
        // 设置玩家大小
        setWidth(20);
        setHeight(95);
        // 设置实体大小修正值
        setxLeftRevision((70 - getWidth()) / 2);
        setxRightRevision((70 - getWidth()) / 2 + getWidth());
        setyUpRevision(0);
        setyDownRevision(getHeight());
    }

    public Player(String name) {
        setIdCode(-2);
        // 设置玩家名
        this.name = name;
        World.getInstance().getEntityList().add(this);
        World.getInstance().getPlayerList().add(this);
        // 设置玩家重生点
        setxSpawn(World.getInstance().getxSpawn());
        setySpawn(World.getInstance().getySpawn());
        this.xHome = World.getInstance().getxSpawn();
        this.yHome = World.getInstance().getySpawn();
        // 设置玩家坐标
        setX(World.getInstance().getxSpawn());
        setY(World.getInstance().getySpawn());
        // 设置实体类型为玩家
        setType("player");
        // 设置玩家可以复活
        setCanRespawn(true);
        // 设置玩家大小
        setWidth(20);
        setHeight(95);
        // 设置实体大小修正值
        setxLeftRevision((70 - getWidth()) / 2);
        setxRightRevision((70 - getWidth()) / 2 + getWidth());
        setyUpRevision(0);
        setyDownRevision(getHeight());
    }

    // 更新玩家背包是否能继续装下某个物品
    public boolean canLoadItem(int blockId) {
        for (int i = 0; i < 36; i++) {
            if (itemBarId[i] == blockId) {
                if (itemBarAmount[i] < IDIndex.blockIdToMaxAmount(blockId))
                    return true;
            }
        }
        for (int i = 0; i < 36; i++) {
            if (itemBarId[i] == -1)
                return true;
        }
        return false;
    }

    // 玩家拾取掉落物，返回未被拾取的数量
    public int getItem(int id, int amount, int searchSize, boolean soundOn) {
        // 定义还未被捡完的掉落物数量
        int amountLeft = amount;
        // 如果不是工具
        if (!IDIndex.blockIdToIsTool(id)) {
            // 搜索背包内是否已经存在该物品
            for (int i = 0; i < searchSize; i++)
                // 如果存在
                if (itemBarId[i] == id) {
                    // 如果物品数量小于最大堆叠数
                    if (itemBarAmount[i] < IDIndex.blockIdToMaxAmount(id)) {
                        // 如果物品数量加上全部物品多于最大堆叠数
                        if (itemBarAmount[i] + amountLeft > IDIndex.blockIdToMaxAmount(id)) {
                            // 掉落物剩余数量扣除已经捡走的数量
                            amountLeft -= (IDIndex.blockIdToMaxAmount(id) - itemBarAmount[i]);
                            // 该物品堆叠达到上限，设为最大堆叠数
                            itemBarAmount[i] = IDIndex.blockIdToMaxAmount(id);
                        } else {
                            // 否则该物品直接堆叠全部掉落物数量
                            itemBarAmount[i] += amountLeft;
                            // 掉落物剩余数量扣除已经捡走的数量
                            amountLeft = 0;
                            // 退出循环
                            break;
                        }
                    }
                }
        }
        // 如果掉落物数量还有剩余
        if (amountLeft > 0)
            // 搜寻背包内第一个空位
            for (int i = 0; i < searchSize; i++)
                // 如果搜索到了
                if (itemBarId[i] == -1) {
                    // 如果物品剩余数量小于等于最大堆叠数
                    if (amountLeft <= IDIndex.blockIdToMaxAmount(id)) {
                        // 该物品栏直接堆叠剩余数量
                        itemBarAmount[i] += amountLeft;
                        // 设置此物品栏存在该物品
                        itemBarId[i] = id;
                        // 掉落物剩余数量扣除已经捡走的数量
                        amountLeft = 0;
                        // 退出循环
                        break;
                    } else {
                        // 否则堆叠达到上限，设为最大堆叠数
                        itemBarAmount[i] = IDIndex.blockIdToMaxAmount(id);
                        // 设置此物品栏存在该物品
                        itemBarId[i] = id;
                        // 掉落物剩余数量扣除最大堆叠数
                        amountLeft -= IDIndex.blockIdToMaxAmount(id);
                    }
                }
        if (itemBarAmount[itemBarChosen] == 0)
            itemBarId[itemBarChosen] = -1;
        // 返回剩余数量
        return amountLeft;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGameMode() {
        return gameMode;
    }

    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
    }

    public String getFaceTo() {
        return faceTo;
    }

    public void setFaceTo(String faceTo) {
        this.faceTo = faceTo;
    }

    public String getMoveState() {
        return moveState;
    }

    public void setMoveState(String moveState) {
        this.moveState = moveState;
    }

    public String getToolState() {
        return toolState;
    }

    public void setToolState(String toolState) {
        this.toolState = toolState;
    }

    public boolean isKeepInventory() {
        return keepInventory;
    }

    public void setKeepInventory(boolean keepInventory) {
        this.keepInventory = keepInventory;
    }

    public boolean isAutoJump() {
        return autoJump;
    }

    public void setAutoJump(boolean autoJump) {
        this.autoJump = autoJump;
    }

    public int[] getItemBarId() {
        return itemBarId;
    }

    public void setItemBarId(int[] itemBarId) {
        this.itemBarId = itemBarId;
    }

    public int[] getItemBarAmount() {
        return itemBarAmount;
    }

    public void setItemBarAmount(int[] itemBarAmount) {
        this.itemBarAmount = itemBarAmount;
    }

    public void setItemBarIdSingle(int i, int itemBarId) {
        this.itemBarId[i] = itemBarId;
    }

    public void setItemBarAmountSingle(int i, int itemBarAmount) {
        this.itemBarAmount[i] = itemBarAmount;
    }

    public int getWalkTimer() {
        return walkTimer;
    }

    public void setWalkTimer(int walkTimer) {
        this.walkTimer = walkTimer;
    }

    public int getRunTimer() {
        return runTimer;
    }

    public void setRunTimer(int runTimer) {
        this.runTimer = runTimer;
    }

    public int getClickTimer() {
        return clickTimer;
    }

    public void setClickTimer(int clickTimer) {
        this.clickTimer = clickTimer;
    }

    public int getItemBarChosen() {
        return itemBarChosen;
    }

    public void setItemBarChosen(int itemBarChosen) {
        this.itemBarChosen = itemBarChosen;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getxHome() {
        return xHome;
    }

    public void setxHome(int xHome) {
        this.xHome = xHome;
    }

    public int getyHome() {
        return yHome;
    }

    public void setyHome(int yHome) {
        this.yHome = yHome;
    }

    public int[][] getBlockIdListLoaded() {
        return blockIdListLoaded;
    }

    public void setBlockIdListLoaded(int[][] blockIdListLoaded) {
        this.blockIdListLoaded = blockIdListLoaded;
    }
}

