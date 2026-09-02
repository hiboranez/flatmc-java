package Base;

import java.util.Objects;

public class IDIndex {
    // 根据名称查询是否是实体名
    public static boolean nameToIsEntity(String name) {
        if (name.equals("zombie")) return true;
        return false;
    }

    // 根据实体名查询译名
    public static String entityNameToName(String name, String language) {
        if (language.equals("English")) {
            if (name.equals("zombie")) return "zombie";
        } else if (language.equals("Chinese")) {
            if (name.equals("zombie")) return "僵尸";
        }
        // 如果查无此名
        return "null";
    }

    // 根据方块ID查询类别
    public static String blockIdToType(int id) {
        if (id == -1) return "Blocks";
        else if (id == 0) return "Blocks";
        else if (id == 1) return "Blocks";
        else if (id == 2) return "Blocks";
        else if (id == 3) return "Blocks";
        else if (id == 4) return "Blocks";
        else if (id == 5) return "Blocks";
        else if (id == 6) return "Blocks";
        else if (id == 7) return "Blocks";
        else if (id == 8) return "Blocks";
        else if (id == 9) return "Tools";
        else if (id == 10) return "Tools";
        else if (id == 11) return "Tools";
        else if (id == 12) return "Tools";
        else if (id == 13) return "Tools";
        else if (id == 14) return "Tools";
        else if (id == 15) return "Tools";
        else if (id == 16) return "Tools";
        else if (id == 17) return "Others";
        else if (id == 18) return "Others";
        else if (id == 19) return "Minerals";
        else if (id == 20) return "Minerals";
        else if (id == 21) return "Minerals";
        else if (id == 22) return "Minerals";
        else if (id == 23) return "Minerals";
        else if (id == 24) return "Minerals";
        else if (id == 25) return "Minerals";
        else if (id == 26) return "Minerals";
        else if (id == 27) return "Minerals";
        else if (id == 28) return "Minerals";
        else if (id == 29) return "Minerals";
        else if (id == 30) return "Tools";
        else if (id == 31) return "Tools";
        else if (id == 32) return "Tools";
        else if (id == 33) return "Tools";
        else if (id == 34) return "Tools";
        else if (id == 35) return "Tools";
        else if (id == 36) return "Tools";
        else if (id == 37) return "Tools";
        else if (id == 38) return "Tools";
        else if (id == 39) return "Tools";
        else if (id == 40) return "Tools";
        else if (id == 41) return "Tools";
        else if (id == 42) return "Others";
        return "null";
    }

    // 根据方块ID查询是否能够放置
    public static boolean blockIdToCanPlace(int id) {
        if (id == -1) return true;
        else if (id == 0) return true;
        else if (id == 1) return true;
        else if (id == 2) return true;
        else if (id == 3) return true;
        else if (id == 4) return true;
        else if (id == 5) return true;
        else if (id == 6) return true;
        else if (id == 7) return true;
        else if (id == 8) return true;
        else if (id == 9) return false;
        else if (id == 10) return false;
        else if (id == 11) return false;
        else if (id == 12) return false;
        else if (id == 13) return false;
        else if (id == 14) return false;
        else if (id == 15) return false;
        else if (id == 16) return false;
        else if (id == 17) return true;
        else if (id == 18) return false;
        else if (id == 19) return true;
        else if (id == 20) return true;
        else if (id == 21) return true;
        else if (id == 22) return true;
        else if (id == 23) return false;
        else if (id == 24) return false;
        else if (id == 25) return false;
        else if (id == 26) return false;
        else if (id == 27) return true;
        else if (id == 28) return true;
        else if (id == 29) return true;
        else if (id == 30) return false;
        else if (id == 31) return false;
        else if (id == 32) return false;
        else if (id == 33) return false;
        else if (id == 34) return false;
        else if (id == 35) return false;
        else if (id == 36) return false;
        else if (id == 37) return false;
        else if (id == 38) return false;
        else if (id == 39) return false;
        else if (id == 40) return false;
        else if (id == 41) return false;
        else if (id == 42) return false;
        return false;
    }

