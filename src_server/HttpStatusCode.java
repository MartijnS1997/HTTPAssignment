/**
 * Created by Martijn on 10/03/2018.
 * an enum of http status codes
 */
public enum HttpStatusCode {
    OK, BAD_REQUEST, NOT_FOUND, SERVER_ERROR, NOT_MODIFIED, TIMEOUT;

    public int toInt(){
        switch (this){
            case OK:
                return 200;
            case BAD_REQUEST:
                return 400;
            case NOT_FOUND:
                return 404;
            case SERVER_ERROR:
                return 500;
            case NOT_MODIFIED:
                return 304;
            case TIMEOUT:
                return 408;
            default:
                return 500;
        }
    }

    /**
     * Converts the status code into a string that is ready for the http status line
     * @return a string containing the integer status code and the name of the code (CODE NAME)
     */
    @Override
    public String toString() {

        String codeString = HTTP_VERION_STRING +  " " + Integer.toString(this.toInt());

        switch(this){
            case OK:
                return codeString + " " + OK_STRING;
            case BAD_REQUEST:
                return codeString + " " + BAD_REQUEST_STRING;
            case NOT_FOUND:
                return codeString + " " + NOT_FOUND_STRING;
            case SERVER_ERROR:
                return codeString + " " + SERVER_ERROR_STRING;
            case NOT_MODIFIED:
                return codeString + " " + NOT_MODIFIED_STRING;
            case TIMEOUT:
                return codeString + " " + TIMEOUT_STRING;
            default:
                //wont happen
                return "";
        }
    }

    private final static String OK_STRING = "OK";
    private final static String BAD_REQUEST_STRING = "Bad Request " ;
    private final static String NOT_FOUND_STRING = "Not Found";
    private final static String SERVER_ERROR_STRING= "Internal Server Error";
    private final static String NOT_MODIFIED_STRING = "Not Modified";
    private final static String TIMEOUT_STRING = "Request Timeout";

    protected final static String HTTP_VERION_STRING = "HTTP/1.1";
}
