import java.util.*;

import org.w3c.dom.Node;

class UCTPlayer extends Player {

    int             myPNumber;
    ArrayList<Card> myHand;
    // ArrayList<ArrayList<Card>>	playerHands; // To keep track of each player's hand
	Random 			rng;

	final int 		numIterations = 26; 		// How many times we go through MCTS before making a decision
	final int 		maxDepth = 3; 	// How many nodes to expand to before doing random playouts
	Node 			root;

    public class Node {
		State 			state; // the current state of the game
		ArrayList<Card> curHand; // the card hand of that node
        ArrayList<Card> myCurHand; // My card hand at the point of the game of that node
        int             playerIndex; // which player is this node
		int 			winCount;	// the win score of this node		
		int 			visitCount; // the visit count of this node
		Node 			parent; // the parent of this node 
		ArrayList<Node> children; // the children of this node (in other words, the next state of the game if this node plays each potential card in their hand) !!! change so this to match in rest of code 
        // TODO: optimize storage, maybe by changing this back to a normal array with null entries, or explore representing cards with bit-vector
        int             handIndex; // which number card this node is in their parent's hand
		int 			depth; // the depth of this node 
        boolean         lastInRound; // if this node was the last player in the round 

		Node (State s, ArrayList<Card> hand, ArrayList<Card> myCurHand, Node p, int index) {
			state = s;
			curHand = hand;
            myCurHand = myCurHand;
            playerIndex = state.playerIndex;
            // playerHands.set(playerIndex, hand); // replace whatever hand was there before with the hand being passed in 
            // System.out.println("Num player hands now: " + playerHands.size());
			winCount = 0;
			visitCount = 0;
			parent = p;
			children = new ArrayList<Node>(); 		// largest amount of children is # of cards in the hand
            handIndex = index;

            if (p != null) {
                depth = p.depth + 1;
            }
            // root
			else {
                depth = 0;
                myPNumber = s.playerIndex;
            }
            lastInRound = false;
		}
	}

    UCTPlayer(String name) { 
		super(name); 
		System.out.println("UCTPlayer AI ("+name+") initialized."); 

		myHand = new ArrayList<>(hand);
        // playerHands = new ArrayList<>();
		rng = new Random();

        // represents 4 players (currently) empty hands
        // playerHands.add(new ArrayList<>());
        // playerHands.add(new ArrayList<>());
        // playerHands.add(new ArrayList<>());
        // playerHands.add(new ArrayList<>());
        // System.out.println("Num player hands: " + playerHands.size());
	}

    boolean setDebug() { return false; }

    // void generateHands(State state, Node root) {
    //     ArrayList<Card> cardsLeft = new ArrayList<Card> (state.cardsPlayed.invertDeck);
    //     Collections.shuffle(cardsLeft);

    //     ArrayList<Player> players = new ArrayList<>();
    //     Player p1 = new RandomPlayAI("p1");
    //     Player p2 = new RandomPlayAI("p2");
    //     Player p3 = new RandomPlayAI("p3");
    //     players.add(p1);
    //     players.add(p2);
    //     players.add(p3);

    //     // currentRound.size() = number player this round (ex- 2 cards have been put down, this is the third player of the round)
    //     // (# cards played this round) + (index of first player to go - say it's player 1) % 4 = index of current player 
    //     // for (int i = 0; i < state.currentRound.size(); i++) {
    //     //     int playerIndex = i + firstPlayer % 4;
    //     // }
    //     System.out.println("Current Player Number: " + state.playerIndex);
    //     System.out.println("Current Root Number: " + root.playerIndex);

    //     int curPlayer = root.playerIndex;
    //     for (int i = 0; i < cardsLeft.size(); i++) { 
    //         int dealPlayer = curPlayer
    //         p.addToHand ( cardsLeft.remove(i) ); 
    //     }

    // }

    int runMCTS (State originalState) {
        root = new Node(originalState, myHand, myHand, null, -1);

        // run multiple games until we've hit the max number
        for (int i = 0; i < numIterations; i++) {

            // Select which Node to expand
            Node bestNode = selectBestNode(root); 

            // Expand that Node
            if (bestNode.children.isEmpty()) { // ??? only if haven't expanded yet ???
                expandNode(bestNode); 
            }

            // simulation 
            Node nodeToExplore = bestNode; // if we didn't just expand on bestNode, then we're on the node we wanna be simulating
            if (!bestNode.children.isEmpty()) { // if we did just expand on bestNode, then we wanna choose a random child to simulate on 
                int randomNode = (int) (Math.random() * bestNode.children.size());
                nodeToExplore = bestNode.children.get(randomNode); // select which random child to simulate (attach a winScore to)
            }
            ArrayList<Integer> sampleScores = simulateRandomPlayout(nodeToExplore); // Simulate

            // backpropogation
            backPropogation(nodeToExplore, sampleScores); 
        }

        return bestRewardChild(root);
    }

