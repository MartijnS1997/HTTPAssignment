import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.LongStream;

/**
 * Created by Martijn on 14/03/2018.
 * an abstract class of transfer responses classes
 * provides methods for all request response classes
 */
public abstract class HttpTransferRequestResponse extends HttpRequestResponse {


    public static final String CONNECTION_CLOSE = "Connection: close";

    /**
     * Constructor for a http request response
     *
     * @param serverPath the path the response interacts with
     * @param fileSystem the file system used by the server for generating responses
     */
    public HttpTransferRequestResponse(Path serverPath, ServerFileSystem fileSystem) {
        super(serverPath, fileSystem);
    }


    /**
     * Get the total length of the content to be sent
     * @param content the content to be sent
     * @return the size of the content that will be sent
     */
    protected static long getContentSize(String[] content){
        int nbLines = content.length;
        Long nbChars = 0L;
        try{
            nbChars = LongStream.range(0, nbLines).map(l -> (content[Math.toIntExact(l)]).length()).sum();
        } catch(ArithmeticException e){
            throw new ServerException(HttpStatusCode.SERVER_ERROR);

        }

        return nbChars + (nbLines-1); // the chars, the nb of line feeds
    }

    /**
     * Retrieves the error page body, used for sending error 404 messages
     * @return the html file converted to strings ready to send to the client
     */
    private static String[] getErrorPageBody(){
        String errorPagePathString ="resources/Error404.html";
        Path errorPagePath = Paths.get(errorPagePathString);
        String errorPageBody[] = ServerConnection.readFileAtRelPath(errorPagePath);
        return errorPageBody;
    }

    /**
     * Method for sending the 404 error message to the client
     * @param writer the writer used for writing the message
     */
    protected void sendError404Message(PrintWriter writer) {
        //todo send error message the file doesn't exist
        System.out.println("sending 404 message");
        List<String> error404Header = super.createResponseHeader(HttpStatusCode.NOT_FOUND);

        //only keep the first two lines, the others are blank
        String error404Page[] = getErrorPageBody();
//        for(String string: error404Page){
//            System.out.println(string);
//        }

        long contentSize = getContentSize(error404Page);
        error404Header.add(CONTENT_LENGTH_STRING + contentSize);
        error404Header.add(CONTENT_TYPE_STRING + CONTENT_TEXT_HTML_TYPE + CONTENT_CHARSET_ISO);
        error404Header.add(CONNECTION_CLOSE);

        String error404HeaderArray[] = error404Header.toArray(new String[error404Header.size()]);

        //send the response
        this.writeToClient(writer,error404HeaderArray);
//        for(String line: error404HeaderArray){
//            System.out.println(line);
//        }
//        System.out.println();
        //write empty line
        writer.println();
        //send the page
        this.writeToClient(writer, error404Page);
        for(String line: error404Page){
            System.out.println(line);
        }
        writer.println();
        writer.flush();
    }
}
