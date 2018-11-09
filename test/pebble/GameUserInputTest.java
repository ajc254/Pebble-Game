
package pebble;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.NoSuchElementException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class GameUserInputTest {
    static GameUserInput userInput;
    
    /**
    *Create a new game user input instance for testing.
    */
    @BeforeClass
    public static void setUpClass() {
        userInput = new GameUserInput();
    }
    
    @AfterClass
    public static void tearDownClass() {
        userInput.shutdown();
    }
    
    /**
     * @result Expected to throw NoSuchElementException, proving that the error was handled
     * correctly, and the program asked the user for another valid file.
     * @throws IOException
     */
    @Test(expected = NoSuchElementException.class)
    public void testNumberOfPlayerInputNegative() throws IOException {
        System.out.println("Testing for negative players.");
        ByteArrayInputStream in = new ByteArrayInputStream("-1".getBytes());
        System.setIn(in);
        userInput.setUpKeyboardInput();
        userInput.getNumPlayers();
    }
    
    /**
     * @result Expected to throw NoSuchElementException, proving that the error was handled
     * correctly, and the program asked the user for another valid file.
     * @throws IOException
     */
    @Test(expected = NoSuchElementException.class)
    public void testNumberOfPlayerInputZero() throws IOException {
        System.out.println("Testing for 0 players.");
        ByteArrayInputStream in = new ByteArrayInputStream("0".getBytes());
        System.setIn(in);
        userInput.setUpKeyboardInput();
        userInput.getNumPlayers();
    }
    
    /**
     * @result Expected to throw NoSuchElementException, proving that the error was handled
     * correctly, and the program asked the user for another valid file.
     * @throws IOException
     */
    @Test //not expected because test is valid
    public void testNumberOfPlayerInputPositive() throws IOException {
        System.out.println("Testing for valid number of players.");
        ByteArrayInputStream in = new ByteArrayInputStream("4".getBytes());
        System.setIn(in);
        userInput.setUpKeyboardInput();
        userInput.getNumPlayers();
    }
    
    /**
     * @result Expected to throw NoSuchElementException, proving that the error was handled
     * correctly, and the program asked the user for another valid file.
     * @throws IOException
     */
    @Test(expected = NoSuchElementException.class)
    public void testNumberOfPlayerInputNull() throws IOException {
        System.out.println("Testing for empty players.");
        ByteArrayInputStream in = new ByteArrayInputStream("".getBytes());
        System.setIn(in);
        userInput.setUpKeyboardInput();
        userInput.getNumPlayers();
    }
    
    /**
     * @result Expected to throw NoSuchElementException, proving that the error was handled
     * correctly, and the program asked the user for another valid file.
     * @throws IOException
     */
    @Test(expected = NoSuchElementException.class)
    public void testNumberOfPlayerInputNotInteger() throws IOException {
        System.out.println("Testing for non-number input for players.");
        ByteArrayInputStream in = new ByteArrayInputStream("h".getBytes());
        System.setIn(in);
        userInput.setUpKeyboardInput();
        userInput.getNumPlayers();
    }
    
    /**
     * @result Expected to throw NoSuchElementException, proving that the error was handled
     * correctly, and the program asked the user for another valid file.
     * @throws IOException
     */
    @Test(expected = NoSuchElementException.class)
    public void testNonExistingFile() throws IOException {
        System.out.println("Testing for non existing file.");
        String simulatedUserInput = "/filethatwontexists.txt";
        ByteArrayInputStream in = new ByteArrayInputStream(simulatedUserInput.getBytes());
        System.setIn(in);
        userInput.setUpKeyboardInput();
        userInput.fillBag('x',1);
    }
    
    /**
     * @result Expected to throw NoSuchElementException, proving that the error was handled
     * correctly, and the program asked the user for another valid file.
     * @throws IOException
     */
    @Test(expected = NoSuchElementException.class)
    public void testWrongFileType() throws IOException {
        System.out.println("Testing for wrong file type.");
        String simulatedUserInput = "TestFiles/WrongFileType.rtf";
        ByteArrayInputStream in = new ByteArrayInputStream(simulatedUserInput.getBytes());
        System.setIn(in);
        userInput.setUpKeyboardInput();
        userInput.fillBag('x',1);
    }
    
    /**
     * @result Expected to throw NoSuchElementException, proving that the error was handled
     * correctly, and the program asked the user for another valid file.
     * @throws IOException
     */
    @Test(expected = NoSuchElementException.class)
    public void testNegativePebbles() throws IOException {
        System.out.println("Testing for negative pebbles.");
        String simulatedUserInput = "TestFiles/NegativePebbles.txt";
        ByteArrayInputStream in = new ByteArrayInputStream(simulatedUserInput.getBytes());
        System.setIn(in);
        userInput.setUpKeyboardInput();
        userInput.fillBag('x',1);
    }
    
    /**
     * @result Expected to throw NoSuchElementException, proving that the error was handled
     * correctly, and the program asked the user for another valid file.
     * @throws IOException
     */
    @Test(expected = NoSuchElementException.class)
    public void testEmptyPebbles() throws IOException {
        System.out.println("Testing for empty pebbles.");
        String simulatedUserInput = "TestFiles/EmptyPebbles.txt";
        ByteArrayInputStream in = new ByteArrayInputStream(simulatedUserInput.getBytes());
        System.setIn(in);
        userInput.setUpKeyboardInput();
        userInput.fillBag('x',1);
    }
    
    /**
     * @result No exception should be thrown, proving the file was accepted and
     * another was not requested.
     * @throws IOException
     * */
    @Test
    public void testValidPebbles() throws IOException {
        System.out.println("Testing for valid pebbles.");
        String simulatedUserInput = "TestFiles/4PlayerValidPebbles.txt";
        ByteArrayInputStream in = new ByteArrayInputStream(simulatedUserInput.getBytes());
        System.setIn(in);
        userInput.setUpKeyboardInput();
        userInput.fillBag('x',1);
    }
    
    /**
     * @result Expected to throw NoSuchElementException, proving that the error was handled
     * correctly, and the program asked the user for another valid file.
     * @throws IOException
     */
    @Test(expected = NoSuchElementException.class)
    public void testNotEnoughPebbles() throws IOException {
        System.out.println("Testing for not enough pebbles.");
        String simulatedUserInput = "TestFiles/NotEnoughPebbles.txt";
        ByteArrayInputStream in = new ByteArrayInputStream(simulatedUserInput.getBytes());
        System.setIn(in);
        userInput.setUpKeyboardInput();
        userInput.fillBag('x',1);
    }
}
