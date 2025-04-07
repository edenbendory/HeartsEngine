# Hearts

## How To Run

**Using Command Line:**

1. Compile all of the source files by running `javac *.java` 
2. Run the Hearts program by running `java Hearts`

**Using an IDE**

1. Compile all of the source files using your IDE
2. Run the compiled `Hearts` program using your IDE

## Implementing and Adding Your Own Agents

To implement your own AI agent, all you need to do is write a new class that extends `Player`. As an example, you can take a look at the existing classes such as `LookAheadPlayer` or `RandomPlayAI`.

Once they are implemented, you can add them to the game by modifying `p1`, `p2`, `p3`, or `p4` in `Hearts.java` to instantiate a new agent of your class.


## Class Structure

- Value enum
- Suit enum
- Card class (for each individual card)
- Deck class (for the "deck" to deal hands)
- Hand class (for each of the hands)
- Player abstract class (defines base functionality for any player)
- HumanPlayer class extends Player (`performAction()` allows human input)
- State class (track state of the game and allow for random playouts)
- Game class (plays one game)
- Hearts class (this is the main file that brings it all together)

