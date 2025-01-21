package Commands;

import Helper.DBHelper;
import ServerUtilities.Server;
import org.json.JSONArray;

import java.io.PrintWriter;

public class GetInterviewsCommand implements Command {

        @Override
        public void execute(PrintWriter writer) {
                JSONArray interviews = DBHelper.selectAllInterviews();
                if (interviews.length() > 0) {
                        writer.println("Interviews: " + interviews);
                        Server.log.append(interviews + "\n");
                } else {
                        writer.println("No interviews found.\n");
                }
        }
}