import org.junit.Test;
import static org.junit.Assert.*;

/** ULLMapTest. You should write additional tests.
 *  @author Josh Hug
 */

public class TrieTest {
    @Test
    public void testBasic() {
        Trie t = new Trie();
        // t.insert("dog");
        // assertEquals(t.find("dog", true), true);
        // assertEquals(t.find("do", false), true);
        // assertEquals(t.find("doge", true), false);
        // assertEquals(t.find("doge", false), false);
        // t.insert("doge");
        // assertEquals(t.find("doge", true), true);
        // assertEquals(t.find("doge", false), true);
        t.insert("hello");
        System.out.println(t.find("hello", true));
        t.insert("hey");
        System.out.println(t.find("hey", true));

        t.insert("goodday");
        t.insert("goodbye");
        assertEquals(t.find("goodbye", true), true);
        System.out.println(t.find("hell", false));
        System.out.println(t.find("he", false));
        System.out.println(t.find("good", false));
        System.out.println(t.find("bye", false));
        System.out.println(t.find("heyy", false));
        System.out.println(t.find("hell", true));
    }
    /** Runs tests. */
    public static void main(String[] args) {
        jh61b.junit.textui.runClasses(TrieTest.class);
    }
} 