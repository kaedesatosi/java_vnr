package app.task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import app.Global;
import app.frame.MainFrame;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TranslateTask {

	private static TranslateTask instance = new TranslateTask();

	private BlockingQueue<String> textQueue = new LinkedBlockingQueue<>();

	private Thread thread;

	public static void start() {
		instance.consumer();
	}

	public static void stop() {
		if (instance.thread == null)
			return;
		instance.thread.interrupt();
	}

	public static void producter(String text) {
		instance.textQueue.add(text);
	}

	private void consumer() {
		log.info("文本翻译任务队列启动！！！");
		thread = new Thread(() -> {
			boolean stop = false;
			while (!stop) {
				try {
					String text = textQueue.take();
					MainFrame.setSrcText(text);
					if (Global.translateService == null) {
						MainFrame.setDstText("未选择翻译引擎");
						continue;
					}
					String translate = Global.translateService.translate(text);
					MainFrame.setDstText(translate);
					log.info("翻译引擎：{}，原文：{}，翻译：{}", Global.translateService.getServiceName(), text, translate);
				} catch (InterruptedException e) {
					stop = true;
				} catch (Exception e) {
					log.info("文本翻译任务出错", e);
				}
			}
			log.info("文本翻译任务队列关闭！！！");
		});
		thread.start();
	}
}
