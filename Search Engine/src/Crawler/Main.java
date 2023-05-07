// TODO: save State

// ----------------- Libraries -----------------------

// *** Reading data from file ***
import java.io.File; // Import the File class
import java.io.FileNotFoundException; // Import this class to handle errors
import java.nio.charset.StandardCharsets;
import java.util.*;

// *** Reading data from console ***
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

// *** Connecting **
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
// HTTP
import java.net.HttpURLConnection;

// *** Robot rules check (as Regular Expressions)
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

// ** Data Structures ** (Queue)

// ** link Normalization **
import java.net.URL;
import java.net.URLEncoder;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import java.net.HttpURLConnection;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

//  ---------------------   Code    ----------------------------
public class Main {

    public static void main(String[] args) {

        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb+srv://Admin:admin@cluster0.srt79fu.mongodb.net/test"));

        MongoDatabase MongoDB = mongoClient.getDatabase("MongoDB");
        MongoCollection<org.bson.Document> CrawlerCollection = MongoDB.getCollection("CrawlerCollection");

        int ThreadsNo = ReadThreadNumber();

    private static ThreadsCrawler myThreadsCrawler = new ThreadsCrawler();

    public static void main(String[] args) {

        // I return what you want :)
        // getDocs();
        // System.out.println(getDocs().get(20));

    }

    public static ArrayList<Document> getDocs() {
        return myThreadsCrawler.getDocs();
    }

}

class ThreadsCrawler {
    public ThreadsCrawler() {

        int ThreadsNum = ReadThreadNumber();
        System.out.println("Crawler: Threads start");
        run(ThreadsNum);

    }

    private static Crawler c = new Crawler();

    private int ReadThreadNumber() {
        int ThreadsNo = 0;

        System.out.println("Admin: Reading threads number");
        System.out.print("Please enter no. of threads.. ");
        // Enter data using BufferReader
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        // Reading data using readLine
        try {
            ThreadsNo = Integer.parseInt(reader.readLine());
            while (ThreadsNo <= 0) {
                System.out.print("Enter valid value.. ");
                ThreadsNo = Integer.parseInt(reader.readLine());
            }

        } catch (IOException e) {
            e.getStackTrace();
        }
        // Printing ThreadsNo
        System.out.println(ThreadsNo);

        return ThreadsNo;
    }

    private void run(int ThreadsNum) {
        ArrayList<Thread> threads = new ArrayList<>();

        CreateThreads(threads, ThreadsNum, c);
        StartThreads(threads, ThreadsNum);
        JoinThreads(threads, ThreadsNum);

        System.out.println("Crawler: Print Out");

        System.out.println("Crawler: Finish.");
    }

    public ArrayList<Document> getDocs() {
        return c.getDocs();
    }

    private void CreateThreads(ArrayList<Thread> threads, int ThreadsNum, Crawler c) {
        for (int i = 0; i < ThreadsNum; i++) {
            threads.add(new Thread(c));
        }
    }

    private void StartThreads(ArrayList<Thread> threads, int ThreadsNum) {
        for (int i = 0; i < ThreadsNum; i++) {
            threads.get(i).start();
        }
    }

