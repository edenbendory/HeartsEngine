import java.util.ArrayList;

public class ConstantGameGenerator {
    public static void main(String[] args) {
        Deck thing = new Deck();
        thing.printDeck();

        Player p1 = new HighLowPlayAI("EdHighLowPlay");
		// Player p3 = new LowPlayAI("WellsLowPlay");
		//Player p4 = new RandomPlayAI("Wells");
		Player p2 = new RandomPlayAI("JaiRandomPlay");
		// Player p3 = new LookAheadPlayer("AntLookAhead");
		Player p3 = new UCTPlayer("UCTPlayer");
		Player p4 = new MCTSPlayer("JulianMCTS");

        ArrayList<Player> playerOrder = new ArrayList<Player>();
		playerOrder.add(p1);
		playerOrder.add(p2);
		playerOrder.add(p3);
		playerOrder.add(p4);

        p1.addToHand ( new Card(Suit.DIAMONDS, Value.NINE));
        p1.addToHand ( new Card(Suit.DIAMONDS, Value.ACE));
        p1.addToHand ( new Card(Suit.SPADES, Value.KING));
        p1.addToHand ( new Card(Suit.HEARTS, Value.THREE));

        p2.addToHand ( new Card(Suit.CLUBS, Value.TEN));
        p2.addToHand ( new Card(Suit.CLUBS, Value.ACE));
        p2.addToHand ( new Card(Suit.HEARTS, Value.FOUR));
        p2.addToHand ( new Card(Suit.HEARTS, Value.SIX));

        p3.addToHand ( new Card(Suit.SPADES, Value.THREE));
        p3.addToHand ( new Card(Suit.SPADES, Value.FIVE));
        p3.addToHand ( new Card(Suit.SPADES, Value.SIX));
        p3.addToHand ( new Card(Suit.SPADES, Value.JACK));

        p4.addToHand ( new Card(Suit.HEARTS, Value.TWO));
        p4.addToHand ( new Card(Suit.HEARTS, Value.NINE));
        p4.addToHand ( new Card(Suit.HEARTS, Value.TEN));
        p4.addToHand ( new Card(Suit.HEARTS, Value.JACK));

        Deck deck = new Deck(thing);

        while (!deck.invertDeck.isEmpty()) {
            deck.drawTop();
        }
        
        for (Player p : playerOrder) {
            p.printHand();
            for (Card c : p.hand) {
                deck.invertDeck.add(c);

                int size = deck.allCards.size();
                for (int i = 0; i < size; i++) {
                    if(deck.allCards.get(i).equals(c)) {
                        deck.allCards.remove(i);
                        break;
                    }
                }

            }
        }
        deck.initCounter = true;

        // boolean gameOver = false;
		Game round = new Game(deck, p1, p2, p3, p4);
        round.allowForMidGamePlaying();
        round.playerScores.add(0);
        round.playerScores.add(0);
        round.playerScores.add(5);
        round.playerScores.add(14);
        round.firstPlayer = 2;
        p1.points = 0;
        p2.points = 0;
        p3.points = 5;
        p4.points = 14;
        round.playExistingGame();
		// int i = 1;
		// while (!gameOver) {
		// 	System.out.println("\n--------------------------------------------");
		// 	System.out.println("--------------------------------------------");
		// 	System.out.println("--------------------------------------------");
		// 	System.out.println("Playing Game #"+i);
		// 	System.out.println("--------------------------------------------");
		// 	System.out.println("--------------------------------------------");
		// 	System.out.println("--------------------------------------------\n");
		// 	round.playExistingGame();
		// 	i++;

		// 	for (Player p : round.playerOrder) {
		// 		if (p.getPoints() >= 100) {
		// 			gameOver = true;
		// 		}
		// 	}
		// }
    }
}
