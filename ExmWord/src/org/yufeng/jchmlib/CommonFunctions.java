package org.yufeng.jchmlib;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CommonFunctions {
	/**
	 * <p>
	 * 判断给定字符串是否为null或""
	 * 
	 * @param s
	 * @return true或false
	 */
	public static boolean isEmptyStr(String s) {
		if (s == null || s.equals("")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * <p>
	 * 判断给定字符串是否为null或""
	 * 
	 * @author
	 * @param s
	 * @return 不为空返回true，否则false
	 */
	public static boolean isNotEmptyStr(String s) {
		return !isEmptyStr(s);
	}

	/**
	 * <p>
	 * 判断给定字符串是否为null或"" 或" "
	 * 
	 * @author
	 * @param s
	 * @return 不为空返回true，否则false
	 */
	public static boolean isNotEmpty(String s) {
		return !isEmpty(s);
	}

	/**
	 * 通用检查特定对象的（无参数）方法返回不能为空的方法
	 * 
	 * @param obj
	 * @param methods
	 * @return
	 * @throws Exception
	 */
	public static String checkNotNull(Object obj, String[][] methods) throws Exception {
		String rtnValue = null;
		for (int i = 0; obj != null && methods != null && i < methods.length; i++) {
			if (obj.getClass().getMethod(methods[i][0], null).invoke(obj, null) == null)
				rtnValue += methods[i][1] + "不能为空！\r\n";
		}
		return rtnValue;
	}

	/**
	 * 字符串的空值处理
	 */
	public static String changeNullStringToEmpty(String str) {
		if ((str == null) || (str.equals("null"))) {
			return "";
		} else {
			return str.trim();
		}
	}

	/**
	 * 将空字符串变为指定的值
	 */
	public static String changeNullStringToValue(String str, String Value) {
		if (str == null) {
			return Value;
		} else {
			return str.trim();
		}
	}

	/**
	 * 将常用时间字符串转换为日期对象
	 * 
	 * @param String
	 * @return java.util.Calendar
	 */
	public static Calendar parseCalendar(String dateStr) {
		Calendar calendar = Calendar.getInstance();
		try {
			java.util.Date utilDate = parseTime(dateStr);
			if (utilDate != null) {
				calendar.setTime(utilDate);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return calendar;
	}

	/**
	 * 将常用时间字符串转换为日期对象
	 * 
	 * @param String
	 * @return java.sql.Date
	 */
	public static Date parseDate(String date) {
		Date rtnDate = null;
		try {
			java.util.Date utilDate = parseTime(date);
			if (utilDate != null) {
				rtnDate = new Date(utilDate.getTime());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return rtnDate;
	}

	/**
	 * 将常用时间字符串转换为时间对象 zxs 2007-12-25
	 * 
	 * @param String
	 * @return java.util.Date
	 */
	public static java.util.Date parseTime(String date) {
		SimpleDateFormat df = new SimpleDateFormat();
		java.util.Date rtnDate = null;
		if (date == null || date.trim().equals("")
				|| date.trim().equals("null"))
			return rtnDate;
		try {
			date = date.trim();
			int length = date.length();
			if (date.indexOf("-") != -1) {
				if (length == 5) {
					if (date.indexOf("-") == length - 1) {// 2008-
						df.applyPattern("yyyy");
						date = date.substring(0, 4);
						rtnDate = df.parse(date);
					} else {
						df.applyPattern("yyyy-MM");// 2008-01
						rtnDate = df.parse(date);
					}
				} else if (length >= 6 && length <= 7) {// 2008-1 -- 2008-01
					df.applyPattern("yyyy-MM");
					rtnDate = df.parse(date);
				} else if (length >= 8 && length <= 9) {
					if (date.lastIndexOf("-") == length - 1) { // 2008-12-
						df.applyPattern("yyyy-MM");
						date = date.substring(0, length - 1);
						rtnDate = df.parse(date);
					} else {
						df.applyPattern("yyyy-MM-dd");// 2008-1-1 --
						// 2008-01-01
						rtnDate = df.parse(date);
					}
				} else if (length >= 10 && length <= 11) {
					if (date.indexOf(" ") > -1
							&& date.indexOf(" ") < length - 1) {
						df.applyPattern("yyyy-MM-dd HH");// 2008-1-1 1 --
						// 2008-1-1 11 中间有空格
						rtnDate = df.parse(date);
					} else {
						df.applyPattern("yyyy-MM-dd");// "2008-01-01"中间无空格
						rtnDate = df.parse(date);
					}
				} else if (length >= 12 && length <= 13) {
					if (date.indexOf(":") > -1
							&& date.indexOf(":") < length - 1) {
						df.applyPattern("yyyy-MM-dd HH:mm");// 2008-1-1 1:1 --
						// 2008-1-1 1:01
						// 中间有冒号
						rtnDate = df.parse(date);
					} else {
						df.applyPattern("yyyy-MM-dd HH");// 2008-01-01 01
						// 中间有空格
						rtnDate = df.parse(date);
					}
				} else if (length >= 14 && length <= 16) {
					int lastIndex = date.lastIndexOf(":");
					if (date.indexOf(":") > -1 && lastIndex < length - 1
							&& date.indexOf(":") != lastIndex) {
						df.applyPattern("yyyy-MM-dd HH:mm:ss");// 2008-1-1
						// 1:1:1 --
						// 2008-01-01
						// 1:1:1 中间有两个冒号
						if (lastIndex < length - 1 - 2) {
							date = date.substring(0, lastIndex + 3);
						}
						rtnDate = df.parse(date);
					} else if (date.indexOf(":") > -1 && lastIndex < length - 1
							&& date.indexOf(":") == lastIndex) {
						df.applyPattern("yyyy-MM-dd HH:mm");// 2008-01-01 1:1 --
						// 2008-01-01
						// 01:01中间只有一个冒号
						rtnDate = df.parse(date);
					} else if (date.indexOf(":") > -1
							&& lastIndex == length - 1
							&& date.indexOf(":") == lastIndex) {
						df.applyPattern("yyyy-MM-dd HH");// 2008-01-01 01:
						// 只有一个冒号在末尾
						date = date.substring(0, length - 1);
						rtnDate = df.parse(date);
					}
				} else if (length == 17) {
					int lastIndex = date.lastIndexOf(":");
					if (lastIndex < length - 1) {
						df.applyPattern("yyyy-MM-dd HH:mm:ss");// 2008-1-1
						// 1:1:1 --
						// 2008-01-01
						// 1:1:1 中间有两个冒号
						if (lastIndex < length - 1 - 2) {
							date = date.substring(0, lastIndex + 3);
						}
						rtnDate = df.parse(date);
					} else if (lastIndex == length - 1) {
						df.applyPattern("yyyy-MM-dd HH:mm");// 2008-01-01 1:1 --
						// 2008-01-01
						// 01:01中间只有一个冒号
						date = date.substring(0, length - 1);
						rtnDate = df.parse(date);
					}
				} else if (length >= 18) {
					df.applyPattern("yyyy-MM-dd HH:mm:ss");// 2008-1-1 1:1:1 --
					// 2008-01-01
					// 01:01:01 有两个冒号
					int lastIndex = date.lastIndexOf(":");
					if (lastIndex < length - 1 - 2) {
						date = date.substring(0, lastIndex + 3);
					}
					rtnDate = df.parse(date);
				}
			} else if (length == 4) {
				df.applyPattern("yyyy");
				rtnDate = df.parse(date);
			} else if (length >= 5 && length <= 6) {
				df.applyPattern("yyyyMM");
				rtnDate = df.parse(date);
			} else if (length >= 7 && length <= 8) {
				df.applyPattern("yyyyMMdd");
				rtnDate = df.parse(date);
			} else if (length >= 9 && length <= 10) {
				df.applyPattern("yyyyMMddHH");
				rtnDate = df.parse(date);
			} else if (length >= 11 && length <= 12) {
				df.applyPattern("yyyyMMddHHmm");
				rtnDate = df.parse(date);
			} else if (length >= 13 && length <= 14) {
				df.applyPattern("yyyyMMddHHmmss");
				rtnDate = df.parse(date);
			} else if (length >= 15) {
				df.applyPattern("yyyyMMddHHmmss");
				date = date.substring(0, 14);
				rtnDate = df.parse(date);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return rtnDate;
	}

	/**
	 * 检查字符串是否为空
	 */
	public static boolean checkStringIsEmpty(String str) {
		if (str == null || str.trim().equals("")
				|| str.equalsIgnoreCase("null")) {
			return true;
		}

		return false;
	}

	/**
	 * 返回true表示字符串为null或者trim后为""
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		if (str == null || str.trim().equals(""))
			return true;
		else
			return false;
	}

	public static String serializeArrayString(String[] str) {
		if (str == null || str.length == 0)
			return "";
		else if (str.length == 1)
			return str[0];
		else {
			String serializeStr = "";
			for (int i = 0; i < str.length - 1; i++) {
				serializeStr = serializeStr + str[0] + ",";
			}
			serializeStr = serializeStr + str[str.length - 1];
			return serializeStr;
		}
	}

	public static String echoExceptionTrace(Exception ex) {
		StringWriter sw = new StringWriter();
		ex.printStackTrace(new PrintWriter(sw));
		String exMsg = sw.toString();
		return exMsg;
	}

	/**
	 * 根据文件获取扩展名
	 * 
	 * @see #getExtension
	 */
	public static String getExtension(File f) {
		return (f != null) ? getExtension(f.getName()) : "";
	}

	public static String getExtension(String filename) {
		return getExtension(filename, "");
	}

	/**
	 * @author LUKEBIN 根据文件名获取扩展名 DES:获取文件的扩展名，如果没有，则返回空
	 * @param filename
	 * @param defExt
	 * @return
	 */
	public static String getExtension(String filename, String defExt) {
		if ((filename != null) && (filename.length() > 0)) {
			int i = filename.lastIndexOf('.');

			if ((i > -1) && (i < (filename.length() - 1))) {
				return filename.substring(i + 1);
			}
		}
		return changeNullStringToEmpty(defExt);
	}

	/**
	 * @author Administrator DES:获取文件的扩展名，如果没有，则返回文件名
	 * @param filename
	 * @return
	 */
	public static String trimExtension(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int i = filename.lastIndexOf('.');
			if ((i > -1) && (i < (filename.length()))) {
				return filename.substring(0, i);
			}
		}
		return filename;
	}

	// 将字符串按指定的分隔符转换成字符串数组
	public static String[] strToArray(String str, String separator) {
		if (str == null || str.trim().equals("")) {
			return null;
		}

		int index = 0;
		int index2 = 0;
		index2 = str.indexOf(separator);
		List<String> list = new ArrayList<String>();
		String temp = "";
		while (index2 != -1) {
			temp = str.substring(index, index2);
			list.add(temp);
			index = index2 + 1;
			index2 = str.indexOf(separator, index);
			if (index2 == -1) {
				temp = str.substring(index);
				// temp);
				list.add(temp);
			}
		}
		int size = list.size();
		String[] arrStr = new String[size];
		for (int i = 0; i < size; i++) {
			arrStr[i] = (String) list.get(i);
		}
		return arrStr;
	}
//
//	private static void doWriteTxt(String file, String txt) {
//		try {
//			FileOutputStream os = new FileOutputStream(new File(file), true);
//			os.write((txt + "\n").getBytes());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	/***
	 * 将给定的字符串转换为UTF编码的字符串。
	 * 
	 * @param str
	 *            输入字符串
	 * @return 经UTF编码后的字符串，如果有异常，则返回原编码字符串
	 **/
	public static String toGBK(String str) throws Exception {
		if (isEmpty(str))
			return "";
		String retVal = str;
		try {
			retVal = new String(str.getBytes("ISO8859_1"), "GBK");
		} catch (Exception e) {
			throw new Exception(e);
		}
		return retVal;
	}

	/**
	 * 根据文件名创建文件夹
	 * 
	 * @param output
	 * @param filePath
	 * @throws Exception
	 */
	public static void createDirectory(File filePath) throws Exception {
		if (!filePath.isDirectory()) {
			if (!filePath.mkdirs()) {
				throw new IOException("failed to create directory : "
						+ filePath);
			}
		}
	}

	/**
	 * 取得当前的时间的字符串
	 * 
	 * @param dString
	 * @return
	 */
	public static String getDateMMSS(Date date) throws Exception {
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateStr = null;
		try {
			dateStr = formatter.format(date);
		} catch (Exception e) {
			throw new Exception(e);
		}
		return dateStr;
	}
}
