package Helper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
public final class DBHelper {
        private static final String URL = "jdbc:mysql://localhost:3306/recruit_ease_db";
        private static final String USER = "root";
        private static final String PASSWORD = "1234";
        private static Connection connection;
        private static final int MAX_ATTEMPTS = 3;      //incercarile ramase pentru parola gresita
        private static final Map<String, Integer> attemptCounter = new HashMap<>();
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
        public static String verifyUser(String user, String password){
                // TODO daca nu gaseste intoarce un mesaj specific si blocheaza userul de la accesul in aplicatie
                // cauta in bd user si parola + limitarea numarului de incercari
                String query = """
                        SELECT utilizator,parola FROM UTILIZATORI WHERE UTILIZATOR = ?
                        """;
                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                        stmt.setString(1, user);
                        ResultSet rs = stmt.executeQuery();

                        if (rs.next()) {
                                String dbPassword = rs.getString("parola");

                                if (dbPassword.equals(password)) {
                                        // Utilizatorul este adaugat in map daca a gresit parola si cand se ajunge la
                                        // 3 incercari se blocheaza utilizatorul
                                        attemptCounter.remove(user);
                                        return "Success";
                                } else {
                                        incrementAttempts(user);
                                        return "Parola incorecta.Incercari ramase: " + (MAX_ATTEMPTS - attemptCounter.get(user));
                                }
                        } else {
                                return "Utilizatorul nu exista";
                        }
                } catch (SQLException e) {
                        e.printStackTrace();
                        return "Database error.";
                }
        }
        private static void incrementAttempts(String user) {
                attemptCounter.put(user, attemptCounter.getOrDefault(user, 0) + 1);
                if (attemptCounter.get(user) >= MAX_ATTEMPTS) {
                        System.out.println("Utilizatorul " + user + " a fost blocat.");
                }
        }
        public static boolean addCandidate(String nume, String prenume, String email, String telefon, String cv, String sursa,
                                           boolean listaNeagra, int idRecruiter, int idStatus, int idOras, String strada, int numar) {
                String query = """
                            INSERT INTO candidati 
                            (nume, prenume, email, telefon, cv, sursa, lista_neagra, id_recruiter, id_status, id_oras, strada, numar) 
                            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                        """;

                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                        stmt.setString(1, nume);
                        stmt.setString(2, prenume);
                        stmt.setString(3, email);
                        stmt.setString(4, telefon);
                        stmt.setString(5, cv);
                        stmt.setString(6, sursa);
                        stmt.setBoolean(7, listaNeagra);
                        stmt.setInt(8, idRecruiter);
                        stmt.setInt(9, idStatus);
                        stmt.setInt(10, idOras);
                        stmt.setString(11, strada);
                        stmt.setInt(12, numar);

                        stmt.executeUpdate();
                        return true;
                } catch (SQLException e) {
                        e.printStackTrace();
                        return false;
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
                                candidate.put("id_recruiter", rs.getString("id_recruiter"));
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
        public static String deleteRowFromTable(String entitate, int id) {
                //sterge inregistrarea cu id si numele entitatii primite de la client

                //placeholder pentru id
                String sql = "DELETE FROM " + entitate + " WHERE id = ?";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                        stmt.setInt(1, id);
                        int rowsAffected = stmt.executeUpdate();
                        if (rowsAffected > 0) {
                                return "Inregistrarea a fost ștearsă cu succes.";
                        } else {
                                return "Nu s-a găsit inregistrarea cu id-ul " + id;
                        }
                } catch (SQLException e) {
                        e.printStackTrace();
                        return "Eroare la ștergerea înregistrării: " + e.getMessage();
                }
        }

}
