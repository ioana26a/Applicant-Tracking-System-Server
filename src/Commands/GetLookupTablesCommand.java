package Commands;

import Helper.DBHelper;
import ServerUtilities.Server;
import org.json.JSONObject;

import java.io.PrintWriter;

public class GetLookupTablesCommand implements Command {

        @Override
        public void execute(PrintWriter writer) {
                JSONObject lookupTables = DBHelper.selectAllLookupTables();
                if (lookupTables != null) {
                        writer.println("Lookup tables: " + lookupTables);
                        Server.log.append(lookupTables + "\n");
                } else {
                        writer.println("eroare lookup tables.\n");
                }
        }
}
