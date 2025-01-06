package ServerUtilities;

import Commands.AddCVCandidateCommand;
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
                                        Server.clientDisconnected(socket);
                                        Server.log.append("Clientul " + socket.getPort() + " s-a deconectat.\n");
                                        break;
                                }
                                Command command = CommandFactory.createCommand(text);
                                if (command != null) {
                                        if (command instanceof AddCVCandidateCommand) {
                                                // continutul fisierului
                                                byte[] fileData = readFileFromInputStream(input, ((AddCVCandidateCommand) command).getFileSize());
                                                ((AddCVCandidateCommand) command).setFileData(fileData);

                                                // de testat daca este necesar

                                                String mesaj = "Citirea fișierului s-a încheiat.\n";    //semnalizeaza clientului incheierea
                                                output.write(mesaj.getBytes());
                                                output.flush();
                                        }
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

        private byte[] readFileFromInputStream(InputStream inputStream, int fileSize) throws IOException {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int bytesRead = 0;
                int totalRead = 0;

                //se verifica daca s a atins numarul de octeti, atunci de iese din bucla
                while (totalRead < fileSize && (bytesRead = inputStream.read(buffer)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, bytesRead);
                        totalRead += bytesRead;
                }

                return byteArrayOutputStream.toByteArray();
        }
}
