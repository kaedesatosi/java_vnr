package app.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TextHookData {

	// hook索引
	private String id;

	// 程序进程ID
	private int pid;

	/// Hook入口地址：可用于以后卸载Hook
	private String hookAddress;

	// hook context
	private String ctx;

	// hook context2
	private String ctx2;

	/// Hook方法名：Textrator注入游戏进程获得文本时的方法名
	// （为 Console 时代表Textrator本体控制台输出；为 Clipboard 时代表从剪贴板获取的文本）
	private String hookFunc;

	/// 通用特殊码
	private String hookCode;

	/// 实际内容
	private String textData;
}
