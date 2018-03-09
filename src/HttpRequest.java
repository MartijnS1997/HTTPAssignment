import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
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

//    private String extractHost() {
//        URL url = this.getUrl();
//        String host = url.getHost();
//        return host;
//    }
//
//    private String extractPath(){
//        URL  url = this.getUrl();
//        return url.getPath();
//    }

    /**
     * Executes the request with the given input writer and output reader
     * @param outputWriter the writer so write output to the server (eg our request)
     * @param inputReader reader for the input, we read incoming messages from this reader
     */
    public abstract void execute(PrintWriter outputWriter, BufferedReader inputReader);

    /**
     * Getter for the current URL
     * @return the current url used for the request
     */
    private URL getUrl() {
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

}
