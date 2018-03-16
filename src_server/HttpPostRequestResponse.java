import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Martijn on 11/03/2018.
 * a class of post request responses, handles the post request responses for the server
 */
public class HttpPostRequestResponse extends HttpTransferRequestResponse {

    public HttpPostRequestResponse(Path serverPath, ServerFileSystem fileSystem, String[] messageBody) {
        super(serverPath, fileSystem);
        //convert the array to a list
        List<String> messageLines = Arrays.asList(messageBody);
        this.messageBody = messageLines;
    }

    @Override
    public void sendResponse(PrintWriter writer) {

        try {
            //retrieve the specified file
            ServerFileSystem fileSystem = this.getFileSystem();
            Path locationOnServer = this.getServerPath();
            //read file
            appendFile(writer, fileSystem, locationOnServer);

        //the file doesn't exist on the file system
        } catch (ServerFileSystemException e) {
            this.sendError404Message(writer);
        }


    }

    /**
     * Appends the contents specified in the request message to the specified file on the server
     * @param writer the writer used to write the contents back
     * @param fileSystem the file system used to retrieve the file
     * @param locationOnServer the location of the file on thr server
     * @throws ServerFileSystemException
     */
    private void appendFile(PrintWriter writer, ServerFileSystem fileSystem, Path locationOnServer) throws ServerFileSystemException {
        ReadOnlyServerFile file = new ReadOnlyServerFile(fileSystem, locationOnServer);
        List<String> fileLines = file.getFileContents();
        //concatenate the message
        List<String> messageBody = this.getMessageBody();
        fileLines.addAll(messageBody);
        //write back to the server
        fileSystem.writeTextBasedFile(locationOnServer, fileLines.toArray(new String[fileLines.size()]));
        //write the response to the server
        ResponseHeader header = new ResponseHeader(HttpStatusCode.OK);
        header.writeResponseHeader(writer);

        Path outputFolder = this.getOutPutPath();
        Path outputFilePath = Paths.get(outputFolder.toString(), locationOnServer.getFileName().toString());
        File outputFile = new File(outputFilePath.toUri());
        outputFile.getParentFile().mkdirs();

        try {
            outputFile.createNewFile();
            PrintWriter fileWriter = new PrintWriter(new FileOutputStream(outputFile));
            for(String line: messageBody){
                fileWriter.println(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parses the message body of a post request, used for actions on a html page
     * @param messageBody the message body
     * @return a map containing the variables and their corresponding values as a key value map
     */
    private Map<String, String> parseMessageBody(String[] messageBody){
        List<String> cleanedLines = Arrays.stream(messageBody).filter(s -> !s.equals("")).collect(Collectors.toList());
        //check if it is a x-www-form-urlencoded
        if(cleanedLines.size() == 1){
            return parseUrlEncodedRequestMessageBody(cleanedLines.get(0));
        }

        //if not, we cannot handle it, server error
        throw new ServerException(HttpStatusCode.SERVER_ERROR);
    }


    /**
     * Parses a message body of a x-www-form-urlencoded request
     * @param messageBody the body string to parse
     * @return a map containing the variables and their values as strings
     *         the keys of the map are the variable names and the keys their corresponding values
     */
    private Map<String, String> parseUrlEncodedRequestMessageBody(String messageBody){
        //the different variables & values are separated by an &
        String variablesAndValues[] = messageBody.split("&");
        //then split the variables and their values store them in a map
        //with the variables as keys and values as values
        Map<String,String> variableMap = new HashMap<>();

        for(String varKeyValue: variablesAndValues){
            //split on the equals
            String[] keyValString = varKeyValue.split("=");
            String var = keyValString[0];
            String value = replacePlusWithSpace(keyValString[1]);

            //the key is the variable, the value the value of the variable
            variableMap.put(var, value);
        }

        //return the map
        return variableMap;
    }

    /**
     * Replaces all the occurrences plus chars in the string with spaces
     * @param string the string to replace the pluses from
     * @return the cleaned string (doesn't contain any pluses)
     */
    private String replacePlusWithSpace(String string) {
        //split the second string and replace the +'es with spaces
        StringBuilder stringBuilder = new StringBuilder();
        // the \\+ regex splits the string at the plusses
        for(String valueElem: string.split("\\+")){
            //append the separate elements
            stringBuilder.append(valueElem);
            //add a space
            stringBuilder.append(" ");
        }
        //remove the final space
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    /**
     * Getter for the message body used by the Put request
     * @return the message body issued by the post request
     */
    private List<String> getMessageBody() {
        return messageBody;
    }

    /**
     * String containing the message body that went along with the post request, it contains
     * information for the server on how to handle the request
     */
    private List<String> messageBody;
}
