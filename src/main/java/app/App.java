package app;

import app.frame.MainFrame;
import app.task.TranslateTask;

public class App {

	public static void main(String[] args) {
		TranslateTask.start();
		new MainFrame().init();
	}

}
