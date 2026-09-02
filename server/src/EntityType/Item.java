package EntityType;

import Base.TCPServer;
import Element.Entity;
import WorldTool.World;

import static java.lang.Math.abs;

public class Item extends Entity {
    // **定义掉落物ID
    private int id;
    // 定义掉落物物品堆叠数量
    private int amount = 1;
    // 定义掉落物不可捡起时间
    private int timeNoCollect = 0;
    // 定义掉落物悬浮时间
    private int timeSuspend = 0;
    // 定义掉落物悬浮偏差值(-5 + -3~3)
    private int suspendY = -10;
    // 定义掉落物被吸附速度
    private double attractSpeedX = 0;
    private double attractSpeedY = 0;

    public Item() {
        // 设置实体类型为掉落物
        setType("item");
        // 设置实体大小修正值
        setxLeftRevision(3);
        setxRightRevision(22);
        setyUpRevision(0);
        setyDownRevision(getHeight());
    }

    public Item(int idCode, int x, int y, int id, int amount, int timeNoCollect) {
        // 设置编号
        setIdCode(idCode);
        // 设置实体类型为掉落物
        setType("item");
        // 设置掉落物大小
        setWidth(20);
        setHeight(20);
        // 设置掉落物位置
        setX(x);
        setY(y);
        // 设置掉落物id
        this.id = id;
        // 设置掉落物数量
        this.amount = amount;
        this.timeNoCollect = timeNoCollect;
        // 设置掉落物无敌
        setCanHurt(false);
        // 设置实体大小修正值
        setxLeftRevision(3);
        setxRightRevision(22);
        setyUpRevision(0);
        setyDownRevision(getHeight());
    }

    // 重写更新实体纵坐标
    @Override
    public void updateY() {
        // 如果在方块内
        if (isInBlock()) {
            setY(getY() - 25);
        } else if (isHasGravity() && !isOnGround()) {
            // 如果受重力影响且没有落地
            // 未在方块上，下落
            moveY(getyDown(), getEntityDownGroundY());
            setFallSpeed(abs(getySpeed()));
            if (abs(getySpeed()) + World.getInstance().getGravity() < getSpeedMax())
                setySpeed(getySpeed() + World.getInstance().getGravity());
            // 悬浮偏差值设为-10
            suspendY = -10;
        }
        // 否则悬浮
        else if (getxSpeed() == 0) suspend();
        TCPServer.broadcastToAllClients("/updateLocation other " + getIdCode() + " " + getX() + " " + getY() + "\n");
    }

    // 掉落物悬浮
    public void suspend() {
        suspendY = (int) (Math.sin(Math.toRadians(timeSuspend)) * 8) - 10;
        timeSuspend += 3;
        TCPServer.broadcastToAllClients("/updateItemState " + getIdCode() + " " + suspendY + "\n");
    }

    // 更新掉落物数据
    public void updateItemData() {
        // 更新不可被拾取时间
        if (timeNoCollect > 0) {
            timeNoCollect--;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getTimeNoCollect() {
        return timeNoCollect;
    }

    public void setTimeNoCollect(int timeNoCollect) {
        this.timeNoCollect = timeNoCollect;
    }

    public int getTimeSuspend() {
        return timeSuspend;
    }

    public void setTimeSuspend(int timeSuspend) {
        this.timeSuspend = timeSuspend;
    }

    public int getSuspendY() {
        return suspendY;
    }

    public void setSuspendY(int suspendY) {
        this.suspendY = suspendY;
    }

    public double getAttractSpeedX() {
        return attractSpeedX;
    }

    public void setAttractSpeedX(double attractSpeedX) {
        this.attractSpeedX = attractSpeedX;
    }

    public double getAttractSpeedY() {
        return attractSpeedY;
    }

    public void setAttractSpeedY(double attractSpeedY) {
        this.attractSpeedY = attractSpeedY;
    }
}
