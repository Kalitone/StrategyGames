package edu.principia.OODesign.StrategyGames;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.Queue;

import edu.principia.OODesign.StrategyGames.Board.InvalidMoveException;

public class Tournament {
   // Resutlt odf one two player competition, showing which player won
   // and the moves made by each player
   public static class CmpResult {
      Board winner;
      Board loser;

   }

   // Run a competition between two boards, whith brd0 playing ass player_0 and
   // brd1 as player_1
   // use AIsolver for each move. Alternate between the two boards, with one
   // board determining its move via AISolver and he ohter board being given the
   // same move so that the two board track together.Each board initially starts at
   // elve maxLevel and the time allowed for each move is time permove. However id
   // a board has been taken an average of more than the timepermove for its moves,
   // its level is reduced by ine each move(but never < 1) unitl its average time
   // per move is less than timepermove, whereuoon its level is increased byome
   // each move (but never > init level). time used is measured in nanoseconds via
   // ThreadMXbean.getCurrentThreadCpuTime().Timepermove is in seconnds

   // The competition ends when AIplayer.minimax returns a null bestmove. the
   // returned value from AIplayer.minimax determinves who has won or if there is a
   // fraw results, the winner is theh board with the lowers average time per move

   // return the winner and loser of the competition as a CmpResult.
   static CmpResult runCompetition(Board brd0, Board brd1, double timePerMove, int maxLevel)
         throws InvalidMoveException {
      CmpResult result = new CmpResult();

      ThreadMXBean bean = ManagementFactory.getThreadMXBean();
      Board boards[] = { brd0, brd1 };
      int levels[] = { maxLevel, maxLevel };
      double times[] = { 0.0, 0.0 };
      int currentPlayer = 0;
      CmpResult cmpResult = new CmpResult();

      while (true) {
         long start = bean.getCurrentThreadCpuTime();
         AISolver.MMResult res = new AISolver.MMResult();
         AISolver.miniMax(boards[currentPlayer], Integer.MIN_VALUE, Integer.MAX_VALUE, levels[currentPlayer], res);

         long end = bean.getCurrentThreadCpuTime();
         double time = (end - start) / 1e9;
         times[currentPlayer] += time - timePerMove;

         if (times[currentPlayer] > 0) {
            levels[currentPlayer] = Math.max(1, levels[currentPlayer] - 1);
         } else if (times[currentPlayer] < 0) {
            levels[currentPlayer] = Math.min(maxLevel, levels[currentPlayer] + 1);
         }
         if (res.move == null) {
            // pick winner based on res.score or break tie based on time
            if (res.value == 0) {
               result.winner = times[0] < times[1] ? boards[0] : boards[1];
               result.loser = times[0] < times[1] ? boards[1] : boards[0];
            } else {
               result.winner = res.value > 0 ? boards[0] : boards[1];
               result.loser = res.value > 0 ? boards[1] : boards[0];
            }
            break;
         } else {
            try {
               boards[currentPlayer].applyMove(res.move);
               boards[1 - currentPlayer].applyMove(res.move);
            } catch (Board.InvalidMoveException e) {
            }
         }
         currentPlayer = 1 - currentPlayer;
      }
      return result;

   }

   static private class CmpNode {
      String name;
      Board brd;
      CmpNode left;
      CmpNode right;

      public CmpNode(String name, Board brd) {
         this.name = name;
         this.brd = brd;
      }

      // run a competition between left and right, setting brd to the winner and name
      // to the name of the winner
      public CmpNode(CmpNode left, CmpNode right, double timePerMove, int maxLevel) throws InvalidMoveException {
         this.left = left;
         this.right = right;
         CmpResult res = runCompetition(left.brd, right.brd, timePerMove, maxLevel);
         this.brd = res.winner;
         this.name = res.winner == left.brd ? left.name : right.name;
      }
   }

   // Run a tournament between players specified in a players file with time per
   // move and
   // max and max levle specifiec the command line args are name of the file , time
   // per move(as a double)
   // The file format is a sequence of lines, each with a name anf a class name.
   // Classes must implement the board interface for each lnies,
   // create a new instance of the class gereate a board from it and consstruxt a
   // leaf Cmpnode with the name and board

   // Add each new cmp to queue of cmpnodes. then repeatedly pair up the nodes
   // creating a new cmpnode with the winner of the competition
   // between the two nodes and adding this new node to the end of the list.
   // Continue until there is only one unpaired node in the queue which is the
   // winner of the tournament and the root of the tree
   // for each pairwise competition, output a lnie giving the two names and the
   // name of the winner.Finanlly output the name of the winner of the tournament

   public static void main(String[] args)
            throws InvalidMoveException, InstantiationException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
        if (args.length != 3) {
            System.err.println("Usage: java Tournament playersFile timePerMove maxLevel");
            return;
        }

        double timePerMove = Double.parseDouble(args[1]);
        int maxLevel = Integer.parseInt(args[2]);

        Queue<CmpNode> queue = new LinkedList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(args[0]));
            String line;
            int lineNumber = 1;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                try {
                    Class<?> cls = Class.forName(parts[1]);
                    Board brd = (Board) cls.getConstructor().newInstance();
                    queue.add(new CmpNode(parts[0], brd));
                    System.out.println("Constructed: " + line);
                } catch (ClassNotFoundException e) {
                    System.err.println("Class not found: " + parts[1] + " at line " + lineNumber);
                } catch (NoSuchMethodException e) {
                    System.err.println("No constructor found for class " + parts[1] + " at line " + lineNumber);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    System.err.println("Failed to create instance of class: " + parts[1] + " at line " + lineNumber);
                } catch (ClassCastException e) {
                    System.err.println("Class does not implement Board: " + parts[1] + " at line " + lineNumber);
                }
                lineNumber++;
            }
            br.close();
        } catch (IOException e) {
            System.err.println("Error reading file: " + e);
            System.exit(1);
        }

        while (queue.size() > 1) {
            CmpNode left = queue.remove();
            CmpNode right = queue.remove();
            CmpNode winner = new CmpNode(left, right, timePerMove, maxLevel);
            System.out.printf("%s vs %s\n", left.name, right.name, winner.name);
            queue.add(winner);
        }

        System.out.println("Winner: " + queue.remove().name);
    }
   }
    