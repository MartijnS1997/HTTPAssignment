import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Martijn on 8/03/2018.
 * A class that parses the command into a ClientCommand object
 */
//Todo add functionality that checks if there is a protocol in the url, if not it will add it
public class ClientCommandParser {


    public ClientCommandParser(){
        //empty constructor
    }

    public static ClientCommand parseCommand(String commandString){
        String[] commandParts = commandString.split(" "); //split the string space
        //check if the command was properly formatted (three parts)
        if(commandParts.length != NB_OF_COMMAND_PARTS){
            throw new ClientException(INVALID_FORMAT);
        }
        HttpRequestCommand httpRequestCommand = convertToCommand(commandParts[0]);
        //if it is properly formatted return a command object
        return new ClientCommand() {
            @Override
            public HttpRequestCommand getCommandType() {
                return httpRequestCommand;
            }

            @Override
            public URL getUrl() {
                return convertToUrl(commandParts[1]);
            }

            @Override
            public int getPort() {
                return Integer.parseInt(commandParts[2]);
            }

            @Override
            public boolean needsMessageBody() {
                return HttpRequestCommand.isInteractiveCommand(httpRequestCommand);
            }
        };
    }

    protected static URL convertToUrl(String urlString){
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            //first check if the protocol is missing (needed for the assignment)
            String potentialHTTP = urlString.substring(0, HTTP.length()-1);
            if(!HTTP.equals(potentialHTTP)){
                //if so add it
                urlString = HTTP + urlString;
                //retry
                return convertToUrl(urlString);
            }else{
                //we've tried to add the protocol and we still get a problem, report the error
                //an other unexpected problem
                e.printStackTrace();
            }
        }
        return url;
    }


    /**
     * Checks if the given string contains a valid http command
     * @param httpCommand the string to be checked
     * @return only returns true if the string is equal to "GET", "POST", "PUT" or "HEAD"
     */
    private static boolean isValidHttpCommand(String httpCommand){
        switch(httpCommand){
            case GET:
                return true;
            case POST:
                return true;
            case PUT:
                return true;
            case HEAD:
                return true;
            default:
                return false;
        }
    }

    /**
     * Converts the given string to an http command enum
     * @param httpCommand the command to be converted
     * @return a HttpCommand enum object representing the http command
     */
    private static HttpRequestCommand convertToCommand(String httpCommand){
        if(!isValidHttpCommand(httpCommand)){
            throw new ClientException(INVALID_HTTP_COMMAND);
        }
        switch(httpCommand){
            case GET:
                return HttpRequestCommand.GET;
            case POST:
                return HttpRequestCommand.POST;
            case PUT:
                return HttpRequestCommand.PUT;
            case HEAD:
                return HttpRequestCommand.HEAD;
        }

        return null;
    }

    /**
     * Constants
     */
    private final static int NB_OF_COMMAND_PARTS = 3;

    private final static String GET = "GET";

    private final static String POST = "POST";

    private final static String PUT = "PUT";

    private final static String HEAD = "HEAD";

    private final static String HTTP = "http://";

    /**
     * Error messages
     */
    private static final String INVALID_FORMAT = "The command was not properly formatted, right format is: \"HTTPCommand URI PORT\"";

    private static final String INVALID_HTTP_COMMAND = "invalid or unsupported http command, please try GET, POST, PUT or HEAD";

}
