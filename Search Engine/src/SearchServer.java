import Query_Phrase_Processor.Query_Phrase_Processor;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;

import static Query_Phrase_Processor.Query_Phrase_Processor.getQueryWords;
import static Query_Phrase_Processor.Query_Phrase_Processor.getURLs;

public class SearchServer {
    public static void main(String[] args) throws Exception {
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb+srv://Admin:admin@cluster0.srt79fu.mongodb.net/test"));

        MongoDatabase MongoDB = mongoClient.getDatabase("MongoDB");
        MongoCollection<Document> searchDB = MongoDB.getCollection("searchDB");
        MongoCollection<Document> resultDB = MongoDB.getCollection("resultDB");



        MongoCollection<Document> IndexerCollection = MongoDB.getCollection("IndexerDB");
        Document indexerdoc = IndexerCollection.find().first();
        MongoCollection<Document> phraseSearchingCollection = MongoDB.getCollection("phraseSearchingDB");
        FindIterable<Document> phraseSearchingdoc = phraseSearchingCollection.find();


        //for suggestion mechanism
        MongoCollection<Document> queryCollection = MongoDB.getCollection("queryCollection");

        while (true) {
            Document result = searchDB.find().first();
//
            if (result != null) {
                String Query = (String) result.get("Query");
//
                searchDB.deleteMany(result);
//
                System.out.println(Query);

                Document queriesDoc=queryCollection.find().first();
                if(queriesDoc==null)
                {
                    Document tmpd=new Document(Query,1);
                    queryCollection.insertOne(tmpd);
                }
                else if(queriesDoc.get(Query)==null)
                {
                    queriesDoc.append(Query,1);

                    Document filter = new Document("_id", queriesDoc.get("_id"));
                    queryCollection.replaceOne(filter, queriesDoc);
                }
                else
                {

                    int oldval=(int)queriesDoc.get(Query);
                    queriesDoc.replace(Query,oldval+1);

                    Document filter = new Document("_id", queriesDoc.get("_id"));
                    queryCollection.replaceOne(filter, queriesDoc);

                }

//
//                String Query="enter";
                Document res=getURLs(getQueryWords(Query.toLowerCase()),indexerdoc,phraseSearchingdoc);
//                ArrayList<String> urls =new ArrayList<>(Arrays.asList( getURLs(getQueryWords(Query.toLowerCase()),indexerdoc,phraseSearchingdoc)));
                ArrayList<String> urls=new ArrayList<>(Arrays.asList((String[])res.get("Links"))) ;
                ArrayList<String>titles=new ArrayList<>(Arrays.asList((String[]) res.get("Titles")));
                ArrayList<String>descriptions=new ArrayList<>();
                for (String url : urls) {
                    try{
                    String snippet = Query_Phrase_Processor.generateSnippet(url, Query);

                    descriptions.add(snippet);}
                    catch (Exception e)
                    {
                        descriptions.add("");
                    }
                }

//                urls.forEach(System.out::println);
//                titles.forEach(System.out::println);
//                descriptions.forEach(System.out::println);



                Document r = new Document();
                r.append("Query",Query);
                r.append("URLs",urls);
                r.append("Titles",titles);
                r.append("Descriptions",descriptions);
//                r.append("")
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


}
