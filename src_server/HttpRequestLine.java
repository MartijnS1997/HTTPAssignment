import java.nio.file.Path;

/**
 * Created by Martijn on 10/03/2018.
 */
public interface HttpRequestLine {

    /**
     * Getter for the request method
     * @return the request method used by the request line
     */
    HttpRequestMethod getMethod();

    /**
     * getter for the path issued by the request line
     * @return the path
     */
    Path getPath();

    /**
     *Getter for the http version used by the request (we'll only support 1.1)
     */
    String getHttpVersion();

    /**
     * Flag that indicates if we need to listen for an additional message body
     * @return true if and only if the request method is POST or PUT
     */
    boolean hasRequestMessageBody();
}
