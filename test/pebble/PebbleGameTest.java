
package pebble;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import java.lang.reflect.*;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PebbleGameTest { 
    
    static PebbleGame game;
    
    static WhiteBag whiteBagA;
    static WhiteBag whiteBagB;
    static WhiteBag whiteBagC;
    
    static BlackBag blackBagX;
    static BlackBag blackBagY;
    static BlackBag blackBagZ;
    
    static PebbleGame.Player player;
    static PebbleGame.Winner winner;
    static WhiteBag nextDepositBag;

    static PebbleGame.Player[] allPlayers;
    
    /**
    * Create a new instance of a PebbleGame.
    * @result A game will be re-instantiated and all the reflection will be
    *           set up so that the private variables are visible for these tests.
    * @throws NoSuchFieldException
    * @throws IllegalAccessException
    */
    public static void newGame()  throws NoSuchFieldException, IllegalAccessException {
        game = new PebbleGame(4,new ArrayList<>(),new ArrayList<>(),new ArrayList<>());
              
        Class gameClass = PebbleGame.class;
                    
        Field bagA = gameClass.getDeclaredField("whiteBagA");
        Field bagB = gameClass.getDeclaredField("whiteBagB");
        Field bagC = gameClass.getDeclaredField("whiteBagC");
        Field bagX = gameClass.getDeclaredField("blackBagX");
        Field bagY = gameClass.getDeclaredField("blackBagY");
        Field bagZ = gameClass.getDeclaredField("blackBagZ");
        Field players = gameClass.getDeclaredField("allPlayers");

        bagA.setAccessible(true);
        bagB.setAccessible(true);
        bagC.setAccessible(true);
        bagX.setAccessible(true);
        bagY.setAccessible(true);
        bagZ.setAccessible(true);
        players.setAccessible(true);
        
        whiteBagA = (WhiteBag) bagA.get(game);
        whiteBagB = (WhiteBag) bagB.get(game);
        whiteBagC = (WhiteBag) bagC.get(game);
        blackBagX = (BlackBag) bagX.get(game);
        blackBagY = (BlackBag) bagY.get(game);
        blackBagZ = (BlackBag) bagZ.get(game);
        allPlayers = (PebbleGame.Player[]) players.get(game);
    }
   
    /**
     * Make a new game instance to use on many of the tests.
     * @throws NoSuchFieldException
     * @throws IllegalAccessException 
     */
    @BeforeClass
    public static void setUpClass() throws NoSuchFieldException, IllegalAccessException {
        newGame();
    }
    
    /**
    * Clears all black and white bags, re-fills black bags to original state and
    * makes a new player.
    * @result Empty white bags and 3 black bags with 10 pebbles of size 10 each.
    * @throws NoSuchFieldException
    * @throws IllegalAccessException
    */
    public void resetGame() throws NoSuchFieldException, IllegalAccessException {
        blackBagX.getPebbles().clear();
        blackBagY.getPebbles().clear();
        blackBagZ.getPebbles().clear();
        whiteBagA.getPebbles().clear();
        whiteBagB.getPebbles().clear();
        whiteBagC.getPebbles().clear();
        blackBagX.getPebbles().addAll(Arrays.asList(10,10,10,10,10,10,10,10,10,10));
        blackBagY.getPebbles().addAll(Arrays.asList(10,10,10,10,10,10,10,10,10,10));
        blackBagZ.getPebbles().addAll(Arrays.asList(10,10,10,10,10,10,10,10,10,10));
        
        player = game.new Player(1);
    }
    
    /**
    * Reflect the nextDepositBag variable from the nested player class.
    * @return nextDepositBag from nested player class. 
    * @throws NoSuchFieldException
    * @throws IllegalAccessException
    */
    public WhiteBag getPlayerNextDepositBag() throws NoSuchFieldException, IllegalAccessException {
        Class playerClass = PebbleGame.Player.class;
        Field nextBag = playerClass.getDeclaredField("nextDepositBag");
        nextBag.setAccessible(true);
        
        return (WhiteBag) nextBag.get(player);
    }
    
    /**
    * Reflect the hand variable from the nested player class.
    * @param player the instance of a player to get the hand of
    * @return Hand from nested player class.
    * @throws NoSuchFieldException
    * @throws IllegalAccessException
    */
    public List<Integer> getPlayerHand(PebbleGame.Player player) throws NoSuchFieldException, IllegalAccessException{
        Class playerClass = PebbleGame.Player.class;
        Field hand = playerClass.getDeclaredField("hand");
        hand.setAccessible(true);
        
        return (List<Integer>) hand.get(player);
    }
    
    /**
    * Test that the fillHand() method works for a player
    * @result Each bag has been correctly interacted with.
    * @throws NoSuchFieldException
    * @throws IllegalAccessException
    */
    @Test
    public void testFillHand() throws NoSuchFieldException, IllegalAccessException {
        System.out.println("Testing fillHand player method.");
        resetGame();
        
        BlackBag emptyBag = null;
        int numEmptyBags = 0;
        int numFullBags = 0;
        
        player.fillHand();
        
        BlackBag[] blackBags = {blackBagX, blackBagY, blackBagZ};
        
        for( BlackBag blackBag : blackBags ) {
            if(blackBag.size() == 0) {
                emptyBag = blackBag;
                numEmptyBags++;
            }
            if(blackBag.size() == 10) {
                numFullBags++;
            }
        }

        Assert.assertTrue("Player should have 10 pebbles of size 10, adding up to 100", player.getHandValue() == 100);
        Assert.assertTrue("Player should have 10 pebbles in their hand", getPlayerHand(player).size() == 10);
        Assert.assertTrue("Next deposit bag should be the pair of the empty bag", getPlayerNextDepositBag() == emptyBag.getPair());
        Assert.assertTrue("There should be one empty bag", numEmptyBags == 1);
        Assert.assertTrue("There should be two full bags", numFullBags == 2);
    }
    
    /**
    * Test that the draw() method works for a player
    * @result One black bag should have 1 less pebble and that pebble should
    * be in the players hand.
    * @throws NoSuchFieldException
    * @throws IllegalAccessException
    */
    @Test
    public void testDraw() throws NoSuchFieldException, IllegalAccessException {
        System.out.println("Testing draw player method.");
        resetGame();
        
        player.draw();
        int numLength10Bags = 0;
        int numLength9Bags = 0;
        
        BlackBag[] blackBags = {blackBagX, blackBagY, blackBagZ};
        
        for( BlackBag blackBag : blackBags ) {
            if(blackBag.size() == 9) {
                numLength9Bags++;
            }
            else if( blackBag.size() == 10){
                numLength10Bags++;
            }
        }
        Assert.assertTrue("Two bags did not retain their pebble", numLength10Bags == 2);
        Assert.assertTrue("No pebble was removed from the draw", numLength9Bags == 1);
        Assert.assertTrue("Player's hand was incorrect after draw", player.getHandValue() == 10);
    } 
    
    /**
    * Test that the refill() method works for a player. Invoked by calling
    *   draw() on a bag with 1 pebble and having a white bag with some pebbles in.
    * @result The chosen black bag is now full and the paired white bag is now
    * empty. No other bags have been interacted with.
    * @throws NoSuchFieldException
    * @throws IllegalAccessException
    */
    @Test
    public void testRefill() throws NoSuchFieldException, IllegalAccessException {
        System.out.println("Testing drawing from a bag with 1 pebble.");
        resetGame();
        
        BlackBag[] blackBags = {blackBagX, blackBagY, blackBagZ};
        WhiteBag[] whiteBags = {whiteBagA, whiteBagB, whiteBagC};
        
        for( WhiteBag whiteBag : whiteBags ){
            whiteBag.getPebbles().addAll(blackBagX.getPebbles());
        }
        
        for( BlackBag blackBag : blackBags ) {
            blackBag.getPebbles().clear(); 
            blackBag.add(10);
        }
        
        player.draw();
        
        int numFullBlackBags = 0;
        int numLength1BlackBags = 0;
        int numFullWhiteBags = 0;
        int numEmptyWhiteBags = 0;
        
        for( BlackBag blackBag : blackBags ){
            if(blackBag.size() == 10){
                numFullBlackBags++;
            } else if (blackBag.size() == 1){
                numLength1BlackBags++;
            }
        }
        for( WhiteBag whiteBag : whiteBags ){
            if(whiteBag.size() == 10){
                numFullWhiteBags++;
            } else if (whiteBag.size() == 0){
                numEmptyWhiteBags++;
            }
        }
        
        Assert.assertTrue("There should be 1 refilled black bag", numFullBlackBags == 1);
        Assert.assertTrue("There should be 2 black bags with 1 pebble", numLength1BlackBags == 2);
        Assert.assertTrue("There should be 2 full white bags", numFullWhiteBags == 2);
        Assert.assertTrue("There should be 1 empty white bag", numEmptyWhiteBags == 1);
        
    } 
    
    /**
    * Test that the discard() method works for a player
    * @result The player has lost a pebble from its hand and it is now present
    * in the correct white bag.
    * @throws NoSuchFieldException
    * @throws IllegalAccessException
    */
    @Test
    public void testDiscard() throws NoSuchFieldException, IllegalAccessException {
        System.out.println("Testing discard player method.");
        resetGame();
        
        player.draw();
        
        nextDepositBag = getPlayerNextDepositBag();
        
        player.discard();
        int numEmptyBags = 0;
        
        WhiteBag[] whiteBags = {whiteBagA, whiteBagB, whiteBagC};
        
        for( WhiteBag whiteBag : whiteBags ) {
            if(whiteBag.size() == 0) {
                numEmptyBags++;
            }
        }
        
        Assert.assertTrue("Paired bag should have length 1", nextDepositBag.size() == 1); //checks pebble went into the correct white bag
        Assert.assertTrue("There should only be 2 empty white bags", numEmptyBags == 2); 
        Assert.assertTrue("Player's hand was incorrect after discard", player.getHandValue() == 0); //player handvalue will be empty
    } 
    
    /**
    * Test that the checkWon() method works for a player
    * @result There should be an instance of Winner that has the winning player
    * as its winner attribute.
    * @throws NoSuchFieldException
    * @throws IllegalAccessException
    */
    @Test
    public void testCheckWon() throws NoSuchFieldException, IllegalAccessException {
        System.out.println("Testing checkWon player method.");
        resetGame();
        
        player.fillHand();
        player.checkWon();
        
        Class gameClass = PebbleGame.class;
        Field winnerStatus = gameClass.getDeclaredField("winnerStatus");
        winnerStatus.setAccessible(true);
        
        PebbleGame.Winner winnerObject = (PebbleGame.Winner) winnerStatus.get(game);
        
        Class winnerClass = PebbleGame.Winner.class;
        Field winner = winnerClass.getDeclaredField("winner");
        winner.setAccessible(true);
        
        Assert.assertTrue("Winning player should be this player", winner.get(winnerObject) == player);
    }
    
    /**
    * Interrupt 4 players while they are still playing.
    * @result Graceful shutdown.
    * @throws NoSuchFieldException
    * @throws IllegalAccessException
    */
    @Test
    public void testInterruptedRun() throws NoSuchFieldException, IllegalAccessException {
        System.out.println("Testing an interrupted game.");
        newGame();
        
        blackBagX.getPebbles().addAll(Arrays.asList(11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11));
        blackBagY.getPebbles().addAll(Arrays.asList(11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11));
        blackBagZ.getPebbles().addAll(Arrays.asList(11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11,11));

        ExecutorService es = Executors.newFixedThreadPool(4);
        for (PebbleGame.Player player : allPlayers) {
            es.execute( new Thread(player));
        }
        es.shutdown();
        
        try{
            es.awaitTermination(2, TimeUnit.SECONDS);
        } catch( InterruptedException e ) {
            e.printStackTrace();
        }
        
        if(!es.isTerminated()){
            es.shutdownNow();
        }
    }
    
    /**
    * Test that the run() method works for a player
    * @result Every pebbles that is inputted should be exactly the same as every 
    * pebble after the game has finished. 
    * @throws NoSuchFieldException
    * @throws IllegalAccessException
    * @throws IOException
    */
    @Test
    public void testSuccessfulRun() throws IOException, NoSuchFieldException, IllegalAccessException {
        System.out.println("Testing a successful game.");
        newGame();
        
        blackBagX.getPebbles().addAll(Arrays.asList(1, 2, 3, 4, 5, 7, 8, 9, 10, 12, 15, 18, 20, 21, 23, 25, 26, 10, 28, 30, 31, 35, 38, 39, 10, 42, 43, 44, 46, 10, 10, 10, 51, 53, 10, 57, 59, 10, 62, 32, 10, 67, 10, 5));
        blackBagY.getPebbles().addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 11, 12, 13, 14, 15, 16, 17, 20, 21, 22, 23, 25, 28, 29, 31, 32, 33, 34, 35, 36, 37, 40, 10, 43, 48, 10, 10, 52, 54, 10, 56, 58, 10, 62, 10, 10));
        blackBagZ.getPebbles().addAll(Arrays.asList(1, 3, 5, 7, 12, 15, 16, 17, 18, 20, 21, 22, 24, 26, 27, 28, 29, 30, 31, 32, 34, 10, 39, 40, 41, 10, 43, 45, 46, 47, 10, 52, 10, 10, 10, 57, 58, 10, 62, 10, 64, 10, 68, 10));
        
        List<Integer> allStartPebbles = new ArrayList<>();
        allStartPebbles.addAll(blackBagX.getPebbles());
        allStartPebbles.addAll(blackBagY.getPebbles());
        allStartPebbles.addAll(blackBagZ.getPebbles());

        Thread[] t = new Thread[4];
        int i = 0;
        for (PebbleGame.Player player : allPlayers) {
            t[i] = new Thread(player);
            t[i].start();
            i++;
        }
        
        for (Thread thread : t) {
            try{
                thread.join();
            }
            catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        List<Integer> allEndPebbles = new ArrayList<>();
        allEndPebbles.addAll(blackBagX.getPebbles());
        allEndPebbles.addAll(blackBagY.getPebbles());
        allEndPebbles.addAll(blackBagZ.getPebbles());
        
        allEndPebbles.addAll(whiteBagA.getPebbles());
        allEndPebbles.addAll(whiteBagB.getPebbles());
        allEndPebbles.addAll(whiteBagC.getPebbles());
        
        for(PebbleGame.Player player : allPlayers){
            allEndPebbles.addAll(getPlayerHand(player));
        }
        
        Collections.sort(allStartPebbles);
        Collections.sort(allEndPebbles);
        
        Assert.assertTrue("Pebbles got lost or added", allStartPebbles.equals(allEndPebbles));
    }
}
