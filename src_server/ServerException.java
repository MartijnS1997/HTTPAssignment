/**
 * Created by Martijn on 10/03/2018.
 * an exception class used to message issues along the server (eg bad requests, 404, ...)
 */
public class ServerException extends IllegalArgumentException {

    public ServerException(HttpStatusCode statusCode){
        this.statusCode = statusCode;
    }

    public ServerException(String errorMessage){
        this.errorMessage = errorMessage;
    }

    /**
     * getter for the status code that caused the exception
     * @return an http status code that signifies the exception that occurred
     */
    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    /**
     * getter for the error message string used for communication with the server
     * @return the error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    private HttpStatusCode statusCode;
    private String errorMessage;
}
