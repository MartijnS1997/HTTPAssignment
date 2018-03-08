import java.io.*;
import java.net.*;

/**
 * Created by Martijn on 7/03/2018.
 */
public class TestClient {
    public TestClient(){

    }
    //another site to try: http://www.cafeaulait.org/course/week12/22.html
    public static void main(String args[]) throws IOException {
        TestClient testClient = new TestClient();
        InetAddress address = InetAddress.getByName((new URL("http://www.cafeaulait.org")).getHost());
        Socket socket = new Socket(address, 80);
        // Create the input and output streams for the network socket.
        BufferedReader in
                = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        PrintWriter out
                = new PrintWriter(socket.getOutputStream(), true);
        // Send request to the HTTP server.
        out.println("GET http://www.cafeaulait.org/course/week12/22.html HTTP/1.1\r\nHost: www.cafeaulait.org");
        //out.println("Host: www.cafeaulait.org");
        //out.println("Connection: Keep-Alive");
        out.println();   // blank line separating header & body
        out.flush();
        // Read the response and display on console.
        String line;
        // readLine() returns null if server close the network socket.
        while((line = in.readLine()) != null) {
            System.out.println(line);
        }
        // Close the I/O streams.
        in.close();
        out.close();
    }

}
