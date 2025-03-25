/* This file was taken from another GitHub repo that implements a Hearts
 game and players system. Linked here: https://github.com/Devking/HeartsAI 
 The advanceState() and turnNumber() functions were written by @edenbendory */

// NOTE: If you are doing multiple playouts of the game, please use
// multiple State objects -- that is, keep a master State and just
// copy it (using the copy constructor) for every game playout

import java.util.*;

// This is what's passed in for each player
// in order to do full playouts of any one game.

// We will pass in five pieces of information:
// (1) Cards already played / Cards yet to be played
// (2) Cards on the table
// (3) Scores of each of the players (this game)
// (4) Whether hearts has already broken this game or not
// (5) The index of this player
// These are named the same as their copies in Game.java

// Use the Deck class's "invertDeck" member to pick random cards to play from
// Remember to move these cards to "allCards" when removing from invertDeck

class State {

	Deck 				cardsPlayed;		// Note: This keeps track of the cards *not* played as well
	ArrayList<Card> 	currentRound;
	ArrayList<Integer> 	playerScores;
	boolean 			hasHeartsBroken; 	// Keep track of whether hears has broken or not
	Random 				rng = new Random();
	int 				playerIndex;		// To help remember which player # this is

	// Remember to make COPIES of what is passed in!
	State (Deck deck, ArrayList<Card> round, ArrayList<Integer> scores, boolean hearts, int index) {
		cardsPlayed     = new Deck (deck);
		currentRound    = new ArrayList<Card>(round);
		playerScores    = new ArrayList<Integer>(scores);
		hasHeartsBroken = hearts;
		playerIndex 	= index;
	}

	// Copy constructor to spawn off duplicate State objects
	State (State secondCopy) {
		cardsPlayed 	= new Deck(secondCopy.cardsPlayed);
		currentRound 	= new ArrayList<Card>(secondCopy.currentRound);
		playerScores 	= new ArrayList<Integer>(secondCopy.playerScores);
		hasHeartsBroken = secondCopy.hasHeartsBroken;
		playerIndex 	= secondCopy.playerIndex;
	}

	// Figure out what round it is, based on number of cards in the Deck
	// Note: We start counting from Round 1
	int getRoundNumber() { return cardsPlayed.size()/4 + 1; }
	
	// If the Deck still does not have every card played, then the game is still valid
	boolean isGameValid() { return cardsPlayed.size() < 52; }

	// If this Round does not have 4 cards played, then the Round is still going
	boolean validRound() { return currentRound.size() < 4; }

	// Check if this is the first move to happen
	boolean firstMove() { return cardsPlayed.allCards.size()==0; }

	// Check if this is the first move this round
	boolean firstInRound() { return currentRound.size()==0; }

	// Return this player's score
	int getScore() { return playerScores.get(playerIndex); }

	// Return what number player you are this round (first to put a card down, second, etc.)
	// This function was written by @edenbendory
	int turnNumber() { return currentRound.size(); }

	// Check if that randomly played card is actually in my hand
	boolean isInMyHand(Card c, ArrayList<Card> playoutHand) {
		for (Card d : playoutHand) if (c.equals(d)) return true;
		return false;
	}

	// Given some Card c, find a matching card using "equals"
	// And move it from cardsPlayed.invertDeck to cardsPlayed.allCards
	// @edenbendory: add it to the cards on the table (currentRound)
	// This may take some time
	int playCard(Card c) { 
		for (Card d : cardsPlayed.invertDeck) {
			if (c.equals(d)) {
				cardsPlayed.allCards.add(d);
				cardsPlayed.invertDeck.remove(d);
				currentRound.add(d);
				return 0;
			}
		}
		return -1;
	}

	// Used to check if all the cards in this hand is hearts
	boolean hasAllHearts(ArrayList<Card> hand) {
		boolean flag = true;
		for (Card c : hand) { if (c.getSuit() != Suit.HEARTS) flag = false; }
		return flag;
	}