    private void JoinThreads(ArrayList<Thread> threads, int ThreadsNum) {
        for (int i = 0; i < ThreadsNum; i++) {
            try {
                threads.get(i).join();
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
    }
}

class Crawler implements Runnable {

    private final Queue<String> links = new LinkedList<>();
    private final ArrayList<String> visited = new ArrayList<>();
    private final ArrayList<Document> docs = new ArrayList<>();
    private static final Object myLock = new Object();

    public Crawler() {
        System.out.println("Crawler: Start...");

        System.out.println("Crawler: Read seedLinks");
        ReadSeedLinks();

    }

    private void ReadSeedLinks() {
        try {
            File myObj = new File("src/seedLinks.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                links.add(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void run() {
        System.out.println("Thread " + Thread.currentThread().getId() + " Entered run");
        doWork();
        System.out.println("Thread " + Thread.currentThread().getId() + " Left run");
    }

    private void doWork() {
        try {
            crawler();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public ArrayList<Document> getDocs() {
        return docs;
    }

    // =================================================================
    // =========================== Crawling ======================
    // =================================================================

    private void crawler(MongoCollection<org.bson.Document> CrawlerCollection) throws Exception {
        int MAX_CRAWLED = 60; // KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
        String link;
        while (links.isEmpty()) {
            synchronized (myLock) {
                myLock.wait();
            }
        }
        while (!links.isEmpty() && docs.size() < MAX_CRAWLED) {
            synchronized (myLock) {
                while (links.isEmpty())
                    myLock.wait();
                link = links.poll();
            }

            if (link != null && !visited.contains(toCompactString(link)))
                crawl(link);
        }
    }

    private void crawl(String link) throws Exception {
        // check if disallowed link in Robot.txt
        if (!isAllowedLink(link)) {
            // it is disallowed then return
            visited.add(toCompactString(link));
            return;
        }
        // it is allowed then fetch it
        Document doc = request(link);

        if (doc != null) {
            System.out.println("*** " + docs.size() + " ***");
            visited.add(toCompactString(link));
            docs.add(doc);
            for (Element extractedLink : doc.select("a[href]")) {
                String nextLink = extractedLink.absUrl("href");
                synchronized (myLock) {
                    links.add(nextLink);
                    myLock.notify();
                }
            }
        } else
            System.out.println("Not Connected");
    }

    private Document request(String link) {
        try {
            Connection con = Jsoup.connect(link);
            Document doc = con.get();

            if (con.response().statusCode() == 200) {
                System.out.println("Thread " + Thread.currentThread().getId() + " | seedLink: " + link); // Delete
                System.out.println("Title : " + doc.title()); // Delete

                org.bson.Document d = new org.bson.Document("URL", link);
                d.append("Document", doc);

                if (CrawlerCollection.find(d).first() == null)
                    CrawlerCollection.insertOne(d);

                return doc;
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    private String normalizeUrl(String url) throws Exception {
        URL parsedUrl = new URL(url);
        String protocol = parsedUrl.getProtocol().toLowerCase();
        String host = parsedUrl.getHost().toLowerCase();
        int port = parsedUrl.getPort();
        String path = parsedUrl.getPath();
        String query = parsedUrl.getQuery();

        // Remove default ports from URL
        if ((protocol.equals("http") && port == 80) || (protocol.equals("https") && port == 443)) {
            port = -1;
        }

        // Encode special characters in query parameters
        if (query != null) {
            query = URLEncoder.encode(query, StandardCharsets.UTF_8);
            query = query.replace("+", "%20");
            query = query.replace("%21", "!");
            query = query.replace("%27", "'");
            query = query.replace("%28", "(");
            query = query.replace("%29", ")");
            query = query.replace("%7E", "~");
        }

        // Concatenate components into normalized URL string
        StringBuilder sb = new StringBuilder();
        sb.append(protocol).append("://").append(host);
        if (port != -1) {
            sb.append(":").append(port);
        }
        sb.append(path);
        if (query != null) {
            sb.append("?").append(query);
        }

        return sb.toString();
    }

    private String toCompactString(String url) throws Exception {
        String normalizedUrl = normalizeUrl(url);
        String DELIMITER = "/";
        String[] components = normalizedUrl.split(DELIMITER);

        // Concatenate components into compact string
        StringBuilder sb = new StringBuilder();
        for (String component : components) {
            if (!component.isEmpty()) {
                sb.append(component.charAt(0));
            }
            if (component.length() > 1) {
                sb.append(component.substring(1));
            }
            sb.append(DELIMITER);
        }

        return sb.toString();
    }

    // ========================================================
    // ====================== robot.txt ==================
    // ========================================================

    // HashMap with key is hostLink and value is ArrayList<String> of RobotFileLines
    private static HashMap<String, ArrayList<String>> RobotsTxt = new HashMap<>();

    // Take URL --> return robotFileLine in ArrayList
    private static void fetchRobotTxt(URL url) {
        // list of robots.txt lines
        ArrayList<String> rulesLines = new ArrayList<>();

        try {
            // Get connection
            URL link = new URL(url.getProtocol() + "://" + url.getHost() + "/robots.txt");
            HttpURLConnection connection = (HttpURLConnection) link.openConnection();

            // Open robots.txt as file and read line by line
            BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;

            while ((line = input.readLine()) != null) {
                rulesLines.add(line.toLowerCase());
            }
        } catch (Exception e) {
            return;
        }

        synchronized (myLock) {
            RobotsTxt.put(url.getHost(), rulesLines);
        }
    }

    // Takes robotFile lines in ArrayList and crawlerUserAgent --> Return Disallowed
    // rules
    private static ArrayList<String> praseRobotTxt(ArrayList<String> robotFile, String userAgent) {
        ArrayList<String> disallowed = new ArrayList<>();
        // Rule
        String ruleUserAgent = null;

        for (String line : robotFile) {

            line = line.toLowerCase();

            // get ruleUserAgent
            if (line.startsWith("user-agent:")) {
                ruleUserAgent = line.substring(line.indexOf(":") + 1).trim();
            }
            // get disallowRule
            else if (userAgent.equals(ruleUserAgent) && line.startsWith("disallow:")) {
                String rule = line.substring(line.indexOf(":") + 1).trim();

                rule = rule.replaceAll("\\*", ".*"); // Matches any sequence of chars
                rule = rule.replaceAll("\\?", "[?]"); // Matches the question mark char

                if (rule.length() > 0) {
                    disallowed.add(rule);
                    // System.out.println("Rule is: " + rule);
                }
            }
        }

        return disallowed;
    }

    // Takes String hostLink --> Return if robotFile fetched before
    private static boolean isFetchedRobotsTxt(String host) {
        return RobotsTxt.containsKey(host);
    }

    private static boolean checkRules(String link, ArrayList<String> hostRules) {
        for (String rule : hostRules) {
            try {
                Pattern pat = Pattern.compile(rule);
                Matcher matcher = pat.matcher(link);

                if (matcher.find()) {
                    return true;
                }
            } catch (PatternSyntaxException e) {
                // System.err.println(rule + ": pattern exception, " + e.getMessage());
                // Output.log(rule + ": pattern exception, " + e.getMessage());
                e.getStackTrace();
            }
        }
        return false;
    }

    private static boolean isAllowedLink(String link) {
        URL url = null;
        try {
            url = new URL(link);
        } catch (Exception e) {
            e.getStackTrace();
        }
        if (!isFetchedRobotsTxt(url.getHost())) {
            // fetch rules
            fetchRobotTxt(url);

        }

        // check rules
        return !checkRules(link, praseRobotTxt(RobotsTxt.get(url.getHost()), "*"));

    }

    }

     
    
     
    

    

     
    

    
    
        
         
    
    

        
                 
                 

     
            
          
             

           

