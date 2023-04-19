package Crawler;

// TODO: robot.txt
// TODO: save state

// ----------------- Libraries -----------------------

// *** Reading data from file ***
import java.io.File; // Import the File class
import java.io.FileNotFoundException; // Import this class to handle errors
import java.nio.charset.StandardCharsets;
import java.util.Scanner; // Import the Scanner class to read text files

// *** Reading data from console ***
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

// *** Connecting **
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

// ** Data Structures **
import java.util.LinkedList;
import java.util.Queue;
import java.util.ArrayList;

// ** link Normalization **
import java.net.URL;
import java.net.URLEncoder;

//  ---------------------   Code    ----------------------------
public class Main {

    public static void main(String[] args) {
        int ThreadsNo = ReadThreadNumber();

        // Threads Crawler
        ThreadsCrawler myThreadsCrawler = new ThreadsCrawler(ThreadsNo);
    }

    private static int ReadThreadNumber() {
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
        // Printing the read line
        System.out.println(ThreadsNo);

        return ThreadsNo;
    }

}

class ThreadsCrawler {
    public ThreadsCrawler(int ThreadsNum) {
        System.out.println("Crawler: Threads start");
        run(ThreadsNum);

    }

    private void run(int ThreadsNum) {
        ArrayList<Thread> threads = new ArrayList<>();
        main c = new main();

        CreateThreads(threads, ThreadsNum, c);
        StartThreads(threads, ThreadsNum);
        JoinThreads(threads, ThreadsNum);

        System.out.println("Crawler: Print Out");

        System.out.println("Crawler: Finish.");
    }

    private void CreateThreads(ArrayList<Thread> threads, int ThreadsNum, main c) {
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

class main implements Runnable {

    private final Queue<String> links = new LinkedList<>();
    private final ArrayList<String> visited = new ArrayList<>();
    private final ArrayList<Document> docs = new ArrayList<>();
    private static final Object myLock = new Object();

    public main() {
        System.out.println("Crawler: Start...");

        System.out.println("Crawler: Read seedLinks");
        ReadSeedLinks();

    }

    private void ReadSeedLinks() {
        try {
            File myObj = new File("src/Crawler/seedLinks.txt");
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

    private void crawler() throws Exception {
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

}