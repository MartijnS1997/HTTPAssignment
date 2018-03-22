import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Martijn on 10/03/2018.
 * A class of request parsers used for parsing a request line in a http command
 */
//Todo add parser for the message part of the request if needed
public class HttpRequestParser {

    /*
    Request line parser methods
     */

    /**
     * Parses a request line into a http request line object
     * @param requestLine the request line to be parsed eg "GET /foo.bar HTTP/1.1"
     * @return a HttpRequestLine object containing all the needed information about the request
     * @throws ServerException thrown if an error occurred during parsing
     */
    public static HttpRequestLine parseRequestLine(String requestLine) throws ServerException {
        //first split up the string in parts for parsing
        String requestElems[] = requestLine.split(" ");
        HttpRequestMethod method = parseMethod(requestElems[0]);
        Path path = parsePath(requestElems[1]);
        String HttpVersion ;

        if (isValidHttpVersion(requestElems[2])){
            HttpVersion = requestElems[2];
        }
        else {
            //if not the right http version throw error, we may add support for http 1.0 later on
            //for now throw the exception
            throw new ServerException("Unsupported http version");
        }

        boolean hasMessageBody = HttpRequestMethod.hasMessageBody(method);

        return new HttpRequestLine() {
            @Override
            public HttpRequestMethod getMethod() {
                return method;
            }

            @Override
            public Path getPath() {
                return path;
            }

            @Override
            public String getHttpVersion() {
                return HttpVersion;
            }

            @Override
            public boolean hasRequestMessageBody() {
                return hasMessageBody;
            }
        };
    }

    /**
     * Parses the method part of the request line
     * @param methodString the string containing the method
     * @return a HttpRequestMethod object containing the used method of the request
     * @throws ServerException thrown if the method was not GET, HEAD, POST or PUT
     */
    private static HttpRequestMethod parseMethod(String methodString) throws ServerException{
        switch (methodString){
            case GET:
                return HttpRequestMethod.GET;
            case HEAD:
                return HttpRequestMethod.HEAD;
            case POST:
                return HttpRequestMethod.POST;
            case PUT:
                return HttpRequestMethod.PUT;
            default:
                throw new ServerException(HttpStatusCode.BAD_REQUEST);
        }
    }

    /**
     * Parses the path provided by the request line
     * @param pathString the string containing the path
     * @return a path object containing the path
     */
    private static Path parsePath(String pathString){
        Path path = Paths.get(pathString);
        return path;
    }

    /**
     * Checks if the http version in the request line is a valid version
     * @param httpVersion the http version submitted by the client
     * @return true if and only if the http version equals http/1.1
     */
    private static boolean isValidHttpVersion(String httpVersion){
        //first get the lowercase version
        String lowercaseVersion = httpVersion.toLowerCase();
        return lowercaseVersion.equals(HTTP1_1);
    }

    private final static String GET = "GET";
    private final static String HEAD = "HEAD";
    private final static String POST = "POST";
    private final static String PUT = "PUT";
    private final static String HTTP1_1 = "http/1.1";

    /*
    Request header parser methods
     */

    /**
     * Parses the header returning an HttpRequestHeader containing the headers that were found and flags
     * to indicate if certain headers are active/were found
     * @param headers an array containing
     * @return
     */
    public static HttpRequestHeader parseRequestHeader(String[] headers){

        //by traversing all the components of the header and checking if certain parts appear we can safely
        //ignore all the headers we do not need to implement
        String host = null;
        Timestamp ifModifiedSince = null;
        long contentLength = 0;

        for(String headerLine: headers){
            //check if the headers contain a if modified since or a host
            if(isIfModifiedSince(headerLine)){
                ifModifiedSince = parseIfModifiedSinceHeader(headerLine);
            }
            if(isHost(headerLine)){
                host = parseHostHeader(headerLine);
            }
            if(isContentLength(headerLine)){
                contentLength = parseContentLengthHeader(headerLine);
            }
        }
        //declarations for autistic java, only wants final declarations (or effective finals) for its anonymous classes
        String sentHost = host;
        Timestamp sentIfModifiedSince = ifModifiedSince;
        long sentContentLength = contentLength;

        // return the found headers
        return new HttpRequestHeader() {
            @Override
            public String getHost() {
                //get the host found during the parsing
                return sentHost;
            }

            @Override
            public Timestamp getIfModifiedSince() {
                //get the if modified since during the parsing
                return sentIfModifiedSince;
            }

            @Override
            public boolean hasHostHeader() {
                //check if the host part was found during traversing the header
                return sentHost!=null;
            }

            @Override
            public boolean hasIfModifiedSince() {
                //check if the if-modified-since part was found during traversing the header
                return sentIfModifiedSince != null;
            }

            @Override
            public long getContentLength() {
                return sentContentLength;
            }
        };
    }

    /**
     * Checks if the provided string contains the if modified since http request header
     * @param modifiedSince the string to check
     * @return returns true if and only if the provided string starts with "If-Modified-Since: "
     */
    private static boolean isIfModifiedSince(String modifiedSince){
        return modifiedSince.startsWith(IF_MODIFIED_SINCE);
    }

    /**
     * Parses the if modified date to a timestamp containing the date for further processing
     * @param modifiedSinceDate the string to parse
     * @return a timestamp containing the provided date
     */
    private static Timestamp parseIfModifiedSinceHeader(String modifiedSinceDate){
        //first split the string in the header part and the date part, split on
        String modifiedSinceParts[] = modifiedSinceDate.split(" ");
        //we only need the parts after the first one, recombine the string
        StringBuilder builder = new StringBuilder(DATE_FORMAT.length());
        for(int i = 1; i != modifiedSinceParts.length; i++){
            //get each part and append it (only omitting the first element)
            builder.append(modifiedSinceParts[i]);
            builder.append(" ");
        }

        //delete the final space
        builder.deleteCharAt(builder.length() - 1);
        //create the string
        String modifiedSinceDateString = builder.toString();

        //now convert to a timestamp
        Timestamp timestamp = null;

        try {
            //the format of the last modified date
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
//            System.out.println(modifiedSinceDate);
            Date parsedDate = dateFormat.parse(modifiedSinceDateString);
            timestamp = new java.sql.Timestamp(parsedDate.getTime());
        } catch (ParseException e) {
            //the date was badly formatted throw a server exception?
            e.printStackTrace();
        }

        return timestamp;
    }

    /**
     * Checks if the provided string contains the Host http request header
     * @param host the string to check
     * @return true if and only if the provided string starts with "Host:"
     */
    private static boolean isHost(String host){
        return host.startsWith(HOST);
    }

    /**
     * Parses the host header into a string only containing the host address
     * @param hostHeader the host header to parse
     * @return a string containing the host address
     */
    private static String parseHostHeader(String hostHeader){
        //first split the string it its parts, the host and the address
        String hostHeaderParts[] = hostHeader.split(" ");
        //only take the host name part
        return hostHeaderParts[1];
    }

    private static boolean isContentLength(String contentLengthHeader){
        return contentLengthHeader.startsWith(CONTENT_LENGTH);
    }

    private static long parseContentLengthHeader(String  contentLengthHeader){
        //split on the space
        String contentLengthHeaderParts[] = contentLengthHeader.split(" ");
        return Long.parseLong(contentLengthHeaderParts[1]);
    }

    /*
    Constants used for parsing the header
     */

    private final static String IF_MODIFIED_SINCE = "If-Modified-Since: ";

    private final static String HOST = "Host: ";

    private final static String CONTENT_LENGTH = "Content-Length: ";

    private final static String DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
}
