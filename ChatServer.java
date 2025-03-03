import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = Integer.parseInt(System.getenv("PORT"));  // Herokuの環境変数からポート番号を取得  // ポート番号
    private static Set<PrintWriter> clientWriters = new HashSet<>();

    public static void main(String[] args) {
        System.out.println("サーバーが起動しました...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                new ClientHandler(serverSocket.accept()).start();  // クライアント接続を受け付け
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // クライアントごとの処理を担当するスレッド
    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                synchronized (clientWriters) {
                    clientWriters.add(out);  // クライアントの出力ストリームを登録
                }

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("受信: " + message);
                    synchronized (clientWriters) {
                        for (PrintWriter writer : clientWriters) {
                            writer.println(message);  // すべてのクライアントにメッセージを送信
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (clientWriters) {
                    clientWriters.remove(out);  // 接続が切れたクライアントの出力ストリームを削除
                }
            }
        }
    }
}
