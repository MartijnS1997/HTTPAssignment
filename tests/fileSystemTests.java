import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Martijn on 10/03/2018.
 */
public class fileSystemTests {
    @Before
    public void setupMutableFixture(){

    }

    //testing zone for file systems (maybe do in other project so we don't screw this one up)
    @Test
    public void testPaths() throws URISyntaxException {
        Path path = Paths.get("C:\\Users\\Martijn\\Documents\\Univ\\test.txt");
        System.out.println("path filesystem: "+ path.getFileSystem());
        System.out.println("Path tostring: " + path.toString());
        System.out.println("file: " + path.getFileName());
        System.out.println("is absolute? " + path.isAbsolute());
        System.out.println("Normalized path: " + path.normalize());
        System.out.println("parent path: " + path.getParent());
        System.out.println("URI: " + path.toUri());
    }
}
