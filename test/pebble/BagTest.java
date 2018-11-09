
package pebble;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

public class BagTest {
    
    Bag testBag;
    
    /**
     * Constructor for bag test.
     */
    public BagTest() {
    }
    
    /**
     * Instantiate a new bag for each test, ensuring bag state is consistent at start of tests,
     * using a bag with an integer array [1..5] and name "x".
     */
    @Before
    public void newBag(){
        List<Integer> pebbles = new ArrayList<>(5);
        pebbles.addAll(Arrays.asList(1,2,3,4,5));
        testBag = new BlackBag(pebbles, "x");
    }

    /**
     * Test getName method, of class Bag.
     */
    @Test
    public void testGetName() {
        System.out.println("Testing getName bag method.");
        assertEquals("Returned name is not the same as the name set", testBag.getName(), "x");
    }

    /**
     * Test getPebbles method, of class Bag.
     */
    @Test
    public void testGetPebbles() {
        System.out.println("Testing getPebbles bag method.");
        assertEquals("Returned pebbles are not equal to the ones set", testBag.getPebbles(), Arrays.asList(1,2,3,4,5));
    }

    /**
     * Test getSize method, of class Bag.
     */
    @Test
    public void testSize() {
        System.out.println("Testing getSize bag method.");
        assertEquals("Incorrect size returned", testBag.size(), 5);
    }

    /**
     * Test add method, of class Bag.
     */
    @Test
    public void testAdd() {
        System.out.println("Testing getAdd bag method.");
        testBag.add(6);
        assertEquals("Pebble was added incorrectly", testBag.getPebbles(), Arrays.asList(1,2,3,4,5,6));
    }

    /**
     * Test remove method, of class Bag.
     */
    @Test
    public void testRemove() {
        System.out.println("Testing getRemove bag method.");
        testBag.remove(0);
        assertEquals("Pebble was not removed correctly", testBag.getPebbles(), Arrays.asList(2,3,4,5));
    }

    /**
     * Test get method, of class Bag.
     */
    @Test
    public void testGet() {
        System.out.println("Testing getGet bag method.");
        assertEquals("Pebble was not retrieved correctly", testBag.get(0), 1);
    }
}
