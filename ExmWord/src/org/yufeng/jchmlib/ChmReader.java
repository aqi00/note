/*
 * ChmReader.java
 ***************************************************************************************
 * Author: Feng Yu. <yfbio@hotmail.com>
 *org.yufeng.jchmlib 
 *version: 1.0
 ****************************************************************************************
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 **********************************************************************************************/

package org.yufeng.jchmlib;

import java.io.*;
import java.util.*;

/**
 * 
 * @author yufeng
 */
public class ChmReader {

	/** Creates a new instance of ChmReader */
	public ChmReader() {
	}

	public static void main(String[] args) {
		// --enumerateall;
		mysnail("E:/javaprograms/jchmlib");// "F:/ebook");

		// --extract one
		String chmfile = "JAVA.start.chm";// micro.fictions.chm";
		String htmlf = "/HelpFile.hhk";// "/HelpFile.hhk";//"/HTML/Figs/G301.gif";//"/javalogo52x88.gif";//"/Index.hhk";//"/java/util/prefs/Descr.WD3";//"/api.HHC";//"/content.html";//"/java/lang/reflect/package-summary.html";
		String savef = "javastart.hhk";// "j2se.hhc";
		extractOne(chmfile, htmlf, savef);
		// showOne(chmfile,htmlf);

		// --extract all
		// extractAll("D:/lay", "lay");
		// extractAll("F:/ebook", "b");
		// extractAll("gen", "genex");

		// ChmManager cm=new ChmManager(chmfile);
		// System.out.println(cm.retrieveFile(htmlf));
	}

	public static void showOne(String chmfile, String htmlf) {
		ChmManager cm = new ChmManager(chmfile);// "micro.fictions.chm");
		showOne(cm, htmlf);
	}

	public static void showOne(ChmManager cm, String htmlf) {
		// ChmManager cm=new ChmManager(chmfile);//"micro.fictions.chm");
		byte[][] tmp = cm.retrieveObject(htmlf);
		showFile(tmp);
	}

