import java.util.*;
public class mfBot extends ReversiBot{

   public int evaluate(char[][] board, char player) {
      Board brd = new Board(board, player);
      brd.generateChildren();
      Integer[] othersMovesEvals = new Integer[brd.children.length];
      for (int i = 0; i < brd.children.length; i++){
         othersMovesEvals[i] = stepEval(fromBoard(brd.children[i]), getInversePiece(player));
      }

      int evalForBestMove = Collections.max(Arrays.asList(othersMovesEvals));
      return -1 * evalForBestMove;

   }


   
   private static char[][] curBoard;
   private static char curPlayer;
   
   private int stepEval(char[][] board, char player){
      curBoard = board;
		curPlayer = player;
		int evaluation = 0;
		//non-edge pieces:
		for(int i=1;i<5;i++) {
			for(int j=1;j<5;j++) {
				evaluation += getValue(i,j);
			}
		}
		//edge pieces:
		for(int i=1;i<5;i++) {
			evaluation += 5*getValue(0,i);	//top row
			evaluation += 5*getValue(5,i);	//bottom row
			evaluation += 5*getValue(i,0);	//left column
			evaluation += 5*getValue(i,5);	//right column
		}
		evaluation += 100*getValue(0,0);
		evaluation += 100*getValue(0,5);
		evaluation += 100*getValue(5,0);
		evaluation += 100*getValue(5,5);
		return evaluation;
   }

   private int getValue(int row, int col) {
		if(curBoard[row][col]==curPlayer)
			return 1;
		if(curBoard[row][col]=='.')
			return 0;
		return -1;
	}


   public String getHumanName() {
      return "Matthew Freestone";
   }

   public String toString() {
      return "Matthew Freestone's Bot";
   }

   private char[][] fromBoard(Board board){
      String toStr = board.toString();
      char[][] out = new char[6][6];
      String[] lines = toStr.split("\n");
      for (int i = 0; i < 5; i++){
         char[] line = lines[i].toCharArray();
         for (int k = 0; k < 5; k++){
            out[i][k] = line[k];
         }
      }
      return out;
   }
}
