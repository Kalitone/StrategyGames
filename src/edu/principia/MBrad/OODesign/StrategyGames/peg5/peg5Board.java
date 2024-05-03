package edu.principia.MBrad.OODesign.StrategyGames.peg5;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.principia.OODesign.StrategyGames.Board;

public class peg5Board implements Board {
   public static final int BOARD_SIZE = 7;
   public static final int PLAYER_0 = 1;
   public static final int PLAYER_1 = -1;

   private peg5Cell[][] board;
   private int player;
   public int[] greenUnplayedPieces;
   public int[] yellowUnplayedPieces;
   public int moveCount;
   private List<peg5Move> moveHistory;

   // Constructor
   public peg5Board() {
      board = new peg5Cell[BOARD_SIZE][BOARD_SIZE];
      for (int row = 0; row < BOARD_SIZE; row++) {
         for (int col = 0; col < BOARD_SIZE; col++) {
            board[row][col] = new peg5Cell();
         }
      }
      player = PLAYER_0;
      greenUnplayedPieces = new int[] { 10, 4, 4 };
      yellowUnplayedPieces = new int[] { 10, 4, 4 };
      moveCount = 0;
      moveHistory = new ArrayList<>();
   }

   private boolean canPlacePeg(int row, int col, int player) {
      return board[row][col].canPlacePeg(player);
   }

   private boolean canPlaceTube(int row, int col, int player) {
      return board[row][col].canPlaceTube(player);
   }

   // Factory method to create a new move
   @Override
   public Board.Move createMove() {
      return new peg5Move();
   }

   // Apply a move to the board, throwing an exception if the move is invalid
   @Override
   public void applyMove(Board.Move move) throws InvalidMoveException {
      peg5Move peg5Move = (peg5Move) move;
      // System.out.println("DEBUG: Applying move: " + peg5Move);

      int row = peg5Move.getRow();
      int col = peg5Move.getCol();
      peg5Cell cell = board[row][col];

      if (peg5Move.getPieceType() == peg5Move.PieceType.PEG) {
         if (player == PLAYER_0) {
            if (!canPlacePeg(row, col, PLAYER_0)) {
               throw new InvalidMoveException("Invalid move: cannot place peg green peg at (" + row + ", " + col + ")");
            }
            cell.setGreenPeg();
            greenUnplayedPieces[0]--;
         } else {
            if (!canPlacePeg(row, col, PLAYER_1)) {
               throw new InvalidMoveException(
                     "Invalid move: cannot place peg yellow peg at (" + row + ", " + col + ")");
            }
            cell.setYellowPeg();
            yellowUnplayedPieces[0]--;
         }
      } else if (peg5Move.getPieceType() == peg5Move.PieceType.OPEN_TUBE) {
         if (player == PLAYER_0) {
            if (!canPlaceTube(row, col, PLAYER_0)) {
               throw new InvalidMoveException(
                     "Invalid move: cannot place green open tube at (" + row + ", " + col + ")");
            }
            cell.setGreenOpenTube();
            greenUnplayedPieces[1]--;
         } else {
            if (!canPlaceTube(row, col, PLAYER_1)) {
               throw new InvalidMoveException(
                     "Invalid move: cannot place yellow open tube at (" + row + ", " + col + ")");
            }
            cell.setYellowOpenTube();
            yellowUnplayedPieces[1]--;
         }
      } else if (peg5Move.getPieceType() == peg5Move.PieceType.CLOSED_TUBE) {
         if (player == PLAYER_0) {
            if (!canPlaceTube(row, col, PLAYER_0)) {
               throw new InvalidMoveException(
                     "Invalid move: cannot place green closed tube at (" + row + ", " + col + ")");
            }
            cell.setGreenClosedTube();
            greenUnplayedPieces[2]--;
         } else {
            if (!canPlaceTube(row, col, PLAYER_1)) {
               throw new InvalidMoveException(
                     "Invalid move: cannot place yellow closed tube at (" + row + ", " + col + ")");
            }
            cell.setYellowClosedTube();
            yellowUnplayedPieces[2]--;
         }
      }

      if (peg5Move.isRepositioningMove()) {
         int sourceRow = peg5Move.getSourceRow();
         int sourceCol = peg5Move.getSourceCol();
         peg5Cell sourceCell = board[sourceRow][sourceCol];

         if (player == PLAYER_0) {
            if (peg5Move.getPieceType() == peg5Move.PieceType.PEG) {
               sourceCell.removeGreenPeg();
            } else if (peg5Move.getPieceType() == peg5Move.PieceType.OPEN_TUBE) {
               sourceCell.removeGreenOpenTube();
            } else if (peg5Move.getPieceType() == peg5Move.PieceType.CLOSED_TUBE) {
               sourceCell.removeGreenClosedTube();
            }
         } else {
            if (peg5Move.getPieceType() == peg5Move.PieceType.PEG) {
               sourceCell.removeYellowPeg();
            } else if (peg5Move.getPieceType() == peg5Move.PieceType.OPEN_TUBE) {
               sourceCell.removeYellowOpenTube();
            } else if (peg5Move.getPieceType() == peg5Move.PieceType.CLOSED_TUBE) {
               sourceCell.removeYellowClosedTube();
            }
         }
      }

      moveHistory.add(peg5Move.clone());
      player = -player;
      moveCount++;
   }

