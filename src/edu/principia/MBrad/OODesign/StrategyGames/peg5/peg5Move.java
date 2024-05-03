package edu.principia.MBrad.OODesign.StrategyGames.peg5;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;


import edu.principia.OODesign.StrategyGames.Board;
import edu.principia.OODesign.StrategyGames.Board.InvalidMoveException;

public class peg5Move implements Board.Move, Cloneable , Serializable{
   // Enum for piece types
   public static enum PieceType {
       PEG, OPEN_TUBE, CLOSED_TUBE, Empty
   }

   static PieceType PieceType;
   private int row;
   private int col;
   private int sourceRow;
   private int sourceCol;

   // Default constructor for unspecified moves
   public peg5Move() {
       this.PieceType = null; // Indicates an uninitialized state or a default move
       this.row = 0;
       this.col = 0;
       this.sourceRow = 0;
       this.sourceCol = 0;
   }

   // Constructor for initial placement of pieces
   public peg5Move(PieceType PieceType, int row, int col) {
       this.PieceType = PieceType;
       this.row = row;
       this.col = col;
       this.sourceRow = -1; // Default value indicating no source (new placement)
       this.sourceCol = -1;
   }

   // Constructor for repositioning a piece
   public peg5Move(PieceType PieceType, int row, int col, int sourceRow, int sourceCol) {
       this.PieceType = PieceType;
       this.row = row;
       this.col = col;
       this.sourceRow = sourceRow;
       this.sourceCol = sourceCol;
   }

   @Override
   public peg5Move clone() {
       try {
           peg5Move clone = (peg5Move) super.clone();
           return clone;
       } catch (CloneNotSupportedException e) {
           // This should never happen since we are implementing Cloneable
           throw new AssertionError();
       }
   }

   // Get the piece type of the move
   public PieceType getPieceType() {
       return PieceType;
   }

   // Get the target row of the move
   public int getRow() {
       return row;
   }

   // Get the target column of the move
   public int getCol() {
       return col;
   }

   // Get the source row of the move (for repositioning)
   public int getSourceRow() {
       return sourceRow;
   }

   // Get the source column of the move (for repositioning)
   public int getSourceCol() {
       return sourceCol;
   }

   // Check if the move is a repositioning move
   public boolean isRepositioningMove() {
       return sourceRow != -1 && sourceCol != -1;
   }

   // Compare moves based on their string representation
   @Override
   public int compareTo(Board.Move other) {
       return this.toString().compareTo(other.toString());
   }

   // Write the move to an OutputStream in binary format
   @Override
   public void write(OutputStream os) throws IOException {
       // Implement the binary serialization of the move

       DataOutputStream dos = new DataOutputStream(os);
       dos.writeInt(PieceType.ordinal());
       dos.writeInt(row);
       dos.writeInt(col);
       dos.writeInt(sourceRow);
       dos.writeInt(sourceCol);

       dos.flush();
   }

   // Read the move from an InputStream in binary format
   @Override
   public void read(InputStream is) throws IOException {
       // Implement the binary deserialization of the move
       DataInputStream dis = new DataInputStream(is);
       int PieceTypeOrdinal = dis.readInt();
       PieceType = PieceType.values()[PieceTypeOrdinal];

       row = dis.readInt();
       col = dis.readInt();

       sourceRow = dis.readInt();
       sourceCol = dis.readInt();
   }

   @Override
   public void fromString(String s) {
       String[] parts = s.trim().split("\\s+");
       if (parts.length < 2 || parts.length > 4) {
           try {
               throw new InvalidMoveException("Invalid move string: " + s);
           } catch (InvalidMoveException e) {
               e.printStackTrace();
           }
       }
   
       String PieceTypeString = parts[0].toLowerCase();
       switch (PieceTypeString) {
           case "peg":
               PieceType = PieceType.PEG;
               break;
           case "open":
               PieceType = PieceType.OPEN_TUBE;
               break;
           case "closed":
               PieceType = PieceType.CLOSED_TUBE;
               break;
           default:
               try {
                   throw new InvalidMoveException("Invalid piece type: " + PieceTypeString);
               } catch (InvalidMoveException e) {
                   e.printStackTrace();
               }
       }
   
       String[] destCoords = parts[1].substring(1, parts[1].length() - 1).split(",");
       if (destCoords.length != 2) {
           try {
               throw new InvalidMoveException("Invalid move string: " + s);
           } catch (InvalidMoveException e) {
               e.printStackTrace();
           }
       }
   
       try {
           row = Integer.parseInt(destCoords[0]) - 1;
           col = Integer.parseInt(destCoords[1]) - 1;
       } catch (NumberFormatException e) {
           try {
               throw new InvalidMoveException("Invalid move coordinates: " + s);
           } catch (InvalidMoveException e1) {
               e1.printStackTrace();
           }
       }
   
       if (parts.length == 4 && parts[2].equals("<-")) {
           String[] sourceCoords = parts[3].substring(1, parts[3].length() - 1).split(",");
           if (sourceCoords.length != 2) {
               try {
                   throw new InvalidMoveException("Invalid move string: " + s);
               } catch (InvalidMoveException e) {
                   e.printStackTrace();
               }
           }
   
           try {
               sourceRow = Integer.parseInt(sourceCoords[0]) - 1;
               sourceCol = Integer.parseInt(sourceCoords[1]) - 1;
           } catch (NumberFormatException e) {
               try {
                   throw new InvalidMoveException("Invalid source coordinates: " + s);
               } catch (InvalidMoveException e1) {
                   e1.printStackTrace();
               }
           }
       } else {
           sourceRow = -1;
           sourceCol = -1;
       }
   }
   // Return a string representation of the move
   @Override
   public String toString() {
       String PieceTypeString = PieceType.toString().toLowerCase();
       String moveString = PieceTypeString + " (" + (row + 1) + "," + (col + 1) + ")";
       if (isRepositioningMove()) {
           moveString += " <- (" + (sourceRow + 1) + "," + (sourceCol + 1) + ")";
       }
       return moveString;
   }
}