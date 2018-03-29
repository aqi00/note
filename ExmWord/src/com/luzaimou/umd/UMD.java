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

import java.io.File;
import java.util.List;

public class UMD {
	public final static int POUND = 0x23;
	public final static int DOLLAR = 0x24;
	private int contentId;
	private int header;
	private String title;
	private String author;
	private String year;
	private String month;
	private String day;
	private String gender;
	private String publisher;
	private String vendor;
	private int contentLength;
	private int offsets[];
	private int chapterNumber;
	private List<String> chapterTitles;
	private List<String> chapterContents;
	private String content;
	private byte covers[];
	private int fileSize;
	private File coverFile;

	public int getContentId() {
		return contentId;
	}

	public void setContentId(int contentId) {
		this.contentId = contentId;
	}

	public int getHeader() {
		return header;
	}

	public void setHeader(int header) {
		this.header = header;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public int getContentLength() {
		return contentLength;
	}

	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}

	public int[] getOffsets() {
		return offsets;
	}

	public void setOffsets(int[] offsets) {
		this.offsets = offsets;
	}

	public int getChapterNumber() {
		return chapterNumber;
	}

	public void setChapterNumber(int chapterNumber) {
		this.chapterNumber = chapterNumber;
	}

	public List<String> getChapterTitles() {
		return chapterTitles;
	}

	public void setChapterTitles(List chapterTitles) {
		this.chapterTitles = chapterTitles;
	}

	public List<String> getChapterContents() {
		return chapterContents;
	}

	public void setChapterContents(List chapterContents) {
		this.chapterContents = chapterContents;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public byte[] getCovers() {
		return covers;
	}

	public void setCovers(byte[] covers) {
		this.covers = covers;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public File getCoverFile() {
		return coverFile;
	}

	public void setCoverFile(File coverFile) {
		this.coverFile = coverFile;
	}

	public void update() {
		if (chapterTitles != null) {
			chapterNumber = chapterTitles.size();
		}
		if (content != null) {
			contentLength = content.length();
		}
	}

}
