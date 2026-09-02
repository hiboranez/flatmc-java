package Base;

import WorldTool.World;
import WorldTool.WorldGenerate;
import WorldTool.WorldLoad;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static Thread consoleListenerThread;
    public static boolean serverOn = true;
    public static String path = System.getProperty("user.dir") + "/data/world/";

    public static void main(String[] args) {
        // 检查是否存在world.txt文件
        File worldFile = new File(path + "world.txt");
        if (!worldFile.exists()) {
            // 如果不存在，则生成world.txt文件
            WorldGenerate.generateWorld("world", 2001, 1001);
        }
        // 创建World实例并加载world.txt文件
        WorldLoad.loadWorld("world", World.getInstance());
        World.getInstance().setLightIntensity(new int[World.getInstance().getHeight()][World.getInstance().getWidth()]);
        World.getInstance().renderLightIntensity(0, World.getInstance().getWidth() - 1);
        // 启动TCP服务器
        TCPServer.startReceivingClient();
        // 开启服务台监听
        startConsoleListener();
        World.getInstance().getThread().start();
    }

    private static void startConsoleListener() {
        consoleListenerThread = new Thread(() -> {
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            try {
                while (serverOn) {
                    // 从命令台读取信息并存储到consoleMessages中
                    String input = consoleReader.readLine();
                    Command.ReceivedFromServerCommand(input);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        // 启动命令台监听线程
        consoleListenerThread.start();
    }
}
