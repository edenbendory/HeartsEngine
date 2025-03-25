/* This file was written by @edenbendory. (Partially) adapted from LowPlayAI.java*/ 

// this AI will look at the hand and look at the cards currently on the board
// and play the highest VALID card we can play WITHOUT taking the trick
// (the highest valid card that is lower than the first card played)
// if no cards are on the board, and Hearts has not been broken, and the player has cards other than Hearts in its hand, it will play the lowest non-Hearts card it can play
// if no cards are on the board and Hearts has been broken, or if there are only Hearts cards in the player's hand, it will play the lowest overall card in its hand (including Hearts)
// if the player doesn't have the leading suit, play the highest card in the player's hand, prioritizing Hearts
// If the player is not void in the leading suit, it will determine the maximum card that has been played this trick that's of the leading suit. It will then play the highest card of the leading suit in the player’s hand that is still lower than that maximum card. If none of the player’s cards are lower than the suit that was played, it will play the lowest card in the suit. 

class HighLowPlayAI extends Player {

    HighLowPlayAI(String name) { super(name); System.out.println("High Low Play AI ("+name+") initialized.");  }

    @Override
    Player resetPlayer() { return new HighLowPlayAI(name); }

    @Override
    boolean setDebug() { return false; }

    @Override
	Card performAction (State masterCopy) {
        // For human debugging: print the hand
        // printHand();

		// If this is the first move in the game, then we must play the two of clubs - DONE
		if (masterCopy.firstMove())
			return hand.remove(0);

        // Get the first suit that was played this round
		Suit firstSuit = getFirstSuit(masterCopy.currentRound);

        boolean heartsBroken = masterCopy.hasHeartsBroken;

        if (!masterCopy.hasHeartsBroken && masterCopy.hasAllHeartsAndQueen(hand)) {
            for (int i = 0; i < hand.size(); i++) {
                Card c = hand.get(i);
                if (c.getSuit() == Suit.SPADES && c.getValue() == Value.QUEEN) {
                    return hand.remove(i);
                }
            }
        }

        // If we are the first move in the round, play the lowest card
        if (firstSuit == null) {
            // return the lowest card that's not a hearts - DONE
            SuitRange clubsSuitRange = getSuitRange(Suit.CLUBS, hand);
            SuitRange diamondsSuitRange = getSuitRange(Suit.DIAMONDS, hand);
            SuitRange spadesSuitRange = getSuitRange(Suit.SPADES, hand);
            int lowIndex = 0;
            Value lowValue = Value.ACE;
            Value diamondsLow = null;
            Value spadesLow = null;

            // if hearts haven't been broken, we can't play the queen of spades unless we only have queen and hearts
            if (spadesSuitRange.endIndex != -1 && !heartsBroken && !masterCopy.hasAllHeartsAndQueen(hand) && hand.get(spadesSuitRange.endIndex - 1).getValue() == Value.QUEEN) {
                // if the queen is the only spades card, we can play no other spades
                if (spadesSuitRange.startIndex == spadesSuitRange.endIndex - 1) {
                    spadesSuitRange.startIndex = -1;
                    spadesSuitRange.endIndex = -1;
                } else {
                    // otherwise, we just can't play the queen 
                    spadesSuitRange.endIndex--;
                }
            }
            if (spadesSuitRange.startIndex != -1 && !heartsBroken && !masterCopy.hasAllHeartsAndQueen(hand) && hand.get(spadesSuitRange.startIndex).getValue() == Value.QUEEN) {
                if (spadesSuitRange.startIndex == spadesSuitRange.endIndex - 1) {
                    spadesSuitRange.startIndex = -1;
                    spadesSuitRange.endIndex = -1;
                } else {
                    spadesSuitRange.startIndex++;
                }
            }

            // !!! make this check cleaner later!!! - maybe make a helper function in Player called getAnyValue() that returns -1 when you call hand.getAnyValue() on a Value that's not in your hand 
            if (clubsSuitRange.startIndex != -1) {
                lowValue = hand.get(clubsSuitRange.startIndex).getValue(); 
                lowIndex = clubsSuitRange.startIndex;
            }

            if (diamondsSuitRange.startIndex != -1) {
                diamondsLow = hand.get(diamondsSuitRange.startIndex).getValue(); 
            }

            if (spadesSuitRange.startIndex != -1) {
                spadesLow = hand.get(spadesSuitRange.startIndex).getValue(); 
            }

            if (diamondsLow != null && diamondsLow.compareTo(lowValue) <= 0) { 
                lowValue = diamondsLow;
                lowIndex =  diamondsSuitRange.startIndex; 
            }
            if (spadesLow != null && spadesLow.compareTo(lowValue) <= 0) { 
                lowValue = spadesLow;
                lowIndex =  spadesSuitRange.startIndex; 
            } 

            if (heartsBroken || hasAllHearts()) {
                // if hearts is legal, we want the lowest card in our hand overall, which can be a hearts too - DONE
                SuitRange heartsSuitRange = getSuitRange(Suit.HEARTS, hand);
                Value heartsLow = null;
                if (heartsSuitRange.startIndex != -1) {
                    heartsLow = hand.get(heartsSuitRange.startIndex).getValue(); 
                }
                if (heartsLow != null && heartsLow.compareTo(lowValue) < 0) { 
                    // lowValue = heartsLow; 
                    lowIndex =  heartsSuitRange.startIndex; 
                } 
            }

            return hand.remove(lowIndex);
        }
        
        SuitRange range = getSuitRange(firstSuit, hand);

        // If we don't have cards in the suit that was first played, return highest card in hand - DONE
		if (range.getRange() == 0) {
            // if hearts have been broken, return highest hearts - DONE
            if (heartsBroken || hasAllHearts()) {
                SuitRange heartsSuitRange = getSuitRange(Suit.HEARTS, hand);
                // check that we have hearts in our hand
                if (heartsSuitRange.endIndex != -1) {
                    return hand.remove(heartsSuitRange.endIndex - 1); 
                }
            }
            
            // otherwise just the highest card that's not a hearts - DONE
            SuitRange clubsSuitRange = getSuitRange(Suit.CLUBS, hand);
            SuitRange diamondsSuitRange = getSuitRange(Suit.DIAMONDS, hand);
            SuitRange spadesSuitRange = getSuitRange(Suit.SPADES, hand);

            // if hearts haven't been broken, we can't play the queen of spades unless we only have queen and hearts
            if (!heartsBroken && spadesSuitRange.endIndex != -1 && !masterCopy.hasAllHeartsAndQueen(hand) && hand.get(spadesSuitRange.endIndex - 1).getValue() == Value.QUEEN) {
                // if the queen is the only spades card, we can play no other spades
                if (spadesSuitRange.startIndex == spadesSuitRange.endIndex - 1) {
                    spadesSuitRange.startIndex = -1;
                    spadesSuitRange.endIndex = -1;
                    
                } else {
                    // otherwise, we just can't play the queen 
                    spadesSuitRange.endIndex--;
                }
            }
            if (!heartsBroken && spadesSuitRange.startIndex != -1 && !masterCopy.hasAllHeartsAndQueen(hand) && hand.get(spadesSuitRange.startIndex).getValue() == Value.QUEEN) {
                if (spadesSuitRange.startIndex == spadesSuitRange.endIndex - 1) {
                    spadesSuitRange.startIndex = -1;
                    spadesSuitRange.endIndex = -1;
                } else {
                    spadesSuitRange.startIndex++;
                }
            }

            int highIndex = hand.size() - 1;
            Value highValue = Value.TWO;
            Value diamondsHigh = null;
            Value spadesHigh = null;

            // !!! make this check cleaner later!!! - maybe make a helper function in Player called getAnyValue() that returns -1 when you call hand.getAnyValue() on a Value that's not in your hand 
            if (clubsSuitRange.endIndex != -1) {
                highValue = hand.get(clubsSuitRange.endIndex - 1).getValue(); 
                highIndex = clubsSuitRange.endIndex - 1;
            }

            if (diamondsSuitRange.endIndex != -1) {
                diamondsHigh = hand.get(diamondsSuitRange.endIndex - 1).getValue(); 
            }

            if (spadesSuitRange.endIndex != -1) {
                spadesHigh = hand.get(spadesSuitRange.endIndex - 1).getValue(); 
            }

            if (diamondsHigh != null && diamondsHigh.compareTo(highValue) >= 0) { 
                highValue = diamondsHigh;
                highIndex =  diamondsSuitRange.endIndex - 1; 
            }
            if (spadesHigh != null && spadesHigh.compareTo(highValue) >= 0) { 
                // highValue = spadesHigh;
                highIndex =  spadesSuitRange.endIndex - 1; 
            } 

            return hand.remove(highIndex); 
        }

        // If we have cards in the suit that was first played - DONE

        // Get the max card that's been played this round that's of the leading suit - DONE
        Card maxCard = masterCopy.currentRound.get(0);
        int numCardsPlayed = masterCopy.currentRound.size();
        for (int i = 1; i < numCardsPlayed; i++) {
            Card curCard = masterCopy.currentRound.get(i);
            if (curCard.getSuit() == firstSuit && curCard.compareTo(maxCard) > 0) {
                maxCard = curCard;
            }
        }
        
        for (int i = range.endIndex - 1; i >= range.startIndex; i--) {
            // remove our highest card less than the max card played - DONE
			if (hand.get(i).compareTo(maxCard) < 0) { 
                return hand.remove(i);
            }
        }
        // If none of our cards are smaller than the suit that was played, play the lowest card in the suit - DONE
        return hand.remove(range.startIndex); 

	}
}
