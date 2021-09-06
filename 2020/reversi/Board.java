import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Board {
	private char[][] pieces;
	public int[] moves;
	public Board[] children;
	private char turn;
	private char endVal;	//'X' or 'O' for a win, '-' for a draw, 0 for not end
	
	private static Scanner scanner = new Scanner(System.in);

	private static char getInversePiece(char piece) {
		return (char)(167-piece);
	}
	private static boolean onBoard(int row, int col) {
		return row>=0 && row<=5 && col>=0 && col<=5;
	}
	
	public Board() {
		this.pieces = new char[6][6];
		for(int i=0;i<6;i++)
			for(int j=0;j<6;j++)
				this.pieces[i][j]='.';
		this.pieces[2][2] = 'O';
		this.pieces[3][3] = 'O';
		this.pieces[2][3] = 'X';
		this.pieces[3][2] = 'X';
		this.turn = 'X';
		generateMoves();
		this.endVal = 0;
	}
	public Board(Board parent, int move) {
		this.pieces = new char[6][6];
		for(int i=0;i<6;i++)
			this.pieces[i] = parent.pieces[i].clone();
		if(move!=-1)
			makeMove(move/6,move%6,parent.turn);
		this.turn = getInversePiece(parent.turn);
		generateMoves();
		this.endVal = calcEndState();
	}
	public Board(char[][] pieces, char turn) {
		this.pieces = pieces;
		this.turn = turn;
		generateMoves();
		this.endVal = calcEndState();
	}

	private void generateMoves() {
		ArrayList<Integer> movesList = new ArrayList<Integer>();
		for(int i=0;i<6;i++) {
			for(int j=0;j<6;j++) {
				if(isLegalMove(i, j))
					movesList.add(6*i+j);
			}
		}
		this.moves = new int[movesList.size()];
		for(int i=0;i<moves.length;i++)
			moves[i]=movesList.get(i);
	}
	private char calcEndState() {
		if(moves.length!=0)
			return 0;
		turn = getInversePiece(turn);
		generateMoves();
		boolean isEnd = moves.length==0;
		turn = getInversePiece(turn);
		moves = new int[0];
		if(!isEnd)
			return 0;
		int count=0;	//number of P1 (X) pieces
		for(char[] row:pieces)
			for(char piece: row)
				count += (piece=='X' ? 1 : (piece=='O' ? -1 : 0));
		return (count>0 ? 'X' : count<0 ? 'O' : '-');
	}

	public void generateChildren() {
		if(children!=null)
			return;
		if(moves.length==0) {
			children = new Board[1];
			children[0] = new Board(this, -1);
			return;
		}
		children = new Board[moves.length];
		for(int i=0;i<children.length;i++)
			children[i]=new Board(this, moves[i]);
	}

	private boolean isLegalMove(int row, int col) {
		if(pieces[row][col]!='.')
			return false;
		for(int dr=-1;dr<=1;dr++) {
			for(int dc=-1;dc<=1;dc++) {
				if(scan(row, col, dr, dc, turn)!=0)
					return true;
			}
		}
		return false;
	}

	private int scan(int row, int col, int deltaRow, int deltaCol, char piece) {
		char invPiece = getInversePiece(piece);
		int count = 0;
		do {
			row+=deltaRow;
			col+=deltaCol;
			count++;
		} while(onBoard(row,col) && pieces[row][col]==invPiece);
		return onBoard(row,col) && pieces[row][col]==piece ? count-1 : 0;
	}

	private void makeMove(int row, int col, char piece) {
		pieces[row][col]=piece;
		for(int dr=-1;dr<=1;dr++) {
			for(int dc=-1;dc<=1;dc++) {
				if(dr==0 && dc==0)
					continue;
				for(int i=scan(row,col,dr,dc,piece);i>0;i--)
					pieces[row+i*dr][col+i*dc] = piece;
			}
		}
	}

	public String toString() {
		String out = "";
		for(int i=0;i<6;i++)
			out+=String.valueOf(pieces[i]) + (i<5 ? "\n" : "");
		return out;
	}

	public char getEndState() {
		return endVal;
	}

	public Board getChild(int move) {
		if(move==-1)
			return children[0];
		for(int i=0;i<moves.length;i++)
			if(moves[i]==move)
				return children[i];
		System.out.println("Invalid move!");
		return new Board();	//unreachable when giving valid move
	}

	public int getHumanMove() {
		generateChildren();
		if(moves.length==0) {
			System.out.println("You have to pass.");
			return -1;
		}
		int row, col, index;
		do {
			System.out.print("What row? ");
			row = Integer.parseInt(scanner.nextLine());
			System.out.print("What column? ");
			col = Integer.parseInt(scanner.nextLine());
			index = Arrays.binarySearch(moves, 6*row+col);
		} while(!onBoard(row, col) || index<0);
		return 6*row + col;
	}

	public int getAIMove(ReversiBot evaluator, int depth) {
		generateChildren();
		if(moves.length==0)	//have to pass
			return -1;
		if(moves.length==1)	//only one choice
			return moves[0];
		int bestMove = moves[0];
		int bestEval = children[0].minimize(evaluator, depth-1);
		for(int i=1;i<children.length;i++) {
			int curEval = children[i].minimize(evaluator, depth-1);
			if(curEval > bestEval) {
				bestEval = curEval;
				bestMove = moves[i];
			}
		}
		return bestMove;
	}
	private int maximize(ReversiBot evaluator, int depth) {
		if(endVal!=0)
			return (endVal=='-' ? 0 : (endVal==turn ? Integer.MAX_VALUE : Integer.MIN_VALUE));
		generateChildren();
		int output = Integer.MIN_VALUE;
		for(Board child:children)
			output = Math.max(output, child.minimize(evaluator, depth-1));
		return output;
	}
	private int minimize(ReversiBot evaluator, int depth) {
		if(endVal!=0)
			return (endVal=='-' ? 0 : (endVal==turn ? Integer.MIN_VALUE : Integer.MAX_VALUE));
		generateChildren();
		int output = Integer.MAX_VALUE;
		for(Board child:children)
			output = Math.min(output, depth==0 ? evaluator.evaluate(child.pieces, getInversePiece(turn)) : child.maximize(evaluator,depth));
		return output;
	}
}