	// Used to check if all the cards in this hand is hearts or Queen of Spades
	// boolean hasAllHeartsAndQueen(ArrayList<Card> hand) {
	// 	boolean flag = true;
	// 	boolean queen = false;
	// 	for (Card c : hand) { 
	// 		if (c.getSuit() != Suit.HEARTS && !(c.getValue() == Value.QUEEN && c.getSuit() == Suit.SPADES)) {
	// 			flag = false;
	// 		}
	// 		if (c.getValue() == Value.QUEEN && c.getSuit() == Suit.SPADES) {
	// 			queen = true;
	// 		}
	// 	}
	// 	return (flag && queen);
	// }

	// Given a Card c, check against the current round to see if it's a valid play
	boolean checkRound(Card c, ArrayList<Card> playoutHand) {
		// First, check for the case of the first move
		Card twoClubs = new Card(Suit.CLUBS, Value.TWO);
		if (firstMove() && !c.equals(twoClubs)) {
			System.out.println("Simulation issue: Must play two of clubs to start the game.");
			return false;
		}
		// Next, check if a valid firstInRound move was made
		if (firstInRound()) {
			if (c.getSuit() == Suit.HEARTS && !hasHeartsBroken && !hasAllHearts(playoutHand)) 
				return false;
			// if (c.getSuit() == Suit.SPADES && c.getValue() == Value.QUEEN && !hasHeartsBroken && !hasAllHearts(playoutHand))
			// 	return false;
			// if (c.getSuit() == Suit.HEARTS && !hasHeartsBroken && hasAllHeartsAndQueen(playoutHand))
			// 	return false;
			return true;
		} else {
			// Check if suit is appropriate
			Suit firstSuit = currentRound.get(0).getSuit();
			// If off-suit play, check if the hand still has that suit
			if (firstSuit != c.getSuit()) {
				boolean flag = false;
				for (Card d: playoutHand) { if (d.getSuit() == firstSuit) flag = true; }
				if (flag) return false;
			}
			// If suit is appropriate, check if hearts
			if (c.getSuit() == Suit.HEARTS || (c.getSuit() == Suit.SPADES && c.getValue() == Value.QUEEN)) {
				hasHeartsBroken = true;
			}
		}
		return true;
	}

	// Go through the cards from the currentRound and calculate their point values
	int calculatePoints() {
		int points = 0;
		for (Card c : currentRound) {
			if (c.getSuit() == Suit.HEARTS) points++;
			if (c.getValue() == Value.QUEEN && c.getSuit() == Suit.SPADES) points += 13;
			if (c.getValue() == Value.JACK && c.getSuit() == Suit.DIAMONDS) points -= 10; // @edenbendory
		}
		return points;
	}


	// Return the index of the next player who will play // the player who takes this round
	// Pass in the index of the current first player (to check who played what card)
	// NOTE: This MUST return an int from 0 to 3! ALWAYS DO % playerOrder.size();
	int findTaker (int firstPlayer) {
		Suit firstSuit = currentRound.get(0).getSuit();
		Value largestValue = currentRound.get(0).getValue();
		int taker = firstPlayer;

		// go through all 4 cards that were played this round
		// @edenbendory changed to start from i=1, rather than i=0
		for (int i = 1; i < playerScores.size(); i++) {
			// keep track of the index of who played it
			int index = (firstPlayer+i) % playerScores.size();
			// if this card is the same suit as the first card, proceed
			if (currentRound.get(i).getSuit() == firstSuit) {
				// if this card is the largest played of the right suit this round, this player takes the round
				if (largestValue.compareTo(currentRound.get(i).getValue()) < 0) {
					taker = index;
					largestValue = currentRound.get(i).getValue();
				}
			}
		}

		return taker % playerScores.size();
	}

	// Return -1 if a card that cannot be played is played
	// On the controller side: first save the pointer to the card, then remove from the hand
	// then check advance(), then if necessary, add card back to hand
	
