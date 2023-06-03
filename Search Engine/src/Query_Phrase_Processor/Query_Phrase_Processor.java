//TODO: remove stop words
//TODO: fix it to work after you changed the IndexerDB
package Query_Phrase_Processor;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import opennlp.tools.stemmer.snowball.SnowballStemmer;
import org.bson.Document;

import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


public class Query_Phrase_Processor {


    public static String[] getQueryWords(String input) {
        String Phrase = "", tempWords;
        String wordsOnly[] = new String[0];
        input = input.replaceAll(",|\\.|!|\\?|:|;|\\)|\\(|\\[|]|\\*&\\^%\\$|\'", "");
        input = input.replaceAll("/|\\\\|", "");
        input = input.replaceAll("©|»|-|\\{|}|=", "");
        String words__phrase[] = input.split("\"");
        if (input.indexOf("\"") != input.lastIndexOf("\"")) {
            if (input.startsWith("\"")) {
                Phrase = words__phrase[1];
                if (words__phrase.length > 2)
                    wordsOnly = words__phrase[2].split(" ");
                else
                {
                    tempWords="";
                }

            } else if (input.endsWith("\"")) {
                Phrase = words__phrase[1];
                wordsOnly = words__phrase[0].split(" ");
            } else {
                Phrase = words__phrase[1];
                tempWords = words__phrase[0];
                if (words__phrase.length > 2)
                    tempWords += words__phrase[2];
                else
                {
                    tempWords="";
                }
                wordsOnly = tempWords.split(" ");

            }
        } else {
            input = input.replaceAll("\"", "");
            wordsOnly = input.split(" ");
        }

        String[] newWordsOnly = Arrays.stream(wordsOnly).filter(x -> x != "").toArray(String[]::new);

        SnowballStemmer stemmer = new SnowballStemmer(ENGLISH);

        String finalWords[] = new String[newWordsOnly.length + 1];

        for (int i = 0; i < newWordsOnly.length + 1; i++) {
            if (i == newWordsOnly.length) {
                if (Phrase.length() != 0) {
                    finalWords[i] = Phrase;
                } else finalWords[i] = "";
//                    System.out.print(Phrase);
                continue;
            }
            finalWords[i] = (String) stemmer.stem(newWordsOnly[i]);

        }

        return Arrays.stream(finalWords).distinct().toArray(String[]::new);
    }

    public static Document getURLs(String[] words, MongoCollection<Document> IndexerCollection, FindIterable<Document> phraseSearchingdoc) {

        ArrayList<String> links = new ArrayList<>();
        ArrayList<String> titles = new ArrayList<>();
        ArrayList<String> importantWords = new ArrayList<>();






        for (int i = 0; i < words.length - 1; i++) {

            String importantWord="";


            Long frequency = 0L;

//            Document search = new Document(words[i]);

            Document indexerdoc= IndexerCollection.find(new Document("Word",words[i])).first();

            if(indexerdoc==null)
            {
                continue;
            }
            ArrayList<Document> sub = (ArrayList<Document>) indexerdoc.get("Data");
            if (sub == null)
                continue;

//            ArrayList<String> websites = new ArrayList<>();


            System.out.println(words[i]+" " + sub.size());


            for (Document document : sub) {
                if(!links.contains(document.get("URL")))
                {
                    links.add((String) document.get("URL"));
                    titles.add((String) document.get("Title"));
                    if(document.get("Freq")!=null)
                        frequency=(Long) document.get("Freq");
                    importantWords.add(words[i]);
//                    System.out.println(titles.get(titles.size()-1));


                }
                if(document.get("Freq") !=null&&(Long) document.get("Freq")>frequency)
                {

                    frequency=(Long) document.get("Freq");
                    importantWords.set(i, words[i]);
                }

//                websites.add((String) document.get("URL"));
//                titles.add((String) document.get("Title"));

            }
//            Document result = IndexerCollection.find(search).first();

//            if (result != null) {
//                ArrayList<String> sub = (ArrayList<String>) result.get("URLs");
//            links.addAll(websites);

//            }

        }

        if (!Objects.equals(words[words.length - 1], "")) {

//            System.out.println(words[words.length - 1]);

            try (MongoCursor<Document> cursor = phraseSearchingdoc.iterator()) {
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    String html = (String) doc.get("PageBody");
                    String url = (String) doc.get("PageLink");
                    String title = (String) doc.get("PageTitle");

                    if (html.contains(words[words.length - 1])) {
                        links.add(url);
                        titles.add(title);
                        importantWords.add(words[words.length-1]);

                    }
                }
            }
            catch (Exception e)
            {
                titles.add("");
            }
        }

        Document res = new Document("Links", links.toArray(String[]::new));
        res.append("Titles", titles.toArray(String[]::new));
        res.append("ImportantWords",importantWords);
        return res;

    }

    public static String generateSnippet(String url, String searchTerm,String query) throws Exception {

//        System.out.println("Search Term "+ searchTerm);
       String[] terms = getQueryWords(query);

//       for(String l:terms)
//       {
//           System.out.println("Term "+l);


//       }

        org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
        String snippet = "";


if(!Objects.equals(terms[terms.length - 1], ""))
{
    Elements elements = doc.getElementsContainingOwnText(terms[terms.length - 1]);
    for (org.jsoup.nodes.Element element : elements) {
        String text = element.text().toLowerCase();
//                System.out.println("Element: "+text);
        if (text.contains(terms[terms.length - 1].toLowerCase())) {
            snippet = text;
//                    System.out.println(snippet);
            break;
        }


    }
    if(!snippet.equals(""))
        return snippet;
}
//        System.out.println("dkjfsahkdjshaf"+searchTerm);




        for (String term : terms) {
            Elements elements = doc.getElementsContainingOwnText(term);
            for (org.jsoup.nodes.Element element : elements) {
                String text = element.text().toLowerCase();
//                System.out.println("Element: "+text);
                if (text.contains(term.toLowerCase())) {
                    snippet = text;
//                    System.out.println(snippet);
                    break;
                }


            }
            if (!snippet.equals("")) {
                break;
            }
        }

        return snippet;
    }

}