   // getvalue
   // Since game play may extend indefinitely, the game is a draw after 100
   // half-moves (50 piece placements or movements by each player
   // Board valuation is WIN (-WIN) for green (yellow) having a 5-cell pattern.
   // Otherwise count the number of 4-cell patterns, where a player is one step
   // away from completing a 5-cell pattern (just a single piece needed, and a
   // winning piece is playable at the cell in question) and also 3-cell patterns
   // (just two missing pieces, with winning pieces playable in their slots). Any
   // set of 5 cells counts toward at most one partial pattern, whatever partial
   // size is largest. But, a given cell may be involved in many 5-cell groups and
   // may be part of a different pattern for each.
   // Multiply the count of 4-cell patterns by 10 and add the count of 3-cell
   // patterns, for each player. Then return the difference between the players'
   // values

   // getvalue should return the value of the board
   // in getvalue the pattern method should take in the specific player and the
   // winning pattern corresponding to the player

   // count3pattern and count4pattern takes in the winning pattern and the player
   // and returns the number of 3 and 4 patterns for the player
   // the method takes in the winning pattern and the player and returns the number
   // of 3 patterns for the player
   // the method takes in the winning pattern and the player and returns the number
   // of 4 patterns for the player
   // based on the number of patterns for a 3 we multiply by 1 and for a 4 we
   // multiply by 10
   // then from that we traverse through the board
   // checking through all 60 possible patterns to see if the player has a winning
   // pattern.
   // there are 21 possible patterns horizonatally, 21 vertically and 18
   // diagonally(diagnonal and anti diagonal).
   // in our check we are going to collect the contents of the cell on the board
   // for example in a horizantal pattern: we pick the five cells in
   // row and check if each piece of the 5 type mathces any of our 3 winning
   // patterns. since we are checking if only three piece mathces the pattern
   // as we might fing a n empty cell to which we have to keep track of the empty
   // cells in the pattern. to know if it is a true 3cell patter
   // example: G . G -g . . . that is a 3 pattern with 2 empty cells which is a
   // valid pattern. also we have to check for verticals like
   // Yg
   // .
   // Y
   // Oy
   // .
   // since we are using bitwise operations we have to check if the bitwise
   // operation of the pattern and the cell contents is not equal to 0 to validate
   // the pattern
   // if the pattern is valid we return 1 else we return 0. we do the same for the
   // 4 pattern but this time we check for 4 matching pieces and 1 empty cell
   // nOTE: There maybe cases where a pattern was found in a three cell and a four
   // cell pattern but we only count it as a 4 cell pattern
   // since it is a higher value than a 3 cell pattern.

   private static final List<byte[]> winningPattGreen = new ArrayList<>(Arrays.asList(
         new byte[] { peg5Cell.GREEN_PEG, peg5Cell.GREEN_PEG,
               (peg5Cell.GREEN_OPEN_TUBE | peg5Cell.GREEN_CLOSED_TUBE), peg5Cell.GREEN_PEG, peg5Cell.GREEN_PEG }, // PPtPP
         new byte[] { peg5Cell.GREEN_PEG, (peg5Cell.GREEN_OPEN_TUBE | peg5Cell.GREEN_CLOSED_TUBE),
               peg5Cell.GREEN_PEG, (peg5Cell.GREEN_OPEN_TUBE | peg5Cell.GREEN_CLOSED_TUBE), peg5Cell.GREEN_PEG }, // PtPtP
         new byte[] { peg5Cell.GREEN_PEG, (peg5Cell.GREEN_OPEN_TUBE | peg5Cell.GREEN_CLOSED_TUBE),
               (peg5Cell.GREEN_OPEN_TUBE | peg5Cell.GREEN_CLOSED_TUBE),
               (peg5Cell.GREEN_OPEN_TUBE | peg5Cell.GREEN_CLOSED_TUBE), peg5Cell.GREEN_PEG } // PtttP
   ));

   private static final List<byte[]> winningPattYellow = new ArrayList<>(Arrays.asList(
         new byte[] { peg5Cell.YELLOW_PEG, peg5Cell.YELLOW_PEG,
               (peg5Cell.YELLOW_OPEN_TUBE | peg5Cell.YELLOW_CLOSED_TUBE), peg5Cell.YELLOW_PEG,
               peg5Cell.YELLOW_PEG }, // PPtPP
         new byte[] { peg5Cell.YELLOW_PEG, (peg5Cell.YELLOW_OPEN_TUBE | peg5Cell.YELLOW_CLOSED_TUBE),
               peg5Cell.YELLOW_PEG, (peg5Cell.YELLOW_OPEN_TUBE | peg5Cell.YELLOW_CLOSED_TUBE),
               peg5Cell.YELLOW_PEG }, // PtPtP
         new byte[] { peg5Cell.YELLOW_PEG, (peg5Cell.YELLOW_OPEN_TUBE | peg5Cell.YELLOW_CLOSED_TUBE),
               (peg5Cell.YELLOW_OPEN_TUBE | peg5Cell.YELLOW_CLOSED_TUBE),
               (peg5Cell.YELLOW_OPEN_TUBE | peg5Cell.YELLOW_CLOSED_TUBE), peg5Cell.YELLOW_PEG } // PtttP
   ));