    // 根据方块ID查询是否是食物
    public static boolean blockIdToIsFood(int id) {
        if (id == -1) return false;
        else if (id == 0) return false;
        else if (id == 1) return false;
        else if (id == 2) return false;
        else if (id == 3) return false;
        else if (id == 4) return false;
        else if (id == 5) return false;
        else if (id == 6) return false;
        else if (id == 7) return false;
        else if (id == 8) return false;
        else if (id == 9) return false;
        else if (id == 10) return false;
        else if (id == 11) return false;
        else if (id == 12) return false;
        else if (id == 13) return false;
        else if (id == 14) return false;
        else if (id == 15) return false;
        else if (id == 16) return false;
        else if (id == 17) return false;
        else if (id == 18) return false;
        else if (id == 19) return false;
        else if (id == 20) return false;
        else if (id == 21) return false;
        else if (id == 22) return false;
        else if (id == 23) return false;
        else if (id == 24) return false;
        else if (id == 25) return false;
        else if (id == 26) return false;
        else if (id == 27) return false;
        else if (id == 28) return false;
        else if (id == 29) return false;
        else if (id == 30) return false;
        else if (id == 31) return false;
        else if (id == 32) return false;
        else if (id == 33) return false;
        else if (id == 34) return false;
        else if (id == 35) return false;
        else if (id == 36) return false;
        else if (id == 37) return false;
        else if (id == 38) return false;
        else if (id == 39) return false;
        else if (id == 40) return false;
        else if (id == 41) return false;
        else if (id == 42) return true;
        return false;
    }

    // 根据方块ID查询是否是防具
    public static boolean blockIdToIsArmor(int id) {
        if (id == -1) return false;
        else if (id == 0) return false;
        else if (id == 1) return false;
        else if (id == 2) return false;
        else if (id == 3) return false;
        else if (id == 4) return false;
        else if (id == 5) return false;
        else if (id == 6) return false;
        else if (id == 7) return false;
        else if (id == 8) return false;
        else if (id == 9) return false;
        else if (id == 10) return false;
        else if (id == 11) return false;
        else if (id == 12) return false;
        else if (id == 13) return false;
        else if (id == 14) return false;
        else if (id == 15) return false;
        else if (id == 16) return false;
        else if (id == 17) return false;
        else if (id == 18) return false;
        else if (id == 19) return false;
        else if (id == 20) return false;
        else if (id == 21) return false;
        else if (id == 22) return false;
        else if (id == 23) return false;
        else if (id == 24) return false;
        else if (id == 25) return false;
        else if (id == 26) return false;
        else if (id == 27) return false;
        else if (id == 28) return false;
        else if (id == 29) return false;
        else if (id == 30) return false;
        else if (id == 31) return false;
        else if (id == 32) return false;
        else if (id == 33) return false;
        else if (id == 34) return false;
        else if (id == 35) return false;
        else if (id == 36) return false;
        else if (id == 37) return false;
        else if (id == 38) return false;
        else if (id == 39) return false;
        else if (id == 40) return false;
        else if (id == 41) return false;
        else if (id == 42) return false;
        return false;
    }

    // 根据方块ID查询攻击CD
    public static int blockIdToAttackCD(int id) {
        if (id == -1) return 50;
        else if (id == 0) return 50;
        else if (id == 1) return 50;
        else if (id == 2) return 50;
        else if (id == 3) return 50;
        else if (id == 4) return 50;
        else if (id == 5) return 50;
        else if (id == 6) return 50;
        else if (id == 7) return 50;
        else if (id == 8) return 50;
        else if (id == 9) return 50;
        else if (id == 10) return 50;
        else if (id == 11) return 100;
        else if (id == 12) return 50;
        else if (id == 13) return 50;
        else if (id == 14) return 50;
        else if (id == 15) return 100;
        else if (id == 16) return 50;
        else if (id == 17) return 50;
        else if (id == 18) return 50;
        else if (id == 19) return 50;
        else if (id == 20) return 50;
        else if (id == 21) return 50;
        else if (id == 22) return 50;
        else if (id == 23) return 50;
        else if (id == 24) return 50;
        else if (id == 25) return 50;
        else if (id == 26) return 50;
        else if (id == 27) return 50;
        else if (id == 28) return 50;
        else if (id == 29) return 50;
        else if (id == 30) return 50;
        else if (id == 31) return 50;
        else if (id == 32) return 100;
        else if (id == 33) return 50;
        else if (id == 34) return 50;
        else if (id == 35) return 50;
        else if (id == 36) return 100;
        else if (id == 37) return 50;
        else if (id == 38) return 50;
        else if (id == 39) return 50;
        else if (id == 40) return 100;
        else if (id == 41) return 50;
        else if (id == 42) return 50;
        return 1;
    }

    // 根据方块ID判断攻击力
    public static int blockIdToAttackValue(int id) {
        if (id == 0) return 1;
        else if (id == 1) return 1;
        else if (id == 2) return 1;
        else if (id == 3) return 1;
        else if (id == 4) return 1;
        else if (id == 5) return 1;
        else if (id == 6) return 1;
        else if (id == 7) return 1;
        else if (id == 8) return 1;
        else if (id == 9) return 3;
        else if (id == 10) return 1;
        else if (id == 11) return 4;
        else if (id == 12) return 1;
        else if (id == 13) return 5;
        else if (id == 14) return 1;
        else if (id == 15) return 7;
        else if (id == 16) return 1;
        else if (id == 17) return 1;
        else if (id == 18) return 1;
        else if (id == 19) return 1;
        else if (id == 20) return 1;
        else if (id == 21) return 1;
        else if (id == 22) return 1;
        else if (id == 23) return 1;
        else if (id == 24) return 1;
        else if (id == 25) return 1;
        else if (id == 26) return 1;
        else if (id == 27) return 1;
        else if (id == 28) return 1;
        else if (id == 29) return 1;
        else if (id == 30) return 7;
        else if (id == 31) return 1;
        else if (id == 32) return 9;
        else if (id == 33) return 1;
        else if (id == 34) return 5;
        else if (id == 35) return 1;
        else if (id == 36) return 7;
        else if (id == 37) return 1;
        else if (id == 38) return 9;
        else if (id == 39) return 1;
        else if (id == 40) return 11;
        else if (id == 41) return 1;
        else if (id == 42) return 1;
        return 1;
    }

