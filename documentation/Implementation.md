# Implementation

The program is based on the [research](https://dash.harvard.edu/bitstream/handle/1/14398552/BECERRA-SENIORTHESIS-2015.pdf?sequence=1&isAllowed=y) page 22.
While game is still on, the bot will first go through a list of all known safe squares and find all squares where it is safe to open any adjacent untouched square (AFN). If inspected Square is not AFN, it will be placed on a separate list containing uncertain squares. After the safe squares list is empty, the program will try to find squares on the uncertain squares list where all remaining adjacent squares are mines (AMN). After these have been inspected, it will go through the same list trying to find AFNs. 

If this proces has not resulted in a changed board state, the bot will try to find AFNs and AMNs, but it will also take into consideration pairs. Pairs are formed during the previous sections last AFN search when inspected square has two remaining untouched squares that contain one mine in either one. If some square is found to have adjacent pair, this pair can be used as one mined square to deduce mines or free squares from the remaining adjacent tiles.

In case the board state still has not changed, the first while loop will be broken and random move is returned.

At the time of writing this (16.10) the algorithm can achieve following results (Times in milliseconds):


Beginner, 1000 games

* Wins: 992
* Defeats: 8
* Win Ratio: 99.0
* Fastest time: 0
* Slowest Time: 32
* Average Time: 1
 
Intermediate, 1000 games
* Wins: 851
* Defeats: 149
* Win Ratio: 85.0
* Fastest time: 0
* Slowest Time: 73
* Average Time: 3
 
Expert, 1000 games
* Wins: 120
* Defeats: 880
* Win Ratio: 12.0
* Fastest time: 0
* Slowest Time: 27
* Average Time: 5

Separate class "BotPerformance" was created for this test and used from the main method.
Faster slowest time in expert mode is likely result from earlier random moves on this game mode. Average time is still longer than on other modes and can be seen increasing with the difficulty level.

At the moment the algorithm does not "run freely" and is stopped to return moves one at a time. This could be solved easily but would result in changing some classes in the template.