    // Select which node to expand next using UCT
	Node selectBestNode(Node rootNode) {
		Node curNode = rootNode;

		// Go through this node
		while (curNode.state.isGameValid() && maxDepth > curNode.depth) {
            // if this node has no children
            if (curNode.children.isEmpty()) {
                return curNode; // we need to expand on that node
            }
            // if this node does have chilren 
            else {
                // if not every child has been visited
                if (curNode.visitCount < curNode.children.size()) {
                    // iterate over the children to find the child with visitCount = 0
                    for (Node child : curNode.children) {
                        // if there's a child that hasn't been visited, simulate/explore it
                        if (child.visitCount == 0) { return child; }
                    }
                }
                // choose which Node is the best of the valid child nodes
                curNode = bestUCTChild(curNode);
            }
		}
		return curNode;
	}

    public static double uctValue(int totalVisitCount, double nodeWinCount, int nodeVisitCount) {
        if (nodeVisitCount == 0) {
            return Integer.MAX_VALUE;
        }
        return (
            ((double) nodeWinCount / (double) nodeVisitCount) 
            + Math.sqrt(2) * Math.sqrt(Math.log(totalVisitCount) / (double) nodeVisitCount));
    }

    private static Node bestUCTChild(Node node) {
        // return the child with the max uctValue
        double bestValue = -Double.MAX_VALUE;
        Node bestChild = node.children.get(0); // ??? good to start with child 0?

        for (Node child : node.children) {
            // if (curChild != null) { // ??? don't need this right ???
            double uctVal = uctValue(node.visitCount, child.winCount, child.visitCount);
            if (uctVal > bestValue) {
                bestValue = uctVal;
                bestChild = child;
            }
        }

        return bestChild;
    }

    // Expands the game tree by appending all possible states from curNode (the leaf node)
    private void expandNode(Node curNode) {
        // if this node hasn't been explored yet, and it has a parent, 
        // that means it's a child node that was just added (it's not the root), and needs 
        // to be explored before it's expanded upon 
        // --> if the visitCount = 0, and it's the root, continue to expand
        // --> if the visitCount = 0, an it's not the root, don't expand
        // --> if the visitCount > 0, then expand 
        // ??? FIX THIS CONDITION??? 
        if (curNode.visitCount == 0 && curNode.parent != null) {
            return;
        }

        // Find out which children are valid 
        int[] indexRange = getValidRange(curNode.state, curNode.curHand);
        int firstIndex = indexRange[1];
        int lastIndex = indexRange[2];

        // Create a new Node representing each valid child 
        for (int i = firstIndex; i < lastIndex; i++) {
            // Notice: This will exclude the invalid children (only valid children are added as children)
            addNewChild(curNode, i);
        }
    }

    // Returns the range of valid "children" cards that curNode can play this round
    private int[] getValidRange(State curState, ArrayList<Card> curHand) {
        // Get the first suit that was played this round
       Suit firstSuit = getFirstSuit(curState.currentRound);

       SuitRange range = getSuitRange(firstSuit, curHand);
       // If we have firstSuit, get the range of cards we can play rn
       int firstIndex = range.startIndex;
       int lastIndex = range.endIndex;

       // If this is the first move in the round
       if (firstSuit == null) {
           // If hearts has broken or we only have hearts left, we can play any card in our hand
           if (curState.hasHeartsBroken || hasAllHearts(curHand)) {
               firstIndex = 0;
               lastIndex = curHand.size();
           } else {
               // If hearts has not broken, and we have at least one non-hearts card we can play
               SuitRange heartsRange = getSuitRange(Suit.HEARTS, curHand);
               // If we have no hearts, we can play anything
               if (heartsRange.startIndex == -1) {
                   firstIndex = 0;
                   lastIndex = curHand.size();
               } else {
                   // Otherwise, we need to eliminate the hearts range
                   firstIndex = 0;
                   lastIndex = heartsRange.startIndex;
               }
           }
       } 
       
       // If we are void in firstSuit (if firstIndex is -1 and firstSuit != null), we can play any card in our hand
       if (firstIndex == -1) {
           firstIndex = 0;
           lastIndex = curHand.size();
       }

       int[] indexRange = new int[2];
       indexRange[0] = firstIndex;
       indexRange[1] = lastIndex;
       return indexRange;
   }

    // Used to check if all the cards in the given hand is hearts 
    private boolean hasAllHearts(ArrayList<Card> hand) {
        boolean flag = true;
        for (Card c : hand) { if (c.getSuit() != Suit.HEARTS) flag = false; }
        return flag;
    }

