/**
 * Created by Martijn on 9/03/2018.
 * A class of client exceptions, these exceptions are used for communication with he command line
 */
public class ClientException extends IllegalArgumentException {

    public ClientException(String message){
        this.errorMessage = message;
    }

    /**
     * Getter for the error message used for the command client
     * @return the error message to be read by the command line client
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * The error message used for communication with the command line client
     */
    private String errorMessage;
}