    // 根据方块ID判断是否是不阻挡型方块
    public static boolean blockIdToIsUnTouchable(int id) {
        if (id == -1) return true;
        else if (id == 0) return false;
        else if (id == 1) return false;
        else if (id == 2) return false;
        else if (id == 3) return false;
        else if (id == 4) return false;
        else if (id == 5) return false;
        else if (id == 6) return false;
        else if (id == 7) return false;
        else if (id == 8) return false;
        else if (id == 9) return true;
        else if (id == 10) return true;
        else if (id == 11) return true;
        else if (id == 12) return true;
        else if (id == 13) return true;
        else if (id == 14) return true;
        else if (id == 15) return true;
        else if (id == 16) return true;
        else if (id == 17) return true;
        else if (id == 18) return true;
        else if (id == 19) return false;
        else if (id == 20) return false;
        else if (id == 21) return false;
        else if (id == 22) return false;
        else if (id == 23) return true;
        else if (id == 24) return true;
        else if (id == 25) return true;
        else if (id == 26) return true;
        else if (id == 27) return false;
        else if (id == 28) return false;
        else if (id == 29) return false;
        else if (id == 30) return true;
        else if (id == 31) return true;
        else if (id == 32) return true;
        else if (id == 33) return true;
        else if (id == 34) return true;
        else if (id == 35) return true;
        else if (id == 36) return true;
        else if (id == 37) return true;
        else if (id == 38) return true;
        else if (id == 39) return true;
        else if (id == 40) return true;
        else if (id == 41) return true;
        else if (id == 42) return true;
        return false;
    }

    // 根据方块ID判断是否是棍棒型物品
    public static boolean blockIdToIsStickItem(int id) {
        if (id == 0) return false;
        else if (id == 1) return false;
        else if (id == 2) return false;
        else if (id == 3) return false;
        else if (id == 4) return false;
        else if (id == 5) return false;
        else if (id == 6) return false;
        else if (id == 7) return false;
        else if (id == 8) return false;
        else if (id == 9) return true;
        else if (id == 10) return true;
        else if (id == 11) return true;
        else if (id == 12) return true;
        else if (id == 13) return true;
        else if (id == 14) return true;
        else if (id == 15) return true;
        else if (id == 16) return true;
        else if (id == 17) return true;
        else if (id == 18) return true;
        else if (id == 19) return false;
        else if (id == 20) return false;
        else if (id == 21) return false;
        else if (id == 22) return false;
        else if (id == 23) return false;
        else if (id == 24) return false;
        else if (id == 25) return false;
        else if (id == 26) return false;
        else if (id == 27) return false;
        else if (id == 28) return false;
        else if (id == 29) return false;
        else if (id == 30) return true;
        else if (id == 31) return true;
        else if (id == 32) return true;
        else if (id == 33) return true;
        else if (id == 34) return true;
        else if (id == 35) return true;
        else if (id == 36) return true;
        else if (id == 37) return true;
        else if (id == 38) return true;
        else if (id == 39) return true;
        else if (id == 40) return true;
        else if (id == 41) return true;
        else if (id == 42) return false;
        return false;
    }

    // 根据方块ID判断是否是光源
    public static boolean blockIdToIsLightSource(int id) {
        if (id == 0) return false;
        else if (id == 1) return false;
        else if (id == 2) return false;
        else if (id == 3) return false;
        else if (id == 4) return false;
        else if (id == 5) return false;
        else if (id == 6) return false;
        else if (id == 7) return false;
        else if (id == 8) return false;
        else if (id == 9) return false;
        else if (id == 10) return false;
        else if (id == 11) return false;
        else if (id == 12) return false;
        else if (id == 13) return false;
        else if (id == 14) return false;
        else if (id == 15) return false;
        else if (id == 16) return false;
        else if (id == 17) return true;
        else if (id == 18) return false;
        else if (id == 19) return false;
        else if (id == 20) return false;
        else if (id == 21) return false;
        else if (id == 22) return false;
        else if (id == 23) return false;
        else if (id == 24) return false;
        else if (id == 25) return false;
        else if (id == 26) return false;
        else if (id == 27) return false;
        else if (id == 28) return false;
        else if (id == 29) return false;
        else if (id == 30) return false;
        else if (id == 31) return false;
        else if (id == 32) return false;
        else if (id == 33) return false;
        else if (id == 34) return false;
        else if (id == 35) return false;
        else if (id == 36) return false;
        else if (id == 37) return false;
        else if (id == 38) return false;
        else if (id == 39) return false;
        else if (id == 40) return false;
        else if (id == 41) return false;
        else if (id == 42) return false;
        return false;
    }

