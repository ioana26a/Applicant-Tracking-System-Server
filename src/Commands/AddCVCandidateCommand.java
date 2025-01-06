package Commands;

import ServerUtilities.Server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class AddCVCandidateCommand implements Command {

        private final int fileSize;
        private final String fileName;
        private byte[] fileData;
        public AddCVCandidateCommand(String fileName, int fileSize) {
                this.fileName = fileName;
                this.fileSize = fileSize;
        }
        public int getFileSize(){
                return fileSize;
        }
        // continutul fisierului
        public void setFileData(byte[] fileData) {
                this.fileData = fileData;
        }
        @Override
       public void execute(PrintWriter writer) {
                String folderPath = "C:\\Users\\Ioana\\IdeaProjects\\cv-licenta";
                File folder = new File(folderPath);
                if (!folder.exists()) {
                        folder.mkdirs();
                }
                String filePath = folderPath + "\\" + fileName; // Se folosește fileName pentru consistență
                File fisierCV = new File(filePath); // Se verifică existența fișierului conform fileName
                if (fisierCV.exists()) {
                        writer.println("Fișierul deja există\n");
                        Server.log.append("Fișierul deja există\n");
                        return;
                }
                try (FileOutputStream fos = new FileOutputStream(filePath)) {
                        fos.write(fileData);
                        writer.println("CV-ul a fost salvat cu succes!\n");
                        Server.log.append("CV-ul a fost salvat cu succes!\n");
                } catch (Exception e) {
                        e.printStackTrace();
                        writer.println("Eroare la salvarea CV-ului: " + e.getMessage() + "\n");
                        Server.log.append("Eroare la salvarea CV-ului: " + e.getMessage() + "\n");
                }
        }
}

