import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Martijn on 8/03/2018.
 * a class of http put requests
 * immutable semantics should be applied to this class
 */
public class HttpPutRequest extends HttpRequest {

    public HttpPutRequest(URL url, String messageBody) {
        super(url);
        this.messageBody = messageBody;
    }

    @Override
    public void execute(PrintWriter outputWriter, BufferedReader inputReader) {

    }

    public String getMessageBody() {
        return messageBody;
    }

    private String messageBody;

}
