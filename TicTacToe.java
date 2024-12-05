/* This file was taken from another GitHub repo that implemented a basic
MCTS for TicTacToe. Linked here: https://github.com/eugenp/tutorials/tree/master/algorithms-modules/algorithms-searching/src/main/java/com/baeldung/algorithms/mcts */

// import java.util.*;

// import javax.swing.text.Position;

// class TicTacToe extends Player {

//     public class Node {
//         State state;
//         Node parent;
//         List<Node> childArray;

//         public Node() {
//             this.state = new State();
//             childArray = new ArrayList<>();
//         }

//         public Node(State state) {
//             this.state = state;
//             childArray = new ArrayList<>();
//         }

//         public Node(State state, Node parent, List<Node> childArray) {
//             this.state = state;
//             this.parent = parent;
//             this.childArray = childArray;
//         }

//         public Node(Node node) {
//             this.childArray = new ArrayList<>();
//             this.state = new State(node.getState());
//             if (node.getParent() != null)
//                 this.parent = node.getParent();
//             List<Node> childArray = node.getChildArray();
//             for (Node child : childArray) {
//                 this.childArray.add(new Node(child));
//             }
//         }

//         public State getState() {
//             return state;
//         }

//         public void setState(State state) {
//             this.state = state;
//         }

//         public Node getParent() {
//             return parent;
//         }

//         public void setParent(Node parent) {
//             this.parent = parent;
//         }

//         public List<Node> getChildArray() {
//             return childArray;
//         }

//         public void setChildArray(List<Node> childArray) {
//             this.childArray = childArray;
//         }

//         public Node getRandomChildNode() {
//             int noOfPossibleMoves = this.childArray.size();
//             int selectRandom = (int) (Math.random() * noOfPossibleMoves);
//             return this.childArray.get(selectRandom);
//         }

//         public Node getChildWithMaxScore() {
//             return Collections.max(this.childArray, Comparator.comparing(c -> {
//                 return c.getState().getVisitCount();
//             }));
//         }
//     }

//     public class Tree {
//         Node root;

//         public Tree() {
//             root = new Node();
//         }

//         public Tree(Node root) {
//             this.root = root;
//         }

//         public Node getRoot() {
//             return root;
//         }

//         public void setRoot(Node root) {
//             this.root = root;
//         }

//         public void addChild(Node parent, Node child) {
//             parent.getChildArray().add(child);
//         }
//     }

//     public class State {
//         private Board board;
//         private int playerNo;
//         private int visitCount;
//         private double winScore;

//         public State() {
//             board = new Board();
//         }

//         public State(State state) {
//             this.board = new Board(state.getBoard());
//             this.playerNo = state.getPlayerNo();
//             this.visitCount = state.getVisitCount();
//             this.winScore = state.getWinScore();
//         }

//         public State(Board board) {
//             this.board = new Board(board);
//         }

//         Board getBoard() {
//             return board;
//         }

//         void setBoard(Board board) {
//             this.board = board;
//         }

//         int getPlayerNo() {
//             return playerNo;
//         }

//         void setPlayerNo(int playerNo) {
//             this.playerNo = playerNo;
//         }

//         int getOpponent() {
//             return 3 - playerNo;
//         }

//         public int getVisitCount() {
//             return visitCount;
//         }

//         public void setVisitCount(int visitCount) {
//             this.visitCount = visitCount;
//         }

//         double getWinScore() {
//             return winScore;
//         }

//         void setWinScore(double winScore) {
//             this.winScore = winScore;
//         }

//         public List<State> getAllPossibleStates() {
//             List<State> possibleStates = new ArrayList<>();
//             List<Position> availablePositions = this.board.getEmptyPositions();
//             availablePositions.forEach(p -> {
//                 State newState = new State(this.board);
//                 newState.setPlayerNo(3 - this.playerNo);
//                 newState.getBoard().performMove(newState.getPlayerNo(), p);
//                 possibleStates.add(newState);
//             });
//             return possibleStates;
//         }

//         void incrementVisit() {
//             this.visitCount++;
//         }

//         void addScore(double score) {
//             if (this.winScore != Integer.MIN_VALUE)
//                 this.winScore += score;
//         }

//         void randomPlay() {
//             List<Position> availablePositions = this.board.getEmptyPositions();
//             int totalPossibilities = availablePositions.size();
//             int selectRandom = (int) (Math.random() * totalPossibilities);
//             this.board.performMove(this.playerNo, availablePositions.get(selectRandom));
//         }

//         void togglePlayer() {
//             this.playerNo = 3 - this.playerNo;
//         }
//     }

//     public class MonteCarloTreeSearch {
//         private static final int WIN_SCORE = 10;
//         private int level;
//         private int opponent;

//         public MonteCarloTreeSearch() {
//             this.level = 3;
//         }

//         public int getLevel() {
//             return level;
//         }

//         public void setLevel(int level) {
//             this.level = level;
//         }

//         private int getMillisForCurrentLevel() {
//             return 2 * (this.level - 1) + 1;
//         }
    
