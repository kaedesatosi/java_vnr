package app.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CmdCli {

	public static List<TasklistData> getTasklist() {
		return getTasklistData();
	}

	private static List<TasklistData> getTasklistData() {
		ProcessBuilder builder = new ProcessBuilder("TASKLIST", "/FI", "USERNAME ne NT AUTHORITY\\SYSTEM", "/FI",
				"STATUS eq running");
		builder.redirectErrorStream(true);
		Process process = null;
		InputStream input = null;
		List<TasklistData> tasklistDataList = null;
		try {
			process = builder.start();
			input = process.getInputStream();
			tasklistDataList = solveTasklistInput(input);
			log.debug("获取进程列表={}", tasklistDataList);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			process.destroy();
		}
		return tasklistDataList;
	}

	private static List<TasklistData> solveTasklistInput(InputStream input) {
		List<TasklistData> tasklistDataList = new ArrayList<>();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(input, "gbk"));
			String temp = null;
			boolean start = false;
			while ((temp = br.readLine()) != null) {
				if (start) {
					String[] split = temp.split(" +");
					String imageName = split[0];
					int pid = Integer.parseInt(split[1]);
					TasklistData tasklistData = new TasklistData(imageName, pid);
					tasklistDataList.add(tasklistData);
				} else {
					start = temp.startsWith("=");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tasklistDataList;
	}

}
