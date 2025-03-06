/* The essence and structure of this code was written by @edenbendory.

The code was based on and partially supplemented by:
* MCTSPlayer.java in the repo at this link https://github.com/Devking/HeartsAI
* The MCTS structure and code snippets from this tutorial: https://www.baeldung.com/java-monte-carlo-tree-search
* The functions in the mcts folder in the repo at this link: https://github.com/eugenp/tutorials/tree/master/algorithms-modules/algorithms-searching/src/main/java/com/baeldung/algorithms/mcts */

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.*;

class UCTPlayer extends Player {

    int             myPNumber; // DONE
    ArrayList<Card> myHand;
    ArrayList<ArrayList<Card>> playerHands; // To keep track of each player's hand
    Random          rand;
    boolean         debug = false;

	final int 		numIterations = 100; 		// How many times we go through MCTS before making a decision
	final int 		maxDepth = Integer.MAX_VALUE; 	// How many nodes to expand to before doing random playouts
	Node 			root;

    public class Node {
		State 			state; // the current state of the game
		ArrayList<Card> curHand; // the card hand of that node
        ArrayList<ArrayList<Card>> curPlayerHands; // Player hands at that point in the game
        ArrayList<Card> myCurHand; // My card hand at the point of the game of that node
        int             playerIndex; // which player is this node
		int 			winScore;	// the win score of this node		
		int 			visitCount; // the visit count of this node
		Node 			parent; // the parent of this node 
        // the children of this node (in other words, the next state of the game if this node plays each potential card in their hand) 
		ArrayList<Node> children; 
        // ^^ TODO: optimize storage, maybe by changing this back to a reg array with null entries, or explore representing cards with bit-vector
        int             handIndex; // which number card this node is in their parent's hand
		int 			depth; // the depth of this node 

		Node (State s, ArrayList<Card> hand, ArrayList<Card> myCurHand, ArrayList<ArrayList<Card>> curPlayerHands, Node p, int index) {
			state = s;
			curHand = new ArrayList<>(hand);
            this.myCurHand = new ArrayList<>(myCurHand);
            this.curPlayerHands = new ArrayList<>(curPlayerHands);
            playerIndex = state.playerIndex;
            // playerHands.set(playerIndex, hand); // replace whatever hand was there before with the hand being passed in 
            // System.out.println("Num player hands now: " + playerHands.size());
			winScore = 0;
			visitCount = 0;
			parent = p;
			children = new ArrayList<>(); 		// largest amount of children is # of cards in the hand
            handIndex = index;

            if (p != null) {
                depth = p.depth + 1;
            }
            // root
			else {
                depth = 0;
            }
		}
	}

    UCTPlayer(String name) { 
		super(name); 
		System.out.println("UCTPlayer AI ("+name+") initialized."); 

		myHand = new ArrayList<>(hand);
        rand = new Random();
	}

    @Override
    Player resetPlayer() { return new UCTPlayer(name); }

    @Override
    boolean setDebug() { return false; }

    public int getNumIterations() { return numIterations; }
    public int getMaxDepth() { return maxDepth; }

    int runMCTS (State originalState, ArrayList<Player> playerOrder) {
        myPNumber = originalState.playerIndex;
        myHand = new ArrayList<>(hand);
        playerHands = generateHands(originalState, playerOrder);
        root = new Node(originalState, myHand, myHand, playerHands, null, -1);

        assert(root.children.isEmpty());

        // run multiple games until we've hit the max number
        for (int i = 0; i < numIterations; i++) {

            // Select which Node to expand
            Node bestNode = selectBestNode(root); 

            // Expand that Node
            if (bestNode.children.isEmpty()) {
                expandNode(bestNode); 
            }

            // simulation 
            Node nodeToExplore = bestNode; // if we didn't just expand on bestNode, then we're on the node we wanna be simulating
            if (!bestNode.children.isEmpty()) { // if we did just expand on bestNode, then we wanna choose a random child to simulate on 
                int randomNode = (int) (Math.random() * bestNode.children.size());
                nodeToExplore = bestNode.children.get(randomNode); // select which random child to simulate (attach a winScore to)
            }
            ArrayList<Integer> sampleScores = simulateHighLowPlayout(nodeToExplore); // Simulate

            // backpropogation
            backPropogate(nodeToExplore, sampleScores); 
        }

        return bestRewardChild(root);
    }

