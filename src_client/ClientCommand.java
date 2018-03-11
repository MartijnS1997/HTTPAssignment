import java.net.URL;

/**
 * Created by Martijn on 8/03/2018.
 * an interface of client commands
 * after parsing the parser should generate a ClientCommand object to communicate the current command to the client
 * should contain a command type (enum) specifying the type of command that was issued (used in request construction)
 * also should contain the url & port that needs to be used by the client
 * the interface also signifies if additional information is needed (a message body) for completion of the command
 */
public interface ClientCommand {

    /**
     * Gets the command type of the command
     * @return the command type
     */
    HttpRequestCommand getCommandType();

    //Todo change type to URL and name to getUrl once ready
    /**
     * The URI of the command
     * @return the uri (as a string)
     */
    URL getUrl();

    /**
     * Getter for the TCP port used for server communication
     * @return the port trough which communication happens
     */
    int getPort();

    /**
     * @return true if and only if the command type is post or put
     */
    boolean needsMessageBody();

}
