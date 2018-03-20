import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Martijn on 14/03/2018.
 * A class of read only files, the read only files are read from the server file system upon initialization
 * and cannot be changed after reading
 */
public class ReadOnlyServerFile {

    public ReadOnlyServerFile(ServerFileSystem fileSystem, Path locationOnServer) throws ServerFileSystemException {
        this.locationOnServer = locationOnServer;
        this.fileSystem = fileSystem;

        readFile();
    }

    /**
     * Reads the file from the filesystem of the server is only to be invoked at the start of the method
     * @throws ServerFileSystemException thrown if the file could not be located
     */
    private void readFile() throws ServerFileSystemException {
        Path path = this.getLocationOnServer();
        ServerFileSystem fileSystem = this.getFileSystem();


//        long fileSize = fileSystem.getFileSize(path);
//        this.fileSize = fileSize;

        //now read the file
        String file[] = fileSystem.readTextBasedFileLines(path);
        List<String> fileLines = Arrays.asList(file);
        byte[] fileLineBytes = convertLinesToBytes(fileLines);


        this.fileContents = fileLineBytes;
        //the file size is equal to the nb of bytes in the file content
        this.fileSize = fileLineBytes.length;
    }

    /**
     * Writes the contents of the file to the provided print writer
     * @param outputStream used to write the file contents to the client
     * note: flushes the data stream so other methods don't have to worry about it
     */
    public void writeFileToOutStream(DataOutputStream outputStream){
        byte[] contents = this.getFileContents();
        try {
            outputStream.write(contents);
            outputStream.flush();
        } catch (IOException e) {
            throw new ServerException(HttpStatusCode.SERVER_ERROR);
        }

    }

    /**
     * converts the requested lines into an byte array
     * every line will be ended with a \r\n for good measure
     * @param lines the lines to convert
     * @return an array of bytes containing the original message but encoded in US_ASCII
     */
    private static byte[] convertLinesToBytes(List<String> lines){
        //first we need to build a complete string containing all the lines
        //a new line is \r\n
        StringBuilder builder = new StringBuilder();
        for(String line: lines){
            //append the line to the builder
            builder.append(line);
            //append \r\n
            builder.append("\r\n");
        }
        //build the string
        String lineString = builder.toString();
        return lineString.getBytes(StandardCharsets.US_ASCII);
    }

    /**
     * Getter for the file content represented in lines of strings
     * @return an list made of strings where each entry is one line in the content
     * warning: the resulting list implementation does not support appending new content!
     */
    public List<String> getFileContentLines(){
        //first get the bytes of the content
        byte[] fileContent = this.getFileContents();
        //then convert the bytes to a string
        String fileString = new String(fileContent,0, fileContent.length);
        //then split the string on any newline
        String[] lines = fileString.split("\\R");
        //then to list
        return Arrays.asList(lines);
    }

    /**
     * Getter for the size of the file, it is saved once after that it doesn't change for the lifetime of the object
     * it is never queried again
     * @return the file size
     */
    long getFileSize() {
        return fileSize;
    }

    /**
     * Getter for the contents of the file
     * the only files the server can handle are text based files so it is fine we return a list containing
     * a string for each line of text
     * @return a list of strings containing the contents of the file
     */
    byte[] getFileContents() {
        return fileContents;
    }

    /**
     * The location of the file on the server (within the virtual file system)
     * @return a path object containing the path of the file on the server
     */
    private Path getLocationOnServer() {
        return locationOnServer;
    }

    /**
     * Getter for the file system used by the server for saving files
     * @return the ServerFileSystem currently used by the server
     */
    private ServerFileSystem getFileSystem() {
        return fileSystem;
    }

    private long fileSize;

    /**
     * The contents of the file that are read once but never written
     */
    private byte[] fileContents;

    /**
     * The path containing the location of the file on the server
     */
    private Path locationOnServer;

    /**
     * The file system used by the server for serving its clients
     */
    private ServerFileSystem fileSystem;
}
