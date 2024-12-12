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

        p1.addToHand ( new Card(Suit.DIAMONDS, Value.THREE));
        p1.addToHand ( new Card(Suit.SPADES, Value.ACE));
        p1.addToHand ( new Card(Suit.HEARTS, Value.TWO));
        p1.addToHand ( new Card(Suit.HEARTS, Value.FOUR));
        p1.addToHand ( new Card(Suit.HEARTS, Value.SEVEN));
        p1.addToHand ( new Card(Suit.HEARTS, Value.KING));

        p2.addToHand ( new Card(Suit.DIAMONDS, Value.TEN));
        p2.addToHand ( new Card(Suit.SPADES, Value.QUEEN));
        p2.addToHand ( new Card(Suit.HEARTS, Value.FIVE));
        p2.addToHand ( new Card(Suit.HEARTS, Value.EIGHT));
        p2.addToHand ( new Card(Suit.HEARTS, Value.TEN));
        p2.addToHand ( new Card(Suit.HEARTS, Value.QUEEN));

        p3.addToHand ( new Card(Suit.DIAMONDS, Value.SIX));
        p3.addToHand ( new Card(Suit.DIAMONDS, Value.NINE));
        p3.addToHand ( new Card(Suit.SPADES, Value.FIVE));
        p3.addToHand ( new Card(Suit.SPADES, Value.SIX));
        p3.addToHand ( new Card(Suit.HEARTS, Value.THREE));
        p3.addToHand ( new Card(Suit.HEARTS, Value.ACE));

        p4.addToHand ( new Card(Suit.CLUBS, Value.KING));
        p4.addToHand ( new Card(Suit.DIAMONDS, Value.FIVE));
        p4.addToHand ( new Card(Suit.SPADES, Value.TEN));
        p4.addToHand ( new Card(Suit.HEARTS, Value.SIX));
        p4.addToHand ( new Card(Suit.HEARTS, Value.NINE));
        p4.addToHand ( new Card(Suit.HEARTS, Value.JACK));

        Deck deck = new Deck(thing);

        while (!deck.allCards.isEmpty()) {
            deck.drawTop();
        }
        for (Player p : playerOrder) {
            for (Card c : p.hand) {
                deck.allCards.add(c);
                deck.invertDeck.remove(c);
            }
        }


        boolean gameOver = false;
		Game round = new Game(deck, p1, p2, p3, p4);
        round.allowForMidGamePlaying();
		int i = 1;
		while (!gameOver) {
			System.out.println("\n--------------------------------------------");
			System.out.println("--------------------------------------------");
			System.out.println("--------------------------------------------");
			System.out.println("Playing Game #"+i);
			System.out.println("--------------------------------------------");
			System.out.println("--------------------------------------------");
			System.out.println("--------------------------------------------\n");
			round.playExistingGame();
			i++;

			for (Player p : round.playerOrder) {
				if (p.getPoints() >= 100) {
					gameOver = true;
				}
			}
		}
    }
}
