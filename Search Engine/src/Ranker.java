
import Crawler.Main;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.model.*;
import java.net.HttpURLConnection;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.netty.util.Constant;
import kotlin.io.ConstantsKt;
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
//import org.bson.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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

class Pair {
    String x;
    int y;
    public Pair(String x, int y) {
        this.x = x;
        this.y = y;
    }
}
class ArrayOfPairsSorter {
    static void sort(Pair[] arr) {
        Comparator<Pair> comparator = new Comparator<>() {
            public int compare(Pair p1, Pair p2) {
                return p2.y - p1.y;
            }
        };
        Arrays.sort(arr, comparator);
    }
}

public class Ranker{
    int max_depth=3;
    int num_visited=0;
    double damping_factor=0.85;
    public static String seed="https://www.linkedin.com/";
    HashSet<String>pages=new HashSet<>();
    static HashMap<String,Integer>visited=new HashMap<>();
    static HashMap<String,Vector<String>>graph=new HashMap<>();
    static HashMap<String,Double>scores=new HashMap<>();
    static HashMap<String,Double>temp_score=new HashMap<>();
    static HashMap<String,Double>initial_score=new HashMap<>();
    static Pair[] final_ranks=new Pair[10000];
    HashMap<String,Double>tf_idf_score=new HashMap<>();
    public void find_pages(String url,int curr_depth){
        if(!visited.containsKey(url)&&curr_depth<max_depth){
            num_visited++;
            try{
                System.out.println(url);
                pages.add(url);
                visited.put(url,1);
                Document curr_doc=Jsoup.connect(url).get();         //fetch the whole document html
                Elements urls_in_page= curr_doc.select("a[href]");      //fetch all href links in document
                int i=0;
                for(Element next_url:urls_in_page){
                    if(i>=20)break;
                    if(graph.containsKey(url)){
                        graph.get(url).add(next_url.attr("abs:href"));
                    }
                    else {
                        graph.put(url,new Vector<>());
                        graph.get(url).add(next_url.attr("abs:href"));
                    }
                    find_pages(next_url.attr("abs:href"),curr_depth+1);         //recursive fetching of other urls
                    i++;
                }
            }
            catch(Exception e){
                e.getStackTrace();
            }
        }
    }
    HashMap<String,Boolean>ivisited=new HashMap<String,Boolean>();
    public void initialize_graph_scores2(String url){
        ivisited.put(url,true);

    }
    public void initialize_graph_scores(){
        for(Map.Entry<String,Vector<String>>initial_page:graph.entrySet()){
            scores.put(initial_page.getKey(),1.0/num_visited);
            for(String page:initial_page.getValue()){
                scores.put(page,1.0/num_visited);
            }
        }
    }
    public void update(){
        for(Map.Entry<String,Vector<String>>parent:graph.entrySet()){
            for(String child:parent.getValue()){
                initial_score.put(child,initial_score.get(child)+temp_score.get(parent)*1.0/parent.getValue().size());
            }
        }
    }
    public void update_page_rank(){
        for(Map.Entry<String,Double>rank:temp_score.entrySet()){
            temp_score.put(rank.getKey(),scores.get(rank.getKey()));
        }
        for(Map.Entry<String,Double>rank:initial_score.entrySet()){
            initial_score.put(rank.getKey(),0.0);
        }
        update();
        for(Map.Entry<String,Double>rank:scores.entrySet()){
            scores.put(rank.getKey(),rank.getValue()+1-damping_factor+damping_factor*initial_score.get(rank.getKey()));
        }
        int i=0;
        for(Map.Entry<String,Double>rank:scores.entrySet()){
            final_ranks[i].x=rank.getKey();
            final_ranks[i].y=(int)Math.round(rank.getValue());
        }
        ArrayOfPairsSorter.sort(final_ranks);
    }
//    public HashMap<String,Double> calculateInverseDocFrequency(DocumentProperties [] docProperties)
//    {
//        HashMap<String,Double> InverseDocFreqMap = new HashMap<>();
//        int size = docProperties.length;
//        double wordCount ;
//        for (String word : wordList) {
//            wordCount = 0;
//            for(int i=0;i<size;i++)
//            {
//                HashMap<String,Integer> tempMap = docProperties[i].getWordCountMap();
//                if(tempMap.containsKey(word))
//                {
//                    wordCount++;
//                    continue;
//                }
//            }
//            double temp = size/ wordCount;
//            double idf = 1 + Math.log(temp);
//            InverseDocFreqMap.put(word,idf);
//        }
//        return InverseDocFreqMap;
//    }
//    public HashMap<String,Double> calculateTermFrequency(HashMap<String,Integer>inputMap) {
//        HashMap termFreqMap = new HashMap<>();
//        double sum = 0.0;
//        //Get the sum of all elements in hashmap
//        for (float val : inputMap.values()) {
//            sum += val;
//        }
//        //create a new hashMap with Tf values in it.
//        Iterator it = inputMap.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry pair = (Map.Entry)it.next();
//            double tf = (Integer)pair.getValue()/ sum;
//            termFreqMap.put((pair.getKey().toString()),tf);
//        }
//        return termFreqMap;
//    }
    public static void main(String[] args){
        Ranker temp=new Ranker();
        temp.find_pages(seed,0);
        temp.initialize_graph_scores();
        System.out.println(scores.size());
        for(Map.Entry<String,Double>ans:scores.entrySet()){
            System.out.println(ans.getKey());
        }
    }
}