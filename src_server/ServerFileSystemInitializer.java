import com.sun.security.ntlm.*;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Martijn on 12/03/2018.
 * A class to initialize the server file system
 */
public class ServerFileSystemInitializer {

    /**
     * Initializes all the files in the virtual memory of the server
     * @param fileSystem the file system used by the server
     */
    public static void initServerFiles(ServerFileSystem fileSystem){
        try{
        File file = new File(hostPath.toUri());
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while((line = reader.readLine()) != null){
            //format of the file: first the name of the page, then the uri leading to the page
            String lineParts[] = line.split(" ");
            //the first element is the resource
            String resource = "/" + lineParts[0];
            //get the read file from the resources
            Path filePath = Paths.get(resourcePath.toString(), resource);

            //the location on the server is located in the second part
            Path serverPath = Paths.get(lineParts[1]);

            transferFileToServer(fileSystem, filePath, serverPath);
        }
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * transfers one file to the server
     * @param fileSystem the file system used by the server
     * @param resourcePath the path to the resource we want to transfer
     * @param serverPath the path the file will be allocated on the server itself
     * @throws IOException
     */
    private static void transferFileToServer(ServerFileSystem fileSystem, Path resourcePath, Path serverPath) throws IOException {
        //open the file
        File resourceFile = new File(resourcePath.toUri());
        //create a reader for it
        BufferedReader reader = new BufferedReader(new FileReader(resourceFile));
        String line;
        List<String> fileLines = new ArrayList<>();
        //read the lines
        while((line = reader.readLine()) != null){
            fileLines.add(line);
        }

        //after the lines are read, convert the string list to an array
        String lineArray[] = fileLines.toArray(new String[0]);
        //write the file to the server
        fileSystem.writeTextBasedFile(serverPath, lineArray);
    }

    /**
     * The path that contains the file with the hosted pages
     */
    private final static Path hostPath = Paths.get(System.getProperty("user.dir") + "/resources/hostedPages.txt");

    /**
     * The path that leads to the resource folder
     */
    private final static Path resourcePath = Paths.get(System.getProperty("user.dir") + "/resources");
}


