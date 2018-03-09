import java.io.*;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Martijn on 9/03/2018.
 * a class of client command lines which is able to scan for the commands, interpret them and
 * sending the requests
 */
public class ClientCommandLine {


    public ClientCommandLine(){
        this(System.out, System.in);
    }

    public ClientCommandLine(PrintStream consoleOutput, InputStream consoleInput){
        this.client = new Client();
        this.printStream = consoleOutput;
        this.readStream = consoleInput;
    }

    /**
     * the main loop of the command line
     * @param args the arguments for the first command
     */
    public void commandLoop(String args){
        //the first time we run the loop the command is received by the initialization of the main loop
        executeCommand(args);
        //check if we want to continue:
        boolean activeSession = anotherCommand();
        //after that we're on our own to retrieve commands
        while(activeSession){
            //get the command
           String command = getCommand();
           //execute it
           executeCommand(command);
           //ask if we need a new one
           activeSession = anotherCommand();
        }
    }

    /**
     * Queries if the user want to submit another request to the client
     * @return
     */
    private boolean anotherCommand(){
        BufferedReader reader = new BufferedReader( new InputStreamReader(this.getReadStream()));
        PrintStream printer = this.getPrintStream();

        //send the query to the user:
        printer.println(CONTINUE_SESSION);
        try {
            String line = reader.readLine();
            switch (line){
                case YES:
                    return true;
                case NO:
                    return false;
                default:
                    //in this case invalid input was issued, try again
                    printer.println(RETRY_Y_N);
                    return anotherCommand();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //we will never reach this part
        return false;
    }

    private String getCommand(){
        //first create a scanner that reads input from the command line client
        BufferedReader reader = new BufferedReader( new InputStreamReader(System.in));
        PrintStream printer = this.getPrintStream();
        //send message to the user
        printer.println(ENTER_COMMAND);
        //then retrieve the command
        String command = "";
        try {
            command = reader.readLine();
        } catch (IOException e) {
            //this should not happen
            e.printStackTrace();
        }

        return command;
    }

    /**
     * A single execution cycle for a command
     * @param clientCommand the command created by the client
     * @return returns the response for the request
     */
    private void executeCommand(String clientCommand){
        //first parse the request:
        ClientCommand command = ClientCommandParser.parseCommand(clientCommand);

        //connect to the host
        connect(command);

        //initialize the message body, will remain empty if
        String messageBody = "";
        if(command.needsMessageBody()){
            messageBody = getMessageBody();
        }
        //then generate the request
        HttpRequest httpRequest = RequestFactory.createRequest(command, messageBody);
        //then file it to the client
        Client client = this.getClient();
        String Response = client.issueRequest(httpRequest);
        PrintStream printStream = this.getPrintStream();
        printStream.println(Response);
    }
    //TODO check also for same tcp

    /**
     * Connects the client with the server
     * @param command the command issued by the user
     */
    private void connect(ClientCommand command){
        //get the URL
        URL commandURL = command.getUrl();
        //get the connected client
        Client client = this.getClient();
        //check if we are still operating on the same host

        if(client.hasSameHost(commandURL)){
            //we are already connected, no issue
            return;
        }

        //if not we connect to the new host
        //by first closing our current connection
        try {
            client.closeConnection();
        }catch(NullPointerException e){
            //this was the first run, nothing bad happened
        }
        //and connecting to the new host
        client.initConnection(command);
        //now we're connected
    }

    /**
     * Method that retrieves the message body from the command line
     * @return the message body for the http client
     */
    private String getMessageBody(){
        //first create a scanner that reads input from the command line client
        BufferedReader reader = new BufferedReader( new InputStreamReader(this.getReadStream()));
        PrintStream printer = this.getPrintStream();
        //send a friendly message to the user
        printer.println(ENTER_MESSAGE_BODY);
        printer.println(MESSAGE_INSTRUCTIONS);
        //read the input, do this until the
        boolean scanning = true;
        StringBuilder builder = new StringBuilder(BUILDER_CAPACITY);

        //scan for the body
        while(scanning){
            try {
                String line = reader.readLine();
                //the scanning will stop if we have an empty line
                if(line.isEmpty()){
                    scanning=false;
                    continue;
                }
                //else add to the builder
                builder.append(line);
            } catch (IOException e) {
                //idk what happened
                e.printStackTrace();
            }
        }

        //check if the length is zero, if so throw error
        if(builder.length() == 0){
            throw new ClientException(EMPTY_MESSAGE_BODY);
        }
        //now build the string
        String messageBody = builder.toString();
        try {
            reader.close();
        } catch (IOException e) {
            //close the buffered reader?
            //does it also close the stream?
            e.printStackTrace();
        }

        return messageBody;
    }

    /**
     * Getter for the client used in the command line session
     * @return a client object containing the client used in this session
     */
    public Client getClient() {
        return client;
    }

    /**
     * Getter for the print stream, the stream where the messages are written to
     * @return a print stream object
     */
    public PrintStream getPrintStream() {
        return printStream;
    }

    /**
     * Getter for the input reader
     * @return the input reader used in the command line session
     */
    public InputStream getReadStream() {
        return readStream;
    }

    private Client client;
    private PrintStream printStream;
    private InputStream readStream;

    /*
    Messages
     */
    private final static String ENTER_MESSAGE_BODY ="Please enter http message body";
    private final static String MESSAGE_INSTRUCTIONS = "Escape is empty line";
    private final static String EMPTY_MESSAGE_BODY = "The provided message body was empty, please try again";
    private final static String CONTINUE_SESSION = "Do you want to submit another request? [y/n]";
    private final static String RETRY_Y_N = "Invalid input please try again";
    private final static String ENTER_COMMAND = "Please enter your next http request";

    /*
    Constants
     */
    private final static int BUILDER_CAPACITY = 100;
    private final static String YES = "y";
    private final static String NO = "n";
}
