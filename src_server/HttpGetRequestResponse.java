import java.io.*;
import java.nio.file.Path;
import java.sql.Timestamp;

/**
 * Created by Martijn on 11/03/2018.
 * a class of get request responses, handles the get request responses for the server
 */
public class HttpGetRequestResponse extends HttpTransferRequestResponse {
    /**
     * Constructor for a http get response
     * @param serverPath the requested file path
     * @param header the header, needed to retrieve info about the if-modified-since
     */
    public HttpGetRequestResponse(Path serverPath, ServerFileSystem fileSystem, HttpRequestHeader header) {
        super(serverPath, fileSystem);
        this.header = header;

    }

    @Override
    public void sendResponse(DataOutputStream outputStream) {
//        System.out.println("Start sending get response");
        //first check if the message contains the if modified since
        if(isModifiedSince()){
//            System.out.println("send modified since response");
            sendModifiedResponse(outputStream);
        }else{
            sendNotModifiedResponse(outputStream);
        }


    }

    /**
     * Send a message to the client that the file hasn't been modified since the requested date
     * @param outputStream the output stream to send the not modified response to
     */
    private void sendNotModifiedResponse(DataOutputStream outputStream){

        //create header
        ResponseHeader header = new ResponseHeader(HttpStatusCode.NOT_MODIFIED);

        //also give the last modified date
        ServerFileSystem fileSystem = this.getFileSystem();
        Path fileLocOnServer = this.getServerPath();

        header.setModifiedSince(fileSystem.getLastModifiedDate(fileLocOnServer));

        //write the header to the client
        header.writeResponseHeader(outputStream);
    }

    /**
     * Sends a message to the client with the requested file attached
     * @param outputStream stream used to write the message to
     */
    private void sendModifiedResponse(DataOutputStream outputStream){
        try {
            ServerFileSystem fileSystem = this.getFileSystem();
            Path fileLocOnServer = this.getServerPath();
            ReadOnlyServerFile messageBodyFile = new ReadOnlyServerFile(fileSystem, fileLocOnServer);
            setMessageBodyFile(messageBodyFile);
            //we can only create the response header after the content is known
            //create the header
            ResponseHeader header = new ResponseHeader(HttpStatusCode.OK);
            header.setContentLength(messageBodyFile.getFileSize());
            header.setContentType(CONTENT_TEXT_HTML_TYPE);

            //now send the response
            //write the header (takes care of the blank line)
            header.writeResponseHeader(outputStream);

            //send the file to the client
            messageBodyFile.writeFileToOutStream(outputStream);

            //System.out.println("messageWritten");

        } catch (ServerFileSystemException | ServerException e) {
            sendError404Message(outputStream);

        }
    }


    /**
     * Reads the text based document at the provided path in the server
     * @return an array of strings where each string is a separate line in the file
     * @throws ServerFileSystemException thrown if the file doesn't exist or couldn't be retrieved
     */
    private String[] getFileStringLines() throws ServerFileSystemException {
        ServerFileSystem fileSystem = this.getFileSystem();
        Path serverPath = this.getServerPath();
        String[] fileLines = fileSystem.readTextBasedFileLines(serverPath);
        return fileLines;
    }

    /**
     * Checks if the requested file is modified since the requested date
     * if there was no modified since date specified, returns true by default
     * @return true if the has modified since flag is inactive or if the last modified date
     *         of the file is larger than the modified since request date otherwise returns false
     */
    private boolean isModifiedSince(){
        HttpRequestHeader header = this.getHeader();
        //if it does not contain the if modified since we may just return true
        if(!header.hasIfModifiedSince()){
            return true;
        }
        Path serverPath = this.getServerPath();
        //if not we must check the date
        Timestamp fileTimestamp = this.getFileSystem().getLastModifiedDate(serverPath);
        Timestamp modifiedSinceTimeStamp = header.getIfModifiedSince();
        return fileTimestamp.after(modifiedSinceTimeStamp);

    }

    /**
     * Getter for the request header of the get request
     * @return a Http request header containing the necessary information about the get request
     */
    private HttpRequestHeader getHeader() {
        return header;
    }

    /**
     * Getter for the message body of the get request, the content that will be sent
     * back to the client if requested
     * @return the lines of the content to be sent back
     */
    public ReadOnlyServerFile getMessageBodyFile() {
        return messageBodyFile;
    }

    /**
     * Setter for the message body of a request (see getter for info)
     * @param messageBodyFile the content to set
     */
    public void setMessageBodyFile(ReadOnlyServerFile messageBodyFile) {
        if(!canHaveAsMessageBody(messageBodyFile)){
            throw new ServerException(HttpStatusCode.SERVER_ERROR);
        }
        this.messageBodyFile = messageBodyFile;
    }

    /**
     * Checks if we can set the message body content with the specified content
     * @param messageBodyContent the content to be checked
     * @return true if the content is not a null pointer and the current content is a null reference (uninitialized)
     */
    private boolean canHaveAsMessageBody(ReadOnlyServerFile messageBodyContent){
        return messageBodyContent != null && this.getMessageBodyFile() == null;
    }

    /**
     * The request header received from the client
     */
    private HttpRequestHeader header;

    /**
     * The message body content, the content to be sent back to the client
     */
    private ReadOnlyServerFile messageBodyFile;
}
