package EntityType;

import Base.Command;
import Base.IDIndex;
import Base.TCPServer;
import Element.Entity;
import WorldTool.World;

import java.util.Objects;
import java.util.Random;

import static java.lang.Math.*;

public class Zombie extends Entity {
    // 定义僵尸朝向
    // left right
    private String faceTo = "left";
    // 定义僵尸当前运动状态
    // stand    walk
    private String moveState = "stand";
    // 定义僵尸锁定的玩家
    private Player player = null;
    // 定义僵尸移动计时器，用于播放走路声音
    private int moveTimer = 0;
    // 定义僵尸走路计时器，用于播放运动动画
    private int walkTimer = 0;
    // 定义僵尸移动频率
    private int moveFrequency = 20;
    // 定义僵尸攻击冷却时间
    private int attackCD = 100;
    // 定义僵尸攻击范围
    private int attackRange = 50;
    // 定义僵尸移动目的地
    private int moveToX = 0;
    // 定义僵尸是否需要移动
    private boolean onMoving = false;

    public Zombie(int x, int y) {
        // 设置实体类型
        setType("zombie");
        // 设置坐标
        setX(x);
        setY(y);
        // 设置大小
        setWidth(20);
        setHeight(95);
        // 设置实体大小修正值
        setxLeftRevision((70 - getWidth()) / 2);
        setxRightRevision((70 - getWidth()) / 2 + getWidth());
        setyUpRevision(0);
        setyDownRevision(getHeight());
    }

    public Zombie(int idCode, int x, int y, int health) {
        setIdCode(idCode);
        setHealth(health);
        // 设置实体类型
        setType("zombie");
        // 设置坐标
        setX(x);
        setY(y);
        // 设置大小
        setWidth(20);
        setHeight(95);
        // 设置实体大小修正值
        setxLeftRevision((70 - getWidth()) / 2);
        setxRightRevision((70 - getWidth()) / 2 + getWidth());
        setyUpRevision(0);
        setyDownRevision(getHeight());
    }


    public Zombie() {
        // 设置实体类型
        setType("zombie");
        // 设置大小
        setWidth(20);
        setHeight(95);
        // 设置实体大小修正值
        setxLeftRevision((70 - getWidth()) / 2);
        setxRightRevision((70 - getWidth()) / 2 + getWidth());
        setyUpRevision(0);
        setyDownRevision(getHeight());
    }

    public void stopMoveX() {
        // 改变僵尸水平速度
        setxSpeed(0);
        // 改变僵尸运动状态
        moveState = "stand";
        if (Objects.equals(faceTo, "left"))
            TCPServer.broadcastToAllClients("/updateTimer other " + getIdCode() + " " + walkTimer + " 1 " + "0\n");
        else if (Objects.equals(faceTo, "right"))
            TCPServer.broadcastToAllClients("/updateTimer other " + getIdCode() + " " + walkTimer + " 2 " + "0\n");
    }

    // 僵尸向左走
    public void walkLeft() {
        if (isCanLeft() && !isInParalysis()) {
            // 改变速度
            setxSpeed(-1);
            // 改变朝向
            faceTo = "left";
            // 改变运动状态
            moveState = "walk";
            // 改变移动频率
            moveFrequency = 50;
        }
    }

    // 僵尸向右走
    public void walkRight() {
        if (isCanRight() && !isInParalysis()) {
            // 改变速度
            setxSpeed(1);
            // 改变朝向
            faceTo = "right";
            // 改变运动状态
            moveState = "walk";
            // 改变移动频率
            moveFrequency = 50;
        }
    }

    // 僵尸跳跃
    public void jump() {
        // 判断是否站在方块上
        if (isOnGround() && !isUnderCeil()) {
            // 更改上升速度
            setJumpSpeed(-8.0);
        }
    }

