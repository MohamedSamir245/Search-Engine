import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

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
import java.util.stream.Collectors;
import com.google.gson.*;
import org.jsoup.select.Elements;

import javax.print.Doc;

import static java.util.Arrays.asList;
//import static sun.nio.ch.DatagramChannelImpl.AbstractSelectableChannels.forEach;


public class Indexer {

    public static void main(String[] args) throws IOException {
        MongoClient mongoClient= new MongoClient(new MongoClientURI("mongodb+srv://Admin:admin@cluster0.srt79fu.mongodb.net/test"));

        MongoDatabase MongoDB=mongoClient.getDatabase("MongoDB");

        MongoCollection<Document> IndexerCollection=MongoDB.getCollection("IndexerDB");
        Document tst=new Document();

//        tst.append("key","mohamed");
//        IndexerCollection.insertOne(tst);

        //Samir Testing
        URL SamirURL=new URL("https://jsoup.org/cookbook/");
        URLConnection connection=SamirURL.openConnection();

        InputStream SamirStream=connection.getInputStream();
        BufferedReader br=new BufferedReader(new InputStreamReader(SamirStream));

        String line=null;

        StringBuilder sb=new StringBuilder();

        while((line=br.readLine())!=null)
        {
            sb.append(line);
        }

//        System.out.println("\n\n\nGetting HTML");
//        System.out.println(sb.toString());

//        String str=String.format("D:\\CMP1Materials\\SecondYear\\SecondTerm\\APT\\Project\\Search-Engine\\Search Engine\\test\\%s",)

//        File input=new File(sb.toString());
        File input=new File("D:\\CMP1Materials\\SecondYear\\SecondTerm\\APT\\Project\\Search-Engine\\Search Engine\\test\\Wikipedia.html");


//        System.out.println("Parsing From HTML File");
        org.jsoup.nodes.Document doc=Jsoup.parse(input,"UTF-8");

        System.out.println("Parsing From String");
        org.jsoup.nodes.Document doc2=Jsoup.parse(sb.toString());

//        System.out.println(doc);
//        System.out.println(doc2);


        Elements links = doc2.select("a[href]"); // a with href
//        System.out.println(links);

        String bodyText= doc2.body().text();

        bodyText = bodyText.replaceAll(",|\\.|!|\\?|:|;|\\)|\\(|\\[|]|\\*&\\^%\\$|\"|\'", "");
        bodyText = bodyText.replaceAll("/|\\\\|", "");
        bodyText = bodyText.replaceAll("©|»|-", "");

        String Words[];
        Words=bodyText.split(" ");

//        System.out.println(Words.length);

        long[] freqNum = new long[bodyText.length()];

        //finding freq of all word in a doc
        var freq = Arrays.stream(Words)
                .collect(Collectors.groupingBy(String::toLowerCase, Collectors.counting()));

        var uniqeWords=Arrays.stream(Words).distinct().toArray();

//        for(int i=0;i<uniqeWords.length;i++)
//        {
//            System.out.println(uniqeWords[i]);
//        }

//        System.out.println(uniqeWords);

        ArrayList<String> stopwordsList=new ArrayList<>();

    try{
        File stopFile = new File("stop_words_english.txt");
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
        ArrayList<String> modifiedWords = new ArrayList<>();

        for(int i=0;i<uniqeWords.length;i++)
        {
            if(!stopwordsList.contains(uniqeWords[i]))
            {
                modifiedWords.add(uniqeWords[i].toString());
            }
        }
//        modifiedWords.forEach(System.out::println);



        System.out.println(modifiedWords.size());
        System.out.println(uniqeWords.length);





//    System.out.println(freq);

    Document freqDocument=new Document();

        for(int i=0;i<modifiedWords.size();i++)
        {
            freqDocument.append(modifiedWords.get(i),SamirURL.toString());
        }
//for(int i=0;i<freq.size();i++)
//{
//    freq.forEach(freqDocument::append);
//}

//System.out.println(freqDocument);

        IndexerCollection.insertOne(freqDocument);



    int size = 0;






        //



    }

}