   private static final int[][] horizantals = {
         { 0, 0, 0, 1, 0, 2, 0, 3, 0, 4 },
         { 0, 1, 0, 2, 0, 3, 0, 4, 0, 5 },
         { 0, 2, 0, 3, 0, 4, 0, 5, 0, 6 },
         { 1, 0, 1, 1, 1, 2, 1, 3, 1, 4 },
         { 1, 1, 1, 2, 1, 3, 1, 4, 1, 5 },
         { 1, 2, 1, 3, 1, 4, 1, 5, 1, 6 },
         { 2, 0, 2, 1, 2, 2, 2, 3, 2, 4 },
         { 2, 1, 2, 2, 2, 3, 2, 4, 2, 5 },
         { 2, 2, 2, 3, 2, 4, 2, 5, 2, 6 },
         { 3, 0, 3, 1, 3, 2, 3, 3, 3, 4 },
         { 3, 1, 3, 2, 3, 3, 3, 4, 3, 5 },
         { 3, 2, 3, 3, 3, 4, 3, 5, 3, 6 },
         { 4, 0, 4, 1, 4, 2, 4, 3, 4, 4 },
         { 4, 1, 4, 2, 4, 3, 4, 4, 4, 5 },
         { 4, 2, 4, 3, 4, 4, 4, 5, 4, 6 },
         { 5, 0, 5, 1, 5, 2, 5, 3, 5, 4 },
         { 5, 1, 5, 2, 5, 3, 5, 4, 5, 5 },
         { 5, 2, 5, 3, 5, 4, 5, 5, 5, 6 },
         { 6, 0, 6, 1, 6, 2, 6, 3, 6, 4 },
         { 6, 1, 6, 2, 6, 3, 6, 4, 6, 5 },
         { 6, 2, 6, 3, 6, 4, 6, 5, 6, 6 }

   };

   private static final int[][] verticals = {

         { 0, 0, 1, 0, 2, 0, 3, 0, 4, 0 },
         { 1, 0, 2, 0, 3, 0, 4, 0, 5, 0 },
         { 2, 0, 3, 0, 4, 0, 5, 0, 6, 0 },
         { 0, 1, 1, 1, 2, 1, 3, 1, 4, 1 },
         { 1, 1, 2, 1, 3, 1, 4, 1, 5, 1 },
         { 2, 1, 3, 1, 4, 1, 5, 1, 6, 1 },
         { 0, 2, 1, 2, 2, 2, 3, 2, 4, 2 },
         { 1, 2, 2, 2, 3, 2, 4, 2, 5, 2 },
         { 2, 2, 3, 2, 4, 2, 5, 2, 6, 2 },
         { 0, 3, 1, 3, 2, 3, 3, 3, 4, 3 },
         { 1, 3, 2, 3, 3, 3, 4, 3, 5, 3 },
         { 2, 3, 3, 3, 4, 3, 5, 3, 6, 3 },
         { 0, 4, 1, 4, 2, 4, 3, 4, 4, 4 },
         { 1, 4, 2, 4, 3, 4, 4, 4, 5, 4 },
         { 2, 4, 3, 4, 4, 4, 5, 4, 6, 4 },
         { 0, 5, 1, 5, 2, 5, 3, 5, 4, 5 },
         { 1, 5, 2, 5, 3, 5, 4, 5, 5, 5 },
         { 2, 5, 3, 5, 4, 5, 5, 5, 6, 5 },
         { 0, 6, 1, 6, 2, 6, 3, 6, 4, 6 },
         { 1, 6, 2, 6, 3, 6, 4, 6, 5, 6 },
         { 2, 6, 3, 6, 4, 6, 5, 6, 6, 6 }

   };

   private static final int[][] diagonals = {
         { 0, 0, 1, 1, 2, 2, 3, 3, 4, 4 },
         { 1, 1, 2, 2, 3, 3, 4, 4, 5, 5 },
         { 2, 2, 3, 3, 4, 4, 5, 5, 6, 6 },
         { 1, 0, 2, 1, 3, 2, 4, 3, 5, 4 },
         { 2, 1, 3, 2, 4, 3, 5, 4, 6, 5 },
         { 2, 0, 3, 1, 4, 2, 5, 3, 6, 4 },
         { 0, 1, 1, 2, 2, 3, 3, 4, 4, 5 },
         { 1, 2, 2, 3, 3, 4, 4, 5, 5, 6 },
         { 0, 2, 1, 3, 2, 4, 3, 5, 4, 6 }
   };
   private static final int[][] antiDiagonals = {
         { 6, 0, 5, 1, 4, 2, 3, 3, 2, 4 },
         { 5, 1, 4, 2, 3, 3, 2, 4, 1, 5 },
         { 4, 2, 3, 3, 2, 4, 1, 5, 0, 6 },
         { 5, 0, 4, 1, 3, 2, 2, 3, 1, 4 },
         { 4, 1, 3, 2, 2, 3, 1, 4, 0, 5 },
         { 4, 0, 3, 1, 2, 2, 1, 3, 0, 4 },
         { 6, 1, 5, 2, 4, 3, 3, 4, 2, 5 },
         { 5, 2, 4, 3, 3, 4, 2, 5, 1, 6 },
         { 0, 2, 5, 3, 4, 4, 3, 5, 2, 6 }
   };

   @Override
   public int getValue() {
      if (moveCount >= 100) {
         return 0;
      }

      // int threevalue = count3Pattern();
      int value = count4Pattern();

      // System.out.println("we are debugging 4 pattern");

      return value;
   }

