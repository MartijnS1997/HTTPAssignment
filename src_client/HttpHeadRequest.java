import java.io.DataInputStream;
import java.io.PrintWriter;
import java.net.URL;

/**
 * Created by Martijn on 8/03/2018.
 */
public class HttpHeadRequest extends HttpRequest {


    public HttpHeadRequest(URL url) {
        super(url);
    }

    @Override
    public String execute(PrintWriter outputWriter, DataInputStream inputReader) {
        URL url = this.getUrl();
        String host = url.getHost();
        String path = url.getPath();
        String requestMessage[] = buildHeadRequest(host, path);

        //send the newly created message
        sendRequestHeader(requestMessage, outputWriter);

        //read the input from the stream
        return receiveResponse(inputStream);

    }


    private String receiveResponse(DataInputStream inputStream){
        //initialize the strings

        String response = null;
        //get the input stream reader
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        //initialize the builder
        StringBuilder responseBodyBuilder = new StringBuilder(reader);

        while((line = reader.readLine)!=null) {
            //append the line
            responseBodyBuilder.append(line);
            //also add newline feed
            responseBodyBuilder.append("\n");
            }


        response = responseBodyBuilder.toString();
        this.saveHtmlPage(response, "HeadResult");

        return ;
    }


    private static String[] buildHeadRequest(String host, String path){
        if(path.equals("")){
            path = "/";
        }
        //generate he string array
        String request[] = new String[NB_LINES_IN_COMMAND];
        //generate the first line
        request[0] = GET + " " + path + " " + HTTP_VERSION;
        //also add the host
        request[1] = HOST + host;
        //request to keep the connection alive
        request[2] = KEEP_CONNECTION_ALIVE;

        return request;
    }
}
