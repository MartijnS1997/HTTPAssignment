import java.io.PrintWriter;
import java.nio.file.Path;

/**
 * Created by Martijn on 11/03/2018.
 * a class of get request responses, handles the get request responses for the server
 */
public class HttpGetRequestResponse extends HttpRequestResponse {
    /**
     * Constructor for a http get response
     * @param serverPath the requested filepath
     * @param header the header, needed to retrieve info about the if-modified-since
     */
    public HttpGetRequestResponse(Path serverPath, ServerFileSystem fileSystem, HttpRequestHeader header) {
        super(serverPath, fileSystem);
        this.header = header;

    }

    @Override
    public void sendResponse(PrintWriter writer) {

    }

    //todo implement
    private String[] getFileStringLines(){

        return null;
    }

    /**
     * Getter for the request header of the get request
     * @return a Http request header containing the necessary information about the get request
     */
    private HttpRequestHeader getHeader() {
        return header;
    }

    private HttpRequestHeader header;
}
