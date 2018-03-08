import java.net.URL;

/**
 * Created by Martijn on 8/03/2018.
 */
public interface Command {

    /**
     * Gets the command type of the command
     * @return the command type
     */
    HttpCommands getCommandType();

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




//TODO may be added, maybe better way to add the extra input
//    /**
//     * The body of the message, contains the extra information for a post or put command
//     * @return the body of the message
//     */
//    String getMessageBody();
//

}
