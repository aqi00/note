/**
 * ChmManager.java
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
import java.math.*;
import java.util.*;

/***
 * 
 * @author yufeng the main body to control the reading process of chm
 */

public class ChmManager {
	/**
	 * these four String is useful for us to get system information of a chm
	 * file
	 */
	private static String CHMU_SPANINFO = "::DataSpace/Storage/MSCompressed/SpanInfo";
	private static String CHMU_RESET_TABLE = "::DataSpace/Storage/MSCompressed/Transform/{7FC28940-9D31-11D0-9B27-00A0C91E9C7C}/InstanceData/ResetTable";
	private static String CHMU_CONTENT = "::DataSpace/Storage/MSCompressed/Content";
	private static String CHMU_LZXC_CONTROLDATA = "::DataSpace/Storage/MSCompressed/ControlData";
	private static int MAX_CACHED_BLOCKS = 100;
	// public String comlen;
	// public String dirlen;
	// private static int ItsfHeadLen=60;
	// char[] itsfhead=new char[ItsfHeadLen] ;
	/** __attribute__ ((aligned (1))); */
	/*** Creates a new instance of ChmManager */
	RandomAccessChm fr = null;
	ChmItsfHeader cih;
	ChmItspHeader cip;
	ChmPmglSection cps;
	ChmPmgiSection cpi;
	// ChmIndexSection cds;
	ChmLzxcResetTable lrt;
	ChmLzxcControlData clcd;
	// Lzx lzx;
	long dirOffset;
	String encoding = "gbk";
	BigInteger dataOffset;
	BigInteger lzxBlockOffset;
	BigInteger lzxBlockLen;
	int bytesPerBlock;
	long windowSize;

	byte[] spaninfo;
	ArrayList<LzxBlock> lzxBlocksCache;

	// int firstCachedBlock;
	public ChmManager() {
	}

	/**
	 * enumerate all content files in chm
	 */
	public ArrayList<FileEntry> enumerateFiles() {
		ArrayList<FileEntry> tmp = enumerateAll();
		ArrayList<FileEntry> res = new ArrayList<FileEntry>();
		FileEntry fe;
		String name;
		for (int i = 0; i < tmp.size(); i++) {
			fe = (FileEntry) tmp.get(i);
			name = fe.entryName;
			if (name.startsWith("/") && name.length() > 1) { // kicks system info and first /
				name = name.substring(1);
				if (name.startsWith("#") || name.startsWith("$"))
					continue;
				else
					res.add(fe);
			}
		}
		return res;
	}

	/**
	 * enumerate all thing which is listed in pmgls
	 */
	public ArrayList<FileEntry> enumerateAll() {
		ArrayList<FileEntry> tmp = new ArrayList<FileEntry>();
		int i;
		ChmPmglSection enucpl = resolvePmgl(cip.first_pmglblock);
		if (enucpl == null)
			return null;
		while (enucpl != null) {
			for (i = 0; i < enucpl.fileEntries.size(); i++)
				tmp.add(enucpl.fileEntries.get(i));
			enucpl = resolvePmgl(enucpl.block_next);
		}
		return tmp;
	}

	/**
	 * this will set the encoding which is necessary for read file content
	 */
	private void setEncoding() {
		switch ((int) cih.lang_id) {
		case 0x0804:
			encoding = "gbk";
			break;
		case 0x0404:// tw
			encoding = "big5";
			break;
		case 0xc04:// hk
			encoding = "big5";
			break;
		case 0x0401:// Arabic
			encoding = "iso-8859-6";
			break;
		case 0x0405:// cz //hungarian
			encoding = "ISO-8859-2";
			break;
		case 0x0408:// greek
			encoding = "ISO-8859-7";
			break;
		case 0x040D:// hebrew
			encoding = "ISO-8859-8";
			break;
		case 0x0411:// japanese
			encoding = "euc-jp";
			break;
		case 0x0412:// korean
			encoding = "euc-kr";
			break;
		case 0x0419:// russian
			encoding = "ISO-8859-5";
			break;
		case 0x041F:// turkish
			encoding = "ISO-8859-9";
			break;
		default:// 0x0409 en,0x0407 de: 0x0416 ://brazilla
			encoding = "iso-8859-1";
			break;
		}
	}

