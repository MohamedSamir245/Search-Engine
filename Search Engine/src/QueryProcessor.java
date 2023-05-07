//TODO: remove stop words
//TODO: fix it to work after you changed the IndexerDB

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

//import opennlp.tools.stemmer.PorterStemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;
import org.bson.Document;


public class QueryProcessor {
    public static void main(String[] args) throws IOException {
        //TODO take the query and assign it to (Query) variable

        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb+srv://Admin:admin@cluster0.srt79fu.mongodb.net/test"));

        MongoDatabase MongoDB = mongoClient.getDatabase("MongoDB");
        MongoCollection<Document> searchDB = MongoDB.getCollection("searchDB");
        MongoCollection<Document> resultDB = MongoDB.getCollection("resultDB");


//        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb+srv://Admin:admin@cluster0.srt79fu.mongodb.net/test"));
//        MongoDatabase MongoDB = mongoClient.getDatabase("MongoDB");
        MongoCollection<Document> IndexerCollection = MongoDB.getCollection("IndexerDB");
        Document indexerdoc = IndexerCollection.find().first();
        MongoCollection<Document> phraseSearchingCollection = MongoDB.getCollection("phraseSearchingDB");
        FindIterable<Document> phraseSearchingdoc = phraseSearchingCollection.find();


        while (true) {
            Document result = searchDB.find().first();
//
            if (result != null) {
                String Query = (String) result.get("Query");
//
                searchDB.deleteMany(result);
//
                System.out.println(Query);

//
//                String Query="enter";
                ArrayList<String> urls =new ArrayList<>(Arrays.asList( getURLs(getQueryWords(Query.toLowerCase()),indexerdoc,phraseSearchingdoc)));

                urls.forEach(System.out::println);


                Document r = new Document();
                r.append("Query",Query);
                r.append("URLs",urls);
//
//
                resultDB.insertOne(r);


            }
        }


//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Enter query");
//        String input = scanner.nextLine();

//        String[]urls=getURLs(getQueryWords(input));

//        for(int i=0;i<urls.length;i++)
//            System.out.println(urls[i]);

    }

    static String[] getQueryWords(String input) {
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

    static String[] getURLs(String[] words,Document indexerdoc,FindIterable<Document> phraseSearchingdoc) {

        ArrayList<String> links = new ArrayList<>();


        for (int i = 0; i < words.length - 1; i++) {

//            Document search = new Document(words[i]);



            ArrayList<Document> sub = (ArrayList<Document>)indexerdoc.get(words[i]);
            if(sub==null)
                continue;

            ArrayList<String>websites=new ArrayList<>();

            System.out.println(sub.size());

            for (Document document : sub) {
                websites.add((String) document.get("URL"));

            }
//            Document result = IndexerCollection.find(search).first();

//            if (result != null) {
//                ArrayList<String> sub = (ArrayList<String>) result.get("URLs");
                links.addAll(websites);

//            }

        }

        if (!Objects.equals(words[words.length - 1], "")) {
            MongoCursor<Document> cursor = phraseSearchingdoc.iterator();

            System.out.println(words[words.length-1]);

            try {
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    String html = (String) doc.get("PageBody");
                    String url = (String) doc.get("PageLink");

                    if (html.contains(words[words.length - 1])) {
                        links.add(url);
                    }
                }
            } finally {
                cursor.close();
            }
        }
        return links.stream().distinct().toArray(String[]::new);

    }

}
