package app.listener;

import app.command.TextHookData;
import app.command.TextHookDataObserver;
import app.command.TextractorCli;
import app.task.TranslateTask;

public class TextractorListener implements TextHookDataObserver {

	private static TextractorListener instance = new TextractorListener();

	public static String selectId = "-1";

	public static void start() {
		TextractorCli.addObserver(instance);
	}

	public static void stop() {
		TextractorCli.removeObserver(instance);
	}

	@Override
	public void onDataChanged(TextHookData data) {
		if (data.getId().equals(selectId))
			TranslateTask.producter(data.getTextData());
	}
}
