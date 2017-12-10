import java.io.*;
import java.net.Socket;
import java.util.List;

interface ReaderListener {
    void onTurnChange(String message);

    void onUsers(String message);

    void onDraw(String message);

    void onSuccess(String message);

    void onFail(String message);

    void onDisconnect(String message);

    void onRefresh(String message);

}

class ReaderThread extends Thread {
    private InputStream is;
    private DataInputStream dis;
    private ReaderListener listener;

    void setReaderListener(ReaderListener readerListener) {
        this.listener = readerListener;
    }

    public ReaderThread(InputStream is, ReaderListener listener) {
        this.is = is;
        this.dis = new DataInputStream(is);
        this.listener = listener;
    }

    public static void readn(DataInputStream is, byte[] data, int size)
            throws IOException {
        int left = size;
        int offset = 0;

        while (left > 0) {
            int len = is.read(data, offset, left);
            left -= len;
            offset += len;
        }
    }

    @Override
    public void run() {
        byte[] data = new byte[1024];
        try {
            while (true) {

//                int len = is.read(data);
//                if (len == -1)
//                    break;

                int packetLen = dis.readInt();
                readn(dis, data, packetLen);
                String message = new String(data, 0, packetLen);
                System.out.println(message);

                // create sea 8000
                listener.onTurnChange(message);
                listener.onUsers(message);
                listener.onDraw(message);
                listener.onSuccess(message);
                listener.onFail(message);
                listener.onDisconnect(message);
                listener.onRefresh(message);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class Client {
    private Socket socket;
    private OutputStream os;
    private DataOutputStream dos;
    private ReaderThread readerThread;

    public void connect(ReaderListener listener) throws IOException {
        this.socket = new Socket("192.168.11.12", 8080);
        this.os = socket.getOutputStream();
        this.dos = new DataOutputStream(os);

        this.readerThread = new ReaderThread(socket.getInputStream(), listener);
        readerThread.start();
    }

    public void setUserName(String name) throws IOException {
        String message = "CONNECT#" + name;
        byte[] setMsg = message.getBytes();
        dos.writeInt(setMsg.length);
        dos.write(setMsg);
    }

    public void setAnswer(String keyword) throws IOException {
        String message = "ANSWER#" +"Hanna Babo#"+ keyword;
        byte[] setMsg = message.getBytes();
        dos.writeInt(setMsg.length);
        dos.write(setMsg);
    }

    public void setDisconnect(String userId) throws IOException {
        String message = "DISCONNECT#" + userId;
        byte[] setMsg = message.getBytes();
        dos.writeInt(setMsg.length);
        dos.write(setMsg);
    }

    public void setDraw(String userId, int x, int y) throws IOException {
        String message = "DRAW#" + userId +"#"+ x +"#"+ y;

        byte[] setMsg = message.getBytes();
        dos.writeInt(setMsg.length);
        dos.write(setMsg);
    }
}
