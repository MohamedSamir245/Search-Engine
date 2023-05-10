import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class test {

    public static void main(String[] args) throws IOException {

            LocalDate Today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String TodayFormatted = Today.format(formatter);
            String test="05/05/2023";
        LocalDate date1 = LocalDate.parse(test, formatter);

            System.out.println(TodayFormatted);
        System.out.println(test);


        long daysBetween = ChronoUnit.DAYS.between(date1,Today);

        System.out.println(daysBetween);


    }

}
