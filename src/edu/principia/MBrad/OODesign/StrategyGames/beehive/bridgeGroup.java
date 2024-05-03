package edu.principia.MBrad.OODesign.StrategyGames.beehive;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class bridgeGroup implements Serializable {
   // List of cells in the bridge group
   List<cell> cells;
   // List of connections between cells
   public List<connection> connections = new ArrayList<connection>();
   // Owner of the bridge group
   public int owner;
   // Minimum row index in the bridge group
   public int minRow;
   // Maximum row index in the bridge group
   public int maxRow;
   // Minimum column index in the bridge group
   public int minCol;
   // Maximum column index in the bridge group
   public int maxCol;
   public static final int PLAYER_0 = 1;
   public static final int PLAYER_1 = -1;
   // Define PLAYER_0

   // Constructor for the BridgeGroup class
   public bridgeGroup() {
      List<cell> cells;

      minCol = Integer.MAX_VALUE;
      maxCol = Integer.MIN_VALUE;
      minRow = Integer.MAX_VALUE;
      maxRow = Integer.MIN_VALUE;
   }

   // Adds a cell to the bridge group
   public void add(cell cell) {
      if (this.cells.contains(cell)) {
         throw new IllegalArgumentException("cell is already part of this bridge group");
      }
      cells.add(cell);
      cell.group = this;
      updateSpan(cell);
   }

   // Updates the span based on the cell added
   private void updateSpan(cell cell) {
      minCol = Math.min(minCol, cell.col);
      maxCol = Math.max(maxCol, cell.col);
      minRow = Math.min(minRow, cell.row);
      maxRow = Math.max(maxRow, cell.row);
   }

   // Forms a bridge between two cells
   public void formBridge(cell cell1, cell cell2) {
      if (!cells.contains(cell1) || !cells.contains(cell2)) {
         throw new IllegalArgumentException("Both cells must be part of the bridge group to form a bridge");
      }
      connections.add(new connection(cell1, cell2));
   }

   // Returns the weight of the bridge group for a given player
   public int getWeight(int player) {
      return cells.size() * getSpan(player);
   }

   // Returns the span of the bridge group for a given player
   public int getSpan(int player) {
      if (player == PLAYER_0) {
         return maxCol - minCol + 1;
      } else {
         return maxRow - minRow + 1;
      }
   }

   // Returns the owner of the bridge group
   public int getOwner() {
      return owner;
   }

   public boolean contains(cell cell) {
      return cells.contains(cell);
   }

}
