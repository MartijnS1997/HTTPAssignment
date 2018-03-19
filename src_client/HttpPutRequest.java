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
        byte[] messageBodyBytes = convertLinesToByteArray(messageBody);
        this.messageBody = messageBodyBytes;
    }

    @Override
    public String execute(DataOutputStream outputStream, DataInputStream inputReader) throws IOException {
        byte[] messageBody = this.getMessageBody();
        //get the header
        List<String> requestHeader = buildPutRequestHeader();

        //send the newly created header
        sendRequestHeader(requestHeader, outputStream);

        //System.out.println("Sending message body: ");

        sendMessageBody(outputStream, messageBody);
        outputStream.flush();
        //send the message
//        for(String messageLine: messageBody){
//            outputWriter.println(messageLine);
//        }
//        outputWriter.flush();

        //System.out.println("Message sent");

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
     * @return A list of strings where each string is a line to send
     */
    private List<String> buildPutRequestHeader() {
        URL url = this.getUrl();
        String path = url.getPath();
        if(path.equals("")){
            path = "/";
        }
        //generate he string array
        List<String> request = new ArrayList<>();
        //generate the first line
        request.add(PUT+ " " + path + " " + HTTP_VERSION);
        //also add the host
        request.add(HOST + url.getHost());
        //add the content type
        request.add(CONTENT_TYPE + CONTENTT_TYPE_HTML_TXT);
        //add the content length
        byte[] contentBytes = this.getMessageBody();
        //newlines were omitted while reading, add them back to the content
        long contentChars = contentBytes.length;
        //add the content length: the nb of bytes (or ascii chars)
        request.add(CONTENT_LENGTH + contentChars);

        return request;
    }

    private String receiveResponse(DataInputStream inputStream) throws IOException {
        //initialize the inputStream
        ClientResponseHeader header = new ClientResponseHeader();
        header.readResponseHeader(inputStream);
        String result = header.toString();

        if(header.hasErrorCode()){
            header.handleErrorStatusCode(inputStream);
        }

        this.saveHtmlPage(header.toString(), "PutResult");

        return header.toString() + "\n\n" ;
    }


    public byte[] getMessageBody() {
        return messageBody;
    }

    private byte[] messageBody;

}
