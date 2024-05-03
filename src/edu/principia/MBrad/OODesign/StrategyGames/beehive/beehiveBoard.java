   package edu.principia.MBrad.OODesign.StrategyGames.beehive;

   import java.util.ArrayList;
   import java.util.List;
   import java.util.stream.Collectors;
   import java.io.Serializable;
   import edu.principia.OODesign.StrategyGames.Board;

   public class beehiveBoard implements Board, Serializable {
      public static final int BOARD_SIZE = 11;
      public cell[][] board;
      private int currentPlayer;
      private List<beehiveMove> moveHistory;
      private List<connection> connections;
      private List<bridgeGroup> bridgeGroups;
      private boolean hasSwapped;
      public bridgeGroup winningGroup = null;
      public boolean isGameOver = false;
      public int totalWeightPlayer1 = 0;
      public int totalWeightPlayer2 = 0;

      public beehiveBoard() {
         board = new cell[BOARD_SIZE][BOARD_SIZE];
         currentPlayer = Board.PLAYER_0; // Starting player
         moveHistory = new ArrayList<beehiveMove>();
         connections = new ArrayList<connection>();
         bridgeGroups = new ArrayList<bridgeGroup>();
         hasSwapped = false;

         // Initialize the board with empty cells
         for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
               board[i][j] = new cell(i, j);
            }
         }

         // Initialize the adjacent cells for each cell
         for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
               board[i][j].computeAdjacentcells();
               board[i][j].computebridgedcells();
            }
         }
      }

      public Move createMove() {
         return new beehiveMove();
      }

      public void applyMove(Move m) throws InvalidMoveException {
         // Check if the game is over
         if (isGameOver) {
            throw new InvalidMoveException("Game is already over.");
         }

         // Cast the move to beeHiveBoardMove
         beehiveMove move = new beehiveMove((beehiveMove) m);

         // Check if the move is within the board boundaries
         // if (move.row < 0 || move.row >= BOARD_SIZE || move.col < 0 || move.col >=
         // BOARD_SIZE) {
         // throw new InvalidMoveException("Move is out of board boundaries.");
         // }

         // Clear the bridge groups
         bridgeGroups.clear();

         // Check if the move is a swap move, the move history size is 1, no swap has
         // occurred yet, and the current player is 2
         // This condition is for the special case where the second player can swap the
         // position of the first player's move
         if (move.isSwap) {
            if (moveHistory.size() == 1 && !hasSwapped && currentPlayer == Board.PLAYER_1) {
               // Get the first move from the move history
               beehiveMove firstMove = moveHistory.get(0);
               // Store the owner of the cell where the first move was made
               move.previousOwner = board[firstMove.row][firstMove.col].player;
               // Swap the owner of the cell where the first move was made
               board[firstMove.row][firstMove.col].setOwner(currentPlayer);
               // Set the flag indicating a swap has occurred
               hasSwapped = true;
            } else {
               // If the move is a swap move, but the conditions are not met, throw an
               // exception
               throw new InvalidMoveException("Invalid swap move.");
            }
         } else {
            // If the cell where the move is being made is already occupied by a player,
            // throw an exception
            if (board[move.row][move.col].player != 0) {
               throw new InvalidMoveException("cell is already occupied.");
            }
            // Store the owner of the cell where the move is being made
            move.previousOwner = board[move.row][move.col].player;
            // Set the owner of the cell where the move is being made to the current player
            board[move.row][move.col].player = currentPlayer;
            // updatebridgeGroups(board[move.row][move.col]);
            // currentPlayer = 3- currentPlayer ;

            // Initialize a variable to store a connection to delete
            connection connToDelete = null;
            // Iterate over all the connections
            for (connection conn : connections) {
               // If the current connection involves the cell where the move is being made,
               // store it in the variable to delete
               if ((conn.cell1.row == move.row && conn.cell1.col == move.col) ||
                     (conn.cell2.row == move.row && conn.cell2.col == move.col)) {
                  connToDelete = conn;
                  break;
               }
            }
            // If a connection to delete was found, remove it from the connections
            if (connToDelete != null) {
               connections.remove(connToDelete);
            }

            // printBoardWithAdjacencies(move.row, move.col);

            // Switch the current player
            currentPlayer = currentPlayer == Board.PLAYER_0 ? Board.PLAYER_1 : Board.PLAYER_0; // ==0?
         }

         moveHistory.add(move);
         recalculatebridgeGroups();
         // printbridgeGroupsDetailed();

         int gameState = getValue();
         if (gameState == WIN || gameState == -WIN) {
            isGameOver = true;
         }
      }

      public List<? extends Move> getValidMoves() {
         // Create a new list to store the valid moves
         List<beehiveMove> validMoves = new ArrayList<beehiveMove>();

         // Iterate over the entire board
         for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
               // If the current cell is empty (player == 0), add a new move to this cell to
               // the list of valid moves
               if (board[i][j].player == 0) {
                  validMoves.add(new beehiveMove(i, j));
               }
            }
         }

         // If only one move has been made so far, add a swap move to the list of valid
         // moves
         if (moveHistory.size() == 1) {
            validMoves.add(new beehiveMove(true));
         }

         // If the current game state is a win for either player, clear the list of valid
         // moves
         if (getValue() == WIN || getValue() == -WIN) {
            validMoves.clear();
         }

         // Return the list of valid moves
         return validMoves;
      }

      public int getCurrentPlayer() {
         return currentPlayer == Board.PLAYER_0 ? Board.PLAYER_0 : Board.PLAYER_1;
      }

      public List<? extends Move> getMoveHistory() {
         return moveHistory;
      }

      // This method undoes the most recent move made on the board.
      // If no moves have been made, the method does nothing.
      // If the most recent move was a swap move, the method sets the player of the
      // first move to 1.
      // If the most recent move was a regular move, the method sets the player of the
      // cell to 0.
      // The method then recalculates the bridge groups on the board and checks if the
      // game is over.
      // If the game is over, the method sets the isGameOver flag to true.

      public void undoMove() {
         // Check if the move history is empty. If it is, there are no moves to undo, so
         // return.
         if (moveHistory.isEmpty()) {
            return;
         }

         // Remove the last move from the move history and create a copy of it.
         beehiveMove lastMove = moveHistory.remove(moveHistory.size() - 1);
         beehiveMove move = new beehiveMove(lastMove);

         // If the last move was a swap move, reset the hasSwapped flag.
         if (lastMove.isSwap) {
            hasSwapped = false;

            // If there are still moves in the move history, get the first move.
            if (!moveHistory.isEmpty()) {
               beehiveMove firstMove = moveHistory.get(0);
               // If the first move's row and column are within the board's boundaries,
               // set the player of the corresponding cell to 1.
               if (firstMove.row >= 0 && firstMove.row <= BOARD_SIZE && firstMove.col >= 0
                     && firstMove.col <= BOARD_SIZE) {
                  board[firstMove.row][firstMove.col].player = 1;
               }
            }
         } else {
            // If the last move was not a swap move and its row and column are within the
            // board's boundaries,
            // set the player of the corresponding cell to 0.
            if (move.row >= 0 && move.row <= BOARD_SIZE && move.col >= 0 && move.col <= BOARD_SIZE) {
               board[move.row - 0][move.col - 0].player = 0;
            }
         }

         // Switch the current player.
         currentPlayer = currentPlayer == Board.PLAYER_0 ? Board.PLAYER_1 : Board.PLAYER_0;

         // Recalculate the bridge groups on the board.
         // recalculatebridgeGroups();

         // Reset the isGameOver flag to false.
         isGameOver = false;

         // Get the current game state.
         int gameState = getValue();

         // If the game state indicates a win for either player, set the isGameOver flag
         // to true.
         if (gameState == WIN || gameState == -WIN) {
            isGameOver = true;
         }
      }

      /**
       * This method calculates and returns the current value of the game board.
      * The value is determined by the difference in total weights of the players'
      * bridge groups.
      * If a winning bridge group is found, the method returns a winning value for
      * the respective player.
      * 
      * @return the current value of the game board.
      */
      public int getValue() {
         // Recalculate bridge groups
         recalculatebridgeGroups();

         // Check for a winning group
         bridgeGroup winningGroup = getWinningGroup();
         if (winningGroup != null) {
            // Return the appropriate winning value based on the player
            if (winningGroup.getOwner() == PLAYER_0) {
               return WIN;
            } else {
               return -WIN;
            }
         }

         // Calculate total weights for each player
         calculateTotalWeights();

         // Return the difference in total weights if no winning group is found
         return totalWeightPlayer2 - totalWeightPlayer1;
      }

      public void recalculatebridgeGroups() {
         // Reset visited and group properties of all cells
         for (cell[] row : board) {
            for (cell cell : row) {
               cell.visited = false;
               cell.group = null;
            }
         }

         // Clear existing bridge groups
         bridgeGroups.clear();

         // Traverse the board and form new bridge groups
         for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
               cell cell = board[row][col];
               if (!cell.visited && cell.player != 0) {
                  bridgeGroup group = new bridgeGroup();
                  dfsFindGroups(cell, group, cell.player, new ArrayList<>());
                  bridgeGroups.add(group);
               }
            }
         }
      }

      private void dfsFindGroups(cell cell, bridgeGroup group, int player, List<bridgeGroup> encounteredGroups) {
         // Check if the cell is already visited
         if (cell.visited) {
            return;
         }

         // Mark the cell as visited
         cell.visited = true;

         // Add the cell to the bridge group if it belongs to the current player
         if (cell.player == player) {
            group.add(cell);
            cell.group = group;

            // Check if the cell belongs to a different bridge group
            if (cell.group != null && cell.group != group) {
               encounteredGroups.add(cell.group);
            }
         }

         // Recursively visit adjacent cells
         for (location loc : cell.adjacentcells) {
            cell adjacentcell = board[loc.row][loc.col];
            if (!adjacentcell.visited && adjacentcell.player == player) {
               dfsFindGroups(adjacentcell, group, player, encounteredGroups);
            }
         }

         // Recursively visit bridged cells
         for (location loc : cell.bridgedcells) {
            cell bridgedcell = board[loc.row][loc.col];
            if (!bridgedcell.visited && bridgedcell.player == player && isValidbridge(cell, bridgedcell)) {
               dfsFindGroups(bridgedcell, group, player, encounteredGroups);
            }
         }

         // Merge encountered bridge groups if necessary
         if (encounteredGroups.size() > 1) {
            bridgeGroup mergedGroup = encounteredGroups.get(0);
            for (int i = 1; i < encounteredGroups.size(); i++) {
               bridgeGroup groupToMerge = encounteredGroups.get(i);
               for (cell c : groupToMerge.cells) {
                  mergedGroup.add(c);
                  c.group = mergedGroup;
               }
               bridgeGroups.remove(groupToMerge);
            }
         }
      }

      private boolean isValidbridge(cell cell1, cell cell2) {
         // Check if the cells belong to the same player and are not adjacent
         if (cell1.player != cell2.player || cell1.adjacentcells.contains(cell2.location)) {
            return false;
         }

         // Check if the cells between the bridge are empty
         List<location> betweenlocations = getlocationsBetween(cell1.location, cell2.location);
         for (location loc : betweenlocations) {
            if (loc.row >= 0 && loc.row < BOARD_SIZE && loc.col >= 0 && loc.col < BOARD_SIZE) {
               cell betweencell = board[loc.row][loc.col];
               if (betweencell.player != 0) {
                  return false;
               }
            }
         }

         return true;
      }

      private List<location> getlocationsBetween(location loc1, location loc2) {
         List<location> betweenlocations = new ArrayList<>();

         // Calculate the differences in row and column between the two locations
         int rowDiff = loc2.row - loc1.row;
         int colDiff = loc2.col - loc1.col;

         // Determine the step sizes for row and column based on the differences
         int rowStep = (rowDiff != 0) ? rowDiff / Math.abs(rowDiff) : 0;
         int colStep = (colDiff != 0) ? colDiff / Math.abs(colDiff) : 0;

         // Iterate from loc1 to loc2 and add the locations in between
         int row = loc1.row + rowStep;
         int col = loc1.col + colStep;
         while (row != loc2.row || col != loc2.col) {
            betweenlocations.add(new location(row, col));
            row += rowStep;
            col += colStep;
         }

         return betweenlocations;
      }

      private void calculateTotalWeights() {
         // Reset total weights for each player
         totalWeightPlayer1 = 0;
         totalWeightPlayer2 = 0;

         // Iterate over bridge groups and calculate weights
         for (bridgeGroup group : bridgeGroups) {
            if (group.cells.isEmpty()) {
               continue;
            }

            // Determine the owner of the bridge group
            int owner = group.cells.get(0).player;

            // Calculate the weight of the bridge group
            int weight = group.getWeight(owner);

            // Add the weight to the respective player's total weight
            if (owner == PLAYER_0) {
               totalWeightPlayer1 += weight;
            } else if (owner == PLAYER_1) {
               totalWeightPlayer2 += weight;
            }
         }
      }

      private bridgeGroup getWinningGroup() {
         // Iterate over bridge groups and check for a winning group
         for (bridgeGroup group : bridgeGroups) {
            if (isWinningGroup(group)) {
               return group;
            }
         }

         // No winning group found
         return null;
      }

      private boolean isWinningGroup(bridgeGroup group) {
         // Check if the group spans the entire board horizontally or vertically
         int owner = group.cells.get(0).player;
         int span = group.getSpan(owner);

         if (owner == PLAYER_0 && span == BOARD_SIZE) {
            // Player 0 wins if the group spans the entire board horizontally
            return true;
         } else if (owner == PLAYER_1 && span == BOARD_SIZE) {
            // Player 1 wins if the group spans the entire board vertically
            return true;
         }

         // Not a winning group
         return false;
      }

      private void updatebridgeGroups(cell newcell) {
         // Create a new bridge group for the new cell
         bridgeGroup newGroup = new bridgeGroup();
         newGroup.add(newcell);
         newcell.group = newGroup;

         // Perform DFS traversal starting from the new cell
         List<bridgeGroup> encounteredGroups = new ArrayList<>();
         dfsFindGroups(newcell, newGroup, newcell.player, encounteredGroups);

         // Merge encountered bridge groups if necessary
         if (encounteredGroups.size() > 1) {
            bridgeGroup mergedGroup = encounteredGroups.get(0);
            for (int i = 1; i < encounteredGroups.size(); i++) {
               bridgeGroup groupToMerge = encounteredGroups.get(i);
               for (cell cell : groupToMerge.cells) {
                  mergedGroup.add(cell);
                  cell.group = mergedGroup;
               }
               bridgeGroups.remove(groupToMerge);
            }
         }

         // Check if the new bridge group is a winning group
         if (isWinningGroup(newcell.group)) {
            winningGroup = newcell.group;
            isGameOver = true;
         }
      }

      private String getLabel(int number) {
         if (number < 9) {
            return String.valueOf(number + 1);
         } else if (number == 9) {
            return "T";
         } else {
            return "E";
         }
      }

      private boolean[][] findWinningPath() {
         boolean[][] winningPath = new boolean[BOARD_SIZE][BOARD_SIZE];
         for (int row = 0; row < BOARD_SIZE; row++) {
            if (board[row][0].player == PLAYER_0 && !winningPath[row][0]) {
               if (dfsFindPath(board[row][0], PLAYER_0, winningPath)) {
                  break; // Found the winning path, no need to continue.
               }
            }
         }
         return winningPath;
      }

      private boolean dfsFindPath(cell cell, int player, boolean[][] path) {
         int row = cell.row;
         int col = cell.col;

         if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE || board[row][col].player != player
               || path[row][col]) {
            return false;
         }

         path[row][col] = true; // Mark the cell as part of the path

         if (player == PLAYER_0 && col == BOARD_SIZE - 1) { // Win condition for PLAYER_0
            return true;
         }

         // Explore all six directions
         boolean found = dfsFindPath(new cell(row - 1, col), player, path) ||
               dfsFindPath(new cell(row + 1, col), player, path) ||
               dfsFindPath(new cell(row, col - 1), player, path) ||
               dfsFindPath(new cell(row, col + 1), player, path) ||
               dfsFindPath(new cell(row - 1, col + 1), player, path) ||
               dfsFindPath(new cell(row + 1, col - 1), player, path);

         if (!found) {
            path[row][col] = false; // Unmark if not part of the winning path
         }

         return found;
      }

      private char getcellSymbol(int row, int col) {
         // Check the player attribute of the cell object
         if (board[row][col].player == Board.PLAYER_0) {
            return 'b'; // Symbol for player 0
         } else if (board[row][col].player == Board.PLAYER_1) {
            return 'r'; // Symbol for player 1
         }
         return '.'; // Symbol for an empty cell
      }

      @Override
      public String toString() {
         StringBuilder sb = new StringBuilder();
         sb.append("  ");
         for (int col = 0; col < BOARD_SIZE; col++) {
            sb.append(" ").append(getLabel(col));
         }
         sb.append("\n");

         for (int row = 0; row < BOARD_SIZE; row++) {
            for (int space = 0; space < row; space++) {
               sb.append(" ");
            }
            sb.append(getLabel(row)).append(" ");
            for (int col = 0; col < BOARD_SIZE; col++) {
               char symbol = getcellSymbol(row, col);
               if (winningGroup != null && winningGroup.contains(board[row][col])) {
                  symbol = Character.toUpperCase(symbol);
               }
               sb.append(symbol).append(" ");
            }
            sb.append("\n");
         }

         if (isGameOver && winningGroup != null) {
            if (winningGroup.getOwner() == PLAYER_0) {
               sb.append("Player 1 (Blue) wins!");
            } else if (winningGroup.getOwner() == PLAYER_1) {
               sb.append("Player 2 (Red) wins!");
            }
         }
         System.out.println(board.toString()); // This will print the current board state in the test

         return sb.toString();
      }

      public void printbridgeGroups() {
         for (bridgeGroup bg : bridgeGroups) {
            System.out.println("bridge group owned by player " + bg.cells.get(0).player + " with span "
                  + bg.getSpan(bg.cells.get(0).player));
            for (cell c : bg.cells) {
               System.out.println("cell at (" + (c.row + 1) + ", " + (c.col + 1) + ")");
            }
         }
      }

      public void printBoardWithAdjacencies(int row, int col) {
         cell selectedcell = board[row][col];
         selectedcell.computeAdjacentcells();
         selectedcell.computebridgedcells();

         // Debug output for adjacent cells
         System.out.println("Adjacent cells to (" + (row + 1) + ", " + (col + 1) +
               "):");
         for (location adjcell : selectedcell.adjacentcells) {
            if (adjcell != null) {
               System.out.println("Adjacent cell at (" + (adjcell.row + 1) + ", " +
                     (adjcell.col + 1) + ")");
            }
         }

      }

      public void printbridgeGroupsDetailed() {
         System.out.println("Current bridge Groups:");
         for (bridgeGroup bg : bridgeGroups) {
            System.out.println("--------------------------------");
            System.out.println("Owned by Player: " + (bg.cells.isEmpty() ? "None" : bg.cells.get(0).player));
            System.out.println("Group Span: " + bg.getSpan(bg.cells.isEmpty() ? 0 : bg.cells.get(0).player));
            System.out.println("cells in Group:");
            for (cell c : bg.cells) {
               System.out.println("cell at (" + (c.row) + ", " + (c.col) + ")"); // Adjusting for 1-based
                                                                                 // indexing
            }
         }
         System.out.println("--------------------------------");
      }

   }
