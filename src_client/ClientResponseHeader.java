import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Martijn on 18/03/2018.
 * A class of headers received by the client as a response for the server
 */
public class ClientResponseHeader {

    public ClientResponseHeader(){

    }

    /**
     * Converts the header into a string containing all the header lines
     * @return a string containing the response line and all the header lines
     */
    @Override
    public String toString(){
       return  HttpRequest.convertLinesToString(this.getHeaderLines());
    }

    /**
     * Read the input stream for a header and parses it
     * @param reader the reader to read the header from
     */
    @Deprecated
    public void readForHeader(BufferedReader reader){
        try {
            //read the response line
            String responseLine = reader.readLine();
            this.statusCode = parseResponseLine(responseLine);
            //initialize
            String line;
            List<String> readHeaderLines = new ArrayList<>();
            readHeaderLines.add(responseLine);
            while (!(line = reader.readLine()).equals("")) {
                readHeaderLines.add(line);
            }

            //then parse the header
            parseHeader(readHeaderLines);

            this.setHeaderLines(readHeaderLines);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Reads the response header from the specified input stream
     * @param inputStream the input stream to read from
     */
    public void readResponseHeader(DataInputStream inputStream){
        //first read the input stream for the header
        try {
            List<String> headerLines = readHeaderFromInput(inputStream);
            //then parse the first line
            String responseLine = headerLines.get(0);
            this.statusCode = parseResponseLine(responseLine);
            parseHeader(headerLines);
            this.setHeaderLines(headerLines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * read the header contents from the input stream
     * @param inputStream the input stream to read the headers from
     * @return a list of strings where each string is one line received
     * @throws IOException
     */
    private static List<String> readHeaderFromInput(InputStream inputStream)
            throws IOException {
        //the character used to buffer
        int charRead;
        //the builder where the header is built
        StringBuilder sb = new StringBuilder();
        while (true) {
            sb.append((char) (charRead = inputStream.read()));
            if ((char) charRead == '\r') {            // if we've got a '\r'
                sb.append((char) inputStream.read()); // then write '\n'
                charRead = inputStream.read();        // read the next char;
                if (charRead == '\r') {                  // if it's another '\r'
                    sb.append((char) inputStream.read());// write the '\n'
                    break;
                } else {
                    sb.append((char) charRead);
                }
            }
        }

        String[] headersArray = sb.toString().split("\r\n");
        List<String> headerList = new ArrayList<>(); //convert to a list such that we can concatenate if needed
        headerList.addAll(Arrays.asList(headersArray));
        //remove the empty lines (the final line only contains line feed)
        headerList = headerList.stream().filter(s-> !s.matches("\\R")).collect(Collectors.toList());

        return headerList;
    }


    /**
     * Parses the response line for the status code sent by the server
     * @param responseLine the response line to be parsed (the fist line of the response)
     * @return an integer representing the response code
     */
    private static int parseResponseLine(String responseLine){
        String responseElems[] = responseLine.split(" ");
        //the first part may be discarded
        //the second part is of interest, contains the status code
        String statusCode = responseElems[1];
        //parse the status code
        //System.out.println("Response line: " + responseLine);
        return Integer.parseInt(statusCode);
    }

    /**
     * parses the header lines given and sets the header object parameters right
     * eg sets the content length variable if content length is encountered
     * @param headerLines
     */
    private void parseHeader(List<String> headerLines){
        for(String line: headerLines){
            if(isContentLengthField(line)){
                this.contentLength = parseContentLengthField(line);
            }
            //add cases if needed
        }
    }

    /**
     * Checks if the provided string is the field of content length
     * @param headerLine the line to be checked
     * @return returns true if line starts with "Content-Length"
     */
    private boolean isContentLengthField(String headerLine){
        return headerLine.startsWith(HttpRequest.CONTENT_LENGTH);
    }

    /**
     * Parses the content length string to extract the length of the content provided in the message body
     * @param contentLengthString the string to parse
     * @return the length of the content in the message body
     */
    private static long parseContentLengthField(String contentLengthString){
        String contentLenghtElems[] = contentLengthString.split(" ");
        //only the second part is of interest
        String contentLengthValue = contentLenghtElems[1];
        return Long.parseLong(contentLengthValue);
    }

    /**
     * handler for the status codes produced by the request
     * @param reader the reader for the input
     * @return a string containing the input
     */
    @Deprecated
    public String handleErrorStatusCode(BufferedReader reader){
        //get if there is a body to be read
        long contentLength = this.getContentLength();
        String errorContent = "";
        if(this.hasContent()){
            //read the message body
            try {
                errorContent = HttpRequest.getResponseBody(reader, contentLength);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //return a string containing the content
        return this.toString() + "\n\n" + errorContent;
    }

    public String handleErrorStatusCode(DataInputStream inputStream){
        long contentLength = this.getContentLength();
        String errorContent = "";
        if(this.hasContent()){
            //read the message body if present
            errorContent = HttpRequest.readResponseBodyBytes(inputStream, contentLength);
        }

        //send the string
        return this.toString() + "\n\n" + errorContent;
    }

    /**
     * Returns true if and only if the status code within the header is not an 200 OK status code

     * @return true if and only if the header is a non 200 OK status code
     */
    public boolean hasErrorCode(){
        return this.getStatusCode() != ClientResponseHeader.OK_STATUSCODE;
    }

    /**
     * Getter for all the lines in the header
     * @return the list containing the lines of the header
     */
    private List<String> getHeaderLines() {
        return headerLines;
    }

    /**
     * Setter for the header lines
     * @param headerLines the lines to set
     */
    private void setHeaderLines(List<String> headerLines) {
        this.headerLines = headerLines;
    }

    /**
     * Getter for the status code of the header (eg 200, 404, ...)
     * @return the status code
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Getter for the content length field in the header (the length of the content in the message body)
     * @return the content length in long
     */
    public long getContentLength() {
        return contentLength;
    }

    /**
     * Checks if the header contains a content length header field
     * @return true if the content length in the header is > 0
     */
    public boolean hasContent(){
        return getContentLength() > 0;
    }

    /**
     * The list that contains all the lines of the header
     */
    private List<String> headerLines;

    /**
     * The status code of the current header
     */
    private int statusCode;

    /**
     * The length of the content specified in the header
     */
    private long contentLength;

    /**
     * The header lines
     */
    private final static String CONTENT_LENGTH = "Content-Length";

    public final static int OK_STATUSCODE = 200;
}
