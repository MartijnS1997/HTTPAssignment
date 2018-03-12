import java.io.PrintWriter;
import java.nio.file.Path;

/**
 * Created by Martijn on 11/03/2018.
 * an abstract class of request responses, implements general methods needed in all response cases
 * and enables the server to handle responses in a generic way
 */
public abstract class HttpRequestResponse {
    /**
     * Constructor for a http request response
     * @param serverPath the path the response interacts with
     * @param fileSystem the file system used by the server for generating responses
     * note: the server path has the same structure as the url
     */
    public HttpRequestResponse(Path serverPath, ServerFileSystem fileSystem){
        this.serverPath = serverPath;
    }

    /**
     * Sends the request response to the client that is connected to the server
     * @param writer the writer used to send the response
     */
    public abstract void sendResponse(PrintWriter writer);

    /**
     * Getter for the server path, the path used by the server for locating files that interact with the request
     * @return the path containing the file to interact with
     */
    private Path getServerPath() {
        return serverPath;
    }

    /**
     * Getter for the file system used by the server that will be used in generating the
     * responses for the server
     * @return the file system used by the server
     */
    public ServerFileSystem getFileSystem() {
        return fileSystem;
    }

    /**
     * The path the request needs to interact with
     */
    private Path serverPath;

    /**
     * The file system that will be used for accessing the data for the requests
     */
    private ServerFileSystem fileSystem;
}
