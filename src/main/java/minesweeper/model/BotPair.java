package minesweeper.model;

import java.util.Objects;


/**
 * THIS CLASS IS A NEW ADDITION TO THE TEMPLATE BY:
 * @author santeripitkanen
 * BASED ON THE PAIR CLASS
 */

/**
 * A simple pair type implementation
 *
 * Used for wrapping X, Y coordinate values for example
 */
public class BotPair {
    public Square first;
    public Square second;
    public Boolean pairContainsMine;

    public BotPair(Square first, Square second) {
        this.first = first;
        this.second = second;
        this.pairContainsMine = false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.first);
        hash = 17 * hash + Objects.hashCode(this.second);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Pair<?> other = (Pair<?>) obj;
        if (!Objects.equals(this.first, other.first)) {
            return false;
        }
        if (!Objects.equals(this.second, other.second)) {
            return false;
        }
        return true;
    }
    
    public boolean samePair(BotPair pair) {
        if (this.first.getX() != pair.getFirst().getX()) {
            return false;
        }
        if (this.first.getY() != pair.getFirst().getY()) {
            return false;
        }
        if (this.second.getX() != pair.getSecond().getX()) {
            return false;
        }
        if (this.second.getY() != pair.getSecond().getY()) {
            return false;
        }
        return true;
               
    }
    
    public void setMine() {
        this.pairContainsMine = true;
    }
    
    public Square getFirst() {
        return this.first;
    }
    
    public Square getSecond() {
        return this.second;
    }

    
}
