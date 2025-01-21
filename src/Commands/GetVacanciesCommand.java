package Commands;

import Helper.DBHelper;
import ServerUtilities.Server;
import org.json.JSONArray;

import java.io.PrintWriter;

public class GetVacanciesCommand implements Command {

        @Override
        public void execute(PrintWriter writer) {
                JSONArray vacancies = DBHelper.selectAllVacancies();
                if (vacancies.length() > 0) {
                        writer.println("Vacancies: " + vacancies);
                        Server.log.append(vacancies + "\n");
                } else {
                        writer.println("No candidates found.\n");
                }
        }
}

