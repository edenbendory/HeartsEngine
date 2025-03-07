/* This file was taken from another GitHub repo that implements a Hearts
 game and players system. Linked here: https://github.com/Devking/HeartsAI */

import java.util.Scanner;

class HumanPlayer extends Player {
	
	Scanner sc;

	HumanPlayer (String name) { 
		super(name); 
		sc = new Scanner (System.in);
		System.out.println("Human player ("+name+") initialized."); 
	}

	@Override
    Player resetPlayer() { return new HumanPlayer(name); }

	boolean setDebug() { return false; }

	Card performAction (State masterCopy) {

		boolean flag = true;
		int i = 0;
		// run a while loop to make sure the user keeps inputting until valid input
		while (flag) {
			// print hand is only needed for debugging / human players
			printHand();
			
			flag = false;

			System.out.print("\nInput the index of the card you ("+name+") wish to play:\n> ");
			while (!sc.hasNextInt()) { 
				sc.next(); 
				System.out.println("\nYou did not input a valid integer index! Try again!"); 
				printHand();
				System.out.print("\nInput the index of the card you ("+name+") wish to play:\n> ");
			}
			i = sc.nextInt();

			// check if the number is valid for this hand size
			if (i > hand.size()-1) { 
				System.out.println("Invalid card index! Please input a valid number!"); 
				flag = true; 
			}
		}
		System.out.println();
		return hand.remove(i);
	}

}