	private static void showFile(byte[][] tmp) {
		String[] s = new String[tmp.length];
		try {
			for (int i = 0; i < tmp.length; i++) {
				s[i] = new String(tmp[i], "utf8");// "ISO8859-1");//gbk
				System.out.println(s[i]);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public static void extractOne(ChmManager cm, FileEntry fe, String savef) {
		byte[][] tmp = cm.retrieveObject(fe);// "/mini.GIF");///java/awt/CardLayout.html");///java/awt/AWTEvent.html");///javalogo52x88.gif");////test.html");//"/test.html");//"/����.html");//content.html");//
		writeFile(tmp, savef);
	}

	public static void extractOne(String chmfile, String htmlf, String savef) {
		ChmManager cm = new ChmManager(chmfile);// "micro.fictions.chm");
		extractOne(cm, htmlf, savef);
	}

	public static void extractOne(ChmManager cm, String htmlf, String savef) {
		byte[][] tmp = cm.retrieveObject(htmlf);// "/mini.GIF");///java/awt/CardLayout.html");///java/awt/AWTEvent.html");///javalogo52x88.gif");////test.html");//"/test.html");//"/����.html");//content.html");//
		writeFile(tmp, savef);
		// if(cm.open("utest.chm"))

		// cm.lzx.decompress();
		// cm.readBlock();
		// System.out.println(cm.read("/content.html"));

	}

	private static void writeFile(byte[][] tmp, String savef) {
		FileOutputStream fops = null, fops1 = null;
		try {
			fops = new FileOutputStream(savef);// "a/mini.GIF"); //cann't use
												// path like /lay/layout.html
			// fops1=new FileOutputStream("content1.html");
			// DataPutStream dps=new DataPutStream(fops);
			// dps.
			for (int i = 0; i < tmp.length; i++)
				fops.write(tmp[i]);
			// fops1.write(tmp[1]);
			fops.close();
			// fops1.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void mysnail(String fpath) {
		// System.out.println("........mysnail");
		File tmpf = new File(fpath);
		MyFileFilter mff = new MyFileFilter("chm");
		if (tmpf.isDirectory()) {
			try {
				// System.out.println("-- Begin --\n");
				String[] c = tmpf.list();
				if ((c != null) && (c.length != 0)) {
					for (int i = 0; i < c.length; i++) {
						String tmp_path = fpath + "/" + c[i];
						File tf = new File(tmp_path);
						if (tf.isDirectory()) {
							mysnail(tmp_path);
						}
					}
					String[] li = tmpf.list(mff);
					ChmManager cm = null;
					if (li != null) {
						FileEntry fe;
						File f;
						for (int j = 0; j < li.length; j++) {
							System.out.print("............................");
							System.out.println(li[j]);
							f = new File(li[j]);
							if (f.exists())
								System.out.println("i'm here");
							try {
								cm = new ChmManager(li[j]);
							} catch (Exception e) {
								e.printStackTrace();
							}

							if (cm == null) {
								// hmm,it seemed that files that can't be opened is because they don't exist.
								System.out.println("can't open");
							} else {
								ArrayList<FileEntry> fes = cm.enumerateFiles();
								System.out.println("ok");
								for (int k = 0; k < fes.size(); k++) {
									fe = (FileEntry) (fes.get(k));
									System.out.println(fe.entryName);
								}
							}
							// continue;

						}
					}
				}
				// System.out.println("\n-- End --");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void extractInSitu(String chmfile, String savep) {
//		ChmManager cm = new ChmManager(chmfile);
	}

	public static void extractInSitu(ChmManager cm, String savep) {
		ArrayList<FileEntry> fes = cm.enumerateFiles();
		FileEntry fe;
		String name;
		File f;
		System.out.println("ok");
		for (int k = 0; k < fes.size(); k++) {
			fe = (FileEntry) (fes.get(k));
			name = fe.entryName;
			if (name.lastIndexOf('/') == name.length() - 1) {// dir
				f = new File(savep.concat(name));
				f.mkdirs();
			} else {
				extractOne(cm, fe, savep.concat(name));
			}
			System.out.println(fe.entryName);
		}
	}

	public static void extractAll(String fpath, String savep) {
		// System.out.println("........mysnail");
		File tmpf = new File(fpath);
		MyFileFilter mff = new MyFileFilter("chm");
		if (tmpf.isDirectory()) {
			try {
				// System.out.println("-- Begin --\n");
				String[] c = tmpf.list();
				if ((c != null) && (c.length != 0)) {
					for (int i = 0; i < c.length; i++) {
						String tmp_path = fpath + "/" + c[i];
						File tf = new File(tmp_path);
						if (tf.isDirectory()) {
							extractAll(tmp_path, savep + "/" + c[i]);
						}
					}
					File[] li = tmpf.listFiles(mff);
					ChmManager cm = null;
					if (li != null) {
//						FileEntry fe;
						String f;
						for (int j = 0; j < li.length; j++) {
							System.out.print("............................");
							System.out.println(li[j]);
							f = li[j].getAbsolutePath();
							if (li[j].exists())
								System.out.println("i'm here");
							try {
								cm = new ChmManager(f);
							} catch (Exception e) {
								e.printStackTrace();
							}
							if (cm == null) {
								// hmm,it seemed that files that can't be opened is because they don't exist.
								System.out.println("can't open");
							} else {
								extractInSitu(cm, savep);
							}
						}
						// continue;
					}
				}
				// System.out.println("\n-- End --");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

class MyFileFilter implements FilenameFilter {// it's an interface
	// File f=new File("lay");
	String ext;

	public MyFileFilter(String fileext) {
		ext = "." + fileext;
	}

	public boolean accept(File dir, String name) { //给INTERFACE里方法实体化时，该方法应为public属性
		return name.toLowerCase().endsWith(ext) & dir.canRead();
		// if(dir.getParent()="D:\\lay"&&name.indexOf(name)>-1)return true;
		// else return false;
	}

}
