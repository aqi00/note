package org.yufeng.jchmlib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.example.exmword.util.FileUtil;

import android.util.Log;

public class ChmExtractor {
	private static final String TAG = "ChmExtractor";
	private static final String File_SEPORATOR = "/";// 文件分隔符

	public static String getContentFiles(String path, String dir) throws Exception {
		String index_path = "";
		File dirFile = new File(dir);
		if (dirFile.exists() != true) {
			dirFile.mkdirs();
		}
		ChmManager cm = new ChmManager(path);
		ArrayList<FileEntry> fes = (ArrayList<FileEntry>) cm.enumerateFiles();
		FileEntry fe = null;
		String hhc_name = FileUtil.getFileName(path) + ".hhc";
		Log.d(TAG, "hhc_name="+hhc_name);
		for (int i = 0; i < fes.size(); i++) {
			fe = (FileEntry) fes.get(i);
			extractOne(cm, fe, dir, fe.entryName);
			Log.d(TAG, "i="+i+", name="+fe.entryName);
			if (fe.entryName.indexOf(hhc_name) > 0) {
				index_path = readIndex(dir, fe.entryName);
			}
		}
		return index_path;
	}

	private static String readIndex(String dir, String index_file) throws Exception {
		String content = openTxtFile(dir+index_file).replace("osaram", "<osaram");
		int begin_pos = content.indexOf("<HTML>");
		int end_pos = content.indexOf("</HTML>", begin_pos);
		Log.d(TAG, "begin_pos="+begin_pos+", end_pos="+end_pos);
		String html = content.substring(begin_pos, end_pos+"</HTML>".length());
		Log.d(TAG, "html="+html);
		return readHtml(dir, html);
	}
	
	private static String readHtml(String dir, String html) throws Exception {
		String index_path = dir+"/index.html";
		String htmlBegin = "<html><meta charset=\"utf-8\"><body>";
		String htmlEnd = "</body></html>";
		FileOutputStream output = new FileOutputStream(index_path);
		output.write(htmlBegin.getBytes());
		Document doc = Jsoup.parse(html);
		Element link_li = null;
		for (int i = 0;; i++) {
			try {
				link_li = doc.select("LI").get(i);
				String link_li_s = link_li.text();
				if (link_li_s.indexOf("./") >= 0) {
					continue;
				}
			} catch (IndexOutOfBoundsException ex) {
				break;
			}
			Element link_osaram = null;
			String title = "", url = "";
			for (int j=0;; j++) {
				try {
					link_osaram = link_li.select("osaram").get(j);
				} catch (IndexOutOfBoundsException ex) {
					break;
				}
				if (link_osaram.attr("name").equals("Name")) {
					title = link_osaram.attr("value");
				} else if (link_osaram.attr("name").equals("Local")) {
					url = link_osaram.attr("value");
				}
			}
			Log.d(TAG, "title="+title+", url="+url);
			String link = String.format("<a href=\"%s\">\"%s\"</a><br><br>", 
					dir+"/html/"+FileUtil.getFileNameWithExt(url), title);
			output.write(link.getBytes());
		}
		output.write(htmlEnd.getBytes());
		return index_path;
	}

	private static String openTxtFile(String path) {
		String readStr = "";
		try {
			FileInputStream fis = new FileInputStream(path);
			byte[] b = new byte[fis.available()];
			fis.read(b);
			fis.close();
			readStr = new String(b, "gbk");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return readStr;
	}
	
	private static void extractOne(ChmManager cm, FileEntry fe, String savef, String fileName) throws Exception {
		try {
			if (fe.length.longValue() == 0)
				return;
			byte[][] tmp = cm.retrieveObject(fe);
			writeFile(tmp, savef, fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * output the specified file
	 */
	private static void writeFile(byte[][] tmp, String savef, String fileName) throws Exception {
		FileOutputStream fops = null;
		try {
			createFileParentPath(savef, fileName);
			File file = new File(savef, fileName);
			fops = new FileOutputStream(file.getPath());
			for (int i = 0; i < tmp.length; i++) {
				fops.write(tmp[i]);
			}
			fops.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 说明:创建每个文件的父目录 DATE:2010-08-13
	 * 
	 * @param output
	 *            总目录
	 * @param filePath
	 *            文件路径
	 * @throws Exception
	 */
	private static void createFileParentPath(String output, String filePath) throws Exception {
		int spcialTagMark = filePath.indexOf(File_SEPORATOR);
		if (spcialTagMark == -1)
			return;// 说明文字存放于根目录下
		filePath = filePath.substring(0, filePath.lastIndexOf(File_SEPORATOR));
		if (CommonFunctions.isNotEmpty(filePath)) {
			createFileDirectory(output, filePath);
		}
	}

	/**
	 * 说明:创建目录
	 * 
	 * @param output
	 * @param filePath
	 * @throws Exception
	 */
	private static void createFileDirectory(String output, String filePath) throws Exception {
		String[] filePaths = CommonFunctions.strToArray(filePath, File_SEPORATOR);
		String tmpPath = File_SEPORATOR;
		if (filePaths.length > 0) {
			for (int i = 0; i < filePaths.length; i++) {
				if (CommonFunctions.isEmpty(filePaths[i]))
					continue;
				// 组装子目录的父亲目录
				if (i == (filePaths.length - 1)) {// 最后一个节点时
					tmpPath = tmpPath + filePaths[i];
				} else {
					tmpPath = tmpPath + filePaths[i] + File_SEPORATOR;
				}

				File dest = new File(output, tmpPath);
				CommonFunctions.createDirectory(dest);
			}
		} else {// 如果没有双层目录,则直接在根目录创建文件夹
			File dest = new File(output, filePath);
			CommonFunctions.createDirectory(dest);
		}
	}

}
