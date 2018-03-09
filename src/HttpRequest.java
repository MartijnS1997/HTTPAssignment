import java.io.DataInputStream;
import java.io.PrintWriter;
import java.net.URL;

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
    public abstract String execute(PrintWriter outputWriter, DataInputStream inputReader);

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
    protected final static String GET = "GET";
    protected final static String HEAD = "HEAD";
    protected final static String PUT = "PUT";
    protected final static String POST = "POST";

}