   private int count4Pattern() {
      int countgreen = 0;
      int countyellow = 0;
      int count3green = 0;
      int count3yellow = 0;
     // System.out.println("count for green");
      //System.out.println(" ");

      countgreen += calc4Horizontal(PLAYER_0, horizantals, winningPattGreen); // horizontal
      // System.out.println("horizontal:" + countgreen);
      countgreen += calc4Horizontal(PLAYER_0, verticals, winningPattGreen); // verticals
      // System.out.println("verts: " + countgreen);

      countgreen += calc4Diagonal(PLAYER_0, diagonals, winningPattGreen);// diagonals
      // System.out.println("diagonal: " + countgreen);

      countgreen += calc4Diagonal(PLAYER_0, antiDiagonals, winningPattGreen); // antidiagonal
      // System.out.println("antidiagonal: " + countgreen);

      countyellow += calc4Horizontal(PLAYER_1, horizantals, winningPattYellow);//
      // horizontals
      countyellow += calc4Horizontal(PLAYER_1, verticals, winningPattYellow);//
      // vericals
      countyellow += calc4Diagonal(PLAYER_1, diagonals, winningPattYellow);//
      // diagonals
      countyellow += calc4Diagonal(PLAYER_1, antiDiagonals, winningPattYellow); //


      count3green += calcHorizontal(PLAYER_0, horizantals, winningPattYellow);// horizontals
      count3green += calcvertical(PLAYER_0, verticals, winningPattYellow);// vericals
      count3green += calcDiagonal(PLAYER_0, diagonals, winningPattYellow);// diagonals
      count3green += calcantiDiagonal(PLAYER_0, antiDiagonals, winningPattYellow); // antidiagonal

      count3yellow += calcHorizontal(PLAYER_1, horizantals, winningPattYellow);// horizontals
      count3yellow += calcvertical(PLAYER_1, verticals, winningPattYellow);// vericals
      count3yellow += calcDiagonal(PLAYER_1, diagonals, winningPattYellow);// diagonals
      count3yellow += calcantiDiagonal(PLAYER_1, antiDiagonals, winningPattYellow); // antidiagonal
      // System.out.println("count for 3yellow");

      // System.out.println(" ");

      // System.out.println("horizontal: " + count3yellowhoritz);

      // System.out.println("verts: " + count3yellowvert);

      // System.out.println("diagonal: " + count3yellowdiag);

      // System.out.println("antidiagonal: " + count3yellowdanti);

      int finaly = count3yellow + countyellow;
      int finalg = count3green + countgreen;

      return finalg - finaly;

   }

   public int calc4Diagonal(int player, int[][] diag, List<byte[]> winningPattern) {
      int maxCount = 0;

      for (byte[] pattern : winningPattern) {
         // iterate over the first idx of horiz
         // System.out.println("here we are checking what pattern we have" +
         // Arrays.toString(pattern));

         for (int i = 0; i < diag.length; i += 1) {
            // use j and j+1 to get row and col from the second array in horiz
            int pcellcount = 0;
            ArrayList<peg5Cell> cellcontents = new ArrayList<>(); // Use peg5Cell objects
            for (int j = 0; j < 10; j += 2) {
               int row = diag[i][j];
               int col = diag[i][j + 1];
               // 0 based idx
               // System.out.println("row" + row + "col" + col);

               peg5Cell cell = board[row][col];
               // get contnts of the cell and verify the piece and adds if it belongs to the
               // player
               if (cell.hasPiece(player)) {
                  cellcontents.add(cell);
                  pcellcount++;
               }
               // still add the contents since we need the pattern still
               else {
                  cellcontents.add(cell);
               }
            }
            // System.out.println("here are the cellcontents after each iteration" +
            // cellcontents);
            // System.out.println("size of cellcontents" + cellcontents.size());
            // System.out.println("curent pcellcount" + pcellcount);
            if (pcellcount == 4) {
               int patternmatch = 0;
               int emptycell = 0;
               for (int idx = 0; idx < cellcontents.size(); idx++) {
                  if ((pattern[idx] & cellcontents.get(idx).getContents()) != 0) {
                     // System.out.println("current bit pattern we are checking" + pattern[idx]);
                     // System.out.println("cellcontents.get(idx).getContents()");
                     patternmatch += 1;
                  }
                  if ((pattern[idx] & cellcontents.get(idx).getContents()) == 0
                        && cellcontents.get(idx).getContents() == 0) {
                     emptycell += 1;

                  }
               }
               // System.out.println("number of pattern matches" + patternmatch);
               if (patternmatch == 4 && emptycell == 1) {
                  maxCount += 1;
               }
               // System.out.println("maxcount" + maxCount);

            }
         }
      }
      // multiply by 10 to add 4 cell value
      return maxCount * 10;

   }

   public int calc4Horizontal(int player, int[][] horiz, List<byte[]> winningPattern) {
      int maxCount = 0;

      for (byte[] pattern : winningPattern) {
         // iterate over the first idx of horiz
         // System.out.println("here we are checking what pattern we have" +
         // Arrays.toString(pattern));

         for (int i = 0; i < horiz.length; i += 1) {
            // use j and j+1 to get row and col from the second array in horiz
            int pcellcount = 0;
            ArrayList<peg5Cell> cellcontents = new ArrayList<>(); // Use peg5Cell objects
            for (int j = 0; j < 10; j += 2) {
               int row = horiz[i][j];
               int col = horiz[i][j + 1];
               // 0 based idx
               // System.out.println("row" + row + "col" + col);

               peg5Cell cell = board[row][col];
               // get contnts of the cell and verify the piece and adds if it belongs to the
               // player
               if (cell.hasPiece(player)) {
                  cellcontents.add(cell);
                  pcellcount++;
               }
               // still add the contents since we need the pattern still
               else {
                  cellcontents.add(cell);
               }
            }
            // System.out.println("here are the cellcontents after each iteration" +
            // cellcontents);
            // System.out.println("size of cellcontents" + cellcontents.size());
            // System.out.println("curent pcellcount" + pcellcount);
            if (pcellcount == 4) {
               int patternmatch = 0;
               int emptycell = 0;
               for (int idx = 0; idx < cellcontents.size(); idx++) {
                  if ((pattern[idx] & cellcontents.get(idx).getContents()) != 0) {
                     // System.out.println("current bit pattern we are checking" + pattern[idx]);
                     // System.out.println("cellcontents.get(idx).getContents()");
                     patternmatch += 1;
                  }
                  if ((pattern[idx] & cellcontents.get(idx).getContents()) == 0
                        && cellcontents.get(idx).getContents() == 0) {
                     emptycell += 1;

                  }
               }
               // System.out.println("number of pattern matches" + patternmatch);
               if (patternmatch == 4 && emptycell == 1) {
                  maxCount += 1;
               }
               // System.out.println("maxcount" + maxCount);

            }
         }
      }
      // multiply by 10 to add 4 cell value
      return maxCount * 10;

   }

