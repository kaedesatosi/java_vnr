package app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpUtils {

	public static String get(String urlPath) {
		return get(urlPath, true);
	}

	public static String get(String urlPath, boolean gizp) {
		String text = null;
		BufferedReader reader = null;
		try {
			URL url = new URL(urlPath);
			URLConnection con = url.openConnection();
			RequestHelper.initRequestHeader(con);
			InputStream in = con.getInputStream();
			if (gizp) {
				GZIPInputStream gzin = new GZIPInputStream(in);
				reader = new BufferedReader(new InputStreamReader(gzin));
			} else {
				reader = new BufferedReader(new InputStreamReader(in));
			}
			String str = null;
			StringBuffer sb = new StringBuffer();
			while ((str = reader.readLine()) != null) {
				sb.append(str);
			}
			text = sb.toString();
		} catch (Exception e) {
			log.error("请求出错", e);
		} finally {
			StreamUtils.close(reader);
		}
		return text;
	}
}
