package Commands;

import java.util.HashMap;
import java.util.Map;

public class CommandFactory {

        private static final Map<String, Command> commandMap = new HashMap<>();

        static {
                commandMap.put("get_candidates", new GetCandidatesCommand());
                //commandMap.put("get_vacancies", new GetVacanciesCommand());
                //commandMap.put("get_applications", new GetVacanciesCommand());
                //commandMap.put("get_interviews", new GetInterviewsCommand());
                //commandMap.put("get_clients", new GetClientsCommand());
                //commandMap.put("get_contacts", new GetContactsCommand());
        }

        public static Command createCommand(String input) {
                String[] parts = input.split(" ");
                if (input.startsWith("Candidat info:")) {
                        // Extragem JSON-ul din mesaj
                        String candidateJson = input.substring("Candidat info:".length()).trim();
                        return new AddCandidateCommand(candidateJson);
                }
                if (input.startsWith("CV:")) {
                        // Extragem informațiile despre fișier
                        String fileInfo = input.substring("CV:".length()).trim();
                        String[] fileParts = fileInfo.split("\\|"); // Format așteptat: CV:nume_fisier|dimensiune
                        if (fileParts.length == 2) {
                                String fileName = fileParts[0];
                                int fileSize;
                                try {
                                        fileSize = Integer.parseInt(fileParts[1]);
                                } catch (NumberFormatException e) {
                                        System.err.println("Dimensiunea fișierului este invalidă: " + fileParts[1]);
                                        return null;
                                }
                                System.out.println("info: " + fileName + fileSize);
                                return new AddCVCandidateCommand(fileName, fileSize);
                        } else {
                                System.err.println("Format invalid pentru comanda CV.");
                                return null;
                        }
                }
                if (parts.length == 3 && parts[0].equalsIgnoreCase("delete")) {
                        String entity = parts[1];
                        int id = -1;
                        try {
                                id = Integer.parseInt(parts[2]);
                        } catch (NumberFormatException e) {
                                System.err.println("ID invalid: " + parts[2]);
                                return null;
                        }
                        return new DeleteCommand(entity, id);
                }
                if (parts.length == 3 && parts[0].equalsIgnoreCase("verify_user")) {
                        String user = parts[1];
                        String password = parts[2];
                        return new VerifyUserCommand(user, password);
                }
                //apelarea celorlalte comenzi
                return commandMap.get(parts[0].toLowerCase());
        }
}


