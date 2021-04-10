package app.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MD5Utils {

	public static String getMD5String(String str) { // 用来计算MD5的函数
		String[] hexArray = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte[] rawBit = md.digest();
			String outputMD5 = " ";
			for (int i = 0; i < 16; i++) {
				outputMD5 = outputMD5 + hexArray[rawBit[i] >>> 4 & 0x0f];
				outputMD5 = outputMD5 + hexArray[rawBit[i] & 0x0f];
			}
			return outputMD5.trim();
		} catch (NoSuchAlgorithmException e) {
			log.error("计算MD5值发生错误", e);
			e.printStackTrace();
		}
		return null;
	}
}
