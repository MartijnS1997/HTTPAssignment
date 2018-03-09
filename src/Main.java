import java.util.Arrays;

/**
 * Created by Martijn on 9/03/2018.
 */
public class Main {

    public static void main(String args[]){
        //System.out.println(Arrays.toString(args));
        String command = convertArgsToCommand(args);
        //System.out.println(command);
        //initialize the client:
        ClientCommandLine commandLine = new ClientCommandLine();
        commandLine.commandLoop(command);
    }

    private static String convertArgsToCommand(String args[]){
        StringBuilder builder = new StringBuilder();
        for(String argument: args){
            builder.append(argument);
            builder.append(" ");
        }
        builder.deleteCharAt(builder.length()-1);
        return builder.toString();
    }
}
