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
		int 			winCount;			
		int 			visitCount;
		Node 			parent;
		Node[] 			children;
		int 			depth;

		Node (State s, ArrayList<Card> hand, Node p) {
			state = s;
			curHand = hand;
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

    int runMCTS (State originalState) {
        // run multiple games until we've hit the max number
        for (int i = 0; i < numIterations; i++) {
            root = new Node(originalState, playoutHand, null);

            // Select which Node to expand
            Node bestNode = selectBestNode(root); 

            // Expand that Node
            expandNode(bestNode); 

            // simulation 
            Node nodeToExplore = bestNode;
            if (bestNode.getChildArray().size() > 0) { // corner case - ???
                nodeToExplore = bestNode.getRandomChildNode(); // select which random child to simulate (attach a winScore to)
            }
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

            // Find out which children are valid 
            int[] indexRange = getValidRange(curNode);
            int firstIndex = indexRange[1];
            int lastIndex = indexRange[2];

			// choose which Node is the best of the valid child nodes
			thisNode = bestChild(thisNode, 0.1);
		}
		return thisNode;
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

    public class UCT {
        public static double uctValue(
          int totalVisitCount, double nodeWinCount, int nodeVisitCount) {
            if (nodeVisitCount == 0) {
                return Integer.MAX_VALUE;
            }
            return ((double) nodeWinCount / (double) nodeVisitCount) 
              + 1.41 * Math.sqrt(Math.log(totalVisitCount) / (double) nodeVisitCount);
        }
    
        public static Node bestNodeWithUCT(Node node) {
            // ??? maybe just use MCTS player ???
            int parentVisitCount = node.visitCount + 1;
            // return the child with the max uctValue
            return Collections.max(
              node.children,
              Comparator.comparing(c -> uctValue(parentVisitCount, 
                c.winCount, c.visitCount)));
        }
    }


    private void expandNode(Node curNode, int firstIndex, int lastIndex) {
        // Add new Nodes for each valid child 
        for (int i = firstIndex; i < lastIndex; i++) {
            // Notice: This will leave some children to be null, since they are invalid
            if (curNode.children[i] == null) {
                addNewChild(curNode, i);
            }
        }
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

