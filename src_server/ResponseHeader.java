import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Martijn on 14/03/2018.
 */
public class ResponseHeader {

    public ResponseHeader(HttpStatusCode statusCode){
        //standard initialized for all responses
        this.responseLine = statusCode.toString();
        this.date = getCurrentTimestamp();
    }

    public List<String> getHeaderLines(){
        List<String> headerLines = new ArrayList<>();

        //add the standard included attributes
        //the response line
        headerLines.add(this.getResponseLine());
        //the current time
        String dateLine = DATE + convertToHttpDateFormat(this.getDate());
        headerLines.add(dateLine);

        //now add the "might be active" lines of the header
        if(this.getContentLength() > 0){
            String contentLengthLine = CONTENT_LENGTH + this.getContentLength();
            headerLines.add(contentLengthLine);
        }
        if(this.getContentType() != null){
            String contentTypeString = CONTENT_TYPE + this.getContentType();
            headerLines.add(contentTypeString);
        }
        if(this.getConnection() != null){
            String connectionString = CONNECTION + this.getConnection();
            headerLines.add(connectionString);
        }
        if(this.getModifiedSince() != null){
            String modifiedSinceString = MODIFIED_SINCE + convertToHttpDateFormat(this.getModifiedSince());
            headerLines.add(modifiedSinceString);
        }

        //finally add all the extras, we don't check anything, just add
        headerLines.addAll(this.getExtras());

        return headerLines;
    }

    /**
     * Writes the header to the specified printer
     * also adds the clear line to indicate the end of the header
     * @param writer the printer to write with (writes to the client)
     */
    public void writeResponseHeader(PrintWriter writer){
        List<String> headerLines = this.getHeaderLines();
        for(String headerLine: headerLines){
            writer.println(headerLine);
        }
        //print the final newline to indicate the start of the response
        writer.println();
    }


    /**
     * Gets the current timestamp at the time of the creation of the header
     * @return the timestamp of this time instance
     */
    private Timestamp getCurrentTimestamp(){
        Calendar calendar = Calendar.getInstance();
        return new Timestamp((calendar.getTime()).getTime()); //get time returns the current time, second get time converts it to milliseconds
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
     * Getter for the response line of the http response, this is the first line
     * to be sent in the response header as it informs the client the state of the server and its response
     * @return a string containing the response line
     */
    private String getResponseLine() {
        return responseLine;
    }

    /**
     * Getter for the date of the creation of the response
     * @return the creation date of the response
     */
    public Timestamp getDate() {
        return date;
    }

    /**
     * Getter for the length of the content sent in the message body of the response
     * @return the size of the content in bytes
     */
    private Long getContentLength() {
        return contentLength;
    }

    /**
     * Setter for the length of the content sent in the message body
     * @param contentLength the length of the content in long
     */
    public void setContentLength(Long contentLength) {
        this.contentLength = contentLength;
    }

    /**
     * Getter for the type of the content sent in the message body of the response
     * @return a string containing the content type
     */
    private String getContentType() {
        return contentType;
    }

    /**
     * Setter for the content type
     * @param contentType a string containing the content type
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Getter for the connection parameter
     * @return a string containing info for the connection
     */
    private String getConnection() {
        return connection;
    }

    /**
     * Setter for the connection information must be either
     * close or keep-alive
     * @param connection the connection configuration to be sent back by the server
     */
    public void setConnection(String connection) {
        this.connection = connection;
    }

    /**
     * Getter for the modified since timestamp
     * this timestamp indicates the last date the page was modified
     * @return a timestamp containing the last modified date
     */
    private Timestamp getModifiedSince() {
        return modifiedSince;
    }

    /**
     * Stter for the modified since timestamp
     * @param modifiedSince the date to set as the modified since
     */
    public void setModifiedSince(Timestamp modifiedSince) {
        this.modifiedSince = modifiedSince;
    }

    /**
     * Getter for the extra header lines contained withing the header
     * @return the list containing all the extra header parameters
     */
    private List<String> getExtras() {
        return extras;
    }

    /**
     * Adds one extra header line to be send to the client
     * @param extra the extra line to add
     */
    public void addExtra(String extra) {
        List<String> extras = this.getExtras();
        extras.add(extra);
    }

    /**
     * Adds all the extra lines the the header
     * @param extras the extra lines to be added to the header
     */
    public void addExtras(List<String> extras){
        List<String> currExtras = this.getExtras();
        currExtras.addAll(extras);
    }

    /**
     * The string containing the response line
     */
    private String responseLine;

    private Timestamp date;

    private Long contentLength = -1L;

    private String contentType = null;

    private String connection = null;

    private Timestamp modifiedSince = null;

    private List<String> extras = new ArrayList<>();


    private final static String DATE = "Date: ";
    private final static String CONTENT_LENGTH = "Content-length: ";
    private final static String CONTENT_TYPE = "Content-Type: ";
    private final static String CONNECTION = "Connection: ";
    private final static String MODIFIED_SINCE = "Modified-Since: ";
}
