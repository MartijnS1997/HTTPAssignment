import java.io.PrintWriter;
import java.nio.file.Path;

/**
 * Created by Martijn on 11/03/2018.
 * a class of head request responses, handles the head request responses for the server
 */
public class HttpHeadRequestResponse extends HttpRequestResponse{

    /**
     * Constructor for the head response
     * @param serverPath the path to interact with
     */
    public HttpHeadRequestResponse(Path serverPath, ServerFileSystem fileSystem) {
        super(serverPath, fileSystem);
    }

    @Override
    public void sendResponse(PrintWriter writer) {

    }
}
