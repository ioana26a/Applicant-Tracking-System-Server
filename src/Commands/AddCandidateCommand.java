package Commands;

import Helper.DBHelper;
import org.json.JSONObject;
import java.io.PrintWriter;

public class AddCandidateCommand implements Command {

        private final JSONObject candidateJson;

        public AddCandidateCommand(String jsonString) {
                this.candidateJson = new JSONObject(jsonString);
        }

        @Override
        public void execute(PrintWriter writer) {
                String nume = candidateJson.getString("nume");
                String prenume = candidateJson.getString("prenume");
                String email = candidateJson.getString("email");
                String telefon = candidateJson.getString("telefon");
                String cv = candidateJson.getString("cv");
                String sursa = candidateJson.getString("sursa");
                boolean listaNeagra = candidateJson.getBoolean("lista_neagra");
                int idRecruiter = candidateJson.getInt("id_recruiter");
                int idStatus = candidateJson.getInt("id_status");
                int idOras = candidateJson.getInt("id_oras");
                String strada = candidateJson.getString("strada");
                int numar = candidateJson.getInt("numar");

                boolean success = DBHelper.addCandidate(
                        nume, prenume, email, telefon, cv, sursa, listaNeagra,
                        idRecruiter, idStatus, idOras, strada, numar
                );

                if (success) {
                        writer.println("Candidate added successfully.");
                } else {
                        writer.println("Failed to add candidate.");
                }
        }
}
