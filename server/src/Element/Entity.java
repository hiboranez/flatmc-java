package Element;

import Base.IDIndex;
import WorldTool.World;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.lang.Math.abs;

public abstract class Entity {
    // ★定义实体类型
    private String type = null;
    // ★定义实体材质包
    private List<BufferedImage> textureList = new ArrayList<>();
    // ★定义实体当前材质
    private BufferedImage textureCurrent = null;
    // 定义实体编号
    private int idCode;
    // **定义实体大小
    private int width = 0;
    private int height = 0;
    // **定义实体绘制坐标
    private int x = 0, y = 0;
    // 定义实体中心坐标
    private int xCenter = 0, yCenter = 0;
    // 定义实体碰撞坐标
    private int xLeft = x;
    private int xRight = x + width;
    private int yUp = y;
    private int yDown = y + height;
    // 定义实体碰撞坐标修正值
    private int xLeftRevision = 0;
    private int xRightRevision = 0;
    private int yUpRevision = 0;
    private int yDownRevision = 0;
    // 定义碰撞范围
    private int xLeftCollision = (xCenter - 500) / 50;
    private int xRightCollision = (xCenter + 500) / 50;
    private int yUpCollision = (yCenter - 500) / 50;
    private int yDownCollision = (yCenter + 500) / 50;
    // 定义实体重生点
    private int xSpawn = 0;
    private int ySpawn = 0;
    // 定义实体生命上限
    private int healthMax = 20;
    // 定义实体生命值
    private int health = 20;
    // 定义玩家上次的血量
    private int lastHealth = 20;
    // 定义回血等待时间
    private int healWaitingTime = 0;
    // 定义实体水平移动速度
    private double xSpeed = 0;
    // 定义实体垂直移动速度
    private double ySpeed = 0;
    // 定义实体跳跃速度
    private double jumpSpeed = 0;
    // 定义实体掉落速度
    private double fallSpeed = 0;
    // 定义实体速度上限
    private int speedMax = 50;
    // 定义实体最小速度
    private double speedMin = 0.2;
    // 定义实体头上房顶纵坐标
    private int entityUpCeilY = 0;
    // 定义实体脚下地面纵坐标
    private int entityDownGroundY = 0;
    // 定义实体左侧墙体横坐标
    private int entityLeftWallX = 0;
    // 定义实体右侧墙体横坐标
    private int entityRightWallX = 0;
    // 定义实体窒息计时器
    private int chokeTimer = 0;
    // 定义玩家受伤闪烁计时器
    private int damageTimer = 0;
    // 定义麻痹计时器
    private int paralysisTimer = 0;
    // 定义玩家是否处于麻痹状态
    private boolean inParalysis = false;
    // 定义实体是否能复活
    private boolean canRespawn = false;
    // 定义实体是否死亡
    private boolean dead = false;
    // 定义实体是否无敌
    private boolean canHurt = true;
    // 定义实体是否受到重力影响
    private boolean hasGravity = true;
    // 定义实体是否能水平移动
    private boolean canMoveX = true;
    // 定义实体是否能跳跃
    private boolean canJump = true;
    // 定义实体是否能够飞行
    private boolean flying = false;
    // 定义实体顶头状态
    private boolean underCeil = false;
    // 定义实体落地状态
    private boolean onGround = false;
    // 定义实体是否可以往右走
    private boolean canRight = true;
    // 定义实体是否可以往左走
    private boolean canLeft = true;
    // 定义实体是否在方块内
    private boolean inBlock = false;
    // 定义实体是否在燃烧
    private boolean onFire = false;

    public Entity() {
    }

