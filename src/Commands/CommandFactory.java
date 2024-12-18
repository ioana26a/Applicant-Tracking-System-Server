package Commands;

import java.util.HashMap;
import java.util.Map;

public class CommandFactory {

        private static final Map<String, Command> commandMap = new HashMap<>();

        static {
                commandMap.put("get_candidates", new GetCandidatesCommand());
                //commandMap.put("add_candidate", new AddCandidateCommand());
        }

        public static Command createCommand(String input) {
                String[] parts = input.split(" ");

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


