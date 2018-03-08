import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;

/**
 * Created by Martijn on 8/03/2018.
 *
 */
public class HttpGetRequest extends HttpRequest {


    public HttpGetRequest(String urlString) throws MalformedURLException {
        super(urlString);
    }

    @Override
    public void execute(PrintWriter outputWriter, BufferedReader inputReader) {

    }
}