   public int calcDiagonal(int player, int[][] diag, List<byte[]> winningPattern) {
      int maxCount = 0;

      for (byte[] pattern : winningPattern) {
         // iterate over the first idx of horiz
         // System.out.println("here we are checking what pattern we have" +
         // Arrays.toString(pattern));

         for (int i = 0; i < diag.length; i += 1) {
            // use j and j+1 to get row and col from the second array in horiz
            int pcellcount = 0;
            ArrayList<peg5Cell> cellcontents = new ArrayList<>(); // Use peg5Cell objects
            for (int j = 0; j < 10; j += 2) {
               int row = diag[i][j];
               int col = diag[i][j + 1];
               // 0 based idx
               // System.out.println("row" + row + "col" + col);

               peg5Cell cell = board[row][col];
               // get contnts of the cell and verify the piece and adds if it belongs to the
               // player
               if (cell.hasPiece(player)) {
                  cellcontents.add(cell);
                  pcellcount++;
               }
               // still add the contents since we need the pattern still
               else {
                  cellcontents.add(cell);
               }
            }
            // System.out.println("here are the cellcontents after each iteration" +
            // cellcontents);
            // System.out.println("size of cellcontents" + cellcontents.size());
            // System.out.println("curent pcellcount" + pcellcount);
            if (pcellcount == 3) {
               int patternmatch = 0;
               int emptycell = 0;
               for (int idx = 0; idx < cellcontents.size(); idx++) {
                  if ((pattern[idx] & cellcontents.get(idx).getContents()) != 0) {
                     // System.out.println("current bit pattern we are checking" + pattern[idx]);
                     // System.out.println("cellcontents.get(idx).getContents()");
                     patternmatch += 1;
                  }
                  if ((pattern[idx] & cellcontents.get(idx).getContents()) == 0
                        && cellcontents.get(idx).getContents() == 0) {
                     emptycell += 1;

                  }
               }
               // System.out.println("number of pattern matches" + patternmatch);
               if (patternmatch == 3 && emptycell == 2) {
                  maxCount += 1;
               }
               // System.out.println("maxcount" + maxCount);

            }
         }
      }
      return maxCount;

   }

   public int calcantiDiagonal(int player, int[][] diag, List<byte[]> winningPattern) {
      int maxCount = 0;

      for (byte[] pattern : winningPattern) {
         // iterate over the first idx of horiz
         // System.out.println("here we are checking what pattern we have" +
         // Arrays.toString(pattern));

         for (int i = 0; i < diag.length; i += 1) {
            // use j and j+1 to get row and col from the second array in horiz
            int pcellcount = 0;
            ArrayList<peg5Cell> cellcontents = new ArrayList<>(); // Use peg5Cell objects
            for (int j = 0; j < 10; j += 2) {
               int row = diag[i][j];
               int col = diag[i][j + 1];
               // 0 based idx
               // System.out.println("row" + row + "col" + col);

               peg5Cell cell = board[row][col];
               // get contnts of the cell and verify the piece and adds if it belongs to the
               // player
               if (cell.hasPiece(player)) {
                  cellcontents.add(cell);
                  pcellcount++;
               }
               // still add the contents since we need the pattern still
               else {
                  cellcontents.add(cell);
               }
            }
            // System.out.println("here are the cellcontents after each iteration" +
            // cellcontents);
            // System.out.println("size of cellcontents" + cellcontents.size());
            // System.out.println("curent pcellcount" + pcellcount);
            if (pcellcount == 3) {
               int patternmatch = 0;
               int emptycell = 0;
               for (int idx = 0; idx < cellcontents.size(); idx++) {
                  // if cell contnets doesnt match with the pattern cell in idx pattern check if
                  // that cell pattern is empty
                  // and you can play on it dont increment matchcount increment playble count
                  // if
                  if ((pattern[idx] & cellcontents.get(idx).getContents()) != 0) {
                     // System.out.println("current bit pattern we are checking: " + pattern[idx]);
                     // System.out.println(cellcontents.get(idx).getContents());
                     patternmatch += 1;
                  }
                  if ((pattern[idx] & cellcontents.get(idx).getContents()) == 0
                        && cellcontents.get(idx).getContents() == 0) {
                     emptycell += 1;
                  }
               }
               /// System.out.println("number of pattern matches" + patternmatch);
               if (patternmatch == 3 && emptycell == 2) {
                  maxCount += 1;
               }
               // System.out.println("maxcount" + maxCount);

            }
         }
      }
      return maxCount;

   }

