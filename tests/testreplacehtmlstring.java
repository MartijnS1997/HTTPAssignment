import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class testreplacehtmlstring {


    ClientCommandParser parser;

    @Before


    @Test
    public void testReplaceText() throws IOException {
        String workingDir = System.getProperty("user.dir");
        List<String> lineList = Files.readAllLines(Paths.get(workingDir, "GetResult.html"));
        StringBuilder builder = new StringBuilder();
        for (String i : lineList) {
            builder.append(i);
        }
        String html = builder.toString();
        Document doc = Jsoup.parse(html);
        Elements images = doc.select("img");
        for(Element img: images){
            String source = img.attr("src");
            String newSource = "imageCache/cleanedFiles/"+source;
            img.attr("src", newSource);


        }

        Elements imagestest = doc.select("img");
        for(Element img: imagestest){
            System.out.println(img.attr("src"));


        }

    }

}
