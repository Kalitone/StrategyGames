package edu.principia.MBrad.OODesign.StrategyGames.beehive;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
public class cell implements Serializable {
   
   public int row;
   public int col;
   public int owner;
   public location location;
   public int player;
   public int bridgeGroup;
   public List<location> adjacentcells;
   public List<location> bridgedcells;
   bridgeGroup group;
   beehiveBoard board;
   boolean visited = false;
   int offset = 6;

       // Define an array of location objects representing the offsets for adjacent cells.
       // Each location object in the array represents a direction from the current cell to an adjacent cell.
       // For example, new location(0, 1) represents an offset to the cell directly to the right of the current cell.
       public location[] adjacentOffeset = new location[] {
           new location(0, 1),  // Right
           new location(1, 0),  // Down
           new location(1, -1), // Down-left
           new location(0, -1), // Left
           new location(-1, 0), // Up
           new location(1, -1)  // Up-right
       };

   // Define an array of location objects representing the offsets for bridged cells.
   // Each location object in the array represents a direction from the current cell to a bridged cell.
   // For example, new location(-1, -1) represents an offset to the cell diagonally up-left of the current cell.
   public location[] bridgedOffeset = new location[] {
       new location(-1, -1), // Up-left
       new location(-1, 2),  // Up-right
       new location(-2, 1),  // Left-up
       new location(1, -2),  // Down-left
       new location(1, 1),   // Down-right
       new location(2, -1)   // Right-down
   };

   // Constructor for the cell class
   // Takes in two parameters: the row and column of the cell
   public cell(int r, int c) {
       // Set the row and column of the cell
       row = r;
       col = c;
       
       // Initialize the player to 0 (no player)
       player = 0;
       
       // Create a new location object for the cell's location
       location = new location(r, c);
       
       // Initialize the lists of adjacent and bridged cells
       adjacentcells = new ArrayList<location>();
       bridgedcells = new ArrayList<location>();
   }


   // Method to compute and store the adjacent cells to the current cell
   public void computeAdjacentcells() {
       // Clear the list of adjacent cells
       adjacentcells.clear();

       // Loop through each of the 6 possible adjacent offsets
       for (int i = 0; i < offset; i++) {
           // Create a new location based on the current cell's location and the offset
           location newlocation = new location(row, col).add(adjacentOffeset[i]);

           // Check if the new location is within the bounds of the board
           if (newlocation.isInBounds()) {
               // If it is, add it to the list of adjacent cells
               adjacentcells.add(newlocation);
           }
       }
   }

   public boolean contains(cell cell) {
       return (row == cell.row && col == cell.col);
   }

   // This method is used to compute the locations of cells adjacent to the current cell.
   // It creates a new list of location objects, then iterates through the array of adjacent offsets.
   // For each offset, it creates a new location object by adding the offset to the current cell's location.
   // If the new location is within the bounds of the board, it is added to the list of adjacent locations.
   // The method then returns the list of adjacent locations.
   public List<location> computeAdjacentlocations() {
       List<location> adjacentlocations = new ArrayList<location>();
       for (int i = 0; i < offset; i++) {
           location newlocation = new location(row, col).add(adjacentOffeset[i]);
           if (newlocation.isInBounds()) {
               adjacentlocations.add(newlocation);
           }
       }
       return adjacentlocations;
   }


   // This method is used to compute the locations of cells that can be bridged to from the current cell.
   public void computebridgedcells() {
       // Clear the list of bridged cells
       bridgedcells.clear();

       // Loop through each of the 6 possible bridged offsets
       for (int i = 0; i < offset; i++) {
           // Create a new location based on the current cell's location and the offset
           location newlocation = new location(row, col).add(bridgedOffeset[i]);

           // Check if the new location is within the bounds of the board
           if (newlocation.isInBounds()) {
               // If it is, add it to the list of bridged cells
               bridgedcells.add(newlocation);
           }
       }
   }

   public void setOwner(int newOwner) {
       owner = newOwner;
      // System.out.println("cell at" + (location.row + 1) + ", " +
               //(location.col + 1) + " is owned by " + newOwner);

       if (player == 0) {
           player = 1; 
       } else {
           player = -1;
       }
   }

   public int getOwner() {
       return owner;
   }

   @Override
   public String toString() {
       return "cell at (" + location.row + ", " + location.col + ")"; 
   }

}