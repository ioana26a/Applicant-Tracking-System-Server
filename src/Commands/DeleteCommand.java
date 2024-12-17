package Commands;

import Helper.DBHelper;

import java.io.PrintWriter;

public class DeleteCommand implements Command {
        private final String entity;
        private final int id;
        public DeleteCommand(String entity, int id) {
                this.entity = entity;
                this.id = id;
        }
        @Override
        public void execute(PrintWriter writer) {
                //trebuie metoda sa intoarca un mesaj corespunzator. ex inoarce stringul ce trebuie afisat utilizatorului
                String message = DBHelper.deleteRowFromTable(entity, id);
                writer.println(message);
        }
}
