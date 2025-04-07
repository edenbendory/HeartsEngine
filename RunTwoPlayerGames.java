/* Written by @edenbendory
 * Plays 14 games for one dealing of card hands, so that the strategy
 * assigned to each of the 4 players varies, but the 4 players' card 
 * hands do not. Do this for each opponent selected to play against
 * the player in question, usually UCTPlayer. 
 */

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class RunTwoPlayerGames {

    // Returns and array of the 14 possible ways for p1 and p2 to play a 4-player game
    private ArrayList<ArrayList<Player>> getAllCombos(Player p1, Player p2) {
        ArrayList<ArrayList<Player>> playerCombos = new ArrayList<>();

        playerCombos.add(new ArrayList<>(List.of(p1.resetPlayer(), p1.resetPlayer(), p1.resetPlayer(), p2.resetPlayer())));
        playerCombos.add(new ArrayList<>(List.of(p1.resetPlayer(), p1.resetPlayer(), p2.resetPlayer(), p1.resetPlayer())));
        playerCombos.add(new ArrayList<>(List.of(p1.resetPlayer(), p1.resetPlayer(), p2.resetPlayer(), p2.resetPlayer())));
        playerCombos.add(new ArrayList<>(List.of(p1.resetPlayer(), p2.resetPlayer(), p1.resetPlayer(), p1.resetPlayer())));
        playerCombos.add(new ArrayList<>(List.of(p1.resetPlayer(), p2.resetPlayer(), p1.resetPlayer(), p2.resetPlayer())));
        playerCombos.add(new ArrayList<>(List.of(p1.resetPlayer(), p2.resetPlayer(), p2.resetPlayer(), p1.resetPlayer())));
        playerCombos.add(new ArrayList<>(List.of(p1.resetPlayer(), p2.resetPlayer(), p2.resetPlayer(), p2.resetPlayer())));
        playerCombos.add(new ArrayList<>(List.of(p2.resetPlayer(), p1.resetPlayer(), p1.resetPlayer(), p1.resetPlayer())));
        playerCombos.add(new ArrayList<>(List.of(p2.resetPlayer(), p1.resetPlayer(), p1.resetPlayer(), p2.resetPlayer())));
        playerCombos.add(new ArrayList<>(List.of(p2.resetPlayer(), p1.resetPlayer(), p2.resetPlayer(), p1.resetPlayer())));
        playerCombos.add(new ArrayList<>(List.of(p2.resetPlayer(), p1.resetPlayer(), p2.resetPlayer(), p2.resetPlayer())));
        playerCombos.add(new ArrayList<>(List.of(p2.resetPlayer(), p2.resetPlayer(), p1.resetPlayer(), p1.resetPlayer())));
        playerCombos.add(new ArrayList<>(List.of(p2.resetPlayer(), p2.resetPlayer(), p1.resetPlayer(), p2.resetPlayer())));
        playerCombos.add(new ArrayList<>(List.of(p2.resetPlayer(), p2.resetPlayer(), p2.resetPlayer(), p1.resetPlayer())));

        return playerCombos;
    }

    // Returns the average scores when comparing 2 players.
    // A total of 14 games are played, 14 variations for each deal 
    // (the number of times this function is called is the number of games x14 that will be played )
    private ArrayList<Double> getTwoPlayerStats(Player p1, Player p2) {
        ArrayList<Double> totalAvgScore = new ArrayList<>();
        totalAvgScore.add(0.0);
        totalAvgScore.add(0.0);

        // Play Multiple Games
        // Initalize the deck of cards
        Deck thing = new Deck();
        thing.shuffleDeck();

        double totalScoreOne=0;
        int totalCountOne=0;
        double totalScoreTwo=0;
        int totalCountTwo=0;

        for (int j = 0; j < 14; j++) {
            int score = 0;
            // for the purposes of this experimental setup, one round = one game 
            Deck thisRound = new Deck(thing);

            // determine which arrangement of hands we want for this game
            ArrayList<ArrayList<Player>> playerCombos = getAllCombos(p1, p2);
            Player first = playerCombos.get(j).get(0);
            Player second = playerCombos.get(j).get(1);
            Player third = playerCombos.get(j).get(2);
            Player fourth = playerCombos.get(j).get(3);

            // re-initialize each player
            first.points = 0;
            first.hand = new ArrayList<>();
            second.points = 0;
            second.hand = new ArrayList<>();
            third.points = 0;
            third.hand = new ArrayList<>();
            fourth.points = 0;
            fourth.hand = new ArrayList<>();

            Game round = new Game(thisRound, first, second, third, fourth);
            round.playNewGame(true, round.cardsPlayed);

            for (int k = 0; k < 4; k++) {
                if (round.playerOrder.get(k).name.equals(p1.name)) {
                    totalScoreOne+=round.playerScores.get(k);
                    totalCountOne++;
                }
                else {
                    totalScoreTwo+=round.playerScores.get(k);
                    totalCountTwo++;
                }
                score+=round.playerScores.get(k);
            }
            
            assert (score == 16 || score == 68);
        }

        assert (totalCountOne == 28 && totalCountTwo == 28);

        totalAvgScore.set(0, totalScoreOne/28);
        totalAvgScore.set(1, totalScoreTwo/28);

        return totalAvgScore;
    }

    // Runs getTwoPlayerStats for each opponent chosen to play against
    // UCTPlayer
    public static void runTwoPlayerGame() {
        RunTwoPlayerGames tester = new RunTwoPlayerGames();

        PrintStream originalOut = System.out;

        // Redirect output to null to suppress print statements
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {}
        }));

        ArrayList<Player> p1List = new ArrayList<>();
        p1List.add(new HighLowPlayAI("HighLowPlayer"));
        p1List.add(new RandomPlayAI("RandomPlayer"));
        p1List.add(new MCTSPlayer("MCTSPlayer"));
        // p1List.add(new LowPlayAI("LowPlayer"));
        // p1List.add(new LookAheadPlayer("LookAheadPlayer"));
        UCTPlayer p2 = new UCTPlayer("UCTPlayer");
        // HighLowPlayAI p2 = new HighLowPlayAI("HighLowPlayer");
        // RandomPlayAI p2 = new RandomPlayAI("RandomPlayer");
        // firstUCTPlayer p2 = new firstUCTPlayer("UCTPlayer");
        // generateHandUCTPlayer p2 = new generateHandUCTPlayer("UCTPlayer");

        System.setOut(originalOut);
        // System.out.println("Number of Iterations: " + p2.getNumIterations());
        // System.out.println("Max Depth: " + p2.getMaxDepth());
        // System.out.println("--------------------------------------------");

        for (Player p1 : p1List) {
            // Redirect output to null to suppress print statements
            System.setOut(new PrintStream(new OutputStream() {
                @Override
                public void write(int b) {}
            }));

            ArrayList<Double> twoPlayerStats = tester.getTwoPlayerStats(p1, p2);

            // Restore original output stream
            System.setOut(originalOut);
    
            System.out.println(String.format("%s: %.2f", p1.name, twoPlayerStats.get(0)));
            System.out.println(String.format("%s_%s: %.2f", p2.name, p1.name, twoPlayerStats.get(1)));
            System.out.println(String.format("%s_%s_Difference: %.2f", p2.name, p1.name, twoPlayerStats.get(0) - twoPlayerStats.get(1)));
            System.out.println();
        }
    }

    public static void main(String[] args) {
        // System.out.println("--------------------------------------------");
        // System.out.println("Running 2 Player Games");
        // System.out.println("--------------------------------------------");
        // System.out.println();
        runTwoPlayerGame();
    }
}
