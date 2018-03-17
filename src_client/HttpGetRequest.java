import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;

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

        String responseHeader = null;
        String responseBody = null;
        //get the input stream reader
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        //read from the stream while concatenating
        try {
            responseHeader = getResponseHeader(reader);
            responseBody = getResponseMessageBody(reader);
        } catch (IOException e) {
            //idk what happened better print the error
            e.printStackTrace();
        }



        //Retrieve the embedded images from a site and save them to "imageCache"
        Elements images = ParseHTML.scanForEmbeddedImages(responseBody);

        ArrayList imageList = ParseHTML.getImageLinkList(images);

        try {
            ImageRetriever.retrieveImages(imageList, getUrl().getHost());

        } catch (IOException e) {
            //Fault in imageRetriever
            e.printStackTrace();
        }
        //TODO: append /HTTPAssignment/imageCache/cleanedFiles to filePath

        Document doc = Jsoup.parse(responseBody);
        Elements imagesToReplace = doc.select("img");
        for(Element img: imagesToReplace){
            String source = img.attr("src");
            String newSource = "imageCache/cleanedFiles/"+source;
            img.attr("src", newSource);


        }

        this.saveHtmlPage(doc.toString(), "GetResult");
        return responseHeader + "\n" + responseBody;
    }

    private String getResponseMessageBody(BufferedReader reader) throws IOException {
        //initialize the builder
        StringBuilder responseBodyBuilder = new StringBuilder(RESPONSE_SIZE);
        //and the line to be read
        String line;
        //if we got the html tail, close the reader
        boolean gotHtmlTail = false;

        while(!gotHtmlTail) {
            line = reader.readLine();
            //append the line
            responseBodyBuilder.append(line);
            //also add newline feed
            responseBodyBuilder.append("\n");
            //check if we have received the html closing
            if((line.toLowerCase()).contains("</html>")||(line == null)){
                gotHtmlTail = true;
            }
        }

        return responseBodyBuilder.toString();
    }

    /**
     * Reads the response header from the reader and returns a string containing the response
     * @param reader the reader to read the input from
     * @return the string containing the message header from the response
     * @throws IOException thrown if io went wrong
     */
    private String getResponseHeader(BufferedReader reader) throws IOException {
        //initiate the string builder
        StringBuilder responseHeaderBuilder = new StringBuilder();
        String line;
        //while we do not read a blank line we are building the header
        while(!(line = reader.readLine()).equals("")){
            //add the line
            responseHeaderBuilder.append(line);
            //also add the newline feed
            responseHeaderBuilder.append("\n");
        }

        return responseHeaderBuilder.toString();
    }

    /**
     * Generates the http request, each entry in the array is another line to print
     * @param host the host for the Host part of the request
     * @param path the path used in the request
     * @return an array of string where each string is a single line in the command
     */
    private static String[] buildGetRequest(String host, String path){
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

    private final static int NB_LINES_IN_COMMAND = 3;
    private final static int RESPONSE_SIZE = 1000;
}
