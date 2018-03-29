/*------------------------------------------------------------------------------
 * COPYRIGHT liuyuan 2009
 *
 * The copyright to the computer program(s) herein is the property of
 * MR.Yuan Liu . The programs may be used and/or copied only with written
 * permission from MR.Yuan Liu. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *----------------------------------------------------------------------------*/
package com.luzaimou.umd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.InflaterOutputStream;

public class UMDDecoder {
	public UMD umd = new UMD();

	public UMD decode(File file) throws Exception {
		UMDInputStream in = null;
		int random1;
		int random2;
		int size = 0;
		StringBuffer buffer;
		StringBuffer bufferContent = new StringBuffer();
		byte b;
		int type;
		try {
			in = new UMDInputStream(new FileInputStream(file));
			int header = in.readInt();
			if (header != -560292983) {
				in.close();
				throw new Exception("It's not a umd file!");
			}
			umd.setHeader(header);

			boolean flag = true;
			b = in.readByte();
			while (flag) {
				switch (b) {
				case '#':
					type = in.readUnsignedShort();
					switch (type) {
					case 1:
						in.skipBytes(2);
						int umd_type = in.readUnsignedByte();
						if (umd_type != 1) {
							in.close();
							throw new Exception("It's not a text umd file!");
						}
						in.skipBytes(2);
						break;
					case 2:
						buffer = new StringBuffer();
						in.skipBytes(1);
						size = in.readUnsignedByte();
						for (int i = 0; i < (size - 5) / 2; i++) {
							char c = in.readChar();
							buffer.append(c);
						}
						umd.setTitle(buffer.toString());
						break;
					case 3:
						buffer = new StringBuffer();
						in.skipBytes(1);
						size = in.readUnsignedByte();
						for (int i = 0; i < (size - 5) / 2; i++) {
							char c = in.readChar();
							buffer.append(c);
						}
						umd.setAuthor(buffer.toString());
						break;
					case 4:
						buffer = new StringBuffer();
						in.skipBytes(1);
						size = in.readUnsignedByte();
						for (int i = 0; i < (size - 5) / 2; i++) {
							char c = in.readChar();
							buffer.append(c);
						}
						umd.setYear(buffer.toString());
						break;
					case 5:
						buffer = new StringBuffer();
						in.skipBytes(1);
						size = in.readUnsignedByte();
						for (int i = 0; i < (size - 5) / 2; i++) {
							char c = in.readChar();
							buffer.append(c);
						}
						umd.setMonth(buffer.toString());
						break;
					case 6:
						buffer = new StringBuffer();
						in.skipBytes(1);
						size = in.readUnsignedByte();
						for (int i = 0; i < (size - 5) / 2; i++) {
							char c = in.readChar();
							buffer.append(c);
						}
						umd.setDay(buffer.toString());
						break;
					case 7:
						buffer = new StringBuffer();
						in.skipBytes(1);
						size = in.readUnsignedByte();
						for (int i = 0; i < (size - 5) / 2; i++) {
							char c = in.readChar();
							buffer.append(c);
						}
						umd.setGender(buffer.toString());
						break;
					case 8:
						buffer = new StringBuffer();
						in.skipBytes(1);
						size = in.readUnsignedByte();
						for (int i = 0; i < (size - 5) / 2; i++) {
							char c = in.readChar();
							buffer.append(c);
						}
						umd.setPublisher(buffer.toString());
						break;
					case 9:
						buffer = new StringBuffer();
						in.skipBytes(1);
						size = in.readUnsignedByte();
						for (int i = 0; i < (size - 5) / 2; i++) {
							char c = in.readChar();
							buffer.append(c);
						}
						umd.setVendor(buffer.toString());
						break;
					case 0x0b:
						in.skipBytes(2);
						umd.setContentLength(in.readInt() / 2);
						break;
					case 0x83:
						in.skipBytes(2);
						random1 = in.readInt();
						in.skipBytes(1);
						random2 = in.readInt();
						if (random1 != random2) {
							in.close();
							throw new Exception("It's not a umd file!");
						}
						umd.setChapterNumber((in.readInt() - 9) / 4);

						int offsets[] = new int[umd.getChapterNumber()];
						for (int i = 0; i < offsets.length; i++) {
							offsets[i] = in.readInt() / 2;
						}
						umd.setOffsets(offsets);
						break;
					case 0x84:
						in.skipBytes(2);
						random1 = in.readInt();
						in.skipBytes(1);
						random2 = in.readInt();
						if (random1 != random2) {
							in.close();
							throw new Exception("It's not a umd file!");
						}

						in.readInt();
						List<String> chapterTitles = new ArrayList<String>();
						for (int i = 0; i < umd.getChapterNumber(); i++) {
							int titleLength = in.readUnsignedByte() / 2;
							StringBuffer titleBuf = new StringBuffer();
							for (int j = 0; j < titleLength; j++) {
								char c = in.readChar();
								titleBuf.append(c);
							}
							chapterTitles.add(titleBuf.toString());
						}
						umd.setChapterTitles(chapterTitles);
						break;
					case 0x81:
						in.skipBytes(2);
						random1 = in.readInt();
						in.skipBytes(1);
						random2 = in.readInt();
						if (random1 != random2) {
							in.close();
							throw new Exception("It's not a umd file!");
						}
						int dataBlockNum = (in.readInt() - 9) / 4;
						in.skipBytes(dataBlockNum * 4);
						break;
					case 0x82:
						in.skipBytes(3);
						random1 = in.readInt();
						in.skipBytes(1);
						random2 = in.readInt();
						if (random1 != random2) {
							in.close();
							throw new Exception("It's not a umd file!");
						}
						int coverLength = in.readInt() - 9;
						byte covers[] = new byte[coverLength];
						in.read(covers);
						umd.setCovers(covers);
						break;
					case 0x87:
						in.skipBytes(4);
						random1 = in.readInt();
						in.skipBytes(1);
						random2 = in.readInt();
						if (random1 != random2) {
							in.close();
							throw new Exception("It's not a umd file!");
						}
						int offsetLentgth = in.readInt() - 9;
						in.skipBytes(offsetLentgth);
						break;
					case 0x0a:
						in.skipBytes(2);
						umd.setContentId(in.readInt());
						break;
					case 0x0c:
						in.skipBytes(2);
						int fileSize = in.readInt();
						umd.setFileSize(fileSize);
						flag = false;
						continue;
					case 0xf1:
						in.skipBytes(2);
						in.skipBytes(16);
						break;
					default:
						flag = false;
						break;
					}
					break;
				case 0x24:
					random1 = in.readInt();
					int dataLength = in.readInt() - 9;
					byte bytes[] = new byte[dataLength];
					in.read(bytes);
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					InflaterOutputStream zos = new InflaterOutputStream(bos);
					zos.write(bytes);
					zos.close();
					//String chapterContents[] = new String[umd.getChapterNumber()];
					UMDInputStream tmp = new UMDInputStream(
							new ByteArrayInputStream(bos.toByteArray()));
					bufferContent.append(tmp.toUnicodeString());
					tmp.close();
					break;
				default:
					flag = false;
					break;
				}
				b = in.readByte();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			umd.setContent(bufferContent.toString());
			if (in != null) {
				in.close();
			}
		}

		String content = umd.getContent();
		int offsets[] = umd.getOffsets();
		List<String> chapterContents = new ArrayList<String>();
		for (int i = 0; i < offsets.length; i++) {
			int begin = offsets[i];
			if (i == offsets.length - 1) {
				chapterContents.add(content.substring(begin));
			} else {
				chapterContents.add(content.substring(begin, offsets[i + 1]));
			}
		}
		umd.setChapterContents(chapterContents);
		umd.setContent(null);
		return umd;
	}
}
