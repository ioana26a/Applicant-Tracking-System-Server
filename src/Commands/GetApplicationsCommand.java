package Commands;

import Helper.DBHelper;
import ServerUtilities.Server;
import org.json.JSONArray;

import java.io.PrintWriter;

public class GetApplicationsCommand implements Command {

        @Override
        public void execute(PrintWriter writer) {
                JSONArray applications = DBHelper.selectAllApplications();
                if (applications.length() > 0) {
                        writer.println("Applications: " + applications);
                        Server.log.append(applications + "\n");
                } else {
                        writer.println("No applications found.\n");
                }
        }
}
