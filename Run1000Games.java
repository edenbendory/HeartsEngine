import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

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
        // !!!!! need to make deep copy of each player - can't just add them to array !!!
        ArrayList<ArrayList<Player>> playerCombos = new ArrayList<>();

        ArrayList<Player> combo0 = new ArrayList<>();
        combo0.add(new Player(p1));
        combo0.add(p1);
        combo0.add(p1);
        combo0.add(p2);
        playerCombos.add(combo0);

        ArrayList<Player> combo1 = new ArrayList<>();
        combo1.add(p1);
        combo1.add(p1);
        combo1.add(p2);
        combo1.add(p1);
        playerCombos.add(combo1);

        ArrayList<Player> combo2 = new ArrayList<>();
        combo2.add(p1);
        combo2.add(p1);
        combo2.add(p2);
        combo2.add(p2);
        playerCombos.add(combo2);

        ArrayList<Player> combo3 = new ArrayList<>();
        combo3.add(p1);
        combo3.add(p2);
        combo3.add(p1);
        combo3.add(p1);
        playerCombos.add(combo3);

        ArrayList<Player> combo4 = new ArrayList<>();
        combo4.add(p1);
        combo4.add(p2);
        combo4.add(p1);
        combo4.add(p2);
        playerCombos.add(combo4);

        ArrayList<Player> combo5 = new ArrayList<>();
        combo5.add(p1);
        combo5.add(p2);
        combo5.add(p2);
        combo5.add(p1);
        playerCombos.add(combo5);

        ArrayList<Player> combo6 = new ArrayList<>();
        combo6.add(p1);
        combo6.add(p2);
        combo6.add(p2);
        combo6.add(p2);
        playerCombos.add(combo6);

        ArrayList<Player> combo7 = new ArrayList<>();
        combo7.add(p2);
        combo7.add(p1);
        combo7.add(p1);
        combo7.add(p1);
        playerCombos.add(combo7);

        ArrayList<Player> combo8 = new ArrayList<>();
        combo8.add(p2);
        combo8.add(p1);
        combo8.add(p1);
        combo8.add(p2);
        playerCombos.add(combo8);

        ArrayList<Player> combo9 = new ArrayList<>();
        combo9.add(p2);
        combo9.add(p1);
        combo9.add(p2);
        combo9.add(p1);
        playerCombos.add(combo9);

        ArrayList<Player> combo10 = new ArrayList<>();
        combo10.add(p2);
        combo10.add(p1);
        combo10.add(p2);
        combo10.add(p2);
        playerCombos.add(combo10);

        ArrayList<Player> combo11 = new ArrayList<>();
        combo11.add(p2);
        combo11.add(p2);
        combo11.add(p1);
        combo11.add(p1);
        playerCombos.add(combo11);

        ArrayList<Player> combo12 = new ArrayList<>();
        combo12.add(p2);
        combo12.add(p2);
        combo12.add(p1);
        combo12.add(p2);
        playerCombos.add(combo12);

        ArrayList<Player> combo13 = new ArrayList<>();
        combo13.add(p2);
        combo13.add(p2);
        combo13.add(p2);
        combo13.add(p1);
        playerCombos.add(combo13);

        return playerCombos;
    }

    public ArrayList<Double> getTwoPlayerStats(Player p1, Player p2) {
        ArrayList<Double> totalAvgScore = new ArrayList<Double>();
        totalAvgScore.add(0.0);
        totalAvgScore.add(0.0);

        // Play Multiple Games
        int numberOfGames = 100;
        for (int i = 1; i <= numberOfGames; i++) {
            // Initalize the deck of cards
            Deck thing = new Deck();
            thing.shuffleDeck();

            ArrayList<Double> thisAvgScore = new ArrayList<Double>();
            thisAvgScore.add(0.0);
            thisAvgScore.add(0.0);

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

                // !!! COME BACK TO THIS - have to make sure accounting for scores of each correct player, even when player 1 is playing multiple players in a round 
                double avgScoreOne = (thisAvgScore.get(0) * (j) + first.points) / (j+1);
                thisAvgScore.set(0, avgScoreOne);
                double avgScoreTwo = (thisAvgScore.get(1) * (j) + second.points) / (j+1);
                thisAvgScore.set(1, avgScoreTwo);
            }

            double totalAvgScoreOne = (totalAvgScore.get(0) * (i) + thisAvgScore.get(0)*14) / (i + 14);
            totalAvgScore.set(0, totalAvgScoreOne);
            double totalAvgScoreTwo = (totalAvgScore.get(1) * (i) + thisAvgScore.get(1)*14) / (i + 14);
            totalAvgScore.set(1, totalAvgScoreTwo);
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
        Player p2 = new UCTPlayer("UCTPlayer");
        ArrayList<Double> twoPlayerStats = tester.getTwoPlayerStats(p1, p2);

        // Restore original output stream
        System.setOut(originalOut);

        System.out.println(twoPlayerStats.get(0));
        System.out.println(twoPlayerStats.get(1));
        

        // for (int i = 0; i < 4; i++){
        //     System.out.println(playerNames.get(i) + ": " + gamesWon.get(i));
        // }
    }
}
