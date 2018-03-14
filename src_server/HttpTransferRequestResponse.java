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
     * Method for sending the 404 error message to the client
     * @param writer the writer used for writing the message
     */
    protected void sendError404Message(PrintWriter writer) {
        //todo send error message the file doesn't exist
        System.out.println("sending 404 message");
        //create the standard header, ready to be expanded
        List<String> error404Header = super.createResponseHeader(HttpStatusCode.NOT_FOUND);

//        String error404Page[] = getErrorPageBody();
        Path fileLocationOnServer = ERRROR_404_PAGE_PATH;
        ServerFileSystem fileSystem = this.getFileSystem();
        ReadOnlyServerFile error404File;

        try {
            //reads the file into memory, its contents wont change during its lifetime
            error404File = new ReadOnlyServerFile(fileSystem, fileLocationOnServer);
        } catch (ServerFileSystemException e) {
            //will actually never happen, otherwise throw server error
            throw new ServerException(HttpStatusCode.SERVER_ERROR);
        }


        long contentSize = error404File.getFileSize();
        error404Header.add(CONTENT_LENGTH_STRING + contentSize);
        error404Header.add(CONTENT_TYPE_STRING + CONTENT_TEXT_HTML_TYPE + CONTENT_CHARSET_ISO);
        error404Header.add(CONNECTION_CLOSE);

        String error404HeaderArray[] = error404Header.toArray(new String[error404Header.size()]);

        //send the response
        this.writeToClient(writer,error404HeaderArray);

        //write empty line
        writer.println();
        //send the page
        error404File.writeFile(writer);
//        for(String line: error404Page){
//            System.out.println(line);
//        }
//        writer.println();
        writer.flush();
    }

    private final static Path ERRROR_404_PAGE_PATH = Paths.get("/messagePages/Error404.html");
}
