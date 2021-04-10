package app.util;

import java.io.Closeable;
import java.io.IOException;

public class StreamUtils {

	public static void close(Closeable close) {
		if (close != null) {
			try {
				close.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
