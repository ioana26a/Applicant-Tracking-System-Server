package ServerUtilities;

import Commands.Command;
import Commands.CommandFactory;

import java.io.*;
import java.net.Socket;

public class ServerThread extends Thread {

        private final Socket socket;
        public ServerThread(Socket socket) {
                this.socket = socket;
        }

        public void run() {
                try (
                        InputStream input = socket.getInputStream();
                        OutputStream output = socket.getOutputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                        PrintWriter writer = new PrintWriter(output, true)
                ) {
                        String text;
                        while ((text = reader.readLine()) != null) {
                                Server.log.append("Received from client: " + text + "\n");
                                if (text.equalsIgnoreCase("disconnect_client")) {
                                        Server.log.append("Clientul " + socket.getPort() + " s-a deconectat.\n");
                                        break;
                                }
                                Command command = CommandFactory.createCommand(text);
                                if (command != null) {
                                        command.execute(writer);
                                } else {
                                        Server.log.append("Received from client: Unknown command: " + text + "\n");
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
}
