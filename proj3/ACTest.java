import org.junit.Test;
import static org.junit.Assert.*;

/** ULLMapTest. You should write additional tests.
 *  @author Josh Hug
 */

public class ACTest {
    @Test
    public void testBasic() {
        String[] terms = new String[5];
        double[] weights = new double[5];
        terms[0] = "hi";
        terms[1] = "I";
        terms[2] = "am";
        terms[3] = "a";
        terms[4] = "test";
        weights[0] = 1;
        weights[1] = 2;
        weights[2] = 3;
        weights[3] = 4;
        weights[4] = 5;
        Autocomplete a = new Autocomplete(terms, weights);
        System.out.println(a.weightOf("test"));
    }
    /** Runs tests. */
    public static void main(String[] args) {
        jh61b.junit.textui.runClasses(ACTest.class);
    }
} 