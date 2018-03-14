import java.io.*;
import java.net.*;

/**
 * Created by Martijn on 7/03/2018.
 * A testclient only used for basic stuff
 */
public class TestClient {
    private TestClient(){

    }
    //another site to try: http://www.cafeaulait.org/course/week12/22.html
    public static void main(String args[]) throws IOException {
        //TestClient testClient = new TestClient();
        InetAddress address = InetAddress.getByName((new URL("http://www.cafeaulait.org")).getHost());
        Socket socket = new Socket(address, 80);
        // Create the input and output streams for the network socket.
        BufferedReader in
                = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        PrintWriter out
                = new PrintWriter(socket.getOutputStream(), true);
        // Send request to the HTTP server.
        out.println("GET /course/week12/22.html HTTP/1.1\r\nHost: www.cafeaulait.org");
        //out.println("Host: www.cafeaulait.org");
        //out.println("Connection: Keep-Alive");
        out.println();   // blank line separating header & body
        out.flush();
        // Read the response and display on console.
        String line;
        // readLine() returns null if server close the network socket.
        StringBuilder builder = new StringBuilder();
        int lineCounter = 0;
        boolean firstWhitePassed = false;
        boolean gotFinalHeader = false;
        while(!gotFinalHeader) {

            line = in.readLine();
            System.out.println(line);
            if (line.equals("") && !firstWhitePassed) {
                firstWhitePassed = true;
                System.out.println("Actual response: \n");
            }
            if(firstWhitePassed){
            builder.append(line);
            lineCounter ++;
            }
            if(line==null||line.toLowerCase().contains("</html>")){
                System.out.println("Line with HTML: " + line);
                lineCounter ++;
                gotFinalHeader = true;
            }
        }

        String response = builder.toString();
        System.out.println("Response length: " + (response.length() + lineCounter));
        // Close the I/O streams.
        in.close();
        out.close();
    }

}
