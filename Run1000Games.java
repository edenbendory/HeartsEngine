import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

public class Run1000Games {
    public static ArrayList<Integer> getGameTally() {
        ArrayList<Integer> gamesWon = new ArrayList<>();
        gamesWon.add(0);
        gamesWon.add(0);
        gamesWon.add(0);
        gamesWon.add(0);

        // Play Multiple Games
        int numberOfGames = 10;
        for (int i = 1; i <= numberOfGames; i++) {
            // Initalize the deck of cards
            Deck thing = new Deck();

            // Assume this order is clockwise
            Player p1 = new HighLowPlayAI("HighLowPlayer");
            Player p2 = new RandomPlayAI("RandomPlayer");
            Player p3 = new UCTPlayer("UCTPlayer");
            Player p4 = new MCTSPlayer("MCTSPlayer");

            // at the end of every game, we will have all the cards back in the deck
            // thing.printDeck();
            

            // The rest of this code was written by @edenbendory
            // Play rounds until someone hits 100 points - then one game is complete
            System.out.println("\n--------------------------------------------");
            System.out.println("--------------------------------------------");
            System.out.println("--------------------------------------------");
            System.out.println("Playing Game #"+i);
            System.out.println("--------------------------------------------");
            System.out.println("--------------------------------------------");
            System.out.println("--------------------------------------------\n");

            boolean gameOver = false;
            Game round = new Game(thing, p1, p2, p3, p4);
            // int j = 1;
            while (!gameOver) {
                // System.out.println("\n--------------------------------------------");
                // System.out.println("--------------------------------------------");
                // System.out.println("--------------------------------------------");
                // System.out.println("Playing Round #"+j);
                // System.out.println("--------------------------------------------");
                // System.out.println("--------------------------------------------");
                // System.out.println("--------------------------------------------\n");
                round.playNewGame(true, null);
                // j++;

                for (Player p : round.playerOrder) {
                    if (p.getPoints() >= 100) {
                        gameOver = true;
                    }
                }
            }

            // System.out.println("------------------------------------------");
            // System.out.println("Total Game Summary:");
            // System.out.println("------------------------------------------\n");
            // round.printPoints();
            // round.printTotalPoints();
            int winner = round.getWinner();
            gamesWon.set(winner, gamesWon.get(winner)+1);
        }

        return gamesWon;
    }

    public static void run1000Games() throws FileNotFoundException {
        PrintStream originalOut = System.out;
        
        // Redirect output to null to suppress print statements
        System.setOut(new PrintStream(new File("./run1000games.log")));

        ArrayList<String> playerNames = new ArrayList<>();
        playerNames.add("HighLowPlayer");
        playerNames.add("RandomPlayer");
        playerNames.add("UCTPlayer");
        playerNames.add("MCTSPlayer");

        ArrayList<Integer> gamesWon = getGameTally();

        // Restore original output stream
        System.setOut(originalOut);

        for (int i = 0; i < 4; i++){
            System.out.println(playerNames.get(i) + ": " + gamesWon.get(i));
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        // System.out.println("--------------------------------------------");
        // System.out.println("Running 1000 Games");
        // System.out.println("--------------------------------------------");
        // System.out.println();
        run1000Games();
    }
}
