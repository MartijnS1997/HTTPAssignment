import java.io.PrintWriter;
import java.nio.file.Path;

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
