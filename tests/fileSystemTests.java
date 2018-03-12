import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Martijn on 10/03/2018.
 */
public class fileSystemTests {
    ServerFileSystem serverFileSystem;
    @Before
    public void setupMutableFixture(){
        serverFileSystem = new ServerFileSystem();
    }

    //testing zone for file systems (maybe do in other project so we don't screw this one up)
    @Test
    public void testPaths() throws URISyntaxException {

        System.out.println(Paths.get(System.getProperty("user.dir") + "/resources/hostedPages.txt"));
        Path path = Paths.get("C:\\Users\\Martijn\\Documents\\GitHub\\HTTPAssignment\\tests\\stackOverflow.html");
        Path path2 = Paths.get("/tests/stackOverflow.html");
        Path path3 = Paths.get(System.getProperty("user.dir"));
        System.out.println(path3);
        System.out.println(path2.toAbsolutePath());
        System.out.println(path3.toString() + path2.toString());
        Path path4 = Paths.get(path3.toString() + path2.toString());
        System.out.println(path2.toAbsolutePath());
//        System.out.println("path filesystem: "+ path.getFileSystem());
//        System.out.println("Path tostring: " + path.toString());
//        System.out.println("file: " + path.getFileName());
//        System.out.println("is absolute? " + path.isAbsolute());
//        System.out.println("Normalized path: " + path.normalize());
//        System.out.println("parent path: " + path.getParent());
//        System.out.println("URI: " + path.toUri());
        File file = new File(path4.toUri());
        System.out.println(file.canRead());
    }

    @Test
    public void testFileSystem() throws ServerFileSystemException, IOException {

        //try your own paths
        String stackOverflowPathString = "C:\\Users\\Martijn\\Documents\\GitHub\\HTTPAssignment\\tests\\stackOverflow.html";
        String toledoPathString = "C:\\Users\\Martijn\\Documents\\GitHub\\HTTPAssignment\\tests\\toledo.html";
        String vtkPathString = "C:\\Users\\Martijn\\Documents\\GitHub\\HTTPAssignment\\tests\\vtk.html";
        String stackString = getString(stackOverflowPathString);
        String toledoString = getString(toledoPathString);
        String vtkString = getString(vtkPathString);
        Path stackPath = Paths.get("/foo/bar/StackOverflow.html");
        Path vtkPath = Paths.get("/burgie/test/vtk.html");
        Path toledoPath = Paths.get("/burgie/internet/toledo.html");

        serverFileSystem.writeTextBasedFile(stackPath, stackString);
        serverFileSystem.writeTextBasedFile(vtkPath, vtkString);
        serverFileSystem.writeTextBasedFile(toledoPath, toledoString);

        //now extract it again
        String[] fileString = serverFileSystem.readTextBasedFileLines(stackPath);
        for(String fileLine: fileString){
            System.out.println(fileLine);
        }

        serverFileSystem.terminate();

    }

    private String getString(String path) throws IOException {
        File file = new File(path);
        //read the file
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        while((line = reader.readLine()) != null ){
            stringBuilder.append(line);
            stringBuilder.append("\n");
        }
        reader.close();
        return stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();
    }
}
