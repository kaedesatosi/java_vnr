package app.listener;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import app.task.TranslateTask;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClipboardListener implements ClipboardOwner {

	private static ClipboardListener instance = new ClipboardListener();

	private Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

	private boolean stop = false;

	public static void start() {
		instance.stop = false;
		// 如果剪贴板中有文本，则将它的ClipboardOwner设为自己
		if (instance.clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
			instance.clipboard.setContents(instance.clipboard.getContents(null), instance);
		}
	}

	public static void stop() {
		instance.stop = true;
	}

	// 如果剪贴板的内容改变，则系统自动调用此方法
	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		if (stop)
			return;
		try {
			Thread.sleep(100);
			// 如果不暂停一下，经常会抛出IllegalStateException
			// 猜测是操作系统正在使用系统剪切板，故暂时无法访问
			// 取出文本并进行一次文本处理
			String text = null;
			if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
				text = (String) clipboard.getData(DataFlavor.stringFlavor);
			}
			TranslateTask.producter(text);
			// 存入剪贴板，并注册自己为所有者
			// 用以监控下一次剪贴板内容变化
			StringSelection tmp = new StringSelection(text);
			clipboard.setContents(tmp, this);
		} catch (Exception e) {
			log.error("剪贴板出错", e);
			reset();
		}
	}

	private void reset() {
		log.info("重置剪贴板监控");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		new Thread(() -> {
			try {
				if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
					clipboard.setContents(clipboard.getContents(null), this);
				}
			} catch (Exception e) {
				reset();
			}
		}).start();
	}
}