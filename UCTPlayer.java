import java.util.*;

import org.w3c.dom.Node;

public class UCTPlayer extends Player {

    ArrayList<Card> playoutHand;

	Random 			rng;
	final int 		numIterations = 26; 		// How many times we go through MCTS before making a decision
	final int 		maxDepth = 3; 	// How many nodes to expand to before doing random playouts
	Node 			root;

    public class Node {
		State 			state;
		ArrayList<Card> curHand;
        int             playerIndex;
		int 			winCount;			
		int 			visitCount;
		Node 			parent;
		Node[] 			children; // !!! change so this is an arrayList of only valid children, not an array 
		int 			depth;

		Node (State s, ArrayList<Card> hand, Node p) {
			state = s;
			curHand = hand;
            playerIndex = state.playerIndex;
			winCount = 0;
			visitCount = 0;
			parent = p;
			children = new Node[hand.size()]; 		// largest amount of children is # of cards in the hand
            if (p != null) depth = p.depth + 1;
			else depth = 0;
		}
	}

    UCTPlayer(String name) { 
		super(name); 
		System.out.println("UCTPlayer AI ("+name+") initialized."); 

		playoutHand = new ArrayList<Card>(hand);	// Need to call this every performAction()
		rng = new Random();
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

    private ArrayList<Card> generateHand(State state, Node node, int handSize) {
        ArrayList<Card> cardsLeft = new ArrayList<Card> (state.cardsPlayed.invertDeck);
        Collections.shuffle(cardsLeft);
        ArrayList<Card> hand = new ArrayList<Card>();

        // currentRound.size() = number player this round (ex- 2 cards have been put down, this is the third player of the round)
        // (# cards played this round) + (index of first player to go - say it's player 1) % 4 = index of current player 
        // for (int i = 0; i < state.currentRound.size(); i++) {
        //     int playerIndex = i + firstPlayer % 4;
        // }
        System.out.println("Hand Size: " + handSize);
        System.out.println("Current Player Number: " + state.playerIndex);
        System.out.println("Current Tree Depth: " + node.depth);
        System.out.println("Current Node Number: " + node.playerIndex);

        // right now this is random, but later we will change this to update based on player tables 
        for (int i = 0; i < handSize; i++) { 
            hand.add(cardsLeft.remove(i)); 
        }

        return hand;
    }

    int runMCTS (State originalState) {
        root = new Node(originalState, playoutHand, null);
        // generateHands(originalState, root);

        // run multiple games until we've hit the max number
        for (int i = 0; i < numIterations; i++) {

            // Select which Node to expand
            Node bestNode = selectBestNode(root); 

            // Expand that Node
            expandNode(bestNode); 

            // simulation 
            Node nodeToExplore = bestNode;
            // if (bestNode.getChildArray().size() > 0) { // corner case - ???
            //     nodeToExplore = bestNode.getRandomChildNode(); // select which random child to simulate (attach a winScore to)
            // }
            int valueChange = simulateRandomPlayout(nodeToExplore); // Simulate

            // backpropogation
            backPropogation(nodeToExplore, valueChange); 
        }

        return bestRewardChild(root);
    }

    // Select which node to expand next using UCT
	Node selectBestNode(Node rootNode) {
		Node curNode = rootNode;

		// Go through this node
		while (curNode.state.isGameValid() && maxDepth > curNode.depth) {
            // if we're not at a leaf node 
            if (curNode.children.length > 0) {
                // choose which Node is the best of the valid child nodes
                curNode = bestUCTChild(curNode);
            }
            // can no longer apply UCT to find a successor node (at a leaf node)
            else {
                return curNode;
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
        int bestIndex = 0; // ??? good to start with child 0?

        for (int i = 0; i < node.children.length; i++) {
            Node curChild = node.children[i];
            if (curChild != null) {
                double uctVal = uctValue(node.visitCount, curChild.winCount, curChild.visitCount);
                if (uctVal > bestValue) {
					bestValue = uctVal;
					bestIndex = i;
				}
            }
        }

        return node.children[bestIndex];
    }

    // Expands the game tree by appending all possible states from curNode (the leaf node)
    private void expandNode(Node curNode) {
        // Find out which children are valid 
        int[] indexRange = getValidRange(curNode);
        int firstIndex = indexRange[1];
        int lastIndex = indexRange[2];

        // Create a new Node representing each valid child 
        for (int i = firstIndex; i < lastIndex; i++) {
            // Notice: This will leave some children to be null, since they are invalid
            addNewChild(curNode, i);
        }
    }

    // Returns the range of valid "children" cards that curNode can play this round
    private int[] getValidRange(Node curNode) {
        // Get the first suit that was played this round
       Suit firstSuit = getFirstSuit(curNode.state.currentRound);

       SuitRange range = getSuitRange(firstSuit, curNode.curHand);
       // If we have firstSuit, get the range of cards we can play rn
       int firstIndex = range.startIndex;
       int lastIndex = range.endIndex;

       // If this is the first move in the round
       if (firstSuit == null) {
           // If hearts has broken or we only have hearts left, we can play any card in our hand
           if (curNode.state.hasHeartsBroken || hasAllHearts(curNode.curHand)) {
               firstIndex = 0;
               lastIndex = curNode.curHand.size();
           } else {
               // If hearts has not broken, and we have at least one non-hearts card we can play
               SuitRange heartsRange = getSuitRange(Suit.HEARTS, curNode.curHand);
               // If we have no hearts, we can play anything
               if (heartsRange.startIndex == -1) {
                   firstIndex = 0;
                   lastIndex = curNode.curHand.size();
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
           lastIndex = curNode.curHand.size();
       }

       int[] indexRange = new int[2];
       indexRange[1] = firstIndex;
       indexRange[2] = lastIndex;
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
		ArrayList<Card> childHand = new ArrayList<Card>(parentNode.curHand);

		// Remove the card from the hand of the child
		int debug = childState.advance( childHand.remove(childIndex), childHand ); // !!! change this - tree should include 4 players

		// Create a new child node that corresponds to the card we have just successfully played
		parentNode.children[childIndex] = new Node(childState, childHand, parentNode);
    }


}

