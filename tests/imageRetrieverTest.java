import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.JUnit4;
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
    public void testImageSameHost() throws MalformedURLException {
        String siteHost = (new URL("http://ableshare.net")).getHost();
        String imageHost = (new URL("http://ableshare.net/data/img/facebook.png")).getHost();
        assert siteHost.equals(imageHost);
        }

    @Test
    public void testImageRetriever() throws IOException {
        String currentHost = (new URL("http://www.pics4learning.com")).getHost();
        InetAddress address = InetAddress.getByName(currentHost);
        Socket socket = new Socket(address, 80);

        // Create the input and output streams for the network socket.
        java.io.DataInputStream inputStream
                = new java.io.DataInputStream(socket.getInputStream());
        PrintWriter outStream
                = new PrintWriter(socket.getOutputStream(), true);


        outStream.println("GET / HTTP/1.1");
        outStream.println("Host: " +currentHost );
        outStream.println();   // blank line separating header & body
        outStream.flush();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] byteChunk = new byte[4096];
        try {
            int nb_bytes;
            while( (nb_bytes = inputStream.read(byteChunk)) >0 ){
                baos.write(byteChunk, 0, nb_bytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String siteContent = baos.toString();



        Elements images = ParseHTML.scanForEmbeddedImages(siteContent);
        ArrayList imagelist = ParseHTML.getImageLinkList(images);

        inputStream.close();

        outStream.close();

        socket.close();

        ImageRetriever.retrieveImages(imagelist, currentHost);









    }

    @Test
    public void testImageFromSYM() throws IOException {
        Socket socket = new Socket("www.symbolica.be", 80);


        PrintWriter bw = new PrintWriter(socket.getOutputStream());

        bw.println("GET /img/logodoor.png HTTP/1.1");
        bw.println("Host: www.symbolica.be");
        bw.println();
        bw.flush();

        java.io.DataInputStream in = new java.io.DataInputStream(socket.getInputStream());

        String workingDir = System.getProperty("user.dir");
        File file = new File(workingDir +"/imageCache/SymTest/test");
        file.getParentFile().mkdirs();
        file.createNewFile();
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));
        int count;
        byte[] buffer = new byte[8192];
        while ((count = in.read(buffer)) > 0)
        {
            dos.write(buffer, 0, count);
            dos.flush();
        }
        dos.close();
        System.out.println("image transfer done");

        socket.close();
    }

}
