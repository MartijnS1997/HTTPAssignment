import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Martijn on 8/03/2018.
 *
 */
public class HttpGetRequest extends HttpRequest {

    public HttpGetRequest(URL url) {
        super(url);
    }

    @Override
    public void execute(PrintWriter outputWriter, BufferedReader inputReader) {

    }
}
