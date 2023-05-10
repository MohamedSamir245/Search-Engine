//TODO: Make separate collection each for a major HTML tag
//TODO: there is an error in phraseSearchingDB (it stores the same website again even if it exists)

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import java.net.HttpURLConnection;

import com.mongodb.client.*;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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

    static ArrayList<String>  getStopWords()
    {
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

            return stopwordsList;
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            return null;
        }
    }

    static ArrayList<Document> getBodiessList(MongoCollection<Document> collection) throws MalformedURLException {

        int j=0;

        System.out.println("Getting Data From Database ..");
        ArrayList<URL> urlArrayList = new ArrayList<>();

        ArrayList<Document> List = new ArrayList<>();


        FindIterable<Document> collectionindexer=collection.find();
        MongoCursor<Document> cursor = collectionindexer.iterator();


        try {
            while (cursor.hasNext()) {

                System.out.println("Collecting URL Number "+j++);
                Document doc = cursor.next();

                Document d=new Document();
                String Body =(String)doc.get("BodyText");
                String url =(String)doc.get("URL");


                d.append("Body",Body);
                d.append("Title", doc.get("Title"));

                d.append("URL",url);





                List.add(d);


            }
        } finally {
            cursor.close();
        }





        urlArrayList.add(new URL("https://www.codeforces.com/"));
        urlArrayList.add(new URL("https://www.wikipedia.org/"));
        urlArrayList.add(new URL("https://www.tyrereviews.com/"));
        urlArrayList.add(new URL("https://www.kanbkam.com/eg/en/ir-sensor-line-tracking-5-channels-B091D57PSP"));
        return List;


}


    static org.jsoup.nodes.Document getWebsiteInfo(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        if(responseCode==HttpURLConnection.HTTP_OK)
        {
            return Jsoup.connect(url.toString()).get();
        }
        else {
            System.out.println("Failed to Get Website Info => Error Code = "+responseCode);
            return null;
        }
    }

    static ArrayList<String> getStemmedWords(String body)
    {
        String[] Words = body.split("\\W+");

//            bodyText = bodyText.replaceAll(",|\\.|!|\\?|:|;|\\)|\\(|\\[|]|\\*&\\^%\\$|\"|\'|→|\\||#", "");
//            bodyText = bodyText.replaceAll("\\p{C}", "");
//            bodyText = bodyText.replaceAll("/|\\\\|", "");
//            bodyText = bodyText.replaceAll("©|»|-|\\{|}|=", "");


        SnowballStemmer stemmer = new SnowballStemmer(ENGLISH);

        ArrayList<String>newWords=new ArrayList<>();

        for (String word : Words) {
            if (isNumber(word))
                continue;
            if (word != null && !word.equals("")) {
//                    System.out.println(Words[i]);
                newWords.add((String) stemmer.stem(word.toLowerCase()));
            }
        }

        return newWords;

    }

    static ArrayList<String> removeStopWords(ArrayList<String> words,ArrayList<String>stopWords)
    {
        ArrayList<String> modifiedWords = new ArrayList<>();

        for (String word : words) {
            if (!stopWords.contains(word)) {
                modifiedWords.add(word);
            }
        }
        return modifiedWords;
    }
    public static void main(String[] args) throws IOException {
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb+srv://Admin:admin@cluster0.srt79fu.mongodb.net/test"));

        MongoDatabase MongoDB = mongoClient.getDatabase("MongoDB");
        MongoCollection<Document> IndexerCollection = MongoDB.getCollection("IndexerDB");
        MongoCollection<Document> phraseSearchingCollection = MongoDB.getCollection("phraseSearchingDB");
        MongoCollection<Document> CrawlerCollection = MongoDB.getCollection("CrawlerCollection");
        MongoCollection<Document> IndexerProgessCollection = MongoDB.getCollection("IndexerProgessCollection");



        ArrayList<String> stopwordsList = getStopWords();
//        ArrayList<URL> urlArrayList = getURLsList(CrawlerCollection);

        ArrayList<Document> List=getBodiessList(CrawlerCollection);

//        ArrayList<String> BodiesList = getBodiessList(CrawlerCollection);

//        BodiesList.forEach(System.out::println);
        int progressCounter=0;

        LocalDate Today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String TodayFormatted = Today.format(formatter);


        for (Document d : List) {





            String bodyText = (String) d.get("Body");
            String PageTitle=(String) d.get("Title");
            String url=(String) d.get("URL");

            URL u=new URL(url);

            org.jsoup.nodes.Document PageDoc= getWebsiteInfo(u);

            if(PageDoc==null) {
                System.out.println("Can't index: "+ u);
                continue;
            }

            System.out.println(progressCounter++ +". Working on: "+PageTitle);

            Document doc=IndexerProgessCollection.find(new Document("URL",url)).first();

            int reindexingFlag=0;
            if(doc!=null)
            {
                String formattedDate=(String) doc.get("Date");
                LocalDate date1 = LocalDate.parse(formattedDate, formatter);

                long daysBetween = ChronoUnit.DAYS.between(date1,Today);

                if(daysBetween<=3) {
                    System.out.println("Already Indexed From Less Than 3 Days");
                    continue;
                }
                reindexingFlag=1;
            }









//            String htmlWholeBody = bodyText;
            Document htmlDoc = new Document();
            htmlDoc.append("PageBody", bodyText);
            htmlDoc.append("PageLink", url);
            htmlDoc.append("PageTitle",PageTitle);

            if (phraseSearchingCollection.find(htmlDoc).first() == null)
                phraseSearchingCollection.insertOne(htmlDoc);

            bodyText =bodyText.replaceAll("\\d+", "");

            ArrayList<String>Words=getStemmedWords(bodyText);

            var freq = Words.stream().collect(Collectors.groupingBy(String::toLowerCase, Collectors.counting()));

            var un = Words.stream().distinct().toArray();
            ArrayList<String>uniqeWords=new ArrayList<>();
            for (Object o : un) {
                uniqeWords.add(o.toString());
            }

            ArrayList<String> modifiedWords = removeStopWords(uniqeWords,stopwordsList);

            Document result = IndexerCollection.find().first();
            if (result == null) {
                Document newDoc = new Document();

                for (String modifiedWord : modifiedWords) {
                    Document valDoc = new Document();
                    ArrayList<Document> val = new ArrayList<>();


                    valDoc.append("URL", url);
                    valDoc.append("Freq", freq.get(modifiedWord));
                    valDoc.append("Title",PageTitle);

                    val.add(valDoc);

                    newDoc.append(modifiedWord, val);
                }

                IndexerCollection.insertOne(newDoc);

            } else {

                Document oldDoc = result;
                for (String modifiedWord : modifiedWords) {
//                    ArrayList<String> newurls = new ArrayList<>();
                    ArrayList<Document> docList;
                    if (result.get(modifiedWord) != null) {

                        if (result.get(modifiedWord).getClass() == ArrayList.class) {
                            docList = (ArrayList<Document>) result.get(modifiedWord);
//                            System.out.println(docList.size());
                            boolean found = false;
                            for (Document document : docList) {
//                                System.out.println(docList.get(j).get("URL"));
                                if (Objects.equals(document.get("URL").toString(), url)) {
                                    found = true;
                                    break;
                                }

//                                System.out.println("After = "+docList.size());


                            }
                            if (!found) {
                                Document n = new Document();
                                n.append("URL", url);
                                n.append("Freq", freq.get(modifiedWord));
                                n.append("Title",PageTitle);
                                docList.add(n);
                                result.replace(modifiedWord, docList);

                            }
                        }
//
                    } else {
                        Document valDoc = new Document();
                        ArrayList<Document> val = new ArrayList<>();


                        valDoc.append("URL", url);
                        valDoc.append("Freq", freq.get(modifiedWord));
                        valDoc.append("Title",PageTitle);

                        val.add(valDoc);

                        result.append(modifiedWord, val);

                    }
                }
                System.out.println(result.size());
                System.out.println(oldDoc.size());

                Document filter = new Document("_id", result.get("_id"));
                IndexerCollection.replaceOne(filter, result);
            }

            Document progressDoc=new Document("URL",url.toString());
            progressDoc.append("Date",TodayFormatted);

            if(reindexingFlag==0)
            {


                IndexerProgessCollection.insertOne(progressDoc);
            }
            else
            {

                Document e=IndexerProgessCollection.find(new Document("URL",url.toString())).first();
                Document filter = new Document("_id", e.get("_id"));

                IndexerProgessCollection.replaceOne(filter,progressDoc);

                System.out.println("Updated Date");
            }



        }
    }

}
