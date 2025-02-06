/* This file was taken from another GitHub repo that implements a Hearts
 game and players system. Linked here: https://github.com/Devking/HeartsAI 
 
 Slight modifications of player numbers made by @edenbendory */

import java.util.Scanner;

public class Hearts {
	public static void main(String[] args) {
		System.out.println("Welcome to Hearts version 1.1.0.");

		// Initalize the deck of cards
		Deck thing = new Deck();

		// Assume this order is clockwise
		Player p1 = new HighLowPlayAI("EdHighLowPlay");
		// Player p3 = new LowPlayAI("WellsLowPlay");
		//Player p4 = new RandomPlayAI("Wells");
		Player p2 = new RandomPlayAI("JaiRandomPlay");
		// Player p3 = new LookAheadPlayer("AntLookAhead");
		Player p3 = new UCTPlayer("UCTPlayer");
		Player p4 = new MCTSPlayer("JulianMCTS");
		// Player p4 = new HumanPlayer("EdenPlay");

		// at the end of every game, we will have all the cards back in the deck
		// thing.printDeck();

		// Play Multiple Games
		// int numberOfGames = 10;
		// Game round = new Game(thing, p1, p2, p3, p4);
		// for (int i = 1; i <= numberOfGames; i++) {
		// 	System.out.println("\n--------------------------------------------");
		// 	System.out.println("--------------------------------------------");
		// 	System.out.println("--------------------------------------------");
		// 	System.out.println("Playing Game #"+i);
		// 	System.out.println("--------------------------------------------");
		// 	System.out.println("--------------------------------------------");
		// 	System.out.println("--------------------------------------------\n");
		// 	round.playNewGame(false);
		// }

		// The rest of this code was written by @edenbendory
		// Play until someone hits 100 points
		boolean gameOver = false;
		Game round = new Game(thing, p1, p2, p3, p4);
		int i = 1;
		while (!gameOver) {
			System.out.println("\n--------------------------------------------");
			System.out.println("--------------------------------------------");
			System.out.println("--------------------------------------------");
			System.out.println("Playing Game #"+i);
			System.out.println("--------------------------------------------");
			System.out.println("--------------------------------------------");
			System.out.println("--------------------------------------------\n");
			round.playNewGame(false, null);
			i++;

			for (Player p : round.playerOrder) {
				if (p.getPoints() >= 100) {
					gameOver = true;
				}
			}
		}

		Scanner in = new Scanner(System.in);
		System.out.println("------------------------------------------");
		System.out.println("Total Game Summary:");
		System.out.println("------------------------------------------\n");
		round.printPoints();
		round.printTotalPoints();
		round.printEndOfGameStats();
		System.out.println("Press ENTER to END all games.");
	    in.nextLine();
		in.close();
	}
}