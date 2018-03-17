import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Martijn on 8/03/2018.
 * a class of http put requests
 * immutable semantics should be applied to this class
 */
public class HttpPutRequest extends HttpRequest {

    public HttpPutRequest(URL url, List<String> messageBody) {
        super(url);
        this.messageBody = messageBody;
    }

    @Override
    public String execute(PrintWriter outputWriter, DataInputStream inputReader) throws IOException {
        URL url = this.getUrl();
        String host = url.getHost();
        String path = url.getPath();
        List<String> messageBody = this.getMessageBody();
        //get the header
        List<String> requestHeader = buildPutRequestHeader(host, path);

        //send the newly created header
        sendRequestHeader(requestHeader, outputWriter);

        System.out.println("Sending message body: ");

        //send the message
        for(String messageLine: messageBody){
            outputWriter.println(messageLine);
        }
        outputWriter.flush();

        System.out.println("Message sent");

        //read the input from the stream
        try {
            return receiveResponse(inputReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return receiveResponse(inputReader);

    }

    /**
     * Creates a header for a put request
     * @param host the host to send to
     * @param path the path where the request will be placed
     * @return A list of strings where each string is a line to send
     */
    private List<String> buildPutRequestHeader(String host, String path) {
        if(path.equals("")){
            path = "/";
        }
        //generate he string array
        List<String> request = new ArrayList<>();
        //generate the first line
        request.add(PUT+ " " + path + " " + HTTP_VERSION);
        //also add the host
        request.add(HOST + host);
        //add the content type
        request.add(CONTENT_TYPE + CONTENTT_TYPE_HTML_TXT);
        //add the content length
        List<String> contentLines = this.getMessageBody();
        //newlines were omitted while reading, add them back to the content
        long contentChars = contentLines.size();
        for(String line: contentLines){
            contentChars+= line.length();
        }
        //add the content length: the nb of bytes (or ascii chars)
        request.add(CONTENT_LENGTH + contentChars);

        return request;
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
            System.out.println(line);
        }


        response = responseBodyBuilder.toString();
        this.saveHtmlPage(response, "PutResult");

        return response ;
    }


    public List<String> getMessageBody() {
        return messageBody;
    }

    private List<String> messageBody;

}
