/**
 * Node that works specifically for a Ternary Search Trie
 * @author Larry Zhou
 */
public class TSTNode {
    char c;                        // character
    TSTNode left, mid, right;  // left, middle, and right subtries
    double val = 0;                     // value associated with string
    double maxVal = 0;
    String fullWord;
    /**
     * Returns the child of this TSTNode whose char is ch
     * @param ch character to look for
     * @return TSTNode which has the chararacter.
     */
    public TSTNode getChild(char ch) {
        if (mid.c == ch) {
            TSTNode temp = mid;
            return temp;
        }
        if (left != null) {
            if (left.c == ch) {
                TSTNode temp = left;
                return temp;
            }
        }
        if (right != null) {
            if (right.c == ch) {
                TSTNode temp = right;
                return temp;
            }
        }
        return null;
    }
}
