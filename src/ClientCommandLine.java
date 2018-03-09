/**
 * Created by Martijn on 9/03/2018.
 * a class of client command lines which is able to scan for the commands, interpret them and
 * sending the requests
 */
public class ClientCommandLine {

    public ClientCommandLine(){
        //empty constructor
    }

    private Client client;
    private RequestFactory requestFactory;
    private ClientCommandParser parser;
}
