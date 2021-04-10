package app.service;

import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import app.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GoogleService extends BaseTranslateService {

	public String TKK = null;

	public GoogleService() {
		String url = "https://translate.google.cn/";
		String text = HttpUtils.get(url);
		TKK = getTKK(text);
		log.info("tkk={}", TKK);
	}

	@Override
	public String getServiceName() {
		return "谷歌翻译";
	}

	@Override
	public String translate(String text) {
		if (TKK == null) {
			log.error("tkk为空");
			return null;
		}
		String tk = getTk(text, TKK);
		StringBuffer turl = new StringBuffer("https://translate.google.cn/translate_a/single");
		turl.append("?client=webapp");
		turl.append("&sl=auto");
		turl.append("&tl=zh-CN");
		turl.append("&dt=at");
		turl.append("&dt=ex");
		turl.append("&dt=ld");
		turl.append("&dt=md");
		turl.append("&dt=qca");
		turl.append("&dt=rw");
		turl.append("&dt=rm");
		turl.append("&dt=sos");
		turl.append("&dt=ss");
		turl.append("&dt=t");
		turl.append("&otf=1");
		turl.append("&ssel=0");
		turl.append("&tsel=0");
		turl.append("&kc=1");
		turl.append("&tk=" + tk);
		try {
			text = URLEncoder.encode(text, "utf-8");
		} catch (UnsupportedEncodingException e) {
			log.error("原文编码时出错：" + text, e);
			return null;
		}
		turl.append("&q=" + text);
		String response = HttpUtils.get(turl.toString());
		return getTranslateResult(response);
	}

	private String getTranslateResult(String result) {
		if (result == null)
			return null;
		int i = result.indexOf("\"");
		if (i == -1)
			return null;
		result = result.substring(i + 1);
		i = result.indexOf("\"");
		if (i == -1)
			return null;
		result = result.substring(0, i);
		return result;
	}

	private String getTKK(String text) {
		int start = text.indexOf("tkk:'");
		if (start == -1)
			return null;
		text = text.substring(start + 5);
		int end = text.indexOf("'");
		text = text.substring(0, 0 + end);
		return text;
	}

	private String getTk(String a, String TKK) {
		String tk = null;
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
		URL url = this.getClass().getResource("/tk.js");
		try {
			FileReader fr = new FileReader(URLDecoder.decode(url.getPath(), "utf-8"));
			engine.eval(fr);
			Invocable in = (Invocable) engine;
			tk = in.invokeFunction("tk", a, TKK).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tk;
	}

}
