package com.example.exmword.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Table;
import org.apache.poi.hwpf.usermodel.TableCell;
import org.apache.poi.hwpf.usermodel.TableIterator;
import org.apache.poi.hwpf.usermodel.TableRow;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.xmlpull.v1.XmlPullParser;

import android.util.Log;
import android.util.Xml;

public class WordUtil {
	private final static String TAG = "WordUtil";
	public String htmlPath;
	private String docPath;
	private String picturePath;
	private List<Picture> pictures;
	private TableIterator tableIterator;
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

	public WordUtil(String doc_name) {
		docPath = doc_name;
		htmlPath = FileUtil.createFile("html", FileUtil.getFileName(docPath) + ".html");
		Log.d(TAG, "htmlPath=" + htmlPath);
		try {
			output = new FileOutputStream(new File(htmlPath));
			presentPicture = 0;
			output.write(htmlBegin.getBytes());
			if (docPath.endsWith(".doc")) {
				readDOC();
			} else if (docPath.endsWith(".docx")) {
				readDOCX();
			}
			output.write(htmlEnd.getBytes());
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//读取word中的内容并写到sd卡上的html文件中
	private void readDOC() {
		try {
			FileInputStream in = new FileInputStream(docPath);
			POIFSFileSystem pfs = new POIFSFileSystem(in);
			HWPFDocument hwpf = new HWPFDocument(pfs);
			Range range = hwpf.getRange();
			pictures = hwpf.getPicturesTable().getAllPictures();
			tableIterator = new TableIterator(range);
			int numParagraphs = range.numParagraphs();// 得到页面所有的段落数
			for (int i = 0; i < numParagraphs; i++) { // 遍历段落数
				Paragraph p = range.getParagraph(i); // 得到文档中的每一个段落
				if (p.isInTable()) {
					int temp = i;
					if (tableIterator.hasNext()) {
						Table table = tableIterator.next();
						output.write(tableBegin.getBytes());
						int rows = table.numRows();
						for (int r = 0; r < rows; r++) {
							output.write(rowBegin.getBytes());
							TableRow row = table.getRow(r);
							int cols = row.numCells();
							int rowNumParagraphs = row.numParagraphs();
							int colsNumParagraphs = 0;
							for (int c = 0; c < cols; c++) {
								output.write(columnBegin.getBytes());
								TableCell cell = row.getCell(c);
								int max = temp + cell.numParagraphs();
								colsNumParagraphs = colsNumParagraphs + cell.numParagraphs();
								for (int cp = temp; cp < max; cp++) {
									Paragraph p1 = range.getParagraph(cp);
									output.write(lineBegin.getBytes());
									writeParagraphContent(p1);
									output.write(lineEnd.getBytes());
									temp++;
								}
								output.write(columnEnd.getBytes());
							}
							int max1 = temp + rowNumParagraphs;
							for (int m = temp + colsNumParagraphs; m < max1; m++) {
								temp++;
							}
							output.write(rowEnd.getBytes());
						}
						output.write(tableEnd.getBytes());
					}
					i = temp;
				} else {
					output.write(lineBegin.getBytes());
					writeParagraphContent(p);
					output.write(lineEnd.getBytes());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void readDOCX() {
		try {
			ZipFile docxFile = new ZipFile(new File(docPath));
			ZipEntry sharedStringXML = docxFile.getEntry("word/document.xml");
			InputStream inputStream = docxFile.getInputStream(sharedStringXML);
			XmlPullParser xmlParser = Xml.newPullParser();
			xmlParser.setInput(inputStream, "utf-8");
			boolean isTable = false; // 表格
			boolean isSize = false; // 文字大小
			boolean isColor = false; // 文字颜色
			boolean isCenter = false; // 居中对齐
			boolean isRight = false; // 靠右对齐
			boolean isItalic = false; // 斜体
			boolean isUnderline = false; // 下划线
			boolean isBold = false; // 加粗
			boolean isRegion = false; // 在那个区域中
			int pic_index = 1; // docx中的图片名从image1开始，所以索引从1开始
			int event_type = xmlParser.getEventType();
			while (event_type != XmlPullParser.END_DOCUMENT) {
				switch (event_type) {
				case XmlPullParser.START_TAG: // 开始标签
					String tagBegin = xmlParser.getName();
					if (tagBegin.equalsIgnoreCase("r")) {
						isRegion = true;
					}
					if (tagBegin.equalsIgnoreCase("jc")) { // 判断对齐方式
						String align = xmlParser.getAttributeValue(0);
						if (align.equals("center")) {
							output.write(centerBegin.getBytes());
							isCenter = true;
						}
						if (align.equals("right")) {
							output.write(divRight.getBytes());
							isRight = true;
						}
					}
					if (tagBegin.equalsIgnoreCase("color")) { // 判断文字颜色
						String color = xmlParser.getAttributeValue(0);
						output.write(String.format(spanColor, color).getBytes());
						isColor = true;
					}
					if (tagBegin.equalsIgnoreCase("sz")) { // 判断文字大小
						if (isRegion == true) {
							int size = getSize(Integer.valueOf(xmlParser.getAttributeValue(0)));
							output.write(String.format(fontSizeTag, size).getBytes());
							isSize = true;
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
						ZipEntry pic_entry = FileUtil.getPicEntry(docxFile, "word", pic_index);
						if (pic_entry != null) {
							byte[] pictureBytes = FileUtil.getPictureBytes(docxFile, pic_entry);
							writeDocumentPicture(pictureBytes);
						}
						pic_index++; // 转换一张后，索引+1
					}
					if (tagBegin.equalsIgnoreCase("p") && !isTable) {// 检测到段落，如果在表格中就无视
						output.write(lineBegin.getBytes());
					}
					if (tagBegin.equalsIgnoreCase("b")) { // 检测到加粗
						isBold = true;
					}
					if (tagBegin.equalsIgnoreCase("u")) { // 检测到下划线
						isUnderline = true;
					}
					if (tagBegin.equalsIgnoreCase("i")) { // 检测到斜体
						isItalic = true;
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
//						if (isCenter == true) { // 输入居中结束标签</center>。要在段落结束之前再输入该标签，因为该标签会强制换行
//							output.write(centerEnd.getBytes());
//							isCenter = false;
//						}
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
					if (tagEnd.equalsIgnoreCase("r")) {
						isRegion = false;
					}
					break;
				default:
					break;
				}
				event_type = xmlParser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int getSize(int sizeType) {
		if (sizeType >= 1 && sizeType <= 8) {
			return 1;
		} else if (sizeType >= 9 && sizeType <= 11) {
			return 2;
		} else if (sizeType >= 12 && sizeType <= 14) {
			return 3;
		} else if (sizeType >= 15 && sizeType <= 19) {
			return 4;
		} else if (sizeType >= 20 && sizeType <= 29) {
			return 5;
		} else if (sizeType >= 30 && sizeType <= 39) {
			return 6;
		} else if (sizeType >= 40) {
			return 7;
		} else {
			return 3;
		}
	}

	private String getColor(int colorType) {
		if (colorType == 1) {
			return "#000000";
		} else if (colorType == 2) {
			return "#0000FF";
		} else if (colorType == 3 || colorType == 4) {
			return "#00FF00";
		} else if (colorType == 5 || colorType == 6) {
			return "#FF0000";
		} else if (colorType == 7) {
			return "#FFFF00";
		} else if (colorType == 8) {
			return "#FFFFFF";
		} else if (colorType == 9 || colorType == 15) {
			return "#CCCCCC";
		} else if (colorType == 10 || colorType == 11) {
			return "#00FF00";
		} else if (colorType == 12 || colorType == 16) {
			return "#080808";
		} else if (colorType == 13 || colorType == 14) {
			return "#FFFF00";
		} else {
			return "#000000";
		}
	}

	public void writeDocumentPicture(byte[] pictureBytes) {
		picturePath = FileUtil.createFile("html", FileUtil.getFileName(docPath) + presentPicture + ".jpg");
		FileUtil.writeFile(picturePath, pictureBytes);
		presentPicture++;
		String imageString = String.format(imgBegin, picturePath);
		try {
			output.write(imageString.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeParagraphContent(Paragraph paragraph) {
		Paragraph p = paragraph;
		int pnumCharacterRuns = p.numCharacterRuns();
		for (int j = 0; j < pnumCharacterRuns; j++) {
			CharacterRun run = p.getCharacterRun(j);
			if (run.getPicOffset() == 0 || run.getPicOffset() >= 1000) {
				if (presentPicture < pictures.size()) {
					writeDocumentPicture(pictures.get(presentPicture).getContent());
				}
			} else {
				try {
					String text = run.text();
					if (text.length() >= 2 && pnumCharacterRuns < 2) {
						output.write(text.getBytes());
					} else {
						String fontSizeBegin = String.format(fontSizeTag, getSize(run.getFontSize()));
						String fontColorBegin = String.format(fontColorTag, getColor(run.getColor()));
						output.write(fontSizeBegin.getBytes());
						output.write(fontColorBegin.getBytes());
						if (run.isBold()) {
							output.write(boldBegin.getBytes());
						}
						if (run.isItalic()) {
							output.write(italicBegin.getBytes());
						}
						output.write(text.getBytes());
						if (run.isBold()) {
							output.write(boldEnd.getBytes());
						}
						if (run.isItalic()) {
							output.write(italicEnd.getBytes());
						}
						output.write(fontEnd.getBytes());
						output.write(fontEnd.getBytes());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
