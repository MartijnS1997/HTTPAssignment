import java.io.DataInputStream;
import java.io.PrintWriter;
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
    public String execute(PrintWriter outputWriter, DataInputStream inputReader) {
        return null;
    }

    public String getMessageBody() {
        return messageBody;
    }

    private String messageBody;

}
