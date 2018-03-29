package com.luzaimou.umd;

import java.io.File;
import java.util.List;

public class UmdMain {
	public static void main(String args[]) throws Exception {
		UMDDecoder umdDecoder = new UMDDecoder();
		File file = new File("f:/BaiduYunDownload/swing.umd");
		if (file.exists()) {
			System.out.println("==============file exist");
		}
		umdDecoder.decode(file);
		UMD umd = umdDecoder.umd;
		List<String> contentList = umd.getChapterContents();
		System.out.println("=================content:");
		System.out.println(contentList.get(1).replace(" 　　", "\n 　　"));
	}
}
