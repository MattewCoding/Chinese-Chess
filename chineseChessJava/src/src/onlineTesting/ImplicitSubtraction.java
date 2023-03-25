package onlineTesting;

/**
 * Testing pre/post-incrementing variables in if statements
 * @author Yang Mattew
 *
 */
public class ImplicitSubtraction {

	public static void main(String[] args) {
		int test = 10;
		while(--test >= 0 && printTest(test)) {}
	}
	
	public static Boolean printTest(int x) {
		System.out.println(x);
		return x >= 0;
	}

}