   public int calcHorizontal(int player, int[][] horiz, List<byte[]> winningPattern) {
      int maxCount = 0;

      for (byte[] pattern : winningPattern) {
         // iterate over the first idx of horiz
         // System.out.println("here we are checking what pattern we have: " +
         // Arrays.toString(pattern));

         for (int i = 0; i < horiz.length; i += 1) {
            // use j and j+1 to get row and col from the second array in horiz
            int pcellcount = 0;
            ArrayList<peg5Cell> cellcontents = new ArrayList<>(); // Use peg5Cell objects
            for (int j = 0; j < 10; j += 2) {
               int row = horiz[i][j];
               int col = horiz[i][j + 1];
               // 0 based idx
               // System.out.println("row" + row + "col" + col);

               peg5Cell cell = board[row][col];
               // get contnts of the cell and verify the piece and adds if it belongs to the
               // player
               if (cell.hasPiece(player)) {
                  cellcontents.add(cell);
                  pcellcount++;
               }
               // still add the contents since we need the pattern still
               else {
                  cellcontents.add(cell);
               }
            }
           // System.out.println("here are the cellcontents after each iteration" +
                 // cellcontents);
            // System.out.println("size of cellcontents" + cellcontents.size());
            // System.out.println("curent pcellcount" + pcellcount);
            if (pcellcount == 3) {
               int patternmatch = 0;
               int emptycell = 0;
               for (int idx = 0; idx < cellcontents.size(); idx++) {
                  // if cell contnets doesnt match with the pattern cell in idx pattern check if
                  // that cell pattern is empty
                  // and you can play on it dont increment matchcount increment playble count
                  // if
                  if ((pattern[idx] & cellcontents.get(idx).getContents()) != 0) {
                     // System.out.println("current bit pattern we are checking: " + pattern[idx]);
                     // System.out.println(cellcontents.get(idx).getContents());
                     patternmatch += 1;
                  }
                  if ((pattern[idx] & cellcontents.get(idx).getContents()) == 0
                        && cellcontents.get(idx).getContents() == 0) {
                     emptycell += 1;
                  }
               }
               // System.out.println("number of pattern matches" + patternmatch);
               if (patternmatch == 3 && emptycell == 2) {
                  maxCount += 1;
               }
               // System.out.println("maxcount" + maxCount);

            }
         }
      }
      return maxCount;

   }

   public int calcvertical(int player, int[][] horiz, List<byte[]> winningPattern) {
      int maxCount = 0;

      for (byte[] pattern : winningPattern) {
         // iterate over the first idx of horiz
        // System.out.println("here we are checking what pattern we have: " + Arrays.toString(pattern));

         for (int i = 0; i < horiz.length; i += 1) {
            // use j and j+1 to get row and col from the second array in horiz
            int pcellcount = 0;
            ArrayList<peg5Cell> cellcontents = new ArrayList<>(); // Use peg5Cell objects
            for (int j = 0; j < 10; j += 2) {
               int row = horiz[i][j];
               int col = horiz[i][j + 1];
               // 0 based idx
               // System.out.println("row" + row + "col" + col);

               peg5Cell cell = board[row][col];
               // get contnts of the cell and verify the piece and adds if it belongs to the
               // player
               if (cell.hasPiece(player)) {
                  cellcontents.add(cell);
                  pcellcount++;
               }
               // still add the contents since we need the pattern still
               else {
                  cellcontents.add(cell);
               }
            }
            // System.out.println("here are the cellcontents after each iteration" +
            /// cellcontents);
            // System.out.println("size of cellcontents" + cellcontents.size());
            // System.out.println("curent pcellcount" + pcellcount);
            if (pcellcount == 3) {
               int patternmatch = 0;
               int emptycell = 0;
               for (int idx = 0; idx < cellcontents.size(); idx++) {
                  // if cell contnets doesnt match with the pattern cell in idx pattern check if
                  // that cell pattern is empty
                  // and you can play on it dont increment matchcount increment playble count
                  // if
                  if ((pattern[idx] & cellcontents.get(idx).getContents()) != 0) {
                    // System.out.println("current bit pattern we are checking:  " + pattern[idx]);
                     //System.out.println(cellcontents.get(idx).getContents());
                     patternmatch += 1;
                  }
                  if ((pattern[idx] & cellcontents.get(idx).getContents()) == 0
                        && cellcontents.get(idx).getContents() == 0) {
                     emptycell += 1;
                  }
               }
               // System.out.println("number of pattern matches" + patternmatch);
               if (patternmatch == 3 && emptycell == 2) {
                  maxCount += 1;
               }
               // System.out.println("maxcount" + maxCount);

            }
         }
      }
      return maxCount;

   }
   // count3 cell pattern it takes in the player and the winning pattern and
   // returns the number of 3 cell patterns for the player
   // in the method it calculates the number of patterns by traversing through a
   // list of all possible patterns from horizontal to vertical to diagonal and
   // anti diagonal
   // so from the coordinates of the pattern we get the contents of the cell and
   // check if the bitwise operation of the pattern and the cell contents is not
   // equal to 0 to ensure that the pattern is valid as well we need to check if
   // the piece actualy belongs to the player
   // if the pattern is valid we return 1 else we return 0
   // we do this for all the patterns and return the total number of patterns
   // rememer to actually verify the pattern is a 3 cell pattern we have to check
   // if 3 pieces match the pattern and if it is 4 pieces it should not be counted
   // as a 3 cell pattern