//         public Board findNextMove(Board board, int playerNo) {
//             // define an end time which will act as a terminating condition
//             long start = System.currentTimeMillis();
//             long end = start + 60 * getMillisForCurrentLevel();
    
//             opponent = 3 - playerNo;
//             Tree tree = new Tree();
//             Node rootNode = tree.getRoot();
//             rootNode.getState().setBoard(board);
//             rootNode.getState().setPlayerNo(opponent);
    
//             // run multiple games until time runs out 
//             while (System.currentTimeMillis() < end) {
//                 // selection 
//                 Node promisingNode = selectPromisingNode(rootNode);
//                 // expansion 
//                 if (promisingNode.getState().getBoard().checkStatus() 
//                   == Board.IN_PROGRESS) {
//                     expandNode(promisingNode);
//                 }
//                 // simulation 
//                 Node nodeToExplore = promisingNode;
//                 if (promisingNode.getChildArray().size() > 0) {
//                     nodeToExplore = promisingNode.getRandomChildNode();
//                 }
//                 int playoutResult = simulateRandomPlayout(nodeToExplore);
//                 // backpropogation
//                 backPropogation(nodeToExplore, playoutResult);
//             }
    
//             Node winnerNode = rootNode.getChildWithMaxScore();
//             tree.setRoot(winnerNode);
//             return winnerNode.getState().getBoard();
//         }

//         // selection phase implementation 
//         private Node selectPromisingNode(Node rootNode) {
//             Node node = rootNode;
//             while (node.getChildArray().size() != 0) {
//                 node = UCT.findBestNodeWithUCT(node);
//             }
//             return node;
//         }

//         // expansion phase implementation - selects a leaf node
//         private void expandNode(Node node) {
//             List<State> possibleStates = node.getState().getAllPossibleStates();
//             possibleStates.forEach(state -> {
//                 Node newNode = new Node(state);
//                 newNode.setParent(node);
//                 newNode.getState().setPlayerNo(node.getState().getOpponent());
//                 node.getChildArray().add(newNode);
//             });
//         }

//         // backpropogation phase
//         private void backPropogation(Node nodeToExplore, int playerNo) {
//             Node tempNode = nodeToExplore;
//             while (tempNode != null) {
//                 tempNode.getState().incrementVisit();
//                 if (tempNode.getState().getPlayerNo() == playerNo) {
//                     tempNode.getState().addScore(WIN_SCORE);
//                 }
//                 tempNode = tempNode.getParent();
//             }
//         }

//         // pick a random node to simulate
//         private int simulateRandomPlayout(Node node) {
//             Node tempNode = new Node(node);
//             State tempState = tempNode.getState();
//             int boardStatus = tempState.getBoard().checkStatus();
//             if (boardStatus == opponent) {
//                 tempNode.getParent().getState().setWinScore(Integer.MIN_VALUE);
//                 return boardStatus;
//             }
//             while (boardStatus == Board.IN_PROGRESS) {
//                 tempState.togglePlayer();
//                 tempState.randomPlay();
//                 boardStatus = tempState.getBoard().checkStatus();
//             }
//             return boardStatus;
//         }
//     }

    
//     public class UCT {
//         public static double uctValue(
//           int totalVisit, double nodeWinScore, int nodeVisit) {
//             if (nodeVisit == 0) {
//                 return Integer.MAX_VALUE;
//             }
//             return ((double) nodeWinScore / (double) nodeVisit) 
//               + 1.41 * Math.sqrt(Math.log(totalVisit) / (double) nodeVisit);
//         }
    
//         public static Node findBestNodeWithUCT(Node node) {
//             int parentVisit = node.getState().getVisitCount();
//             // return the child with the max uctValue
//             return Collections.max(
//               node.getChildArray(),
//               Comparator.comparing(c -> uctValue(parentVisit, 
//                 c.getState().getWinScore(), c.getState().getVisitCount())));
//         }
//     }

//     // for ticTacToe specifically 
//     public class Board {
//         int[][] boardValues;
//         public static final int DEFAULT_BOARD_SIZE = 3;
//         public static final int IN_PROGRESS = -1;
//         public static final int DRAW = 0;
//         public static final int P1 = 1;
//         public static final int P2 = 2;
        
//         // getters and setters
//         public void performMove(int player, Position p) {
//             this.totalMoves++;
//             boardValues[p.getX()][p.getY()] = player;
//         }

//         public int checkStatus() {
//             /* Evaluate whether the game is won and return winner.
//             If it is draw return 0 else return -1 */         
//         }

//         public List<Position> getEmptyPositions() {
//             int size = this.boardValues.length;
//             List<Position> emptyPositions = new ArrayList<>();
//             for (int i = 0; i < size; i++) {
//                 for (int j = 0; j < size; j++) {
//                     if (boardValues[i][j] == 0)
//                         emptyPositions.add(new Position(i, j));
//                 }
//             }
//             return emptyPositions;
//         }
//     }
// }
