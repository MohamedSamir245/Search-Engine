//TODO: Make separate collection each for a major HTML tag

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import opennlp.tools.stemmer.PorterStemmer;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.bson.Document;


import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Array;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import com.google.gson.*;
import org.jsoup.select.Elements;

import javax.print.Doc;

import static java.util.Arrays.asList;
//import static sun.nio.ch.DatagramChannelImpl.AbstractSelectableChannels.forEach;


public class Indexer {

    public static void main(String[] args) throws IOException {
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb+srv://Admin:admin@cluster0.srt79fu.mongodb.net/test"));

        MongoDatabase MongoDB = mongoClient.getDatabase("MongoDB");
        MongoCollection<Document> IndexerCollection = MongoDB.getCollection("IndexerDB");
        MongoCollection<Document> phraseSearchingCollection = MongoDB.getCollection("phraseSearchingDB");



        ArrayList<String> stopwordsList = new ArrayList<>();

        try {
            File stopFile = new File("D:\\CMP1Materials\\SecondYear\\SecondTerm\\APT\\Project\\Search Engine\\Search Engine\\stop_words_english.txt");
            Scanner myReader = new Scanner(stopFile);
            while (myReader.hasNext()) {
                String data = myReader.next();
                stopwordsList.add(data);
//            System.out.println(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }


        ArrayList<URL> urlArrayList = new ArrayList<>();
        urlArrayList.add(new URL("https://github.com/yuze98/IndexerDB/blob/main/src/main/java/org/example/App.java"));
        urlArrayList.add(new URL("https://jsoup.org/cookbook/"));
        urlArrayList.add(new URL("https://www.wikipedia.org/"));

        PorterStemmer stemmer = new PorterStemmer();


        for (URL SamirURL : urlArrayList) {

            URLConnection connection = SamirURL.openConnection();

            InputStream SamirStream = connection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(SamirStream));

            String line = null;

            StringBuilder sb = new StringBuilder();

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            org.jsoup.nodes.Document doc = Jsoup.parse(sb.toString());


            String bodyText = doc.body().text();


            bodyText = bodyText.replaceAll(",|\\.|!|\\?|:|;|\\)|\\(|\\[|]|\\*&\\^%\\$|\"|\'", "");
            bodyText = bodyText.replaceAll("/|\\\\|", "");
            bodyText = bodyText.replaceAll("©|»|-|\\{|}|=", "");


            //TODO: make the document out of for loop
            String htmlWholeBody = bodyText;
            Document htmlDoc = new Document();
            htmlDoc.append("html", htmlWholeBody);
            htmlDoc.append("htmlLink", SamirURL.toString());


            if (phraseSearchingCollection.find(htmlDoc).first() == null)
                phraseSearchingCollection.insertOne(htmlDoc);

            String Words[];
            Words = bodyText.split(" ");

            for (int i = 0; i < Words.length; i++) {
                Words[i] = stemmer.stem(Words[i].toString());
            }

            long[] freqNum = new long[bodyText.length()];

            //finding freq of all word in a doc
            var freq = Arrays.stream(Words).collect(Collectors.groupingBy(String::toLowerCase, Collectors.counting()));

            var uniqeWords = Arrays.stream(Words).distinct().toArray();
//            Arrays.stream(uniqeWords).map(word->stemmer.stem(word.toString()));


//            System.out.println(uniqeWords[0].toString());
//            for(int i=0;i<uniqeWords.length;i++)
//            {
//                uniqeWords[i]=stemmer.stem(uniqeWords[i].toString());
//            }

            ArrayList<String> modifiedWords = new ArrayList<>();
//        ArrayList<Integer>Freq=new ArrayList<>();

            for (int i = 0; i < uniqeWords.length; i++) {
                if (!stopwordsList.contains(uniqeWords[i])) {
                    modifiedWords.add(uniqeWords[i].toString());
                }
            }

            for (int i = 0; i < modifiedWords.size(); i++) {
                Document searchQuery = new Document("Word", modifiedWords.get(i).toString());
                Document result=IndexerCollection.find(searchQuery).first();
                if (result == null) {
                    ArrayList<String> websites = new ArrayList<>();
                    websites.add(SamirURL.toString());
                    Document freqDocument = new Document("Word",modifiedWords.get(i).toString());
                    freqDocument.append("URLs",websites);

                    IndexerCollection.insertOne(freqDocument);
                }
                else
                {
                    ArrayList<String>links= (ArrayList<String>)result.get("URLs");

                    links.add(SamirURL.toString());
                    result.replace("URLs",links);

                    IndexerCollection.replaceOne(searchQuery,result);

                }
            }

        }

    }


}
