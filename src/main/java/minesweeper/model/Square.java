
package minesweeper.model;

/**
 * Represent a single square on the board.
 * @see model.Board
 */
public class Square {
    public boolean isMine, opened = false;
    public int surrounding = 0;

    public Square() {
    }

    public Square(boolean isMine) {
	this.isMine = isMine;
    }

    @Override
    public String toString() {
        if (!this.opened) return "X";
        return this.isMine ? "*" : "" + surrounding;
    }



/*
    @Override
    protected Object clone() throws CloneNotSupportedException {
        Square newObj = new Square();
        newObj.isMine = this.isMine;
        newObj.surrounding = this.surrounding;
        return (Object) newObj;
    }
*/
    
}
