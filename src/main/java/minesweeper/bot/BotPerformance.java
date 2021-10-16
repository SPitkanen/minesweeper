/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minesweeper.bot;

import minesweeper.model.Board;
import minesweeper.model.GameStats;
import minesweeper.model.Move;
import minesweeper.model.MoveType;
import minesweeper.model.Pair;
import minesweeper.model.Square;
import minesweeper.generator.MinefieldGenerator;
/**
 *
 * @author santeripitkanen
 */
public class BotPerformance {
    
    public void BotPerformance() {
        
    }
    
    public void execute(int games) {
        test("Beginner", 10, 10, 10, games);
        test("Intermediate", 16, 16, 40, games);
        test("Expert", 16, 30, 99, games);
    }
    
    public void test(String name, int x, int y, int mines, int games) {
        int victory = 0;
        int defeat = 0;
        long seed = 1234;
        long tt = 0;
        long fastest = 1000000000;
        long longest = 0;
        for (int i = 0; i < games; i++) {
            MinefieldGenerator generator = new MinefieldGenerator(seed);
            Board board = new Board(generator, x, y, mines);
            ProjectBot bot = new ProjectBot();
            long start = System.currentTimeMillis();
            while (!board.gameLost && !board.gameWon) {
                Move move = bot.makeMove(board);
                board.makeMove(move);
            }
            long stop = System.currentTimeMillis();
            long time = stop - start;
            tt += time;
            if (fastest > time) {
                fastest = time;
            }
            if (longest < time) {
                longest = time;
            }
            if (board.gameLost) {
                defeat++;
            }
            if (board.gameWon) {
                victory++;
            }
        }
        double wr = (victory * 100 / games);
        long averageTime = (tt / games);
        System.out.println(" ");
        System.out.println(name + ", " + games + " games");
        System.out.println("Wins: " + victory);
        System.out.println("Defeats: " + defeat);
        System.out.println("Win Ratio: " + wr);
        System.out.println("Fastest time: " + fastest);
        System.out.println("Slowest Time: " + longest);
        System.out.println("Average Time: " + averageTime);
        System.out.println(" ");
    }
}

