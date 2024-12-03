import Commands.DBHelper;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

        public static void connectClient(int port) {
                try (ServerSocket serverSocket = new ServerSocket(port)) {
                        System.out.println("Server is listening on port " + port);
                        DBHelper.connect();
                        /*JSONArray a = DBHelper.selectAllCandidates();
                        for (int i = 0; i < a.length(); i++) {
                                // Obținem un obiect JSONObject pentru fiecare candidat
                                JSONObject jsonObject = a.getJSONObject(i);

                                // Deserializăm datele în obiecte de tip Candidat
                                int id = jsonObject.getInt("id");
                                String nume = jsonObject.getString("nume");
                                String prenume = jsonObject.getString("prenume");


                                // Afisăm datele candidatului
                                System.out.println(id+nume+prenume);
                        }*/
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