	// Otherwise, return the next player's index (based on who took the hand, or just based on who is going next)
	// This function was written by @edenbendory
	int advanceState(Card c, ArrayList<Card> playoutHand, boolean debug) {
		if (!checkRound(c,playoutHand)) return -1; // check it's a valid play (and updates hasHeartsBroken accordingly)

		// Move card between decks and put card onto the table
		int playedCard = playCard(c);
		assert(playedCard == 0);

		if (debug) {
			System.out.println("round size: " + currentRound.size());
		}

		// if this is the end of a round:
		if (!validRound()) {
			// Round has ended -- 1) update what points have gone where and 2) determine who goes next (use playerScores)
			int firstPlayer = (playerIndex - currentRound.size() + 1 + playerScores.size()) % playerScores.size(); // 0-3
			// System.out.println("First Player Number: " + firstPlayer);
			int points = calculatePoints();
			int taker = findTaker(firstPlayer);
			playerScores.set(taker, playerScores.get(taker)+points);

			// For expansion, we need to return which player number is next. that's the only thing this function will return 
			// For simulation, UCTPlayer can call isGameValid to check if the game is over - if it's over, can backpropogate
			// by calling the points array from state (doesn't need to return the points from this function)

			// Clear the cards on the table (don't worry, pointers to them are tracked in the cardsPlayed deck)
			currentRound.clear();

			playerIndex = taker; // 0-3
			if (debug) {
				System.out.println();
				System.out.println("Taker: " + taker);
				System.out.println();
				System.out.println();
				System.out.println("----------------------------------");
				System.out.println("NEW ROUND:");
				System.out.println("----------------------------------");
			}
		}
		else {
			// if this is the middle of a round, the next player is just in order
			playerIndex = (playerIndex + 1) % 4; // 0-3
			if (debug) {
				System.out.println("Next Player: " + playerIndex);
			}
		}
		
		return playerIndex;
	}

	// After playing that card, this will advance the game as much as possible
	int advance(Card c, ArrayList<Card> playoutHand) {
		if (!checkRound(c,playoutHand)) return -1;

		int playTurn = currentRound.size();		// keep track of which player this is
		// Move card between decks and put card onto the table
		playCard(c);

		// While the round is still valid, do *random* playouts to finish the round
		// Notice: playouts do not have to be valid, since we do not have information on what cards
		// other players may be holding -- is this a good representation of hidden information, or not?
		while (validRound()) {

			// Pick a random number that represents an index of the cards in the invertedDeck (cards yet to be played)
			int index = rng.nextInt(cardsPlayed.invertDeck.size());
			// Takes time equivalent to hand size!
			while (isInMyHand(cardsPlayed.invertDeck.get(index), playoutHand)) {
				index = rng.nextInt(cardsPlayed.invertDeck.size());
			}
			// Use the play card method to put take the card out of the invert deck, into the played deck, and also onto the table
			playCard(cardsPlayed.invertDeck.get(index));

		}

		// Round has ended -- check what points have gone where and determine who goes next (use playerScores)
		int firstPlayer = (playerIndex - playTurn + playerScores.size()) % playerScores.size();
		int points = calculatePoints();
		int taker = findTaker(firstPlayer);
		playerScores.set(taker, playerScores.get(taker)+points);

		// Check what points to return from this function
		int returnpoints = 0;
		if (taker == playerIndex) returnpoints = points;

		// Clear the cards on the table (don't worry, pointers to them are tracked in the cardsPlayed deck)
		currentRound.clear();

		// If the game still has more rounds to play, begin the next round just enough for the AI to make another move
		if (isGameValid()) {
			// repeat until taker = playerIndex
			while (taker != playerIndex) {
				// Pick a random number that represents an index of the cards in the invertedDeck (cards yet to be played)
				int index = rng.nextInt(cardsPlayed.invertDeck.size());
				// Takes time equivalent to hand size!
				while (isInMyHand(cardsPlayed.invertDeck.get(index), playoutHand)) {
					index = rng.nextInt(cardsPlayed.invertDeck.size());
				}
				// Use the play card method to put take the card out of the invert deck, into the played deck, and also onto the table
				playCard(cardsPlayed.invertDeck.get(index));
				taker = (taker+1) % playerScores.size();
			}
		}

		// Return the amount of points that the player has received
		return returnpoints;
	}

}