/**
 * Created by Martijn on 12/03/2018.
 * A class of response factories
 * builds request responses for the server connections
 */
public class RequestResponseFactory {

    /**
     * Creates a HttpRequestResponse object configured with the parameters provided by the request line, header and
     * message body of the received request
     * @param requestLine the request line object containing the line with the request
     * @param header the header of the request contains information about the flavour of the request (eg if-modified-since)
     * @param messageBody the extra message at the body of the request, only needed for interactive requests
     *                    like PUT and POST, in all other cases this string will be ignored
     * @return a HttpRequestResponse object ready to respond to the request
     */
    public static HttpRequestResponse createResponse(HttpRequestLine requestLine , HttpRequestHeader header, String[] messageBody, ServerFileSystem fileSystem){
        switch(requestLine.getMethod()){
            case GET:
                return generateGetResponse(requestLine, header, fileSystem);
            case HEAD:
                return generateHeadResponse(requestLine, fileSystem);
            case POST:
                return generatePostResponse(requestLine, messageBody, fileSystem);
            case PUT:
                return generatePutResponse(requestLine, messageBody, fileSystem);
            default:
                //will never happen only enums are defined
                return null;
        }
    }

    /**
     * Generates a http get request response object
     * @param requestLine the request line object containing the path needed for the get response
     * @param header the header of the request needed to check if the if-modified-since was added
     * @return a HttpGetRequestResponse object that can be executed by the connection for the client
     */
    private static HttpGetRequestResponse generateGetResponse(HttpRequestLine requestLine, HttpRequestHeader header, ServerFileSystem fileSystem){
        return new HttpGetRequestResponse(requestLine.getPath(), fileSystem, header);
    }

    /**
     * Generates a http head response object
     * @param requestLine the request line containing the path needed to generate the response
     * @return a HttpHeadRequestResponse object that cna be executed by the connection for the client
     */
    private static HttpHeadRequestResponse generateHeadResponse(HttpRequestLine requestLine, ServerFileSystem fileSystem){
        return new HttpHeadRequestResponse(requestLine.getPath(), fileSystem);
    }

    /**
     * Generates a http post request response object
     * @param requestLine the request line containing the path needed to generate the response
     * @param messageBody the message body containing further information for the post response
     * @return a HttpPostRequestResponse object
     */
    private static HttpPostRequestResponse generatePostResponse(HttpRequestLine requestLine, String[] messageBody, ServerFileSystem fileSystem){
        return new HttpPostRequestResponse(requestLine.getPath(), fileSystem, messageBody);
    }

    /**
     * Generates a http put request response object
     * @param requestLine the request line containing the path needed to generate the response
     * @param messageBody containing the data for the page to be replaced
     * @return a HttpPutRequestResponse object
     */
    private static HttpPutRequestResponse generatePutResponse(HttpRequestLine requestLine, String[] messageBody, ServerFileSystem fileSystem){
        //System.out.println("Put request created");
        return new HttpPutRequestResponse(requestLine.getPath(), fileSystem, messageBody);
    }

}
