import java.io.PrintWriter;

/**
 * Created by Martijn on 11/03/2018.
 * an abstract class of request responses, implements general methods needed in all response cases
 * and enables the server to handle responses in a generic way
 */
public abstract class HttpRequestResponse {

    /**
     * Sends the request response to the client that is connected to the server
     * @param writer the writer used to send the response
     */
    public abstract void sendResponse(PrintWriter writer);
}