   // Return all valid moves for the current player
   @Override
   public List<Board.Move> getValidMoves() {
      List<Board.Move> validMoves = new ArrayList<>();

      // Check for placing moves
      for (int row = 0; row < BOARD_SIZE; row++) {
         for (int col = 0; col < BOARD_SIZE; col++) {
            peg5Cell cell = board[row][col];

            if (canPlaceTube(row, col, player)) {
               if (player == PLAYER_0 && greenUnplayedPieces[1] > 0) {
                  validMoves.add(new peg5Move(peg5Move.PieceType.OPEN_TUBE, row, col));
               } else if (player == PLAYER_1 && yellowUnplayedPieces[1] > 0) {
                  validMoves.add(new peg5Move(peg5Move.PieceType.OPEN_TUBE, row, col));
               }

               if (player == PLAYER_0 && greenUnplayedPieces[2] > 0) {
                  validMoves.add(new peg5Move(peg5Move.PieceType.CLOSED_TUBE, row, col));
               } else if (player == PLAYER_1 && yellowUnplayedPieces[2] > 0) {
                  validMoves.add(new peg5Move(peg5Move.PieceType.CLOSED_TUBE, row, col));
               }
            }

            if (canPlacePeg(row, col, player)) {
               validMoves.add(new peg5Move(peg5Move.PieceType.PEG, row, col));
            }

            // Check for repositioning moves for pegs
            if (player == PLAYER_0 && greenUnplayedPieces[0] == 0) {
               for (int sourceRow = 0; sourceRow < BOARD_SIZE; sourceRow++) {
                  for (int sourceCol = 0; sourceCol < BOARD_SIZE; sourceCol++) {
                     if (board[sourceRow][sourceCol].hasGreenPeg() && canPlacePeg(row, col, player)) {
                        validMoves.add(new peg5Move(peg5Move.PieceType.PEG, row, col, sourceRow, sourceCol));
                     }
                  }
               }
            } else if (player == PLAYER_1 && yellowUnplayedPieces[0] == 0) {
               for (int sourceRow = 0; sourceRow < BOARD_SIZE; sourceRow++) {
                  for (int sourceCol = 0; sourceCol < BOARD_SIZE; sourceCol++) {
                     if (board[sourceRow][sourceCol].hasYellowPeg() && canPlacePeg(row, col, player)) {
                        validMoves.add(new peg5Move(peg5Move.PieceType.PEG, row, col, sourceRow, sourceCol));
                     }
                  }
               }
            }
         }
      }

      // Check for repositioning moves
      if (player == PLAYER_0 && greenUnplayedPieces[0] == 0) {
         for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
               peg5Cell cell = board[row][col];

               if (cell.hasGreenPeg()) {
                  validMoves.addAll(getRepositioningMoves(row, col, peg5Move.PieceType.PEG));
               }
            }
         }
      } else if (player == PLAYER_1 && yellowUnplayedPieces[0] == 0) {
         for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
               peg5Cell cell = board[row][col];

               if (cell.hasYellowPeg()) {
                  validMoves.addAll(getRepositioningMoves(row, col, peg5Move.PieceType.PEG));
               }
            }
         }
      }

      if (player == PLAYER_0 && greenUnplayedPieces[1] == 0 && greenUnplayedPieces[2] == 0) {
         for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
               peg5Cell cell = board[row][col];

               if (cell.hasGreenOpenTube()) {
                  validMoves.addAll(getRepositioningMoves(row, col, peg5Move.PieceType.OPEN_TUBE));
               }
               if (cell.hasGreenClosedTube()) {
                  validMoves.addAll(getRepositioningMoves(row, col, peg5Move.PieceType.CLOSED_TUBE));
               }
            }
         }
      } else if (player == PLAYER_1 && yellowUnplayedPieces[1] == 0 && yellowUnplayedPieces[2] == 0) {
         for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
               peg5Cell cell = board[row][col];

               if (cell.hasYellowOpenTube()) {
                  validMoves.addAll(getRepositioningMoves(row, col, peg5Move.PieceType.OPEN_TUBE));
               }
               if (cell.hasYellowClosedTube()) {
                  validMoves.addAll(getRepositioningMoves(row, col, peg5Move.PieceType.CLOSED_TUBE));
               }
            }
         }
      }

      // Sort the valid moves based on the desired order
      validMoves.sort((move1, move2) -> {
         peg5Move peg5Move1 = (peg5Move) move1;
         peg5Move peg5Move2 = (peg5Move) move2;

         // Compare row and column first
         if (peg5Move1.getRow() != peg5Move2.getRow()) {
            return Integer.compare(peg5Move1.getRow(), peg5Move2.getRow());
         } else if (peg5Move1.getCol() != peg5Move2.getCol()) {
            return Integer.compare(peg5Move1.getCol(), peg5Move2.getCol());
         } else {
            // If row and column are the same, compare piece types
            return peg5Move1.getPieceType().compareTo(peg5Move2.getPieceType());
         }
      });

      return validMoves;
   }

   private List<peg5Move> getRepositioningMoves(int sourceRow, int sourceCol, peg5Move.PieceType pieceType) {
      List<peg5Move> repositioningMoves = new ArrayList<>();

      for (int row = 0; row < BOARD_SIZE; row++) {
         for (int col = 0; col < BOARD_SIZE; col++) {
            if (pieceType == peg5Move.PieceType.PEG && canPlacePeg(row, col, player)) {
               repositioningMoves.add(new peg5Move(pieceType, row, col, sourceRow, sourceCol));
            } else if ((pieceType == peg5Move.PieceType.OPEN_TUBE || pieceType == peg5Move.PieceType.CLOSED_TUBE)
                  && canPlaceTube(row, col, player)) {
               repositioningMoves.add(new peg5Move(pieceType, row, col, sourceRow, sourceCol));
            }
         }
      }

      return repositioningMoves;
   }

   // Return the current player
   @Override
   public int getCurrentPlayer() {
      return player;
   }

   // Return the move history
   @Override
   public List<? extends Board.Move> getMoveHistory() {
      return moveHistory;
   }

   // Undo the last move
   @Override
   public void undoMove() {
      if (!moveHistory.isEmpty()) {
         peg5Move lastMove = moveHistory.remove(moveHistory.size() - 1);
         moveCount--;
         player = -player;

         int row = lastMove.getRow();
         int col = lastMove.getCol();
         peg5Cell cell = board[row][col];

         if (lastMove.getPieceType() == peg5Move.PieceType.PEG) {
            if (player == PLAYER_0) {
               cell.removeGreenPeg();
               greenUnplayedPieces[0]++;
            } else {
               cell.removeYellowPeg();
               yellowUnplayedPieces[0]++;
            }
         } else if (lastMove.getPieceType() == peg5Move.PieceType.OPEN_TUBE) {
            if (player == PLAYER_0) {
               cell.removeGreenOpenTube();
               greenUnplayedPieces[1]++;
            } else {
               cell.removeYellowOpenTube();
               yellowUnplayedPieces[1]++;
            }
         } else if (lastMove.getPieceType() == peg5Move.PieceType.CLOSED_TUBE) {
            if (player == PLAYER_0) {
               cell.removeGreenClosedTube();
               greenUnplayedPieces[2]++;
            } else {
               cell.removeYellowClosedTube();
               yellowUnplayedPieces[2]++;
            }
         }

         if (lastMove.isRepositioningMove()) {
            int sourceRow = lastMove.getSourceRow();
            int sourceCol = lastMove.getSourceCol();
            peg5Cell sourceCell = board[sourceRow][sourceCol];

            if (player == PLAYER_0) {
               if (lastMove.getPieceType() == peg5Move.PieceType.PEG) {
                  sourceCell.setGreenPeg();
               } else if (lastMove.getPieceType() == peg5Move.PieceType.OPEN_TUBE) {
                  sourceCell.setGreenOpenTube();
               } else if (lastMove.getPieceType() == peg5Move.PieceType.CLOSED_TUBE) {
                  sourceCell.setGreenClosedTube();
               }
            } else {
               if (lastMove.getPieceType() == peg5Move.PieceType.PEG) {
                  sourceCell.setYellowPeg();
               } else if (lastMove.getPieceType() == peg5Move.PieceType.OPEN_TUBE) {
                  sourceCell.setYellowOpenTube();
               } else if (lastMove.getPieceType() == peg5Move.PieceType.CLOSED_TUBE) {
                  sourceCell.setYellowClosedTube();
               }
            }
         }
      }
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();

      // Display Green's unplayed pieces
      sb.append(String.format("%" + 10 + "s",
            String.join("", Collections.nCopies(Math.max(0, greenUnplayedPieces[0]), "G")))).append("\n");
      sb.append(String.format("%" + 10 + "s",
            String.join(" ", Collections.nCopies(Math.max(0, greenUnplayedPieces[1]), "Og"))))
            .append(" ")
            .append(String.format("%" + 10 + "s",
                  String.join(" ", Collections.nCopies(Math.max(0, greenUnplayedPieces[2]), "-g"))))
            .append("\n\n\n");

      // Display the board
      sb.append(" ").append(" ").append(" 1  2  3  4  5  6  7\n"); // Add spacing for row numbers
      for (int row = 0; row < BOARD_SIZE; row++) {
         sb.append(String.format("%2d", row + 1)); // Left-justify row numbers with 2 spaces
         for (int col = 0; col < BOARD_SIZE; col++) {
            peg5Cell cell = board[row][col];
            String cellStr = getCellString(cell);
            sb.append(String.format(" %-2s", cellStr)); // Left-justify cell strings with 2 spaces
         }
         sb.append("\n");
      }
      sb.append("\n\n");

      // Display Yellow's unplayed pieces
      sb.append("")
            .append(String.format("%" + 10 + "s",
                  String.join(" ", Collections.nCopies(Math.max(0, yellowUnplayedPieces[1]), "Oy"))))
            .append(" ")
            .append(String.format("%" + 10 + "s",
                  String.join(" ", Collections.nCopies(Math.max(0, yellowUnplayedPieces[2]), "-y"))))
            .append("\n")
            .append(String.format("%" + 10 + "s",
                  String.join("", Collections.nCopies(Math.max(0, yellowUnplayedPieces[0]), "Y"))))
            .append("\n\n");

      // Display the current player's turn
      sb.append(player == PLAYER_0 ? "Green to play" : "Yellow to play");

      return sb.toString();
   }

   private String getCellString(peg5Cell cell) {
      int contents = cell.getContents();

      if ((contents & peg5Cell.GREEN_PEG) != 0) {
         if ((contents & peg5Cell.YELLOW_OPEN_TUBE) != 0) {
            return "Gy";
         } else {
            return "G";
         }
      } else if ((contents & peg5Cell.YELLOW_PEG) != 0) {
         if ((contents & peg5Cell.GREEN_OPEN_TUBE) != 0) {
            return "Yg";
         } else {
            return "Y";
         }
      } else if ((contents & peg5Cell.GREEN_OPEN_TUBE) != 0) {
         return "Og";
      } else if ((contents & peg5Cell.GREEN_CLOSED_TUBE) != 0) {
         return "-g";
      } else if ((contents & peg5Cell.YELLOW_OPEN_TUBE) != 0) {
         return "Oy";
      } else if ((contents & peg5Cell.YELLOW_CLOSED_TUBE) != 0) {
         return "-y";
      } else {
         return ".";
      }
   }
}
