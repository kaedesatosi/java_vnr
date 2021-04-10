package app.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import app.util.HttpUtils;
import app.util.MD5Utils;
import app.util.RandomUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaiduService extends BaseTranslateService {
	
	public static String appid = "";
	public static String secret = "";

	@Override
	public String getServiceName() {
		return "百度翻译";
	}

	@Override
	public String translate(String text) {
		StringBuffer turl = new StringBuffer("https://fanyi-api.baidu.com/api/trans/vip/translate");
		int salt = RandomUtils.getRandomNum();
		String sign = getSign(text, salt);
		try {
			text = URLEncoder.encode(text, "utf-8");
		} catch (UnsupportedEncodingException e) {
			log.error("原文编码时出错：" + text, e);
			return null;
		}
		turl.append("?q=" + text);
		turl.append("&from=auto");
		turl.append("&to=zh");
		turl.append("&appid=" + appid);
		turl.append("&salt=" + salt);
		turl.append("&sign=" + sign);
		String response = HttpUtils.get(turl.toString(), false);
		log.debug("百度翻译返回结果:{}", response);
		return getTranslateResult(response);
	}

	private String getSign(String query, int salt) {
		String concat = appid + query + salt + secret;
		String sign = MD5Utils.getMD5String(concat);
		return sign;
	}

	private String getTranslateResult(String result) {
		if (result == null)
			return null;
		int i = result.indexOf("dst");
		if (i == -1)
			return null;
		result = result.substring(i + 6);
		i = result.indexOf("\"");
		if (i == -1)
			return null;
		result = result.substring(0, i);
		return ustartToCn(result);
	}

	private static String ustartToCn(final String str) {
		String[] split = str.split("\\\\u");
		StringBuffer sb = new StringBuffer();
		for (int i = 1; i < split.length; i++) {
			int result = 0;
			for (int j = 0; j < split[i].length(); j++) {
				result <<= 4;
				char charAt = split[i].charAt(j);
				if (charAt >= '0' && charAt <= '9')
					result += charAt - '0';
				else if (charAt >= 'a' && charAt <= 'z')
					result += charAt - 'a' + 10;
				else if (charAt >= 'A' && charAt <= 'Z')
					result += charAt - 'A' + 10;
			}
			sb.append(String.valueOf((char) result));
		}
		return sb.toString();
	}
}
