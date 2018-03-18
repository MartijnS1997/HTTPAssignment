import java.io.PrintWriter;
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


        long fileSize = fileSystem.getFileSize(path);
        this.fileSize = fileSize;

        //now read the file
        String file[] = fileSystem.readTextBasedFileLines(path);
        List<String> fileLines = Arrays.asList(file);



        this.fileContents = fileLines;
    }

    /**
     * Writes the contents of the file to the provided print writer
     * @param writer the writer used to write the file with to its destination
     */
    public void writeFile(PrintWriter writer){

        List<String> fileLines = this.getFileContents();
        for(String line: fileLines){
            //System.out.println("line: " + line);
            writer.println(line);
        }
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
    List<String> getFileContents() {
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
    private List<String> fileContents;

    /**
     * The path containing the location of the file on the server
     */
    private Path locationOnServer;

    /**
     * The file system used by the server for serving its clients
     */
    private ServerFileSystem fileSystem;
}
