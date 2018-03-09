import java.net.URL;

/**
 * Created by Martijn on 8/03/2018.
 */
public class RequestFactory {
    public RequestFactory(){
        //standard constructor, may be extended later
    }

    //Todo finish the create request functionality

    /**
     * Creates a HttpRequest based on the client command and the message body
     * @param clientCommand the command created by the client
     * @param messageBody message used for post and put requests, may be ignored if get or head request
     * @return a http request object ready to execute
     */
    public HttpRequest createRequest(ClientCommand clientCommand, String messageBody){
        //first we need to check if there needs to be added a message body
        //extract the type from the command
        HttpRequestCommand httpCommand = clientCommand.getCommandType();
        if(HttpRequestCommand.isInteractiveCommand(httpCommand)){
            //open a switch to generate the appropriate request
            switch(httpCommand){
                case PUT:
                    return createPutRequest(clientCommand, messageBody);
                case POST:
                    return createPostRequest(clientCommand, messageBody);
            }
        }else{
            switch(httpCommand){
                case GET:
                    return createGetRequest(clientCommand);
                case HEAD:
                    return createHeadRequest(clientCommand);
            }
        }
        return null;
    }

    /**
     * Creates a put request for the given url and message body
     * @param clientCommand the command issued to the client
     * @param messageBody the message to add to the request
     * @return a http put request object ready to execute
     */
    private static HttpPutRequest createPutRequest(ClientCommand clientCommand, String messageBody){
        URL url = clientCommand.getUrl();
        return new HttpPutRequest(url, messageBody);
    }

    /**
     * Creates a post request for the given url and message body
     * @param clientCommand the command issued to the client
     * @param messageBody the message to add to the request
     * @return a http post request object ready to execute
     */
    private static HttpPostRequest createPostRequest(ClientCommand clientCommand, String messageBody){
        URL url = clientCommand.getUrl();
        return new HttpPostRequest(url, messageBody);
    }

    /**
     * Creates a get request for the given url
     * @param clientCommand the command issued to the client
     * @return a http get request object ready to execute
     */
    private static HttpGetRequest createGetRequest(ClientCommand clientCommand){
        URL url = clientCommand.getUrl();
        return new HttpGetRequest(url);
    }

    /**
     * Creates a header request for the given url
     * @param clientCommand the command issued to the client
     * @return a http header request object ready to execute
     */
    private static HttpHeadRequest createHeadRequest(ClientCommand clientCommand){
        URL url = clientCommand.getUrl();
        return new HttpHeadRequest(url);
    }

}
