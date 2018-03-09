import java.io.*;
import java.net.URL;

/**
 * Created by Martijn on 8/03/2018.
 *
 */
public class HttpGetRequest extends HttpRequest {

    public HttpGetRequest(URL url) {
        super(url);
        //System.out.println("Used url: " + url);
    }

    @Override
    public String execute(PrintWriter outputWriter, DataInputStream inputStream) {
        //first the host and the path from the given URl:
        URL url = this.getUrl();
        String host = url.getHost();
        String path = url.getPath();
        String requestMessage[] = buildGetRequest(host, path);

        //send the newly created message
        sendMessage(requestMessage, outputWriter);

        //read the input from the stream
        return receiveResponse(inputStream);
    }

    /**
     * Returns the response from the server as a string
     * @param inputStream the input stream used for receiving the input
     * @return a string containing the response from the server
     */
    private String receiveResponse(DataInputStream inputStream){
        StringBuilder builder = new StringBuilder(RESPONSE_SIZE);
        //get the input stream reader
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        //read from the stream while concatenating
        try {
            //if the line is null, terminate
            while((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return builder.toString();
    }

    private void sendMessage(String[] requestMessage, PrintWriter outputWriter){
        for(String requestString: requestMessage){
            System.out.println(requestString);
            outputWriter.println(requestString);
        }
        //then add empty line to finish the request
        outputWriter.println();
        outputWriter.flush();

        //we are finished
    }

    /**
     * Generates the http request, each entry in the array is another line to print
     * @param host the host for the Host part of the request
     * @param path the path used in the request
     * @return an array of string where each string is a single line in the command
     */
    private static String[] buildGetRequest(String host, String path){
        //generate he string array
        String request[] = new String[NB_LINES_IN_COMMAND];
        //generate the first line
        request[0] = GET + " " + path + " " + HTTP_VERSION;
        //also add the host
        request[1] = HOST + host;

        return request;
    }

    private final static int NB_LINES_IN_COMMAND = 2;
    private final static int RESPONSE_SIZE = 1000;
}
