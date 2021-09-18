# Week 2

This week was used to get some basic functionalities for the bot working. Currently the bot has two rules: 
1. If there are as many unopened squares surrounding a specific square as its mine count, all of the unopened squares must be mines
2. If there are as many flagged adjacent squares as squares' mine count, all remaining unflagged tiles are safe and can be opened.

If the bot cannot make a move based on the two rules listed above, it will make a random move. First move is also random.

I tried to create some tests too, but that needs some more work in the future to get working properly.


Even in its current "dumb" state the bot is surprisingly good at solving the game. Beginner levels can be solved pretty much 100% of the time and intermediate levels sometimes.


This week I had difficulties trying to implement testing for the bot, I'm not sure if the current method to create the board for the bot works. I couldn't get the tests to run (even the old that came with the template).


Next I will try to figure out how to get the test to work. On bot I want to add a functionality that enables it to deduce mines and safe squares instead of making a random move.
