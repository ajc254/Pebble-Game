package pebble;

import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

public final class PebbleGame {
    private final BlackBag blackBagX;
    private final BlackBag blackBagY;
    private final BlackBag blackBagZ;
    private final WhiteBag whiteBagA;
    private final WhiteBag whiteBagB;
    private final WhiteBag whiteBagC;
    private final int numPlayers;
    private final AtomicInteger numPlayersReady;
    private final Winner winnerStatus;
    private volatile boolean notifiedGameReady;
    private final Player[] allPlayers;
    
    /**
     * Make a game with filled black bags, empty white bags and a list of players.
     * 
     * @param numPlayers Number of players for this game instance.
     * @param x List of integers to represent black bag X.
     * @param y List of integers to represent black bag Y.
     * @param z List of integers to represent black bag Z.
     */
    public PebbleGame( int numPlayers, List<Integer> x, List<Integer> y, List<Integer> z ) {
        this.numPlayers = numPlayers;
        blackBagX = new BlackBag( x, "X");
        blackBagY = new BlackBag( y, "Y");
        blackBagZ = new BlackBag( z, "Z");
        
        //Instantiate the whitebags with the same size as its paired black bag.
        whiteBagA = new WhiteBag(new ArrayList<>(blackBagX.size()), "A");
        whiteBagB = new WhiteBag(new ArrayList<>(blackBagY.size()), "B");
        whiteBagC = new WhiteBag(new ArrayList<>(blackBagZ.size()), "C");
        
        //Pair every black bag with its white bag pair.
        blackBagX.setPair(whiteBagA);
        blackBagY.setPair(whiteBagB);
        blackBagZ.setPair(whiteBagC);

        numPlayersReady = new AtomicInteger();
        winnerStatus = new Winner();
        
        //Create a new player instance for the number of players that the user has inputted.
        allPlayers = new Player[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            allPlayers[i] = new Player(i+1);
        }
    }
    
    static class Winner{
        private volatile Boolean noWinner;
        private Player winner;
        
        /**
         * Create a new winner class, without a winner.
         */
        private Winner(){
            noWinner = true;
        }
        
        /**
         * Sets the state of the class to when a winner has been found.
         * @param winner The winner of the game
         */
        private void setWinner(Player winner){
            noWinner = false;
            this.winner = winner;
        }
    }
    
    class Player implements Runnable {
        private final List<Integer> hand;
        private int handValue;
        private WhiteBag nextDepositBag;
        private final String name;
        private int turnCounter;
        private final List<String> gameLog;
        
        /**
         * @param number The number of this player relative to the game, used for naming.
         */
        Player(int number) {
            hand = new ArrayList<>();
            name = "Player " + number;
            gameLog = new ArrayList<>();
        }
        
        /**
         * @return The current weight value the player's hand has.
         */
        int getHandValue() {
            return handValue;
        }
        
        /**
         * Fills the player hand with 10 pebbles from a random black bag.
         */
        void fillHand(){
            BlackBag bag = chooseRandomBag(); //Choose a random bag to build starting hand with.
            int pebbleValue;
            
            //Draw 10 pebbles to fill the players hand.
            for(int i = 0; i < 10; i++){ 
               
                //Get the pebble from the bag and remove from the bag.
                synchronized(bag) {
                    int pebbleIndex = ThreadLocalRandom.current().nextInt( 0, bag.size() ); //Choose a random pebble.
                    pebbleValue = bag.get(pebbleIndex);
                    bag.remove( pebbleIndex );
                }
                //Add the pebble to the player hand, and recalculate hand value.
                hand.add(pebbleValue);
                handValue += pebbleValue;
                gameLog.add(name + " has drawn a " 
                        + pebbleValue + " from bag " + bag.getName() + "\r\n"
                        + name + "'s hand is now: "
                        + hand + "\r\n");
            }
            checkWon(); //Check if the player has drawn a winning hand.
            gameLog.add( name + " now has a full hand using 10 pebbles from bag  " + 
                       bag.getName() + "\r\n");
            nextDepositBag = bag.getPair(); //Set the bag for the first discard.
        }
        
