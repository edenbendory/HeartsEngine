// this AI will look at the hand and look at the cards currently on the board
// and play the highest VALID card we can play WITHOUT taking the trick
// if no cards are on the board, will play the lowest non-hearts (if possible)
// if no cards are on the board and there are only hearts, play the lowest hearts

class HighLowPlayAI extends Player {

    HighLowPlayAI(String name) { super(name); System.out.println("High Low Play AI ("+name+") initialized.");  }

    boolean setDebug() { return false; }

	Card performAction (State masterCopy) {
        // For human debugging: print the hand
		printHand();

		// If this is the first move, then we must play the two of clubs 
		if (masterCopy.firstMove())
			return hand.remove(0);

        // Get the first suit that was played this round
		Suit firstSuit = getFirstSuit(masterCopy.currentRound);

        boolean heartsBroken = masterCopy.hasHeartsBroken;

        // If we are the first one to play a card this round, play the lowest card in our hand
        if (firstSuit == null) {
            if (heartsBroken || hasAllHearts()) {
                return hand.remove(0); // just return lowest ??? is this lowest or just lowest of first suit ???
            }
            else {
                int index = 0;
				while (hand.get(index).getSuit() == Suit.HEARTS) {
					index++;
				}
                return hand.remove(0); // return lowest non hearts !!! check that it works
            }
        }
        
        SuitRange range = getSuitRange(firstSuit, hand);

        // If we don't have cards in the suit that was first played, return highest card in hand 
		if (range.getRange() == 0) {
            // if hearts have been broken, return highest hearts
            if (heartsBroken || hasAllHearts()) {
                SuitRange heartsSuitRange = getSuitRange(Suit.HEARTS, hand);
                return hand.remove(heartsSuitRange.endIndex); 
            }
            else {
                // otherwise just the highest card that's not a hearts
                // ??? !!! is this the highest card or is it just moving backwards in the deck? check the deck order
                int index = hand.size()-1;
				while (hand.get(index).getSuit() == Suit.HEARTS) {
					index--;
				}
                return hand.remove(index); 
            }
        }

        // If we have cards in the suit that was first played
        Card firstCard = masterCopy.currentRound.get(0);
        for (int i = range.endIndex; i >= range.startIndex; i--) {
            // remove our highest card less than the card played
			if (hand.get(i).compareTo(firstCard) < 0) { // !!! check that compareto works
                return hand.remove(i);
            }
        }
        // If none of our cards are smaller than the suit that was played, play the lowest card in the suit
        return hand.remove(range.startIndex); // !!! make sure indices line up

        // ??? (Later add: If hearts have been broken, play the lowest hearts before another suit)
	}
}
