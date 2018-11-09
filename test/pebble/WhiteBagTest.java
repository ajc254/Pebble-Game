
package pebble;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class WhiteBagTest {
    
    static WhiteBag testWhiteBag;
    
    public WhiteBagTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        List<Integer> pebbles = new ArrayList<>(5);
        pebbles.addAll(Arrays.asList(1,2,3,4,5));
        testWhiteBag = new WhiteBag(pebbles, "a");
    }
    
    @Test
    public void testClear() {
         System.out.println("Testing clear White Bag method.");
        testWhiteBag.clear();
        assertEquals( "White bag was not cleared", testWhiteBag.getPebbles(), Arrays.asList());
    }
}
