// INTERPRETER.java

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.util.ArrayList;

public class INTERPRETER {

	static String studentName = "Eric Ko / Kristine Cheng";
	static String studentID = "Eric: 88335453 / Kristine: 62637032";
	static String uciNetID = "Eric: sangyuk1 / Kristine: cycheng5";

	boolean debug = false;

	public static void main(String[] args) {
		INTERPRETER i = new INTERPRETER(args[0]);
		i.runProgram();
	}

	class SyntaxException extends Exception {
		public SyntaxException() {
		}
	}

	private LineNumberReader codeIn;
	private LineNumberReader inputIn;
	private FileOutputStream outFile;
	private PrintStream outStream;

	private static final int DATA_SEG_SIZE = 100;
	private ArrayList<String> C;
	private int[] D;
	private int PC;
	private String IR;
	private boolean run_bit;

	private int curIRIndex = 0;

	public INTERPRETER(String sourceFile) {
		try {
			inputIn = new LineNumberReader(new FileReader("input.txt"));
			inputIn.setLineNumber(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Init: Errors accessing input.txt");
			System.exit(-2);
		}

		try {
			outFile = new FileOutputStream(sourceFile + ".out");
			outStream = new PrintStream(outFile);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Init: Errors accessing " + sourceFile + ".out");
			System.exit(-2);
		}

		// Initialize the SIMPLESEM processor state
		try {
			// Initialize the Code segment
			C = new ArrayList<String>();
			codeIn = new LineNumberReader(new FileReader(sourceFile));
			codeIn.setLineNumber(1);
			while (codeIn.ready()) {
				C.add(codeIn.readLine());
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Init: Errors accessing source file " + sourceFile);
			System.exit(-2);
		}

		// Initialize the Data segment
		D = new int[DATA_SEG_SIZE];
		for (int i = 0; i < DATA_SEG_SIZE; i++) {
			D[i] = 0;
		}
		PC = 0; // Every SIMPLESEM program begins at instruction 0
		IR = null;
		run_bit = true; // Enable the processor
	}

	public void runProgram() {

		// TODO FETCH-INCREMENT-EXECUTE CYCLE

		while (run_bit) {
			fetch();
			incrementPC();
			execute();
		}

		// for (String s : C) {
		// IR = s;
		// curIRIndex = 0;
		// try {
		// parseStatement();
		// } catch (SyntaxException e) {
		// }
		// }

		printDataSeg();
	}

	private void printDataSeg() {
		outStream.println("Data Segment Contents");
		for (int i = 0; i < DATA_SEG_SIZE; i++) {
			outStream.println(i + ": " + D[i]);
		}
	}

	private void fetch() {
		// TODO
		IR = C.get(PC);
	}

	private void incrementPC() {
		// TODO
		PC += 1;
	}

	private void execute() {
		// TODO
		try {
			curIRIndex = 0;
			parseStatement();
		} catch (SyntaxException e) {
			System.out.println("ERROR: " + e);
		}
	}

	// Output: used in the case of: set write, source
	private void write(int source) {
		outStream.println(source);
	}

	// Input: used in the case of: set destination, read
	private int read() {
		int value = Integer.MIN_VALUE;
		try {
			value = new Integer((inputIn.readLine())).intValue();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * Checks and returns if the character c is found at the current position in IR.
	 * If c is found, advance to the next (non-whitespace) character.
	 */
	private boolean accept(char c) {
		if (curIRIndex >= IR.length())
			return false;

		if (IR.charAt(curIRIndex) == c) {
			curIRIndex++;
			skipWhitespace();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Checks and returns if the string s is found at the current position in IR. If
	 * s is found, advance to the next (non-whitespace) character.
	 */
	private boolean accept(String s) {

		if (curIRIndex >= IR.length())
			return false;

		if (curIRIndex + s.length() <= IR.length() && s.equals(IR.substring(curIRIndex, curIRIndex + s.length()))) {
			curIRIndex += s.length();
			skipWhitespace();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Checks if the character c is found at the current position in IR. Throws a
	 * syntax error if c is not found at the current position.
	 */
	private void expect(char c) throws SyntaxException {
		if (!accept(c))
			throw new SyntaxException();
	}

	/**
	 * Checks if the string s is found at the current position in IR. Throws a
	 * syntax error if s is not found at the current position.
	 */
	private void expect(String s) throws SyntaxException {
		if (!accept(s))
			throw new SyntaxException();
	}

	private void skipWhitespace() {
		while (curIRIndex < IR.length() && Character.isWhitespace(IR.charAt(curIRIndex)))
			curIRIndex++;
	}

	private void parseStatement() throws SyntaxException {
		if (debug)
			System.err.println("Statement");

		if (accept("halt")) {
			run_bit = false;
			return;
		} else if (accept("set"))
			parseSet();
		else if (accept("jumpt"))
			parseJumpt();
		else if (accept("jump"))
			parseJump();
	}

	private void parseSet() throws SyntaxException {
		if (debug)
			System.err.println("Set");

		Integer num1 = null;
		Integer num2 = null;
		Integer data = null;

		if (!accept("write")) {
			num1 = parseExpr();
		}

		expect(',');

		if (!accept("read")) {
			num2 = parseExpr();
		}

		// writing
		if (num1 == null) {
			write(num2);
		}
		// reading
		else if (num2 == null) {
			data = read();
			D[num1] = data;
		}
		// moving value
		else {
			D[num1] = num2;
		}
	}

	private Integer parseExpr() throws SyntaxException {
		if (debug)
			System.err.println("Expr");

		Integer num1 = null;
		Integer num2 = null;

		num1 = parseTerm();

		while (true) {
			if (accept('+')) {
				num2 = parseTerm();
				num1 += num2;

			} else if (accept('-')) {
				num2 = parseTerm();
				num1 -= num2;

			} else {
				break;
			}
		}

		return num1;
	}

	private Integer parseTerm() throws SyntaxException {
		if (debug)
			System.err.println("Term");

		Integer num1 = null;
		Integer num2 = null;

		num1 = parseFactor();

		while (true) {
			if (accept('*')) {
				num2 = parseFactor();
				num1 *= num2;
			}

			else if (accept('/')) {
				num2 = parseFactor();
				num1 /= num2;
			}

			else if (accept('%')) {
				num2 = parseFactor();
				num1 %= num2;
			}

			else {
				break;
			}
		}

		return num1;
	}

	private Integer parseFactor() throws SyntaxException {
		if (debug)
			System.err.println("Factor");

		Integer num = null;

		if (accept("D[")) {
			num = D[parseExpr()];

			expect(']');
		} else if (accept('(')) {
			num = parseExpr();

			expect(')');
		} else {
			num = parseNumber();
		}

		return num;
	}

	private Integer parseNumber() throws SyntaxException {
		if (debug)
			System.err.println("Number");

		String num_str = "";
		Integer num;

		if (curIRIndex >= IR.length())
			throw new SyntaxException();

		if (IR.charAt(curIRIndex) == '0') {

			num_str += IR.charAt(curIRIndex);
			curIRIndex++;
			skipWhitespace();

		} else if (Character.isDigit(IR.charAt(curIRIndex))) {
			while (curIRIndex < IR.length() && Character.isDigit(IR.charAt(curIRIndex))) {

				num_str += IR.charAt(curIRIndex);
				curIRIndex++;
			}

			skipWhitespace();
		} else {
			throw new SyntaxException();
		}

		num = Integer.parseInt(num_str);

		return num;
	}

	private void parseJump() throws SyntaxException {
		if (debug)
			System.err.println("Jump");

		Integer num = null;

		num = parseExpr();

		PC = num;
	}

	// <Jumpt>-> jumpt <Expr>, <Expr> ( != | == | > | < | >= | <= ) <Expr>
	private void parseJumpt() throws SyntaxException {
		if (debug)
			System.err.println("Jumpt");

		Integer expr1 = null;
		Integer expr2 = null;
		Integer dest = null;

		dest = parseExpr();

		expect(',');

		expr1 = parseExpr();

		if (accept("!=")) {
			expr2 = parseExpr();

			if (expr1 != expr2) {
				PC = dest;
			}
		}

		else if (accept("==")) {
			expr2 = parseExpr();

			if (expr1 == expr2) {
				PC = dest;
			}
		}

		else if (accept(">=")) {
			expr2 = parseExpr();

			if (expr1 >= expr2) {
				PC = dest;
			}
		}

		else if (accept("<=")) {
			expr2 = parseExpr();

			if (expr1 <= expr2) {
				PC = dest;
			}
		}

		else if (accept(">")) {
			expr2 = parseExpr();

			if (expr1 > expr2) {
				PC = dest;
			}
		}

		else if (accept("<")) {
			expr2 = parseExpr();

			if (expr1 < expr2) {
				PC = dest;
			}
		}

		else {
			throw new SyntaxException();
		}

		// if (accept("!=") || accept("==") || accept(">=") || accept("<=") ||
		// accept('>') || accept('<')) {
		// parseExpr();
		// } else {
		// throw new SyntaxException();
		// }
	}
}
