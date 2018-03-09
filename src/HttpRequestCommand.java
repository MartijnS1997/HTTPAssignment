/**
 * Created by Martijn on 8/03/2018.
 */
public enum HttpRequestCommand {
    GET, HEAD, PUT, POST;

    /**
     * Checks if the provided command is an interactive command (PUT or POST)
     * @param httpCommand the command to be checked
     * @return true if and only if the command is a PUT or a POST
     */
    public static boolean isInteractiveCommand(HttpRequestCommand httpCommand){
        //then open a switch to check:
        switch(httpCommand){
            case PUT:
                return true;
            case POST:
                return true;
            default:
                //default case, is not an interactive command only PUT and POST are interactive
                return false;
        }
    }
}