	/**
	 * this will use the chm file name to construct a chmmanager object, it
	 * initialize everything needed for chm reading
	 */
	public ChmManager(String file) {
//		byte[] b;
		// ChmSeg cc
		try {
			fr = new RandomAccessChm(file, "r");
		} catch (IOException e) {
			e.printStackTrace();
		}
		// b=new byte[ChmItsfHeader.LEN_MAX];
		// if(fr.read(b)==ChmItsfHeader.LEN_MAX)
		cih = new ChmItsfHeader(getChmSeg(0, ChmItsfHeader.LEN_MAX));
		dataOffset = cih.data_offset;
		setEncoding();

		// b=new byte[ChmItspHeader.LEN_MAX];
		// fr.seek(cih.dir_offset);
		// if(fr.read(b)==ChmItspHeader.LEN_MAX)
		cip = new ChmItspHeader(getChmSeg(cih.dir_offset, ChmItspHeader.LEN_MAX));
		dirOffset = cih.dir_offset.longValue() + cip.header_len;

		cpi = resolvePmgi();
		FileEntry fe = resolveObject(CHMU_SPANINFO);
		spaninfo = getChmSeg(dataOffset.add(fe.offset).longValue(),
				fe.length.intValue()).getBytes(fe.length.intValue());
		fe = resolveObject(CHMU_RESET_TABLE);
		lrt = new ChmLzxcResetTable(getChmSeg(dataOffset.add(fe.offset)
				.longValue(), fe.length.intValue()));
		bytesPerBlock = lrt.block_len.intValue();
		fe = resolveObject(CHMU_CONTENT);
		lzxBlockOffset = fe.offset.add(cih.data_offset);
		lzxBlockLen = fe.length;
		fe = resolveObject(CHMU_LZXC_CONTROLDATA);
		clcd = new ChmLzxcControlData(getChmSeg(dataOffset.add(fe.offset)
				.longValue(), fe.length.intValue()));
		windowSize = clcd.windowSize;
		lzxBlocksCache = new ArrayList<LzxBlock>();
		// firstCachedBlock=0;
		// int a=1;
	}

	/**
	 * get the specified text file,the name should have no any different with
	 * that listed in Pmgls, including path,
	 */
	public String retrieveFile(String filename) {
		FileEntry fe = resolveObject(filename);
		return retrieveFile(fe);
	}

