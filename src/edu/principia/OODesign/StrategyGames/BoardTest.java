package edu.principia.OODesign.StrategyGames;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import edu.principia.OODesign.StrategyGames.Board.InvalidMoveException;
public class BoardTest {
   private static Board board;
   private static Board.Move move;
   private static Scanner scanner;

   public static void main(String[] args) throws ClassNotFoundException, InstantiationException,
           IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
           SecurityException, FileNotFoundException, IOException, InvalidMoveException, ClassNotFoundException {
       if (args.length != 1) {
           System.err.println("Usage: java BoardTest <BoardClassName>");
           System.exit(1);
       }
      // String boardClassName = "edu.principia.MBrad.OODesign.StrategyGames." + args[0];
       String boardClassName = "edu.principia.MBrad.OODesign.StrategyGames."+ args[0];

       try {
           Class<?> boardClass = Class.forName(boardClassName);
           board = (Board) boardClass.getDeclaredConstructor().newInstance();
           move = board.createMove();
           scanner = new Scanner(System.in);

           while (scanner.hasNext()) {
               String command = scanner.next().toLowerCase();
               switch (command) {
                   case "showboard":
                       showBoard();
                       break;
                   case "showmoves":
                       showMoves();
                       break;
                   case "entermove":
                       enterMove();
                       break;
                   case "showmove":
                       showMove();
                       break;
                   case "applymove":
                       applyMove();
                       break;
                   case "domove":
                       doMove();
                       break;
                   case "undomoves":
                       undoMoves();
                       break;
                   case "showval":
                       showVal();
                       break;
                   case "showmovehist":
                       showMoveHist();
                       System.out.println();
                       break;
                   case "saveboard":
                       saveBoard();
                       break;
                   case "loadboard":
                       loadBoard(boardClass);
                       break;
                   case "comparemove":
                       compareMove();
                       break;
                   case "testrun":
                       testRun();
                       break;
                   case "testplay":
                       testPlay();
                       break;
                   case "aiplay":
                       aiPlay();
                       break;
                   case "airun":
                       aiRun();
                       break;
                   case "showplayer":
                       showplayer();
                       break;
                   case "quit":
                       return;
                   default:
                       System.out.println("Unknown command");
                       break;
               }
           }
           scanner.close();
       } catch (ClassNotFoundException e) {
           System.err.println("Class not found: " + boardClassName);
           System.exit(1);
       } catch (InstantiationException e) {
           System.err.println("Class not instantiable: " + boardClassName);
           System.exit(1);
       } catch (IllegalAccessException e) {
           System.err.println("Class not accessible: " + boardClassName);
           System.exit(1);
       } catch (java.io.IOException e) {
           System.err.println("I/O error: " + e.getMessage());
           System.exit(1);
       }
   }

   private static void showplayer() {
       System.out.println(board.getCurrentPlayer());
   }

   private static void showBoard() {
       System.out.println(board);
   }

   private static void showMoves() {
       List<? extends Board.Move> moves = board.getValidMoves();
       int maxMoveSize = moves.stream()
               .mapToInt(m -> m.toString().length())
               .max()
               .orElse(0);
       int cols = 80 / (maxMoveSize + 1);
       int col = 0;
       for (Board.Move m : moves) {
           System.out.printf("%-" + maxMoveSize + "s ", m.toString());
           if (++col == cols) {
               System.out.println();
               col = 0;
           }
       }
       if (col != 0) {
           System.out.println();
       }
   }

   private static void enterMove() throws IOException {
       // scanner.nextLine(); // Skip the newline
       if (scanner.hasNextLine()) {
           String moveString = scanner.nextLine().trim(); // Read the entire move string
           move.fromString(moveString);
       }
   }

   private static void showMove() {
       System.out.println(move);
   }

   private static void applyMove() {
       try {
           board.applyMove(move);
       } catch (Board.InvalidMoveException e) {
           System.out.println("Invalid move: " + e.getMessage());
       } catch (Exception e) {
           System.out.println("An error occurred while applying the move: " + e.getMessage());
       }
   }

   private static void doMove() throws IOException {
       if (scanner.hasNext()) {
           String moveString = scanner.nextLine().trim(); // Read the entire move string
           move.fromString(moveString);
           try {
               board.applyMove(move);
           } catch (InvalidMoveException e) {
               System.out.println("Invalid move: " + e.getMessage());
           }
       }
   }

   private static void undoMoves() {
       int count = scanner.nextInt();
       for (int i = 0; i < count; i++) {
           board.undoMove();
       }
   }

   private static void showVal() {
       System.out.println(board.getValue());
   }

