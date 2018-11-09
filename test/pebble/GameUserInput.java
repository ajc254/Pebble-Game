package pebble;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class GameUserInput {
    
    private Scanner keyboardInput;
    
    /**
     * GameUserInput constructor.
     */
    GameUserInput() {
    }
    
    /**
     * Sets up a keyboard input scanner.
     */
    void setUpKeyboardInput() {
        keyboardInput = new Scanner(System.in);
    }
    
    /**
     * Exits if the input was "e" at command line prompts.
     * @param input Takes a string user input to evaluate.
     */
    private void checkExitKey( String input ) {
        if(input.toLowerCase().trim().equals("e")){
            System.out.println("Exiting Program.");
            System.exit(0);
        }
    }
    
    /**
     * Command line prompt to ask for the number of players.
     * @return Number of players given.
     */
    int getNumPlayers() {
        
        String players;
        int numPlayers;
        
        while( true ) {
            try{
                System.out.println( "\nPlease provide number of players:");
                players = keyboardInput.nextLine();
                checkExitKey( players );
                numPlayers = Integer.parseInt(players.trim()); //Remove any whitespace and get the integer.
                if ( numPlayers < 1 ) {     //Checks for a valid integer.
                    throw new NumberFormatException();
                }
                break;
            }catch( NumberFormatException e ) {
                System.out.println( "\nNumber of players must be an integer above 0.");
            }
            finally {
                keyboardInput.reset();
            }
        }
        return numPlayers;
    }
    
    /**
     * Asks for a file to fill the black bags, ordered by letter.
     * @param bagLetter Bag letter to be filled by the file's pebbles.
     * @param gameNumPlayers Number of players in the game holding these bags.
     * @return Integer list of pebbles to be assigned by the bag.
     * @throws IOException 
     */
    List<Integer> fillBag(char bagLetter, int gameNumPlayers) throws IOException{
        List<Integer> bag;
        Scanner fileReader = null;
        String fileLocation = null;
        int pebbleWeight;
        
        while( true ) {
            try{
                System.out.println( "\nPlease provide file source to fill bag " + bagLetter
                                   + ":\n");
                fileLocation = keyboardInput.nextLine();
                checkExitKey(fileLocation);
                fileReader = new Scanner(new File(fileLocation));
                
                //Check file type is valid.
                if( !fileLocation.substring(fileLocation.length() - 4).equals(".txt") &&
                    !fileLocation.substring(fileLocation.length() - 4).equals(".csv")) {
                        throw new InvalidFileException();
                    }
                if(!fileReader.hasNext() ) {
                    throw new EmptyFileException();   //If file is empty.
                }
                //Split the numbers by commas, into an array.
                String[] pebbleValues = fileReader.nextLine().split(",");
                if ( pebbleValues.length < 11*gameNumPlayers ) {
                    throw new NumberOfPebblesException();   //If not enough pebbles for players.
                }
                //Parse the pebbles to an integer list.
                bag = new ArrayList<>(pebbleValues.length);
                for(String value : pebbleValues){
                    pebbleWeight = Integer.parseInt(value.trim());
                    if( pebbleWeight > 0 ){
                        bag.add( pebbleWeight );
                    } else {
                        throw new NumberFormatException();  //If negative pebble encountered.
                    } 
                }
            
            //Deal with errors, print the appropriate message to user and ask for a valid file.
            }catch( NumberFormatException e ) {
                System.out.println( "\nFile at: " + fileLocation + 
                    "\nMust have positive integers separated by commas " );
                continue;
            }catch( EmptyFileException e ){
                System.out.println(" File at: " + fileLocation +
                                   " \nis empty, please try again.\n");
                continue;
            }catch( FileNotFoundException e ){
                System.out.println(" File path: " + fileLocation + 
                                   " \nCould not be found, please try again.\n");
                continue;
            }catch( InvalidFileException e){
                System.out.println( "File inputted is not a plain text file (.txt)" +
                                    "\nPlease enter a vailid file.");
                continue;
            }catch( NumberOfPebblesException e ){
                System.out.println( "File inputted does not contain enough pebbles," + 
                                    "\nIt must have at least " + gameNumPlayers*11 + " pebbles.");
                continue;
            }
            finally {
                if(fileReader != null){
                    fileReader.close();  //Close file reader scanner.
                }
            }
            break;
        }
        return bag;
    }
    
    void shutdown(){
        keyboardInput.close();  //Close system.in scanner.
    }
}