        /**
         * Choose a random black bag.
         * @return The black bag chosen.
         */
        BlackBag chooseRandomBag(){
            
            BlackBag bag = null;
            int bagNum = ThreadLocalRandom.current().nextInt(1, 4);

            switch( bagNum ) {
                case 1:
                    bag = blackBagX;
                    break;
                case 2:
                    bag = blackBagY;
                    break;
                case 3:
                    bag = blackBagZ;
                    break;
                }
            
            return bag; 
        }
        
        /**
         * Draw a pebble from one of the black bags, and check for a winning hand.
         */
        void draw() {
            BlackBag bag = null;
            do {
                bag = chooseRandomBag();
            } while ( bag.size() == 0 );
            
            int drawnPebbleValue;
            
            synchronized(bag) {
                int pebbleIndex = ThreadLocalRandom.current().nextInt( 0, bag.size() ); //Choose a random pebble from the bag.
                drawnPebbleValue = bag.get(pebbleIndex); //Find the pebble and retrieve it's value.
                //Add the pebble to the hand and remove from the bag.
                hand.add(drawnPebbleValue);
                handValue += drawnPebbleValue;
                bag.remove( pebbleIndex );
                
                gameLog.add(name + " has drawn a "
                        + drawnPebbleValue + " from bag " + bag.getName() + "\r\n"
                        + name + "'s hand is now: "
                        + hand
                        + "  (hand value: " + handValue + ")\r\n");
                
                //Check whether the player has won as soon as possible.
                checkWon();
                
                //If the bag is now empty, refill the black bag from the paired white bag.
                if( bag.size() == 0 ) {
                    bag.refill();
                    gameLog.add(name + " refilled" + " bag " + bag.getName() 
                                + " using all pebbles" + " in bag " 
                                + bag.getPair().getName() + "\r\n");
                }
            }
            //Record the next bag to deposit a pebble to.
            nextDepositBag = bag.getPair();
        }
        /**
         * Discard a random pebble from the player hand, to the correct bag.
         */
        void discard() {
             //Pick a random index to discard a pebble from.
            int pebbleDiscardIndex = ThreadLocalRandom.current().nextInt(0, hand.size());
            int pebbleValue = hand.get(pebbleDiscardIndex); //Get the pebble from the chosen index.
            hand.remove(pebbleDiscardIndex); //Remove the pebble from the player's hand.
            handValue -= pebbleValue;
            
            synchronized( nextDepositBag ){
                nextDepositBag.add(pebbleValue); //Add the pebble to the correct white bag.
            }
            gameLog.add(name + " has discarded a " + 
                        pebbleValue + " to bag " + nextDepositBag.getName() +
                        "\r\n" + name + "'s hand is now: "
                        + hand + "\r\n");
        }
        
        /**
         * Evaluates whether the player has a winning hand, if so, exclaim this to the game.
         */
        void checkWon() {
            if( getHandValue() == 100 && winnerStatus.noWinner ) {
                synchronized(winnerStatus){
                    if (getHandValue() == 100 && winnerStatus.noWinner) { //if the player has a winning hand and no other player has already won
                        winnerStatus.setWinner(this);
                        gameLog.add("\r\n" + name + " GOT A WINNING HAND FIRST WITH:\r\n"
                                + hand
                                + "\r\nUsing: " + turnCounter + " turns\r\n");
                    }
                }
            }
        }
             
        /**
         * If the player is interrupted, they do not start another turn.
         */
        void interrupted() {
            gameLog.add("Game took over 1 minute to finish, the game may be Impossible." 
                             + "The log above represents what occurred so far.");
        }
        
