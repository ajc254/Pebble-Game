
package pebble;

import java.util.List;

class Bag {
    private final List<Integer> pebbles;
    private final String name;
    
    /**
     * Constructs a bag with pebbles and a name.
     * @param pebbles Pebbles list to use.
     * @param name Name of bag.
     */
    Bag(List<Integer> pebbles, String name){
        this.pebbles = pebbles;
        this.name = name;
    }
    
    /**
     * Gets the name of the bag.
     * @return Name of bag.
     */
    String getName(){
        return name;
    }
    
    /**
     * Gets the pebbles in the bag.
     * @return Pebbles in the bag.
     */
    List<Integer> getPebbles(){
        return pebbles;
    }   
    
    /**
     * Add an integer (pebble) to the bag.
     * @param pebble Pebble to add.
     */
    void add( int pebble ) {
        pebbles.add(pebble);
    }
    
    /**
     * Remove a pebble from the bag using it's index.
     * @param index Index to remove.
     */
    void remove( int index ) {
        pebbles.remove(index);
    }
    
    /**
     * Retrieve value of a specific index in the bag.
     * @param index Index to evaluate.
     * @return Value of pebble at that index.
     */
    int get( int index ) {
        return pebbles.get(index);
    }
    
    /**
     * Get the number of pebbles in the bag currently.
     * @return Number of pebbles.
     */
    int size() {
        return pebbles.size();
    }  
}

class BlackBag extends Bag{
    private WhiteBag pair;
    
    /**
     * Constructs a new black bag with pebbles and a name.
     * @param pebbles List of pebbles to fill the bag.
     * @param name Name of the bag.
     */
    BlackBag(List<Integer> pebbles, String name){
        super(pebbles,name);
    }
    
    /**
     * Transfer all pebbles from the paired white bag to this bag.
     */
    void refill() {
        synchronized(getPair()){
            getPebbles().addAll(getPair().getPebbles());    //Add all pair's pebbles.
            getPair().clear();   //Remove all pebbles from pair bag.
        }
    }
    
    /**
     * Get paired white bag.
     * @return White bag that is paired to this bag.
     */
    WhiteBag getPair(){
        return pair;
    }
    
    /**
     * Set the white bag to be paired to this bag.
     * @param pair White bag to be paired.
     */
    void setPair(WhiteBag pair){
        this.pair = pair;
    }   
}

class WhiteBag extends Bag{
    
    /**
     * Constructs a new white bag with pebbles and a name.
     * @param pebbles List of pebbles to fill the bag.
     * @param name Name of the bag.
     */
    WhiteBag(List<Integer> pebbles, String name){
        super(pebbles,name);
    }
    
    /**
     * Empty the bag.
     */
    void clear(){
        this.getPebbles().clear();
    }
}


