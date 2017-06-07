import org.junit.Test;
import static org.junit.Assert.*;

/** ULLMapTest. You should write additional tests.
 *  @author Josh Hug
 */

public class TSTTest {
    @Test
    public void testBasic() {
        TST t = new TST();
        t.put("smog", 5);
        t.put("buck", 10);
        t.put("sad", 12);
        t.put("spite", 20);
        t.put("spit", 15);
        t.put("spy", 7);
        System.out.println(t.get("spy"));
    }
    /** Runs tests. */
    public static void main(String[] args) {
        jh61b.junit.textui.runClasses(TSTTest.class);
    }
} 