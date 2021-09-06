public class SimpleAI extends ReversiBot {

	private static char[][] curBoard;
	private static char curPlayer;
	
	//Evaluation function: the number of your pieces minus the number of opponent's pieces
	//Each piece is weighted by: 1 for non-edge, 5 for edge, 100 for corner
	public int evaluate(char[][] board, char player) {
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

	//helper method: returns 1 if given square is our piece, -1 if opponent's, 0 if neither
	private int getValue(int row, int col) {
		if(curBoard[row][col]==curPlayer)
			return 1;
		if(curBoard[row][col]=='.')
			return 0;
		return -1;
	}

	public String getHumanName() {
		return "Joe Smith";	//creator of the bot
	}

	public String toString() {
		return "Simple AI";
	}
}