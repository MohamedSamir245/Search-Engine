//TODO: remove stop words
//TODO: fix it to work after you changed the IndexerDB
package Query_Phrase_Processor;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import com.mongodb.client.FindIterable;
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
        String wordsOnly[];
        input = input.replaceAll(",|\\.|!|\\?|:|;|\\)|\\(|\\[|]|\\*&\\^%\\$|\'", "");
        input = input.replaceAll("/|\\\\|", "");
        input = input.replaceAll("©|»|-|\\{|}|=", "");
        String words__phrase[] = input.split("\"");
        if (input.indexOf("\"") != input.lastIndexOf("\"")) {
            if (input.startsWith("\"")) {
                Phrase = words__phrase[1];
                wordsOnly = words__phrase[2].split(" ");
            } else if (input.endsWith("\"")) {
                Phrase = words__phrase[1];
                wordsOnly = words__phrase[0].split(" ");
            } else {
                Phrase = words__phrase[1];
                tempWords = words__phrase[0];
                tempWords += words__phrase[2];
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

    public static Document getURLs(String[] words, Document indexerdoc, FindIterable<Document> phraseSearchingdoc) {

        ArrayList<String> links = new ArrayList<>();
        ArrayList<String> titles = new ArrayList<>();


        for (int i = 0; i < words.length - 1; i++) {

//            Document search = new Document(words[i]);


            ArrayList<Document> sub = (ArrayList<Document>) indexerdoc.get(words[i]);
            if (sub == null)
                continue;

            ArrayList<String> websites = new ArrayList<>();


            System.out.println(sub.size());

            for (Document document : sub) {
                websites.add((String) document.get("URL"));
                titles.add((String) document.get("Title"));

            }
//            Document result = IndexerCollection.find(search).first();

//            if (result != null) {
//                ArrayList<String> sub = (ArrayList<String>) result.get("URLs");
            links.addAll(websites);

//            }

        }

        if (!Objects.equals(words[words.length - 1], "")) {
            MongoCursor<Document> cursor = phraseSearchingdoc.iterator();

            System.out.println(words[words.length - 1]);

            try {
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    String html = (String) doc.get("PageBody");
                    String url = (String) doc.get("PageLink");
                    String title = (String) doc.get("PageTitle");

                    titles.add(title);
                    if (html.contains(words[words.length - 1])) {
                        links.add(url);
                    }
                }
            } finally {
                cursor.close();
            }
        }

        Document res = new Document("Links", links.stream().distinct().toArray(String[]::new));
        res.append("Titles", titles.stream().distinct().toArray(String[]::new));
        return res;

    }

    public static String generateSnippet(String url, String searchTerm) throws Exception {
//        System.out.println(searchTerm);
        String[] terms = searchTerm.split(" ");
//        for (String term : terms) {
//            System.out.println(term);
//        }
        org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
        String snippet = "";

        for (String term : terms) {
            Elements elements = doc.getElementsContainingOwnText(term);
            for (org.jsoup.nodes.Element element : elements) {
                String text = element.text();
                if (text.contains(term)) {
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
