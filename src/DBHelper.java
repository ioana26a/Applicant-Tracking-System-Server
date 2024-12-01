import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
public final class DBHelper {
        private static final String URL = "jdbc:mysql://localhost:3306/recruit_ease_db";
        private static final String USER = "root";
        private static final String PASSWORD = "1234";
        private static Connection connection;
        public static void connect() {
                try {
                        if (connection == null || connection.isClosed()) {
                                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                                System.out.println("Conexiune reușită la baza de date!");
                        }
                } catch (SQLException e) {
                        e.printStackTrace();
                }
        }
        public static void disconnect() {
                if (connection != null) {
                        try {
                                connection.close();
                                System.out.println("Conexiunea a fost închisă cu succes.");
                        } catch (SQLException e) {
                                System.err.println("Eroare la închiderea conexiunii: " + e.getMessage());
                        }
                }
        }
        public static JSONArray selectAllCandidates() {
                String sql = "SELECT * FROM candidati";
                JSONArray candidatesArray = new JSONArray();
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                        while (rs.next()) {
                                JSONObject candidate = new JSONObject();
                                candidate.put("id", rs.getInt("id"));
                                candidate.put("nume", rs.getString("nume"));
                                candidate.put("prenume", rs.getString("prenume"));
                                candidate.put("telefon", rs.getString("telefon"));
                                candidate.put("email", rs.getString("email"));
                                candidate.put("cv", rs.getString("cv"));
                                candidate.put("sursa", rs.getString("sursa"));
                                candidate.put("lista_neagra", rs.getString("lista_neagra"));
                                candidate.put("id_recrutor", rs.getString("id_recrutor"));
                                candidate.put("id_status", rs.getString("id_status"));
                                candidate.put("id_oras", rs.getString("id_oras"));
                                candidate.put("strada", rs.getString("strada"));
                                candidate.put("numar", rs.getString("numar"));
                                candidatesArray.put(candidate);
                        }
                } catch (SQLException e) {
                        e.printStackTrace();
                }
                return candidatesArray;
        }

        public static void verifyUser(String user, String password){
                // TODO daca nu gaseste intoarce un mesaj specific si blocheaza userul de la accesul in aplicatie
                // cauta in bd user si parola + limitarea numarului de incercari
        }
}
