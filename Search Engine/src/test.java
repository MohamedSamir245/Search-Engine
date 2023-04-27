import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.IOException;

public class test {
    public static void main(String[] args) throws IOException {

        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb+srv://ramadanesmail801:ismail2992002@osproject.cnayefi.mongodb.net/?retryWrites=true&w=majority"));

        MongoDatabase MongoDB= mongoClient.getDatabase("osProject");
//        MongoDB.createCollection("IndexerDB");
        MongoCollection<Document> IndexerCollection = MongoDB.getCollection("sample_airbnb.listingsAndReviews");
        Document doc =new Document();
        doc.append("name","ismail");
        doc.append("age",20);
        IndexerCollection.insertOne(doc);

    }

}