	/**
	 * get the specified text file,the name should have no any different with
	 * that listed in Pmgls, including path,
	 */
	public String retrieveFile(FileEntry fe) {
		String tmp = "";// ,s; //tmp should be an empty string,not null string
		byte[][] b = retrieveObject(fe);
		try {
			for (int i = 0; i < b.length; i++) {
				tmp = tmp.concat(new String(b[i], encoding)); // should get
																// return value
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return tmp;
	}

	/**
	 * get any specified file,which could be a text file or a picture the name
	 * should have no any different with that listed in Pmgls, including path,
	 */
	public byte[][] retrieveObject(String filename) {
		FileEntry fe = resolveObject(filename);
		return retrieveObject(fe);
	}

	/**
	 * get any specified file,which could be a text file or a picture the name
	 * should have no any different with that listed in Pmgls, including path,
	 */
	public byte[][] retrieveObject(FileEntry fe) {
		byte[][] tmp = null;
//		ChmSeg cs;
		BlocksBounds bb;
		int i = 0, start = 0, block = 0;
		LzxBlock lzxBlock;
		if (fe.compressType == CompressType.CHM_UNCOMPRESSED) // uncompressed
			tmp[0] = (getChmSeg(dataOffset.add(fe.offset).longValue(),
					fe.length.intValue())).getBytes(fe.length.intValue());
		else if (fe.compressType == CompressType.CHM_COMPRESSED) { // compressed
			bb = getBlocksBounds(fe);
			tmp = new byte[bb.endBlock - bb.startBlock + 1][];
			// if(bb.startBlock/clcd.resetInterval)
			// for(int j=bb.startBlock;j<=bb.endBlock;j++)
			if (lzxBlocksCache.size() != 0) {
				for (i = 0; i < lzxBlocksCache.size(); i++) {
					lzxBlock = (LzxBlock) (lzxBlocksCache.get(i));
					for (int j = bb.iniBlock; j <= bb.startBlock; j++) {
						if (lzxBlock.blockNo == j)
							if (j > start) {
								start = j;
								block = i;
							}
						if (start == bb.startBlock)
							break;
					}
				}
			}
			if (i == lzxBlocksCache.size()) { // don't exist
				start = bb.iniBlock;
				lzxBlock = new LzxBlock(start, windowSize, getLzxSeg(start),
						lrt.block_len, null);
				lzxBlocksCache.add(lzxBlock);
				// block=lzxBlocksCache.size()-1;
			} else
				// exist
				lzxBlock = (LzxBlock) lzxBlocksCache.get(block);
			//
			for (i = start; i <= bb.endBlock;) {
				if (i == bb.startBlock && i == bb.endBlock) {
					tmp[0] = lzxBlock.getContent(bb.startOffset, bb.endOffset);
					break;
				}
				if (i == bb.startBlock)
					tmp[0] = lzxBlock.getContent(bb.startOffset);
				if (i > bb.startBlock && i < bb.endBlock)
					tmp[i - bb.startBlock] = lzxBlock.getContent();
				if (i == bb.endBlock) {
					tmp[i - bb.startBlock] = lzxBlock.getContent(0,
							bb.endOffset);
					break;
				}
				i++;
				if (i % (int) clcd.resetInterval == 0)
					lzxBlock = new LzxBlock(i, windowSize, getLzxSeg(i),
							lrt.block_len, null);
				else
					lzxBlock = new LzxBlock(i, windowSize, getLzxSeg(i),
							lrt.block_len, lzxBlock);
				lzxBlocksCache.add(lzxBlock);
			}
			cleanBlocksCache(MAX_CACHED_BLOCKS);
		}
		return tmp;
	}

	/**
	 * it will kick some cached blocks
	 */
	public void cleanBlocksCache(int blocks) {
		if (lzxBlocksCache.size() > blocks) {
			for (int i = 0; i < blocks / 5 + lzxBlocksCache.size() - blocks; i++)
				// 减到最大允许值的4/5
				lzxBlocksCache.remove(i);
		}
	}

	/**
	 * it will get bounds of any specified blocks
	 */
	public BlocksBounds getBlocksBounds(FileEntry fe) {
		BlocksBounds bb = new BlocksBounds();
		bb.startBlock = (int) (fe.offset.longValue() / bytesPerBlock);
		bb.endBlock = (int) (fe.offset.add(fe.length).longValue() / bytesPerBlock);
		bb.startOffset = (int) (fe.offset.longValue() % bytesPerBlock);
		bb.endOffset = (int) (fe.offset.add(fe.length).longValue() % bytesPerBlock);
		bb.iniBlock = bb.startBlock - bb.startBlock % (int) clcd.resetInterval;
		return bb;
	}

	/**
	 * a nested class, pack data of a block bounds
	 */
	class BlocksBounds {
		int iniBlock;// here reset the maintree
		int startBlock;
		int endBlock;
		int startOffset;
		int endOffset;
	}

	/**
	 * patch a block into a ChmSeg object
	 */
	public ChmSeg getLzxSeg(int block) {// for retrieving lzx block
		if (block < 0 || block >= lrt.block_address.length)
			return null;
		int len;
		if (block < lrt.block_address.length - 1)// not last block
			len = lrt.block_address[block + 1].subtract(
					lrt.block_address[block]).intValue();
		else
			// lastblock
			len = lzxBlockLen.subtract(lrt.block_address[block]).intValue();
		return getChmSeg(lzxBlockOffset.add(lrt.block_address[block]), len);
	}

	/**
	 * patch a block into a ChmSeg object
	 */
	public ChmSeg getChmSeg(BigInteger start, int len) {
		return getChmSeg(start.longValue(), len);
	}

	/**
	 * patch a block into a ChmSeg object
	 */
	public ChmSeg getChmSeg(long start, int len) {
		ChmSeg cs = null;
		try {
			byte[] b = new byte[len];
			fr.seek(start);
			if (fr.read(b) == len)
				cs = new ChmSeg(b);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cs;
	}

	/**
	 * get information of an object, its offset and its length and whether it's
	 * compressed
	 */
	public FileEntry resolveObject(String filename) {
		int startpmgl = cip.first_pmglblock;
		int stoppmgl = cip.last_pmglblock;
		ChmPmglSection cpl;
		FileEntry fe = null;
		if (cpi != null) {
			int i;
			for (i = 0; i < cpi.indexEntries.size(); i++)
				if (filename
						.compareTo(((IndexEntry) cpi.indexEntries.get(i)).entryName) <= 0)
					break;
			startpmgl = ((IndexEntry) cpi.indexEntries.get(i - 1)).nBlock;
		}
		do {
			cpl = resolvePmgl(startpmgl);
			fe = cpl.resolveEntry(filename);
			if (fe != null)
				break;
			else {
				if (startpmgl == stoppmgl)
					break;
				startpmgl = cpl.block_next;
			}
		} while (true);
		return fe;
	}

	/**
	 * get information from pmgl section
	 */
	public ChmPmglSection resolvePmgl(int startpmgl) {
		if (startpmgl == -1)
			return null;
//		byte[] b = new byte[(int) cip.dir_blocklen];
		// FileEntry ie=null;

		// fr.seek(dirOffset+startpmgl*cip.dir_blocklen);//the start point of
		// first pmgl or pmgi
		// if(fr.read(b)==(int)cip.dir_blocklen) {
		ChmSeg cc = getChmSeg(dirOffset + startpmgl * cip.dir_blocklen,
				(int) cip.dir_blocklen);
		return (new ChmPmglSection(cc));
	}

	/**
	 * get information from pmgi section
	 */
	public ChmPmgiSection resolvePmgi() {
//		byte[] b = new byte[(int) cip.dir_blocklen];
		ChmPmgiSection cpi = null;
		// FileEntry ie=null;
		// try {
		if (cip.index_depth == 2) {
			// dirOffset=cih.dir_offset+cip.dir_blocklen+cip.index_root*cip.dir_blocklen;
			// fr.seek(dirOffset+cip.index_root*cip.dir_blocklen);//the start
			// point of first pmgl or pmgi
			// if(fr.read(b)==(int)cip.dir_blocklen) {
			ChmSeg cc = getChmSeg(
					dirOffset + cip.index_root * cip.dir_blocklen,
					(int) cip.dir_blocklen);
			cpi = new ChmPmgiSection(cc);
		}
		return cpi;
	}

}

/**
 * define the type of data compressed or uncompressed
 */
class CompressType {
	public static int CHM_UNCOMPRESSED = 0;
	public static int CHM_COMPRESSED = 1;
}

/**
 * information of itsf head of chm
 */
class ChmItsfHeader {
	// private static int IniSigLen=4; //means 2 chars, 4 bytes
	public final static int LEN_MAX = 0x60;
	byte[] signature = ChmSeg.string2AsciiBytes("ITSF");
	/** 0 (ITSF) 4 bytes */
	int version;
	int header_len;
	int unknown_000c;
	long last_modified;
	long lang_id;
	/** 14 */
	byte[] dir_uuid = new byte[16];
	byte[] stream_uuid = new byte[16];
	BigInteger unknown_offset;
	/** 38 */
	BigInteger unknown_len;
	BigInteger dir_offset;
	BigInteger dir_len;
	BigInteger data_offset;

	/** 58 (Not present before V3) */
	public ChmItsfHeader(ChmSeg cc) {
		if (cc.compareTo(signature) == 0) {
			version = cc.getInt();
			header_len = cc.getInt();
			unknown_000c = cc.getInt();
			last_modified = cc.getUInt();
			lang_id = cc.getUInt();
			/** 14 */
			dir_uuid = cc.getBytes(16);
			stream_uuid = cc.getBytes(16);
			unknown_offset = cc.getUlong();
			/** 38 */
			unknown_len = cc.getUlong();
			dir_offset = cc.getUlong();
			dir_len = cc.getUlong();
			if (version == ItsfVersion.V3)
				data_offset = cc.getUlong();
			else
				data_offset = dir_offset.add(dir_len);
		}
	}
}

/**
 * define the version of itsf
 */
class ItsfVersion {
	// public static int length=2; //
	public static int V3 = 3;
	public static int V2 = 2;
}

/**
 * define the version of itsp
 */
class ItspVersion {
	// public static int length=2; //
	public static int V1 = 1;
	// public static int V2=2;
}

/**
 * information of itsp head of chm
 */
class ChmItspHeader {
	public final static int LEN_MAX = 0x54;
	byte[] signature = ChmSeg.string2AsciiBytes("ITSP");
	/** 0 (ITSP) 4 bytes */
	int version;
	int header_len;
	int unknown_000c;
	long dir_blocklen;
	/** 10 */
	int blockidx_intvl;
	int index_depth;
	int index_root;
	int first_pmglblock; // index_head; /** 20 */
	int last_pmglblock; // unknown_0024; /** 24 */
	long unknown_ffff; // -1 num_blocks; /** 28 */
	int num_dirblocks; // unknown_002c; /** 2c */
	long lang_id;
	byte[] system_uuid = new byte[16];
	byte[] unknown_0044 = new byte[16];

	public ChmItspHeader() {
	}

	public ChmItspHeader(ChmSeg cc) {
		if (cc.compareTo(signature) == 0) {
			version = cc.getInt();
			header_len = cc.getInt();
			unknown_000c = cc.getInt();
			dir_blocklen = cc.getUInt();
			/** 10 */
			blockidx_intvl = cc.getInt();
			index_depth = cc.getInt();
			index_root = cc.getInt();
			first_pmglblock = cc.getInt(); // index_head; /** 20 */
			last_pmglblock = cc.getInt(); // unknown_0024; /** 24 */
			unknown_ffff = cc.getUInt(); // -1 num_blocks; /** 28 */
			num_dirblocks = cc.getInt(); // unknown_002c; /** 2c */
			lang_id = cc.getUInt();
			system_uuid = cc.getBytes(16);
			unknown_0044 = cc.getBytes(16);
		}
	}
}

/**
 * information of pmgl section
 */
class ChmPmglSection {
	byte[] signature = ChmSeg.string2AsciiBytes("PMGL");
	/** 0 (ITSP) 4 bytes */
	long free_space;
	/** 4 */
	long unknown_0008;
	int block_prev;
	int block_next;
	ArrayList<FileEntry> fileEntries = new ArrayList<FileEntry>();

	public ChmPmglSection() {
	}

	/**
	 * get information of files
	 */
	public FileEntry resolveEntry(String filename) {
		int i;
		FileEntry fe = null;
		for (i = 0; i < fileEntries.size(); i++) {
			fe = (FileEntry) fileEntries.get(i);
			if (filename.compareTo(fe.entryName) == 0)
				break;
		}
		return fe;
	}

	public ChmPmglSection(ChmSeg cc) {
		if (cc.compareTo(signature) == 0) {
			free_space = cc.getUInt();
			unknown_0008 = cc.getUInt();
			block_prev = cc.getInt();
			block_next = cc.getInt();
			ChmSeg cs = new ChmSeg(cc.getBytes((int) (cc.getLeft() - free_space)));
			// long i=indexoffset;
			int namelen = 0;
			String name;
			while (cs.getLeft() > 0) {
				namelen = cs.getEncint().intValue();
				name = cs.getUtfString(namelen);
				FileEntry tmp = new FileEntry(name);
				tmp.compressType = cs.getEncint().intValue();
				tmp.offset = cs.getEncint();
				tmp.length = cs.getEncint();
				fileEntries.add(tmp);
			}
		}
	}
}

/**
 * style of directory list entry in pmgi section
 */
class IndexEntry {
	int nameLen;
	String entryName;
	int nBlock;

	public IndexEntry(String name) {
		entryName = name;
		nameLen = name.length();
	}
}

/**
 * information of pmgi section
 */
class ChmPmgiSection {
	byte[] signature = ChmSeg.string2AsciiBytes("PMGI");
	/** 0 (ITSP) 4 bytes */
	long free_space;
	/** 4 */
	ArrayList<IndexEntry> indexEntries = new ArrayList<IndexEntry>();

	public ChmPmgiSection(ChmSeg cc) {
		if (cc.compareTo(signature) == 0) {
			free_space = cc.getUInt();
			ChmSeg cs = new ChmSeg(cc.getBytes((int) (cc.getLeft() - free_space)));
			// long i=indexoffset;
			int namelen = 0;
			String name;
			while (cs.getLeft() > 0) {
				namelen = cs.getEncint().intValue();
				name = cs.getUtfString(namelen);
				IndexEntry tmp = new IndexEntry(name);
				tmp.nBlock = cs.getEncint().intValue();
				indexEntries.add(tmp);
			}
		}
	}
}

/**
 * resettable information
 */
class ChmLzxcResetTable {
	long version; // UInt32
	long block_count;
	long unknown;
	long table_offset;
	BigInteger uncompressed_len; // UInt64
	BigInteger compressed_len;
	BigInteger block_len;
	public BigInteger[] block_address;

	public ChmLzxcResetTable() {
	}

	public ChmLzxcResetTable(ChmSeg cc) {
		version = cc.getUInt(); // UInt32
		block_count = cc.getUInt();
		unknown = cc.getUInt();
		table_offset = cc.getUInt();
		uncompressed_len = cc.getUlong();
		compressed_len = cc.getUlong();
		block_len = cc.getUlong();
		int i = cc.getLeft() / 8;
		block_address = new BigInteger[i];
		for (int j = 0; j < i; j++) {
			block_address[j] = cc.getUlong();
		}
	}
}

/**
 * verion of control data
 */
class ControlDataVersion {
	public static int V2 = 2;
}

/**
 * control data
 */
class ChmLzxcControlData {
	long size;
	/** 0 */
	byte[] signature = ChmSeg.string2AsciiBytes("LZXC");
	/** 4 (LZXC) */
	public long version;
	/** 8 */
	public long resetInterval;
	/** c */
	public long windowSize;
	/** 10 */
	public long windowsPerReset;
	/** 14 */
	public long unknown_18;

	/** 18 */
	public ChmLzxcControlData() {
	}

	public ChmLzxcControlData(ChmSeg cc) {
		size = cc.getUInt();
		if (cc.compareTo(signature) == 0) {
			version = cc.getUInt(); // UInt32
			resetInterval = cc.getUInt();
			/** c */
			windowSize = cc.getUInt();
			/** 10 */
			windowsPerReset = cc.getUInt();
			/** 14 */
			unknown_18 = cc.getUInt();
			if (version == ControlDataVersion.V2) {
				// resetInterval= resetInterval*0x8000; /** c */
				windowSize = windowSize * 0x8000;
			}
		}
	}
}