    // 根据方块ID判断是否是类火把放置型方块
    public static boolean blockIdToIsTorchLike(int id) {
        if (id == 0) return false;
        else if (id == 1) return false;
        else if (id == 2) return false;
        else if (id == 3) return false;
        else if (id == 4) return false;
        else if (id == 5) return false;
        else if (id == 6) return false;
        else if (id == 7) return false;
        else if (id == 8) return false;
        else if (id == 9) return false;
        else if (id == 10) return false;
        else if (id == 11) return false;
        else if (id == 12) return false;
        else if (id == 13) return false;
        else if (id == 14) return false;
        else if (id == 15) return false;
        else if (id == 16) return false;
        else if (id == 17) return true;
        else if (id == 18) return false;
        else if (id == 19) return false;
        else if (id == 20) return false;
        else if (id == 21) return false;
        else if (id == 22) return false;
        else if (id == 23) return false;
        else if (id == 24) return false;
        else if (id == 25) return false;
        else if (id == 26) return false;
        else if (id == 27) return false;
        else if (id == 28) return false;
        else if (id == 29) return false;
        else if (id == 30) return false;
        else if (id == 31) return false;
        else if (id == 32) return false;
        else if (id == 33) return false;
        else if (id == 34) return false;
        else if (id == 35) return false;
        else if (id == 36) return false;
        else if (id == 37) return false;
        else if (id == 38) return false;
        else if (id == 39) return false;
        else if (id == 40) return false;
        else if (id == 41) return false;
        else if (id == 42) return false;
        return false;
    }

    // 根据时间数字转换24h制时间
    public static String numberToTime(int number) {
        if (number < 0 || number > 120000) {
            return "Invalid number";
        }
        int hours = number / 5000;  // 计算小时数
        int minutes = (int) ((number % 5000) / 83.33);  // 计算分钟数，将0-10000映射到0-60的范围
        //int seconds = (int) (((number % 5000) % 83) / 1.39);  // 计算秒数，将0-100映射到0-60的范围
        return String.format("%02d:%02d", hours, minutes);
    }

    // 根据世界难度判断刷新率
    public static int difficultyToRandom(String difficulty) {
        if (Objects.equals(difficulty, "peaceful"))
            return 0;
        else if (Objects.equals(difficulty, "easy"))
            return 1300;
        else if (Objects.equals(difficulty, "normal"))
            return 600;
        else if (Objects.equals(difficulty, "hard"))
            return 300;
        return 0;
    }

    // 根据世界难度判断怪物数量上限
    public static int difficultyToMobAmountMax(String difficulty) {
        if (Objects.equals(difficulty, "peaceful"))
            return 0;
        else if (Objects.equals(difficulty, "easy"))
            return 10;
        else if (Objects.equals(difficulty, "normal"))
            return 15;
        else if (Objects.equals(difficulty, "hard"))
            return 20;
        return 0;
    }

    // 根据ID判断是否是工具
    public static boolean blockIdToIsTool(int id) {
        if (id == 0) return false;
        else if (id == 1) return false;
        else if (id == 2) return false;
        else if (id == 3) return false;
        else if (id == 4) return false;
        else if (id == 5) return false;
        else if (id == 6) return false;
        else if (id == 7) return false;
        else if (id == 8) return false;
        else if (id == 9) return true;
        else if (id == 10) return true;
        else if (id == 11) return true;
        else if (id == 12) return true;
        else if (id == 13) return true;
        else if (id == 14) return true;
        else if (id == 15) return true;
        else if (id == 16) return true;
        else if (id == 17) return false;
        else if (id == 18) return false;
        else if (id == 19) return false;
        else if (id == 20) return false;
        else if (id == 21) return false;
        else if (id == 22) return false;
        else if (id == 23) return false;
        else if (id == 24) return false;
        else if (id == 25) return false;
        else if (id == 26) return false;
        else if (id == 27) return false;
        else if (id == 28) return false;
        else if (id == 29) return false;
        else if (id == 30) return true;
        else if (id == 31) return true;
        else if (id == 32) return true;
        else if (id == 33) return true;
        else if (id == 34) return true;
        else if (id == 35) return true;
        else if (id == 36) return true;
        else if (id == 37) return true;
        else if (id == 38) return true;
        else if (id == 39) return true;
        else if (id == 40) return true;
        else if (id == 41) return true;
        else if (id == 42) return false;
        return false;
    }