    // 获取实体在方块内深度，若不在方块内返回0
    public int distanceInBlock(String entitySide, String blockHalf, int entitySideLocation, int blockHalfLocation, int blockSize) {
        // 如果比较的是实体碰撞坐标下边界和方块上半侧
        if (Objects.equals(entitySide, "down") && Objects.equals(blockHalf, "up"))
            // 如果实体在方块半侧内
            if (blockHalfLocation < entitySideLocation && blockHalfLocation + blockSize >= entitySideLocation)
                // 返回在半侧内多深
                return abs(entitySideLocation - blockHalfLocation);

        // 如果比较的是实体碰撞坐标上边界和方块下半侧
        if (Objects.equals(entitySide, "up") && Objects.equals(blockHalf, "down"))
            // 如果实体在方块半侧内
            if (blockHalfLocation > entitySideLocation && blockHalfLocation - blockSize < entitySideLocation)
                // 返回在半侧内多深
                return abs(entitySideLocation - blockHalfLocation);

        // 如果比较的是实体碰撞坐标左边界和方块右半侧
        if (Objects.equals(entitySide, "left") && Objects.equals(blockHalf, "right"))
            // 如果实体在方块半侧内
            if (blockHalfLocation > entitySideLocation && blockHalfLocation - blockSize <= entitySideLocation)
                // 返回在半侧内多深
                return abs(entitySideLocation - blockHalfLocation);

        // 如果比较的是实体碰撞坐标右边界和方块左半侧
        if (Objects.equals(entitySide, "right") && Objects.equals(blockHalf, "left"))
            // 如果实体在方块半侧内
            if (blockHalfLocation < entitySideLocation && blockHalfLocation + blockSize > entitySideLocation)
                // 返回在半侧内多深
                return abs(entitySideLocation - blockHalfLocation);

        // 如果实体不在方块内，返回0，即没有深度
        return 0;
    }


    // 实体重生
    public void respawn() {
        x = World.getInstance().getxSpawn();
        y = World.getInstance().getySpawn();
        xSpeed = 0;
        ySpeed = 0;
        fallSpeed = 0;
        jumpSpeed = 0;
        health = healthMax;
    }

    // 实体死亡
    public void kill() {
        World.getInstance().getEntityList().remove(this);
    }

    // 实体进行水平移动
    public void moveX(int entitySideLocation, int blockHalfLocation) {
        // 如果是向左移动
        if ((int) xSpeed < 0) {
            // 只移动到与方块贴合
            x -= abs((int) xSpeed) - distanceInBlock("left", "right", entitySideLocation + (int) xSpeed, blockHalfLocation, World.getInstance().getBlockSize());
        }
        // 如果是向右移动
        if ((int) xSpeed > 0) {
            // 只移动到与方块贴合
            x += abs((int) xSpeed) - distanceInBlock("right", "left", entitySideLocation + (int) xSpeed, blockHalfLocation, World.getInstance().getBlockSize());
        }
    }

    // 实体进行垂直移动
    public void moveY(int entitySideLocation, int blockHalfLocation) {
        // 如果是向上移动
        if ((int) ySpeed < 0) {
            // 只移动到与方块贴合
            y -= abs((int) ySpeed) - distanceInBlock("up", "down", entitySideLocation + (int) ySpeed, blockHalfLocation, World.getInstance().getBlockSize());
        }
        // 如果是向下移动
        if ((int) ySpeed > 0) {
            // 只移动到与方块贴合
            y += abs((int) ySpeed) - distanceInBlock("down", "up", entitySideLocation + (int) ySpeed, blockHalfLocation, World.getInstance().getBlockSize());
        }
    }

    // 实体停止水平移动
    public void stopMoveX() {
        // 水平速度归零
        xSpeed = 0;
    }

    // 实体停止垂直移动
    public void stopMoveY() {
        // 垂直速度归零
        ySpeed = 0;
    }

    // 实体回血
    public void heal() {
        // 如果长时间未受伤，开始回血
        if (canHurt) {
            // 如果未达最大血量
            if (health < healthMax)
                // 增加回血等待时间
                healWaitingTime++;
                // 否则回血等待时间设为0
            else healWaitingTime = 0;
            // 如果等待时间达到1000
            if (healWaitingTime > 1000) {
                // 血量加一
                health++;
                // 等待时间重设为750
                healWaitingTime = 750;
            }
            // 如果实体死亡，不能回血
            if (dead) healWaitingTime = 0;
        }
    }

    // 实体扣血
    public void hurt(int damage) {
        // 如果生命值能够承受伤害
        if (health >= damage)
            // 生命值直接减去伤害值
            health -= damage;
        else
            // 否则生命值直接归零
            health = 0;
        // 回血等待时间归零
        healWaitingTime = 0;
    }

    // 实体摔落受伤
    public void fallHurt() {
        // 计算所受伤害
        int damage = (int) fallSpeed - 15;
        // 掉落速度恢复为0
        fallSpeed = 0;
        // 如果生命值能够承受伤害
        if (health >= damage) {
            // 生命值直接减去伤害值
            health -= damage;
        } else {
            // 否则生命值直接归零
            health = 0;
        }
        // 回血等待时间归零
        healWaitingTime = 0;
    }

