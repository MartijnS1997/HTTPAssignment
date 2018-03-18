import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Martijn on 8/03/2018.
 * an abstract class of generic http requests, all other requests inherit from this specific class
 *
 */
public abstract class HttpRequest  {

    public HttpRequest(URL url){
        //get the url needed for the request
        this.setUrl(url);
    }

    /**
     * Converts a list of strings (representing the lines of the resulting string) to
     * a single string
     * @param lines the lines to concatenate
     * @return the string containing the lines
     */
    protected static String convertLinesToString(List<String> lines){
        StringBuilder builder = new StringBuilder();
        for(String line: lines){
            builder.append(line);
            //add linefeed
            builder.append("\n");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    /**
     * Calculates the amount of bytes the transfer will take
     * for ascii characters this means 1 character for each byte and 2 for each line (line feed  + carriage return)
     * @param contentLines the lines to calculate the file size from
     * @return the total amount of bytes the transmission will take
     */
    protected static long calcContentBytes(List<String> contentLines) {
        long contentChars = contentLines.size()*2 - 2; // each line contains 1 linefeed and one carriage return (minus final line)
        for(String line: contentLines){
            contentChars+= line.length();
        }
        //add the content length: the nb of bytes (or ascii chars)
        return contentChars;
    }

    /**
     * Executes the request with the given input writer and output reader
     * @param outputWriter the writer so write output to the server (eg our request)
     * @param inputReader reader for the input, we read incoming messages from this reader
     */
    public abstract String execute(PrintWriter outputWriter, DataInputStream inputReader) throws IOException;

    /**
     * Saves the file locally to the specified location
     * @param htmlString the string containing the html code
     * @param filename the file name
     */
    public void saveHtmlPage(String htmlString, String filename){
        try {
            String fileNameWithExtension = filename + ".html";
            Path printPath = Paths.get(HTML_SAVEPAGE, fileNameWithExtension);
            PrintWriter out = new PrintWriter(printPath.toString());
            out.print(htmlString);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends the request header to the server additional data like message body needs to be sent
     * by sendRequestMessageBody
     * @param requestHeader the request Header
     * @param outputWriter the writer to write the data with to the server
     */
    protected void sendRequestHeader(List<String> requestHeader, PrintWriter outputWriter){
        //write all the messages line for line to the server
        for(String requestLine: requestHeader){
            outputWriter.println(requestLine);
        }
        //then add empty line to finish the request
        outputWriter.println();
        outputWriter.flush();

        //we are finished
    }

    /**
     * Getter for the response body of the message, extracts the message body from the provided stream
     * @param reader the reader used to read from the stream
     * @return a list of strings containing the message body, each entry in the list represents
     * a line of the message body
     * @throws IOException
     */
    @Deprecated
    protected static String getResponseBody(BufferedReader reader, long contentLength) throws IOException {

        long totalContentRead = 0L;
        String line;
        List<String> linesRead = new ArrayList<>();
        boolean fileRead = false;
        while(!fileRead){
            //read the lines
            line = reader.readLine();
            //test for null
            if(line == null){
                fileRead = true;
                continue;
            }
            //if not store the line and add the read bytes to the read content
            linesRead.add(line);
            totalContentRead += line.length() + 1; //+1 for line feed
//            System.out.println(totalContentRead);
//            System.out.println(line);

            if(totalContentRead >= contentLength || line.toLowerCase().contains("</html>")){
                fileRead = true;
            }
        }

        String responseBody = convertLinesToString(linesRead);

        return responseBody;
    }

    /**
     * Read the response body for the specified number of bytes
     * @param inputStream the input stream to read from
     * @param contentLength the length of the content in bytes
     * @return a string containing the file, if the content length is zero returns an empty string
     */
    public static String readResponseBodyBytes(DataInputStream inputStream, long contentLength){
        //check the content length
        if(contentLength <= 0){
            return "";
        }
        //todo split buffer to be sure that it fits
        //create the buffer
        byte buffer[] = new byte[Math.toIntExact(contentLength)];

        try {
            //read for the buffer size
            int readBytes = inputStream.read(buffer);
            //generate the string containing the message body
            return new String(buffer, 0, readBytes);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Getter for the current URL
     * @return the current url used for the request
     */
    protected URL getUrl() {
        return url;
    }

    /**
     * Setter for the current Url used for the request
     * @param url
     */
    private void setUrl(URL url) {
        this.url = url;
    }

    private URL url;

    /*
    Constants
     */
    protected final static String HTTP_VERSION = "HTTP/1.1";
    protected final static String HOST = "Host: ";
    protected final static String KEEP_CONNECTION_ALIVE = "Connection: Keep-Alive";
    protected final static String CONTENT_LENGTH = "Content-Length: ";
    protected final static String CONTENT_TYPE = "Content-Type: ";
    protected final static String CONTENTT_TYPE_HTML_TXT = "text/html";
    protected final static String GET = "GET";
    protected final static String HEAD = "HEAD";
    protected final static String PUT = "PUT";
    protected final static String POST = "POST";
    private final static String HTML_SAVEPAGE = "RequestedPageCache";

}
