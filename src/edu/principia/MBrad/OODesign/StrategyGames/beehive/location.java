package edu.principia.MBrad.OODesign.StrategyGames.beehive;

import java.io.Serializable;
import java.util.Objects;
public class location implements Serializable{
 // The row and colum imn coordinates of the location.
 public int row;
 public int col;
 public int BOARD_SIZE = 11;

 // Constructs a new location with the specified row and column.
 public location(int row, int col) {
     this.row = row;
     this.col = col;
 }
 public location(location loc) {
     this.row = loc.row;
     this.col = loc.col;
 }

 // Adds the row and column of another location to this location's row and column,
 // and returns a new location with the result.
 public location add(location other) {
     return new location(row + other.row, col + other.col);
 }

 // Checks if this location is within the bounds of the BeehiveBoard.
 public boolean isInBounds() {
     return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE;
 }

 // Checks if this location is equal to another object.
 // Two locations are equal if they have the same row and column.
 @Override
 public boolean equals(Object obj) {
     if (this == obj) {
         return true;
     }
     if (obj == null || getClass() != obj.getClass()) {
         return false;
     }
     location other = (location) obj;
     return row == other.row && col == other.col;
 }

 @Override
 public String toString() {
     return "(" + row + ", " + col + ")";
 }


 // Returns a hash code for this location.
 @Override
 public int hashCode() {
     return Objects.hash(row, col);
 }
}