    public static int shovelResistance(int id, String toolState) {
        if (Objects.equals(toolState, "wooden_shovel")) return 60;
        else if (Objects.equals(toolState, "stone_shovel")) return 40;
        else if (Objects.equals(toolState, "iron_shovel")) return 20;
        else if (Objects.equals(toolState, "Golden_Shovel")) return 30;
        else if (Objects.equals(toolState, "diamond_shovel")) return 10;
        else return 100;
    }

    public static int pickaxeResistance(int id, String toolState) {
        if (Objects.equals(toolState, "wooden_pickaxe")) {
            if (id == 21 || id == 22 || id == 27 || id == 28 || id == 29)
                return 800;
            else return 180;
        } else if (Objects.equals(toolState, "stone_pickaxe")) {
            if (id == 21 || id == 22 || id == 27 || id == 28 || id == 29)
                return 700;
            else return 100;
        } else if (Objects.equals(toolState, "iron_pickaxe")) return 50;
        else if (Objects.equals(toolState, "Golden_Pickaxe")) return 70;
        else if (Objects.equals(toolState, "diamond_pickaxe")) return 20;
        if (id == 21 || id == 22 || id == 27 || id == 28 || id == 29) return 1000;
        else return 500;
    }

    public static int axeResistance(int id, String toolState) {
        if (Objects.equals(toolState, "wooden_axe")) return 100;
        else if (Objects.equals(toolState, "stone_axe")) return 50;
        else if (Objects.equals(toolState, "iron_axe")) return 25;
        else if (Objects.equals(toolState, "Golden_Axe")) return 30;
        else if (Objects.equals(toolState, "diamond_axe")) return 15;
        else return 200;
    }

    // 根据方块ID查询方块耐久
    public static int blockIdToResistance(int id, String toolState) {
        if (id == 0) return shovelResistance(id, toolState);
        else if (id == 1) return shovelResistance(id, toolState);
        else if (id == 2) return pickaxeResistance(id, toolState);
        else if (id == 3) return -1;
        else if (id == 4) return pickaxeResistance(id, toolState);
        else if (id == 5) return axeResistance(id, toolState);
        else if (id == 6) return axeResistance(id, toolState);
        else if (id == 7) return 60;
        else if (id == 8) return axeResistance(id, toolState);
        else if (id == 9) return 1;
        else if (id == 10) return 1;
        else if (id == 11) return 1;
        else if (id == 12) return 1;
        else if (id == 13) return 1;
        else if (id == 14) return 1;
        else if (id == 15) return 1;
        else if (id == 16) return 1;
        else if (id == 17) return 1;
        else if (id == 18) return 1;
        else if (id == 19) return pickaxeResistance(id, toolState);
        else if (id == 20) return pickaxeResistance(id, toolState);
        else if (id == 21) return pickaxeResistance(id, toolState);
        else if (id == 22) return pickaxeResistance(id, toolState);
        else if (id == 23) return 1;
        else if (id == 24) return 1;
        else if (id == 25) return 1;
        else if (id == 26) return 1;
        else if (id == 27) return pickaxeResistance(id, toolState);
        else if (id == 28) return pickaxeResistance(id, toolState);
        else if (id == 29) return pickaxeResistance(id, toolState);
        else if (id == 30) return 1;
        else if (id == 31) return 1;
        else if (id == 32) return 1;
        else if (id == 33) return 1;
        else if (id == 34) return 1;
        else if (id == 35) return 1;
        else if (id == 36) return 1;
        else if (id == 37) return 1;
        else if (id == 38) return 1;
        else if (id == 39) return 1;
        else if (id == 40) return 1;
        else if (id == 41) return 1;
        else if (id == 42) return 1;
        return -1;
    }

    // 根据方块ID查询方块最大堆叠数
    public static int blockIdToMaxAmount(int id) {
        if (id == 0) return 64;
        else if (id == 1) return 64;
        else if (id == 2) return 64;
        else if (id == 3) return 64;
        else if (id == 4) return 64;
        else if (id == 5) return 64;
        else if (id == 6) return 64;
        else if (id == 7) return 64;
        else if (id == 8) return 64;
        else if (id == 9) return 100;
        else if (id == 10) return 100;
        else if (id == 11) return 100;
        else if (id == 12) return 100;
        else if (id == 13) return 200;
        else if (id == 14) return 200;
        else if (id == 15) return 200;
        else if (id == 16) return 200;
        else if (id == 17) return 64;
        else if (id == 18) return 64;
        else if (id == 19) return 64;
        else if (id == 20) return 64;
        else if (id == 21) return 64;
        else if (id == 22) return 64;
        else if (id == 23) return 64;
        else if (id == 24) return 64;
        else if (id == 25) return 64;
        else if (id == 26) return 64;
        else if (id == 27) return 64;
        else if (id == 28) return 64;
        else if (id == 29) return 64;
        else if (id == 30) return 300;
        else if (id == 31) return 300;
        else if (id == 32) return 300;
        else if (id == 33) return 300;
        else if (id == 34) return 150;
        else if (id == 35) return 150;
        else if (id == 36) return 150;
        else if (id == 37) return 150;
        else if (id == 38) return 400;
        else if (id == 39) return 400;
        else if (id == 40) return 400;
        else if (id == 41) return 400;
        else if (id == 42) return 64;
        return -1;
    }

