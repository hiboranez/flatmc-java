package Base;

import Element.Entity;
import EntityType.Item;
import EntityType.Player;
import EntityType.Zombie;
import WorldTool.World;
import WorldTool.WorldSave;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class Command {
    public static List<TpaRequest> tpaRequestList = new CopyOnWriteArrayList<>();

    public static void recievedFromClientCommand(String command, Socket socket) {
        String[] parts = command.split(" ");
        boolean opPermission = false;
        if (World.getInstance().getOpList().contains(parts[0])) opPermission = true;

        if (parts.length >= 2) {
            // 状态命令
            if (Objects.equals(parts[1], "/updatePlayerLink")) {
                if (parts.length == 3) {
                    boolean noSameName = true;
                    boolean banned = false;
                    boolean baniped = false;
                    for (Entity entityTmp : World.getInstance().getEntityList()) {
                        if (entityTmp instanceof Player) {
                            Player playerTmp = (Player) entityTmp;
                            if (playerTmp.getName().equals(parts[2])) {
                                noSameName = false;
                                break;
                            }
                        }
                    }
                    if (noSameName) {
                        Player player = null;
                        // 建立玩家与世界的连接
                        for (Player player1 : World.getInstance().getPlayerList()) {
                            if (Objects.equals(player1.getName(), parts[2])) {
                                player1.setIdCode(World.getInstance().getEntityList().size() - 1 + World.getInstance().getEntityListExtension());
                                TCPServer.broadcastToSingleClient("/xPlayer " + player1.getX() + "\n", socket);
                                TCPServer.broadcastToSingleClient("/yPlayer " + player1.getY() + "\n", socket);
                                TCPServer.broadcastToSingleClient("/xSpawnPlayer " + player1.getxSpawn() + "\n", socket);
                                TCPServer.broadcastToSingleClient("/ySpawnPlayer " + player1.getySpawn() + "\n", socket);
                                TCPServer.broadcastToSingleClient("/gameModePlayer " + player1.getGameMode() + "\n", socket);
                                TCPServer.broadcastToSingleClient("/healthPlayer " + player1.getHealth() + "\n", socket);
                                TCPServer.broadcastToSingleClient("/flyingPlayer " + player1.isFlying() + "\n", socket);
                                TCPServer.broadcastToSingleClient("/autoJumpPlayer: " + player1.isAutoJump() + "\n", socket);
                                TCPServer.broadcastToSingleClient("/keepInventoryPlayer " + player1.isKeepInventory() + "\n", socket);
                                TCPServer.broadcastToSingleClient("/ItemBarAmountPlayer " + StringConversion.intArrayToString(player1.getItemBarAmount()) + "\n", socket);
                                TCPServer.broadcastToSingleClient("/ItemBarIdPlayer " + StringConversion.intArrayToString(player1.getItemBarId()) + "\n", socket);
                                player = player1;
                                World.getInstance().getEntityList().add(player);
                            }
                        }
                        if (player == null) {
                            player = new Player(parts[2]);
                            player.setIdCode(World.getInstance().getEntityList().size() - 1 + World.getInstance().getEntityListExtension());
                        }
                        player.setHasGravity(false);
                        player.setBlockIdListLoaded(new int[(World.getInstance().getHeight() - 1) / 50][(World.getInstance().getWidth() - 1) / 50]);
                        for (Client client : TCPServer.clientList)
                            if (client.getSocket() == socket) {
                                client.setPlayerName(parts[2]);
                                client.getThread().start();
                            }
                        if (World.getInstance().getBanList().contains(parts[2])) {
                            banned = true;
                            for (Client client : TCPServer.clientList) {
                                if (client.getPlayerName() != null && client.getPlayerName().equals(parts[2])) {
                                    client.getThread().interrupt();
                                    break;
                                }
                            }
                        }
                        for (Client client : TCPServer.clientList) {
                            if (client.getPlayerName() != null && client.getPlayerName().equals(parts[2])) {
                                if (World.getInstance().getBanIpList().contains(String.valueOf(client.getSocket().getInetAddress()))) {
                                    client.getThread().interrupt();
                                    baniped = true;
                                    break;
                                }
                            }
                        }
                        if (!baniped) {
                            if (!banned) {
                                player.setxCenter(player.getX()+10);
                                player.setyCenter(player.getY()+95/2);
                                World.getInstance().updatePlayerAroundBlockIdList();
                                TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + "\n", socket);
                                TCPServer.broadcastToOtherClients("/updatePlayerJoin " + parts[2] + "\n", socket);
                                TCPServer.broadcastToOtherClients("/updateNewPlayer " + parts[2] + " " + player.getX() + " " + player.getY() + "\n", socket);
                                TCPServer.broadcastToOtherClients("/updatePlayerMode " + player.getName() + " " + player.getGameMode() + " " + player.isFlying() + " " + player.isKeepInventory() + "\n", socket);
                                TCPServer.broadcastToOtherClients("/updateItemBarChosen " + player.getName() + " " + player.getItemBarChosen() + "\n", socket);
                                TCPServer.broadcastToOtherClients("/updateItemBarAmount " + player.getName() + " " + StringConversion.intArrayToString(player.getItemBarAmount()) + "\n", socket);
                                TCPServer.broadcastToOtherClients("/updateItemBarId " + player.getName() + " " + StringConversion.intArrayToString(player.getItemBarId()) + "\n", socket);
                                System.out.println("[/localhost] " + parts[0] + " joined the game");
                            } else {
                                for (Client client : TCPServer.clientList)
                                    if (client.getSocket() == socket)
                                        client.setPlayerName(null);
                                World.getInstance().getEntityList().remove(player);
                                TCPServer.broadcastToSingleClient("/updatePlayerLinkBan " + parts[2] + "\n", socket);
                                System.out.println("[" + socket.getInetAddress() + "] Failed login with: " + parts[2]);
                            }
                        } else {
                            for (Client client : TCPServer.clientList)
                                if (client.getSocket() == socket)
                                    client.setPlayerName(null);
                            World.getInstance().getEntityList().remove(player);
                            TCPServer.broadcastToSingleClient("/updatePlayerLinkBanIp " + parts[2] + "\n", socket);
                            System.out.println("[" + socket.getInetAddress() + "] Failed login with: " + parts[2]);
                        }
                    } else {
                        for (Client client : TCPServer.clientList)
                            if (client.getSocket() == socket)
                                client.setPlayerName(null);
                        TCPServer.broadcastToSingleClient("/updatePlayerLinkDeny " + parts[2] + "\n", socket);
                        System.out.println("[" + socket.getInetAddress() + "] Failed login with: " + parts[2]);
                    }
                }
            } else if (Objects.equals(parts[1], "/updateDenied")) {
                if (parts.length == 3) {
                    TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + "\n", socket);
                }
            } else if (Objects.equals(parts[1], "/updateUsage")) {
                if (parts.length == 3) {
                    TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + "\n", socket);
                }
            } else if (Objects.equals(parts[1], "/updateAlive")) {
                if (parts.length == 3) {
                    TCPServer.broadcastToSingleClient("/updateAlive 1200\n", socket);
                }
            } else if (Objects.equals(parts[1], "/updatePlayerJoin")) {
                if (parts.length == 3) {
                    TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + "\n", socket);
                }
            } else if (Objects.equals(parts[1], "/updatePlayerExit")) {
                if (parts.length == 3) {
                    TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + "\n", socket);
                }
            } else if (Objects.equals(parts[1], "/updateNewPlayer")) {
                if (parts.length == 5) {
                    TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + "\n", socket);
                }
            } else if (Objects.equals(parts[1], "/updateTime")) {
                if (parts.length == 3) {
                    TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + "\n", socket);
                }
            } else if (Objects.equals(parts[1], "/updateBlockIdListSingle")) {
                if (parts.length == 5) {
                    int x = Integer.parseInt(parts[2]);
                    int y = Integer.parseInt(parts[3]);
                    int id = Integer.parseInt(parts[4]);
                    boolean outOfProtection = true;
                    for (Area area : World.getInstance().getAreaProtectedList()) {
                        if (area.getX1() + World.getInstance().getWidth() / 2 <= x && x <= area.getX2() + World.getInstance().getWidth() / 2)
                            if (area.getY1() + World.getInstance().getHeight() / 2 <= y && y <= area.getY2() + World.getInstance().getHeight() / 2) {
                                outOfProtection = false;
                            }
                    }
                    if (outOfProtection || opPermission) {
                        if (isCanOperate(parts[0])) {
                            if (World.getInstance().getBlockIdList()[y][x] != id)
                                World.getInstance().getBlockIdList()[y][x] = id;
                            TCPServer.broadcastToAllClients(parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + "\n");
                        } else {
                            TCPServer.broadcastToSingleClient("/updateBlockIdListSingle" + " " + parts[2] + " " + parts[3] + " " + World.getInstance().getBlockIdList()[y][x] + "\n", socket);
                        }
                    } else {
                        TCPServer.broadcastToSingleClient("/updateBlockIdListSingle" + " " + parts[2] + " " + parts[3] + " " + World.getInstance().getBlockIdList()[y][x] + "\n", socket);
                        TCPServer.broadcastToSingleClient("/updateAreaProtected\n", socket);
                    }
                }
            } else if (Objects.equals(parts[1], "/updateBlockIdListSingleNoSound")) {
                if (parts.length == 5) {
                    int x = Integer.parseInt(parts[2]);
                    int y = Integer.parseInt(parts[3]);
                    int id = Integer.parseInt(parts[4]);
                    if (World.getInstance().getBlockIdList()[y][x] != id)
                        World.getInstance().getBlockIdList()[y][x] = id;
                    TCPServer.broadcastToAllClients(parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + "\n");
                }
            } else if (Objects.equals(parts[1], "/updateBlockIdListRange")) {
                if (parts.length == 7) {
                    int[][] blockIdListRange = StringConversion.stringToIntDoubleArray(parts[6]);
                    int j = 0;
                    for (int y = Integer.parseInt(parts[3]); y < Integer.parseInt(parts[5]); y++) {
                        int i = 0;
                        for (int x = Integer.parseInt(parts[2]); x < Integer.parseInt(parts[4]); x++) {
                            World.getInstance().getBlockIdList()[y][x] = blockIdListRange[j][i];
                            i++;
                        }
                        j++;
                    }
                    TCPServer.broadcastToOtherClients(parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + " " + parts[5] + " " + parts[6] + "\n", socket);
                }
            } else if (Objects.equals(parts[1], "/updateLocation")) {
                if (parts.length == 6) {
                    if (parts[2].equals("player")) {
                        if (isCanOperate(parts[0])) {
                            for (Entity entity : World.getInstance().getEntityList())
                                if (entity instanceof Player) {
                                    Player player = (Player) entity;
                                    if (player.getName().equals(parts[3])) {
                                        player.setX(Integer.parseInt(parts[4]));
                                        player.setY(Integer.parseInt(parts[5]));
                                    }
                                }
                        } else {
                            for (Entity entity : World.getInstance().getEntityList())
                                if (entity instanceof Player) {
                                    Player player = (Player) entity;
                                    if (player.getName().equals(parts[3])) {
                                        TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + " " + parts[3] + " " + player.getX() + " " + player.getY() + "\n", socket);
                                    }
                                }
                        }
                    } else if (parts[2].equals("other")) {
                        for (Entity entity : World.getInstance().getEntityList()) {
                            if (entity.getIdCode() == Integer.parseInt(parts[3])) {
                                entity.setX(Integer.parseInt(parts[4]));
                                entity.setY(Integer.parseInt(parts[5]));
                            }
                        }
                    }
                    TCPServer.broadcastToOtherClients(parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + " " + parts[5] + "\n", socket);
                }
            } else if (Objects.equals(parts[1], "/updateTimer")) {
                if (parts.length == 7) {
                    if (parts[2].equals("player")) {
                        for (Entity entity : World.getInstance().getEntityList())
                            if (entity instanceof Player) {
                                Player player = (Player) entity;
                                if (player.getName().equals(parts[3])) {
                                    player.setWalkTimer(Integer.parseInt(parts[4]));
                                    player.setRunTimer(Integer.parseInt(parts[5]));
                                    player.setClickTimer(Integer.parseInt(parts[6]));
                                }
                            }
                    } else if (parts[2].equals("other")) {
                        for (Entity entity : World.getInstance().getEntityList()) {
                            if (entity.getIdCode() == Integer.parseInt(parts[3])) {
                                if (entity instanceof Zombie) {
                                    Zombie zombie = (Zombie) entity;
                                    zombie.setWalkTimer(Integer.parseInt(parts[4]));
                                }
                            }
                        }
                    }
                    TCPServer.broadcastToOtherClients(parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + " " + parts[5] + " " + parts[6] + "\n", socket);
                }
            } else if (Objects.equals(parts[1], "/updateState")) {
                if (parts.length == 6) {
                    if (parts[2].equals("player")) {
                        for (Entity entity : World.getInstance().getEntityList())
                            if (entity instanceof Player) {
                                Player player = (Player) entity;
                                Boolean commandCorrect1 = false;
                                Boolean commandCorrect2 = false;
                                if (player.getName().equals(parts[3])) {
                                    if (parts[4].equals("left")) {
                                        player.setFaceTo("left");
                                        commandCorrect1 = true;
                                    } else if (parts[4].equals("right")) {
                                        player.setFaceTo("right");
                                        commandCorrect1 = true;
                                    }
                                    if (parts[5].equals("stand")) {
                                        player.setMoveState("stand");
                                        commandCorrect2 = true;
                                    } else if (parts[5].equals("walk")) {
                                        player.setMoveState("walk");
                                        commandCorrect2 = true;
                                    } else if (parts[5].equals("run")) {
                                        player.setMoveState("run");
                                        commandCorrect2 = true;
                                    }
                                    if (commandCorrect1 && commandCorrect2)
                                        TCPServer.broadcastToOtherClients(parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + " " + parts[5] + "\n", socket);
                                }
                            }
                    } else if (parts[2].equals("other")) {
                        for (Entity entity : World.getInstance().getEntityList()) {
                            if (entity.getIdCode() == Integer.parseInt(parts[3])) {
                                if (entity instanceof Zombie) {
                                    Zombie zombie = (Zombie) entity;
                                    Boolean commandCorrect1 = false;
                                    Boolean commandCorrect2 = false;
                                    if (parts[4].equals("left")) {
                                        zombie.setFaceTo("left");
                                        commandCorrect1 = true;
                                    } else if (parts[4].equals("right")) {
                                        zombie.setFaceTo("right");
                                        commandCorrect1 = true;
                                    }
                                    if (parts[5].equals("stand")) {
                                        zombie.setMoveState("stand");
                                        commandCorrect2 = true;
                                    } else if (parts[5].equals("walk")) {
                                        zombie.setMoveState("walk");
                                        commandCorrect2 = true;
                                    }
                                    if (commandCorrect1 && commandCorrect2)
                                        TCPServer.broadcastToOtherClients(parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + " " + parts[5] + "\n", socket);
                                }
                            }
                        }
                    }
                }
            } else if (Objects.equals(parts[1], "/updateEntityData")) {
                if (parts.length == 6) {
                    if (parts[2].equals("player")) {
                        for (Entity entity : World.getInstance().getEntityList())
                            if (entity instanceof Player) {
                                Player player = (Player) entity;
                                if (player.getName().equals(parts[3])) {
                                    player.setHealth(Integer.parseInt(parts[4]));
                                    player.setDead(Boolean.parseBoolean(parts[5]));
                                }
                            }
                    } else if (parts[2].equals("other")) {
                        for (Entity entity : World.getInstance().getEntityList()) {
                            if (entity.getIdCode() == Integer.parseInt(parts[3])) {
                                entity.setHealth(Integer.parseInt(parts[4]));
                                entity.setDead(Boolean.parseBoolean(parts[5]));
                            }
                        }
                    }
                    TCPServer.broadcastToOtherClients(parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + " " + parts[5] + "\n", socket);
                }
            } else if (Objects.equals(parts[1], "/updateAttack")) {
                if (parts.length == 6 || parts.length == 7) {
                    if (parts[2].equals("player")) {
                        if (isCanOperate(parts[0]) && isCanOperate(parts[3])) {
                            int x = 0;
                            int y = 0;
                            for (Entity entity : World.getInstance().getEntityList()) {
                                if (entity instanceof Player) {
                                    Player player = (Player) entity;
                                    if (player.getName().equals(parts[3])) {
                                        x = player.getxCenter() / 50;
                                        y = player.getyCenter() / 50;
                                    }
                                }
                            }
                            boolean outOfProtection = true;
                            for (Area area : World.getInstance().getAreaProtectedList()) {
                                if (area.getX1() + World.getInstance().getWidth() / 2 <= x && x <= area.getX2() + World.getInstance().getWidth() / 2)
                                    if (area.getY1() + World.getInstance().getHeight() / 2 <= y && y <= area.getY2() + World.getInstance().getHeight() / 2) {
                                        outOfProtection = false;
                                    }
                            }
                            if (outOfProtection || opPermission) {
                                if (parts.length == 6)
                                    TCPServer.broadcastToOtherClients(parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + " " + parts[5] + "\n", socket);
                                else if (parts.length == 7)
                                    TCPServer.broadcastToOtherClients(parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + " " + parts[5] + " " + parts[6] + "\n", socket);
                            } else {
                                TCPServer.broadcastToSingleClient("/updateAreaProtected\n", socket);
                            }
                        }
                    } else {
                        if (parts.length == 6)
                            TCPServer.broadcastToOtherClients(parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + " " + parts[5] + "\n", socket);
                        else if (parts.length == 7)
                            TCPServer.broadcastToOtherClients(parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + " " + parts[5] + " " + parts[6] + "\n", socket);
                    }
                }
            } else if (Objects.equals(parts[1], "/updatePlayerMode")) {
                if (parts.length == 6) {
                    TCPServer.broadcastToOtherClients(parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + " " + parts[5] + "\n", socket);
                }
            } else if (Objects.equals(parts[1], "/updateItemBarChosen")) {
                if (parts.length == 4) {
                    for (Entity entity : World.getInstance().getEntityList())
                        if (entity instanceof Player) {
                            Player player = (Player) entity;
                            if (player.getName().equals(parts[2])) {
                                player.setItemBarChosen(Integer.parseInt(parts[3]));
                            }
                        }
                    TCPServer.broadcastToOtherClients(parts[1] + " " + parts[2] + " " + parts[3] + "\n", socket);
                }
            } else if (Objects.equals(parts[1], "/updateItemBarAmount")) {
                if (parts.length == 4) {
                    if (isCanOperate(parts[0])) {
                        for (Entity entity : World.getInstance().getEntityList())
                            if (entity instanceof Player) {
                                Player player = (Player) entity;
                                if (player.getName().equals(parts[2])) {
                                    int[] itemBarAmountTmp = StringConversion.stringToIntArray(parts[3]);
                                    player.setItemBarAmount(itemBarAmountTmp);
                                }
                            }
                        TCPServer.broadcastToOtherClients(parts[1] + " " + parts[2] + " " + parts[3] + "\n", socket);
                    }
                }
            } else if (Objects.equals(parts[1], "/updateItemBarId")) {
                if (parts.length == 4) {
                    if (isCanOperate(parts[0])) {
                        for (Entity entity : World.getInstance().getEntityList())
                            if (entity instanceof Player) {
                                Player player = (Player) entity;
                                if (player.getName().equals(parts[2])) {
                                    int[] itemBarIdTmp = StringConversion.stringToIntArray(parts[3]);
                                    player.setItemBarId(itemBarIdTmp);
                                }
                            }
                        TCPServer.broadcastToOtherClients(parts[1] + " " + parts[2] + " " + parts[3] + "\n", socket);
                    }
                }
            } else if (Objects.equals(parts[1], "/updateSummonItem")) {
                if (parts.length == 7) {
                    int x = Integer.parseInt(parts[2]) / 50;
                    int y = Integer.parseInt(parts[3]) / 50;
                    boolean outOfProtection = true;
                    for (Area area : World.getInstance().getAreaProtectedList()) {
                        if (area.getX1() + World.getInstance().getWidth() / 2 <= x && x <= area.getX2() + World.getInstance().getWidth() / 2)
                            if (area.getY1() + World.getInstance().getHeight() / 2 <= y && y <= area.getY2() + World.getInstance().getHeight() / 2) {
                                outOfProtection = false;
                            }
                    }
                    if (isCanOperate(parts[0]) && (outOfProtection || opPermission)) {
                        int idCode = World.getInstance().getEntityList().size() - 1 + World.getInstance().getEntityListExtension();
                        Item item = new Item(idCode, Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]), Integer.parseInt(parts[6]));
                        World.getInstance().getEntityList().add(item);
                        TCPServer.broadcastToAllClients(parts[1] + " " + item.getIdCode() + " " + parts[2] + " " + parts[3] + " " + parts[4] + " " + parts[5] + " " + parts[6] + "\n");
                    }
                }
            } else if (Objects.equals(parts[1], "/updateRemoveItem")) {
                if (parts.length == 3) {
                    if (isCanOperate(parts[0])) {
                        if (World.getInstance() != null) {
                            for (Entity entity : World.getInstance().getEntityList()) {
                                if (entity instanceof Item) {
                                    if (entity.getIdCode() == Integer.parseInt(parts[2])) {
                                        World.getInstance().getEntityList().remove(entity);
                                        World.getInstance().setEntityListExtension(World.getInstance().getEntityListExtension() + 1);
                                    }
                                }
                            }
                            TCPServer.broadcastToAllClients(parts[1] + " " + parts[2] + "\n");
                        }
                    }
                }
            } else if (Objects.equals(parts[1], "/updateSummonItemWithXSpeed")) {
                if (parts.length == 8) {
                    if (isCanOperate(parts[0])) {
                        int idCode = World.getInstance().getEntityList().size() - 1 + World.getInstance().getEntityListExtension();
                        Item item = new Item(idCode, Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]), Integer.parseInt(parts[6]));
                        World.getInstance().getEntityList().add(item);
                        TCPServer.broadcastToAllClients(parts[1] + " " + item.getIdCode() + " " + parts[2] + " " + parts[3] + " " + parts[4] + " " + parts[5] + " " + parts[6] + " " + parts[7] + "\n");
                    }
                }
            } else if (Objects.equals(parts[1], "/updateIsOp")) {
                if (parts.length == 3) {
                    if (World.getInstance().getOpList().contains(parts[2]))
                        TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + " Yes\n", socket);
                    else TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + " No\n", socket);
                }
            } else if (Objects.equals(parts[1], "/updateSound")) {
                if (parts.length == 6) {
                    TCPServer.broadcastToAllClients(parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + " " + parts[5] + "\n");
                }
            } else if (Objects.equals(parts[1], "/updateCrack")) {
                if (parts.length == 5) {
                    TCPServer.broadcastToAllClients(parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + " " + parts[0] + "\n");
                }
            } else if (Objects.equals(parts[1], "/updateChat")) {
                if (parts.length == 3) {
                    TCPServer.broadcastToAllClients(parts[1] + " " + parts[0] + " " + parts[2] + "\n");
                }
            } else if (Objects.equals(parts[1], "/updateText")) {
                if (parts.length >= 4) {
                    if (parts[3].charAt(0) == '/') {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 3; i < parts.length; i++) {
                            stringBuilder.append(parts[i]);
                            if (i == parts.length - 1) break;
                            stringBuilder.append(" ");
                        }
                        Command.recievedFromClientCommand(parts[0] + " " + stringBuilder.toString(), socket);
                    } else {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 2; i < parts.length; i++) {
                            stringBuilder.append(" ");
                            stringBuilder.append(parts[i]);
                        }
                        System.out.println(stringBuilder.substring(1));
                        TCPServer.broadcastToAllClients(parts[1] + stringBuilder + "\n");
                    }
                }
            } else if (Objects.equals(parts[1], "/updateDeathInfo")) {
                if (parts.length == 4) {
                    if (parts[2].equals("kill"))
                        System.out.println("[" + parts[0] + "] " + parts[0] + " killed " + parts[3]);
                    else if (parts[2].equals("fall"))
                        System.out.println("[" + parts[0] + "] " + parts[3] + " fell to death");
                    else if (parts[2].equals("choke"))
                        System.out.println("[" + parts[0] + "] " + parts[3] + " choked to death");
                    else if (parts[2].equals("zombie"))
                        System.out.println("[" + parts[0] + "] " + parts[3] + "'s brain was eaten by zombie");
                    else if (parts[2].equals("void"))
                        System.out.println("[" + parts[0] + "] " + parts[3] + " fell into the void");
                    TCPServer.broadcastToAllClients(parts[1] + " " + parts[0] + " " + parts[2] + " " + parts[3] + "\n");
                }
            } else if (Objects.equals(parts[1], "/updateDamageTimer")) {
                TCPServer.broadcastToOtherClients(parts[1] + " " + parts[2] + " " + parts[3] + "\n", socket);
            } else if (Objects.equals(parts[1], "/updateSummonZombie")) {
                if (parts.length == 5) {
                    int idCode = World.getInstance().getEntityList().size() - 1 + World.getInstance().getEntityListExtension();
                    Zombie zombie = new Zombie(idCode, Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]));
                    World.getInstance().getEntityList().add(zombie);
                    TCPServer.broadcastToAllClients(parts[1] + " " + zombie.getIdCode() + " " + parts[2] + " " + parts[3] + " " + parts[4] + "\n");
                }
            } else if (Objects.equals(parts[1], "/updateRemoveZombie")) {
                if (parts.length == 3) {
                    if (World.getInstance() != null) {
                        for (Entity entity : World.getInstance().getEntityList()) {
                            if (entity instanceof Zombie) {
                                Zombie zombie = (Zombie) entity;
                                if (zombie.getIdCode() == Integer.parseInt(parts[2])) {
                                    World.getInstance().getEntityList().remove(entity);
                                    World.getInstance().setEntityListExtension(World.getInstance().getEntityListExtension() + 1);
                                }
                            }
                        }
                        TCPServer.broadcastToAllClients(parts[1] + " " + parts[2] + "\n");
                    }
                }
            } else if (Objects.equals(parts[1], "/updateHitZombie")) {
                if (parts.length == 5) {
                    for (Entity entity : World.getInstance().getEntityList()) {
                        if (entity.getIdCode() == Integer.parseInt(parts[2])) {
                            if (entity instanceof Zombie) {
                                Zombie zombie = (Zombie) entity;
                                if (Objects.equals(parts[3], "left")) {
                                    zombie.stopMoveX();
                                    zombie.setInParalysis(true);
                                    zombie.setParalysisTimer(30);
                                    zombie.setxSpeed(-8);
                                    zombie.setHealth(Integer.parseInt(parts[4]));
                                } else if (Objects.equals(parts[3], "right")) {
                                    zombie.stopMoveX();
                                    zombie.setInParalysis(true);
                                    zombie.setParalysisTimer(30);
                                    zombie.setxSpeed(8);
                                    zombie.setHealth(Integer.parseInt(parts[4]));
                                }
                            }
                        }
                    }
                    TCPServer.broadcastToOtherClients(parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + "\n", socket);
                }
            } else if (Objects.equals(parts[1], "/updateHealth")) {
                if (parts.length == 5) {
                    if (parts[2].equals("player")) {
                        if (World.getInstance() != null) {
                            for (Entity entity : World.getInstance().getEntityList())
                                if (entity instanceof Player) {
                                    Player player = (Player) entity;
                                    if (player.getName().equals(parts[3])) {
                                        player.setHealth(Integer.parseInt(parts[4]));
                                    }
                                }
                        }
                    } else if (parts[2].equals("other")) {
                        for (Entity entity : World.getInstance().getEntityList()) {
                            if (entity.getIdCode() == Integer.parseInt(parts[3])) {
                                if (entity instanceof Zombie) {
                                    entity.setHealth(Integer.parseInt(parts[4]));
                                }
                            }
                        }
                    }
                }
            } else if (Objects.equals(parts[1], "/updateDropItemWithXSpeed")) {
                if (parts.length == 8) {
                    if (isCanOperate(parts[0])) {
                        int idCode = World.getInstance().getEntityList().size() - 1 + World.getInstance().getEntityListExtension();
                        Item item = new Item(idCode, Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]), Integer.parseInt(parts[6]));
                        item.setxSpeed(Integer.parseInt(parts[7]));
                        World.getInstance().getEntityList().add(item);
                        TCPServer.broadcastToAllClients(parts[1] + " " + item.getIdCode() + " " + parts[2] + " " + parts[3] + " " + parts[4] + " " + parts[5] + " " + parts[6] + " " + parts[7] + "\n");
                    }
                }
            } else if (Objects.equals(parts[1], "/updatePlaceBlock")) {
                if (parts.length == 5) {
                    int x = Integer.parseInt(parts[2]);
                    int y = Integer.parseInt(parts[3]);
                    int id = Integer.parseInt(parts[4]);
                    boolean outOfProtection = true;
                    for (Area area : World.getInstance().getAreaProtectedList()) {
                        if (area.getX1() + World.getInstance().getWidth() / 2 <= x && x <= area.getX2() + World.getInstance().getWidth() / 2)
                            if (area.getY1() + World.getInstance().getHeight() / 2 <= y && y <= area.getY2() + World.getInstance().getHeight() / 2) {
                                outOfProtection = false;
                            }
                    }
                    if (outOfProtection || opPermission) {
                        if (isCanOperate(parts[0])) {
                            boolean canPlace = true;
                            for (Entity entity : World.getInstance().getEntityList()) {
                                if (entity instanceof Player) {
                                    Player player = (Player) entity;
                                    if (player.getName().equals(parts[0]) && !player.getGameMode().equals("creative")) {
                                       if(player.getItemBarAmount()[player.getItemBarChosen()] == 0 || player.getItemBarId()[player.getItemBarChosen()] == -1)
                                           canPlace = false;
                                    }
                                }
                            }
                            if(canPlace){
                                if (World.getInstance().getBlockIdList()[y][x] != id)
                                    World.getInstance().getBlockIdList()[y][x] = id;
                                TCPServer.broadcastToAllClients("/updateBlockIdListSingle " + parts[2] + " " + parts[3] + " " + parts[4] + "\n");
                                for (Entity entity : World.getInstance().getEntityList()) {
                                    if (entity instanceof Player) {
                                        Player player = (Player) entity;
                                        if (player.getName().equals(parts[0]) && !player.getGameMode().equals("creative")) {
                                            player.getItemBarAmount()[player.getItemBarChosen()]--;
                                            if (player.getItemBarAmount()[player.getItemBarChosen()] == 0) {
                                                player.getItemBarId()[player.getItemBarChosen()] = -1;
                                            }
                                            TCPServer.broadcastToSingleClient("/ItemBarAmountPlayer " + StringConversion.intArrayToString(player.getItemBarAmount()) + "\n", socket);
                                            TCPServer.broadcastToSingleClient("/ItemBarIdPlayer " + StringConversion.intArrayToString(player.getItemBarId()) + "\n", socket);
                                        }
                                    }
                                }
                            }
                        } else {
                            TCPServer.broadcastToSingleClient("/updateBlockIdListSingle" + " " + parts[2] + " " + parts[3] + " " + World.getInstance().getBlockIdList()[y][x] + "\n", socket);
                        }
                    } else {
                        TCPServer.broadcastToSingleClient("/updateBlockIdListSingle" + " " + parts[2] + " " + parts[3] + " " + World.getInstance().getBlockIdList()[y][x] + "\n", socket);
                        TCPServer.broadcastToSingleClient("/updateAreaProtected\n", socket);
                    }
                }
            }
            // 登录权限
            else if (Objects.equals(parts[1], "/register")) {
                if (parts.length == 4) {
                    boolean registered = false;
                    for (Player player : World.getInstance().getPlayerList()) {
                        if (player.getName().equals(parts[0])) {
                            if (player.getPassword() != null && !player.getPassword().equals("null"))
                                registered = true;
                        }
                    }
                    if (!registered) {
                        if (parts[2].equals(parts[3])) {
                            if (!parts[2].equals("null")) {
                                for (Entity entity : World.getInstance().getEntityList()) {
                                    if (entity instanceof Player) {
                                        Player player = (Player) entity;
                                        if (player.getName().equals(parts[0])) {
                                            player.setPassword(parts[2]);
                                            player.setHasGravity(true);
                                            TCPServer.broadcastToSingleClient("/ItemBarAmountPlayer " + StringConversion.intArrayToString(player.getItemBarAmount()) + "\n", socket);
                                            TCPServer.broadcastToSingleClient("/ItemBarIdPlayer " + StringConversion.intArrayToString(player.getItemBarId()) + "\n", socket);
                                        }
                                    }
                                }
                                TCPServer.broadcastToSingleClient("/register success\n", socket);
                                for (Client client : TCPServer.clientList) {
                                    if (client.getPlayerName().equals(parts[0])) {
                                        client.setCanOperate(true);
                                        client.getThread().interrupt();
                                    }
                                }
                                for (Entity entity1 : World.getInstance().getEntityList()) {
                                    if (entity1 instanceof Player) {
                                        Player player1 = (Player) entity1;
                                        TCPServer.broadcastToSingleClient("/updatePlayerMode " + player1.getName() + " " + player1.getGameMode() + " " + player1.isFlying() + " " + player1.isKeepInventory() + "\n", socket);
                                        TCPServer.broadcastToSingleClient("/updateItemBarChosen " + player1.getName() + " " + player1.getItemBarChosen() + "\n", socket);
                                        TCPServer.broadcastToSingleClient("/updateItemBarAmount " + player1.getName() + " " + StringConversion.intArrayToString(player1.getItemBarAmount()) + "\n", socket);
                                        TCPServer.broadcastToSingleClient("/updateItemBarId " + player1.getName() + " " + StringConversion.intArrayToString(player1.getItemBarId()) + "\n", socket);
                                    } else if (entity1 instanceof Item) {
                                        Item item1 = (Item) entity1;
                                        TCPServer.broadcastToSingleClient("/updateSummonItem " + item1.getIdCode() + " " + item1.getX() + " " + item1.getY() + " " + item1.getId() + " " + item1.getAmount() + " " + item1.getTimeNoCollect() + "\n", socket);
                                    } else if (entity1 instanceof Zombie) {
                                        Zombie zombie1 = (Zombie) entity1;
                                        TCPServer.broadcastToSingleClient("/updateSummonZombie " + zombie1.getIdCode() + " " + zombie1.getX() + " " + zombie1.getY() + " " + zombie1.getHealth() + "\n", socket);
                                    }
                                }
                                System.out.println("[" + parts[0] + "] Registered: " + parts[2]);
                            } else {
                                TCPServer.broadcastToSingleClient("/register null\n", socket);
                                System.out.println("[" + parts[0] + "] Failed usage: /register");
                            }
                        } else {
                            TCPServer.broadcastToSingleClient("/register differ\n", socket);
                            System.out.println("[" + parts[0] + "] Failed usage: /register");
                        }
                    } else {
                        TCPServer.broadcastToSingleClient("/register registered\n", socket);
                        System.out.println("[" + parts[0] + "] Failed usage: /register");
                    }
                } else {
                    TCPServer.broadcastToSingleClient("/updateUsage /register\n", socket);
                    System.out.println("[" + parts[0] + "] Failed usage: /register");
                }
            } else if (Objects.equals(parts[1], "/login")) {
                if (parts.length == 3) {
                    for (Entity entity : World.getInstance().getEntityList()) {
                        if (entity instanceof Player) {
                            Player player = (Player) entity;
                            if (player.getName().equals(parts[0])) {
                                if (player.getPassword() != null) {
                                    boolean logined = false;
                                    for (Client client : TCPServer.clientList) {
                                        if (client.getPlayerName().equals(player.getName()))
                                            logined = client.isCanOperate();
                                    }
                                    if (!logined) {
                                        if (player.getPassword().equals(parts[2])) {
                                            TCPServer.broadcastToSingleClient("/login success\n", socket);
                                            TCPServer.broadcastToSingleClient("/ItemBarAmountPlayer " + StringConversion.intArrayToString(player.getItemBarAmount()) + "\n", socket);
                                            TCPServer.broadcastToSingleClient("/ItemBarIdPlayer " + StringConversion.intArrayToString(player.getItemBarId()) + "\n", socket);
                                            player.setHasGravity(true);
                                            for (Entity entity1 : World.getInstance().getEntityList()) {
                                                if (entity1 instanceof Player) {
                                                    Player player1 = (Player) entity1;
                                                    TCPServer.broadcastToSingleClient("/updatePlayerMode " + player1.getName() + " " + player1.getGameMode() + " " + player1.isFlying() + " " + player1.isKeepInventory() + "\n", socket);
                                                    TCPServer.broadcastToSingleClient("/updateItemBarChosen " + player1.getName() + " " + player1.getItemBarChosen() + "\n", socket);
                                                    TCPServer.broadcastToSingleClient("/updateItemBarAmount " + player1.getName() + " " + StringConversion.intArrayToString(player1.getItemBarAmount()) + "\n", socket);
                                                    TCPServer.broadcastToSingleClient("/updateItemBarId " + player1.getName() + " " + StringConversion.intArrayToString(player1.getItemBarId()) + "\n", socket);
                                                } else if (entity1 instanceof Item) {
                                                    Item item1 = (Item) entity1;
                                                    TCPServer.broadcastToSingleClient("/updateSummonItem " + item1.getIdCode() + " " + item1.getX() + " " + item1.getY() + " " + item1.getId() + " " + item1.getAmount() + " " + item1.getTimeNoCollect() + "\n", socket);
                                                } else if (entity1 instanceof Zombie) {
                                                    Zombie zombie1 = (Zombie) entity1;
                                                    TCPServer.broadcastToSingleClient("/updateSummonZombie " + zombie1.getIdCode() + " " + zombie1.getX() + " " + zombie1.getY() + " " + zombie1.getHealth() + "\n", socket);
                                                }
                                            }
                                            System.out.println("[" + parts[0] + "] Logined: " + parts[2]);
                                            for (Client client : TCPServer.clientList) {
                                                if (client.getPlayerName().equals(parts[0])) {
                                                    client.setCanOperate(true);
                                                    client.getThread().interrupt();
                                                }
                                            }
                                            break;
                                        } else {
                                            TCPServer.broadcastToSingleClient("/login wrong\n", socket);
                                            System.out.println("[" + parts[0] + "] Failed usage: /login");
                                        }
                                    } else {
                                        TCPServer.broadcastToSingleClient("/login logined\n", socket);
                                        System.out.println("[" + parts[0] + "] Failed usage: /login");
                                    }
                                } else {
                                    TCPServer.broadcastToSingleClient("/login noPassword\n", socket);
                                    System.out.println("[" + parts[0] + "] Failed usage: /login");
                                }
                            }
                        }
                    }
                } else {
                    TCPServer.broadcastToSingleClient("/updateUsage /login\n", socket);
                    System.out.println("[" + parts[0] + "] Failed usage: /login");
                }
            }
            // 公共权限
            else if (isCanOperate(parts[0])) {
                if (Objects.equals(parts[1], "/help")) {
                    if (parts.length == 3) {
                        if (isIntNumber(parts[2])) {
                            if (opPermission)
                                TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + " true\n", socket);
                            else TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + " false\n", socket);
                            System.out.println("[" + parts[0] + "] Helped: " + parts[2]);
                        } else {
                            TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + "\n", socket);
                            System.out.println("[" + parts[0] + "] Failed usage: /help");
                        }
                    } else {
                        TCPServer.broadcastToSingleClient("/updateUsage /help\n", socket);
                        System.out.println("[" + parts[0] + "] Failed usage: /help");
                    }
                }
                // 管理员权限
                else if (Objects.equals(parts[1], "/time")) {
                    if (opPermission) {
                        if (parts.length == 4 && parts[2].equals("set")) {
                            if (isIntNumber(parts[3])) {
                                World.getInstance().setTime(Integer.parseInt(parts[3]));
                                TCPServer.broadcastToAllClients(parts[1] + " " + parts[2] + " " + parts[3] + "\n");
                                System.out.println("[" + parts[0] + "] Time set: " + parts[3]);
                            } else {
                                TCPServer.broadcastToAllClients(parts[1] + " " + parts[2] + " " + parts[3] + "\n");
                                System.out.println("[" + parts[0] + "] Failed usage: /time");
                            }
                        } else if (parts.length == 2) {
                            TCPServer.broadcastToSingleClient(parts[1] + "\n", socket);
                            System.out.println("[" + parts[0] + "] Time: " + World.getInstance().getTime());
                        } else {
                            TCPServer.broadcastToSingleClient("/updateUsage /time\n", socket);
                            System.out.println("[" + parts[0] + "] Failed usage: /time");
                        }
                    } else {
                        TCPServer.broadcastToSingleClient("/updateDenied /time\n", socket);
                        System.out.println("[" + parts[0] + "] Failed permission: /time");
                    }
                } else if (Objects.equals(parts[1], "/tp")) {
                    if (opPermission) {
                        if (parts.length == 5) {
                            if (isPlayerOnline(parts[2])) {
                                int xBlock = -2;
                                int yBlock = -2;
                                if (isIntNumber(parts[3]))
                                    xBlock = (Integer.parseInt(parts[3]) + (World.getInstance().getWidth() / 2));
                                if (isIntNumber(parts[4]))
                                    yBlock = (-Integer.parseInt(parts[4]) + (World.getInstance().getHeight() / 2));
                                if (xBlock == -2) {
                                    System.out.println("[" + parts[0] + "] Failed usage: /tp");
                                    TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + "\n", socket);
                                } else if (yBlock == -2) {
                                    System.out.println("[" + parts[0] + "] Failed usage: /tp");
                                    TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + "\n", socket);
                                } else {
                                    if (World.getInstance() != null)
                                        for (Entity entity : World.getInstance().getEntityList()) {
                                            if (entity instanceof Player) {
                                                Player player = (Player) entity;
                                                if (player.getName().equals(parts[2])) {
                                                    player.setX(xBlock * 50 - player.getWidth() / 2);
                                                    player.setY(yBlock * 50 - player.getHeight() / 2);
                                                }
                                            }
                                        }
                                    TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + "\n", socket);
                                    System.out.println("[" + parts[0] + "] Teleported: " + parts[2] + " " + parts[3] + " " + parts[4]);
                                }
                            } else {
                                TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + "\n", socket);
                                System.out.println("[" + parts[0] + "] Error: " + parts[2] + " player not exist or online");
                            }
                        } else if (parts.length == 4) {
                            if (isPlayerOnline(parts[2]) && isPlayerOnline(parts[3])) {
                                for (Entity entity1 : World.getInstance().getEntityList()) {
                                    if (entity1 instanceof Player) {
                                        Player player1 = (Player) entity1;
                                        if (player1.getName().equals(parts[2])) {
                                            for (Entity entity2 : World.getInstance().getEntityList()) {
                                                if (entity2 instanceof Player) {
                                                    Player player2 = (Player) entity2;
                                                    if (player2.getName().equals(parts[3])) {
                                                        player1.setX(player2.getX());
                                                        player1.setY(player2.getY());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                TCPServer.broadcastToAllClients(parts[1] + " " + parts[2] + " " + parts[3] + "\n");
                                System.out.println("[" + parts[0] + "] Teleported: " + parts[2] + " " + parts[3]);
                            } else if (!isPlayerOnline(parts[2])) {
                                TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + " " + parts[3] + "\n", socket);
                                System.out.println("[" + parts[0] + "] Error: " + parts[2] + " player not exist or online");
                            } else if (!isPlayerOnline(parts[3])) {
                                TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + " " + parts[3] + "\n", socket);
                                System.out.println("[" + parts[0] + "] Error: " + parts[3] + " player not exist or online");
                            }
                        } else {
                            TCPServer.broadcastToSingleClient("/updateUsage /tp\n", socket);
                            System.out.println("[" + parts[0] + "] Failed usage: /tp");
                        }
                    } else {
                        TCPServer.broadcastToSingleClient("/updateDenied /tp\n", socket);
                        System.out.println("[" + parts[0] + "] Failed permission: /tp");
                    }
                } else if (Objects.equals(parts[1], "/keepinventory")) {
                    if (opPermission) {
                        if (parts.length == 4) {
                            if (isPlayerOnline(parts[2])) {
                                if (isBooleanString(parts[3])) {
                                    for (Entity entity : World.getInstance().getEntityList()) {
                                        if (entity instanceof Player) {
                                            Player player = (Player) entity;
                                            if (player.getName().equals(parts[2])) {
                                                boolean bool = Boolean.parseBoolean(parts[3]);
                                                if (parts[3].equals("1")) bool = true;
                                                else if (parts[3].equals("0")) bool = false;
                                                player.setKeepInventory(bool);
                                            }
                                        }
                                    }
                                    if (parts[3].equals("1")) {
                                        TCPServer.broadcastToAllClients(parts[1] + " " + parts[2] + " " + parts[3] + "\n");
                                        System.out.println("[" + parts[0] + "] KeepInventoried: " + parts[2] + " true");
                                    } else if (parts[3].equals("0")) {
                                        TCPServer.broadcastToAllClients(parts[1] + " " + parts[2] + " " + parts[3] + "\n");
                                        System.out.println("[" + parts[0] + "] KeepInventoried: " + parts[2] + " false");
                                    } else {
                                        TCPServer.broadcastToAllClients(parts[1] + " " + parts[2] + " " + parts[3] + "\n");
                                        System.out.println("[" + parts[0] + "] KeepInventoried: " + parts[2] + " " + parts[3]);
                                    }
                                } else {
                                    TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + " " + parts[3] + "\n", socket);
                                    System.out.println("[" + parts[0] + "] Failed usage: /keepinventory");
                                }
                            } else {
                                TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + " " + parts[3] + "\n", socket);
                                System.out.println("[" + parts[0] + "] Error: " + parts[2] + " player not exist or online");
                            }
                        } else {
                            TCPServer.broadcastToSingleClient("/updateUsage /keepinventory\n", socket);
                            System.out.println("[" + parts[0] + "] Failed usage: /keepinventory");
                        }
                    } else {
                        TCPServer.broadcastToSingleClient("/updateDenied /keepinventory\n", socket);
                        System.out.println("[" + parts[0] + "] Failed permission: /keepinventory");
                    }
                } else if (Objects.equals(parts[1], "/gamemode")) {
                    if (opPermission) {
                        if (parts.length == 4) {
                            if (isPlayerOnline(parts[2])) {
                                for (Entity entity : World.getInstance().getEntityList()) {
                                    if (entity instanceof Player) {
                                        Player player = (Player) entity;
                                        if (player.getName().equals(parts[2])) {
                                            if (parts[3].equals("creative")) {
                                                player.setGameMode("creative");
                                                TCPServer.broadcastToAllClients(parts[1] + " " + parts[2] + " " + parts[3] + "\n");
                                                System.out.println("[" + parts[0] + "] Gamemoded: " + parts[2] + " " + parts[3]);
                                            } else if (parts[3].equals("survival")) {
                                                player.setGameMode("survival");
                                                TCPServer.broadcastToAllClients(parts[1] + " " + parts[2] + " " + parts[3] + "\n");
                                                System.out.println("[" + parts[0] + "] Gamemoded: " + parts[2] + " " + parts[3]);
                                            } else if (isIntNumber(parts[2]) && Integer.parseInt(parts[3]) == 1) {
                                                player.setGameMode("creative");
                                                TCPServer.broadcastToAllClients(parts[1] + " " + parts[2] + " " + parts[3] + "\n");
                                                System.out.println("[" + parts[0] + "] Gamemoded: " + parts[2] + " " + parts[3]);
                                            } else if (isIntNumber(parts[2]) && Integer.parseInt(parts[3]) == 0) {
                                                player.setGameMode("survival");
                                                TCPServer.broadcastToAllClients(parts[1] + " " + parts[2] + " " + parts[3] + "\n");
                                                System.out.println("[" + parts[0] + "] Gamemoded: " + parts[2] + " " + parts[3]);
                                            } else {
                                                TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + " " + parts[3] + "\n", socket);
                                                System.out.println("[" + parts[0] + "] Failed usage: /gamemode");
                                            }
                                        }
                                    }
                                }
                            } else {
                                TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + " " + parts[3] + "\n", socket);
                                System.out.println("[" + parts[0] + "] Failed usage: /gamemode");
                            }
                        } else {
                            TCPServer.broadcastToSingleClient("/updateUsage /gamemode\n", socket);
                            System.out.println("[" + parts[0] + "] Failed usage: /gamemode");
                        }
                    } else {
                        TCPServer.broadcastToSingleClient("/updateDenied /gamemode\n", socket);
                        System.out.println("[" + parts[0] + "] Failed permission: /gamemode");
                    }
                } else if (Objects.equals(parts[1], "/difficulty")) {
                    if (opPermission) {
                        if (parts.length == 3) {
                            if (parts[2].equals("peaceful")) {
                                World.getInstance().setDifficulty("peaceful");
                                TCPServer.broadcastToAllClients(parts[1] + " " + parts[2] + "\n");
                                System.out.println("[" + parts[0] + "] Difficultied: " + parts[2]);
                            } else if (parts[2].equals("easy")) {
                                World.getInstance().setDifficulty("easy");
                                TCPServer.broadcastToAllClients(parts[1] + " " + parts[2] + "\n");
                                System.out.println("[" + parts[0] + "] Difficultied: " + parts[2]);
                            } else if (parts[2].equals("normal")) {
                                World.getInstance().setDifficulty("normal");
                                TCPServer.broadcastToAllClients(parts[1] + " " + parts[2] + "\n");
                                System.out.println("[" + parts[0] + "] Difficultied: " + parts[2]);
                            } else if (parts[2].equals("hard")) {
                                World.getInstance().setDifficulty("hard");
                                TCPServer.broadcastToAllClients(parts[1] + " " + parts[2] + "\n");
                                System.out.println("[" + parts[0] + "] Difficultied: " + parts[2]);
                            } else {
                                System.out.println("[" + parts[0] + "] Failed usage: /difficulty");
                                TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + "\n", socket);
                            }
                        } else {
                            TCPServer.broadcastToSingleClient("/updateUsage /difficulty\n", socket);
                            System.out.println("[" + parts[0] + "] Failed usage: /difficulty");
                        }
                    } else {
                        TCPServer.broadcastToSingleClient("/updateDenied /difficulty\n", socket);
                        System.out.println("[" + parts[0] + "] Failed permission: /difficulty");
                    }
                } else if (Objects.equals(parts[1], "/save")) {
                    if (opPermission) {
                        if (parts.length == 2) {
                            WorldSave.saveWorld("world", World.getInstance());
                            TCPServer.broadcastToAllClients(parts[1] + "\n");
                            System.out.println("[" + parts[0] + "] World saved");
                        } else {
                            TCPServer.broadcastToSingleClient("/updateUsage /save\n", socket);
                            System.out.println("[" + parts[0] + "] Failed usage: /save");
                        }
                    } else {
                        TCPServer.broadcastToSingleClient("/updateDenied /save\n", socket);
                        System.out.println("[" + parts[0] + "] Failed permission: /save");
                    }
                } else if (Objects.equals(parts[1], "/stop")) {
                    if (opPermission) {
                        if (parts.length == 2) {
                            WorldSave.saveWorld("world", World.getInstance());
                            System.out.println("[/localhost] World saved");
                            TCPServer.broadcastToAllClients(parts[1] + "\n");
                            System.out.println("[/localhost] Server stopped");
                            World.getInstance().getThread().interrupt();
                            World.getInstance().setThread(null);
                            Main.serverOn = false;
                            for (Thread thread : TCPServer.threadList1) {
                                thread.interrupt();
                                TCPServer.threadList1.remove(thread);
                                thread = null;
                            }
                            TCPServer.listenerThread.interrupt();
                            TCPServer.listenerThread = null;
                            for (Client client : TCPServer.clientList) {
                                try {
                                    client.getSocket().close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            Main.consoleListenerThread.interrupt();
                            Main.consoleListenerThread = null;
                        } else {
                            TCPServer.broadcastToSingleClient("/updateUsage /stop\n", socket);
                            System.out.println("[" + parts[0] + "] Failed usage: /stop");
                        }
                    } else {
                        TCPServer.broadcastToSingleClient("/updateDenied /stop\n", socket);
                        System.out.println("[" + parts[0] + "] Failed permission: /stop");
                    }
                } else if (Objects.equals(parts[1], "/kill")) {
                    if (opPermission) {
                        if (parts.length == 3) {
                            if (isPlayerOnline(parts[2])) {
                                for (Entity entity : World.getInstance().getEntityList()) {
                                    if (entity instanceof Player) {
                                        Player player = (Player) entity;
                                        if (player.getName().equals(parts[2])) {
                                            player.setDead(true);
                                        }
                                    }
                                }
                                TCPServer.broadcastToAllClients(parts[1] + " " + parts[2] + "\n");
                                System.out.println("[" + parts[0] + "] Killed: " + parts[2]);
                            } else {
                                TCPServer.broadcastToAllClients(parts[1] + " " + parts[2] + "\n");
                                System.out.println("[" + parts[0] + "] Error: " + parts[2] + " player not exist or online");
                            }
                        } else {
                            TCPServer.broadcastToSingleClient("/updateUsage /kill\n", socket);
                            System.out.println("[" + parts[0] + "] Failed usage: /kill");
                        }
                    } else {
                        TCPServer.broadcastToSingleClient("/updateDenied /kill\n", socket);
                        System.out.println("[" + parts[0] + "] Failed permission: /kill");
                    }
                } else if (Objects.equals(parts[1], "/op")) {
                    if (opPermission) {
                        if (parts.length == 3) {
                            Boolean existed = false;
                            for (String string : World.getInstance().getOpList()) {
                                if (string.equals(parts[2])) {
                                    existed = true;
                                }
                            }
                            if (!existed) World.getInstance().getOpList().add(parts[2]);
                            TCPServer.broadcastToOtherClients(parts[1] + " " + parts[2] + "\n", socket);
                            System.out.println("[" + parts[0] + "] Oped: " + parts[2]);
                        } else {
                            TCPServer.broadcastToSingleClient("/updateUsage /op\n", socket);
                            System.out.println("[" + parts[0] + "] Failed usage: /op");
                        }
                    } else {
                        TCPServer.broadcastToSingleClient("/updateDenied /op\n", socket);
                        System.out.println("[" + parts[0] + "] Failed permission: /op");
                    }
                } else if (Objects.equals(parts[1], "/deop")) {
                    if (opPermission) {
                        if (parts.length == 3) {
                            for (String string : World.getInstance().getOpList()) {
                                if (string.equals(parts[2])) {
                                    World.getInstance().getOpList().remove(parts[2]);
                                }
                            }
                            TCPServer.broadcastToOtherClients(parts[1] + " " + parts[2] + "\n", socket);
                            System.out.println("[" + parts[0] + "] Deoped: " + parts[2]);
                        } else {
                            TCPServer.broadcastToSingleClient("/updateUsage /deop\n", socket);
                            System.out.println("[" + parts[0] + "] Failed usage: /deop");
                        }
                    } else {
                        TCPServer.broadcastToSingleClient("/updateDenied /deop\n", socket);
                        System.out.println("[" + parts[0] + "] Failed permission: /deop");
                    }
                } else if (Objects.equals(parts[1], "/setblock")) {
                    if (opPermission) {
                        if (parts.length == 5) {
                            if (World.getInstance() != null) {
                                int xBlock = -2;
                                if (isIntNumber(parts[2]))
                                    xBlock = Integer.parseInt(parts[2]) + World.getInstance().getWidth() / 2;
                                int yBlock = -2;
                                if (isIntNumber(parts[3]))
                                    yBlock = -Integer.parseInt(parts[3]) + World.getInstance().getHeight() / 2;
                                int id = -2;
                                if (xBlock == -2) {
                                    System.out.println("[" + parts[0] + "] " + parts[2] + " is not an available number");
                                    TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + "\n", socket);
                                } else if (yBlock == -2) {
                                    System.out.println("[" + parts[0] + "] " + parts[3] + " is not an available number");
                                    TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + "\n", socket);
                                } else if (Math.abs(Integer.parseInt(parts[2])) <= World.getInstance().getWidth() / 2 && Math.abs(Integer.parseInt(parts[3])) <= World.getInstance().getHeight() / 2) {
                                    if (isIntNumber(parts[4]) && IDIndex.blockIDToName(Integer.parseInt(parts[4]), "English") != null)
                                        id = Integer.parseInt(parts[4]);
                                    else if (IDIndex.blockNameToID(parts[4]) != -1)
                                        id = IDIndex.blockNameToID(parts[4]);
                                    if (id != -2) {
                                        World.getInstance().getBlockIdList()[yBlock][xBlock] = id;
                                        System.out.println("[" + parts[0] + "] Setblocked: " + parts[2] + " " + parts[3] + " " + IDIndex.blockIDToName(id, "English"));
                                        TCPServer.broadcastToAllClients(parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + "\n");
                                    } else {
                                        System.out.println("[" + parts[0] + "] Failed usage: /setblock");
                                        TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + "\n", socket);
                                    }
                                } else {
                                    System.out.println("[" + parts[0] + "] Failed usage: /setblock");
                                    TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + "\n", socket);
                                }
                            }
                        } else {
                            TCPServer.broadcastToSingleClient("/updateUsage /setblock\n", socket);
                            System.out.println("[" + parts[0] + "] Failed usage: /setblock");
                        }
                    } else {
                        TCPServer.broadcastToSingleClient("/updateDenied /setblock\n", socket);
                        System.out.println("[" + parts[0] + "] Failed permission: /setblock");
                    }
                } else if (Objects.equals(parts[1], "/give")) {
                    if (opPermission) {
                        if (parts.length == 5) {
                            if (isPlayerOnline(parts[2])) {
                                int id = -2;
                                int amount = -2;
                                if (isIntNumber(parts[3]) && !IDIndex.blockIDToName(Integer.parseInt(parts[3]), "English").equals("null"))
                                    id = Integer.parseInt(parts[3]);
                                else if (IDIndex.blockNameToID(parts[3]) != -2) id = IDIndex.blockNameToID(parts[3]);
                                if (isIntNumber(parts[4])) amount = Integer.parseInt(parts[4]);
                                if (id == -2) {
                                    TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + " " + "\n", socket);
                                    System.out.println("[" + parts[0] + "] Failed usage: /give");
                                } else if (amount == -2) {
                                    TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + " " + "\n", socket);
                                    System.out.println("[" + parts[0] + "] Failed usage: /give");
                                } else {
                                    int amountLeft = 0;
                                    for (Entity entity : World.getInstance().getEntityList()) {
                                        if (entity instanceof Player) {
                                            Player player = (Player) entity;
                                            if (player.getName().equals(parts[2])) {
                                                amountLeft = player.getItem(id, amount, 36, true);
                                            }
                                        }
                                    }
                                    TCPServer.broadcastToAllClients(parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + " " + "\n");
                                    System.out.println("[" + parts[0] + "] Given: " + parts[2] + " " + IDIndex.blockIDToName(id, "English") + " * " + (amount - amountLeft));
                                }
                            } else {
                                TCPServer.broadcastToAllClients(parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + " " + "\n");
                                System.out.println("[" + parts[0] + "] Error: " + parts[2] + " player not exist or online");
                            }
                        } else {
                            TCPServer.broadcastToSingleClient("/updateUsage /give\n", socket);
                            System.out.println("[" + parts[0] + "] Failed usage: /give");
                        }
                    } else {
                        TCPServer.broadcastToSingleClient("/updateDenied /give\n", socket);
                        System.out.println("[" + parts[0] + "] Failed permission: /give");
                    }
                } else if (Objects.equals(parts[1], "/clear")) {
                    if (parts.length == 3) {
                        if (isPlayerOnline(parts[2])) {
                            for (Entity entity : World.getInstance().getEntityList()) {
                                if (entity instanceof Player) {
                                    Player player = (Player) entity;
                                    if (player.getName().equals(parts[2])) {
                                        for (int i = 0; i < 36; i++) {
                                            player.getItemBarAmount()[i] = 0;
                                            player.getItemBarId()[i] = -1;
                                        }
                                    }
                                }
                            }
                            TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + "\n", socket);
                            System.out.println("[" + parts[0] + "] Cleared: " + parts[2]);
                        } else {
                            TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + "\n", socket);
                            System.out.println("[" + parts[0] + "] Error: " + parts[2] + " player not exist or online");
                        }
                    } else if (parts.length == 4) {
                        if (isPlayerOnline(parts[2])) {
                            int id = -2;
                            if (isIntNumber(parts[3]) && !IDIndex.blockIDToName(Integer.parseInt(parts[3]), "English").equals("null"))
                                id = Integer.parseInt(parts[3]);
                            else if (IDIndex.blockNameToID(parts[3]) != -2) id = IDIndex.blockNameToID(parts[3]);
                            if (id == -2) {
                                TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + " " + parts[3] + "\n", socket);
                                System.out.println("[" + parts[0] + "]Error: " + parts[3] + " is not an available number or name");
                            } else {
                                int amountCleared = 0;
                                for (Entity entity : World.getInstance().getEntityList()) {
                                    if (entity instanceof Player) {
                                        Player player = (Player) entity;
                                        if (player.getName().equals(parts[2])) {
                                            for (int i = 0; i < 36; i++) {
                                                if (player.getItemBarId()[i] == id) {
                                                    amountCleared += player.getItemBarAmount()[i];
                                                    player.getItemBarAmount()[i] = 0;
                                                    player.getItemBarId()[i] = -1;
                                                }
                                            }
                                        }
                                    }
                                }
                                TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + " " + parts[3] + "\n", socket);
                                System.out.println("[" + parts[0] + "] Cleared: " + parts[2] + " " + IDIndex.blockIDToName(id, "English") + " * " + amountCleared);
                            }
                        } else {
                            TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + " " + parts[3] + "\n", socket);
                            System.out.println("[" + parts[0] + "] Error: " + parts[2] + " player not exist or online");
                        }
                    } else if (parts.length == 5) {
                        if (isPlayerOnline(parts[2])) {
                            int id = -2;
                            int amount = -2;
                            if (isIntNumber(parts[3]) && !IDIndex.blockIDToName(Integer.parseInt(parts[3]), "English").equals("null"))
                                id = Integer.parseInt(parts[3]);
                            else if (IDIndex.blockNameToID(parts[3]) != -2) id = IDIndex.blockNameToID(parts[3]);
                            if (isIntNumber(parts[4])) amount = Integer.parseInt(parts[4]);
                            if (id == -2) {
                                TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + "\n", socket);
                                System.out.println("[" + parts[0] + "] Error: " + parts[3] + " is not an available number or name");
                            } else if (amount == -2) {
                                TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + "\n", socket);
                                System.out.println("[" + parts[0] + "] Error: " + parts[4] + " is not an available number");
                            } else {
                                int amountCleared = 0;
                                for (Entity entity : World.getInstance().getEntityList()) {
                                    if (entity instanceof Player) {
                                        Player player = (Player) entity;
                                        if (player.getName().equals(parts[2])) {
                                            for (int i = 0; i < 36; i++) {
                                                if (player.getItemBarId()[i] == id) {
                                                    while (player.getItemBarAmount()[i] > 0) {
                                                        if (amountCleared >= amount) break;
                                                        amountCleared += 1;
                                                        player.getItemBarAmount()[i] -= 1;
                                                    }
                                                }
                                                if (amountCleared >= amount) break;
                                            }
                                        }
                                    }
                                }
                                TCPServer.broadcastToAllClients(parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + "\n");
                                System.out.println("[" + parts[0] + "] Cleared: " + parts[1] + " " + IDIndex.blockIDToName(id, "English") + " * " + amountCleared);
                            }
                        } else {
                            TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + "\n", socket);
                            System.out.println("[" + parts[0] + "] Error: " + parts[1] + " player not exist or online");
                        }
                    } else {
                        TCPServer.broadcastToSingleClient("/updateUsage /clear\n", socket);
                        System.out.println("[" + parts[0] + "] Failed usage: /clear");
                    }
                } else if (Objects.equals(parts[1], "/summon")) {
                    if (parts.length == 5) {
                        if (IDIndex.nameToIsEntity(parts[2])) {
                            int xBlock = -2;
                            int yBlock = -2;
                            if (isIntNumber(parts[3]))
                                xBlock = (Integer.parseInt(parts[3]) + World.getInstance().getWidth() / 2);
                            if (isIntNumber(parts[4]))
                                yBlock = (-Integer.parseInt(parts[4]) + World.getInstance().getHeight() / 2);
                            if (xBlock == -2) {
                                TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + "\n", socket);
                                System.out.println("[" + parts[0] + "] Error:" + parts[3] + " is not an available number");
                            } else if (yBlock == -2) {
                                TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + "\n", socket);
                                System.out.println("[" + parts[0] + "] Error:" + parts[4] + " is not an available number");
                            } else {
                                if (World.getInstance() != null)
                                    if (parts[2].equals("zombie")) {
                                        int idCode = World.getInstance().getEntityList().size() - 1 + World.getInstance().getEntityListExtension();
                                        Zombie zombie = new Zombie(idCode, (xBlock * 50 - 20 / 2), (yBlock * 50 - 95 / 2), 20);
                                        World.getInstance().getEntityList().add(zombie);
                                        TCPServer.broadcastToAllClients("/updateSummonZombie " + zombie.getIdCode() + " " + (xBlock * 50 - 20 / 2) + " " + (yBlock * 50 - 95 / 2) + " " + 20 + "\n");
                                        System.out.println("[" + parts[0] + "] Summoned: " + parts[2] + " " + parts[3] + " " + parts[4]);
                                        TCPServer.broadcastToAllClients("/summon " + parts[2] + " " + parts[3] + " " + parts[4] + "\n");
                                    }
                            }
                        } else {
                            TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + "\n", socket);
                            System.out.println("[" + parts[0] + "] Error: " + parts[2] + " is not an available entity name");
                        }
                    } else {
                        TCPServer.broadcastToSingleClient("/updateUsage /summon\n", socket);
                        System.out.println("[" + parts[0] + "] Failed usage: /summon");
                    }
                } else if (Objects.equals(parts[1], "/gama")) {
                    if (opPermission) {
                        if (parts.length == 4 && parts[2].equals("set")) {
                            if (isDoubleNumber(parts[3])) {
                                World.getInstance().setGama(Double.parseDouble(parts[3]));
                                TCPServer.broadcastToAllClients(parts[1] + " " + parts[2] + " " + parts[3] + "\n");
                                System.out.println("[" + parts[0] + "] Gama set: " + parts[3]);
                            } else {
                                TCPServer.broadcastToAllClients(parts[1] + " " + parts[2] + " " + parts[3] + "\n");
                                System.out.println("[" + parts[0] + "] Failed usage: /gama");
                            }
                        } else if (parts.length == 2) {
                            TCPServer.broadcastToSingleClient(parts[1] + "\n", socket);
                            System.out.println("[" + parts[0] + "] Gama: " + World.getInstance().getGama());
                        } else {
                            TCPServer.broadcastToSingleClient("/updateUsage /gama\n", socket);
                            System.out.println("[" + parts[0] + "] Failed usage: /gama");
                        }
                    } else {
                        TCPServer.broadcastToSingleClient("/updateDenied /gama\n", socket);
                        System.out.println("[" + parts[0] + "] Failed permission: /gama");
                    }
                } else if (Objects.equals(parts[1], "/gravity")) {
                    if (opPermission) {
                        if (parts.length == 4 && parts[2].equals("set")) {
                            if (isDoubleNumber(parts[3])) {
                                World.getInstance().setGravity(Double.parseDouble(parts[3]));
                                TCPServer.broadcastToAllClients(parts[1] + " " + parts[2] + " " + parts[3] + "\n");
                                System.out.println("[" + parts[0] + "] Gravity set: " + parts[3]);
                            } else {
                                TCPServer.broadcastToAllClients(parts[1] + " " + parts[2] + " " + parts[3] + "\n");
                                System.out.println("[" + parts[0] + "] Failed usage: /gravity");
                            }
                        } else if (parts.length == 2) {
                            TCPServer.broadcastToSingleClient(parts[1] + "\n", socket);
                            System.out.println("[" + parts[0] + "] Gravity: " + World.getInstance().getGravity());
                        } else {
                            TCPServer.broadcastToSingleClient("/updateUsage /gravity\n", socket);
                            System.out.println("[" + parts[0] + "] Failed usage: /gravity");
                        }
                    } else {
                        TCPServer.broadcastToSingleClient("/updateDenied /gravity\n", socket);
                        System.out.println("[" + parts[0] + "] Failed permission: /gravity");
                    }
                } else if (Objects.equals(parts[1], "/resistance")) {
                    if (parts.length >= 3) {
                        if (parts[2].equals("air")) {
                            if (parts.length == 5 && parts[3].equals("set")) {
                                if (isDoubleNumber(parts[4])) {
                                    World.getInstance().setAirResistance(Double.parseDouble(parts[4]));
                                    System.out.println("[" + parts[0] + "] Air resistance set: " + parts[4]);
                                    TCPServer.broadcastToAllClients(parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + "\n");
                                } else {
                                    TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + "\n", socket);
                                    System.out.println("[" + parts[0] + "] Failed usage: /resistance");
                                }
                            } else if (parts.length == 3) {
                                double airResistance = World.getInstance().getAirResistance();
                                System.out.println("[" + parts[0] + "] Air resistance: " + airResistance);
                                TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + "\n", socket);
                            } else {
                                System.out.println("[" + parts[0] + "] Failed usage: /resistance");
                                TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + "\n", socket);
                            }
                        } else {
                            System.out.println("[" + parts[0] + "] Failed usage: /resistance");
                            TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + "\n", socket);
                        }
                    } else {
                        System.out.println("[" + parts[0] + "] Failed usage: /resistance");
                        TCPServer.broadcastToSingleClient("/updateUsage /resistance\n", socket);
                    }
                } else if (Objects.equals(parts[1], "/spawnpoint")) {
                    if (opPermission) {
                        if (parts.length == 5) {
                            if (isPlayerOnline(parts[2])) {
                                int xBlock = -2;
                                int yBlock = -2;
                                if (isIntNumber(parts[3]))
                                    xBlock = (Integer.parseInt(parts[3]) + (World.getInstance().getWidth() / 2));
                                if (isIntNumber(parts[4]))
                                    yBlock = (-Integer.parseInt(parts[4]) + (World.getInstance().getHeight() / 2));
                                if (xBlock == -2) {
                                    System.out.println("[" + parts[0] + "] Failed usage: /spawnpoint");
                                    TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + "\n", socket);
                                } else if (yBlock == -2) {
                                    System.out.println("[" + parts[0] + "] Failed usage: /spawnpoint");
                                    TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + "\n", socket);
                                } else {
                                    if (World.getInstance() != null)
                                        for (Entity entity : World.getInstance().getEntityList()) {
                                            if (entity instanceof Player) {
                                                Player player = (Player) entity;
                                                if (player.getName().equals(parts[2])) {
                                                    player.setxSpawn(xBlock * 50 - player.getWidth() / 2);
                                                    player.setySpawn(yBlock * 50 - player.getHeight() / 2);
                                                }
                                            }
                                        }
                                    TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + "\n", socket);
                                    System.out.println("[" + parts[0] + "] Spawnpointed: " + parts[2] + " " + parts[3] + " " + parts[4]);
                                }
                            } else {
                                TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4] + "\n", socket);
                                System.out.println("[" + parts[0] + "] Error: " + parts[2] + " player not exist or online");
                            }
                        } else {
                            TCPServer.broadcastToSingleClient("/updateUsage /spawnpoint\n", socket);
                            System.out.println("[" + parts[0] + "] Failed usage: /spawnpoint");
                        }
                    } else {
                        TCPServer.broadcastToSingleClient("/updateDenied /spawnpoint\n", socket);
                        System.out.println("[" + parts[0] + "] Failed permission: /spawnpoint");
                    }
                } else if (Objects.equals(parts[1], "/spawnworld")) {
                    if (opPermission) {
                        if (parts.length == 4) {
                            int xBlock = -2;
                            int yBlock = -2;
                            if (isIntNumber(parts[2]))
                                xBlock = (Integer.parseInt(parts[2]) + (World.getInstance().getWidth() / 2));
                            if (isIntNumber(parts[3]))
                                yBlock = (-Integer.parseInt(parts[3]) + (World.getInstance().getHeight() / 2));
                            if (xBlock == -2) {
                                System.out.println("[" + parts[0] + "] Failed usage: /spawnworld");
                                TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + " " + parts[3] + "\n", socket);
                            } else if (yBlock == -2) {
                                System.out.println("[" + parts[0] + "] Failed usage: /spawnworld");
                                TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + " " + parts[3] + "\n", socket);
                            } else {
                                World.getInstance().setxSpawn(xBlock * 50 - 10 / 2);
                                World.getInstance().setySpawn(yBlock * 50 - 95 / 2);
                                TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + " " + parts[3] + "\n", socket);
                                System.out.println("[" + parts[0] + "] Spawnworlded: " + parts[2] + " " + parts[3]);
                            }
                        } else {
                            TCPServer.broadcastToSingleClient("/updateUsage /spawnworld\n", socket);
                            System.out.println("[" + parts[0] + "] Failed usage: /spawnworld");
                        }
                    } else {
                        TCPServer.broadcastToSingleClient("/updateDenied /spawnworld\n", socket);
                        System.out.println("[" + parts[0] + "] Failed permission: /spawnworld");
                    }
                } else if (Objects.equals(parts[1], "/spawn")) {
                    if (parts.length == 2) {
                        for (Entity entity : World.getInstance().getEntityList()) {
                            if (entity instanceof Player) {
                                Player player = (Player) entity;
                                if (player.getName().equals(parts[0])) {
                                    player.setX(World.getInstance().getxSpawn());
                                    player.setY(World.getInstance().getySpawn());
                                }
                            }
                        }
                        TCPServer.broadcastToAllClients(parts[1] + "\n");
                        System.out.println("[" + parts[0] + "] Teleported to world spawn point");
                    } else {
                        TCPServer.broadcastToSingleClient("/updateUsage /spawn\n", socket);
                        System.out.println("[" + parts[0] + "] Failed usage: /spawn");
                    }
                } else if (Objects.equals(parts[1], "/tpa")) {
                    if (parts.length == 3) {
                        if (!parts[0].equals(parts[2])) {
                            if (isPlayerOnline(parts[2])) {
                                for (TpaRequest tpaRequest : tpaRequestList) {
                                    if (tpaRequest.getFromName().equals(parts[0])) {
                                        tpaRequestList.remove(tpaRequest);
                                        TCPServer.broadcastToAllClients("/updateTpaWithdraw " + tpaRequest.getFromName() + " " + tpaRequest.getToName() + "\n");
                                        tpaRequest.getThread().interrupt();
                                    }
                                }
                                TCPServer.broadcastToAllClients(parts[1] + " " + parts[0] + " " + parts[2] + "\n");
                                System.out.println("[" + parts[0] + "] Tpaed: " + parts[2]);
                                tpaRequestList.add(new TpaRequest(parts[0], parts[2]));
                            } else {
                                TCPServer.broadcastToSingleClient("/updateTpaNoPlayer " + parts[2] + "\n", socket);
                                System.out.println("[" + parts[0] + "] Error: " + parts[2] + " player not exist or online");
                            }
                        } else {
                            System.out.println("[" + parts[0] + "] Failed usage: /tpa");
                            TCPServer.broadcastToSingleClient("/updateTpaSelf\n", socket);
                        }
                    } else {
                        TCPServer.broadcastToSingleClient("/updateUsage /tpa\n", socket);
                        System.out.println("[" + parts[0] + "] Failed usage: /tpa");
                    }
                } else if (Objects.equals(parts[1], "/tpaccept")) {
                    if (parts.length == 2) {
                        Boolean hasRequest = false;
                        for (TpaRequest tpaRequest : tpaRequestList) {
                            if (tpaRequest.getToName() != null && tpaRequest.getToName().equals(parts[0])) {
                                hasRequest = true;
                                tpaRequestList.remove(tpaRequest);
                                tpaRequest.getThread().interrupt();
                                TCPServer.broadcastToAllClients(parts[1] + " " + tpaRequest.getFromName() + " " + parts[0] + "\n");
                                System.out.println("[" + parts[0] + "] Tpaccepted: " + tpaRequest.getFromName());
                            }
                        }
                        if (!hasRequest) {
                            TCPServer.broadcastToSingleClient("/updateTpaNoRequest\n", socket);
                        }
                    } else {
                        TCPServer.broadcastToSingleClient("/updateUsage /tpaccept\n", socket);
                        System.out.println("[" + parts[0] + "] Failed usage: /tpaccept");
                    }
                } else if (Objects.equals(parts[1], "/tpdeny")) {
                    if (parts.length == 2) {
                        Boolean hasRequest = false;
                        for (TpaRequest tpaRequest : tpaRequestList) {
                            if (tpaRequest.getToName().equals(parts[0])) {
                                hasRequest = true;
                                tpaRequestList.remove(tpaRequest);
                                tpaRequest.getThread().interrupt();
                                TCPServer.broadcastToAllClients(parts[1] + " " + tpaRequest.getFromName() + " " + parts[0] + "\n");
                                System.out.println("[" + parts[0] + "] Tpdenied " + tpaRequest.getFromName());
                            }
                        }
                        if (!hasRequest) {
                            TCPServer.broadcastToSingleClient("/updateTpaNoRequest\n", socket);
                        }
                    } else {
                        TCPServer.broadcastToSingleClient("/updateUsage /tpdeny\n", socket);
                        System.out.println("[" + parts[0] + "] Failed usage: /tpdeny");
                    }
                } else if (Objects.equals(parts[1], "/sethome")) {
                    if (parts.length == 2) {
                        for (Entity entity : World.getInstance().getEntityList()) {
                            if (entity instanceof Player) {
                                Player player = (Player) entity;
                                if (player.getName().equals(parts[0])) {
                                    player.setxHome(player.getX());
                                    player.setyHome(player.getY());
                                }
                            }
                        }
                        TCPServer.broadcastToAllClients(parts[1] + "\n");
                        System.out.println("[" + parts[0] + "] Home set successfully");
                    } else {
                        TCPServer.broadcastToSingleClient("/updateUsage /sethome\n", socket);
                        System.out.println("[" + parts[0] + "] Failed usage: /sethome");
                    }
                } else if (Objects.equals(parts[1], "/home")) {
                    if (parts.length == 2) {
                        int xHome = World.getInstance().getxSpawn();
                        int yHome = World.getInstance().getySpawn();
                        for (Entity entity : World.getInstance().getEntityList()) {
                            if (entity instanceof Player) {
                                Player player = (Player) entity;
                                if (player.getName().equals(parts[0])) {
                                    xHome = player.getxHome();
                                    yHome = player.getyHome();
                                    player.setX(xHome);
                                    player.setY(yHome);
                                }
                            }
                        }
                        TCPServer.broadcastToAllClients(parts[1] + " " + xHome + " " + yHome + "\n");
                        System.out.println("[" + parts[0] + "] Returned home");
                    } else {
                        TCPServer.broadcastToSingleClient("/updateUsage /home\n", socket);
                        System.out.println("[" + parts[0] + "] Failed usage: /home");
                    }
                } else if (Objects.equals(parts[1], "/msg")) {
                    if (parts.length == 4) {
                        if (isPlayerOnline(parts[2])) {
                            for (Client client : TCPServer.clientList) {
                                if (client.getPlayerName().equals(parts[2])) {
                                    TCPServer.broadcastToAllClients(parts[1] + " " + parts[0] + " " + parts[2] + " " + parts[3] + "\n");
                                    System.out.println("[" + parts[0] + "] Messaged: " + parts[2] + " " + parts[3]);
                                }
                            }
                        } else {
                            TCPServer.broadcastToSingleClient(parts[1] + " " + parts[0] + " " + parts[2] + " " + parts[3] + "\n", socket);
                            System.out.println("[" + parts[0] + "] Failed usage: /msg");
                        }
                    } else {
                        TCPServer.broadcastToSingleClient("/updateUsage /msg\n", socket);
                        System.out.println("[" + parts[0] + "] Failed usage: /msg");
                    }
                } else if (Objects.equals(parts[1], "/kick")) {
                    if (parts.length == 3) {
                        if (isPlayerOnline(parts[2])) {
                            for (Client client : TCPServer.clientList) {
                                if (client.getPlayerName().equals(parts[2])) {
                                    TCPServer.broadcastToAllClients(parts[1] + " " + parts[2] + "\n");
                                    System.out.println("[" + parts[0] + "] Kicked: " + parts[2]);
                                    for (Entity entity : World.getInstance().getEntityList()) {
                                        if (entity instanceof Player) {
                                            Player player = (Player) entity;
                                            if (player.getName().equals(parts[2])) {
                                                World.getInstance().getEntityList().remove(player);
                                            }
                                        }
                                    }
                                    if (client.getThread().isAlive()) client.getThread().interrupt();
                                    if (!client.getSocket().isClosed()) {
                                        try {
                                            client.getSocket().close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    TCPServer.clientList.remove(client);
                                }
                            }
                        } else {
                            TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + "\n", socket);
                            System.out.println("[" + parts[0] + "] Failed usage: /kick");
                        }
                    } else {
                        TCPServer.broadcastToSingleClient("/updateUsage /kick\n", socket);
                        System.out.println("[" + parts[0] + "] Failed usage: /kick");
                    }
                } else if (Objects.equals(parts[1], "/banip")) {
                    if (parts.length == 3) {
                        if (isPlayerOnline(parts[2])) {
                            for (Client client : TCPServer.clientList) {
                                if (client.getPlayerName().equals(parts[2])) {
                                    TCPServer.broadcastToAllClients(parts[1] + " " + parts[2] + "\n");
                                    System.out.println("[" + parts[0] + "] Baniped: " + parts[2]);
                                    World.getInstance().getBanIpList().add(String.valueOf(client.getSocket().getInetAddress()));
                                    for (Entity entity : World.getInstance().getEntityList()) {
                                        if (entity instanceof Player) {
                                            Player player = (Player) entity;
                                            if (player.getName().equals(parts[2])) {
                                                World.getInstance().getEntityList().remove(player);
                                            }
                                        }
                                    }
                                    if (client.getThread().isAlive()) client.getThread().interrupt();
                                    if (!client.getSocket().isClosed()) {
                                        try {
                                            client.getSocket().close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    TCPServer.clientList.remove(client);
                                }
                            }
                        } else {
                            TCPServer.broadcastToSingleClient(parts[1] + " " + parts[2] + "\n", socket);
                            System.out.println("[" + parts[0] + "] Failed usage: /banip");
                        }
                    } else {
                        TCPServer.broadcastToSingleClient("/updateUsage /banip\n", socket);
                        System.out.println("[" + parts[0] + "] Failed usage: /banip");
                    }
                } else if (Objects.equals(parts[1], "/ban")) {
                    if (parts.length == 3) {
                        TCPServer.broadcastToAllClients(parts[1] + " " + parts[2] + "\n");
                        System.out.println("[" + parts[0] + "] Baned: " + parts[2]);
                        World.getInstance().getBanList().add(parts[2]);
                        for (Entity entity : World.getInstance().getEntityList()) {
                            if (entity instanceof Player) {
                                Player player = (Player) entity;
                                if (player.getName().equals(parts[2])) {
                                    World.getInstance().getEntityList().remove(player);
                                }
                            }
                        }
                        for (Client client : TCPServer.clientList) {
                            if (client.getPlayerName().equals(parts[2])) {
                                if (client.getThread().isAlive()) client.getThread().interrupt();
                                if (!client.getSocket().isClosed()) {
                                    try {
                                        client.getSocket().close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                TCPServer.clientList.remove(client);
                            }
                        }
                    } else {
                        TCPServer.broadcastToSingleClient("/updateUsage /ban\n", socket);
                        System.out.println("[" + parts[0] + "] Failed usage: /ban");
                    }
                } else if (Objects.equals(parts[1], "/unban")) {
                    if (parts.length == 3) {
                        TCPServer.broadcastToAllClients(parts[1] + " " + parts[2] + "\n");
                        System.out.println("[" + parts[0] + "] Unbaned: " + parts[2]);
                        World.getInstance().getBanList().remove(parts[2]);
                    } else {
                        TCPServer.broadcastToSingleClient("/updateUsage /unban\n", socket);
                        System.out.println("[" + parts[0] + "] Failed usage: /unban");
                    }
                } else {
                    TCPServer.broadcastToSingleClient("/updateDenied /unknownCommand\n", socket);
                }
            }
        }
    }

    public static void ReceivedFromServerCommand(String command) {
        String[] parts = command.split(" ");
        if (Objects.equals(parts[0], "/help")) {
            if (parts.length == 2) {
                if (isIntNumber(parts[1])) {
                    if (Integer.parseInt(parts[1]) == 1) {
                        System.out.println("[/localhost] /help");
                        System.out.println("[/localhost] /time");
                        System.out.println("[/localhost] /keepinventory");
                        System.out.println("[/localhost] /gamemode");
                        System.out.println("[/localhost] /difficulty");
                        System.out.println("[/localhost] /kill");
                        System.out.println("[/localhost] page 1/6");
                    } else if (Integer.parseInt(parts[1]) == 2) {
                        System.out.println("[/localhost] /setblock");
                        System.out.println("[/localhost] /give");
                        System.out.println("[/localhost] /tp");
                        System.out.println("[/localhost] /op");
                        System.out.println("[/localhost] /deop");
                        System.out.println("[/localhost] /clear");
                        System.out.println("[/localhost] page 2/6");
                    } else if (Integer.parseInt(parts[1]) == 3) {
                        System.out.println("[/localhost] /summon");
                        System.out.println("[/localhost] /gama");
                        System.out.println("[/localhost] /gravity");
                        System.out.println("[/localhost] /spawnpoint");
                        System.out.println("[/localhost] /spawnworld");
                        System.out.println("[/localhost] /resistance");
                        System.out.println("[/localhost] page 3/6");
                    } else if (Integer.parseInt(parts[1]) == 4) {
                        System.out.println("[/localhost] /spawn");
                        System.out.println("[/localhost] /tpa");
                        System.out.println("[/localhost] /tpaccept");
                        System.out.println("[/localhost] /tpdeny");
                        System.out.println("[/localhost] /register");
                        System.out.println("[/localhost] /login");
                        System.out.println("[/localhost] page 4/6");
                    } else if (Integer.parseInt(parts[1]) == 5) {
                        System.out.println("[/localhost] /sethome");
                        System.out.println("[/localhost] /home");
                        System.out.println("[/localhost] /msg");
                        System.out.println("[/localhost] /kick");
                        System.out.println("[/localhost] /ban");
                        System.out.println("[/localhost] /banip");
                        System.out.println("[/localhost] page 5/6");
                    } else if (Integer.parseInt(parts[1]) == 6) {
                        System.out.println("[/localhost] /unban");
                        System.out.println("[/localhost] /say");
                        System.out.println("[/localhost] page 6/6");
                    } else {
                        System.out.println("[/localhost] page must be 1~6");
                    }
                } else {
                    System.out.println("[/localhost] " + parts[1] + " is not an available number");
                }
            } else {
                System.out.println("[/localhost] Usage: /help page");
            }
        } else if (Objects.equals(parts[0], "/time")) {
            if (parts.length == 3 && parts[1].equals("set")) {
                if (isIntNumber(parts[2])) {
                    World.getInstance().setTime(Integer.parseInt(parts[2]));
                    TCPServer.broadcastToAllClients(parts[0] + " " + parts[1] + " " + parts[2] + "\n");
                    System.out.println("[/localhost] Time set: " + parts[2]);
                } else {
                    System.out.println("[/localhost] " + parts[2] + " is not an available number");
                }
            } else if (parts.length == 1) {
                System.out.println("[/localhost] Time: " + World.getInstance().getTime());
            } else {
                System.out.println("[/localhost] Usage: /time set newTime or /time");
            }
        } else if (Objects.equals(parts[0], "/tp")) {
            if (parts.length == 4) {
                if (isPlayerOnline(parts[1])) {
                    int xBlock = -2;
                    int yBlock = -2;
                    if (isIntNumber(parts[2]))
                        xBlock = (Integer.parseInt(parts[2]) + (World.getInstance().getWidth() / 2));
                    if (isIntNumber(parts[3]))
                        yBlock = (-Integer.parseInt(parts[3]) + (World.getInstance().getHeight() / 2));
                    if (xBlock == -2) {
                        System.out.println("[/localhost] " + parts[2] + " is not an available number");
                    } else if (yBlock == -2) {
                        System.out.println("[/localhost] " + parts[3] + " is not an available number");
                    } else {
                        for (Entity entity : World.getInstance().getEntityList()) {
                            if (entity instanceof Player) {
                                Player player = (Player) entity;
                                if (player.getName().equals(parts[1])) {
                                    player.setX(xBlock * 50 - player.getWidth() / 2);
                                    player.setY(yBlock * 50 - player.getHeight() / 2);
                                }
                            }
                        }
                        TCPServer.broadcastToAllClients(parts[0] + " " + parts[1] + " " + parts[2] + " " + parts[3] + "\n");
                        System.out.println("[/localhost] Teleported: " + parts[1] + " " + parts[2] + " " + parts[3]);
                    }
                } else {
                    System.out.println("[/localhost] Error: " + parts[1] + " player not exist or online");
                }
            } else if (parts.length == 3) {
                if (isPlayerOnline(parts[1]) && isPlayerOnline(parts[2])) {
                    for (Entity entity1 : World.getInstance().getEntityList()) {
                        if (entity1 instanceof Player) {
                            Player player1 = (Player) entity1;
                            if (player1.getName().equals(parts[1])) {
                                for (Entity entity2 : World.getInstance().getEntityList()) {
                                    if (entity2 instanceof Player) {
                                        Player player2 = (Player) entity2;
                                        if (player2.getName().equals(parts[2])) {
                                            player1.setX(player2.getX());
                                            player1.setY(player2.getY());
                                        }
                                    }
                                }
                            }
                        }
                    }
                    TCPServer.broadcastToAllClients(parts[0] + " " + parts[1] + " " + parts[2] + "\n");
                    System.out.println("[/localhost] Teleported: " + parts[1] + " " + parts[2]);
                } else if (!isPlayerOnline(parts[1])) {
                    System.out.println("[/localhost] Error: " + parts[1] + " player not exist or online");
                } else if (!isPlayerOnline(parts[2])) {
                    System.out.println("[/localhost] Error: " + parts[2] + " player not exist or online");
                }
            } else {
                System.out.println("[/localhost] Usage: /tp playerName xBlock yBlock or /tp playerSent playerReceive");
            }
        } else if (Objects.equals(parts[0], "/keepinventory")) {
            if (parts.length == 3) {
                if (isPlayerOnline(parts[1])) {
                    if (isBooleanString(parts[2])) {

                        for (Entity entity : World.getInstance().getEntityList()) {
                            if (entity instanceof Player) {
                                Player player = (Player) entity;
                                if (player.getName().equals(parts[1])) {
                                    boolean bool = Boolean.parseBoolean(parts[2]);
                                    if (parts[2].equals("1")) bool = true;
                                    else if (parts[2].equals("0")) bool = false;
                                    player.setKeepInventory(bool);
                                }
                            }
                        }
                        if (parts[2].equals("1")) {
                            TCPServer.broadcastToAllClients(parts[0] + " " + parts[1] + " " + parts[2] + "\n");
                            System.out.println("[/localhost] KeepInventoried: " + parts[1] + " true");
                        } else if (parts[2].equals("0")) {
                            TCPServer.broadcastToAllClients(parts[0] + " " + parts[1] + " " + parts[2] + "\n");
                            System.out.println("[/localhost] KeepInventoried: " + parts[1] + " false");
                        } else {
                            TCPServer.broadcastToAllClients(parts[0] + " " + parts[1] + " " + parts[2] + "\n");
                            System.out.println("[/localhost] KeepInventoried: " + parts[1] + " " + parts[2]);
                        }

                    } else {
                        System.out.println("[/localhost] " + parts[2] + " is not an available boolean");
                    }
                } else {
                    System.out.println("[/localhost] Error: " + parts[1] + " player not exist or online");
                }
            } else {
                System.out.println("[/localhost] Usage: /keepinventory playerName boolean");
            }
        } else if (Objects.equals(parts[0], "/gamemode")) {
            if (parts.length == 3) {
                if (isPlayerOnline(parts[1])) {
                    for (Entity entity : World.getInstance().getEntityList()) {
                        if (entity instanceof Player) {
                            Player player = (Player) entity;
                            if (player.getName().equals(parts[1])) {
                                if (parts[2].equals("creative")) {
                                    player.setGameMode("creative");
                                    System.out.println("[/localhost] Gamemoded: " + parts[1] + " " + "creative");
                                    TCPServer.broadcastToAllClients("/gamemode " + parts[1] + " " + parts[2] + "\n");
                                } else if (parts[2].equals("survival")) {
                                    player.setGameMode("survival");
                                    System.out.println("[/localhost] Gamemoded: " + parts[1] + " " + "survival");
                                    TCPServer.broadcastToAllClients("/gamemode " + parts[1] + " " + parts[2] + "\n");
                                } else if (isIntNumber(parts[2]) && Integer.parseInt(parts[2]) == 1) {
                                    player.setGameMode("creative");
                                    System.out.println("[/localhost] Gamemoded: " + parts[1] + " " + "creative");
                                    TCPServer.broadcastToAllClients("/gamemode " + parts[1] + " " + parts[2] + "\n");
                                } else if (isIntNumber(parts[2]) && Integer.parseInt(parts[2]) == 0) {
                                    player.setGameMode("survival");
                                    System.out.println("[/localhost] Gamemoded: " + parts[1] + " " + "survival");
                                    TCPServer.broadcastToAllClients("/gamemode " + parts[1] + " " + parts[2] + "\n");
                                } else {
                                    System.out.println("[/localhost] " + parts[2] + " is not available number or mode");
                                }
                            }
                        }
                    }
                } else {
                    System.out.println("[/localhost] Error: " + parts[1] + " player not exist or online");
                }
            } else {
                System.out.println("[/localhost] Usage: /gamemode playerName mode");
            }
        } else if (Objects.equals(parts[0], "/difficulty")) {
            if (parts.length == 2) {
                if (parts[1].equals("peaceful")) {
                    World.getInstance().setDifficulty("peaceful");
                    TCPServer.broadcastToAllClients(parts[0] + " " + parts[1] + "\n");
                    System.out.println("[/localhost] Difficultied: " + parts[1]);
                } else if (parts[1].equals("easy")) {
                    World.getInstance().setDifficulty("easy");
                    TCPServer.broadcastToAllClients(parts[0] + " " + parts[1] + "\n");
                    System.out.println("[/localhost] Difficultied: " + parts[1]);
                } else if (parts[1].equals("normal")) {
                    World.getInstance().setDifficulty("normal");
                    TCPServer.broadcastToAllClients(parts[0] + " " + parts[1] + "\n");
                    System.out.println("[/localhost] Difficultied: " + parts[1]);
                } else if (parts[1].equals("hard")) {
                    World.getInstance().setDifficulty("hard");
                    TCPServer.broadcastToAllClients(parts[0] + " " + parts[1] + "\n");
                    System.out.println("[/localhost] Difficultied: " + parts[1]);
                } else {
                    System.out.println("[/localhost] " + parts[1] + " is not available difficulty level");
                }
            } else {
                System.out.println("[/localhost] Usage: /difficulty level");
            }
        } else if (Objects.equals(parts[0], "/save")) {
            if (parts.length == 1) {
                WorldSave.saveWorld("world", World.getInstance());
                TCPServer.broadcastToAllClients(parts[0] + "\n");
                System.out.println("[/localhost] World saved");
            } else {
                System.out.println("[/localhost] Usage: /save");
            }
        } else if (Objects.equals(parts[0], "/stop")) {
            if (parts.length == 1) {
                WorldSave.saveWorld("world", World.getInstance());
                System.out.println("[/localhost] World saved");
                TCPServer.broadcastToAllClients(parts[0] + "\n");
                System.out.println("[/localhost] Server stopped");
                World.getInstance().getThread().interrupt();
                World.getInstance().setThread(null);
                Main.serverOn = false;
                for (Thread thread : TCPServer.threadList1) {
                    thread.interrupt();
                    TCPServer.threadList1.remove(thread);
                    thread = null;
                }
                TCPServer.listenerThread.interrupt();
                TCPServer.listenerThread = null;
                for (Client client : TCPServer.clientList) {
                    try {
                        client.getSocket().close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Main.consoleListenerThread.interrupt();
                Main.consoleListenerThread = null;
            } else {
                System.out.println("[/localhost] Usage: /stop");
            }
        } else if (Objects.equals(parts[0], "/kill")) {
            if (parts.length == 2) {
                if (isPlayerOnline(parts[1])) {
                    for (Entity entity : World.getInstance().getEntityList()) {
                        if (entity instanceof Player) {
                            Player player = (Player) entity;
                            if (player.getName().equals(parts[1])) {
                                player.setDead(true);
                            }
                        }
                    }
                    System.out.println("[/localhost] Killed: " + parts[1]);
                    TCPServer.broadcastToAllClients(parts[0] + " " + parts[1] + "\n");
                } else {
                    System.out.println("[/localhost] Error: " + parts[1] + " player not exist or online");
                }
            } else {
                System.out.println("[/localhost] Usage: /kill playerName");
            }
        } else if (Objects.equals(parts[0], "/op")) {
            if (parts.length == 2) {
                Boolean existed = false;
                for (String string : World.getInstance().getOpList()) {
                    if (string.equals(parts[1])) {
                        existed = true;
                    }
                }
                if (!existed) World.getInstance().getOpList().add(parts[1]);
                System.out.println("[/localhost] Oped: " + parts[1]);
                TCPServer.broadcastToAllClients(parts[0] + " " + parts[1] + "\n");
            } else {
                System.out.println("[/localhost] Usage: /op playerName");
            }
        } else if (Objects.equals(parts[0], "/deop")) {
            if (parts.length == 2) {
                for (String string : World.getInstance().getOpList()) {
                    if (string.equals(parts[1])) {
                        World.getInstance().getOpList().remove(parts[1]);
                    }
                }
                System.out.println("[/localhost] Deoped: " + parts[1]);
                TCPServer.broadcastToAllClients(parts[0] + " " + parts[1] + "\n");
            } else {
                System.out.println("[/localhost] Usage: /deop playerName");
            }
        } else if (Objects.equals(parts[0], "/setblock")) {
            if (parts.length == 4) {
                if (World.getInstance() != null) {
                    int xBlock = -2;
                    if (isIntNumber(parts[1])) xBlock = Integer.parseInt(parts[1]) + World.getInstance().getWidth() / 2;
                    int yBlock = -2;
                    if (isIntNumber(parts[2]))
                        yBlock = -Integer.parseInt(parts[2]) + World.getInstance().getHeight() / 2;
                    int id = -2;
                    if (xBlock == -2) {
                        System.out.println("[/localhost] " + parts[1] + " is not an available number");
                    } else if (yBlock == -2) {
                        System.out.println("[/localhost] " + parts[2] + " is not an available number");
                    } else if (Math.abs(Integer.parseInt(parts[1])) <= World.getInstance().getWidth() / 2 && Math.abs(Integer.parseInt(parts[2])) <= World.getInstance().getHeight() / 2) {
                        if (isIntNumber(parts[3]) && IDIndex.blockIDToName(Integer.parseInt(parts[3]), "English") != null)
                            id = Integer.parseInt(parts[3]);
                        else if (IDIndex.blockNameToID(parts[3]) != -1)
                            id = IDIndex.blockNameToID(parts[3]);
                        if (id != -2) {
                            World.getInstance().getBlockIdList()[yBlock][xBlock] = id;
                            System.out.println("[/localhost] Setblocked: " + parts[1] + " " + parts[2] + " " + parts[3]);
                            TCPServer.broadcastToAllClients("/setblock " + parts[1] + " " + parts[2] + " " + parts[3] + "\n");
                        } else {
                            System.out.println("[/localhost] " + parts[3] + " is not an available number or name");
                        }
                    } else {
                        System.out.println("[/localhost] Location is out of the world's size");
                    }
                }
            } else {
                System.out.println("[/localhost] Usage: /setblock xBlock yBlock blockId");
            }
        } else if (Objects.equals(parts[0], "/give")) {
            if (parts.length == 4) {
                if (isPlayerOnline(parts[1])) {
                    int id = -2;
                    int amount = -2;
                    if (isIntNumber(parts[2]) && !IDIndex.blockIDToName(Integer.parseInt(parts[2]), "English").equals("null"))
                        id = Integer.parseInt(parts[2]);
                    else if (IDIndex.blockNameToID(parts[2]) != -2) id = IDIndex.blockNameToID(parts[2]);
                    if (isIntNumber(parts[3])) amount = Integer.parseInt(parts[3]);
                    if (id == -2) {
                        System.out.println("[/localhost] Error: " + parts[2] + " is not an available number or name");
                    } else if (amount == -2) {
                        System.out.println("[/localhost] Error: " + parts[3] + " is not an available number");
                    } else {
                        int amountLeft = 0;
                        for (Entity entity : World.getInstance().getEntityList()) {
                            if (entity instanceof Player) {
                                Player player = (Player) entity;
                                if (player.getName().equals(parts[1])) {
                                    amountLeft = player.getItem(id, amount, 36, true);
                                }
                            }
                        }
                        TCPServer.broadcastToAllClients(parts[0] + " " + parts[1] + " " + parts[2] + " " + parts[3] + "\n");
                        System.out.println("[/localhost] Given: " + parts[1] + " " + IDIndex.blockIDToName(id, "English") + " * " + (amount - amountLeft));
                    }
                } else {
                    System.out.println("[/localhost] Error: " + parts[1] + " player not exist or online");
                }
            } else {
                System.out.println("[/localhost] Usage: /give playerName itemId / Name amount");
            }
        } else if (Objects.equals(parts[0], "/clear")) {
            if (parts.length == 2) {
                if (isPlayerOnline(parts[1])) {
                    for (Entity entity : World.getInstance().getEntityList()) {
                        if (entity instanceof Player) {
                            Player player = (Player) entity;
                            if (player.getName().equals(parts[1])) {
                                for (int i = 0; i < 36; i++) {
                                    player.getItemBarAmount()[i] = 0;
                                    player.getItemBarId()[i] = -1;
                                }
                            }
                        }
                    }
                    TCPServer.broadcastToAllClients(parts[0] + " " + parts[1] + "\n");
                    System.out.println("[/localhost] Cleared: " + parts[1]);
                } else {
                    System.out.println("[/localhost] Error: " + parts[1] + " player not exist or online");
                }
            } else if (parts.length == 3) {
                if (isPlayerOnline(parts[1])) {
                    int id = -2;
                    if (isIntNumber(parts[2]) && !IDIndex.blockIDToName(Integer.parseInt(parts[2]), "English").equals("null"))
                        id = Integer.parseInt(parts[2]);
                    else if (IDIndex.blockNameToID(parts[2]) != -2) id = IDIndex.blockNameToID(parts[2]);
                    if (id == -2) {
                        System.out.println("[/localhost] Error: " + parts[2] + " is not an available number or name");
                    } else {
                        int amountCleared = 0;
                        for (Entity entity : World.getInstance().getEntityList()) {
                            if (entity instanceof Player) {
                                Player player = (Player) entity;
                                if (player.getName().equals(parts[1])) {
                                    for (int i = 0; i < 36; i++) {
                                        if (player.getItemBarId()[i] == id) {
                                            amountCleared += player.getItemBarAmount()[i];
                                            player.getItemBarAmount()[i] = 0;
                                            player.getItemBarId()[i] = -1;
                                        }
                                    }
                                }
                            }
                        }
                        TCPServer.broadcastToAllClients(parts[0] + " " + parts[1] + " " + parts[2] + "\n");
                        System.out.println("[/localhost] Cleared: " + parts[1] + " " + IDIndex.blockIDToName(id, "English") + " * " + amountCleared);
                    }
                } else {
                    System.out.println("[/localhost] Error: " + parts[1] + " player not exist or online");
                }
            } else if (parts.length == 4) {
                if (isPlayerOnline(parts[1])) {
                    int id = -2;
                    int amount = -2;
                    if (isIntNumber(parts[2]) && !IDIndex.blockIDToName(Integer.parseInt(parts[2]), "English").equals("null"))
                        id = Integer.parseInt(parts[2]);
                    else if (IDIndex.blockNameToID(parts[2]) != -2) id = IDIndex.blockNameToID(parts[2]);
                    if (isIntNumber(parts[3])) amount = Integer.parseInt(parts[3]);
                    if (id == -2) {
                        System.out.println("[/localhost] Error: " + parts[2] + " is not an available number or name");
                    } else if (amount == -2) {
                        System.out.println("[/localhost] Error: " + parts[3] + " is not an available number");
                    } else {
                        int amountCleared = 0;
                        for (Entity entity : World.getInstance().getEntityList()) {
                            if (entity instanceof Player) {
                                Player player = (Player) entity;
                                if (player.getName().equals(parts[1])) {
                                    for (int i = 0; i < 36; i++) {
                                        if (player.getItemBarId()[i] == id) {
                                            while (player.getItemBarAmount()[i] > 0) {
                                                if (amountCleared >= amount) break;
                                                amountCleared += 1;
                                                player.getItemBarAmount()[i] -= 1;
                                            }
                                        }
                                        if (amountCleared >= amount) break;
                                    }
                                }
                            }
                        }
                        TCPServer.broadcastToAllClients(parts[0] + " " + parts[1] + " " + parts[2] + " " + parts[3] + "\n");
                        System.out.println("[/localhost] Cleared: " + parts[1] + " " + IDIndex.blockIDToName(id, "English") + " * " + amountCleared);
                    }
                } else {
                    System.out.println("[/localhost] Error: " + parts[1] + " player not exist or online");
                }
            } else {
                System.out.println("[/localhost] Usage: /clear playerName itemId / Name amount");
            }
        } else if (Objects.equals(parts[0], "/summon")) {
            if (parts.length == 4) {
                if (IDIndex.nameToIsEntity(parts[1])) {
                    int xBlock = -2;
                    int yBlock = -2;
                    if (isIntNumber(parts[2]))
                        xBlock = (Integer.parseInt(parts[2]) + World.getInstance().getWidth() / 2);
                    if (isIntNumber(parts[3]))
                        yBlock = (-Integer.parseInt(parts[3]) + World.getInstance().getHeight() / 2);
                    if (xBlock == -2) {
                        System.out.println("[/localhost] Error: " + parts[2] + " is not an available number");
                    } else if (yBlock == -2) {
                        System.out.println("[/localhost] Error: " + parts[3] + " is not an available number");
                    } else {
                        if (World.getInstance() != null)
                            if (parts[1].equals("zombie")) {
                                int idCode = World.getInstance().getEntityList().size() - 1 + World.getInstance().getEntityListExtension();
                                Zombie zombie = new Zombie(idCode, (xBlock * 50 - 20 / 2), (yBlock * 50 - 95 / 2), 20);
                                World.getInstance().getEntityList().add(zombie);
                                TCPServer.broadcastToAllClients("/updateSummonZombie " + zombie.getIdCode() + " " + (xBlock * 50 - 20 / 2) + " " + (yBlock * 50 - 95 / 2) + " " + 20 + "\n");
                                System.out.println("[/localhost] Summoned: " + parts[1] + " " + parts[2] + " " + parts[3]);
                                TCPServer.broadcastToAllClients("/summon " + parts[1] + " " + parts[2] + " " + parts[3] + "\n");
                            }
                    }
                } else {
                    System.out.println("[/localhost] Error: " + parts[1] + " is not an available entity name");
                }
            } else {
                System.out.println("[/localhost] Usage: /summon entityName xBlock yBlock");
            }
        } else if (Objects.equals(parts[0], "/gama")) {
            if (parts.length == 3 && parts[1].equals("set")) {
                if (isDoubleNumber(parts[2])) {
                    World.getInstance().setGama(Double.parseDouble(parts[2]));
                    TCPServer.broadcastToAllClients(parts[0] + " " + parts[1] + " " + parts[2] + "\n");
                    System.out.println("[/localhost] Gama set: " + parts[2]);
                } else {
                    System.out.println("[/localhost] " + parts[2] + " is not an available number");
                }
            } else if (parts.length == 1) {
                System.out.println("[/localhost] Gama: " + World.getInstance().getGama());
            } else {
                System.out.println("[/localhost] Usage: /gama set newGama or /gama");
            }
        } else if (Objects.equals(parts[0], "/gravity")) {
            if (parts.length == 3 && parts[1].equals("set")) {
                if (isDoubleNumber(parts[2])) {
                    World.getInstance().setGravity(Double.parseDouble(parts[2]));
                    TCPServer.broadcastToAllClients(parts[0] + " " + parts[1] + " " + parts[2] + "\n");
                    System.out.println("[/localhost] Gravity set: " + parts[2]);
                } else {
                    System.out.println("[/localhost] " + parts[2] + " is not an available number");
                }
            } else if (parts.length == 1) {
                System.out.println("[/localhost] Gravity: " + World.getInstance().getGravity());
            } else {
                System.out.println("[/localhost] Usage: /gravity set newGravity or /gravity");
            }
        } else if (Objects.equals(parts[0], "/resistance")) {
            if (parts.length >= 2) {
                if (parts[1].equals("air")) {
                    if (parts.length == 4 && parts[2].equals("set")) {
                        if (isDoubleNumber(parts[3])) {
                            World.getInstance().setAirResistance(Double.parseDouble(parts[3]));
                            System.out.println("[/localhost] Air resistance set: " + parts[3]);
                            TCPServer.broadcastToAllClients(parts[0] + " " + parts[1] + " " + parts[2] + " " + parts[3] + "\n");
                        } else {
                            System.out.println("[/localhost] Error: " + parts[3] + " is not an available number");
                        }
                    } else if (parts.length == 2) {
                        double airResistance = World.getInstance().getAirResistance();
                        System.out.println("[/localhost] Air resistance: " + airResistance);
                    } else {
                        System.out.println("[/localhost] Usage: /resistance object set value or /gravity object");
                    }
                } else {
                    System.out.println("[/localhost] Error: " + parts[1] + " is not an available object");
                }
            } else {
                System.out.println("[/localhost] Usage: /resistance object set value or /gravity object");
            }
        } else if (Objects.equals(parts[0], "/spawnpoint")) {
            if (parts.length == 4) {
                if (isPlayerOnline(parts[1])) {
                    int xBlock = -2;
                    int yBlock = -2;
                    if (isIntNumber(parts[2]))
                        xBlock = (Integer.parseInt(parts[2]) + (World.getInstance().getWidth() / 2));
                    if (isIntNumber(parts[3]))
                        yBlock = (-Integer.parseInt(parts[3]) + (World.getInstance().getHeight() / 2));
                    if (xBlock == -2) {
                        System.out.println("[/localhost] " + parts[2] + " is not an available number");
                    } else if (yBlock == -2) {
                        System.out.println("[/localhost] " + parts[3] + " is not an available number");
                    } else {
                        for (Entity entity : World.getInstance().getEntityList()) {
                            if (entity instanceof Player) {
                                Player player = (Player) entity;
                                if (player.getName().equals(parts[1])) {
                                    player.setxSpawn(xBlock * 50 - player.getWidth() / 2);
                                    player.setySpawn(yBlock * 50 - player.getHeight() / 2);
                                }
                            }
                        }
                        TCPServer.broadcastToAllClients(parts[0] + " " + parts[1] + " " + parts[2] + " " + parts[3] + "\n");
                        System.out.println("[/localhost] Spawnpointed: " + parts[1] + " " + parts[2] + " " + parts[3]);
                    }
                } else {
                    System.out.println("[/localhost] Error: " + parts[1] + " player not exist or online");
                }
            } else {
                System.out.println("[/localhost] Usage: /spawnpoint playerName xBlock yBlock");
            }
        } else if (Objects.equals(parts[0], "/spawnworld")) {
            if (parts.length == 3) {
                int xBlock = -2;
                int yBlock = -2;
                if (isIntNumber(parts[1]))
                    xBlock = (Integer.parseInt(parts[1]) + (World.getInstance().getWidth() / 2));
                if (isIntNumber(parts[2]))
                    yBlock = (-Integer.parseInt(parts[2]) + (World.getInstance().getHeight() / 2));
                if (xBlock == -2) {
                    System.out.println("[/localhost] " + parts[1] + " is not an available number");
                } else if (yBlock == -2) {
                    System.out.println("[/localhost] " + parts[2] + " is not an available number");
                } else {
                    World.getInstance().setxSpawn(xBlock * 50 - 10 / 2);
                    World.getInstance().setySpawn(yBlock * 50 - 95 / 2);
                    TCPServer.broadcastToAllClients(parts[0] + " " + parts[1] + " " + parts[2] + " " + parts[3] + "\n");
                    System.out.println("[/localhost] Spawnworlded: " + parts[1] + " " + parts[2] + " " + parts[3]);
                }

            } else {
                System.out.println("[/localhost] Usage: /spawnworld xBlock yBlock");
            }
        } else if (Objects.equals(parts[0], "/say")) {
            if (parts.length == 2) {
                TCPServer.broadcastToAllClients("/updateText " + parts[1] + "\n");
                System.out.println("[/localhost] Said: " + parts[1]);
            } else {
                System.out.println("[/localhost] Usage: /say sentence");
            }
        } else if (Objects.equals(parts[0], "/kick")) {
            if (parts.length == 2) {
                if (isPlayerOnline(parts[1])) {
                    for (Client client : TCPServer.clientList) {
                        if (client.getPlayerName().equals(parts[1])) {
                            TCPServer.broadcastToAllClients(parts[0] + " " + parts[1] + "\n");
                            System.out.println("[" + parts[0] + "] Kicked: " + parts[1]);
                            for (Entity entity : World.getInstance().getEntityList()) {
                                if (entity instanceof Player) {
                                    Player player = (Player) entity;
                                    if (player.getName().equals(parts[1])) {
                                        World.getInstance().getEntityList().remove(player);
                                    }
                                }
                            }
                            if (client.getThread().isAlive()) client.getThread().interrupt();
                            if (!client.getSocket().isClosed()) {
                                try {
                                    client.getSocket().close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            TCPServer.clientList.remove(client);
                        }
                    }
                } else {
                    System.out.println("[/localhost] Error: " + parts[1] + " player not exist or online");
                    System.out.println("[" + parts[0] + "] Failed usage: /kick");
                }
            } else {
                System.out.println("[/localhost] Usage: /kick playerName");
            }
        } else if (Objects.equals(parts[0], "/banip")) {
            if (parts.length == 2) {
                if (isPlayerOnline(parts[1])) {
                    for (Client client : TCPServer.clientList) {
                        if (client.getPlayerName().equals(parts[1])) {
                            TCPServer.broadcastToAllClients(parts[0] + " " + parts[1] + "\n");
                            System.out.println("[" + parts[0] + "] Baniped: " + parts[1]);
                            World.getInstance().getBanIpList().add(String.valueOf(client.getSocket().getInetAddress()));
                            for (Entity entity : World.getInstance().getEntityList()) {
                                if (entity instanceof Player) {
                                    Player player = (Player) entity;
                                    if (player.getName().equals(parts[1])) {
                                        World.getInstance().getEntityList().remove(player);
                                    }
                                }
                            }
                            if (client.getThread().isAlive()) client.getThread().interrupt();
                            if (!client.getSocket().isClosed()) {
                                try {
                                    client.getSocket().close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            TCPServer.clientList.remove(client);
                        }
                    }
                } else {
                    System.out.println("[/localhost] Error: " + parts[1] + " player not exist or online");
                    System.out.println("[" + parts[0] + "] Failed usage: /banip");
                }
            } else {
                System.out.println("[/localhost] Usage: /banip playerName");
            }
        } else if (Objects.equals(parts[0], "/ban")) {
            if (parts.length == 2) {
                TCPServer.broadcastToAllClients(parts[0] + " " + parts[1] + "\n");
                System.out.println("[" + parts[0] + "] Banned: " + parts[1]);
                World.getInstance().getBanList().add(parts[1]);
                for (Entity entity : World.getInstance().getEntityList()) {
                    if (entity instanceof Player) {
                        Player player = (Player) entity;
                        if (player.getName().equals(parts[1])) {
                            World.getInstance().getEntityList().remove(player);
                        }
                    }
                }
                for (Client client : TCPServer.clientList) {
                    if (client.getPlayerName().equals(parts[1])) {
                        if (client.getThread().isAlive()) client.getThread().interrupt();
                        if (!client.getSocket().isClosed()) {
                            try {
                                client.getSocket().close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        TCPServer.clientList.remove(client);
                    }
                }
            } else {
                System.out.println("[/localhost] Usage: /ban playerName");
            }
        } else if (Objects.equals(parts[0], "/unban")) {
            if (parts.length == 2) {
                TCPServer.broadcastToAllClients(parts[0] + " " + parts[1] + "\n");
                System.out.println("[/localhost] Unbaned: " + parts[1]);
                World.getInstance().getBanList().remove(parts[1]);
            } else {
                System.out.println("[/localhost] Failed usage: /unban");
            }
        } else {
            System.out.println("[/localhost] Error: Unknown command");
        }
    }

    public static boolean isIntNumber(String string) {
        int intValue;
        if (string == null || string.equals("")) {
            return false;
        }

        try {
            intValue = Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
        }
        return false;
    }

    public static boolean isDoubleNumber(String string) {
        double value;
        if (string == null || string.equals("")) {
            return false;
        }
        try {
            value = Double.parseDouble(string);
            return true;
        } catch (NumberFormatException e) {
        }
        return false;
    }

    public static boolean isBooleanString(String string) {
        if (string.equals("true") || string.equals(("True")))
            return true;
        else if (string.equals("false") || string.equals(("False")))
            return true;
        else if (string.equals("1"))
            return true;
        else if (string.equals("0"))
            return true;
        return false;
    }

    public static boolean isPlayerOnline(String name) {
        for (Entity entity : World.getInstance().getEntityList()) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                if (player.getName().equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isCanOperate(String playerName) {
        for (Client client : TCPServer.clientList) {
            if (client.getPlayerName().equals(playerName)) {
                return client.isCanOperate();
            }
        }
        return false;
    }

    public static boolean isPlayerHasPassword(String playerName) {
        for (Player player : World.getInstance().getPlayerList()) {
            if (player.getName().equals(playerName)) {
                if (player.getPassword() != null && !Objects.equals(player.getPassword(), "null")) {
                    return true;
                }
            }
        }
        return false;
    }
}
