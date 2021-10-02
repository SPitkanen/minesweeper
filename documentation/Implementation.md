# Implementation

The program is based on the [research](https://dash.harvard.edu/bitstream/handle/1/14398552/BECERRA-SENIORTHESIS-2015.pdf?sequence=1&isAllowed=y) page 22.
While game is still on, the bot will first go through a list of all known safe squares and find all squares where it is safe to open any adjacent untouched square (AFN). If inspected Square is not AFN, it will be placed on a separate list containing uncertain squares. After the safe squares list is empty, the program will try to find squares on the uncertain squares list where all remaining adjacent squares are mines. After these have been inspected, it will go through the same list trying to find AFNs. When this list is empty, the bot starts the whole process over.