    // 根据工具状态查询挖掘等级
    public static int toolStateToMineLevel(String toolState) {
        if (Objects.equals(toolState, "wooden_pickaxe")) return 2;
        else if (Objects.equals(toolState, "stone_pickaxe")) return 2;
        else if (Objects.equals(toolState, "iron_pickaxe")) return 3;
        else if (Objects.equals(toolState, "golden_pickaxe")) return 2;
        else if (Objects.equals(toolState, "diamond_pickaxe")) return 3;
        return 1;
    }

    // 根据方块ID查询破坏后掉落物ID
    public static int blockIdToItemId(int id, String toolState) {
        if (id == 0) return 0;
        else if (id == 1) return 0;
        else if (id == 2)
            if (toolStateToMineLevel(toolState) >= 2) return 4;
            else return -1;
        else if (id == 3) return -1;
        else if (id == 4)
            if (toolStateToMineLevel(toolState) >= 2) return 4;
            else return -1;
        else if (id == 5) return 5;
        else if (id == 6) return 6;
        else if (id == 7) return -1;
        else if (id == 8) return 8;
        else if (id == 9) return 9;
        else if (id == 10) return 10;
        else if (id == 11) return 11;
        else if (id == 12) return 12;
        else if (id == 13) return 13;
        else if (id == 14) return 14;
        else if (id == 15) return 15;
        else if (id == 16) return 16;
        else if (id == 17) return 17;
        else if (id == 18) return 18;
        else if (id == 19)
            if (toolStateToMineLevel(toolState) >= 2) return 23;
            else return -1;
        else if (id == 20)
            if (toolStateToMineLevel(toolState) >= 2) return 24;
            else return -1;
        else if (id == 21)
            if (toolStateToMineLevel(toolState) >= 3) return 25;
            else return -1;
        else if (id == 22)
            if (toolStateToMineLevel(toolState) >= 3) return 26;
            else return -1;
        else if (id == 23) return 23;
        else if (id == 24) return 24;
        else if (id == 25) return 25;
        else if (id == 26) return 26;
        else if (id == 27)
            if (toolStateToMineLevel(toolState) >= 3) return 27;
            else return -1;
        else if (id == 28)
            if (toolStateToMineLevel(toolState) >= 3) return 28;
            else return -1;
        else if (id == 29)
            if (toolStateToMineLevel(toolState) >= 3) return 29;
            else return -1;
        else if (id == 30) return 30;
        else if (id == 31) return 31;
        else if (id == 32) return 32;
        else if (id == 33) return 33;
        else if (id == 34) return 34;
        else if (id == 35) return 35;
        else if (id == 36) return 36;
        else if (id == 37) return 37;
        else if (id == 38) return 38;
        else if (id == 39) return 39;
        else if (id == 40) return 40;
        else if (id == 41) return 41;
        else if (id == 42) return 42;
        return -1;
    }

