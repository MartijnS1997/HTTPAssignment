import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import sun.jvm.hotspot.utilities.BitMap;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * Created by Kobe on 08/03/2018.
 */
public class TestClient_Kobe {
    public TestClient_Kobe(){

    }

    //another site to try: http://www.cafeaulait.org/course/week12/22.html
    public static void main(String args[]) throws IOException {
        TestClient_Kobe testClient = new TestClient_Kobe();
        InetAddress address = InetAddress.getByName((new URL("https://www.symbolica.be")).getHost());
        System.out.println(address);
        Socket socket = new Socket(address, 80);

        // Create the input and output streams for the network socket.
        BufferedReader inStream
                = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        PrintWriter outStream
                = new PrintWriter(socket.getOutputStream(), true);


//        String line;
//        while((line = in.readLine()) != null) {
//           System.out.println(line);
//        }
        // Send request to the HTTP server.
        outStream.println("GET / HTTP/1.1");
        outStream.println("Host: www.symbolica.be" );
//        outStream.println("HOST: " + address);
//        System.out.println(inStream.readLine());
        //out.println("Connection: Keep-Alive");
        outStream.println();   // blank line separating header & body
        outStream.flush();
        // Read the response and display on console.
        String line;
        StringBuilder contentBuilder = new StringBuilder();
        // readLine() returns null if server close the network socket.
        while((line = inStream.readLine()) != null) {
//            System.out.println(line);
            contentBuilder.append(line);
        }

        //Turn the input stream into a single String object.
        String content = contentBuilder.toString();
//        System.out.println(content);
//        System.out.println();

        //Parse the html file for embedded images.
        Elements images = ParseHTML.scanForEmbeddedImages(content);
        ArrayList imagelist = ParseHTML.getImageLinkList(images);

        System.out.println(imagelist);

        System.out.println(imagelist.get(0));


        outStream.println("GET /" +imagelist.get(0) + " HTTP/1.1");
        outStream.println("Host: www.symbolica.be" );
        outStream.println();   // blank line separating header & body
        outStream.flush();






        //        System.out.println(images);

//        for (Iterator<Element> iter = images.iterator(); iter.hasNext(); ) {
//            Element elem = iter.next();
//            System.out.println(elem);
//            String lineElem;
//            StringBuilder elemLineBuilder = new StringBuilder();
//            elemLineBuilder.append(elem.toString());
//            String elemString = elemLineBuilder.toString();
//            String splittedElem = elemString.split("src=")[1];
//            splittedElem = splittedElem.split("\"")[1];
////            splittedElem = splittedElem.substring(1, splittedElem.length() - 1);
//            System.out.println("test " + splittedElem);
//
//
//            iter.remove();
//
//        }

        // Close the I/O streams.
        inStream.close();
        outStream.close();

    }

}
