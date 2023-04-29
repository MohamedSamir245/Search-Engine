//TODO: remove stop words

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import opennlp.tools.stemmer.PorterStemmer;
import org.bson.Document;


public class QueryProcessor {
    public static void main(String[] args) throws IOException {
        //TODO take the query and assign it to (Query) variable

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter query");
        String input = scanner.nextLine();

        String[]urls=getURLs(getQueryWords(input));

        for(int i=0;i<urls.length;i++)
            System.out.println(urls[i]);

    }
    static String[]getQueryWords(String input)
    {
        String Phrase="",tempWords;
        String wordsOnly[];
        input=input.replaceAll(",|\\.|!|\\?|:|;|\\)|\\(|\\[|]|\\*&\\^%\\$|\'", "");
        input=input.replaceAll("/|\\\\|", "");
        input=input.replaceAll("©|»|-|\\{|}|=", "");
        String words__phrase[]=input.split("\"");
        if(input.indexOf("\"")!=input.lastIndexOf("\""))
        {
            if(input.startsWith("\""))
            {
                Phrase=words__phrase[1];
                wordsOnly= words__phrase[2].split(" ");
            }
            else if(input.endsWith("\""))
            {
                Phrase=words__phrase[1];
                wordsOnly= words__phrase[0].split(" ");
            }
            else
            {
                Phrase=words__phrase[1];
                tempWords=words__phrase[0];
                tempWords+=words__phrase[2];
                wordsOnly= tempWords.split(" ");

            }
        }
        else
        {
            input=input.replaceAll("\"", "");
            wordsOnly=input.split(" ");
        }

        String[] newWordsOnly= Arrays.stream(wordsOnly).filter(x->x!="").toArray(String[]::new);

        PorterStemmer stemmer = new PorterStemmer();

        String finalWords[]=new String[newWordsOnly.length+1];

        for(int i=0;i<newWordsOnly.length+1;i++)
        {
            if(i==newWordsOnly.length )
            {
                if(Phrase.length()!=0)
                {
                    finalWords[i]= stemmer.stem(Phrase);
                }
                else finalWords[i]="";
//                    System.out.print(Phrase);
                continue;
            }
            finalWords[i]=stemmer.stem(newWordsOnly[i]);

        }

        String[]finalUniqueWords= Arrays.stream(finalWords).distinct().toArray(String[]::new);

        return finalUniqueWords;
    }

    static String[]getURLs(String[]words)
    {
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb+srv://Admin:admin@cluster0.srt79fu.mongodb.net/test"));
        MongoDatabase MongoDB = mongoClient.getDatabase("MongoDB");
        MongoCollection<Document> IndexerCollection = MongoDB.getCollection("IndexerDB");
//        Document indexerdoc = IndexerCollection.find().first();
        MongoCollection<Document> phraseSearchingCollection = MongoDB.getCollection("phraseSearchingDB");
        FindIterable<Document> phraseSearchingdoc = phraseSearchingCollection.find();

        ArrayList<String> links = new ArrayList<>();


        for (int i = 0; i < words.length-1; i++) {

            Document search = new Document("Word", words[i]);

            Document result = IndexerCollection.find(search).first();

            if (result != null) {
                ArrayList<String> sub = (ArrayList<String>) result.get("URLs");
                links.addAll(sub);

            }

        }

        if(words[words.length-1]!="") {
            MongoCursor<Document> cursor = phraseSearchingdoc.iterator();

            try {
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    String html = (String) doc.get("html");
                    String url = (String) doc.get("htmlLink");

                    if (html.indexOf(words[words.length - 1]) != -1) {
                        links.add(url);
                    }
                }
            } finally {
                cursor.close();
            }
        }
        String[]finalLinks= links.stream().distinct().toArray(String[]::new);
        return finalLinks;

    }

}
