import java.io.*;
import java.net.*;

public class ChatClient {
    private static final String SERVER_ADDRESS = "localhost";  // サーバーのIPアドレスまたはホスト名
    private static final int SERVER_PORT = 12345;  // サーバーのポート番号

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("チャットが開始されました！");

            // 別スレッドでメッセージを受信
            Thread receiveThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        System.out.println("受信: " + message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            receiveThread.start();

            // ユーザーからの入力を受け取って送信
            String message;
            while ((message = userInput.readLine()) != null) {
                out.println(message);  // メッセージをサーバーに送信
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
