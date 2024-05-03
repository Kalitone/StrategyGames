package edu.principia.MBrad.OODesign.StrategyGames.beehive;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import edu.principia.OODesign.StrategyGames.Board;
public class beehiveMove implements Board.Move, Serializable{
// Attributes to store the move's row and column, and a flag for the swap move
int row;
int col;
boolean isSwap;
int previousOwner;

// Default constructor for special cases like a swap move or when a move is yet
// to be defined
public beehiveMove() {
   this.row = -1; // Indicates an uninitialized state or a special move (like swap)
   this.col = -1;
   this.isSwap = false; // Default to false, indicating a normal move
}

// Parameterized constructor for standard moves with specified row and column
public beehiveMove(int row, int col) {
   this.row = row;
   this.col = col;
   this.isSwap = false; // Initialized as a regular move, not a swap
}

public beehiveMove(boolean isSwap) { // Specific constructor for swap
   this.row = -1;
   this.col = -1;
   this.isSwap = isSwap;
}

public beehiveMove(beehiveMove move) { // Copy constructor
   this.row = move.row;
   this.col = move.col;
   this.isSwap = move.isSwap;
   previousOwner = move.previousOwner;
}

public int getPreviousOwner() {
   return previousOwner;
}

public void setPreviousOwner(int previousOwner) {
   this.previousOwner = previousOwner;
}

// Getter for row
public int getRow() {
   return row;
}

// Getter for column
public int getCol() {
   return col;
}

// Setter for the swap flag
public void setSwap(boolean isSwap) {
   this.isSwap = isSwap;
}

// Getter for the swap flag
public boolean isSwap() {
   return isSwap;
}

// Implement compareTo to fulfill the Comparable interface requirement
// Check if the object is an instance of beehiveMove
// Handle moves of other types
// If this move is a swap and the other is not, this move is considered less
// If this move is not a swap and the other is, this move is considered greater
// If both moves are swaps, they are considered equal
// If this move's row is less than the other's, this move is considered less
// If this move's row is greater than the other's, this move is considered greater
// If this move's column is less than the other's, this move is considered less
// If this move's column is greater than the other's, this move is considered greater
// If none of the above conditions are met, the moves are considered equal

@Override
public int compareTo(Board.Move o) {
   if (!(o instanceof beehiveMove)) {
      // ...
   }
   // Cast the object to beehiveMove
   beehiveMove other = (beehiveMove) o;

   if (this.isSwap && !other.isSwap) {
      return -1;
   }
   else if (!this.isSwap && other.isSwap) {
      return 1;
   }
   else if (this.isSwap && other.isSwap) {
      return 0;
   }
   else if (this.row < other.row) {
      return -1;
   }
   else if (this.row > other.row) {
      return 1;
   }
   else if (this.col < other.col) {
      return -1;
   }
   
   else if (this.col > other.col) {
      return 1;
   } else {
      return 0;
   }
}

// Override toString for a meaningful string representation of the move
@Override
public String toString() {
   if (isSwap) {
      return "Swap Move"; // Representation for a swap move
   }
   return "Move to (" + (row+1) + ", " + (col+1) + ")"; // Representation for a regular move
}

// Implement additional required methods from the Board.Move interface
@Override
public void write(OutputStream os) throws IOException {
   os.write(row);
   os.write(col);
   os.write(isSwap ? 1 : 0);
}

@Override
public void read(InputStream is) throws IOException {
   this.row = is.read();
   this.col = is.read();
   this.isSwap = is.read() == 1;
}

@Override
public void fromString(String s) {
    // If the string is "swap", set the isSwap attribute to true
    if ("swap".equalsIgnoreCase(s)) {
        this.isSwap = true;
    } else {
        // Split the string into parts using the comma as a delimiter
        String[] parts = s.split(",");
        // If the parts array does not have exactly 2 elements, throw an exception
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid move format: " + s);
        }
        // Parse the row and column from the parts array, adjusting for 0-based indexing
        this.row = Integer.parseInt(parts[0]) -1;
        this.col = Integer.parseInt(parts[1]) -1;
        // Since this is not a swap move, set the isSwap attribute to false
        this.isSwap = false;
    }
}

}


