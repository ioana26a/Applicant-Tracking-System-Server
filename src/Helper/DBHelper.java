package Helper;

import ServerUtilities.Server;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
public final class DBHelper {
        private static final String URL = "jdbc:mysql://localhost:3306/recruit_ease_db";
        private static final String USER = "root";
        private static final String PASSWORD = "1234";
        private static Connection connection;
        private static final int BLOCK_TIME = 10;
        private static final int MAX_ATTEMPTS = 3;      //incercarile ramase pentru parola gresita
        private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        private static final Map<String, Integer> attemptCounter = new HashMap<>();
        public static void connect() {
                try {
                        if (connection == null || connection.isClosed()) {
                                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                                Server.log.append("Conexiune reușită la baza de date!\n");
                        }
                } catch (SQLException e) {
                        e.printStackTrace();
                }
        }
        public static void disconnect() {
                if (connection != null) {
                        try {
                                connection.close();
                                Server.log.append("Conexiunea a fost închisă cu succes.\n");
                        } catch (SQLException e) {
                                System.err.println("Eroare la închiderea conexiunii: " + e.getMessage());
                        }
                }
        }
        public static String verifyUser(String user, String password) {
                String query = """
            SELECT utilizator, parola FROM UTILIZATORI WHERE UTILIZATOR = ?
            """;
                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                        stmt.setString(1, user);
                        ResultSet rs = stmt.executeQuery();

                        if (rs.next()) {
                                String dbPassword = rs.getString("parola");
                                if (BCrypt.checkpw(password, dbPassword)) {
                                        attemptCounter.remove(user);
                                        return "Autentificare cu succes";
                                } else {
                                        incrementAttempts(user);
                                        return "Parola incorectă. Încercări rămase: " + (MAX_ATTEMPTS - attemptCounter.get(user));
                                }
                        } else {
                                return "Utilizatorul nu există";
                        }
                } catch (SQLException e) {
                        e.printStackTrace();
                        return "Eroare la accesarea bazei de date.";
                }
        }
        private static void incrementAttempts(String user) {
                attemptCounter.put(user, attemptCounter.getOrDefault(user, 0) + 1);
                if (attemptCounter.get(user) >= MAX_ATTEMPTS) {
                        scheduler.schedule(() -> resetAttempts(user), BLOCK_TIME, TimeUnit.MINUTES);
                        Server.log.append("Utilizatorul " + user + " a fost blocat. Timpul de asteptare este de " + BLOCK_TIME + " minute.\n");
                }
        }
        private static void resetAttempts(String user) {
                attemptCounter.remove(user);
                Server.log.append("Blocarea utilizatorului " + user + " a fost ridicată.");
        }
        //de inlocuit cu json object
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
        public static String addCandidate(JSONObject candidateJson){
                //la atribut cv trebuie salvata calea catre cv pe server
                //deci mai intai trebuie ca serverul sa primeasca cv ul si mai apoi insereaza in BD candidatul


                String nume = candidateJson.getString("nume");
                String prenume = candidateJson.getString("prenume");
                String email = candidateJson.getString("email");
                String telefon = candidateJson.getString("telefon");
                String cv = "C:\\Users\\Ioana\\IdeaProjects\\cv-licenta";
                String sursa = candidateJson.getString("sursa");
                boolean listaNeagra = candidateJson.getBoolean("lista_neagra");
                int idRecruiter = candidateJson.getInt("recrutor");
                int idStatus = candidateJson.getInt("status");
                int idOras = candidateJson.getInt("oras");
                String strada = candidateJson.getString("strada");
                int numar = candidateJson.getInt("numar");

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
                        return "Candidatul a fost adaugat cu succes";
                } catch (SQLException e) {
                        e.printStackTrace();
                        return "Eroare la adaugarea candidatului";
                }
        }
        public static JSONArray selectAllCandidates() {
                String sql = "SELECT * FROM info_candidati";
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
                                candidate.put("lista_neagra", rs.getBoolean("lista_neagra"));
                                candidate.put("recrutor", rs.getString("recrutor"));
                                candidate.put("status", rs.getString("status"));
                                candidate.put("oras", rs.getString("oras"));
                                candidate.put("strada", rs.getString("strada"));
                                candidate.put("numar", rs.getInt("numar"));
                                candidatesArray.put(candidate);
                        }
                } catch (SQLException e) {
                        e.printStackTrace();
                }
                return candidatesArray;
        }
        public static JSONArray selectAllVacancies() {
                String sql = "SELECT * FROM info_posturi";
                JSONArray vacanciesArray = new JSONArray();
                try (Statement stmt = connection.createStatement();     //de modificat coloanele
                     ResultSet rs = stmt.executeQuery(sql)) {
                        while (rs.next()) {
                                JSONObject vacancy = new JSONObject();
                                vacancy.put("id", rs.getInt("id"));
                                vacancy.put("nume", rs.getString("nume"));
                                vacancy.put("prenume", rs.getString("prenume"));
                                vacancy.put("telefon", rs.getString("telefon"));
                                vacancy.put("email", rs.getString("email"));
                                vacancy.put("cv", rs.getString("cv"));
                                vacancy.put("sursa", rs.getString("sursa"));
                                vacancy.put("lista_neagra", rs.getBoolean("lista_neagra"));
                                vacancy.put("recrutor", rs.getString("recrutor"));
                                vacancy.put("status", rs.getString("status"));
                                vacancy.put("oras", rs.getString("oras"));
                                vacancy.put("strada", rs.getString("strada"));
                                vacancy.put("numar", rs.getInt("numar"));
                                vacanciesArray.put(vacancy);
                        }
                } catch (SQLException e) {
                        e.printStackTrace();
                }
                return vacanciesArray;
        }
        public boolean updateCandidate(JSONObject candidate) throws SQLException {
                String nume = candidate.getString("nume");
                String prenume = candidate.getString("prenume");
                String email = candidate.getString("email");
                String telefon = candidate.getString("telefon");
                String cv = candidate.getString("cv");
                String sursa = candidate.getString("sursa");
                boolean listaNeagra = candidate.getBoolean("lista_neagra");
                int idRecruiter = candidate.getInt("recrutor");
                int idStatus = candidate.getInt("status");
                int idOras = candidate.getInt("oras");
                String strada = candidate.getString("strada");
                int numar = candidate.getInt("numar");

                String query = "UPDATE candidati SET nume = ?, prenume = ?, email = ?, telefon = ?, cv = ?, sursa = ?, " +
                        "lista_neagra = ?, id_recruiter = ?, id_status = ?, id_oras = ?, strada = ?, numar = ? " +
                        "WHERE id_candidat = ?";

                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                        // Setează parametrii
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

                        int rowsUpdated = stmt.executeUpdate();
                        if (rowsUpdated > 0) {
                                System.out.println("Candidatul a fost actualizat cu succes!");
                                return true;
                        } else {
                                System.out.println("Eroare: Candidatul nu a fost găsit.");
                        }
                        return false;
                }
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
