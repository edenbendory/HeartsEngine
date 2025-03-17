/* This file was taken from another GitHub repo that implements a Hearts
 game and players system. Linked here: https://github.com/Devking/HeartsAI */

// This AI will look at the first suit played this round
// If no cards have been played, will pick a random card out of the hand
// Otherwise, pick a random card in the suit first played

import java.util.*;

class RandomPlayAI extends Player {

	Random rng;
	
	RandomPlayAI(String name) { super(name); rng = new Random(); System.out.println("Random Play AI ("+name+") initialized.");  }

	@Override
    Player resetPlayer() { return new RandomPlayAI(name); }

	boolean setDebug() { return false; }

	// NOTE: performAction() must REMOVE the card from the hand
	// we would not want this to be the case in the future
	Card performAction (State masterCopy) {

		// If this is the first move, then we must play the two of spades regardless
		if (masterCopy.firstMove())
			return hand.remove(0);

		// For human debugging: print the hand
		// printHand();

		// Get the first suit that was played this round
		Suit firstSuit = getFirstSuit(masterCopy.currentRound);

		// This code block was written by @edenbendory
		// If no cards were played this round, play a random card 
		if (firstSuit == null) {
			int lastIndex = hand.size();
			// if Hearts aren't legal, eliminiate them from the range of cards we can play
			if (!masterCopy.hasHeartsBroken && !hasAllHearts()) {
				SuitRange heartsRange = getSuitRange(Suit.HEARTS, hand);
				if (heartsRange.getRange() > 0) { lastIndex = heartsRange.startIndex; }
			}
			int index = rng.nextInt(lastIndex);
			return hand.remove(index);
		}

		// Remove a random card of the correct suit
		SuitRange range = getSuitRange(firstSuit, hand);
		if (range.getRange() == 0) return hand.remove(rng.nextInt(hand.size())); // @edenbendory: if we don't have a card in the leading suit
		int index = rng.nextInt(range.getRange());
		return hand.remove(range.startIndex+index);
	}

}