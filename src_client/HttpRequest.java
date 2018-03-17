import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
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
