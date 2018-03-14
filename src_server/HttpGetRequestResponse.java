import java.io.*;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Martijn on 11/03/2018.
 * a class of get request responses, handles the get request responses for the server
 */
public class HttpGetRequestResponse extends HttpTransferRequestResponse {
    /**
     * Constructor for a http get response
     * @param serverPath the requested filepath
     * @param header the header, needed to retrieve info about the if-modified-since
     */
    public HttpGetRequestResponse(Path serverPath, ServerFileSystem fileSystem, HttpRequestHeader header) {
        super(serverPath, fileSystem);
        this.header = header;

    }

    @Override
    public void sendResponse(PrintWriter writer) {
        System.out.println("Start sending get response");
        //first check if the message contains the if modified since
        if(isModifiedSince()){
            System.out.println("send modified since response");
            sendModifiedResponse(writer);
        }else{
            sendNotModifiedResponse(writer);
        }


    }

    /**
     * Send a message to the client that the file hasn't been modified since the requested date
     * @param writer the writer used to send the message
     */
    private void sendNotModifiedResponse(PrintWriter writer){
        //checks if this doesn't work
        List<String> responseList = super.createResponseHeader(HttpStatusCode.NOT_MODIFIED);
        int responseSize = responseList.size();
        String responseLines[] = responseList.toArray(new String[responseSize]);
        writeToClient(writer, responseLines);
        //print empty line
        writer.println();
        writer.flush();
    }

    /**
     * Sends a message to the client with the requested file attached
     * @param writer the writer used to send the message
     */
    private void sendModifiedResponse(PrintWriter writer){
        try {
            String[] messageBody = getFileStringLines();
            setMessageBodyContent(messageBody);
            //we can only create the response header after the content is known
            List<String> headerList = this.createResponseHeader(HttpStatusCode.OK);
            int headerSize = headerList.size();
            String headerArray[] = headerList.toArray(new String[headerSize]);
            //now send the response
            this.writeToClient(writer, headerArray);
            //blank line in between
            writer.println();
            this.writeToClient(writer, messageBody);
            writer.flush();

        } catch (ServerFileSystemException | ServerException e) {
            sendError404Message(writer);


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

    @Override
    protected List<String> createResponseHeader(HttpStatusCode statusCode){
        List<String> standardHeader = super.createResponseHeader(statusCode);
        //add the content length
        //first get the current file system
        ServerFileSystem fileSystem = this.getFileSystem();
        //get the location of the file
        Path filePath = this.getServerPath();
        //get the size

        long contentLength = 0;
        try {
            contentLength = fileSystem.getFileSize(filePath);
            //java is a bit autistic an doesn't want us to add it to the signature
        } catch (ServerFileSystemException e) {
            //so we throw an unchecked exception
            throw new ServerException(HttpStatusCode.NOT_FOUND);
        }
        standardHeader.add(CONTENT_LENGTH_STRING + Long.toString(contentLength));
        //add the content type
        standardHeader.add(CONTENT_TYPE_STRING + CONTENT_TEXT_HTML_TYPE);

        return standardHeader;
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
    public String[] getMessageBodyContent() {
        return messageBodyContent;
    }

    /**
     * Setter for the message body of a request (see getter for info)
     * @param messageBodyContent the content to set
     */
    public void setMessageBodyContent(String[] messageBodyContent) {
        if(!canHaveAsMessageBody(messageBodyContent)){
            throw new ServerException(HttpStatusCode.SERVER_ERROR);
        }
        this.messageBodyContent = messageBodyContent;
    }

    /**
     * Checks if we can set the message body content with the specified content
     * @param messageBodyContent the content to be checked
     * @return true if the content is not a null pointer and the current content is a null reference (uninitialized)
     */
    private boolean canHaveAsMessageBody(String[] messageBodyContent){
        return messageBodyContent != null && this.getMessageBodyContent() == null;
    }

    /**
     * The request header received from the client
     */
    private HttpRequestHeader header;

    /**
     * The message body content, the content to be sent back to the client
     */
    private String[] messageBodyContent;
}
