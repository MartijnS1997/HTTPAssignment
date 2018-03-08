import java.io.*;
import java.net.*;
import java.util.Scanner;


/**
 * Created by Martijn on 7/03/2018.
 */
public class Client {

    public static void main(String args[]){
        Client client = new Client("http://example.com", 80);
        client.initConnection();
        try {
            client.executeCommand();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //Todo expand when http nits and grits are known
    public Client(String url, int tcpPort) {
        setAddress(url);
        setPort(tcpPort);
    }

    public void initConnection(){
        InetAddress ipAddress = this.getAddress();
        int tcpPort = this.getPort();
        Socket socket = null;
        //create the connection:
        try {
            socket = new Socket(ipAddress, tcpPort);
            //once we have sockets, init the data streams
            //InputStream inputStream = socket.getInputStream();
            //we use a buffer for now, we can read the incoming lines from the other side
            //DataInputStream dataInputStream = new DataInputStream(inputStream);
            //we use an output stream at the moment, may change later on
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            //setInputStream(dataInputStream);
            setOutputStream(outputStream);
            setSocket(socket);
        } catch (IOException e) {
            //I have no idea what happened
            e.printStackTrace();
        }

        System.out.println("connection established");

    }


    private void getCommand(){

    }

    public void executeCommand() throws IOException {
        System.out.println("executing command");
        String command = "GET http://example.com/index.html HTTP/1.1";
        DataOutputStream output = this.getOutputStream();
        //DataInputStream input = this.getInputStream();
        byte bytebuffer[] = new  byte[1];
        String content= null;
        try {
            InputStream inputStream = this.getSocket().getInputStream();
            DataInputStream input = new DataInputStream(inputStream);
            output.writeBytes(command);
            System.out.println("command issued");
            int len = input.read(bytebuffer);

            System.out.println("waiting for response");
            //System.out.println("Response length: " + inputData);
            System.out.println("response length: " + len);
        } catch (IOException e) {
            //nothing
        }

//
//        StringBuilder result = new StringBuilder();
//        URL url = null;
//        try {
//            url = new URL("http://example.com");
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        HttpURLConnection conn = null;
//        try {
//            conn = (HttpURLConnection) url.openConnection();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        conn.setRequestMethod("GET");
//        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//        String line;
//        while ((line = rd.readLine()) != null) {
//            result.append(line);
//        }
//        rd.close();
//        System.out.println(result.toString());

    }
    //int inputData = input.read(byteBuffer);
//            boolean isNull = false;
//            Scanner scanner = new Scanner(this.getSocket().getInputStream());
//            scanner.useDelimiter("\\Z");
//            content = scanner.next();
//        }catch ( Exception ex ) {
//            ex.printStackTrace();
//        }
//        System.out.println(content);
//            InputStream inputStream = this.getSocket().getInputStream();
//            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//            int c;
//            while((c = bufferedReader.read()) != -1){
//                System.out.print((char) c);
//            }
//            while(!isNull){
//
//                //character = input.readLine();
////                if(character == null){
////                    isNull = true;
////                }
//                System.out.println(character);
//            }

    private int getPort() {
        return port;
    }

    private void setPort(int port) {
        this.port = port;
    }

    private Socket getSocket() {
        return socket;
    }

    private void setSocket(Socket socket) {
        this.socket = socket;
    }

    private DataOutputStream getOutputStream() {
        return outputStream;
    }

    private void setOutputStream(DataOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    private DataInputStream getInputStream() {
        return inputStream;
    }

    private void setInputStream(DataInputStream inputStream) {
        this.inputStream = inputStream;
    }

    private InetAddress getAddress() {
        return address;
    }

    private void setAddress(String urlString) {
        InetAddress address = null;
        try {
            address = InetAddress.getByName((new URL(urlString)).getHost());
        } catch (UnknownHostException | MalformedURLException e) {
            //just leave it open for now, change if we get issues
            e.printStackTrace();
        }
        this.address = address;
    }

    /**
     * Variable that stores the ip adress of the website we want to visit
     */
    InetAddress address;

    /**
     * The variable that stores the TCP port used for communication with the server
     */
    private int port = 80;

    /**
     * The socket used in the connection with the server
     */
    private Socket socket;

    /**
     * Object that holds the stream that sends data to the server
     */
    private DataOutputStream outputStream;

    /**
     * Object that holds the stream that receives data from the server
     */
    private DataInputStream inputStream;


}
