package com.example.exmword.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.poi.hslf.usermodel.HSLFSlide;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.hslf.usermodel.HSLFTextRun;
import org.xmlpull.v1.XmlPullParser;

import android.util.Log;
import android.util.Xml;

public class PptUtil {

	public static ArrayList<String> readPPT(String path) {
		ArrayList<String> contentArray = new ArrayList<String>();
		try {
			FileInputStream fis = new FileInputStream(new File(path));
			HSLFSlideShow hslf = new HSLFSlideShow(fis);
			List<HSLFSlide> slides = hslf.getSlides();
			for (int i = 0; i < slides.size(); i++) {
				String content = "";
				HSLFSlide item = slides.get(i);
				// 读取一张幻灯片的标题
				//String title = item.getTitle();
				//Log.d(TAG, "i=" + i + ", title=" + title);
				// 读取一张幻灯片的内容(包括标题)
				List<List<HSLFTextParagraph>> tps = item.getTextParagraphs();
				for (int j = 0; j < tps.size(); j++) {
					List<HSLFTextParagraph> tps_row = tps.get(j);
					for (int k = 0; k < tps_row.size(); k++) {
						HSLFTextParagraph tps_item = tps_row.get(k);
						List<HSLFTextRun> trs = tps_item.getTextRuns();
						for (int l = 0; l < trs.size(); l++) {
							HSLFTextRun trs_item = trs.get(l);
							content = String.format("%s%s\n", content, trs_item.getRawText());
//							String debug = String.format("i=%d, j=%d,k=%d,l=%d,text=%s", 
//									i, j, k, l, trs_item.getRawText());
//							Log.d(TAG, debug);
						}
					}
				}
				contentArray.add(content);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return contentArray;
	}

	private final static String TAG = "PptUtil";
//	public String htmlPath;
	public ArrayList<String> htmlArray = new ArrayList<String>();
	private String pptPath;
	private String picturePath;
	private int presentPicture = 0;
	private FileOutputStream output;

	private String htmlBegin = "<html><meta charset=\"utf-8\"><body>";
	private String htmlEnd = "</body></html>";
	private String tableBegin = "<table style=\"border-collapse:collapse\" border=1 bordercolor=\"black\">";
	private String tableEnd = "</table>";
	private String rowBegin = "<tr>", rowEnd = "</tr>";
	private String columnBegin = "<td>", columnEnd = "</td>";
	private String lineBegin = "<p>", lineEnd = "</p>";
	private String centerBegin = "<center>", centerEnd = "</center>";
	private String boldBegin = "<b>", boldEnd = "</b>";
	private String underlineBegin = "<u>", underlineEnd = "</u>";
	private String italicBegin = "<i>", italicEnd = "</i>";
	private String fontSizeTag = "<font size=\"%d\">";
	private String fontColorTag = "<font color=\"%s\">";
	private String fontEnd = "</font>";
	private String spanColor = "<span style=\"color:%s;\">", spanEnd = "</span>";
	private String divRight = "<div align=\"right\">", divEnd = "</div>";
	private String imgBegin = "<img src=\"%s\" >";

	public PptUtil(String ppt_name) {
		pptPath = ppt_name;
		if (pptPath.endsWith(".pptx")) {
			readPPTX(pptPath);
		}
	}

	private void readPPTX(String pptPath) {
		try {
			ZipFile pptxFile = new ZipFile(new File(pptPath));
			int pic_index = 1; // pptx中的图片名从image1开始，所以索引从1开始
			for (int i = 1; i < 100; i++) { // 最多支持100张幻灯片
				String filePath = String.format("%s%d.html", FileUtil.getFileName(pptPath), i);
				String htmlPath = FileUtil.createFile("html", filePath);
				Log.d(TAG, "i="+i+", htmlPath=" + htmlPath);
				output = new FileOutputStream(new File(htmlPath));
				presentPicture = 0;
				output.write(htmlBegin.getBytes());
				
				ZipEntry sharedStringXML = pptxFile.getEntry("ppt/slides/slide" + i + ".xml"); // 获取每张幻灯片
				InputStream inputStream = pptxFile.getInputStream(sharedStringXML);
				XmlPullParser xmlParser = Xml.newPullParser();
				xmlParser.setInput(inputStream, "utf-8");

				boolean isTitle = false; // 标题
				boolean isTable = false; // 表格
				boolean isSize = false; // 文字大小
				boolean isColor = false; // 文字颜色
				boolean isCenter = false; // 居中对齐
				boolean isRight = false; // 靠右对齐
				boolean isItalic = false; // 斜体
				boolean isUnderline = false; // 下划线
				boolean isBold = false; // 加粗
				int event_type = xmlParser.getEventType();// 得到标签类型的状态
				while (event_type != XmlPullParser.END_DOCUMENT) {// 循环读取流
					switch (event_type) {
					case XmlPullParser.START_TAG: // 开始标签
						String tagBegin = xmlParser.getName();
						if (tagBegin.equalsIgnoreCase("ph")) { // 判断是否标题
							String titleType = getAttrValue(xmlParser, "type", "text");
							if (titleType.equals("text")) {
								isTitle = false;
							} else {
								isTitle = true;
								isSize = true;
								if (titleType.equals("ctrTitle")) {
									output.write(centerBegin.getBytes());
									isCenter = true;
									output.write(String.format(fontSizeTag, getSize(60)).getBytes());
								} else if (titleType.equals("subTitle")) {
									output.write(centerBegin.getBytes());
									isCenter = true;
									output.write(String.format(fontSizeTag, getSize(24)).getBytes());
								} else if (titleType.equals("title")) {
									output.write(String.format(fontSizeTag, getSize(44)).getBytes());
								}
							}
						}
						if (tagBegin.equalsIgnoreCase("pPr") && !isTitle) { // 判断对齐方式
							String align = getAttrValue(xmlParser, "algn", "l");
									xmlParser.getAttributeValue(0);
							if (align.equals("ctr")) {
								output.write(centerBegin.getBytes());
								isCenter = true;
							}
							if (align.equals("r")) {
								output.write(divRight.getBytes());
								isRight = true;
							}
						}
						if (tagBegin.equalsIgnoreCase("srgbClr")) { // 判断文字颜色
							String color = xmlParser.getAttributeValue(0);
							output.write(String.format(spanColor, color).getBytes());
							isColor = true;
						}
						if (tagBegin.equalsIgnoreCase("rPr")) {
							if (!isTitle) {
								// 判断文字大小
								String sizeStr = getAttrValue(xmlParser, "sz", "2800");
								int size = getSize(Integer.valueOf(sizeStr)/100);
								output.write(String.format(fontSizeTag, size).getBytes());
								isSize = true;
							}
							// 检测到加粗
							String bStr = getAttrValue(xmlParser, "b", "");
							if (bStr.equals("1")) {
								isBold = true;
							}
							// 检测到斜体
							String iStr = getAttrValue(xmlParser, "i", "");
							if (iStr.equals("1")) {
								isItalic = true;
							}
							// 检测到下划线
							String uStr = getAttrValue(xmlParser, "u", "");
							if (uStr.equals("sng")) {
								isUnderline = true;
							}
						}
						if (tagBegin.equalsIgnoreCase("tbl")) { // 检测到表格
							output.write(tableBegin.getBytes());
							isTable = true;
						} else if (tagBegin.equalsIgnoreCase("tr")) { // 表格行
							output.write(rowBegin.getBytes());
						} else if (tagBegin.equalsIgnoreCase("tc")) { // 表格列
							output.write(columnBegin.getBytes());
						}
						if (tagBegin.equalsIgnoreCase("pic")) { // 检测到图片
							ZipEntry pic_entry = FileUtil.getPicEntry(pptxFile, "ppt", pic_index);
							if (pic_entry != null) {
								byte[] pictureBytes = FileUtil.getPictureBytes(pptxFile, pic_entry);
								writeDocumentPicture(i, pictureBytes);
							}
							pic_index++; // 转换一张后，索引+1
						}
						if (tagBegin.equalsIgnoreCase("p") && !isTable) {// 检测到段落，如果在表格中就无视
							output.write(lineBegin.getBytes());
						}
						// 检测到文本
						if (tagBegin.equalsIgnoreCase("t")) {
							if (isBold == true) { // 加粗
								output.write(boldBegin.getBytes());
							}
							if (isUnderline == true) { // 检测到下划线，输入<u>
								output.write(underlineBegin.getBytes());
							}
							if (isItalic == true) { // 检测到斜体，输入<i>
								output.write(italicBegin.getBytes());
							}
							String text = xmlParser.nextText();
							output.write(text.getBytes()); // 写入文本
							if (isItalic == true) { // 输入斜体结束标签</i>
								output.write(italicEnd.getBytes());
								isItalic = false;
							}
							if (isUnderline == true) { // 输入下划线结束标签</u>
								output.write(underlineEnd.getBytes());
								isUnderline = false;
							}
							if (isBold == true) { // 输入加粗结束标签</b>
								output.write(boldEnd.getBytes());
								isBold = false;
							}
							if (isSize == true) { // 输入字体结束标签</font>
								output.write(fontEnd.getBytes());
								isSize = false;
							}
							if (isColor == true) { // 输入跨度结束标签</span>
								output.write(spanEnd.getBytes());
								isColor = false;
							}
//							if (isCenter == true) { // 输入居中结束标签</center>。要在段落结束之前再输入该标签，因为该标签会强制换行
//								output.write(centerEnd.getBytes());
//								isCenter = false;
//							}
							if (isRight == true) { // 输入区块结束标签</div>
								output.write(divEnd.getBytes());
								isRight = false;
							}
						}
						break;
					// 结束标签
					case XmlPullParser.END_TAG:
						String tagEnd = xmlParser.getName();
						if (tagEnd.equalsIgnoreCase("tbl")) { // 输入表格结束标签</table>
							output.write(tableEnd.getBytes());
							isTable = false;
						}
						if (tagEnd.equalsIgnoreCase("tr")) { // 输入表格行结束标签</tr>
							output.write(rowEnd.getBytes());
						}
						if (tagEnd.equalsIgnoreCase("tc")) { // 输入表格列结束标签</td>
							output.write(columnEnd.getBytes());
						}
						if (tagEnd.equalsIgnoreCase("p")) { // 输入段落结束标签</p>，如果在表格中就无视
							if (isTable == false) {
								if (isCenter == true) { // 输入居中结束标签</center>
									output.write(centerEnd.getBytes());
									isCenter = false;
								}
								output.write(lineEnd.getBytes());
							}
						}
						break;
					default:
						break;
					}
					event_type = xmlParser.next();// 读取下一个标签
				}
				output.write(htmlEnd.getBytes());
				output.close();
				htmlArray.add(htmlPath);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String getAttrValue(XmlPullParser xmlParser, String attr, String defaultVal) {
		String value = xmlParser.getAttributeValue(null, attr);
		value = (value==null? defaultVal : value);
		return value;
	}

	private int getSize(int sizeType) {
		if (sizeType >= 1 && sizeType <= 9) {
			return 1;
		} else if (sizeType >= 10 && sizeType <= 14) {
			return 2;
		} else if (sizeType >= 15 && sizeType <= 19) {
			return 3;
		} else if (sizeType >= 20 && sizeType <= 24) {
			return 4;
		} else if (sizeType >= 25 && sizeType <= 29) {
			return 5;
		} else if (sizeType >= 30 && sizeType <= 39) {
			return 6;
		} else if (sizeType >= 40) {
			return 7;
		} else {
			return 3;
		}
	}

	public void writeDocumentPicture(int index, byte[] pictureBytes) {
		String filePath = String.format("%s%d_%d.jpg", FileUtil.getFileName(pptPath), index, presentPicture);
		picturePath = FileUtil.createFile("html", filePath);
		FileUtil.writeFile(picturePath, pictureBytes);
		presentPicture++;
		String imageString = String.format(imgBegin, picturePath);
		try {
			output.write(imageString.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
