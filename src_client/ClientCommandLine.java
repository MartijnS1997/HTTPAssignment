import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Martijn on 9/03/2018.
 * a class of client command lines which is able to scan for the commands, interpret them and
 * sending the requests
 */
//todo add catchers for client exceptions and print the issue (return to the get command)
public class ClientCommandLine {


    public ClientCommandLine(){
        this(System.out, System.in);
    }

    private ClientCommandLine(PrintStream consoleOutput, InputStream consoleInput){
        this.client = new Client();
        this.printStream = consoleOutput;
        //this.readStream = consoleInput;
        this.reader = new BufferedReader(new InputStreamReader(consoleInput));
    }

    /**
     * the main loop of the command line
     * @param args the arguments for the first command
     */
    public void commandLoop(String args){
        //the first time we run the loop the command is received by the initialization of the main loop
        //check if client finds some error and print it
        try{
            executeCommand(args);
        }catch(ClientException e){
            printException(e);
        }


        boolean activeSession = true;
        //check if we want to continue:
        try {
            activeSession = anotherCommand();

        }catch(ClientException e){
            printException(e);
        }
        //after that we're on our own to retrieve commands
        while(activeSession){
            //the client may find issues, catch the error messages and send them to the prompt
            try{
                //get the command
                String command = getCommand();
                //execute it
                executeCommand(command);
                //ask if we need a new one
                activeSession = anotherCommand();
            }catch(ClientException e){
                printException(e);
            }
        }
        terminate();
    }

    /**
     * Prints the given exception to the output stream
     * @param exception the exception to printy
     */
    private void printException(ClientException exception){
        PrintStream printStream = this.getPrintStream();
        printStream.println(exception.getErrorMessage());
        printStream.println();
    }

    /**
     * Queries if the user want to submit another request to the client
     * @return true if the user responds with "y" and false if with "n"
     */
    private boolean anotherCommand(){
        BufferedReader reader = this.getReader();/*new BufferedReader( new InputStreamReader(this.getReadStream()));*/
        PrintStream printer = this.getPrintStream();

        //send the query to the user:
        printer.println(CONTINUE_SESSION);
        try {String line;
            //check if the line is empty
            if((line = reader.readLine()).equals("")){
                //if so start function again
                return anotherCommand();
            }
            switch (line){
                case YES:
                    return true;
                case NO:
                    return false;
                default:
                    //in this case invalid input was issued, try again
                    printer.println(RETRY_CONTINUE);
                    //empty the line reader
                    return anotherCommand();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //we will never reach this part
        return false;
    }

    /**
     * Termination sequence for the command client
     */
    private void terminate(){
        PrintStream printer = this.getPrintStream();
        printer.println(GOODBYE);

        Client client = this.getClient();
        client.closeConnection();
    }

    private String getCommand(){
        //first create a scanner that reads input from the command line client
        BufferedReader reader = this.getReader(); /*new BufferedReader( new InputStreamReader(System.in));*/
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
     * one cycle does the following:
     * 1. parse the command
     * 2. (optional) request additional information for completion of the command
     * 3. generates a new request for the server
     * 4. sends the request
     * 5. receives the answer to the request
     * 6. prints the server response
     *
     * @param clientCommand the command created by the client
     */
    private void executeCommand(String clientCommand){
        //first parse the request:
        ClientCommand command = ClientCommandParser.parseCommand(clientCommand);

        //connect to the host
        connect(command);

        //initialize the message body, will remain empty if
        List<String> messageBody = new ArrayList<>();
        if(command.needsMessageBody()){
            messageBody = getMessageBody();
        }
        //then generate the request
        HttpRequest httpRequest = RequestFactory.createRequest(command, messageBody);
        //then file it to the client
        Client client = this.getClient();
        //get the response from the server
        String Response = null;
        try {
            Response = client.issueRequest(httpRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //print the response
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
            //System.out.println("Same host");
            return;
        }
        //System.out.println("Another host needs to be selected");

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
    private List<String> getMessageBody(){
        //first create a scanner that reads input from the command line client
        BufferedReader reader = this.getReader();/*new BufferedReader( new InputStreamReader(this.getReadStream()));*/
        PrintStream printer = this.getPrintStream();
        //send a friendly message to the user
        printer.println(ENTER_MESSAGE_BODY);
        printer.println(MESSAGE_INSTRUCTIONS);
        //read the input, do this until the
        boolean scanning = true;
        StringBuilder builder = new StringBuilder(BUILDER_CAPACITY);
        List<String> messageBody = new ArrayList<>();
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
                messageBody.add(line);
            } catch (IOException e) {
                //idk what happened
                e.printStackTrace();
            }
        }

        //check if the length is zero, if so throw error
        if(messageBody.size() == 0){
            throw new ClientException(EMPTY_MESSAGE_BODY);
        }
        //now build the string
        return  messageBody;
    }

    /**
     * Getter for the client used in the command line session
     * @return a client object containing the client used in this session
     */
    private Client getClient() {
        return client;
    }

    /**
     * Getter for the print stream, the stream where the messages are written to
     * @return a print stream object
     */
    private PrintStream getPrintStream() {
        return printStream;
    }

    /**
     * Getter for the reader of the input from the command line client
     * @return the reader used for reading the command line
     */
    private BufferedReader getReader() {
        return reader;
    }

    /**
     * The client associated with the current command line session
     */
    private Client client;

    /**
     * The output print stream used for communication with the user
     */
    private PrintStream printStream;

    /**
     * The input reader used for communication with the user
     */
    private BufferedReader reader;

    /*
    Messages
     */
    private final static String ENTER_MESSAGE_BODY ="Please enter http message body";
    private final static String MESSAGE_INSTRUCTIONS = "Escape is empty line";
    private final static String EMPTY_MESSAGE_BODY = "The provided message body was empty, please try again";
    private final static String CONTINUE_SESSION = "Do you want to submit another request? [y/n]";
    private final static String RETRY_CONTINUE = "Invalid input please try again";
    private final static String ENTER_COMMAND = "Please enter your next http request";
    private final static String GOODBYE = "closing down client";

    /*
    Constants
     */
    private final static int BUILDER_CAPACITY = 100;
    private final static String YES = "y";
    private final static String NO = "n";
}
