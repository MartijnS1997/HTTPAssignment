import java.io.PrintWriter;
import java.nio.file.Path;
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
        List<String> putResponseHeader = this.createResponseHeader(HttpStatusCode.OK);
        int putResponseSize = putResponseHeader.size();
        String[] putResponse = putResponseHeader.toArray(new String[putResponseSize]);
        writeToClient(writer, putResponse);
    }

    private void writeFile(){
        ServerFileSystem fileSystem = this.getFileSystem();
        Path serverPath = this.getServerPath();
        String messageBody[] = this.getMessageBody();

        fileSystem.writeTextBasedFile(serverPath, messageBody);
    }

    @Override
    protected List<String> createResponseHeader(HttpStatusCode statusCode) {
        return super.createResponseHeader(statusCode);
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
