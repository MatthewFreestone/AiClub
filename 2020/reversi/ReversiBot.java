public abstract class ReversiBot {
	//Helper function.  Returns 'O' if given 'X' and vice versa
	protected static char getInversePiece(char piece) {
		return (char)(167-piece);
	}
	/*Returns the evaluation for how good the board is
	for the specified player ('X' or 'O').
	You chose how to calculate it.
	Do not change the board.*/
	public abstract int evaluate(char[][] board, char player);

	//returns your name, so I can figure out who won
	public abstract String getHumanName();

	//returns the bot's name
	public abstract String toString();
}