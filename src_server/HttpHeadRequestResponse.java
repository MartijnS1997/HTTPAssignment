import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.nio.file.Path;

/**
 * Created by Martijn on 11/03/2018.
 * a class of head request responses, handles the head request responses for the server
 */
//todo add the modified since header
public class HttpHeadRequestResponse extends HttpTransferRequestResponse{

    /**
     * Constructor for the head response
     * @param serverPath the path to interact with
     */
    public HttpHeadRequestResponse(Path serverPath, ServerFileSystem fileSystem) {
        super(serverPath, fileSystem);
    }

    @Override
    public void sendResponse(DataOutputStream outputStream) {
        //first try to create the header the normal way
        try{
            //get the file from the server
            ServerFileSystem fileSystem = this.getFileSystem();
            Path fileLocOnServer = this.getServerPath();
            ResponseHeader header = new ResponseHeader(HttpStatusCode.OK);
            //set the content length
            header.setContentLength(fileSystem.getFileSize(fileLocOnServer));
            //set the content type
            header.setContentType(CONTENT_TEXT_HTML_TYPE);
            //set the last modified date
            header.setModifiedSince(fileSystem.getLastModifiedDate(fileLocOnServer));
            //send the header
            header.writeResponseHeader(outputStream);

        }catch(ServerFileSystemException e){
            sendError404Message( outputStream);
        }
    }
}
