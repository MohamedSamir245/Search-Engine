//TODO: remove stop words

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;


import opennlp.tools.stemmer.PorterStemmer;
import org.bson.Document;

import javax.print.Doc;


public class QueryProcessor {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter query");
        String input = scanner.nextLine();

        ArrayList<String> urls = new ArrayList<>();
        urls = getURLs(getQueryWords(input));

        urls.forEach(System.out::println);
    }

    static ArrayList<String> getQueryWords(String input) {
        PorterStemmer stemmer = new PorterStemmer();

        String[] Words = input.split(" ");

        for (int i = 0; i < Words.length; i++) {
            Words[i] = stemmer.stem(Words[i].toString());
        }

        var uniqeWords = Arrays.stream(Words).distinct().toArray();
        ArrayList<String> unique = new ArrayList<>();

        for (int i = 0; i < uniqeWords.length; i++) {
            unique.add(uniqeWords[i].toString());
        }

        return unique;
    }

    static ArrayList<String> getURLs(ArrayList<String> words) {
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb+srv://Admin:admin@cluster0.srt79fu.mongodb.net/test"));
        MongoDatabase MongoDB = mongoClient.getDatabase("MongoDB");
        MongoCollection<Document> IndexerCollection = MongoDB.getCollection("IndexerDB");
        Document doc = IndexerCollection.find().first();

        ArrayList<String> links = new ArrayList<>();

        for (int i = 0; i < words.size(); i++) {

            Document search = new Document("Word", words.get(i).toString());

            Document result = IndexerCollection.find(search).first();

            if (result != null) {
                ArrayList<String> sub = (ArrayList<String>) result.get("URLs");
                links.addAll(sub);

            }

        }
        var un = links.stream().distinct().toArray();
        links.clear();
//
        for (int i = 0; i < un.length; i++) {
            links.add(un[i].toString());
        }
        return links;
    }
}
