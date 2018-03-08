import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class ParseHTML {
    // A class using the JSoup library, to parse the incoming HTML file and retrieve embedded objects.
    public void ParseHTML(){
    }
    public Elements scanForEmbeddedObjects(String htmlstring){
        Document doc = Jsoup.parse(htmlstring);
        Elements images = doc.select("img");
        return images;
    }
}
