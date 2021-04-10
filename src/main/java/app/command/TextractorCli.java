package app.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import app.Global;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TextractorCli {

	private static Process process;
	private static InputStream input;
	private static OutputStream out;

	public static Map<String, TextHookData> textDataMap = new HashMap<>();
	private static Set<Integer> attachedPidSet = new HashSet<>();
	private static List<TextHookDataObserver> observers = new ArrayList<>();

	public static void init() {
		log.info("TextratorCli启动");
		textDataMap.clear();
		attachedPidSet.clear();
		ProcessBuilder builder = new ProcessBuilder(Global.TextractorCliPath);
		builder.redirectErrorStream(true);
		try {
			process = builder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		input = process.getInputStream();
		out = process.getOutputStream();
		solveTextractorInput();
	}

	public static void attach(int pid) {
		if (attachedPidSet.contains(pid)) {
			log.info("pid[{}] has been attached", pid);
			return;
		}
		String command = "attach -P" + pid + "\n";
		log.debug("attach--{}", pid);
		try {
			attachedPidSet.add(pid);
			out.write(command.getBytes("utf-16le"));
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void detach(int pid) {
		String command = "detach -P" + pid + "\n";
		log.debug("detach--{}", pid);
		try {
			out.write(command.getBytes("utf-16le"));
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void detachHook(int pid, String HookAddress) {
		String command = "HW0@" + HookAddress + ":module_not_exists" + " -P" + pid + "\n";
		try {
			out.write(command.getBytes("utf-16le"));
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void solveTextractorInput() {
		new Thread(() -> {
			String temp = null;
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(input, "utf-16le"));
				while ((temp = br.readLine()) != null) {
					String[] split = temp.split(":", 7);
					log.debug(temp);
					if (split.length < 7) {
						continue;
					}
					String id = split[0].substring(1);
					int pid = hexToInt(split[1]);
					String hookAddress = split[2];
					String ctx = split[3];
					String ctx2 = split[4];
					String hookFunc = split[5];
					String data = split[6];
					String[] split2 = data.split("]", -1);
					String hookCode = split2[0];
					String textData = split2[1].trim();
					TextHookData hookData = new TextHookData(id, pid, hookAddress, ctx, ctx2, hookFunc, hookCode,
							textData);
					textDataMap.put(id, hookData);
					observers.forEach(o -> o.onDataChanged(hookData));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

	private static int hexToInt(String hex) {
		int result = 0;
		for (int i = 0; i < hex.length(); i++) {
			result <<= 4;
			char charAt = hex.charAt(i);
			if (charAt >= '0' && charAt <= '9')
				result += charAt - '0';
			else if (charAt >= 'a' && charAt <= 'z')
				result += charAt - 'a' + 10;
			else if (charAt >= 'A' && charAt <= 'Z')
				result += charAt - 'A' + 10;
		}
		return result;
	}

	public static void close() {
		if (process == null || !process.isAlive())
			return;
		attachedPidSet.forEach(pid -> {
			detach(pid);
		});
		// 等待depach
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		process.destroy();
		log.info("TextratorCli关闭");
	}

	public static void addObserver(TextHookDataObserver observer) {
		observers.add(observer);
	}

	public static void removeObserver(TextHookDataObserver observer) {
		observers.remove(observer);
	}

}
