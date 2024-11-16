import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Server {

        public static void connectClient(int port) {
                try (ServerSocket serverSocket = new ServerSocket(port)) {
                        System.out.println("Server is listening on port " + port);
                        connectToDatabase();
                        while (true) {
                                Socket socket = serverSocket.accept();
                                System.out.println("New client connected");
                                new ServerThread(socket).start();
                        }
                } catch (IOException ex) {
                        System.out.println("Server exception: " + ex.getMessage());
                        ex.printStackTrace();
                }
        }
        public static void connectToDatabase() {
                String url = "jdbc:mysql://localhost:3306/recruit_ease_db";
                String user = "root";
                String password = "1234";
                try (Connection connection = DriverManager.getConnection(url, user, password)) {
                        System.out.println("Conexiune reușită la baza de date!");

                        // Creare statement pentru a executa comenzi SQL
                        /*Statement statement = connection.createStatement();
                        String sqlQuery = "SELECT * FROM tabelul_tau"; // Modifică cu numele tabelului tău
                        statement.executeQuery(sqlQuery);*/

                        // Poți adăuga aici mai multe operații pe baza de date

                } catch (SQLException e) {
                        e.printStackTrace();
                }
        }
}