   private static void showMoveHist() {
       List<? extends Board.Move> moveHistory = board.getMoveHistory();
       int cols = 2; // Two columns
       int col = 0; // Counter for columns in output
       for (Board.Move m : moveHistory) {
           String moveStr = m.toString();
           System.out.printf("%-40s", moveStr); // Left-justify in a field of size 40
           if (++col == cols) {
               System.out.println();
               col = 0;
           }
       }
       if (col != 0) {
           System.out.println();
       }
   }

   private static void saveBoard() {
       if (scanner.hasNext()) {
           String fileName = scanner.next();
           try (FileOutputStream fileOut = new FileOutputStream(fileName);
                   ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
               out.writeObject(board.getMoveHistory());
           } catch (IOException e) {
               System.out.println("An error occurred while saving the board: " + e.getMessage());
               e.printStackTrace();
           }
       }
   }

   private static void loadBoard(Class<?> boardClass) throws InstantiationException, IllegalAccessException,
           IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException,
           ClassNotFoundException, IOException, InvalidMoveException {
       if (scanner.hasNext()) {
           String fileName = scanner.next();
           try (FileInputStream fileIn = new FileInputStream(fileName);
                   ObjectInputStream in = new ObjectInputStream(fileIn)) {
               board = (Board) boardClass.getDeclaredConstructor().newInstance();

               @SuppressWarnings("unchecked")
               List<? extends Board.Move> moveHistory = (List<? extends Board.Move>) in.readObject();
               for (Board.Move moveItem : moveHistory) {
                   board.applyMove(moveItem);
               }
           } catch (IOException | ClassNotFoundException | InvalidMoveException e) {
               System.out.println("An error occurred while loading the board: " + e.getMessage());
           }
       }
   }

   private static void compareMove() throws IOException, InvalidMoveException {
       if (move == null) {
           System.out.println("No current move has been set. Use 'enterMove' to set a move first.");
           return;
       }

       String moveString = scanner.nextLine().trim();
       Board.Move other = board.createMove();
       other.fromString(moveString);

       int cmp = move.compareTo(other);
       if (cmp < 0) {
           System.out.println("Current move is less");
       } else if (cmp > 0) {
           System.out.println("Current move is greater");
       } else {
           System.out.println("Current move is equal");
       }
   }

   private static void testRun() {
       if (scanner.hasNextInt()) {
           int seed = scanner.nextInt(); // Reads the next integer as seed
           int stepCount = scanner.nextInt(); // Reads the next integer as stepCount
           Random rnd = new Random(seed);
           for (int i = 0; i < stepCount; i++) {
               List<? extends Board.Move> moves = board.getValidMoves();
               if (moves.isEmpty()) {
                   int moveNum = rnd.nextInt(board.getMoveHistory().size()) + 1;
                   for (int j = 0; j < moveNum; j++) {
                       board.undoMove();
                   }
               } else {
                   int moveNum = rnd.nextInt(moves.size());
                   move = moves.get(moveNum);
                   try {
                       board.applyMove(move);
                   } catch (Board.InvalidMoveException e) {
                       System.out.println("Encountered an invalid move during testRun.");
                   }
               }
           }
       }
   }

   private static void testPlay() {
       int seed = scanner.nextInt();
       int moveCount = scanner.nextInt();
       Random rnd = new Random(seed);
       for (int i = 0; i < moveCount; i++) {
           List<? extends Board.Move> moves = board.getValidMoves();
           int n = rnd.nextInt(moves.size());
           try {
               board.applyMove(moves.get(n));
           } catch (Board.InvalidMoveException e) {
               System.err.println("Invalid move: " + e.getMessage());
           }
           if (board.getValidMoves().isEmpty()) {
               break;
           }
       }
   }

   private static void aiPlay() throws InvalidMoveException {
       int level = scanner.nextInt();
       int maxMoves = scanner.nextInt();
       AISolver.MMResult result = new AISolver.MMResult();
       for (int i = 0; i < maxMoves; i++) {
           AISolver.miniMax(board, Integer.MIN_VALUE, Integer.MAX_VALUE, level, result);
           if (result.move == null) {
               break;
           }
           board.applyMove(result.move);
           // print in this format Move 1: 2,6 MM value 3 Board value 1
           System.out.println("Move " + (i + 1) + ": " + result.move + " MM value " + result.value + " Board value "
                   + board.getValue());

           System.out.println(board);
       }
   }

   private static void aiRun() throws InvalidMoveException {
       int level = scanner.nextInt();
       int maxMoves = scanner.nextInt();
       AISolver.MMResult result = new AISolver.MMResult();
       for (int i = 0; i < maxMoves; i++) {
           AISolver.miniMax(board, Integer.MIN_VALUE, Integer.MAX_VALUE, level, result);
           if (result.move == null) {
               break;
           }
           System.out.println(board);
       }
   }
}