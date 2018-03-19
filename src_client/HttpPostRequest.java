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
        //encode the body contents to ascii
        byte byteMessageBody[] = convertLinesToByteArray(messageBody);
        this.messageBody = byteMessageBody;
    }

    @Override
    public String execute(DataOutputStream outputStream, DataInputStream inputReader) throws IOException {
        List<String> requestMessage = buildRequestHeader();

        //send the newly created message header
        sendRequestHeader(requestMessage, outputStream);

        //get the message body
        byte messageBody[] = this.getMessageBody();

//        send the message body
        sendMessageBody(outputStream, messageBody);

        //read the input from the stream
        try {
            return receiveResponse(inputReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return receiveResponse(inputReader);

    }

    private List<String> buildRequestHeader() {
        URL url = this.getUrl();
        String path = url.getPath();
        if(path.equals("")){
            path = "/";
        }
        //generate he string array
        List<String> request = new ArrayList<>();
        //generate the first line
        request.add(POST+ " " + path + " " + HTTP_VERSION);
        //also add the host
        request.add(HOST + url.getHost());
        request.add(CONTENT_TYPE + CONTENTT_TYPE_HTML_TXT);
        //add the content length
        //newlines were omitted while reading, add them back to the content
        long contentChars = this.getMessageBody().length;

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
    public byte[] getMessageBody() {
        return messageBody;
    }

    private byte[] messageBody;


}
