

import javax.xml.crypto.Data;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        InetAddress address = InetAddress.getByName((new URL("http://www.jmarshall.com")).getHost());
        Socket socket = new Socket(address, 80);
        // Create the input and output streams for the network socket.
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());

        PrintWriter out
                = new PrintWriter(socket.getOutputStream(), true);
        // Send request to the HTTP server.
        out.println("GET / HTTP/1.1\r\nHost: www.jmarshall.com");
        //out.println("Host: www.cafeaulait.org");
        out.println("Connection: Keep-Alive");
        out.println();   // blank line separating header & body
        out.flush();
        // Read the response and display on console.

        int lineCounter = 0;
        long contentLength = 20L;
        boolean gotFinalHeader = false;
        String[] header = parseHTTPHeaders(inputStream);
        System.out.println("Header: " + Arrays.toString(header));
        //start to read:
        byte[] normalBuffer = new byte[2548];
        int readSize;
        StringBuilder builder = new StringBuilder();
        //read the data from the connection
        while ((readSize = inputStream.read(normalBuffer)) > 0){
            System.out.println("reading input");
            String responseString = new String(normalBuffer, 0, readSize);
            System.out.println(responseString);
            System.out.println("response parsed: " + Arrays.toString(responseString.split("\\R")));
            System.out.println("Read bytes: " + readSize);
        }

        // Close the I/O streams.
        inputStream.close();
        out.close();
    }

    public static String[] parseHTTPHeaders(InputStream inputStream)
            throws IOException {
        int charRead;
        StringBuilder sb = new StringBuilder();
        while (true) {
            sb.append((char) (charRead = inputStream.read()));
            if ((char) charRead == '\r') {            // if we've got a '\r'
                sb.append((char) inputStream.read()); // then write '\n'
                charRead = inputStream.read();        // read the next char;
                if (charRead == '\r') {                  // if it's another '\r'
                    sb.append((char) inputStream.read());// write the '\n'
                    break;
                } else {
                    sb.append((char) charRead);
                }
            }
        }

        String[] headersArray = sb.toString().split("\r\n");
        List<String> headerList = new ArrayList<>(); //convert to a list such that we can concatenate if needed
        headerList.addAll(Arrays.asList(headersArray));
        //remove the empty lines
        for(String line: headerList){
            System.out.println(line.isEmpty());
        }
        headerList = headerList.stream().filter(s-> !s.matches("\\R")).collect(Collectors.toList());
        System.out.println(headerList);
        System.out.println(Arrays.toString((headerList.get(headerList.size() - 1).getBytes())));

        return headersArray;
    }
}
