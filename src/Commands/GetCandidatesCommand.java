package Commands;
import Helper.DBHelper;
import ServerUtilities.Server;
import org.json.JSONArray;
import java.io.PrintWriter;

public class GetCandidatesCommand implements Command {

        @Override
        public void execute(PrintWriter writer) {
                JSONArray candidates = DBHelper.selectAllCandidates();
                if (candidates.length() > 0) {
                        writer.println("Candidates: " + candidates.toString());
                        Server.log.append(candidates.toString());
                } else {
                        writer.println("No candidates found.");
                }
        }
}
