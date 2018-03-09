import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Iterator;


public class ParseHTML {
    // A class using the JSoup library, to parse the incoming HTML file and retrieve a list of embedded images.



    // Crawls the html string and returns an "Elements" object containing all elements that are images.
    public static Elements scanForEmbeddedImages(String htmlstring){
        Document doc = Jsoup.parse(htmlstring);
        Elements images = doc.select("img");
        return images;
    }


    // Takes an "Element" object, and returns a list of all source link strings.
    public static ArrayList getImageLinkList(Elements embeddedImages){

        ArrayList<String> imageList = new ArrayList<String>();
        for (Iterator<Element> iter = embeddedImages.iterator(); iter.hasNext(); ) {
            Element elem = iter.next();
            StringBuilder elemLineBuilder = new StringBuilder();
            elemLineBuilder.append(elem.toString());
            String elemString = elemLineBuilder.toString();
            String splitElem = elemString.split("src=")[1];
            splitElem = splitElem.split("\"")[1];
            imageList.add(splitElem);

            iter.remove();

        }

        return imageList;
    }
}
