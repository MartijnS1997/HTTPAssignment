import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static junit.framework.TestCase.assertEquals;

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

//    @Test
//    public void testMethod(){
//        System.out.println(HttpRequestMethod.GET.toString());
//        System.out.println(HttpRequestMethod.PUT.toString());
//        System.out.println(HttpRequestMethod.POST.toString());
//        System.out.println(HttpRequestMethod.HEAD.toString());
//
//
//    }

    @Test
    public void localHostTest() throws MalformedURLException, UnknownHostException {
        URL url = new URL("http://localhost");
        System.out.println(url);
        InetAddress address = InetAddress.getByName(url.getHost());
        System.out.println(address);

    }

    @Test
    public void serverTimeHTTP(){

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        System.out.println(dateFormat.format(calendar.getTime()));

    }
//
//    @Test
//    public void testCharCounter(){
//        String content[] = new String[]{"hey", "bob", "and", "ann"};
//        int nbLines = content.length;
//        Long nbChars = 0L;
//        try{
//            nbChars = LongStream.range(0, nbLines).map(l -> (content[Math.toIntExact(l)]).length()).sum();
//        } catch(ArithmeticException e){
//            throw new ServerException(HttpStatusCode.SERVER_ERROR);
//
//        }
//
//        assert(nbChars == 12L);
//    }

    @Test
    public void parsePlus(){
        String testString = "hello+world+!";
        String splitString[] = testString.split("\\+");
        System.out.println(Arrays.toString(splitString));
    }

    @Test
    public void testStringCleaner(){
        String stringArray[] = new String[] {"", "hey", "", "not empty"};
        List<String> cleanedLines = Arrays.stream(stringArray).filter(s -> !s.equals("")).collect(Collectors.toList());
        System.out.println(cleanedLines);
    }

    @Test
    public void testURLPath() throws MalformedURLException {
        String urlString = "https://www.google.com/maps/what_are_you_looking_at";
        URL url = new URL(urlString);
        System.out.println(url.getPath().substring(1));
    }

    @Test
    public void appendLines(){
        List<String> lines = new ArrayList<>();
        lines.add("hey");
        lines.add("all ok?");

        List<String> appendLines = new ArrayList<>();
        appendLines.add("yes");
        appendLines.add("I'm fine");

        lines.addAll(appendLines);
        System.out.println("lines: " + lines);

    }

    @Test
    public void characterLines(){
        List<String> stringList = new ArrayList<>();
        stringList.add("hey");
        stringList.add("everything okay?");
        stringList.add("yeeaaaaah");

        StringBuilder builder = new StringBuilder();
        for(String elem: stringList){
            builder.append(elem);
            builder.append("\r\n");
        }
        builder.append("\r\n");
        builder.append("\r\n");
        System.out.println(builder.toString());
//        builder.deleteCharAt(builder.length() - 1);
//        System.out.println("normal string length: " + builder.length());
//        byte byteString[] = builder.toString().getBytes();
//        System.out.println("byte string length: " + byteString.length);
        String[] lines = builder.toString().split("\n");
        System.out.println(Arrays.toString(lines));
        String[] cleaned = Arrays.stream(lines).filter(s -> !s.matches("\\R")&&!s.isEmpty()).collect(Collectors.toList()).toArray(new String[0]);
        System.out.println(Arrays.toString(cleaned));

    }
    @Test
    public void parseDate() throws ParseException {
        Date date = null;
        String dateValue  = "Tue, 27 Jan 2015 07:33:54 GMT";

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);

        date = dateFormat.parse(dateValue);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        System.out.println("date = " + dateFormat.format(date));
    }

}