    // 更新实体是否死亡
    public void updateKill() {
        if (health <= 0 && !dead) kill();
        if (xLeft < 0 && !dead) kill();
        if (xRight > World.getInstance().getWidth() * 50 && !dead) kill();
        if (yDown > (World.getInstance().getHeight() + World.getInstance().getVoidSize()) * 50 && !dead) kill();
    }

    // 更新实体速度是否归零
    public void updateSpeedMin() {
        if (abs(xSpeed) < speedMin) xSpeed = 0;
        if (abs(ySpeed) < speedMin) ySpeed = 0;
        if (abs(jumpSpeed) < speedMin) jumpSpeed = 0;
        if (abs(fallSpeed) < speedMin) fallSpeed = 0;
    }

    // 更新实体中心坐标
    public void updateCenterLocation() {
        xCenter = (xLeft + xRight) / 2;
        yCenter = (yUp + yDown) / 2;
    }

    // 更新实体碰撞坐标
    public void updateCollisionLocation() {
        xLeft = x + xLeftRevision;
        xRight = x + xRightRevision;
        yUp = y + yUpRevision;
        yDown = y + yDownRevision;
    }

    // 更新碰撞范围
    public void updateCollisionRange() {
        xLeftCollision = (xCenter - 500) / 50;
        xRightCollision = (xCenter + 500) / 50;
        yUpCollision = (yCenter - 500) / 50;
        yDownCollision = (yCenter + 500) / 50;
        if (xLeftCollision < 0) xLeftCollision = 0;
        if (xRightCollision >= World.getInstance().getWidth()) xRightCollision = World.getInstance().getWidth() - 1;
        if (yUpCollision < 0) yUpCollision = 0;
        if (yDownCollision >= World.getInstance().getHeight()) yDownCollision = World.getInstance().getHeight() - 1;
    }

    // 更新实体与方块的关系
    public void updateEntityBlockRelation() {
        // 初始化墙体坐标值
        entityUpCeilY = -10 * 50;
        entityDownGroundY = (World.getInstance().getHeight() + World.getInstance().getVoidSize() + 10) * 50;
        entityLeftWallX = -10 * 50;
        entityRightWallX = (World.getInstance().getWidth() + 10) * 50;
        for (int i = xLeftCollision; i <= xRightCollision; i++) {
            for (int j = yUpCollision; j <= yDownCollision; j++) {
                if (World.getInstance().getBlockIdList()[j][i] != -1 && !IDIndex.blockIdToIsTool(World.getInstance().getBlockIdList()[j][i]) && !IDIndex.blockIdToIsTorchLike(World.getInstance().getBlockIdList()[j][i])) {
                    // 更新实体上下最近方块边界值
                    if (width <= World.getInstance().getBlockSize() - 2) {
                        if (xRight > i * 50 && xLeft < i * 50 + World.getInstance().getBlockSize()) {
                            if (j * 50 + World.getInstance().getBlockSize() > entityUpCeilY && j * 50 + World.getInstance().getBlockSize() <= yUp)
                                entityUpCeilY = j * 50 + World.getInstance().getBlockSize();
                            if (j * 50 < entityDownGroundY && j * 50 >= yDown)
                                entityDownGroundY = j * 50;
                        }
                    } else if ((i * 50 > xLeft && i * 50 < xRight) || (i * 50 + World.getInstance().getBlockSize() > xLeft && i * 50 + World.getInstance().getBlockSize() < xRight)) {
                        if (j * 50 + World.getInstance().getBlockSize() > entityUpCeilY && j * 50 + World.getInstance().getBlockSize() <= yUp)
                            entityUpCeilY = j * 50 + World.getInstance().getBlockSize();
                        if (j * 50 < entityDownGroundY && j * 50 >= yDown)
                            entityDownGroundY = j * 50;
                    }
                    // 更新左右最近方块边界值
                    if (width <= World.getInstance().getBlockSize() - 2) {
                        if (yDown > j * 50 && yUp < j * 50 + World.getInstance().getBlockSize()) {
                            if (i * 50 + World.getInstance().getBlockSize() > entityLeftWallX && i * 50 + World.getInstance().getBlockSize() <= xLeft)
                                entityLeftWallX = i * 50 + World.getInstance().getBlockSize();
                            if (i * 50 < entityRightWallX && i * 50 >= xRight)
                                entityRightWallX = i * 50;
                        }
                    } else if ((j * 50 > yUp && j * 50 < yDown) || (j * 50 + World.getInstance().getBlockSize() > yUp && j * 50 + World.getInstance().getBlockSize() < yDown)) {
                        if (i * 50 + World.getInstance().getBlockSize() > entityLeftWallX && i * 50 + World.getInstance().getBlockSize() <= xLeft)
                            entityLeftWallX = i * 50 + World.getInstance().getBlockSize();
                        if (i * 50 < entityRightWallX && i * 50 >= xRight)
                            entityRightWallX = i * 50;
                    }
                }
            }
        }
        // 更新实体是否顶头
        if (entityUpCeilY >= yUp) underCeil = true;
        else underCeil = false;
        // 更新实体是否落地
        if (entityDownGroundY <= yDown) {
            if ((int) fallSpeed > 15 && canHurt) fallHurt();
            ySpeed = 0;
            fallSpeed = 0;
            onGround = true;
        } else onGround = false;
        // 更新实体是否能向左走
        if (entityLeftWallX >= xLeft) canLeft = false;
        else canLeft = true;
        // 更新实体是否能向右走
        if (entityRightWallX <= xRight) canRight = false;
        else canRight = true;
        if (dead) {
            canJump = false;
            onGround = true;
            canLeft = false;
            canRight = false;
            xSpeed = 0;
            ySpeed = 0;
        }
        updateInBlock();
        // 如果卡在方块里，不能跳跃
        if (inBlock) canJump = false;
        else canJump = true;
    }

