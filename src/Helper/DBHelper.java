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
                String idRecruiter = candidateJson.getString("recrutor");
                String idStatus = candidateJson.getString("status");
                String idOras = candidateJson.getString("oras");
                String strada = candidateJson.getString("strada");
                String numar = candidateJson.getString("numar");



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
                        stmt.setString(8, idRecruiter);
                        stmt.setString(9, idStatus);
                        stmt.setString(10, idOras);
                        stmt.setString(11, strada);
                        stmt.setString(12, numar);

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
                String sql = "SELECT * FROM info_posturi_vacante";
                JSONArray vacanciesArray = new JSONArray();
                try (Statement stmt = connection.createStatement();     //de modificat coloanele
                     ResultSet rs = stmt.executeQuery(sql)) {
                        while (rs.next()) {
                                JSONObject vacancy = new JSONObject();
                                vacancy.put("id", rs.getInt("id"));
                                vacancy.put("nume_client", rs.getString("nume_client"));
                                vacancy.put("manager_client", rs.getString("manager_client"));
                                vacancy.put("status_post", rs.getString("status_post"));
                                vacancy.put("industrie", rs.getString("industrie"));
                                vacancy.put("oras", rs.getString("oras"));
                                vacancy.put("tip_angajare", rs.getString("tip_angajare"));
                                vacancy.put("tip_post", rs.getString("tip_post"));
                                vacancy.put("titlu", rs.getString("titlu"));
                                vacancy.put("descriere", rs.getString("descriere"));
                                vacancy.put("numar_pozitii", rs.getString("numar_pozitii"));
                                vacancy.put("data_publicarii", rs.getString("data_publicarii"));
                                vacancy.put("data_limita", rs.getString("data_limita"));
                                vacanciesArray.put(vacancy);
                        }
                } catch (SQLException e) {
                        e.printStackTrace();
                }
                return vacanciesArray;
        }

        private static JSONArray getLookupData(String tableName) {
                JSONArray jsonArray = new JSONArray();
                String query = "SELECT * FROM " + tableName;

                try (PreparedStatement statement = connection.prepareStatement(query);
                     ResultSet rs = statement.executeQuery()) {
                        while (rs.next()) {
                                JSONObject row = new JSONObject();
                                ResultSetMetaData metaData = rs.getMetaData();
                                int columnCount = metaData.getColumnCount();
                                for (int i = 1; i <= columnCount; i++) {
                                        String columnName = metaData.getColumnName(i);
                                        Object value = rs.getObject(i);
                                        row.put(columnName, value);
                                }
                                jsonArray.put(row);
                        }
                } catch (SQLException e) {
                        e.printStackTrace();
                }
                if (jsonArray.isEmpty()) {
                        Server.log.append("Tabela " + tableName + " este goală.\n");
                }
                return jsonArray;
        }

        public static JSONObject selectAllLookupTables() {
                JSONObject result = new JSONObject();

                // datele pentru fiecare tabel lookup
                result.put("Abilitati", getLookupData("abilitati"));
                result.put("Abilitati_candidat", getLookupData("abilitati_candidati"));
                result.put("Entitati", getLookupData("entitati"));
                result.put("Nivel", getLookupData("nivel"));
                result.put("Industrie", getLookupData("industrie"));
                result.put("Oras", getLookupData("OrasJudetView"));
                result.put("Status_aplicate", getLookupData("status_aplicatie"));
                result.put("Status_candidat", getLookupData("status_candidat"));
                result.put("Status_interviu", getLookupData("status_interviu"));
                result.put("Status_post", getLookupData("status_post"));
                result.put("Tip_angajare", getLookupData("tip_angajare"));
                result.put("Tip_nota", getLookupData("tip_nota"));
                result.put("Tip_post", getLookupData("tip_post"));
                result.put("Tip_operatie", getLookupData("tip_operatie"));

                return result;
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
