package function;

import java.io.BufferedReader;
import java.io.FileReader;

import static org.junit.Assert.assertEquals;

public class FileContentComparator {
    public void compare() {
        try {
            BufferedReader lead = new BufferedReader(new FileReader("lead"));
            BufferedReader gold = new BufferedReader(new FileReader("src/test/java/function/gold"));
            String line;
            while ((line = gold.readLine()) != null)
                assertEquals(line, lead.readLine());
            assertEquals(null, lead.readLine());
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