    // 根据方块名查询方块ID
    public static int blockNameToID(String name) {
        if (Objects.equals(name, "dirt")) return 0;
        if (Objects.equals(name, "grass_block")) return 1;
        if (Objects.equals(name, "stone")) return 2;
        if (Objects.equals(name, "bedrock")) return 3;
        if (Objects.equals(name, "cobblestone")) return 4;
        if (Objects.equals(name, "oak")) return 5;
        if (Objects.equals(name, "plank")) return 6;
        if (Objects.equals(name, "leaves")) return 7;
        if (Objects.equals(name, "crafting_table")) return 8;
        if (Objects.equals(name, "wooden_sword")) return 9;
        if (Objects.equals(name, "wooden_pickaxe")) return 10;
        if (Objects.equals(name, "wooden_axe")) return 11;
        if (Objects.equals(name, "wooden_shovel")) return 12;
        if (Objects.equals(name, "stone_sword")) return 13;
        if (Objects.equals(name, "stone_pickaxe")) return 14;
        if (Objects.equals(name, "stone_axe")) return 15;
        if (Objects.equals(name, "stone_shovel")) return 16;
        if (Objects.equals(name, "torch")) return 17;
        if (Objects.equals(name, "stick")) return 18;
        if (Objects.equals(name, "coal_ore")) return 19;
        if (Objects.equals(name, "iron_ore")) return 20;
        if (Objects.equals(name, "gold_ore")) return 21;
        if (Objects.equals(name, "diamond_ore")) return 22;
        if (Objects.equals(name, "coal")) return 23;
        if (Objects.equals(name, "iron_ingot")) return 24;
        if (Objects.equals(name, "gold_ingot")) return 25;
        if (Objects.equals(name, "diamond")) return 26;
        if (Objects.equals(name, "iron_block")) return 27;
        if (Objects.equals(name, "gold_block")) return 28;
        if (Objects.equals(name, "diamond_block")) return 29;
        if (Objects.equals(name, "iron_sword")) return 30;
        if (Objects.equals(name, "iron_pickaxe")) return 31;
        if (Objects.equals(name, "iron_axe")) return 32;
        if (Objects.equals(name, "iron_shovel")) return 33;
        if (Objects.equals(name, "golden_sword")) return 34;
        if (Objects.equals(name, "golden_pickaxe")) return 35;
        if (Objects.equals(name, "golden_axe")) return 36;
        if (Objects.equals(name, "golden_shovel")) return 37;
        if (Objects.equals(name, "diamond_sword")) return 38;
        if (Objects.equals(name, "diamond_pickaxe")) return 39;
        if (Objects.equals(name, "diamond_axe")) return 40;
        if (Objects.equals(name, "diamond_shovel")) return 41;
        if (Objects.equals(name, "rotten_flesh")) return 42;
        // 如果查无此名，返回-2
        return -2;
    }

    // 根据方块ID查询方块名
    public static String blockIDToName(int id, String language) {
        if (Objects.equals(language, "English")) {
            if (id == 0) return "dirt";
            if (id == 1) return "grass_block";
            if (id == 2) return "stone";
            if (id == 3) return "bedrock";
            if (id == 4) return "cobblestone";
            if (id == 5) return "oak";
            if (id == 6) return "plank";
            if (id == 7) return "leaves";
            if (id == 8) return "crafting_table";
            if (id == 9) return "wooden_sword";
            if (id == 10) return "wooden_pickaxe";
            if (id == 11) return "wooden_axe";
            if (id == 12) return "wooden_shovel";
            if (id == 13) return "stone_sword";
            if (id == 14) return "stone_pickaxe";
            if (id == 15) return "stone_axe";
            if (id == 16) return "stone_shovel";
            if (id == 17) return "torch";
            if (id == 18) return "stick";
            if (id == 19) return "coal_ore";
            if (id == 20) return "iron_ore";
            if (id == 21) return "gold_ore";
            if (id == 22) return "diamond_ore";
            if (id == 23) return "coal";
            if (id == 24) return "iron_ingot";
            if (id == 25) return "gold_ingot";
            if (id == 26) return "diamond";
            if (id == 27) return "iron_block";
            if (id == 28) return "gold_block";
            if (id == 29) return "diamond_block";
            if (id == 30) return "iron_sword";
            if (id == 31) return "iron_pickaxe";
            if (id == 32) return "iron_axe";
            if (id == 33) return "iron_shovel";
            if (id == 34) return "golden_sword";
            if (id == 35) return "golden_pickaxe";
            if (id == 36) return "golden_axe";
            if (id == 37) return "golden_shovel";
            if (id == 38) return "diamond_sword";
            if (id == 39) return "diamond_pickaxe";
            if (id == 40) return "diamond_axe";
            if (id == 41) return "diamond_shovel";
            if (id == 42) return "rotten_flesh";
        } else if (Objects.equals(language, "Chinese")) {
            if (id == 0) return "泥土";
            if (id == 1) return "草方块";
            if (id == 2) return "石头";
            if (id == 3) return "基岩";
            if (id == 4) return "圆石";
            if (id == 5) return "原木";
            if (id == 6) return "木板";
            if (id == 7) return "树叶";
            if (id == 8) return "工作台";
            if (id == 9) return "木剑";
            if (id == 10) return "木镐";
            if (id == 11) return "木斧";
            if (id == 12) return "木锹";
            if (id == 13) return "石剑";
            if (id == 14) return "石镐";
            if (id == 15) return "石斧";
            if (id == 16) return "石锹";
            if (id == 17) return "火把";
            if (id == 18) return "木棍";
            if (id == 19) return "煤矿石";
            if (id == 20) return "铁矿石";
            if (id == 21) return "金矿石";
            if (id == 22) return "钻石矿石";
            if (id == 23) return "煤炭";
            if (id == 24) return "铁锭";
            if (id == 25) return "金锭";
            if (id == 26) return "钻石";
            if (id == 27) return "铁块";
            if (id == 28) return "金块";
            if (id == 29) return "钻石块";
            if (id == 30) return "铁剑";
            if (id == 31) return "铁镐";
            if (id == 32) return "铁斧";
            if (id == 33) return "铁锹";
            if (id == 34) return "金剑";
            if (id == 35) return "金镐";
            if (id == 36) return "金斧";
            if (id == 37) return "金锹";
            if (id == 38) return "钻石剑";
            if (id == 39) return "钻石镐";
            if (id == 40) return "钻石斧";
            if (id == 41) return "钻石锹";
            if (id == 42) return "腐肉";
        }
        // 如果查无此名，返回"null"
        return "null";
    }