    // 更新实体是否在方块内
    public void updateInBlock() {
        boolean tmpInBlock = false;
        for (int i = xLeftCollision; i <= xRightCollision; i++) {
            for (int j = yUpCollision; j <= yDownCollision; j++) {
                if (World.getInstance().getBlockIdList()[j][i] != -1 && !IDIndex.blockIdToIsTool(World.getInstance().getBlockIdList()[j][i]) && !IDIndex.blockIdToIsTorchLike(World.getInstance().getBlockIdList()[j][i])) {
                    if (yUp >= j * 50 && yDown <= j * 50 + World.getInstance().getBlockSize()) {
                        if (xLeft < i * 50 + World.getInstance().getBlockSize() && xRight > i * 50 || xRight > i * 50 && xLeft < i * 50 + World.getInstance().getBlockSize())
                            tmpInBlock = true;
                    }
                    if (xLeft >= i * 50 && xRight <= i * 50 + World.getInstance().getBlockSize()) {
                        if (yUp < j * 50 + World.getInstance().getBlockSize() && yDown > j * 50 || yDown > j * 50 && yUp < j * 50 + World.getInstance().getBlockSize())
                            tmpInBlock = true;
                    }
                }
            }
        }
        if (tmpInBlock) inBlock = true;
        else inBlock = false;
    }

    // 更新实体横坐标
    public void updateX() {
        if (xSpeed < 0) {
            // 如果是向左移动
            if (canLeft) {
                // 向左移动
                moveX(xLeft, entityLeftWallX);
                xSpeed += World.getInstance().getAirResistance();
                if (xSpeed > 0) xSpeed = 0;
            } else xSpeed = 0;
        } else if (xSpeed > 0) {
            // 如果是向右移动
            if (canRight) {
                // 向右移动
                moveX(xRight, entityRightWallX);
                xSpeed -= World.getInstance().getAirResistance();
                if (xSpeed < 0) xSpeed = 0;
            } else xSpeed = 0;
        }
    }

    // 更新实体纵坐标
    public void updateY() {
        // 如果可以跳跃且跳跃速度不为0
        if (canJump && jumpSpeed != 0) {
            // 如果顶到方块，终止跳跃状态
            if (underCeil) {
                jumpSpeed = 0;
                ySpeed = 0;
            } else {
                // 如果处于跳跃状态，上升
                jumpSpeed += World.getInstance().getGravity();
                if (jumpSpeed > 0) jumpSpeed = 0;
                ySpeed = jumpSpeed;
                moveY(yUp, entityUpCeilY);
            }
        }
        // 否则如果受重力影响且没有落地
        else if (hasGravity && !onGround) {
            // 未在方块上，下落
            moveY(yDown, entityDownGroundY);
            if (abs(ySpeed) + World.getInstance().getGravity() < speedMax)
                ySpeed += World.getInstance().getGravity();
            fallSpeed = abs(ySpeed);
        } else if (inBlock) {
            if (chokeTimer >= 100) {
                hurt(1);
                chokeTimer = 0;
            } else chokeTimer++;
        }
    }

