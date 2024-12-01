import org.json.JSONArray;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerThread extends Thread {

        private final Socket socket;
        private final Map<String, Runnable> commandMap;
        public ServerThread(Socket socket) {
                this.socket = socket;
                this.commandMap = new HashMap<>();
                initializeCommands();
        }
        private void initializeCommands() {
                commandMap.put("get_candidates", this::handleGetCandidates);
                commandMap.put("authenticate_user", this::handleAuthenticateUser);
                commandMap.put("exit", this::handleExit);
        }
        public void run() {
                try (InputStream input = socket.getInputStream();
                     OutputStream output = socket.getOutputStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                     PrintWriter writer = new PrintWriter(output, true)) {

                        String text;
                        while ((text = reader.readLine()) != null) {
                                System.out.println("Received: " + text);
                                Runnable command = commandMap.get(text.toLowerCase());
                                if (command != null) {
                                        command.run();
                                } else {
                                        writer.println("Unknown command: " + text);
                                }
                        }
                } catch (IOException ex) {
                        System.err.println("ServerThread exception: " + ex.getMessage());
                        ex.printStackTrace();
                } finally {
                        try {
                                socket.close();
                        } catch (IOException ex) {
                                System.err.println("Error closing socket: " + ex.getMessage());
                        }
                }
        }
        private void handleAuthenticateUser() {
                // TODO Cod pentru autentificarea unui utilizator
                DBHelper.verifyUser("abc","abc");
        }

        private void handleExit() {
                System.out.println("Client disconnected");
        }
        private void handleGetCandidates() {
                JSONArray candidates = DBHelper.selectAllCandidates();
                // TODO Cod pentru trimiterea datelor candidatului
        }
}