    private ArrayList<ArrayList<Card>> generateHands(State state, ArrayList<Player> playerOrder) {
        ArrayList<ArrayList<Card>> playerHands = new ArrayList<>();
        playerHands.add(new ArrayList<>());
        playerHands.add(new ArrayList<>());
        playerHands.add(new ArrayList<>());
        playerHands.add(new ArrayList<>());
        for (int i = 0; i < 4; i++) {
            playerHands.set(i, playerOrder.get(i).hand);
        }

        return playerHands;
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

    public static double uctValue(int totalVisitCount, double nodeWinScore, int nodeVisitCount) {
        if (nodeVisitCount == 0) {
            return Integer.MAX_VALUE;
        }
        return (
            ((double) nodeWinScore / (double) nodeVisitCount) 
            + (Math.sqrt(2) * Math.sqrt(Math.log(totalVisitCount) / (double) nodeVisitCount)));
    }

    private static Node bestUCTChild(Node node) {
        // return the child with the max uctValue
        double bestValue = -Double.MAX_VALUE;
        Node bestChild = node.children.get(0); 

        for (Node child : node.children) {
            double uctVal = uctValue(node.visitCount, child.winScore, child.visitCount);
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
        if (curNode.visitCount == 0 && curNode.parent != null) {
            return;
        }
        if (curNode.depth >= maxDepth) {
            return;
        }
        if (curNode.curHand.isEmpty()) {
            return;
        }

        // Find out which children are valid 
        int[] indexRange = getValidRange(curNode.state, curNode.curHand);
    
        int firstIndex = indexRange[0];
        int lastIndex = indexRange[1];

        // Create a new Node representing each valid child 
        for (int i = firstIndex; i <= lastIndex; i++) { 
            // Notice: This will exclude the invalid children (only valid children are added as children)
            addNewChild(curNode, i);
        }

        // sanity check:
        Node prevChild = curNode.children.get(0);
        for (Node child : curNode.children) {
            assert(child.state.cardsPlayed.allCards.size() == prevChild.state.cardsPlayed.allCards.size());
            assert(child.state.cardsPlayed.invertDeck.size() == prevChild.state.cardsPlayed.invertDeck.size());
            assert(child.state.currentRound.size() == prevChild.state.currentRound.size());
            assert(child.depth == prevChild.depth);
            assert(child.myCurHand.size() == prevChild.myCurHand.size());

            prevChild = child;
        }

        assert((lastIndex - firstIndex + 1) == curNode.children.size());
    }

    // Returns the range of valid "children" cards that curNode can play this round
    // Index 0 contains the firstIndex in the range, and index 1 contains the lastIndex in the range (both inclusive)
    private int[] getValidRange(State curState, ArrayList<Card> curHand) {
        assert(!curHand.isEmpty());

        // Get the first suit that was played this round
        Suit firstSuit = getFirstSuit(curState.currentRound);

        SuitRange range = getSuitRange(firstSuit, curHand);
        // If we have firstSuit, get the range of cards we can play rn
        int firstIndex = range.startIndex;
        int lastIndex = range.endIndex - 1;

        // If this is the first move in the round
        if (firstSuit == null) {
            // If hearts has broken or we only have hearts left, we can play any card in our hand
            if (curState.hasHeartsBroken || hasAllHearts(curHand)) {
                firstIndex = 0;
                lastIndex = curHand.size() - 1;
            } else {
                // If hearts has not broken, and we have at least one non-hearts card we can play
                SuitRange heartsRange = getSuitRange(Suit.HEARTS, curHand);
                // If we have no hearts, we can play anything
                if (heartsRange.startIndex == -1) {
                    firstIndex = 0;
                    lastIndex = curHand.size() - 1;
                } else {
                    // Otherwise, we need to eliminate the hearts range
                    firstIndex = 0;
                    lastIndex = heartsRange.startIndex - 1;
                }
            }
        } 
       
        // If we are void in firstSuit (if firstIndex is -1 and firstSuit != null), we can play any card in our hand
        if (firstIndex == -1) {
            firstIndex = 0;
            lastIndex = curHand.size() - 1;
        }

        int[] indexRange = new int[2];
        indexRange[0] = firstIndex;
        indexRange[1] = lastIndex;

        assert(firstIndex >= 0 && lastIndex >= 0);
        
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
        int nextPlayer = childState.advanceState(parentNode.curHand.get(childIndex), parentNode.curHand, debug);

        ArrayList<Card> myCurHand = new ArrayList<>(parentNode.myCurHand); // pass my hand along 
        ArrayList<ArrayList<Card>> playerHandsCopy = new ArrayList<>();

        for (ArrayList<Card> copyHand : parentNode.curPlayerHands) {
            playerHandsCopy.add(new ArrayList<>(copyHand));
        }

        if (parentNode.playerIndex == myPNumber) { // if I just went 
            myCurHand.remove(childIndex); // then update my hand 
        }

        // update player's hand based on who just went 
        playerHandsCopy.get(parentNode.playerIndex).remove(childIndex); 

        ArrayList<Card> childHand;
        int playerIndex = nextPlayer;
        childHand = new ArrayList<>(playerHandsCopy.get(playerIndex)); // ??? needs to be a deep copy ???

        int handIndex = childIndex;

		// Create a new child node that corresponds to the card we have just successfully played
		parentNode.children.add(new Node(childState, childHand, myCurHand, playerHandsCopy, parentNode, handIndex));
    }

    private ArrayList<Card> generateHand(State state, Node node) {
        ArrayList<Card> cardsLeft = new ArrayList<>(state.cardsPlayed.invertDeck);
        Collections.shuffle(cardsLeft);
        ArrayList<Card> genHand = new ArrayList<>();

        // currentRound.size() = number player this round (ex- 2 cards have been put down, this is the third player of the round)
        // (# cards played this round) + (index of first player to go - say it's player 1) % 4 = index of current player 
        // for (int i = 0; i < state.currentRound.size(); i++) {
        //     int playerIndex = i + firstPlayer % 4;
        // }

        // remove all the cards that we know are in My hand
        for (Card myCard : node.myCurHand) {
            for (int i = 0; i < cardsLeft.size(); i++) {
                Card cardLeft = cardsLeft.get(i);
                if (cardLeft.equals(myCard)) { cardsLeft.remove(i); }
            }
        }

        // everyone has the same amount of cards at the beginning of a round - so I should have the same as my parent
        int handSize = node.curHand.size(); 
        // unless a new round just started, in which case everyone has put a card down, and so I have one less card than my parent b/c I am first
        if (state.firstInRound()) {handSize--;} 

        // right now this is random, but later we will change this to update based on player tables 
        for (int i = 0; i < handSize; i++) { 
            genHand.add(cardsLeft.remove(i)); 
        }

        return genHand;
    }

    // pick a random node to simulate
    private ArrayList<Integer> simulateRandomPlayout(Node node) {
        // "global" variables that we want to alter throughout the simulation 
        State tempState = new State(node.state); // state of the game
        ArrayList<ArrayList<Card>> simulatedHands = new ArrayList<>(); // hands that will be altered through simulating
        for (ArrayList<Card> copyPlayerHand : node.curPlayerHands) {
            simulatedHands.add(new ArrayList<>(copyPlayerHand));
        }

        int curPlayer = node.playerIndex; // to start off
        while (!tempState.cardsPlayed.invertDeck.isEmpty()) {
            ArrayList<Card> simulatedHand = simulatedHands.get(curPlayer);

            // given the cards in the each player's hand, the cards we can draw from, determine the valid range of cards that can be played
            int[] indexRange = getValidRange(tempState, simulatedHand);
            int firstIndex = indexRange[0];
            int lastIndex = indexRange[1];

            // randomly select a card from the valid range of the "pile"
            int cardNum;

            if (firstIndex == lastIndex) { cardNum = firstIndex; }
            else { cardNum = firstIndex + rand.nextInt(lastIndex - firstIndex); } // ToDo: Change so that it isn't random (optimization)
            Card cardToPlay = simulatedHand.get(cardNum);

            curPlayer = tempState.advanceState(cardToPlay, simulatedHand, debug);
            simulatedHand.remove(cardToPlay);
        }

        return tempState.playerScores;
    }

    // simulate out the rest of the game, assuming each player uses highLow strategy
    private ArrayList<Integer> simulateHighLowPlayout(Node node) {
        // "global" variables that we want to alter throughout the simulation 
        State tempState = new State(node.state); // state of the game
        ArrayList<ArrayList<Card>> simulatedHands = new ArrayList<>(); // hands that will be altered through simulating
        for (ArrayList<Card> copyPlayerHand : node.curPlayerHands) {
            simulatedHands.add(new ArrayList<>(copyPlayerHand));
        }

        PrintStream originalOut = System.out;

        // Redirect output to null to suppress print statements
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {}
        }));
        
