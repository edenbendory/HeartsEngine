/* This file was taken from another GitHub repo that implements a Hearts
 game and players system. Linked here: https://github.com/Devking/HeartsAI */

public enum Suit { CLUBS, DIAMONDS, SPADES, HEARTS }

/*
How to compare enums:

Suit one = Suit.HEARTS;
Suit two = Suit.SPADES;

// compareTo will be positive if the caller is larger than the parameter
// otherwise it will be negative
if (one.compareTo(two) < 0) {
	System.out.println("this will be printed because one is less than two");
}

*/