/**
 * Created by Martijn on 10/03/2018.
 */
public enum HttpRequestMethod {
    GET, HEAD, POST, PUT;

    /**
     * Checks if the request method should have a message body
     * @param method request method to check
     * @return true if and only if the method is PUT or POST
     */
    public static boolean hasMessageBody(HttpRequestMethod method){
        switch(method){
            case POST:
                return true;
            case PUT:
                return true;
            default:
                return false;
        }
    }


    @Override
    public String toString() {
        switch(this){
            case GET:
                return GET_STRING;
            case HEAD:
                return HEAD_STRING;
            case POST:
                return POST_STRING;
            case PUT:
                return PUT_STRING;
        }
        return "HttpRequestMethod{}";
    }

    private static String GET_STRING = "GET";
    private static String HEAD_STRING = "HEAD";
    private static String POST_STRING = "POST";
    private static String PUT_STRING = "PUT";
}
