import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Martijn on 8/03/2018.
 * a class of put requests
 * immutable semantics should be applied
 */
public class HttpPostRequest extends HttpRequest {

    public HttpPostRequest(URL url, String messageBody) {
        super(url);
        this.messageBody = messageBody;
    }

    @Override
    public void execute(PrintWriter outputWriter, BufferedReader inputReader) {

    }

    /**
     * Getter for the message body of the get request
     * @return the message body of the get request
     */
    public String getMessageBody() {
        return messageBody;
    }

    private String messageBody;

}
