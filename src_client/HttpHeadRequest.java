import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Martijn on 8/03/2018.
 */
public class HttpHeadRequest extends HttpRequest {


    public HttpHeadRequest(URL url) {
        super(url);
    }

    @Override
    public String execute(PrintWriter outputWriter, DataInputStream inputReader) throws IOException {
        URL url = this.getUrl();
        String host = url.getHost();
        String path = url.getPath();
        List<String> requestMessage = buildHeadRequest(host, path);

        //send the newly created message
        sendRequestHeader(requestMessage, outputWriter);

        //read the input from the stream
        try {
            return receiveResponse(inputReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return receiveResponse(inputReader);

    }


    private String receiveResponse(DataInputStream inputStream) throws IOException {
        //initialize the strings
        String response;
        //get the input stream reader
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        //initialize the builder
        StringBuilder responseBodyBuilder = new StringBuilder();


        while(!(line = reader.readLine()).equals("")) {
            //append the line
            responseBodyBuilder.append(line);
            //also add newline feed
            responseBodyBuilder.append("\n");
            }


        response = responseBodyBuilder.toString();
        this.saveHtmlPage(response, "HeadResult");

        return response ;
    }


    private static List<String> buildHeadRequest(String host, String path){
        if(path.equals("")){
            path = "/";
        }
        //generate he string array
        List<String> request = new ArrayList<>();
        //generate the first line
        request.add(HEAD + " " + path + " " + HTTP_VERSION);
        //also add the host
        request.add(HOST + host);
        //request to keep the connection alive
        request.add(KEEP_CONNECTION_ALIVE);

        return request;
    }
}
