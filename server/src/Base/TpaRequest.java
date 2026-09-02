package Base;

public class TpaRequest implements Runnable {
    private String fromName;
    private String toName;
    private Thread thread = new Thread(this);
    private int timer = 0;

    public TpaRequest(String fromName, String toName) {
        this.fromName = fromName;
        this.toName = toName;
        thread.start();
    }

    @Override
    public void run() {
        while (true) {
            if (timer >= 30) {
                TCPServer.broadcastToAllClients("/updateTpaTimeOut " + fromName + " " + toName + "\n");
                Command.tpaRequestList.remove(this);
                thread.interrupt();
            }
            timer++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }
}
