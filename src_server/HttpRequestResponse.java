import java.io.PrintWriter;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Martijn on 11/03/2018.
 * an abstract class of request responses, implements general methods needed in all response cases
 * and enables the server to handle responses in a generic way
 */
public abstract class HttpRequestResponse {
    /**
     * Constructor for a http request response
     * @param serverPath the path the response interacts with
     * @param fileSystem the file system used by the server for generating responses
     * note: the server path has the same structure as the url
     */
    public HttpRequestResponse(Path serverPath, ServerFileSystem fileSystem){
        this.serverPath = serverPath;
        this.fileSystem = fileSystem;
    }

    /**
     * Sends the request response to the client that is connected to the server
     * @param writer the writer used to send the response
     */
    public abstract void sendResponse(PrintWriter writer);

    /**
     * The current date (including time) in the http header format:
     * Day (name), day month(string) year hour:minutes:seconds GMT
     * @return a string containing the current time
     */
    protected static String getCurrentDateHttpFormat(){
        Calendar calendar = Calendar.getInstance();
        return convertToHttpDateFormat(new Timestamp((calendar.getTime()).getTime())); //get time returns the current time, second get time converts it to milliseconds
    }

    /**
     * Convert a timestamp to a string in the http date format
     * @param timestamp the timestamp to be converted
     * @return the converted timestamp in the http date format
     */
    protected static String convertToHttpDateFormat(Timestamp timestamp){
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(timestamp);
    }

    /**
     * Creates a standard header for an http response consists of
     * 1. Status code
     * 2. Current date
     * @param statusCode the status code to send
     * @return the beginning of a http response header with status code and current date
     * the extenders should fill in the other header elements
     */
    protected List<String> createResponseHeader(HttpStatusCode statusCode){
        List<String> responseHeader = new ArrayList<>();
        //create the lines of the response
        String statusLine = statusCode.toString();
        String dateString = DATE_STRING + getCurrentDateHttpFormat();

        responseHeader.add(statusLine);
        responseHeader.add(dateString);

        return responseHeader;
    }

    /**
     * Writes all the specified lines to the client
     * @param writer the writer to the client with
     * @param linesToWrite the lines to write to the client
     */
    protected void writeToClient(PrintWriter writer, String[] linesToWrite){
        for(String line: linesToWrite){
            writer.println(line);
        }
        writer.flush();
    }

    /**
     * Getter for the server path, the path used by the server for locating files that interact with the request
     * @return the path containing the file to interact with
     */
    protected Path getServerPath() {
        return serverPath;
    }

    /**
     * Getter for the file system used by the server that will be used in generating the
     * responses for the server
     * @return the file system used by the server
     */
    protected ServerFileSystem getFileSystem() {
        return fileSystem;
    }

    /**
     * The path the request needs to interact with
     */
    private Path serverPath;

    /**
     * The file system that will be used for accessing the data for the requests
     */
    private ServerFileSystem fileSystem;

    /**
     * Constants
     */
    private final static int RESPONSE_HEADER_ELEMENTS = 4;

    /**
     * Message strings
     */
    private final static String DATE_STRING = "Date: ";
    protected final static String CONTENT_TYPE_STRING = "Content-Type: ";
    protected final static String CONTENT_LENGTH_STRING = "Content-Lenght: ";
    protected final static String CONTENT_TEXT_HTML_TYPE = "text/html";
    protected final static String CONTENT_CHARSET_ISO = "; charset=iso-8859-1";
}
