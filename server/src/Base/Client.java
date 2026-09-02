package Base;

import java.net.Socket;

public class Client implements Runnable {
    private Socket socket;
    private String playerName;
    // 定义玩家是否可以操作
    private boolean canOperate = false;
    private Thread thread = new Thread(this);
    private int timer = 0;
    private boolean hasPassword;

    @Override
    public void run() {
        this.hasPassword = Command.isPlayerHasPassword(playerName);
        while (true) {
            if (socket.isClosed()) thread.interrupt();
            if (timer >= 50) {
                if (!socket.isClosed())
                    TCPServer.broadcastToSingleClient("/updateLoginTimeOut\n", socket);
                thread.interrupt();
            }
            if (timer % 5 == 0) {
                if (!socket.isClosed())
                    if (hasPassword) TCPServer.broadcastToSingleClient("/updateLoginInfo\n", socket);
                    else TCPServer.broadcastToSingleClient("/updateRegisterInfo\n", socket);
            }
            timer++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public Client(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setCanOperate(boolean canOperate) {
        this.canOperate = canOperate;
    }

    public boolean isCanOperate() {
        return canOperate;
    }
}
