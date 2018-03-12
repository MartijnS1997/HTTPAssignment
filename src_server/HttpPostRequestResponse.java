import java.io.PrintWriter;
import java.nio.file.Path;

/**
 * Created by Martijn on 11/03/2018.
 * a class of post request responses, handles the post request responses for the server
 */
public class HttpPostRequestResponse extends HttpRequestResponse {

    public HttpPostRequestResponse(Path serverPath, ServerFileSystem fileSystem, String[] messageBody) {
        super(serverPath, fileSystem);
        this.messageBody = messageBody;
    }

    @Override
    public void sendResponse(PrintWriter writer) {

    }

    /**
     * Getter for the message body used by the Put request
     * @return the message body issued by the post request
     */
    private String[] getMessageBody() {
        return messageBody;
    }

    /**
     * String containing the message body that went along with the post request, it contains
     * information for the server on how to handle the request
     */
    private String[] messageBody;
}
