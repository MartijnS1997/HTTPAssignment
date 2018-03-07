import java.io.*;
import java.net.*;


/**
 * Created by Martijn on 7/03/2018.
 */
public class Client {

    public static void main(String args[]){
        Client client = new Client("http://example.com", 80);
        client.initConnection();
        client.executeCommand();
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
            InputStream inputStream = socket.getInputStream();
            //we use a buffer for now, we can read the incoming lines from the other side
            BufferedReader input = new BufferedReader(new InputStreamReader(inputStream));
            //we use an output stream at the moment, may change later on
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            setInputBuffer(input);
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

    public void executeCommand(){
        System.out.println("executing command");
        String command = "GET /index.html";
        DataOutputStream output = this.getOutputStream();
        BufferedReader reader = this.getInputBuffer();

        try {
            output.writeBytes(command);
            System.out.println("command issued");
            String inputLine = reader.readLine();
            System.out.println("waiting for response");
            System.out.println(inputLine);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    private BufferedReader getInputBuffer() {
        return inputBuffer;
    }

    private void setInputBuffer(BufferedReader inputBuffer) {
        this.inputBuffer = inputBuffer;
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
    private BufferedReader inputBuffer;


}
