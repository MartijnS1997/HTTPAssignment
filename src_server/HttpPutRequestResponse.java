import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by Martijn on 11/03/2018.
 * A class of Put responses, handles the put responses for the server
 */
public class HttpPutRequestResponse extends HttpRequestResponse {


    public HttpPutRequestResponse(Path serverPath, ServerFileSystem fileSystem, String[] messageBody){
        super(serverPath, fileSystem);
        this.messageBody = messageBody;
    }

    @Override
    public void sendResponse(PrintWriter writer) {
        writeFile();
        ResponseHeader header = new ResponseHeader(HttpStatusCode.OK);
        header.writeResponseHeader(writer);
    }

    private void writeFile(){
        ServerFileSystem fileSystem = this.getFileSystem();
        Path serverPath = this.getServerPath();
        String messageBody[] = this.getMessageBody();
        fileSystem.writeTextBasedFile(serverPath, messageBody);
        //also write the file back to the backup

        Path outputFolder = this.getOutPutPath();
        Path outputFile = Paths.get(outputFolder.toString(), serverPath.getFileName().toString());
        File file = new File(outputFile.toUri());
        file.getParentFile().mkdirs();

        try {
            file.createNewFile();
            PrintWriter writer = new PrintWriter(new FileOutputStream(file));
            for(String line: messageBody){
                writer.println(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Getter for the message string of the put request
     * the message contains the file to modify
     * @return the string containing the message body
     */
    private String[] getMessageBody() {
        return messageBody;
    }

    private String[] messageBody;
}
