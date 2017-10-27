package app;

import java.util.Locale;

public class SandBox {

	public static void main(String[] args) {
		String entry = null;

		for (int indArg = 0; indArg < args.length; indArg++) {
			if (args[indArg].equals("-l")) {
				entry = args[++indArg];
				continue;
			}
		}
		
		System.out.println("entry = " + entry);
		
		Locale local = new Locale(entry);
		
		System.out.println("local = " + local);
	}
}
