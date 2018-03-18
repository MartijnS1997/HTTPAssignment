import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
        List<String> requestMessage = buildGetRequest(host, path);

        //send the newly created message
        sendRequestHeader(requestMessage, outputWriter);

        //read the input from the stream
        return receiveResponse(inputStream);
    }

    /**
     * Returns the response from the server as a string
     * @param inputStream the input stream used for receiving the input
     * @return a string containing the response from the server
     */
    private String receiveResponse(DataInputStream inputStream){
        //initialize the strings

        String responseBody = null;
        ClientResponseHeader responseHeader = null;
        //get the input stream reader
        //read from the stream while concatenating

        responseHeader = new ClientResponseHeader();
        //responseHeader.readForHeader(reader);
        responseHeader.readResponseHeader(inputStream);
        //check for errors
        if(responseHeader.hasErrorCode()){
            return responseHeader.handleErrorStatusCode(inputStream);
        }
        long messageContentLength = responseHeader.getContentLength();
        responseBody = HttpRequest.readResponseBodyBytes(inputStream, messageContentLength);//getResponseMessageBody(reader, messageContentLength);

        //Retrieve the embedded images from a site and save them to "imageCache"
        getEmbeddedImages(responseBody);

        return  responseHeader.toString() + "\n\n" + responseBody;
    }

    /**
     * Retrieves all the embedded images from the response
     * @param responseBody the response from the server (the html page to download from)
     */
    private void getEmbeddedImages(String responseBody) {
        Elements images = ParseHTML.scanForEmbeddedImages(responseBody);

        ArrayList imageList = ParseHTML.getImageLinkList(images);

        try {
            ImageRetriever.retrieveImages(imageList, getUrl().getHost());

        } catch (IOException e) {
            //Fault in imageRetriever
            e.printStackTrace();
        }
        replaceHtmlImageSources(responseBody);
    }

    /**
     * Replaces all the occurrences of image reference to the locally downloaded image
     * @param responseBody the string to analyze
     */
    private void replaceHtmlImageSources(String responseBody) {
        //TODO: append /HTTPAssignment/imageCache/cleanedFiles to filePath
        //TODO check if external URL, if so rename with the path
        Document doc = Jsoup.parse(responseBody);
        Elements imagesToReplace = doc.select("img");
        for(Element img: imagesToReplace){
            String source = img.attr("src");
            String newSource = "imageCache/cleanedFiles/"+ getRenamedSource(source);
            img.attr("src", newSource);
        }

        this.saveHtmlPage(doc.toString(), "GetResult");
    }
    private static String getRenamedSource(String source){
        try {
            URL sourceUrl = new URL(source);
            return sourceUrl.getPath().substring(1);
        } catch (MalformedURLException e) {
            //the case that the source isn't a url
            //just return the source
            return source;
        }
    }


//    /**
//     * Getter for the response body of the message, extracts the message body from the provided stream
//     * @param reader the reader used to read from the stream
//     * @return a list of strings containing the message body, each entry in the list represents
//     * a line of the message body
//     * @throws IOException
//     */
//    private String getResponseMessageBody(BufferedReader reader, long contentLength) throws IOException {
//        //initialize the builder
//        StringBuilder responseBodyBuilder = new StringBuilder(RESPONSE_SIZE);
//        //and the line to be read
//        String line;
//        //if we got the html tail, close the reader
//        boolean gotMessageTail = false;
//        System.out.println("contentLength: " + contentLength);
//        int nbLines = 0;
//        //the message size is the combined length of all the strings
//        //and the an extra for the string termination character
//        while(!gotMessageTail) {
//            line = reader.readLine();
//            //check if we have ended the line
//            if((line == null)){
//                gotMessageTail = true;
//            }
//            //count the lines
//            nbLines ++;
//
//            //System.out.println("line received: " +  line);
//            //append the line
//            responseBodyBuilder.append(line);
//            //also add newline feed
//            responseBodyBuilder.append("\n");
//            //check if the length of the builder equals the content length
//            //System.out.println("total response length: " + (responseBodyBuilder.length() + nbLines));
//            if(responseBodyBuilder.length() + nbLines >= contentLength){
//                //if so break the loop
//                gotMessageTail = true;
//            }
//        }
//
//        return responseBodyBuilder.toString();
//    }

    /**
     * Reads the response header from the reader and returns a string containing the response
     * @param reader the reader to read the input from
     * @return the list of strings containing the message header from the response (each string equals a line)
     * @throws IOException thrown if io went wrong
     */
    private List<String> getResponseHeader(BufferedReader reader) throws IOException {
        //initiate the string builder
        List<String> responseHeaderLines = new ArrayList<>();
        String line;
        //while we do not read a blank line we are building the header
        while(!(line = reader.readLine()).equals("")){
            //add the line
            responseHeaderLines.add(line);
        }

        return responseHeaderLines;
    }

    /**
     * extracts the content length from the header
     * @param responseHeader the response header to analyze
     * @return the content length value if present in the header, if not, return zero
     */
    private static long getContentLengthFromHeader(List<String> responseHeader){
        for(String line: responseHeader){
            //check if the header line is the content length
            if(line.startsWith(CONTENT_LENGTH)){
                //split the header field from the value
                String lineElems[] = line.split(" ");
                //extract the value (second part)
                return Long.parseLong(lineElems[1]);
            }
        }

        return 0L;
    }

    /**
     * Generates the http request, each entry in the array is another line to print
     * @param host the host for the Host part of the request
     * @param path the path used in the request
     * @return an array of string where each string is a single line in the command
     */
    private static List<String> buildGetRequest(String host, String path){
        if(path.equals("")){
            path = "/";
        }
        //generate he string array
        List<String> request= new ArrayList<>();
        //generate the first line
        request.add(GET + " " + path + " " + HTTP_VERSION);
        //also add the host
        request.add(HOST + host);
        //request to keep the connection alive
        request.add(KEEP_CONNECTION_ALIVE);

        return request;
    }

    private final static int NB_LINES_IN_COMMAND = 3;
    private final static int RESPONSE_SIZE = 1000;
}
