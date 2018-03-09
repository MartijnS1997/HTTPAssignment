import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Martijn on 8/03/2018.
 */
public class HttpHeadRequest extends HttpRequest {


    public HttpHeadRequest(URL url) {
        super(url);
    }

    @Override
    public void execute(PrintWriter outputWriter, BufferedReader inputReader) {

    }
}
