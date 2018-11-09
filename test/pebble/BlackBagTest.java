
package pebble;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class BlackBagTest {
    
    static WhiteBag testWhiteBag;
    static BlackBag testBlackBag;
    
    /**
     * Constructor for black bag test.
     */
    public BlackBagTest() {
    }
    
    /**
     * Setup a black bag with no pebbles and a paired white bag with pebbles [1..5].
     */
    @BeforeClass
    public static void setUpClass() {
        List<Integer> pebbles = new ArrayList<>(5);
        pebbles.addAll(Arrays.asList(1,2,3,4,5));
        testWhiteBag = new WhiteBag(pebbles, "a");
        
        List<Integer> pebbles2 = new ArrayList<>();
        pebbles2.addAll(Arrays.asList());
        testBlackBag = new BlackBag(pebbles2, "a");
        testBlackBag.setPair(testWhiteBag);
    }

    /**
     * Test of getPair method, of class BlackBag.
     */
    @Test
    public void testGetPair() {
        System.out.println("Testing getPair Black Bag method.");
        assertEquals("Retreived incorrect pair", testBlackBag.getPair(), testWhiteBag);
    }
    
    /**
     * Test of refill method, of class BlackBag.
     */
    @Test
    public void testRefill() {
         System.out.println("Testing getRefill Black Bag method.");
        testBlackBag.refill();
        assertEquals("Black bag should have old content of white bag", testBlackBag.getPebbles(), Arrays.asList(1,2,3,4,5));
        assertEquals("White bag should be empty", testWhiteBag.getPebbles(), Arrays.asList());
    }
    
}
