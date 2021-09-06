import java.util.*;

public class Main {
	private static final int SEARCH_DEPTH = 3;

	public static void main(String[] args) {
		runMatch(new ReallySimpleAI(), new mfBot());
	}
	
	//runs a match between the two bots.  Pass in null for one of the bots to get human input
	//Returns 2 for a p1 (X) win, 1 for a draw, and 0 for a p2 (O) win
	//in my version of this, it will catch errors and forfeit the match, but here we don't, so you can see errors in your bot
	private static int runMatch(ReversiBot p1, ReversiBot p2) {
		int move;
		Board b = new Board();
		while(b.getEndState()==0) {
			System.out.println(b);
			move = b.getAIMove(p1, SEARCH_DEPTH);
			System.out.println(p1+" (X) makes move "+toRowCol(move));
			System.out.println();
			b = b.getChild(move);
			if(b.getEndState()!=0)
				break;
			System.out.println(b);
			move = b.getAIMove(p2, SEARCH_DEPTH);
			System.out.println(p2+" (O) makes move "+toRowCol(move));
			System.out.println();
			b = b.getChild(move);
		}
		if(b.getEndState()=='X') {
			System.out.println(p1+" (X) wins!");
			return 2;
		}
		else if(b.getEndState()=='O') {
			System.out.println(p2+" (O) wins!");
			return 0;
		}
		else {
			System.out.println("It is a draw.");
			return 1;
		}
	}
	
	private static String toRowCol(int move) {
		return move==-1 ? "pass" : "("+move/6+", "+move%6+")";
	}
}