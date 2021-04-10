package app.util;

import java.util.Random;

public class RandomUtils {
	private static Random random = new Random();

	public static int getRandomNum() {
		return random.nextInt() & Integer.MAX_VALUE;
	}
}