    // 根据音效类型和音效名查询音效ID
    public static int soundNameToID(String type, String name) {
        // 音效类型为music
        if (Objects.equals(type, "music")) {
            if (Objects.equals(name, "calm1")) return 0;
            if (Objects.equals(name, "calm2")) return 1;
            if (Objects.equals(name, "calm3")) return 2;
            if (Objects.equals(name, "hal1")) return 3;
            if (Objects.equals(name, "hal2")) return 4;
            if (Objects.equals(name, "hal3")) return 5;
            if (Objects.equals(name, "hal4")) return 6;
            if (Objects.equals(name, "nuance1")) return 7;
            if (Objects.equals(name, "nuance2")) return 8;
            if (Objects.equals(name, "piano1")) return 9;
        }

        // 音效类型为
        if (Objects.equals(type, "dig")) {
            if (Objects.equals(name, "gravel")) return 0;
            if (Objects.equals(name, "grass")) return 1;
            if (Objects.equals(name, "stone")) return 2;
            if (Objects.equals(name, "wood")) return 3;
        }

        // 音效类型为step
        if (Objects.equals(type, "step")) {
            if (Objects.equals(name, "gravel")) return 0;
            if (Objects.equals(name, "grass")) return 1;
            if (Objects.equals(name, "stone")) return 2;
            if (Objects.equals(name, "wood")) return 3;
        }

        // 音效类型为player
        if (Objects.equals(type, "player")) {
            if (Objects.equals(name, "pop")) return 0;
            if (Objects.equals(name, "hurt")) return 1;
            if (Objects.equals(name, "eat")) return 2;
            if (Objects.equals(name, "hiccup")) return 3;
        }

        // 音效类型为damage
        if (Objects.equals(type, "damage")) {
            if (Objects.equals(name, "hit")) return 0;
            if (Objects.equals(name, "fallbig")) return 1;
            if (Objects.equals(name, "fallsmall")) return 2;
            if (Objects.equals(name, "toolbreak")) return 3;
        }

        // 音效类型为gui
        if (Objects.equals(type, "gui")) {
            if (Objects.equals(name, "click")) return 0;
        }

        // 音效类型为zombie
        if (Objects.equals(type, "zombie")) {
            if (Objects.equals(name, "hurt")) return 0;
            if (Objects.equals(name, "death")) return 1;
            if (Objects.equals(name, "say")) return 2;
        }

        // 如果查无此名，返回-1
        return -1;
    }

    // 根据音效类型和音效ID查询音效名
    public static String soundIDToName(String type, int id) {
        // 音效类型为music
        if (Objects.equals(type, "music")) {
            if (id == 0) return "calm1";
            if (id == 1) return "calm2";
            if (id == 2) return "calm3";
            if (id == 3) return "hal1";
            if (id == 4) return "hal2";
            if (id == 5) return "hal3";
            if (id == 6) return "hal4";
            if (id == 7) return "nuance1";
            if (id == 8) return "nuance2";
            if (id == 9) return "piano1";
        }

        // 音效类型为
        if (Objects.equals(type, "dig")) {
            if (id == 0) return "gravel";
            if (id == 1) return "grass";
            if (id == 2) return "stone";
            if (id == 3) return "wood";
        }

        // 音效类型为step
        if (Objects.equals(type, "step")) {
            if (id == 0) return "gravel";
            if (id == 1) return "grass";
            if (id == 2) return "stone";
            if (id == 3) return "wood";
        }

        // 音效类型为player
        if (Objects.equals(type, "player")) {
            if (id == 0) return "pop";
            if (id == 1) return "hurt";
            if (id == 2) return "eat";
            if (id == 3) return "hiccup";
        }

        // 音效类型为damage
        if (Objects.equals(type, "damage")) {
            if (id == 0) return "hit";
            if (id == 1) return "fallbig";
            if (id == 2) return "fallsmall";
            if (id == 3) return "toolbreak";
        }

        // 音效类型为gui
        if (Objects.equals(type, "gui")) {
            if (id == 0) return "click";
        }

        // 音效类型为zombie
        if (Objects.equals(type, "zombie")) {
            if (id == 0) return "hurt";
            if (id == 1) return "death";
            if (id == 2) return "say";
        }

        // 如果查无此名，返回"null"
        return "null";
    }
}