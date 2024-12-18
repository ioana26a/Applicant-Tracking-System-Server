package Commands;

import Helper.DBHelper;
import ServerUtilities.Server;

import java.io.PrintWriter;


public class VerifyUserCommand implements Command{
        private final String user;
        private final String password;
        public VerifyUserCommand(String user, String password) {
                this.user = user;
                this.password = password;
        }
        @Override
        public void execute(PrintWriter writer) {
                String response = DBHelper.verifyUser(user,password);
                writer.println(response);
                Server.log.append(response + "\n");
        }
}
