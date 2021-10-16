# Testing the Bot

![Image of Jacoco report](https://github.com/SPitkanen/minesweeper/blob/master/documentation/pictures/minesweeperBotJacocoReport.png)

Testing currently reaches all of the methods in use 100%, except for the DSSP. However DSSP functionality 
can be proven through other tests (gameWon can solve the game which requires proper functioning of the DSSP).

Class `ProjectBotTest` creates an artificial game board that always gets specific mines. This way
algorithm can be reliably be proven to work again and again with the same tests. These tests have been designed 
to test relevant situations that might come up when the algorithm tries to solve the game.

Test report can be generated by using command `./gradlew build jacocoTestReport`. And the report can be found
by following this path: `build/reports/jacoco/test/html/index.html`.

For algorithms performance there is a separate class `BotPerformance` that can be used to test the bots performance on various 
difficulty levels. However the use of this class requires changes to the `App` class `main` method. See results [here](https://github.com/SPitkanen/minesweeper/blob/master/documentation/Implementation.md)