        int curPlayer = node.playerIndex; // to start off
        while (!tempState.cardsPlayed.invertDeck.isEmpty()) {
            ArrayList<Card> simulatedHand = simulatedHands.get(curPlayer);
            
            Player highLow = new HighLowPlayAI("HighLowPlayer");
            highLow.hand = simulatedHand;

            Card cardToPlay = highLow.performAction(tempState, null); 

            curPlayer = tempState.advanceState(cardToPlay, simulatedHand, debug);
            // simulatedHand.remove(cardToPlay);
        }

        System.setOut(originalOut);

        return tempState.playerScores;
    }

    private void backPropogate (Node baseNode, ArrayList<Integer> scores) {
		Node no = baseNode;
		while (no.parent != null) {
            int playerIndex = no.parent.playerIndex; 
            int playerScore = scores.get(playerIndex); // this player's score this game
            int playerWinScore = 26 - playerScore; // do (26 - score) so that more points are good rather thana bad
			no.winScore += playerWinScore;
            
            no.visitCount++;
			no = no.parent;
		}
	}

    // Pick the child of the root with the highest reward. Returns an 
    // array of length 2, in which index 0 holds the best child's index,
    // and index 1 holds the best child's winning score
	private int bestRewardChild(Node root) {
		double bestWinScore = -Double.MAX_VALUE;
        int bestChildIndex = 0; 

        for (int i = 0; i < root.children.size(); i++) {
            Node curNode = root.children.get(i);
            if (curNode.visitCount == 0) { continue; }

            double totalWinScore = (double) curNode.winScore / curNode.visitCount;
            if (totalWinScore > bestWinScore) {
                bestWinScore = totalWinScore;
                bestChildIndex = i;
            }
        }

        return root.children.get(bestChildIndex).handIndex; // handIndex is not necessarily = bestChildIndex
	}

    @Override
    Card performAction (State masterCopy, ArrayList<Player> playerOrder) {
		// If very first move, play the two of clubs (will be first card in hand)
		if (masterCopy.firstMove()) {
			return hand.remove(0);
        }

        // populate a copy of my card hand 
		myHand.clear();
		for (Card c : hand) myHand.add(c.copy());
		// For human debugging: print the hand
		printHand();

		// If very last move, you must play that card
		if (hand.size() == 1)
			return hand.remove(0);

		// Actually play the card, after doing MCTS
		return hand.remove(runMCTS(masterCopy, playerOrder));
	}
}

