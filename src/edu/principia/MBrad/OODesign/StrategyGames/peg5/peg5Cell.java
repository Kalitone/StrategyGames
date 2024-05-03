package edu.principia.MBrad.OODesign.StrategyGames.peg5;

public class peg5Cell {
   // Bit patterns for green and yellow pegs and tubes
   public static final byte GREEN_PEG = 0b0001;
   public static final byte GREEN_OPEN_TUBE = 0b0010;
   public static final byte GREEN_CLOSED_TUBE = 0b0100;
   public static final byte YELLOW_PEG = 0b1000;
   public static final byte YELLOW_OPEN_TUBE = 0b0001_0000;
   public static final byte YELLOW_CLOSED_TUBE = 0b0010_0000;
   public static final byte EMPTY = 0b0000;

   private int contents; // Bitwise representation of the cell contents

   // Constructor
   public peg5Cell() {
       contents = 0;
   }

   // Get the contents of the cell
   public int getContents() {
       return contents;
   }

   // Set the contents of the cell
   public void setContents(int contents) {
       this.contents = contents;
   }
   public boolean isEmpty() {
    return contents == 0;
}

   // Check if the cell contains a green peg
   public boolean hasGreenPeg() {
       return (contents & GREEN_PEG) != 0;
   }

   // Check if the cell contains a green open tube
   public boolean hasGreenOpenTube() {
       return (contents & GREEN_OPEN_TUBE) != 0;
   }

   // Check if the cell contains a green closed tube
   public boolean hasGreenClosedTube() {
       return (contents & GREEN_CLOSED_TUBE) != 0;
   }

   // Check if the cell contains a yellow peg
   public boolean hasYellowPeg() {
       return (contents & YELLOW_PEG) != 0;
   }

   // Check if the cell contains a yellow open tube
   public boolean hasYellowOpenTube() {
       return (contents & YELLOW_OPEN_TUBE) != 0;
   }

   // Check if the cell contains a yellow closed tube
   public boolean hasYellowClosedTube() {
       return (contents & YELLOW_CLOSED_TUBE) != 0;
   }

   // Set the presence of a green peg in the cell
   public void setGreenPeg() {
       contents |= GREEN_PEG;
   }

   // Set the presence of a green open tube in the cell
   public void setGreenOpenTube() {
       contents |= GREEN_OPEN_TUBE;
   }

   // Set the presence of a green closed tube in the cell
   public void setGreenClosedTube() {
       contents |= GREEN_CLOSED_TUBE;
   }

   // Set the presence of a yellow peg in the cell
   public void setYellowPeg() {
       contents |= YELLOW_PEG;
   }

   // Set the presence of a yellow open tube in the cell
   public void setYellowOpenTube() {
       contents |= YELLOW_OPEN_TUBE;
   }

   // Set the presence of a yellow closed tube in the cell
   public void setYellowClosedTube() {
       contents |= YELLOW_CLOSED_TUBE;
   }

   // Remove the green peg from the cell
   public void removeGreenPeg() {
       contents &= ~GREEN_PEG;
   }

   // Remove the green open tube from the cell
   public void removeGreenOpenTube() {
       contents &= ~GREEN_OPEN_TUBE;
   }

   // Remove the green closed tube from the cell
   public void removeGreenClosedTube() {
       contents &= ~GREEN_CLOSED_TUBE;
   }

   // Remove the yellow peg from the cell
   public void removeYellowPeg() {
       contents &= ~YELLOW_PEG;
   }

   // Remove the yellow open tube from the cell
   public void removeYellowOpenTube() {
       contents &= ~YELLOW_OPEN_TUBE;
   }

   // Remove the yellow closed tube from the cell
   public void removeYellowClosedTube() {
       contents &= ~YELLOW_CLOSED_TUBE;
   }

   // Check if a peg can be placed in the cell for the given player
   public boolean canPlacePeg(int player) {
       if (player == peg5Board.PLAYER_0) {
           return (contents & (GREEN_PEG | GREEN_OPEN_TUBE)) == 0;
       } else {
           return (contents & (YELLOW_PEG | YELLOW_OPEN_TUBE)) == 0;
       }
   }

   public boolean canPlaceTube(int player) {
       if (player == peg5Board.PLAYER_0) {
           return (contents & (GREEN_OPEN_TUBE | GREEN_CLOSED_TUBE | YELLOW_CLOSED_TUBE)) == 0;
       } else {
           return (contents & (YELLOW_OPEN_TUBE | YELLOW_CLOSED_TUBE | GREEN_CLOSED_TUBE)) == 0;
       }
   }

   public boolean hasPiece(int player) {
       if (player == peg5Board.PLAYER_0) {
           return hasGreenPeg() || hasGreenOpenTube() || hasGreenClosedTube();
       } else {
           return hasYellowPeg() || hasYellowOpenTube() || hasYellowClosedTube();
       }
   }

   @Override
   public String toString() {
       String contentsDescription;

       if (hasYellowPeg()) {
           contentsDescription = "Yellow Peg";
       } else if (hasYellowOpenTube()) {
           contentsDescription = "Yellow Open Tube";
       } else if (hasYellowClosedTube()) {
           contentsDescription = "Yellow Closed Tube";
       } else if (hasGreenPeg()) {
           contentsDescription = "Green Peg";
       } else if (hasGreenOpenTube()) {
           contentsDescription = "Green Open Tube";
       } else if (hasGreenClosedTube()) {
           contentsDescription = "Green Closed Tube";
       } else {
           contentsDescription = "Empty";
       }

       return contentsDescription;
   }

}