        /**
         * Start the game for the player, running until they or another player wins.
         */
        @Override
        public void run() {
            gameLog.add( name + " Game setup. " + name +
                                " is drawing their initial 10 pebbles.\r\n");
            fillHand(); //Draw initial hand of 10 pebbles.
            numPlayersReady.getAndIncrement();
            
                //Check if all players have finished filling their hand.
            if (PebbleGame.this.numPlayersReady.get() != PebbleGame.this.numPlayers) {
                gameLog.add(name + " is now going to wait for other "
                        + "players to finish filling their initial hand.\r\n");
                synchronized (PebbleGame.this.numPlayersReady) {
                    try {
                        //If all players are not ready, wait for them.
                        PebbleGame.this.numPlayersReady.wait();
                        gameLog.add(name + " HAS BEEN TOLD THAT ALL PLAYERS ARE READY "
                                + "THEREFORE IS STARTING TO PLAY.\r\n");
                    } catch (InterruptedException e) {
                        interrupted();
                    }
                }
            }
                //If all players are ready, and they have not already been notified to start, notify them.
            else if (!PebbleGame.this.notifiedGameReady) {
                synchronized(PebbleGame.this.numPlayersReady) {
                    PebbleGame.this.numPlayersReady.notifyAll();
                }
                PebbleGame.this.notifiedGameReady = true;
                gameLog.add( "\r\n" + name + " FINISHED FILLING THEIR HAND LAST SO" +
                             " ANNOUNCED THAT ALL PLAYERS CAN START!\r\n");
            }
            //Keep playing until a winner is announced.
            while(winnerStatus.noWinner && !Thread.currentThread().isInterrupted()) {
                discard();
                draw();
                turnCounter++;
            }
            if( Thread.currentThread().isInterrupted() ){ //Interrupted if the game takes too long to run.
                interrupted();
            }
            //If the announced winner is not this player, add this to the log.
            else if(winnerStatus.winner != this) {
                gameLog.add( "\r\n" + winnerStatus.winner.name + " WON. This player (" + 
                             name + ") ended with hand:\r\n" +
                              hand + "\r\n" + 
                              "Using " + turnCounter + " turns.\r\n");
            }
        }
    }

    public static void main(String[] args) throws IOException, FileNotFoundException { 
        GameUserInput userInput = new GameUserInput();
        userInput.setUpKeyboardInput();
        //Ask for the number of players.
        int numPlayers = userInput.getNumPlayers();
        
        //Ask the player for the pebble files, and setup a game instance with them.
        PebbleGame game = new PebbleGame(numPlayers, userInput.fillBag('X', numPlayers), 
                                        userInput.fillBag('Y', numPlayers),
                                        userInput.fillBag('Z', numPlayers));
        
        //Clear up the System.in scanner to free up resources.
        userInput.shutdown();
        
        //Execute all the players as threads to play the game simultaneously.
        ExecutorService es = Executors.newFixedThreadPool(game.numPlayers);
        for (Player player : game.allPlayers) {
            es.execute( new Thread(player));
        }
        es.shutdown();
        
        //Main thread waits 20s, or until the game finishes.
        try{
            es.awaitTermination(1, TimeUnit.MINUTES);
        } catch( InterruptedException e ) {
            e.printStackTrace();
        } 
        
        //If the game did not finish after 1 minute, stop the game.
        if(!es.isTerminated()){
            es.shutdownNow();
            System.out.println("Simulation ran for over 1 minute. \nSimulation" + 
                    " could be impossible, it has been interrupted for log analysis.");
        }
        
        //Make a directory for the output files, and remove any old output files.
        File outputDirectory = new File("playerOutputs/");
        if(!outputDirectory.mkdir()){
            for(File file : outputDirectory.listFiles()){
                file.delete();
            }
        }
        
        //Write the output files to the created directory.
        for( Player player : game.allPlayers ) {
            Path path = Paths.get("playerOutputs/" + player.name + ".txt");
            Files.write(path, player.gameLog, StandardCharsets.UTF_8);
        }
        System.out.println("Simulation ended, output files have been added in local directory: playerOutputs/");
    }
}