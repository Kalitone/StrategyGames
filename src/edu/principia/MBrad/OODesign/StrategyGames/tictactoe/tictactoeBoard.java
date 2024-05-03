package edu.principia.MBrad.OODesign.StrategyGames.tictactoe;

import java.util.LinkedList;
import java.util.List;


import java.io.IOException;
import java.io.Serializable;
import edu.principia.OODesign.StrategyGames.Board;

public class tictactoeBoard implements Board, Serializable {
   public class tictactoeMove implements Move, Serializable {
      private static final long serialVersionUID = 1L;
      public int row;
      public int col;

      public tictactoeMove() {
         row = -1;
         col = -1;
      }

      public tictactoeMove(int row, int col) {
         this.row = row;
         this.col = col;
      }

      // Read and write the stream using 4 bits for each row and col
      public void write(java.io.OutputStream os) throws IOException {
         int packedValue = (row << 4) | col; // Pack row and col into a single byte
         os.write(packedValue); // Write the packed value
      }

      public void read(java.io.InputStream is) throws IOException {
         int packedValue = is.read(); // Read the packed value
         row = (packedValue >> 4) & 0xF; // Extract row from the packed value
         col = packedValue & 0xF; // Extract col from the packed value
      }
      @Override
      public void fromString(String s) throws IOException {
          String[] parts = s.split(",");
          if (parts.length != 2) {
              throw new IOException("Invalid move string: " + s);
          }
          try {
              this.row = Integer.parseInt(parts[0].trim()) - 1;
              this.col = Integer.parseInt(parts[1].trim()) - 1;
          } catch (NumberFormatException e) {
              throw new IOException("Invalid move format: " + s);
          }
      }

      public boolean isValidMove(int row, int col) {
         return row >= 0 && row < BSIZE && col >= 0 && col < BSIZE;
      }

      @Override
      public int compareTo(Board.Move m) {
          tictactoeMove other = (tictactoeMove) m;
          if (row == other.row) {
              return Integer.compare(col, other.col);
          } else {
              return Integer.compare(row, other.row);
          }
      }

      @Override
      public String toString() {
         return "row " + (row + 1) + "," + "col " + (col + 1);
      }
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < board.length; i++) {
         sb.append("");
         for (int j = 0; j < board[i].length; j++) {
            sb.append(board[i][j] == 0 ? " " : board[i][j] == 1 ? "O" : "X");
            if (j < board[i].length - 1) {
               sb.append("|");
            }
         }
         sb.append(" \n");
         if (i < board.length - 1) {
            sb.append("-----\n");
         }
      }
      return sb.toString();
   }

   private int[][] board;
   private int currentPlayer; // 0 or 1 for player 0 or player 1
   private java.util.List<tictactoeMove> moveHistory;
   private static final int BSIZE = 3;

   public tictactoeBoard() {

      board = new int[BSIZE][BSIZE];
      currentPlayer = 0;
      moveHistory = new LinkedList<tictactoeMove>();
   }

   @Override
   public Board.Move createMove() {
      return new tictactoeMove();
   }

   @Override
   public void applyMove(Board.Move m) throws InvalidMoveException {
       tictactoeMove move = (tictactoeMove) m;
       if (move.row < 0 || move.row >= BSIZE || move.col < 0 || move.col >= BSIZE) {
           throw new InvalidMoveException("Invalid move: out of bounds");
       }
       if (board[move.row][move.col] != 0) {
           throw new InvalidMoveException("Invalid move: cell already occupied");
       }
       board[move.row][move.col] = currentPlayer == 0 ? 1 : -1;
       moveHistory.add(move);
       currentPlayer = 1 - currentPlayer;
   }

   @Override
   public List<? extends Board.Move> getValidMoves() {
      List<tictactoeMove> validMoves = new LinkedList<tictactoeMove>();
      for (int row = 0; row < BSIZE; row++) {
         for (int col = 0; col < BSIZE; col++) {
            if (board[row][col] == 0) {
               validMoves.add(new tictactoeMove(row, col));
            }
         }
      }
      return validMoves;
   }

   // return a value for the current boar if either player has won return Win for
   // player 0 , -WIN for player 1 or 0 for a draw otherwise return a value on the
   // number of rows, cols or diagonals for whicih a player has 2 pieces in a row
   // and
   // one empty space. Add one to the value for each sych case for thr first player
   // and -1 for each such case for second player. this value is used to guide AI
   // in
   // making moves
   @Override
   public int getValue() {
      int[] rowSum = new int[BSIZE];
      int[] colSum = new int[BSIZE];
      int diagSum = 0;
      int antiDiagSum = 0;

      for (int row = 0; row < BSIZE; row++) {
         for (int col = 0; col < BSIZE; col++) {
            rowSum[row] += board[row][col];
            colSum[col] += board[row][col];
            if (row == col) {
               diagSum += board[row][col];
            }
            if (row + col == BSIZE - 1) {
               antiDiagSum += board[row][col];
            }
         }
      }
      // check for a win
      if (Math.abs(diagSum) == BSIZE || Math.abs(antiDiagSum) == BSIZE) {
         return diagSum == BSIZE ? WIN : -WIN;
      }
      for (int i = 0; i < BSIZE; i++) {
         if (Math.abs(rowSum[i]) == BSIZE || Math.abs(colSum[i]) == BSIZE) {
            return rowSum[i] == BSIZE ? WIN : -WIN;
         }
      }
      // use moveHistory to determine if the game is a draw
      if (moveHistory.size() == BSIZE * BSIZE) {
         return 0;
      }
      // check for 2 in a row and return value based on the number of them
      int value = 0;
      for (int i = 0; i < BSIZE; i++) {

         if (rowSum[i] == 2) {
            value++;
         }
         if (rowSum[i] == -2) {
            value--;
         }
         if (colSum[i] == 2) {
            value++;
         }
         if (colSum[i] == -2) {
            value--;
         }
      }
      if (diagSum == 2) {
         value++;
      }
      if (diagSum == -2) {
         value--;
      }
      if (antiDiagSum == 2) {
         value++;
      }
      if (antiDiagSum == -2) {
         value--;
      }
      return value;
   }

   @Override
   public int getCurrentPlayer() {
      return currentPlayer == PLAYER_0 ? PLAYER_0 : PLAYER_1;
   }

   @Override
   public List<? extends Move> getMoveHistory() {
      // TODO Auto-generated method stub
      return moveHistory;
   }

   @Override
   public void undoMove() {
      // TODO Auto-generated method stub
      if (moveHistory.size() > 0) {
         tictactoeMove lastMove = moveHistory.remove(moveHistory.size() - 1);
         board[lastMove.row][lastMove.col] = 0;
         currentPlayer = 1 - currentPlayer;
      }
   }
}

