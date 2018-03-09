import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;
import org.omg.CORBA.DataInputStream;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class imageRetrieverTest {

    ImageRetriever retriever;

    @Before
    public void setupMutableFixture() {
        retriever = new ImageRetriever();
    }

    @Test
    public void testImageRetriever() throws IOException {
        InetAddress address = InetAddress.getByName((new URL("https://www.symbolica.be")).getHost());
        Socket socket = new Socket(address, 80);

        // Create the input and output streams for the network socket.
        java.io.DataInputStream inputStream
                = new java.io.DataInputStream(socket.getInputStream());
        PrintWriter outStream
                = new PrintWriter(socket.getOutputStream(), true);

        outStream.println("GET / HTTP/1.1");
        outStream.println("Host: www.symbolica.be" );
        outStream.println();   // blank line separating header & body
        outStream.flush();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] byteChunk = new byte[4096];
        try {
            int nb_bytes = 0;
            while( (nb_bytes = inputStream.read(byteChunk)) >0 ){
                baos.write(byteChunk, 0, nb_bytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String siteContent = baos.toString();

        Elements images = ParseHTML.scanForEmbeddedImages(siteContent);
        ArrayList imagelist = ParseHTML.getImageLinkList(images);


        ImageRetriever.retrieveImages(imagelist, inputStream, outStream, "www.symbolica.be");

        inputStream.close();
        outStream.close();





    }

}
