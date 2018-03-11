import java.sql.Timestamp;

/**
 * Created by Martijn on 11/03/2018.
 * An interface of Http request headers only the following headers are supported
 * 1. Host
 * 2. If-modified-since
 */
public interface HttpRequestHeader {

    /**
     * Getter for the host address submitted in the header of request
     * @return a string containing the host address
     */
    String getHost();

    /**
     * Getter for the if modified since date in the header of the request
     * @return
     */
    Timestamp getIfModifiedSince();

    /**
     * Checks if the request header contains a host header
     * @return true if and only if the request header contains a host part
     */
    boolean hasHostHeader();

    /**
     * Checks if the request header contains an if modified since header
     * @return true if and only if the request header contains a if-modified-since part
     */
    boolean hasIfModifiedSince();

}
