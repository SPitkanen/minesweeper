
package minesweeper.bot;

import java.util.concurrent.BlockingQueue;
import minesweeper.model.Board;
import minesweeper.model.Move;

/**
* This class is used to encapsulate the bot to a separate thread so it can be run concurrently with the GUI updater
*/
public class BotExecutor extends Thread {

    private BlockingQueue<Move> queue;
    private Bot bot;
    private Board board;

    public BotExecutor(BlockingQueue<Move> moveQueue, Bot bot, Board board) {
        this.queue = moveQueue;
        this.bot = bot;
        this.board = board;
    }

    @Override
    public void run() {
        //Run bot while game has not ended
        while (!this.board.gameLost) {
            // Try to add new move from bot to queue
            // Bot makes moves to its own board and then the move is added to the supply queue
            Move move = this.bot.makeMove(this.board);
            this.queue.offer(move);
            this.board.makeMove(move);
        }
    }

}
