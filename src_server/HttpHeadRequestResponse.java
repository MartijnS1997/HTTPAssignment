import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by Martijn on 11/03/2018.
 * a class of head request responses, handles the head request responses for the server
 */
public class HttpHeadRequestResponse extends HttpTransferRequestResponse{

    /**
     * Constructor for the head response
     * @param serverPath the path to interact with
     */
    public HttpHeadRequestResponse(Path serverPath, ServerFileSystem fileSystem) {
        super(serverPath, fileSystem);
    }

    @Override
    public void sendResponse(PrintWriter writer) {
        //first try to create the header the normal way
        try{
            List<String> header = createResponseHeader(HttpStatusCode.OK);
            int headerSize = header.size();
            String headerArray[] = header.toArray(new String[headerSize]);
            writeToClient(writer, headerArray);

        }catch(ServerException e){
            sendError404Message( writer);
        }
    }

    @Override
    protected List<String> createResponseHeader(HttpStatusCode statusCode) {
        List<String> baseResponse = super.createResponseHeader(statusCode);
        //set the content size
        try {
            ServerFileSystem fileSystem = this.getFileSystem();
            Path serverPath = this.getServerPath();
            long fileSize = fileSystem.getFileSize(serverPath);
            baseResponse.add(CONTENT_LENGTH_STRING + fileSize);
            //the content type
            baseResponse.add(CONTENT_TYPE_STRING + CONTENT_TEXT_HTML_TYPE);
        } catch (ServerFileSystemException e) {
            throw new ServerException(HttpStatusCode.NOT_FOUND);
        }


        return baseResponse;
    }
}
