package Commands;

import Helper.DBHelper;
import ServerUtilities.Server;
import org.json.JSONObject;
import java.io.PrintWriter;

public class AddCandidateCommand implements Command {

        private final JSONObject candidateJson;

        public AddCandidateCommand(String jsonString) {
                this.candidateJson = new JSONObject(jsonString);
        }

        @Override
        public void execute(PrintWriter writer) {
                String raspuns = DBHelper.addCandidate(candidateJson);
                writer.println(raspuns);
                Server.log.append("Sent to client: " + raspuns + "\n");
        }
}
