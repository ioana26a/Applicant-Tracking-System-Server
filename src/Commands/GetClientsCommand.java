package Commands;

import Helper.DBHelper;
import ServerUtilities.Server;
import org.json.JSONArray;

import java.io.PrintWriter;

public class GetClientsCommand implements Command {

        @Override
        public void execute(PrintWriter writer) {
                JSONArray clients = DBHelper.selectAllClients();
                if (clients.length() > 0) {
                        writer.println("Clients: " + clients);
                        Server.log.append(clients + "\n");
                } else {
                        writer.println("No clients found.\n");
                }
        }
}
