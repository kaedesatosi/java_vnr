package app.frame;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import app.Global;
import app.command.CmdCli;
import app.command.TasklistData;
import app.command.TextractorCli;
import app.listener.ClipboardListener;
import app.listener.TextractorListener;
import app.service.BaiduService;
import app.service.GoogleService;
import app.task.TranslateTask;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private JFrame that = this;
	private static JTextPane srcTextPane = null;
	private static JTextPane dstTextPane = null;
	private Font font = new Font("黑体", Font.BOLD, 18);
	private JPopupMenu menu;

	public void init() {
		setSystemStyle();
		int width = 1000, height = 150;
		this.setSize(width, height);
		this.setLocationRelativeTo(null);
		this.setUndecorated(true);
		this.setBackground(new Color(0x10, 0x10, 0x10, 200));
		this.setLayout(null);
		this.setName("main");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setAlwaysOnTop(true);

		addSrcTextPane();
		addDstTextPane();
		addPopMenu();
		addMouseMoveListener();
		addCloseListener();
		this.setVisible(true);
	}

	private void setSystemStyle() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			log.error("找不到类", e);
		} catch (InstantiationException e) {
			log.error("无法实例化类", e);
		} catch (IllegalAccessException e) {
			log.error("无法访问或初始化类", e);
		} catch (UnsupportedLookAndFeelException e) {
			log.error("平台不支持", e);
		}
	}

	private void addSrcTextPane() {
		srcTextPane = new JTextPane();
		srcTextPane.setText("按住鼠标滚轮拖动");
		srcTextPane.setEditable(false);
		srcTextPane.setOpaque(false);
		srcTextPane.setFont(font);
		srcTextPane.setForeground(Color.WHITE);
		srcTextPane.setSelectedTextColor(Color.WHITE);
		srcTextPane.setSelectionColor(new Color(0x338fff));
		JScrollPane scrollPane = new JScrollPane(srcTextPane);
		scrollPane.setBounds(25, 10, 950, 60);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setBorder(null);
		this.add(scrollPane);
	}

	private void addDstTextPane() {
		dstTextPane = new JTextPane();
		dstTextPane.setEditable(false);
		dstTextPane.setOpaque(false);
		dstTextPane.setFont(font);
		dstTextPane.setForeground(Color.WHITE);
		dstTextPane.setSelectedTextColor(Color.WHITE);
		dstTextPane.setSelectionColor(new Color(0x338fff));
		JScrollPane scrollPane = new JScrollPane(dstTextPane);
		scrollPane.setBounds(25, 80, 950, 60);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setBorder(null);
		this.add(scrollPane);
	}

	private void addPopMenu() {
		menu = new JPopupMenu("menu");

		JMenuItem gameGuide = new JMenuItem("运行向导");
		gameGuide.addActionListener(e -> {

			Object[] options = { "hook方法", "剪贴板" };
			int func = JOptionPane.showOptionDialog(this, "选择获取文本方式", "请选择", JOptionPane.DEFAULT_OPTION,
					JOptionPane.DEFAULT_OPTION, null, options, options[0]);
			if (func == -1) {
				return;
			}
			if (func == 1) {
				TextractorListener.stop();
				TextractorCli.close();
				ClipboardListener.start();
				return;
			}
			TextractorListener.stop();
			ClipboardListener.stop();
			List<TasklistData> tasklist = CmdCli.getTasklist();
			Object[] objects = new Object[tasklist.size()];
			for (int i = 0; i < objects.length; i++) {
				objects[i] = tasklist.get(i);
			}
			Object object = JOptionPane.showInputDialog(this, "选择游戏程序", "选择", JOptionPane.PLAIN_MESSAGE,
					new ImageIcon("icon.png"), objects, null);
			if (object != null) {
				TasklistData data = (TasklistData) object;
				TextractorCli.init();
				TextractorCli.attach(data.getPid());
				new TextDataDialog(this);
			}
		});
		menu.add(gameGuide);
		menu.addSeparator();

		JMenu translateMenu = new JMenu("翻译引擎");
		ButtonGroup translateGroup = new ButtonGroup();
		JRadioButtonMenuItem googleItem = new JRadioButtonMenuItem("谷歌翻译");
		googleItem.addActionListener(e -> {
			JRadioButtonMenuItem source = (JRadioButtonMenuItem) e.getSource();
			if (!(Global.translateService instanceof GoogleService)) {
				new GoogleService().start();
				log.info("切换到{}", source.getText());
			}
		});
		JRadioButtonMenuItem baiduItem = new JRadioButtonMenuItem("百度翻译");
		baiduItem.addActionListener(e -> {
			JRadioButtonMenuItem source = (JRadioButtonMenuItem) e.getSource();
			if (!(Global.translateService instanceof BaiduService)) {
				if (isBlank(BaiduService.appid) || isBlank(BaiduService.secret)) {
					if (!showBaiduConfigDialog())
						return;
				}
				new BaiduService().start();
				log.info("切换到{}", source.getText());
			}
		});
		translateGroup.add(googleItem);
		translateGroup.add(baiduItem);
		translateMenu.add(googleItem);
		translateMenu.add(baiduItem);
		menu.add(translateMenu);
		menu.addSeparator();

		JMenuItem closeItem = new JMenuItem("关闭");
		closeItem.addActionListener(e -> {
			close();
		});
		menu.add(closeItem);

		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
			@Override
			public void eventDispatched(AWTEvent event) {
				if (event instanceof MouseEvent) {
					MouseEvent e = (MouseEvent) event;

					Component c = e.getComponent();
					while (c != null) {
						if (c instanceof JDialog)
							return;
						c = c.getParent();
					}

					if (e.getID() == MouseEvent.MOUSE_RELEASED && e.getButton() == MouseEvent.BUTTON3) {
						menu.show(e.getComponent(), e.getX(), e.getY());
					}
				}
			}
		}, AWTEvent.MOUSE_EVENT_MASK);
	}

	private void addCloseListener() {
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				close();
			}
		});
	}

	private void addMouseMoveListener() {

		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
			int originX, originY;

			@Override
			public void eventDispatched(AWTEvent event) {
				if (event instanceof MouseEvent) {
					MouseEvent e = (MouseEvent) event;

					Component c = e.getComponent();
					while (c != null) {
						if (c instanceof JDialog)
							return;
						c = c.getParent();
					}

					if (e.getID() == MouseEvent.MOUSE_MOVED) {
						originX = e.getX();
						originY = e.getY();
					}
					if (e.getID() == MouseEvent.MOUSE_DRAGGED) {
						if (e.getModifiers() != MouseEvent.BUTTON2_MASK) {
							originX = e.getX();
							originY = e.getY();
							return;
						}
						Point point = that.getLocation();
						// 偏移距离
						int offsetX = e.getX() - originX;
						int offsetY = e.getY() - originY;
						that.setLocation(point.x + offsetX, point.y + offsetY);
					}
				}
			}
		}, AWTEvent.MOUSE_MOTION_EVENT_MASK);

	}

	private boolean showBaiduConfigDialog() {
		JTextField appidField = new JTextField(20);
		JTextField secretField = new JTextField(20);
		JPanel panel = new JPanel();
		Box horBox = Box.createHorizontalBox();
		JLabel appidLabel = new JLabel("appid:");
		appidLabel.setPreferredSize(new Dimension(50, 20));
		horBox.add(appidLabel);
		horBox.add(appidField);
		Box horBox2 = Box.createHorizontalBox();
		JLabel secretLabel = new JLabel("密钥:");
		secretLabel.setPreferredSize(new Dimension(50, 20));
		horBox2.add(secretLabel);
		horBox2.add(secretField);
		Box verBox = Box.createVerticalBox();
		verBox.add(horBox);
		verBox.add(horBox2);
		panel.add(verBox);
		int result = JOptionPane.showConfirmDialog(this, panel, "请输入百度翻译参数", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.DEFAULT_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			BaiduService.appid = appidField.getText();
			BaiduService.secret = secretField.getText();
			return true;
		}
		return false;
	}

	private boolean isBlank(String s) {
		if (s == null || s.length() == 0 || s.trim().length() == 0)
			return true;
		return false;
	}

	private void close() {
		TextractorCli.close();
		TranslateTask.stop();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		log.info("系统关闭");
		System.exit(0);
	}

	public static void setSrcText(String text) {
		srcTextPane.setText(text);
	}

	public static void setDstText(String text) {
		dstTextPane.setText(text);
	}

}
