import org.junit.Before;
import org.junit.Test;

import java.net.URL;

/**
 * Created by Martijn on 8/03/2018.
 */
public class parserTests {

    CommandParser parser;

    @Before
    public void setupMutableFixture() {
        parser = new CommandParser();
    }

    @Test
    public void testUriConverter(){
        URL url =parser.convertToUrl("www.google.com");
        assert(url.toString().equals("http://www.google.com"));
    }

    @Test
    public void testParser(){
        Command command = parser.parseCommand("PUT www.google.be 80");
        assert(command.getCommandType().equals(HttpCommands.PUT));
        assert(command.getUrl().toString().equals("http://www.google.be"));
        assert(command.getPort()==80);
        assert(command.needsMessageBody());
    }
}
