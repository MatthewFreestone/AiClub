public class ReallySimpleAI extends ReversiBot {
	
	//Evaluation function: the number of your pieces minus the number of opponent's pieces
	//Completely ignore positioning and flippability
	public int evaluate(char[][] board, char player) {
		int evaluation = 0;
		for(int i=0;i<6;i++) {
			for(int j=0;j<6;j++) {
				if(board[i][j]==player)
					evaluation++;
				else if(board[i][j]==getInversePiece(player))
					evaluation--;
			}
		}
		return evaluation;
	}

	public String getHumanName() {
		return "Bob Smith";	//creator of the bot
	}

	public String toString() {
		return "Really Simple AI";
	}
}