    private void addNewChild(Node parentNode, int childIndex) {
        State childState = new State(parentNode.state); // inherits copy of parent state
        int nextPlayer = childState.advanceState(parentNode.curHand.get(childIndex), parentNode.curHand);
        // if (nextPlayer == -1) {System.out.println("Error, we've made a mistake.");}

        ArrayList<Card> childHand;
        int playerIndex = nextPlayer;
        if (playerIndex == myPNumber) {
            // if the player we're up to is Me, then we wanna inherit the hand that's been passed down 
            childHand = new ArrayList<>(parentNode.myCurHand);
        }
        else {
            // otherwise, generate a random hand for the player 
            childHand = generateHand(childState, parentNode); // generate a random hand based on the state of the game - !!! CHANGE THIS TO BE MORE THAN ONLY THE NUMBER OF CARDS THEIR PARENT HAS - should be the number of possible cards in their hand (not just the number of cards in their hand) !!!
        }

        ArrayList<Card> myCurHand = new ArrayList<>(parentNode.myCurHand); // pass my hand along 
        if (parentNode.playerIndex == myPNumber) { // if I just went 
            myCurHand.remove(childIndex); // then update my hand 
        }

        int handIndex = childIndex;

		// Create a new child node that corresponds to the card we have just successfully played
		parentNode.children.add(new Node(childState, childHand, myCurHand, parentNode, handIndex));
    }

    private ArrayList<Card> generateHand(State state, Node node) {
        ArrayList<Card> cardsLeft = new ArrayList<Card> (state.cardsPlayed.invertDeck);
        Collections.shuffle(cardsLeft);
        ArrayList<Card> hand = new ArrayList<Card>();

        // currentRound.size() = number player this round (ex- 2 cards have been put down, this is the third player of the round)
        // (# cards played this round) + (index of first player to go - say it's player 1) % 4 = index of current player 
        // for (int i = 0; i < state.currentRound.size(); i++) {
        //     int playerIndex = i + firstPlayer % 4;
        // }

        // everyone has the same amount of cards at the beginning of a round - so I should have the same as my parent
        int handSize = node.curHand.size(); 
        // unless a new round just started, in which case everyone has put a card down, and so I have one less card than my parent b/c I am first
        if (state.firstInRound()) {handSize--;} 

        System.out.println("Hand Size: " + handSize);
        System.out.println("Turn Number: " + state.turnNumber());
        System.out.println("Current Player Number: " + state.playerIndex);
        System.out.println("Current Tree Depth: " + node.depth);
        System.out.println("Current Node Number: " + node.playerIndex);

        // right now this is random, but later we will change this to update based on player tables 
        for (int i = 0; i < handSize; i++) { 
            hand.add(cardsLeft.remove(i)); 
        }

        return hand;
    }

    // pick a random node to simulate
    private ArrayList<Integer> simulateRandomPlayout(Node node) {
        // "global" variables that we want to alter throughout the simulation 
        State tempState = new State(node.state); // state of the game
        ArrayList<Card> mySimulatedHand = new ArrayList<Card>(node.myCurHand); // my hand
        ArrayList<Card> cardsLeft = new ArrayList<Card>(tempState.cardsPlayed.invertDeck); // essentially every other player's hand

        int curPlayer = node.playerIndex; // to start off
        ArrayList<Card> cardPile;
        // !!! make sure this isn't an infinite loop, and that removing a card from cardPile accurately removes it from cardsLeft (that cardPile = cardsLeft makes cardPile point to cardsLeft's memory)
        while (!cardsLeft.isEmpty()) {
            if (curPlayer == myPNumber) {
                cardPile = mySimulatedHand; // My hand as that node knows it at that point in the tree (not my hand when the tree was created)
            } else {
                cardPile = cardsLeft; // !!! later change this to be the YM cards in YMN table
            }

            // debugging / test
            System.out.println("Cards Left: ");
            for (Card curCard : cardPile) {
                System.out.print(curCard);
            }
            System.out.println("Player Hand: ");
            for (Card curCard : node.curHand) {
                System.out.print(curCard);
            }
            // !!! make sure that the above print statements print out cardsLeft the same way a node's hand is printed, to make sure getValidRange will behave the same way 

            // given the cards in the "pile" (my hand or cardsLeft) we can draw from, determine the valid range of cards that can be played
            int[] indexRange = getValidRange(tempState, cardPile);
            int firstIndex = indexRange[0];
            int lastIndex = indexRange[1];

            // randomly select a card from the valid range of the "pile"
            Card cardToPlay = cardPile.get(firstIndex + (int)(Math.random() * ((lastIndex - firstIndex) + 1)));

            curPlayer = tempState.advanceState(cardToPlay, cardPile);
            cardPile.remove(cardToPlay);
        }

        return tempState.playerScores;
    }


}

