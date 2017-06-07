import java.util.Comparator;

/**
 * RadiusComparator.java
 */

public class TSTNodeComparator implements Comparator<TSTNode> {

    public TSTNodeComparator() {
    }

    public int compare(TSTNode node1, TSTNode node2) {
        return Double.compare(node1.maxVal, node2.maxVal);
    }
}