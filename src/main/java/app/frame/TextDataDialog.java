package app.frame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import app.command.TextHookData;
import app.command.TextHookDataObserver;
import app.command.TextractorCli;
import app.listener.TextractorListener;
import app.task.TranslateTask;

public class TextDataDialog extends JDialog implements TextHookDataObserver {

	private static final long serialVersionUID = 1L;

	private JDialog that = this;
	private JTable table;
	private DefaultTableModel tableModel;
	private String[] columnTitle = { "编号", "方法名", "内容" };

	private int idWidth = 100;
	private int funcWidth = 100;
	private int textWidth = 480;

	public TextDataDialog(Frame parent) {
		super(parent, "选择hook方法", true);
		this.setSize(700, 400);
		this.setAlwaysOnTop(true);
		this.setLocationRelativeTo(parent);
		this.setLayout(null);
		this.setResizable(false);

		TextractorCli.addObserver(this);

		tableModel = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		Object[][] tableData = new Object[1][columnTitle.length];
		tableModel.setDataVector(tableData, columnTitle);

		table = new JTable(tableModel);
		table.setFont(new Font("黑体", Font.PLAIN, 14));
		table.setSelectionBackground(new Color(0x338fff));
		table.setRowHeight(30);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		autoSetColumnSize();

		JTableHeader tableHeader = table.getTableHeader();
		tableHeader.setFont(new Font("黑体", Font.PLAIN, 16));
		tableHeader.setForeground(new Color(0x338fff));

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setSize(700, 300);
		this.add(scrollPane);

		JButton button = new JButton("确定");
		button.setBounds(0, 300, 700, 70);
		this.add(button);
		button.addActionListener(e -> {
			int selectIndex = table.getSelectedRow();
			if (selectIndex == -1) {
				JOptionPane.showMessageDialog(this, "请选择hook方法", "提示", JOptionPane.WARNING_MESSAGE);
			}
			TextractorCli.removeObserver(this);
			TextractorListener.selectId = table.getValueAt(selectIndex, 0).toString();
			String text = table.getValueAt(selectIndex, 2).toString();
			TranslateTask.producter(text);
			TextractorListener.start();
			this.dispose();
		});

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				that.dispose();
				TextractorCli.close();
			}
		});

		this.setVisible(true);
	}

	private void autoSetColumnSize() {
		table.getColumnModel().getColumn(0).setPreferredWidth(idWidth);
		table.getColumnModel().getColumn(1).setPreferredWidth(funcWidth);
		table.getColumnModel().getColumn(2).setPreferredWidth(textWidth);
	}

	@Override
	public synchronized void onDataChanged(TextHookData data) {

		idWidth = table.getColumnModel().getColumn(0).getPreferredWidth();
		funcWidth = table.getColumnModel().getColumn(1).getPreferredWidth();
		textWidth = table.getColumnModel().getColumn(2).getPreferredWidth();

		int size = TextractorCli.textDataMap.size();
		Object[][] tableData = new Object[size][columnTitle.length];
		int index = 0;
		for (Entry<String, TextHookData> entry : TextractorCli.textDataMap.entrySet()) {
			tableData[index][0] = entry.getKey();
			tableData[index][1] = entry.getValue().getHookFunc();
			tableData[index][2] = entry.getValue().getTextData();
			index++;
		}
		tableModel.setDataVector(tableData, columnTitle);
		autoSetColumnSize();
	}

}