    // 更新闪烁计时器
    public void updateFlashTimer() {
        if (health != lastHealth) {
            if (health < lastHealth)
                damageTimer = 20;
            lastHealth = health;
        } else {
            if (damageTimer > 0) damageTimer--;
            else if (damageTimer < 0) damageTimer = 0;
        }
    }

    // 更新麻痹状态
    public void updateParalysisState() {
        if (paralysisTimer > 0) {
            paralysisTimer--;
            inParalysis = true;
        } else
            inParalysis = false;
        if (paralysisTimer <= 0)
            paralysisTimer = 0;
    }

    public void updateEntityData() {
        if (!type.equals("player")) {
            // 更新实体最小速度
            updateSpeedMin();
            // 更新实体碰撞范围
            updateCollisionRange();
            // 更新实体碰撞坐标
            updateCollisionLocation();
            // 更新实体与方块的关系
            updateEntityBlockRelation();
            // 更新实体横坐标
            updateX();
            // 更新实体碰撞坐标
            updateCollisionLocation();
            // 更新实体与方块的关系
            updateEntityBlockRelation();
            // 更新实体纵坐标
            updateY();
            // 更新实体恢复血量
            heal();
            // 更新实体中心坐标
            updateCenterLocation();
            // 更新实体是否死亡
            updateKill();
            // 更新闪烁计时器
            updateFlashTimer();
            // 更新麻痹状态
            updateParalysisState();
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<BufferedImage> getTextureList() {
        return textureList;
    }

    public void setTextureList(List<BufferedImage> textureList) {
        this.textureList = textureList;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
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

    public int getxLeft() {
        return xLeft;
    }

    public void setxLeft(int xLeft) {
        this.xLeft = xLeft;
    }

    public int getxRight() {
        return xRight;
    }

    public void setxRight(int xRight) {
        this.xRight = xRight;
    }

    public int getyUp() {
        return yUp;
    }

    public void setyUp(int yUp) {
        this.yUp = yUp;
    }

    public int getyDown() {
        return yDown;
    }

    public void setyDown(int yDown) {
        this.yDown = yDown;
    }

    public boolean isCanHurt() {
        return canHurt;
    }

    public void setCanHurt(boolean canHurt) {
        this.canHurt = canHurt;
    }

    public boolean isHasGravity() {
        return hasGravity;
    }

    public void setHasGravity(boolean hasGravity) {
        this.hasGravity = hasGravity;
    }

    public boolean isCanMoveX() {
        return canMoveX;
    }

    public void setCanMoveX(boolean canMoveX) {
        this.canMoveX = canMoveX;
    }

    public boolean isCanJump() {
        return canJump;
    }

    public void setCanJump(boolean canJump) {
        this.canJump = canJump;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getHealthMax() {
        return healthMax;
    }

    public void setHealthMax(int healthMax) {
        this.healthMax = healthMax;
    }

    public int getHealWaitingTime() {
        return healWaitingTime;
    }

    public void setHealWaitingTime(int healWaitingTime) {
        this.healWaitingTime = healWaitingTime;
    }

    public boolean isUnderCeil() {
        return underCeil;
    }

    public void setUnderCeil(boolean underCeil) {
        this.underCeil = underCeil;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public boolean isCanRight() {
        return canRight;
    }

    public void setCanRight(boolean canRight) {
        this.canRight = canRight;
    }

    public boolean isCanLeft() {
        return canLeft;
    }

    public void setCanLeft(boolean canLeft) {
        this.canLeft = canLeft;
    }

    public int getEntityUpCeilY() {
        return entityUpCeilY;
    }

    public void setEntityUpCeilY(int entityUpCeilY) {
        this.entityUpCeilY = entityUpCeilY;
    }

    public int getEntityDownGroundY() {
        return entityDownGroundY;
    }

    public void setEntityDownGroundY(int entityDownGroundY) {
        this.entityDownGroundY = entityDownGroundY;
    }

    public int getEntityLeftWallX() {
        return entityLeftWallX;
    }

    public void setEntityLeftWallX(int entityLeftWallX) {
        this.entityLeftWallX = entityLeftWallX;
    }

    public int getEntityRightWallX() {
        return entityRightWallX;
    }

    public void setEntityRightWallX(int entityRightWallX) {
        this.entityRightWallX = entityRightWallX;
    }

    public double getxSpeed() {
        return xSpeed;
    }

    public void setxSpeed(double xSpeed) {
        this.xSpeed = xSpeed;
    }

    public double getySpeed() {
        return ySpeed;
    }

    public void setySpeed(double ySpeed) {
        this.ySpeed = ySpeed;
    }

    public double getJumpSpeed() {
        return jumpSpeed;
    }

    public void setJumpSpeed(double jumpSpeed) {
        this.jumpSpeed = jumpSpeed;
    }

    public double getFallSpeed() {
        return fallSpeed;
    }

    public void setFallSpeed(double fallSpeed) {
        this.fallSpeed = fallSpeed;
    }

    public int getxCenter() {
        return xCenter;
    }

    public void setxCenter(int xCenter) {
        this.xCenter = xCenter;
    }

    public int getyCenter() {
        return yCenter;
    }

    public void setyCenter(int yCenter) {
        this.yCenter = yCenter;
    }

    public boolean isFlying() {
        return flying;
    }

    public void setFlying(boolean flying) {
        this.flying = flying;
    }

    public int getSpeedMax() {
        return speedMax;
    }

    public void setSpeedMax(int speedMax) {
        this.speedMax = speedMax;
    }

    public double getSpeedMin() {
        return speedMin;
    }

    public void setSpeedMin(double speedMin) {
        this.speedMin = speedMin;
    }

    public boolean isCanRespawn() {
        return canRespawn;
    }

    public void setCanRespawn(boolean canRespawn) {
        this.canRespawn = canRespawn;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public boolean isInBlock() {
        return inBlock;
    }

    public void setInBlock(boolean inBlock) {
        this.inBlock = inBlock;
    }

    public int getChokeTimer() {
        return chokeTimer;
    }

    public void setChokeTimer(int chokeTimer) {
        this.chokeTimer = chokeTimer;
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

    public BufferedImage getTextureCurrent() {
        return textureCurrent;
    }

    public void setTextureCurrent(BufferedImage textureCurrent) {
        this.textureCurrent = textureCurrent;
    }

    public int getxLeftCollision() {
        return xLeftCollision;
    }

    public void setxLeftCollision(int xLeftCollision) {
        this.xLeftCollision = xLeftCollision;
    }

    public int getxRightCollision() {
        return xRightCollision;
    }

    public void setxRightCollision(int xRightCollision) {
        this.xRightCollision = xRightCollision;
    }

    public int getyUpCollision() {
        return yUpCollision;
    }

    public void setyUpCollision(int yUpCollision) {
        this.yUpCollision = yUpCollision;
    }

    public int getyDownCollision() {
        return yDownCollision;
    }

    public void setyDownCollision(int yDownCollision) {
        this.yDownCollision = yDownCollision;
    }

    public int getxLeftRevision() {
        return xLeftRevision;
    }

    public void setxLeftRevision(int xLeftRevision) {
        this.xLeftRevision = xLeftRevision;
    }

    public int getxRightRevision() {
        return xRightRevision;
    }

    public void setxRightRevision(int xRightRevision) {
        this.xRightRevision = xRightRevision;
    }

    public int getyUpRevision() {
        return yUpRevision;
    }

    public void setyUpRevision(int yUpRevision) {
        this.yUpRevision = yUpRevision;
    }

    public int getyDownRevision() {
        return yDownRevision;
    }

    public void setyDownRevision(int yDownRevision) {
        this.yDownRevision = yDownRevision;
    }

    public int getLastHealth() {
        return lastHealth;
    }

    public void setLastHealth(int lastHealth) {
        this.lastHealth = lastHealth;
    }

    public int getDamageTimer() {
        return damageTimer;
    }

    public void setDamageTimer(int damageTimer) {
        this.damageTimer = damageTimer;
    }

    public int getParalysisTimer() {
        return paralysisTimer;
    }

    public void setParalysisTimer(int paralysisTimer) {
        this.paralysisTimer = paralysisTimer;
    }

    public boolean isInParalysis() {
        return inParalysis;
    }

    public void setInParalysis(boolean inParalysis) {
        this.inParalysis = inParalysis;
    }

    public boolean isOnFire() {
        return onFire;
    }

    public void setOnFire(boolean onFire) {
        this.onFire = onFire;
    }

    public int getIdCode() {
        return idCode;
    }

    public void setIdCode(int idCode) {
        this.idCode = idCode;
    }
}
