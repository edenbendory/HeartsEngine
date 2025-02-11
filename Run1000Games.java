import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Run1000Games {
    public static ArrayList<Integer> getGameTally() {
        ArrayList<Integer> gamesWon = new ArrayList<Integer>();
        gamesWon.add(0);
        gamesWon.add(0);
        gamesWon.add(0);
        gamesWon.add(0);

        // Play Multiple Games
        int numberOfGames = 1000;
        for (int i = 1; i <= numberOfGames; i++) {
            // Initalize the deck of cards
            Deck thing = new Deck();

            // Assume this order is clockwise
            Player p1 = new HighLowPlayAI("EdHighLowPlay");
            Player p2 = new RandomPlayAI("JaiRandomPlay");
            Player p3 = new UCTPlayer("UCTPlayer");
            Player p4 = new MCTSPlayer("JulianMCTS");

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
            int j = 1;
            while (!gameOver) {
                // System.out.println("\n--------------------------------------------");
                // System.out.println("--------------------------------------------");
                // System.out.println("--------------------------------------------");
                // System.out.println("Playing Round #"+j);
                // System.out.println("--------------------------------------------");
                // System.out.println("--------------------------------------------");
                // System.out.println("--------------------------------------------\n");
                round.playNewGame(true, null);
                j++;

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

    public ArrayList<Double> getTwoPlayerStats(Player p1, Player p2) {
        ArrayList<Double> totalAvgScore = new ArrayList<Double>();
        totalAvgScore.add(0.0);
        totalAvgScore.add(0.0);
        ArrayList<Integer> totalScoreCount = new ArrayList<Integer>();
        totalScoreCount.add(0);
        totalScoreCount.add(0);

        // Play Multiple Games
        int numberOfGames = 100;
        for (int i = 1; i <= numberOfGames; i++) {
            // Initalize the deck of cards
            Deck thing = new Deck();
            thing.shuffleDeck();

            ArrayList<Double> thisAvgScore = new ArrayList<Double>();
            thisAvgScore.add(0.0);
            thisAvgScore.add(0.0);
            ArrayList<Integer> thisAvgScoreCount = new ArrayList<Integer>();
            thisAvgScoreCount.add(0);
            thisAvgScoreCount.add(0);

            for (int j = 0; j < 14; j++) {
                // for the purposes of this experimental setup, one round = one game ???
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

                for (Player p : playerCombos.get(j)) {
                    if (p.name.equals("HighLowPlayer")) {
                        int scoreCount = thisAvgScoreCount.get(0);
                        double avgScoreOne = (thisAvgScore.get(0) * scoreCount + p.points) / (scoreCount + 1);
                        thisAvgScore.set(0, avgScoreOne);
                        thisAvgScoreCount.set(0, scoreCount + 1);
                    }
                    else {
                        int scoreCount = thisAvgScoreCount.get(1);
                        double avgScoreTwo = (thisAvgScore.get(1) * scoreCount + p.points) / (scoreCount + 1);
                        thisAvgScore.set(1, avgScoreTwo);
                        thisAvgScoreCount.set(1, scoreCount + 1);
                    }
                }
                
                
            }

            int totalScoreCount1 = totalScoreCount.get(0);
            double totalAvgScoreOne = ((totalAvgScore.get(0) * totalScoreCount1) + (thisAvgScore.get(0) * 28)) /    (totalScoreCount1 + 28);
            totalAvgScore.set(0, totalAvgScoreOne);
            totalScoreCount.set(0, totalScoreCount1 + 28);

            int totalScoreCount2 = totalScoreCount.get(1);
            double totalAvgScoreTwo = ((totalAvgScore.get(1) * totalScoreCount2) + (thisAvgScore.get(1) * 28)) /    (totalScoreCount2 + 28);
            totalAvgScore.set(1, totalAvgScoreTwo);
            totalScoreCount.set(1, totalScoreCount2 + 28);
        }

        return totalAvgScore;
    }

    public static void main(String[] args) {
        Run1000Games tester = new Run1000Games();

        ArrayList<String> playerNames = new ArrayList<>();
        playerNames.add("EdHighLowPlay");
        playerNames.add("JaiRandomPlay");
        playerNames.add("UCTPlayer");
        playerNames.add("JulianMCTS");

        PrintStream originalOut = System.out;

        // Redirect output to null to suppress print statements
        System.setOut(new PrintStream(new OutputStream() {
            public void write(int b) {}
        }));

        // ArrayList<Integer> gamesWon = getGameTally();

        Player p1 = new HighLowPlayAI("HighLowPlayer");
        // Player p1 = new RandomPlayAI("RandomPlayer");
        // Player p1 = new LowPlayAI("LowPlayer");
        Player p2 = new UCTPlayer("UCTPlayer");
        ArrayList<Double> twoPlayerStats = tester.getTwoPlayerStats(p1, p2);

        // Restore original output stream
        System.setOut(originalOut);

        // !!! BUG - players not HighLowPlayer average at score 0.0

        System.out.println(String.format("HighLowPlayer: %.2f", twoPlayerStats.get(0)));
        // System.out.println(String.format("RandomPlayer: %.2f", twoPlayerStats.get(0)));
        // System.out.println(String.format("LowPlayer: %.2f", twoPlayerStats.get(0)));
        System.out.println(String.format("UCTPlayer: %.2f", twoPlayerStats.get(1)));
        

        // for (int i = 0; i < 4; i++){
        //     System.out.println(playerNames.get(i) + ": " + gamesWon.get(i));
        // }
    }
}
