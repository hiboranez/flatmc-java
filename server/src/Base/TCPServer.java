package Base;

import Element.Entity;
import EntityType.Item;
import EntityType.Player;
import EntityType.Zombie;
import WorldTool.World;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TCPServer {
    public static List<Client> clientList = new CopyOnWriteArrayList<>();
    public static List<Thread> threadList1 = new CopyOnWriteArrayList<>();
    public static Thread listenerThread = null;

    public static void startReceivingClient() {
        listenerThread = new Thread(() -> {
            try {
                // 创建ServerSocket并绑定到指定端口
                ServerSocket serverSocket = new ServerSocket(25565);
                System.out.println("[/localhost] Server started");
                while (true) {
                    // 接受客户端连接
                    Socket clientSocket = serverSocket.accept();
                    // 输出客户端连接的信息
                    System.out.println("[" + clientSocket.getInetAddress() + "] Client connected");
                    broadcastToSingleClient("/widthWorld " + World.getInstance().getWidth() + "\n", clientSocket);
                    broadcastToSingleClient("/heightWorld " + World.getInstance().getHeight() + "\n", clientSocket);
                    broadcastToSingleClient("/voidSizeWorld " + World.getInstance().getVoidSize() + "\n", clientSocket);
                    broadcastToSingleClient("/gamaWorld " + World.getInstance().getGama() + "\n", clientSocket);
                    broadcastToSingleClient("/difficultyWorld " + World.getInstance().getDifficulty() + "\n", clientSocket);
                    broadcastToSingleClient("/xSpawnWorld " + World.getInstance().getxSpawn() + "\n", clientSocket);
                    broadcastToSingleClient("/ySpawnWorld " + World.getInstance().getySpawn() + "\n", clientSocket);
                    broadcastToSingleClient("/blockSizeWorld " + World.getInstance().getBlockSize() + "\n", clientSocket);
                    broadcastToSingleClient("/gravityWorld " + World.getInstance().getGravity() + "\n", clientSocket);
                    broadcastToSingleClient("/airResistanceWorld " + World.getInstance().getAirResistance() + "\n", clientSocket);
                    broadcastToSingleClient("/timeWorld " + World.getInstance().getTime() + "\n", clientSocket);
                    for (Entity entity : World.getInstance().getEntityList()) {
                        if (entity instanceof Player) {
                            Player player = (Player) entity;
                            broadcastToSingleClient("/updateNewPlayer " + player.getName() + " " + player.getX() + " " + player.getY() + "\n", clientSocket);
                            broadcastToSingleClient("/updatePlayerMode " + player.getName() + " " + player.getGameMode() + " " + player.isFlying() + " " + player.isKeepInventory() + "\n", clientSocket);
                            broadcastToSingleClient("/updateItemBarChosen " + player.getName() + " " + player.getItemBarChosen() + "\n", clientSocket);
                            broadcastToSingleClient("/updateItemBarAmount " + player.getName() + " " + StringConversion.intArrayToString(player.getItemBarAmount()) + "\n", clientSocket);
                            broadcastToSingleClient("/updateItemBarId " + player.getName() + " " + StringConversion.intArrayToString(player.getItemBarId()) + "\n", clientSocket);
                        } else if (entity instanceof Item) {
                            Item item = (Item) entity;
                            broadcastToSingleClient("/updateSummonItem " + item.getIdCode() + " " + item.getX() + " " + item.getY() + " " + item.getId() + " " + item.getAmount() + " " + item.getTimeNoCollect() + "\n", clientSocket);
                        } else if (entity instanceof Zombie) {
                            Zombie zombie = (Zombie) entity;
                            broadcastToSingleClient("/updateSummonZombie " + zombie.getIdCode() + " " + zombie.getX() + " " + zombie.getY() + " " + zombie.getHealth() + "\n", clientSocket);
                        }
                    }
                    // 将客户端Socket加入列表
                    clientList.add(new Client(clientSocket));
                    // 在新线程中处理客户端连接
                    Thread thread = new Thread(() -> startListeningToClient(clientSocket));
                    threadList1.add(thread);
                    thread.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        // 启动线程
        listenerThread.start();
    }

    public static void startListeningToClient(Socket clientSocket) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8), true);
            while (Main.serverOn) {
                String message = reader.readLine();
                if (message == null || !Main.serverOn) {
                    // 客户端断开连接或发送断开连接通知
                    break;
                }
                Command.recievedFromClientCommand(message, clientSocket);
            }
            // 从列表中移除断开连接的客户端
            for (Client client : clientList) {
                if (client.getSocket() == clientSocket) {
                    if (client.getPlayerName() != null) {
                        System.out.println("[/localhost] " + client.getPlayerName() + " left the game");
                        for (Entity entity : World.getInstance().getEntityList()) {
                            if (entity instanceof Player) {
                                Player player = (Player) entity;
                                if (player.getName().equals(client.getPlayerName())) {
                                    World.getInstance().getEntityList().remove(entity);
                                    World.getInstance().setEntityListExtension(World.getInstance().getEntityListExtension() + 1);
                                }
                            }
                        }
                        broadcastToOtherClients("/updatePlayerExit " + client.getPlayerName() + "\n", client.getSocket());
                    }
                    clientList.remove(client);
                }
            }
            System.out.println("[" + clientSocket.getInetAddress() + "] Client disconnected");
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcastToSingleClient(String message, Socket clientSocket) {
        try {
            // 获取目标客户端输出流
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8), true);
            // 发送信息给特定客户端
            writer.print(message);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void broadcastToOtherClients(String message, Socket clientSocket) {
        for (Client client : clientList) {
            if (client.getSocket() != clientSocket) {
                try {
                    // 获取客户端输出流
                    PrintWriter writer = new PrintWriter(new OutputStreamWriter(client.getSocket().getOutputStream(), StandardCharsets.UTF_8), true);
                    // 发送信息给其他客户端
                    writer.print(message);
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void broadcastToAllClients(String message) {
        for (Client client : clientList) {
            try {
                // 获取客户端输出流
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(client.getSocket().getOutputStream(), StandardCharsets.UTF_8), true);
                // 发送信息给所有其他客户端
                writer.print(message);
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
