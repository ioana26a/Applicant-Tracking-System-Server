import Helper.DBHelper;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
        public static void main(String[] args) {
                Server.connectClient(1234);
        }
        public static void connectClient(int port) {
                try (ServerSocket serverSocket = new ServerSocket(port)) {
                        System.out.println("Server is listening on port " + port);
                        DBHelper.connect();
                        while (true) {
                                Socket socket = serverSocket.accept();
                                System.out.println("New client connected");
                                new ServerThread(socket).start();
                        }
                } catch (IOException ex) {
                        System.err.println("Server exception: " + ex.getMessage());
                        ex.printStackTrace();
                } finally {
                        DBHelper.disconnect();
                }
        }

}