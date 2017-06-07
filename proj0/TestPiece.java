import static org.junit.Assert.*;

import org.junit.Test;

public class TestPiece {
	@Test
    public void testConstructor(){
    	Board b = null;
    	Piece p = new Piece(true, b, 0, 0, "bomb");
    	assertEquals(p.hasCaptured(), false);
    	assertEquals(p.hasCaptured(), false);
    	assertEquals(p.isFire(), true);
    	assertEquals(p.isBomb(), true);

    }
    
public static void main(String[] args) {
        jh61b.junit.textui.runClasses(TestPiece.class);
    }

}