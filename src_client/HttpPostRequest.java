import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Martijn on 8/03/2018.
 * a class of post requests
 * immutable semantics should be applied
 */
public class HttpPostRequest extends HttpRequest {

    public HttpPostRequest(URL url, List<String> messageBody) {
        super(url);
        this.messageBody = messageBody;
    }

    @Override
    public String execute(PrintWriter outputWriter, DataInputStream inputReader) throws IOException {
        URL url = this.getUrl();
        String host = url.getHost();
        String path = url.getPath();
        List<String> requestMessage = buildPostRequest(host, path);

        //send the newly created message header
        sendRequestHeader(requestMessage, outputWriter);

        //get the message body
        List<String> messageBody = this.getMessageBody();

        //send the message body
        for(String messageLine: messageBody){
            outputWriter.println(messageLine);
        }
        outputWriter.flush();

        //read the input from the stream
        try {
            return receiveResponse(inputReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return receiveResponse(inputReader);

    }

    private List<String> buildPostRequest(String host, String path) {
        if(path.equals("")){
            path = "/";
        }
        //generate he string array
        List<String> request = new ArrayList<>();
        //generate the first line
        request.add(POST+ " " + path + " " + HTTP_VERSION);
        //also add the host
        request.add(HOST + host);
        request.add(CONTENT_TYPE + CONTENTT_TYPE_HTML_TXT);
        //add the content length
        List<String> contentLines = this.getMessageBody();
        //newlines were omitted while reading, add them back to the content
        long contentChars = calcContentBytes(contentLines);
        request.add(CONTENT_LENGTH + contentChars);


        return request;
    }

    private String receiveResponse(DataInputStream inputStream) throws IOException {

        ClientResponseHeader header = new ClientResponseHeader();
        header.readResponseHeader(inputStream);
        String resultString = header.toString();
        //check for error
        if(header.hasErrorCode()){
            resultString = header.handleErrorStatusCode(inputStream);
        }

//        response = responseBodyBuilder.toString();
        this.saveHtmlPage(resultString, "PostResult");

        return resultString + "\n\n";
    }

    /**
     * Get the list of strings containing the message body
     * @return
     */
    public List<String> getMessageBody() {
        return messageBody;
    }

    private List<String> messageBody;


}
