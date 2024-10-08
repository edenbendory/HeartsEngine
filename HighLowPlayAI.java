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

		// If this is the first move in the game, then we must play the two of clubs - DONE
		if (masterCopy.firstMove())
			return hand.remove(0);

        // Get the first suit that was played this round
		Suit firstSuit = getFirstSuit(masterCopy.currentRound);

        boolean heartsBroken = masterCopy.hasHeartsBroken;

        // If we are the first move in the round, play the lowest card
        if (firstSuit == null) {
            // return the lowest card that's not a hearts
            SuitRange clubsSuitRange = getSuitRange(Suit.CLUBS, hand);
            SuitRange diamondsSuitRange = getSuitRange(Suit.DIAMONDS, hand);
            SuitRange spadesSuitRange = getSuitRange(Suit.SPADES, hand);
            int lowIndex = Integer.MAX_VALUE;
            Value clubsLow = null;
            Value diamondsLow = null;
            Value spadesLow = null;

            // !!! make this check cleaner later!!! - maybe make a helper function in Player called getAnyValue() that returns -1 when you call hand.getAnyValue() on a Value that's not in your hand 
            if (clubsSuitRange.startIndex != -1) {
                clubsLow = hand.get(clubsSuitRange.startIndex).getValue(); 
                lowIndex = clubsSuitRange.startIndex;
            }

            if (diamondsSuitRange.startIndex != -1) {
                diamondsLow = hand.get(diamondsSuitRange.startIndex).getValue(); 
            }

            if (spadesSuitRange.startIndex != -1) {
                spadesLow = hand.get(spadesSuitRange.startIndex).getValue(); 
            }

            if (diamondsSuitRange.startIndex !=-1 && diamondsLow.compareTo(lowCard) < lowCard || lowCard == -1) { 
                lowCard = diamondsSuitRange.startIndex; }
            if (spadesSuitRange.startIndex !=-1 && spadesSuitRange.startIndex < lowCard || lowCard == -1) { 
                lowCard = spadesSuitRange.startIndex; } 

            if (heartsBroken || hasAllHearts()) {
                // if hearts is legal, we want the lowest card in our hand overall, which can be a hearts too
                SuitRange heartsSuitRange = getSuitRange(Suit.HEARTS, hand);
                if (heartsSuitRange.startIndex !=-1 && heartsSuitRange.startIndex < lowCard || lowCard == -1) { 
                    lowCard = heartsSuitRange.startIndex; } 
            }

            return hand.remove(lowCard);
        }
        
        SuitRange range = getSuitRange(firstSuit, hand);

        // If we don't have cards in the suit that was first played, return highest card in hand 
		if (range.getRange() == 0) {
            // if hearts have been broken, return highest hearts - DONE
            if (heartsBroken || hasAllHearts()) {
                SuitRange heartsSuitRange = getSuitRange(Suit.HEARTS, hand);
                // check that we have hearts in our hand
                if (heartsSuitRange.endIndex != -1) {
                    return hand.remove(heartsSuitRange.endIndex - 1); 
                }
            }
            // otherwise just the highest card that's not a hearts
            SuitRange clubsSuitRange = getSuitRange(Suit.CLUBS, hand);
            SuitRange diamondsSuitRange = getSuitRange(Suit.DIAMONDS, hand);
            SuitRange spadesSuitRange = getSuitRange(Suit.SPADES, hand);
            int highCard = clubsSuitRange.endIndex - 1;
            if (diamondsSuitRange.endIndex - 1 > highCard) { highCard = diamondsSuitRange.endIndex - 1; }
            if (spadesSuitRange.endIndex - 1 > highCard) { highCard = spadesSuitRange.endIndex - 1; } 

            return hand.remove(highCard); 
        }

        // If we have cards in the suit that was first played - DONE
        Card firstCard = masterCopy.currentRound.get(0);
        System.out.println(range.endIndex);
        for (int i = range.endIndex - 1; i >= range.startIndex; i--) {
            // remove our highest card less than the card played - DONE
			if (hand.get(i).compareTo(firstCard) < 0) { 
                return hand.remove(i);
            }
        }
        // If none of our cards are smaller than the suit that was played, play the lowest card in the suit - DONE
        return hand.remove(range.startIndex); 

	}
}
