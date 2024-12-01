import java.io.*;
import java.net.*;

public class Server {

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