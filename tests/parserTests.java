import org.junit.Before;
import org.junit.Test;

import java.net.URL;

/**
 * Created by Martijn on 8/03/2018.
 */
public class parserTests {

    ClientCommandParser parser;

    @Before
    public void setupMutableFixture() {
        parser = new ClientCommandParser();
    }

    @Test
    public void testUriConverter(){
        URL url =parser.convertToUrl("www.google.com");
        assert(url.toString().equals("http://www.google.com"));
    }

    @Test
    public void testParser(){
        ClientCommand clientCommand = parser.parseCommand("PUT www.google.be 80");
        assert(clientCommand.getCommandType().equals(HttpRequestCommand.PUT));
        assert(clientCommand.getUrl().toString().equals("http://www.google.be"));
        assert(clientCommand.getPort()==80);
        assert(clientCommand.needsMessageBody());
    }
}
