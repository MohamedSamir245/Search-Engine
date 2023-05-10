import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class test {

    public static String getTextFromElement(Element element) {
        StringBuilder sb = new StringBuilder();
        for (Element child : element.children()) {
            String text = getTextFromElement(child);
            if (!text.trim().isEmpty()) {
                if (sb.length() > 0 && !Character.isWhitespace(sb.charAt(sb.length() - 1)) && !Character.isWhitespace(text.charAt(0))) {
                    sb.append(" ");
                }
                sb.append(text);
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) throws IOException {

        try {
            URL codeforces = new URL("https://codeforces.com/");



            Document doc = Indexer.getWebsiteInfo(codeforces);

            Element body=doc.body();

            System.out.println(body.text().split(" ").length);


            Elements hTags=body.select("h1,h2,h3,h4,h5,h6");
            Elements pTags=body.select("p");

            System.out.println(hTags.text().split(" ").length);
            System.out.println(pTags.text().split(" ").length);


            hTags.remove();
            pTags.remove();


            System.out.println(body.text().split(" ").length);

//            String Text=getTextFromElement(body);


//            System.out.println(Text);
//            System.out.println(doc.body().text());

        }catch (IOException e)
        {
            e.printStackTrace();
        }

    }

}
