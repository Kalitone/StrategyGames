package edu.principia.MBrad.OODesign.StrategyGames.beehive;

import java.io.Serializable;

public class connection implements Serializable {
   cell cell1;
   cell cell2;

   public connection(cell c1, cell c2) {
          this.cell1 = c1;
          this.cell2 = c2;
      }

}