    // 更新僵尸选中玩家
    public void updatePlayerSelected() {
        double distanceMin = World.getInstance().getWidth() + World.getInstance().getHeight();
        for (Entity entity : World.getInstance().getEntityList()) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                if (Command.isCanOperate(player.getName())) {
                    if (abs(player.getxCenter() - getxCenter()) <= World.getInstance().getxVision())
                        if (!Objects.equals(player.getGameMode(), "creative") && !player.isDead()) {
                            double distance = World.calculateIntDistance(getxCenter(), getyCenter(), player.getxCenter(), player.getyCenter());
                            if (distance < distanceMin) {
                                this.player = player;
                                distanceMin = distance;
                            }
                        } else if (this.player == player) {
                            this.player = null;
                            stopMoveX();
                            moveTimer = 0;
                            walkTimer = 0;
                        }
                }
            }
        }
    }

    // 更新僵尸移动到指定位置
    public void updateZombieMoveToX(int xPurpose) {
        if (getxCenter() < xPurpose) {
            walkRight();
            if (!isCanRight())
                if (!updateCanJumpWall()) stopMoveX();
        } else if (getxCenter() > xPurpose) {
            walkLeft();
            if (!isCanLeft())
                if (!updateCanJumpWall()) stopMoveX();
        } else if (getxCenter() == xPurpose) {
            stopMoveX();
            onMoving = false;
        }
    }

    // 更新僵尸移动策略
    public void updateZombieMoveStrategy() {
        if (player != null) {
            moveToX = player.getxCenter();
            onMoving = true;
        } else {
            Random random = new Random();
            int randomNumber = random.nextInt(500);
            if (randomNumber == 0) {
                moveToX = getxCenter() + random.nextInt(500) - 250;
                onMoving = true;
            }
        }
    }

    // 更新僵尸能否往上跳
    public boolean updateCanJumpWall() {
        if ((getyCenter() - 25) / 50 - 1 >= 0 && (getyCenter() - 25) / 50 <= World.getInstance().getHeight() - 1) {
            if (!isCanLeft() && getxCenter() / 50 - 1 >= 0 && getxCenter() / 50 <= World.getInstance().getWidth() - 1)
                if (IDIndex.blockIdToIsUnTouchable(World.getInstance().getBlockIdList()[(getyCenter() - 25) / 50][getxCenter() / 50 - 1]) && IDIndex.blockIdToIsUnTouchable(World.getInstance().getBlockIdList()[(getyCenter() - 25) / 50 - 1][getxCenter() / 50 - 1]) && IDIndex.blockIdToIsUnTouchable(World.getInstance().getBlockIdList()[(getyCenter() - 25) / 50 - 1][getxCenter() / 50])) {
                    jump();
                    return true;
                }
            if (!isCanRight() && getxCenter() / 50 + 1 <= World.getInstance().getWidth() - 1 && getxCenter() / 50 >= 0)
                if (IDIndex.blockIdToIsUnTouchable(World.getInstance().getBlockIdList()[(getyCenter() - 25) / 50][getxCenter() / 50 + 1]) && IDIndex.blockIdToIsUnTouchable(World.getInstance().getBlockIdList()[(getyCenter() - 25) / 50 - 1][getxCenter() / 50 + 1]) && IDIndex.blockIdToIsUnTouchable(World.getInstance().getBlockIdList()[(getyCenter() - 25) / 50 - 1][getxCenter() / 50])) {
                    jump();
                    return true;
                }
        }
        return false;
    }

    // 僵尸攻击
    public void attack() {
        if (player != null && attackCD == 0 && !isInParalysis()) {
            if (player.getyCenter() >= getyCenter() - 50 && player.getyCenter() <= getyCenter() + 50 && ((Objects.equals(faceTo, "left") && player.getxCenter() <= getxCenter() && player.getxCenter() >= getxCenter() - attackRange) || (Objects.equals(faceTo, "right") && player.getxCenter() >= getxCenter() && player.getxCenter() <= getxCenter() + attackRange))) {
                int xMin = min(getxCenter() / 50, player.getxCenter() / 50);
                int xMax = max(getxCenter() / 50, player.getxCenter() / 50);
                boolean canAttack = true;
                for (int x = xMin; x <= xMax; x++) {
                    if (!(World.getInstance().getBlockIdList()[(getyCenter() - 20) / 50][x] == -1 || IDIndex.blockIdToIsTorchLike(World.getInstance().getBlockIdList()[(getyCenter() - 25) / 50][x])))
                        canAttack = false;
                }
                if (canAttack && !Objects.equals(player.getGameMode(), "creative") && !player.isDead()) {
                    if (!player.getGameMode().equals("creative") && !player.isDead()) {
                        player.setDamageTimer(20);
                        TCPServer.broadcastToAllClients("/updateAttack player " + player.getName() + " " + faceTo + " " + 3 + " zombie\n");
                        if (!player.isDead() && player.getHealth() <= 3) {
                            player.setDead(true);
                            System.out.println(player.isDead());
                            player.hurt(3);
                        }
                    }
                    attackCD = 100;
                }

            }
        }
    }


    // 更新僵尸攻击CD
    public void updateAttackCD() {
        if (attackCD != 0) attackCD--;
        else if (attackCD < 0) attackCD = 0;
    }


    // 更新僵尸数据
    public void updateZombieData() {
        if (World.getInstance().getTime() % 100 == 0)
            // 更新僵尸选中玩家
            updatePlayerSelected();
        // 更新僵尸移动策略
        updateZombieMoveStrategy();
        // 更新僵尸攻击CD
        updateAttackCD();
        // 僵尸攻击
        attack();
        // 更新僵尸运动目的地
        if (onMoving)
            updateZombieMoveToX(moveToX);
        // 更新麻痹状态停止运动
        updateDataToClient();
        updateWalkTimer();
        playWalkSound();
    }

    // 更新僵尸移动声音
    public void playWalkSound() {
        // 如果不处于静止状态且已经落地
        if (!Objects.equals(moveState, "stand") && isOnGround()) {
            // 初始化脚下方块id
            int id = -1;
            for (int i = getxLeftCollision(); i <= getxRightCollision(); i++) {
                // 如果方块处于脚下高度
                if (World.getInstance().getBlockIdList()[getyDown() / 50][i] != -1)
                    // 如果方块在玩家宽度内
                    if (i * 50 + World.getInstance().getBlockSize() > getxLeft() && i * 50 < getxRight()) {
                        // 设置脚下方块id
                        id = World.getInstance().getBlockIdList()[getyDown() / 50][i];
                        break;
                    }
            }
            if (id != -1)
                // 如果移动计时器未达到移动频率，计时器加一
                if (moveTimer <= moveFrequency) moveTimer++;
                else {
                    TCPServer.broadcastToAllClients("/updateSound " + getxCenter() + " " + getyCenter() + " step " + id + "\n");
                    // 移动计时器归零
                    moveTimer = 0;
                }
        }
    }

    public void updateDataToClient() {
        TCPServer.broadcastToAllClients("/updateLocation other " + getIdCode() + " " + getX() + " " + getY() + "\n");
        TCPServer.broadcastToAllClients("/updateTimer other " + getIdCode() + " " + walkTimer + " 0 0\n");
        TCPServer.broadcastToAllClients("/updateState other " + getIdCode() + " " + faceTo + " " + moveState + "\n");
    }

    // 更新僵尸材质
    public void updateWalkTimer() {
        if (Objects.equals(moveState, "walk") && Objects.equals(faceTo, "left")) {
            if (walkTimer >= 100) walkTimer = 0;
            walkTimer++;
        } else if (Objects.equals(moveState, "walk") && Objects.equals(faceTo, "right")) {
            if (walkTimer >= 100) walkTimer = 0;
            walkTimer++;
        } else {
            walkTimer = 0;
        }
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

    public int getWalkTimer() {
        return walkTimer;
    }

    public void setWalkTimer(int walkTimer) {
        this.walkTimer = walkTimer;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getMoveTimer() {
        return moveTimer;
    }

    public void setMoveTimer(int moveTimer) {
        this.moveTimer = moveTimer;
    }

    public int getMoveFrequency() {
        return moveFrequency;
    }

    public void setMoveFrequency(int moveFrequency) {
        this.moveFrequency = moveFrequency;
    }

    public int getAttackCD() {
        return attackCD;
    }

    public void setAttackCD(int attackCD) {
        this.attackCD = attackCD;
    }

    public int getAttackRange() {
        return attackRange;
    }

    public void setAttackRange(int attackRange) {
        this.attackRange = attackRange;
    }

    public int getMoveToX() {
        return moveToX;
    }

    public void setMoveToX(int moveToX) {
        this.moveToX = moveToX;
    }

    public boolean isOnMoving() {
        return onMoving;
    }

    public void setOnMoving(boolean onMoving) {
        this.onMoving = onMoving;
    }
}
