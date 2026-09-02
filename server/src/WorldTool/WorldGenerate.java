package WorldTool;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static java.lang.Math.abs;

public class WorldGenerate {

    public static String path = System.getProperty("user.dir") + "/data/world/";

    public static void generateWorld(String nameWorld, int width, int height) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(path + nameWorld + ".txt"));
            writer.write("width = " + width + "\n");
            writer.write("height = " + height + "\n");
            writer.write("blockIdList = {\n");
            // 定义世界表格
            int[][] blockIdList = new int[height][width];
            // 定义树木表格
            int[] treeHeightDeviation = new int[width];
            // 生成地形噪声
            FastNoiseLite noise = new FastNoiseLite();
            // 定义柏林噪声
            noise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
            noise.SetFractalOctaves(4);
            noise.SetFractalLacunarity(2.0f);
            noise.SetFractalGain(0.5f);
            // 定义最大起伏
            double maxHeightDeviation = height * 0.1;
            // 定义重生点坐标
            int xWorldSpawn = width / 2;
            int yWorldSpawn = 0;
            // 定义矿洞草坪线
            List<Integer> heightDeviationCaveList = new ArrayList<>();
            // 生成草坪线
            for (int x = 0; x < width; x++) {
                double noiseValue = noise.GetNoise((float) x, (float) 500);
                int heightDeviation = (int) (20 * noise.GetNoise((float) noiseValue * 300, (float) noiseValue * 600)) + height / 2 + (int) Math.round(noiseValue * maxHeightDeviation);
                heightDeviationCaveList.add(heightDeviation);
                if (x == xWorldSpawn) yWorldSpawn = heightDeviation - 2;
                // 填充草坪线以下地壳
                for (int y = 0; y < height; y++) {
                    int blockType;
                    if (y < heightDeviation) {
                        blockType = -1; // Grass block above half height
                    } else if (y == heightDeviation) {
                        blockType = 1; // Grass block above half height
                    } else if (y > heightDeviation && y <= 5 + heightDeviation + abs((int) (10 * noise.GetNoise(-(float) noiseValue * 600, (float) noiseValue * 300)))) {
                        blockType = 0; // Dirt block below grass
                    } else {
                        blockType = 2; // Stone block below dirt
                    }
                    blockIdList[y][x] = blockType;
                }
                Random random = new Random();
                // 生成煤矿
                int randomNumber1 = random.nextInt(5000);
                randomNumber1++;
                if (x == 0) {
                    for (int y = heightDeviation; y < height - 1; y++) {
                        if (blockIdList[y][x] == 2 && randomNumber1 <= 100)
                            blockIdList[y][x] = 19;
                        randomNumber1 = random.nextInt(5000);
                        randomNumber1++;
                    }
                } else {
                    for (int y = heightDeviation; y < height - 1; y++) {
                        if (blockIdList[y][x] == 2) {
                            if ((blockIdList[y][x - 1] == 19 || blockIdList[y - 1][x - 1] == 19 || blockIdList[y + 1][x - 1] == 19) && randomNumber1 <= 500) {
                                blockIdList[y][x] = 19;
                            } else if (blockIdList[y][x - 1] != 19 && randomNumber1 <= 100) {
                                blockIdList[y][x] = 19;
                            }
                        }
                        randomNumber1 = random.nextInt(5000);
                        randomNumber1++;
                    }
                }
                // 生成铁矿
                int randomNumber2 = random.nextInt(5000);
                randomNumber2++;
                if (x == 0) {
                    for (int y = heightDeviation; y < height - 1; y++) {
                        if (blockIdList[y][x] == 2 && randomNumber2 <= 75)
                            blockIdList[y][x] = 20;
                        randomNumber2 = random.nextInt(5000);
                        randomNumber2++;
                    }
                } else {
                    for (int y = heightDeviation; y < height - 1; y++) {
                        if (blockIdList[y][x] == 2) {
                            if ((blockIdList[y][x - 1] == 20 || blockIdList[y - 1][x - 1] == 20 || blockIdList[y + 1][x - 1] == 20) && randomNumber2 <= 400) {
                                blockIdList[y][x] = 20;
                            } else if (blockIdList[y][x - 1] != 20 && randomNumber2 <= 75) {
                                blockIdList[y][x] = 20;
                            }
                        }
                        randomNumber2 = random.nextInt(5000);
                        randomNumber2++;
                    }
                }
                // 生成金矿
                int randomNumber3 = random.nextInt(5000);
                randomNumber3++;
                int goldLine = (int) (heightDeviation * 1.5);
                if (goldLine >= height) goldLine = height - 2;
                if (x == 0) {
                    for (int y = goldLine; y < height - 1; y++) {
                        if (blockIdList[y][x] == 2 && randomNumber3 <= 50)
                            blockIdList[y][x] = 21;
                        randomNumber3 = random.nextInt(5000);
                        randomNumber3++;
                    }
                } else {
                    for (int y = goldLine; y < height - 1; y++) {
                        if (blockIdList[y][x] == 2) {
                            if ((blockIdList[y][x - 1] == 21 || blockIdList[y - 1][x - 1] == 21 || blockIdList[y + 1][x - 1] == 21) && randomNumber3 <= 300) {
                                blockIdList[y][x] = 21;
                            } else if (blockIdList[y][x - 1] != 21 && randomNumber3 <= 50) {
                                blockIdList[y][x] = 21;
                            }
                        }
                        randomNumber3 = random.nextInt(5000);
                        randomNumber3++;
                    }
                }
                // 生成钻石矿
                int randomNumber4 = random.nextInt(5000);
                randomNumber4++;
                int diamondLine = (int) (heightDeviation * 1.75);
                if (diamondLine >= height) diamondLine = height - 2;
                if (x == 0) {
                    for (int y = diamondLine; y < height - 1; y++) {
                        if (blockIdList[y][x] == 2 && randomNumber4 <= 30)
                            blockIdList[y][x] = 22;
                        randomNumber4 = random.nextInt(5000);
                        randomNumber4++;
                    }
                } else {
                    for (int y = diamondLine; y < height - 1; y++) {
                        if (blockIdList[y][x] == 2) {
                            if ((blockIdList[y][x - 1] == 22 || blockIdList[y - 1][x - 1] == 22 || blockIdList[y + 1][x - 1] == 22) && randomNumber4 <= 200) {
                                blockIdList[y][x] = 22;
                            } else if (blockIdList[y][x - 1] != 22 && randomNumber4 <= 30) {
                                blockIdList[y][x] = 22;
                            }
                        }
                        randomNumber4 = random.nextInt(5000);
                        randomNumber4++;
                    }
                }
                // 生成树林
                if (x >= 2 && x <= width - 3)
                    if ((abs(noiseValue) >= 0 && abs(noiseValue) <= 0.1) || (abs(noiseValue) >= 0.5 && abs(noiseValue) <= 0.6)) {
                        boolean canGenerateTree = true;
                        for (int i = -2; i <= 2; i++)
                            if (treeHeightDeviation[x + i] != 0)
                                canGenerateTree = false;
                        if (canGenerateTree) treeHeightDeviation[x] = heightDeviation;
                    }
            }
            int averageGrassLevel = (Collections.max(heightDeviationCaveList) + Collections.min(heightDeviationCaveList)) / 2;
            for (int x = 0; x < width; x++)
                if (treeHeightDeviation[x] != 0 && x >= 2 && x <= width - 3) {
                    for (int i = 1; i < 4; i++)
                        blockIdList[treeHeightDeviation[x] - i][x] = 5;
                    blockIdList[treeHeightDeviation[x] - 4][x] = 7;
                    blockIdList[treeHeightDeviation[x] - 5][x] = 7;
                    blockIdList[treeHeightDeviation[x] - 4][x - 1] = 7;
                    blockIdList[treeHeightDeviation[x] - 5][x - 1] = 7;
                    blockIdList[treeHeightDeviation[x] - 4][x + 1] = 7;
                    blockIdList[treeHeightDeviation[x] - 5][x + 1] = 7;
                    blockIdList[treeHeightDeviation[x] - 4][x - 2] = 7;
                    blockIdList[treeHeightDeviation[x] - 4][x + 2] = 7;
                    heightDeviationCaveList.set(x, -1);
                    if (x >= 1)
                        heightDeviationCaveList.set(x - 1, -1);
                    if (x <= height - 2)
                        heightDeviationCaveList.set(x + 1, -1);
                    if (x >= 2)
                        heightDeviationCaveList.set(x - 2, -1);
                    if (x <= height - 3)
                        heightDeviationCaveList.set(x + 2, -1);
                }
            // 生成矿洞
            Random random = new Random();
            for (int k = 5; k < width - 6; k++) {
                int randomCanGenerateCave = random.nextInt(1000);
                randomCanGenerateCave++;
                if (randomCanGenerateCave < 30 && heightDeviationCaveList.get(k) != -1 && heightDeviationCaveList.get(k) >= averageGrassLevel) {
                    int x = k;
                    int y = heightDeviationCaveList.get(k) - 5;
                    heightDeviationCaveList.set(k, -1);
                    heightDeviationCaveList.set(k - 1, -1);
                    heightDeviationCaveList.set(k - 2, -1);
                    heightDeviationCaveList.set(k - 3, -1);
                    heightDeviationCaveList.set(k - 4, -1);
                    heightDeviationCaveList.set(k - 5, -1);
                    heightDeviationCaveList.set(k + 1, -1);
                    heightDeviationCaveList.set(k + 2, -1);
                    heightDeviationCaveList.set(k + 3, -1);
                    heightDeviationCaveList.set(k + 4, -1);
                    heightDeviationCaveList.set(k + 5, -1);
                    int randomCaveDirection = random.nextInt(2);
                    while (y != -1) {
                        float noiseValue = (500 * noise.GetNoise((float) (x / 100.0), (float) (y / 100.0)));
                        int widthDelta = (int) (11 * (noiseValue - (int) noiseValue));
                        if (widthDelta < 7) widthDelta = 7;
                        for (int i = -widthDelta / 2; i < widthDelta - widthDelta / 2; i++) {
                            if (x + i < 0 || x + i > width - 1) continue;
                            blockIdList[y][x + i] = -1;
                        }
                        int xDelta;
                        if (randomCaveDirection == 0) {
                            if (random.nextInt(100) + 1 <= 70)
                                xDelta = -1;
                            else xDelta = -2;
                        } else {
                            if (random.nextInt(100) + 1 <= 70)
                                xDelta = 1;
                            else xDelta = 2;
                        }
                        x += xDelta;
                        y++;
                        int randomCaveEnd = random.nextInt(1000);
                        if (randomCaveEnd < 23) y = -1;
                        if (y >= height) y = -1;
                    }
                }
            }
            // 绘制边界
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (x == 0 || x == width - 1) blockIdList[y][x] = 3;
                    else if (y == 0 || y == height - 1) blockIdList[y][x] = 3;
                }
            }
            // 写入地图存档
            for (int y = 0; y < height; y++) {
                writer.write("[");
                for (int x = 0; x < width; x++) {
                    writer.write(Integer.toString(blockIdList[y][x]));
                    if (x != width - 1) {
                        writer.write(",");
                    }
                }
                writer.write("]\n");
            }
            writer.write("}\n");
            writer.write("voidSize = " + 10 + "\n");
            writer.write("gama = " + 0.4 + "\n");
            writer.write("difficulty = " + "normal" + "\n");
            writer.write("xSpawn = " + xWorldSpawn * 50 + "\n");
            writer.write("ySpawn = " + yWorldSpawn * 50 + "\n");
            writer.write("blockSize = " + 50 + "\n");
            writer.write("gravity = " + 0.5 + "\n");
            writer.write("airResistance = " + 0.5 + "\n");
            writer.write("time = " + 60000 + "\n");
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}