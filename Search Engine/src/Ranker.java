//import javax.swing.text.Document;
//import com.mongodb.BasicDBObject;
//import com.mongodb.DBObject;
//import com.mongodb.MongoClient;
//import com.mongodb.MongoClientURI;
//import com.mongodb.client.model.*;
//import java.net.HttpURLConnection;
//
//import com.mongodb.client.FindIterable;
//import com.mongodb.client.MongoClients;
//import com.mongodb.client.MongoCollection;
//import com.mongodb.client.MongoDatabase;
//import io.netty.util.Constant;
//import kotlin.io.ConstantsKt;
//import opennlp.tools.stemmer.PorterStemmer;
//
//import com.mongodb.client.model.Filters;
//import com.mongodb.client.model.Projections;
//import com.mongodb.client.model.Updates;
//import opennlp.tools.stemmer.snowball.SnowballStemmer;
//import org.bson.conversions.Bson;
//import org.bson.types.ObjectId;
//import org.jsoup.Connection;
//import org.jsoup.Connection.Response;
//import org.jsoup.Jsoup;
////import org.bson.Document;
//
//
//import java.io.*;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.net.URLConnection;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.sql.Array;
//import java.util.*;
//import java.util.concurrent.CopyOnWriteArrayList;
//import java.util.stream.Collectors;
//
//import com.google.gson.*;
//import org.jsoup.select.Elements;
//
//import javax.print.Doc;
//
//import static java.util.Arrays.asList;
////import org.tartarus.snowball.ext.EnglishStemmer;
//import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;
//
//public class Ranker{
//    MongoClient client=new MongoClient();
//    MongoDatabase database = client.getDatabase("MongoDB");
//    public String gethost(String str){
//        try{
//            URL url=new URL(str);
//            String ret=url.getHost();
//            return ret;
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//        return null;
//    }
//    class tfidf_score{
//        Indexer ind;
//        List<Document>pages;
//        List<String>queries;
//        int num_words=0,stemcnt=0;
//        public tfidf_score(Indexer index,List<Document>pg,List<String>words){
//            ind=index;
//            pages=pg;
//            queries=words;
//        }
//
//        //utility functions
//
//        //TF-IDF        term frequency(t,d)      *       inverse document frequency
//        //              number of times the              log((1+n)/1+df(d,t))+1
//        //              term t appears in the            n = # of documents
//        //              document d                       df(d,t) = document frequency of the term t
//
//        /*
//
//        We need to rank the websites according to the query words
//        Calculating the Tf-IDF would need the generation of a certain page score
//        To generate the score we need to filter the words in the query and count the number and weight of each word in the page
//
//         */
//        public double page_score(Document doc){         //calculating the score for each document stand alone
//            String url=gethost(String.valueOf(doc.getDefaultRootElement()));    //should return the url of the document
//            for(int i=0;i<queries.size();i++){
//
//            }
//            double tf,idf,score=0,wordcnt=1,wordscount=1;
//            int docsnum=1,wordsnum=1;
//            if (wordcnt>0){
//                tf=wordcnt/wordscount;
//                idf=Math.log(1.0*docsnum/wordsnum);
//                score+=tf*idf;
//                num_words++;
//            }
//            return 0.0;
//        }
//    }
//
//    class pagerank{
//        Indexer ind;
//        double alpha=0.85;
//        int number_of_pages=0,next_id=0;
//        Map<String,Document>graph;
//        Map<String,String>hosts;
//        Map<String,Integer>ids;         //for simplicity assign an id for each page
//        HashMap<Integer,ArrayList<Integer>>adjacent=new HashMap<>();    //this would store the to ---> from edges
//        ArrayList<Integer>out_degree;                                   //this would store the from ---> to edges
//        ArrayList<Double>rank_pages;        //the final rank of each page
//        pagerank(Indexer index){
//            ind=index;
//            next_id=0;
//            out_degree=new ArrayList<>();
//            rank_pages=new ArrayList<>();
//
//        }
//        public void updatePageRanks(Collection<Document> pages) {
//            List<WriteModel<Document>> operations = new ArrayList<>();
//            for (Document page : pages) {
//                operations.add(new UpdateOneModel<>(
//                        Filters.eq(Constants.FIELD_ID, page.id),
//                        Updates.set(Constants.FIELD_RANK, page.rank)
//                ));
//            }
//
//            if (operations.isEmpty()) {
//                return;
//            }
//
//            mWebPagesCollection.bulkWrite(operations);
//        }
//        //graph building function
//        void add_edge(int start,int end){
//            adjacent.get(end).add(start);
//            out_degree.set(start,out_degree.get(start));        //assigns a new vector with the new parent node in it to the child vector
//        }
//        void reset_rank_pages_vector(){
//            for(int i=0;i<number_of_pages;i++){
//                adjacent.put(i,new ArrayList<>());
//                out_degree.add(0);
//                rank_pages.add(1.0/number_of_pages);        //give each node an equal initial score
//                //ex. 5 nodes each node has an initial score 0.2
//            }
//        }
//        void build_graph(){
//            Map<String,Document>gg=null;
//            graph=gg;               //suppose that the gg map is the list of required documents that should be received
//            reset_rank_pages_vector();
//            for(Map.Entry<String,Document>web_nodes:graph.entrySet()){
//                if(!ids.containsKey(web_nodes.getKey())){
//                    ids.put(web_nodes.getKey(),next_id++);
//                }
//                for(String destination:(List<String>)web_nodes.getValue().getDefaultRootElement()){
//                    if(graph.containsKey(destination)){
//                        if(!ids.containsKey(destination)){
//                            ids.put(destination,next_id++);
//                        }
//                        this.add_edge(ids.get(web_nodes.getKey()),ids.get(destination));
//                    }
//                }
//            }
//        }
//
//        /**
//         * Get host to host web pages graph
//         */
//        public void getHostToHostGraph() {
//            // Get the web pages in the graph (all nodes)
//            Map<String,Document>gg=null;
//            graph=gg;               //suppose that the gg map is the list of required documents that should be received
//            number_of_pages=0;
//            // calculate pages count and get unique host pages ids.
//            for(Map.Entry<String,Document>DocumentNode:graph.entrySet()) {
//                // Get the host web page url.
//                String DocumentHostURL=gethost(DocumentNode.getKey());
//                // Map this url to its host url.
//                hosts.put(DocumentNode.getKey(), DocumentHostURL);
//
//                if (!ids.containsKey(DocumentHostURL)) {
//                    this.number_of_pages++;
//                    ids.put(DocumentHostURL, next_id++);
//                }
//
//                for (String to : (List<String>)DocumentNode.getValue().getDefaultRootElement()) {
//                    String toHostURL = gethost(to);
//
//                    if (graph.containsKey(to)) { // Check if this out link page is currently indexed in the database.
//                        // Map this url to its host url.
//                        hosts.put(to, toHostURL);
//
//                        if (!ids.containsKey(toHostURL)) {
//                            this.number_of_pages++;
//                            ids.put(toHostURL, next_id++);
//                        }
//                    }
//                }
//            }
//
//            // Initialize pageRank lists after getting pages count.
//            initializePageRankLists();
//
//            // add arcs.
//            for (Map.Entry<String, Document> DocumentNode : graph.entrySet()) {
//                // Get the host web page url.
//                String DocumentHostURL = gethost(DocumentNode.getKey());
//
//                // Loop over all links and write arcs
//                for (String to : (List<String>)DocumentNode.getValue().getDefaultRootElement()) {
//                    String toHostURL = gethost(to);
//
//                    if (graph.containsKey(to)) { // Check if this out link page is currently indexed in the database.
//                        add_edge(ids.get(DocumentHostURL), ids.get(toHostURL));
//                    }
//                }
//            }
//        }
//
//        /**
//         * save web pages graph to a file
//         */
//        private void saveGraph() {
//            // Write to the edges file
//            try (PrintWriter out = new PrintWriter(Constants.GRAPH_FILE_NAME)) {
//                // Write the number of nodes
//                out.println(this.number_of_pages);
//
//                // Write arcs
//                for (int to = 0; to < number_of_pages; to++) {
//
//                    if (adjacent.containsKey(to)) {
//                        for (int from : adjacent.get(to)) {
//                            out.println(from + " " + to);
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                System.err.println(e.getMessage());
//            }
//        }
//
//        /**
//         * PageRanker calculations
//         */
//        private void rankPages() {
//            Double danglingSum, pagesRankSum = 1.0;
//
//            for (int iteration = 0; iteration < MAX_ITERATIONS; iteration++) {
//                danglingSum = 0.0;
//
//                // Normalize the PR(i) needed for the power method calculations
//                if (iteration > 0) {
//                    for (int page = 0; page < number_of_pages; page++) {
//                        Double rank = rank_pages.get(page) * 1.0 / pagesRankSum;
//                        rank_pages.set(page, rank);
//                        if (out_degree.get(page) == 0) {
//                            danglingSum += rank;
//                        }
//                    }
//                }
//
//                pagesRankSum = 0.0;
//
//                Double aPage = alpha * danglingSum * (1.0 / number_of_pages); // Same for all pages
//                Double oneProb = (1.0 - alpha) * (1.0 / number_of_pages) * 1.0; // Same for all pages
//
//                // Loop over all pages
//                ArrayList<Double> newPagesRank = new ArrayList<>();
//                for (int page = 0; page < number_of_pages; page++) {
//
//                    Double hPage = 0.0;
//
//                    if (adjacent.containsKey(page)) {
//                        for (Integer from : adjacent.get(page)) {
//                            hPage += (1.0 * rank_pages.get(from) / (1.0 * out_degrees.get(from)));
//                        }
//                        hPage *= alpha; // Multiply by dumping factor.
//                    }
//
//                    newPagesRank.add(hPage + aPage + oneProb);
//                }
//
//                // Update new ranks
//                for (int page = 0; page < number_of_pages; page++) {
//                    pagesRank.set(page, newPagesRank.get(page));
//                    pagesRankSum += newPagesRank.get(page);
//                }
//            }
//        }
//
//        /**
//         * Read page ranks file, used in case of running on CUDA.
//         *
//         * @param inputPath
//         */
//        private void readPagesRanks(String inputPath) {
//            try {
//                BufferedReader br = new BufferedReader(new FileReader(inputPath));
//                String strLine;
//
//                // Read file line by line
//                while ((strLine = br.readLine()) != null) {
//
//                    String[] strs = strLine.trim().split(" ");
//                    Integer pageID = Integer.parseInt(strs[0]);
//                    Double rank = Double.parseDouble(strs[2]);
//
//                    pagesRank.set(pageID, rank);
//
//                }
//            } catch (Exception e) {
//                System.err.println("Error: " + e.getMessage());
//            }
//        }
//
//        /**
//         * Updates pages ranks in the database
//         */
//        private void updatePagesRanks(boolean cudaInput) {
//            if (cudaInput) readPagesRanks(Constants.CUDA_PAGE_RANKS_FILE_NAME);
//            else {
//                // Reverse a map
//                Map<Integer, String> pagesURL = ids.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
//
//                for (int id = 0; id < this.number_of_pages; id++) {
//                    graph.get(pagesURL.get(id)).rank = pagesRank.get(id);
//                }
//
//                updatePageRanks(graph.values());
//            }
//        }
//
//        /**
//         * Updates pages ranks in the database in case of host to host graph.
//         */
//        private void updateHostToHostPagesRanks(boolean cudaInput) {
//            if (cudaInput) readPagesRanks(Constants.CUDA_PAGE_RANKS_FILE_NAME);
//            else {
//                // Loop over the Map and update
//                for (String DocumentURL : hosts.keySet()) {
//                    // Get its host URl.
//                    String DocumentHostURL = hosts.get(DocumentURL);
//
//                    graph.get(DocumentURL).rank = rank_pages.get(ids.get(DocumentHostURL));
////                }
//                }
//
//                ind.updatePageRanks(graph.values());
//            }
//        }
//
//        /**
//         * Print page ranks on console (Left for Debugging)
//         */
//        private void printPR(boolean checkSumOnly) {
//            Double checkSum = 0.0;
//            for (Integer page = 0; page < number_of_pages; page++) {
//                checkSum += rank_pages.get(page);
//                if (!checkSumOnly)
//                    System.out.println("Page Ranker: " + page.toString() + " = " + pagesRank.get(page));
//            }
//            System.out.println("Page Ranker: check sum = " + checkSum.toString());
//        }
//
//        /**
//         * Save page ranks to the output ranks.txt file (Left for Debugging)
//         */
//        private void savePR() {
//            try (PrintWriter out = new PrintWriter(Constants.PAGE_RANKS_FILE_NAME)) {
//                for (Integer page = 0; page < number_of_pages; page++) {
//                    out.println(page.toString() + " = " + pagesRank.get(page));
//                }
//            } catch (Exception e) {
//                System.err.println(e.getMessage());
//            }
//        }
//
//        /**
//         * Read edges list from a file (Deprecated)
//         *
//         * @param filePath
//         */
//        private void readGraphFile(String filePath) {
//            try {
//                BufferedReader br = new BufferedReader(new FileReader(filePath));
//                String strLine;
//
//                this.number_of_pages = Integer.parseInt(strLine = br.readLine().trim().split(" ")[0]);
//
//                initialize();
//
//                // Read file line by line
//                while ((strLine = br.readLine()) != null) {
//
//                    String[] strs = strLine.trim().split(" ");
//                    Integer u = Integer.parseInt(strs[0]);
//                    Integer v = Integer.parseInt(strs[1]);
//
//                    // Add arcs
//                    this.addArc(u, v);
//                }
//            } catch (Exception e) {
//                System.err.println("Error: " + e.getMessage());
//            }
//        }
//
//        /**
//         * Start page ranking algorithm
//         */
//        public void start(boolean hostPagesOnly) {
//            System.out.println("Start page ranking...");
//
//            // Get the graph and save it
//            if (hostPagesOnly) getHostToHostGraph();
//            else getGraph();
//            saveGraph();
//
//            rankPages();
//            updateHostToHostPagesRanks(false);
//
//            printPR(true);
//            savePR();
//
//            System.out.println("Finish page ranking");
//        }
//
//        /**
//         * Start CUDA page ranking algorithm
//         */
//        public void startCUDAPageRank() {
//            try {
//                // Get the graph and save it
//                getGraph();
//                saveGraph();
//
//                // Compile and run cuda.
//                String[] cmd = {Constants.CUDA_SCRIPT_PATH, "../../../" + Constants.GRAPH_FILE_NAME};
//                Process p = Runtime.getRuntime().exec(cmd);
//                p.waitFor();
//
//                // Update Ranks
//                this.updatePagesRanks(true);
//
//            } catch (Exception e) {
//                System.err.println(e.getMessage());
//            }
//        }
//    }
//
//}