//TODO: Make separate collection each for a major HTML tag
//TODO: there is an error in phraseSearchingDB (it stores the same website again even if it exists)

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import java.net.HttpURLConnection;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import opennlp.tools.stemmer.PorterStemmer;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;
import opennlp.tools.stemmer.snowball.SnowballStemmer;
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
//import org.tartarus.snowball.ext.EnglishStemmer;
import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;


//import static sun.nio.ch.DatagramChannelImpl.AbstractSelectableChannels.forEach;


public class Indexer {
    public static boolean isNumber(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

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
        urlArrayList.add(new URL("https://www.codeforces.com/"));
        urlArrayList.add(new URL("https://www.wikipedia.org/"));
        urlArrayList.add(new URL("https://www.tyrereviews.com/"));
//        urlArrayList.add(new URL("https://www.kanbkam.com/eg/en/ir-sensor-line-tracking-5-channels-B091D57PSP"));

        SnowballStemmer stemmer = new SnowballStemmer(ENGLISH);


        for (URL SamirURL : urlArrayList) {

            HttpURLConnection conn = (HttpURLConnection) SamirURL.openConnection();

            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            reader.close();

            String bodyText = Jsoup.parse(content.toString()).text();

            bodyText.replaceAll("\\d+", "");

            String[] Words = bodyText.split("\\W+");


//            bodyText = bodyText.replaceAll(",|\\.|!|\\?|:|;|\\)|\\(|\\[|]|\\*&\\^%\\$|\"|\'|→|\\||#", "");
//            bodyText = bodyText.replaceAll("\\p{C}", "");
//            bodyText = bodyText.replaceAll("/|\\\\|", "");
//            bodyText = bodyText.replaceAll("©|»|-|\\{|}|=", "");


            //TODO: make the document out of for loop
            String htmlWholeBody = bodyText;
            Document htmlDoc = new Document();
            htmlDoc.append("html", htmlWholeBody);
            htmlDoc.append("htmlLink", SamirURL.toString());


            if (phraseSearchingCollection.find(htmlDoc).first() == null)
                phraseSearchingCollection.insertOne(htmlDoc);


            for (int i = 0; i < Words.length; i++) {
                if (isNumber(Words[i]))
                    continue;
                if (Words[i] != "null") {
//                    System.out.println(Words[i]);
                    Words[i] = (String) stemmer.stem(Words[i].toString().toLowerCase());
                }
            }

//            long[] freqNum = new long[bodyText.length()];

            //finding freq of all word in a doc
            var freq = Arrays.stream(Words).collect(Collectors.groupingBy(String::toLowerCase, Collectors.counting()));

//            for(int i=0;i<Words.length;i++)
//            {
//                System.out.println(freq.get(Words[i]));
//            }

            var uniqeWords = Arrays.stream(Words).distinct().toArray();


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

            Document result = IndexerCollection.find().first();
            if (result == null) {
                Document newDoc = new Document();

                for (int i = 0; i < modifiedWords.size(); i++) {
                    Document valDoc = new Document();
                    ArrayList<Document> val = new ArrayList<>();


                    valDoc.append("URL", SamirURL.toString());
                    valDoc.append("Freq", freq.get(modifiedWords.get(i)));

                    val.add(valDoc);

                    newDoc.append(modifiedWords.get(i), val);
                }

                IndexerCollection.insertOne(newDoc);

                continue;
            } else {

                Document oldDoc = result;
                for (int i = 0; i < modifiedWords.size(); i++) {
//                    ArrayList<String> newurls = new ArrayList<>();
                    ArrayList<Document>docList;
                    if (result.get(modifiedWords.get(i)) != null) {

                        if (result.get(modifiedWords.get(i)).getClass() == ArrayList.class)
                        {
                            docList=(ArrayList<Document>) result.get(modifiedWords.get(i));
                            System.out.println(docList.size());
                            Boolean found=false;
                            for(int j=0;j<docList.size();j++)
                            {
//                                System.out.println(docList.get(j).get("URL"));
                                if(docList.get(j).get("URL").toString()==SamirURL.toString())
                                {
                                    found=true;
                                    break;
                                }

//                                System.out.println("After = "+docList.size());


                            }
                            if(!found) {
                                Document n = new Document();
                                n.append("URL", SamirURL.toString());
                                n.append("Freq", freq.get(modifiedWords.get(i)));
                                docList.add(n);
                                result.replace(modifiedWords.get(i),docList);

                            }
                        }
//
                    } else {
                        Document valDoc = new Document();
                        ArrayList<Document> val = new ArrayList<>();


                        valDoc.append("URL", SamirURL.toString());
                        valDoc.append("Freq", freq.get(modifiedWords.get(i)));

                        val.add(valDoc);

                        result.append(modifiedWords.get(i), val);

                    }
                }
                System.out.println(result.size());
                System.out.println(oldDoc.size());

                Document filter = new Document("_id", result.get("_id"));
                IndexerCollection.replaceOne(filter, result);
            }

        }
    }


}
