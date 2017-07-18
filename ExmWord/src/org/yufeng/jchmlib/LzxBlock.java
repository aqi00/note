/**
 * LzxBlock.java
 ***************************************************************************************
 * Author: Feng Yu. <yfbio@hotmail.com>
 *org.yufeng.jchmlib
 *version: 1.0
 ****************************************************************************************
 * // This library is free software; you can redistribute it and/or
 * // modify it under the terms of the GNU Lesser General Public
 * // License as published by the Free Software Foundation; either
 * // version 2.1 of the License, or (at your option) any later version.
 * //
 * // This library is distributed in the hope that it will be useful,
 * // but WITHOUT ANY WARRANTY; without even the implied warranty of
 * // MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * // Lesser General Public License for more details.
 * //
 * // You should have received a copy of the GNU Lesser General Public
 * // License along with this library; if not, write to the Free Software
 * // Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 **********************************************************************************************/

package org.yufeng.jchmlib;

import java.math.*;
import java.io.*;

/***
 * 
 * @author yufeng
 * 
 *         it is the main body to decompress the lzx data, this program is
 *         forged according to jed wing's lzx.c many thanks !!:)
 */
public class LzxBlock {
	class LzxState {
		boolean hadHeader;
		int type;
		int length;
		int remaining;
		// short[] pretreeLen=new short[pretreelen];
		// short[] pretreeTable=new short[pretreetablelen];
		public short[] MainTreeLenTable;
		public short[] MainTreeTable;
		public short[] LengthTreeLenTable;
		public short[] LengthTreeTable;
		public short[] AlignedLenTable;
		public short[] AlignedTreeTable;
		public boolean intelStarted = false;
		long intelFileSize;
		public int framesRead = 0;
		// public long intelCurPos=0;

		public long R0;
		public long R1;
		public long R2;

		public LzxState() {
			hadHeader = false;
			R0 = 1;
			R1 = 1;
			R2 = 1;
			type = 0;
			length = 0;
			remaining = 0;
			intelFileSize = 0;
			framesRead = 0;
			// intelCurPos=0;
		}

	}

	LzxState lzxState;
	int blockNo;

	// LzxBlock prevBlock;
	// ChmSeg chmSeg;
	int blockLen;
	private static short[] extra_bits = { 0, 0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4,
			5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12, 12, 13, 13, 14,
			14, 15, 15, 16, 16, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17,
			17, 17, 17 };

	private static long[] position_base = { 0, 1, 2, 3, 4, 6, 8, 12, 16, 24,
			32, 48, 64, 96, 128, 192, 256, 384, 512, 768, 1024, 1536, 2048,
			3072, 4096, 6144, 8192, 12288, 16384, 24576, 32768, 49152, 65536,
			98304, 131072, 196608, 262144, 393216, 524288, 655360, 786432,
			917504, 1048576, 1179648, 1310720, 1441792, 1572864, 1703936,
			1835008, 1966080, 2097152 };
	// defined by lzx specification.
	private static int MIN_MATCH = 2;
	public static int MAX_MATCH = 257;
	private static int NUM_CHARS = 256;// (256)
	private static int NUM_SECONDARY_LENGTHS = 249;// (249) /** length tree
													// #elements */
	public long WINDOW_SIZE;
	public int NUM_POSITION_SLOTS;
	public int MAIN_TREE_ELEMENTS;

	private static int LZX_PRETREE_NUM_ELEMENTS = 20;
	private static int LZX_PRETREE_NUM_ELEMENTS_BITS = 4;

	private static int LZX_ALIGNED_NUM_ELEMENTS_BITS = 3;
	private static int LZX_ALIGNED_NUM_ELEMENTS = 8;// (8) /** aligned offset
													// tree #elements */

	private static int LZX_NUM_PRIMARY_LENGTHS = 7;// (7) /** this one missing
													// from spec! */

	private static int LZX_PRETREE_MAXSYMBOLS = LZX_PRETREE_NUM_ELEMENTS;
	private static int LZX_PRETREE_TABLEBITS = 6;// (6)
	private static int LZX_MAINTREE_MAXSYMBOLS = (NUM_CHARS + 50 * 8); // 456
	private static int LZX_MAINTREE_TABLEBITS = 12;// (12)
	private static int LZX_LENGTH_MAXSYMBOLS = (NUM_SECONDARY_LENGTHS + 1); // 250
	private static int LZX_LENGTH_TABLEBITS = 12;// (12)
	private static int LZX_ALIGNED_MAXSYMBOLS = (LZX_ALIGNED_NUM_ELEMENTS);
	private static int LZX_ALIGNED_TABLEBITS = 7;// (7)
	private static int LZX_LENTABLE_SAFETY = 64;// (64)
	// private LzxBlock prevblock;//=null;

	private byte[] content = null;
	private int contentlen = 0;

	/*** Creates a new instance of LzxBlock */
	public LzxBlock() {

		lzxState = new LzxState();
	}

	/***
	 * create an empty block with fixed window size
	 */
	public LzxBlock(long win) {
		this();
		int window = 0;
		while (win > 1) {
			win >>>= 1;
			window++;
		}
		if (window < 15 || window > 21)
			window = 16;
		WINDOW_SIZE = 1 << window;
		if (window == 21)
			NUM_POSITION_SLOTS = 50;
		else if (window == 20)
			NUM_POSITION_SLOTS = 42;
		else
			NUM_POSITION_SLOTS = window << 1;
		MAIN_TREE_ELEMENTS = NUM_CHARS + NUM_POSITION_SLOTS * 8;
		lzxState.MainTreeLenTable = new short[MAIN_TREE_ELEMENTS];
		lzxState.LengthTreeLenTable = new short[NUM_SECONDARY_LENGTHS];
	}

	/**
	 * create a block with its previous
	 */
	public LzxBlock(int blockn, long window, ChmSeg cs, BigInteger blocklen,
			LzxBlock prevblock) {
		this(window);
		// byte[] b=new byte[4];
		blockNo = blockn;
		if (prevblock == null) {
			prevblock = new LzxBlock(window);
		} // else {
		lzxState = prevblock.lzxState;
		// lzxState.intelFileSize=prevblock.lzxState.intelFileSize;
		// lzxState.type=prevblock.lzxState.type;
		// lzxState.length=prevblock.lzxState.length;
		// lzxState.remaining=prevblock.lzxState.remaining;
		// prevBlock=prevblock;
		// R0=prevBlock.R0;
		// R1=prevBlock.R1;
		// R2=prevBlock.R2;
		// chmSeg=cs;
		byte[] prevcontent = null;
		if (prevblock.lzxState.length > prevblock.lzxState.remaining)
			prevcontent = prevblock.content;
		blockLen = blocklen.intValue();
		content = new byte[blockLen];
		cs.ready4Bits();
		while (contentlen < blockLen) {
			int len;
			if (lzxState.remaining == 0) {
				if (!lzxState.hadHeader) {
					lzxState.hadHeader = true;
					if (cs.getBitsSync(1) == 1)
						lzxState.intelFileSize = (cs.getBitsSync(16) << 16)
								+ cs.getBitsSync(16); // forward 4 bytes
				}
				lzxState.type = (int) cs.getBitsSync(3);
				// length=(int)cs.getBitsSync(24);
				lzxState.length = (int) ((cs.getBitsSync(16) << 8) + cs
						.getBitsSync(8));
				lzxState.remaining = lzxState.length;
				switch (lzxState.type) {
				case LzxBlockType.AlignedOffset:
					createAlignedTreeTable(cs);
				case LzxBlockType.Verbatim:
					createMainTreeTable(cs);
					createLengthTreeTable(cs);
					if (lzxState.MainTreeLenTable[0xe8] != 0)
						lzxState.intelStarted = true; // 0xE8
					break;
				case LzxBlockType.Uncompressed:
					lzxState.intelStarted = true;
					if (cs.remains > 16)
						cs.kicks -= 2;
					lzxState.R0 = (new BigInteger(ChmSeg.reverseBytesOrder(cs
							.getBytes(4)))).longValue();
					lzxState.R1 = (new BigInteger(ChmSeg.reverseBytesOrder(cs
							.getBytes(4)))).longValue();
					lzxState.R2 = (new BigInteger(ChmSeg.reverseBytesOrder(cs
							.getBytes(4)))).longValue();
					break;
				}
				// printTable("MainTree.txt", lzxState.MainTreeTable);
				// printTable("MainTreeLen.txt",lzxState. MainTreeLenTable);
				// printTable("LengthTree.txt", lzxState.LengthTreeTable);
				// printTable("LengthTreeLen.txt",lzxState. LengthTreeLenTable);
				// decompressVerbatimBlock(cs);
			}
			if (contentlen + lzxState.remaining > blockLen) {
				lzxState.remaining = contentlen + lzxState.remaining - blockLen;
				len = blockLen;
			} else {
				len = contentlen + lzxState.remaining;
				lzxState.remaining = 0;
			}
			switch (lzxState.type) {
			case LzxBlockType.AlignedOffset:
				decompressAlignedBlock(cs, len, prevcontent);
				break;
			case LzxBlockType.Verbatim:
				decompressVerbatimBlock(cs, len, prevcontent);
				break;
			case LzxBlockType.Uncompressed:
				decompressUncompressedBlock(cs, len, prevcontent);
				break;
			}
			if ((lzxState.framesRead++ < 32768) && lzxState.intelFileSize != 0)
				intelE8Decoding();
			// contentlen+=len;
			// lzxState.remaining=lzxState.length-(contentlen-orilen);
		}
	}

	/**
	 * intel e8 decoding
	 */
	public void intelE8Decoding() {
		if (blockLen <= 6 || !lzxState.intelStarted) {
			lzxState.remaining -= blockLen;
		} else {
			long curpos = lzxState.remaining;
			lzxState.remaining -= blockLen;
			int i = 0;
			while (i < blockLen - 10) {
				if (content[i] != 0xe8) {
					i++;
					continue;
				}
				byte[] b = new byte[4];
				b[0] = content[i + 3];
				b[1] = content[i + 2];
				b[2] = content[i + 1];
				b[3] = content[i + 0];
				long absoff = (new BigInteger(b)).longValue();
				if ((absoff >= -curpos) && (absoff < lzxState.intelFileSize)) {
					long reloff = (absoff >= 0) ? absoff - curpos : absoff
							+ lzxState.intelFileSize;
					content[i + 0] = (byte) reloff;
					content[i + 1] = (byte) (reloff >>> 8);
					content[i + 2] = (byte) (reloff >>> 16);
					content[i + 3] = (byte) (reloff >>> 24);
				}
				i += 4;
				curpos += 5;

			}
		}

	}

	/**
	 * uncompressed block
	 */
	public void decompressUncompressedBlock(ChmSeg chmSeg, int len,
			byte[] prevcontent) {
		if (contentlen + lzxState.remaining <= blockLen) {
			for (int i = contentlen; i < contentlen + lzxState.remaining; i++)
				content[i] = chmSeg.getByte();
			contentlen = contentlen + lzxState.remaining;
			lzxState.remaining = 0;
		} else {
			for (int i = contentlen; i < blockLen; i++)
				content[i] = chmSeg.getByte();
			lzxState.remaining = blockLen - contentlen;
			contentlen = blockLen;
		}
	}

	/**
	 * aligned block
	 */
	public void decompressAlignedBlock(ChmSeg chmSeg, int len,
			byte[] prevcontent) {
		// int len=blockLen.intValue()-;
		// byte[] tmp=new byte[len];
		long R0 = lzxState.R0;
		long R1 = lzxState.R1;
		long R2 = lzxState.R2;
		short s;
		int x, i;
		int mainbits = LZX_MAINTREE_TABLEBITS;
		int lenbits = LZX_LENGTH_TABLEBITS;
		int alignedbits = LZX_ALIGNED_TABLEBITS;
		int mainmaxsym = MAIN_TREE_ELEMENTS;
		int lenmaxsym = NUM_SECONDARY_LENGTHS;
//		String st;
//		StringBuffer sb = new StringBuffer();
		int matchlen = 0, matchfooter = 0, extra, rundest, runsrc;
		int matchoffset = 0;
		for (i = contentlen; i < len; i++) {
			int f = (int) chmSeg.getBitsDesync(mainbits, 0);
			s = lzxState.MainTreeTable[f];
			if (s >= mainmaxsym) {
				x = mainbits;
				do {
					x++;
					s <<= 1;
					s += chmSeg.checkBit(x);
					// z+=(1-cs.checkBit(x));
				} while ((s = lzxState.MainTreeTable[s]) >= mainmaxsym);
			}
			chmSeg.getBitsSync(lzxState.MainTreeLenTable[s]);
			if (s < NUM_CHARS) {
				content[i] = (byte) s;
				// sb.append(tmp[i]);
			} else {

				s -= NUM_CHARS;
				matchlen = s & LZX_NUM_PRIMARY_LENGTHS;
				if (matchlen == LZX_NUM_PRIMARY_LENGTHS) {
					matchfooter = lzxState.LengthTreeTable[(int) chmSeg
							.getBitsDesync(lenbits, 0)];
					if (matchfooter >= lenmaxsym) {
						x = lenbits;
						do {
							x++;
							matchfooter <<= 1;
							matchfooter += chmSeg.checkBit(x);
							// z+=(1-cs.checkBit(x));
						} while ((matchfooter = lzxState.LengthTreeTable[matchfooter]) >= lenmaxsym);
					}
					chmSeg.getBitsSync(lzxState.LengthTreeLenTable[matchfooter]);
					matchlen += matchfooter;
				}
				matchlen += MIN_MATCH;
				matchoffset = s >>> 3;
				if (matchoffset > 2) {
					extra = extra_bits[matchoffset];
					matchoffset = (int) (position_base[matchoffset] - 2);
					if (extra > 3) {
						extra -= 3;
						long l = chmSeg.getBitsSync(extra);
						matchoffset += (l << 3);
						int g = (int) chmSeg.getBitsDesync(alignedbits, 0);
						int t = lzxState.AlignedTreeTable[g];
						if (t >= mainmaxsym) {
							x = mainbits;
							do {
								x++;
								t <<= 1;
								t += chmSeg.checkBit(x);
								// z+=(1-cs.checkBit(x));
							} while ((t = lzxState.AlignedTreeTable[t]) >= mainmaxsym);
						}
						chmSeg.getBitsSync(lzxState.AlignedTreeTable[t]);
						matchoffset += t;
					} else if (extra == 3) {
						int g = (int) chmSeg.getBitsDesync(alignedbits, 0);
						int t = lzxState.AlignedTreeTable[g];
						if (t >= mainmaxsym) {
							x = mainbits;
							do {
								x++;
								t <<= 1;
								t += chmSeg.checkBit(x);
								// z+=(1-cs.checkBit(x));
							} while ((t = lzxState.AlignedTreeTable[t]) >= mainmaxsym);
						}
						chmSeg.getBitsSync(lzxState.AlignedTreeTable[t]);
						matchoffset += t;
					} else if (extra > 0) {
						long l = chmSeg.getBitsSync(extra);
						matchoffset += l;

					} else
						// extra==0
						matchoffset = 1;
					R2 = R1;
					R1 = R0;
					R0 = matchoffset;
				} else if (matchoffset == 0) {
					matchoffset = (int) R0;
				} else if (matchoffset == 1) {
					matchoffset = (int) R1;
					R1 = R0;
					R0 = matchoffset;
				} else /** match_offset == 2 */
				{
					matchoffset = (int) R2;
					R2 = R0;
					R0 = matchoffset;
				}
				rundest = i;
				runsrc = rundest - matchoffset;
				i += (matchlen - 1);
				if (i > len)
					break;// return null;
				// this_run -= match_length;
				if (runsrc < 0) {
					// System.out.println(runsrc);
					if (matchlen + runsrc <= 0) {
						runsrc = prevcontent.length + runsrc;
						while (matchlen-- > 0)
							content[rundest++] = prevcontent[runsrc++];
					} else {
						runsrc = prevcontent.length + runsrc;
						while (runsrc < prevcontent.length)
							content[rundest++] = prevcontent[runsrc++];
						matchlen = matchlen + runsrc - prevcontent.length;
						runsrc = 0;
						while (matchlen-- > 0)
							content[rundest++] = content[runsrc++];
					}

				} else {
					/** copy any wrapped around source data */
					while ((runsrc < 0) && (matchlen-- > 0)) {
						content[rundest++] = content[runsrc + blockLen];
						// sb.append(tmp[runsrc + len]);
						runsrc++;
					}
					/** copy match data - no worries about destination wraps */
					while (matchlen-- > 0)
						content[rundest++] = content[runsrc++];
				}
				// sb.append(tmp[rundest-1]);

				// if (i>475) //240
				// break;// continue;//
			}
			// sb.append(tmp[i]);
			// System.out.println(i);
			// System.out.println(sb.toString());
			// if(i>475)break;

		}
		// contentlen=i;
		// st=sb.toString();
		FileOutputStream fops = null;
		try {
			fops = new FileOutputStream("res.txt");
			// DataPutStream dps=new DataPutStream(fops);
			// dps.

			fops.write(content);
			fops.close();
		} catch (IOException e) {

		}
		contentlen = len;
		if (contentlen == blockLen) {
			lzxState.R0 = R0;
			lzxState.R1 = R1;
			lzxState.R2 = R2;
		}
		// return tmp;
	}

	/**
	 * create aligned tree table
	 */
	private void createAlignedTreeTable(ChmSeg cs) {
		lzxState.AlignedLenTable = createAlignedLenTable(cs);
		// saveTree(prelentable, "zlengthprelen.txt");
		lzxState.AlignedTreeTable = createTreeTable2(lzxState.AlignedLenTable,
				(1 << LZX_ALIGNED_TABLEBITS) + (LZX_ALIGNED_MAXSYMBOLS << 1),
				LZX_ALIGNED_TABLEBITS, LZX_ALIGNED_MAXSYMBOLS);
	}

	/**
	 * create lenth table for aligned tree
	 */
	private short[] createAlignedLenTable(ChmSeg cs) {
		int tablelen = LZX_ALIGNED_NUM_ELEMENTS_BITS;
		int bits = LZX_ALIGNED_NUM_ELEMENTS_BITS;
		short[] tmp = new short[tablelen];
		for (int i = 0; i < tablelen; i++) {
			tmp[i] = (short) cs.getBitsSync(bits);
		}
		// System.out.println((short)cs.getBitsSync(6));
		return tmp;
	}

	/**
	 * create length tree table
	 */
	private void createLengthTreeTable(ChmSeg cs) {
		short[] prelentable = createPreLenTable(cs);
		// saveTree(prelentable, "zlengthprelen.txt");
		short[] pretreetable = createTreeTable2(prelentable,
				(1 << LZX_PRETREE_TABLEBITS) + (LZX_PRETREE_MAXSYMBOLS << 1),
				LZX_PRETREE_TABLEBITS, LZX_PRETREE_MAXSYMBOLS);
		// saveTree(pretreetable, "zlengthpretree.txt");
		createLengthTreeLenTable(cs, 0, NUM_SECONDARY_LENGTHS, pretreetable,
				prelentable);
		// saveTree(lzxState.LengthTreeLenTable, "zlenlen.txt");
		lzxState.LengthTreeTable = createTreeTable2(
				lzxState.LengthTreeLenTable, (1 << LZX_LENGTH_TABLEBITS)
						+ (LZX_LENGTH_MAXSYMBOLS << 1), LZX_LENGTH_TABLEBITS,
				NUM_SECONDARY_LENGTHS);
		// saveTree(lzxState.LengthTreeTable, "zlen.txt");
	}

	/**
	 * create length table for length tree
	 */
	private void createLengthTreeLenTable(ChmSeg cs, int offset, int tablelen,
			short[] pretreetable, short[] prelentable) {
		// short[] tmp=new short[tablelen];
		int bits = LZX_PRETREE_TABLEBITS;
		int maxsymbol = LZX_PRETREE_MAXSYMBOLS;
		int i = offset;
		int z, y, x;
		// System.out.println("maintreelen");
		while (i < tablelen) {
			z = pretreetable[(int) cs.getBitsDesync(bits, 0)];
			if (z >= maxsymbol) {
				x = bits;
				do {
					x++;
					z <<= 1;
					z += cs.checkBit(x);
					// z+=(1-cs.checkBit(x));
				} while ((z = pretreetable[z]) >= maxsymbol);
			}
			cs.getBitsSync(prelentable[z]);
			if (z < 17) {
				z = lzxState.LengthTreeLenTable[i] - z;
				if (z < 0)
					z = z + 17;
				lzxState.LengthTreeLenTable[i] = (short) z;
				i++;
			} else if (z == 17) {
				y = (int) cs.getBitsSync(4);
				y += 4;
				for (int j = 0; j < y; j++)
					lzxState.LengthTreeLenTable[i++] = 0;
			} else if (z == 18) {
				y = (int) cs.getBitsSync(5);
				y += 20;
				for (int j = 0; j < y; j++)
					lzxState.LengthTreeLenTable[i++] = 0;
			} else if (z == 19) {
				y = (int) cs.getBitsSync(1);
				y += 4;
				z = pretreetable[(int) cs.getBitsDesync(bits, 0)];
				if (z >= maxsymbol) {
					x = bits;
					do {
						x++;
						z <<= 1;
						z += cs.checkBit(x);
						// z+=(1-cs.checkBit(x));
					} while ((z = pretreetable[z]) >= maxsymbol);
				}
				cs.getBitsSync(prelentable[z]);
				z = lzxState.LengthTreeLenTable[i] - z;
				if (z < 0)
					z = z + 17;
				for (int j = 0; j < y; j++)
					lzxState.LengthTreeLenTable[i++] = (short) z;
			}
			// System.out.print(i-1);
			// System.out.print("   ");
			// System.out.println(LengthTreeLenTable[i-1]);
		}
	}

	/**
	 * create main tree table
	 */
	private void createMainTreeTable(ChmSeg cs) {
		short[] prelentable = createPreLenTable(cs);
		// saveTree(prelentable, "zmainprelen1.txt");
		short[] pretreetable = createTreeTable2(prelentable,
				(1 << LZX_PRETREE_TABLEBITS) + (LZX_PRETREE_MAXSYMBOLS << 1),
				LZX_PRETREE_TABLEBITS, LZX_PRETREE_MAXSYMBOLS);
		// saveTree(pretreetable, "zmainpretree1.txt");
		createMainTreeLenTable(cs, 0, NUM_CHARS, pretreetable, prelentable);
		prelentable = createPreLenTable(cs);
		// saveTree(prelentable, "zmainprelen2.txt");
		// saveTree(prelentable, "zpreelenmainlen2.txt");
		pretreetable = createTreeTable2(prelentable,
				(1 << LZX_PRETREE_TABLEBITS) + (LZX_PRETREE_MAXSYMBOLS << 1),
				LZX_PRETREE_TABLEBITS, LZX_PRETREE_MAXSYMBOLS);
		// saveTree(pretreetable, "zmainpretree2.txt");
		createMainTreeLenTable(cs, NUM_CHARS, lzxState.MainTreeLenTable.length,
				pretreetable, prelentable);
		// saveTree(lzxState.MainTreeLenTable, "zmainlen.txt");
		lzxState.MainTreeTable = createTreeTable2(lzxState.MainTreeLenTable,
				(1 << LZX_MAINTREE_TABLEBITS) + (LZX_MAINTREE_MAXSYMBOLS << 1),
				LZX_MAINTREE_TABLEBITS, MAIN_TREE_ELEMENTS);
		// saveTree(lzxState.MainTreeTable, "zmain.txt");
	}

	/**
	 * create length tree table for main tree
	 */
	private void createMainTreeLenTable(ChmSeg cs, int offset, int tablelen,
			short[] pretreetable, short[] prelentable) {
		// short[] tmp=new short[tablelen];
		int bits = LZX_PRETREE_TABLEBITS;
		int maxsymbol = LZX_PRETREE_MAXSYMBOLS;
		int i = offset;
		int z, y, x;
		// long x;
		// int a=64-bits;
		// System.out.println("maintreelen");
		while (i < tablelen) {
			int f = (int) cs.getBitsDesync(bits, 0);
			// System.out.println(f);
			z = pretreetable[f];
			/**
			 * if(z>=maxsymbol) { x=(1<<(32-bits)); do { x>>>=1; z<<=1; //
			 * z+=(cs.val & x); z+=(((cs.val & x)==0) ? 1 :0);
			 * }while((z=pretreetable[z])>=maxsymbol); }
			 */
			if (z >= maxsymbol) {
				x = bits;
				do {
					x++;
					z <<= 1;
					z += cs.checkBit(x);
					// z+=(1-cs.checkBit(x));
				} while ((z = pretreetable[z]) >= maxsymbol);
			}
			cs.getBitsSync(prelentable[z]);
			if (z < 17) {
				z = lzxState.MainTreeLenTable[i] - z;
				if (z < 0)
					z = z + 17;
				lzxState.MainTreeLenTable[i] = (short) z;
				i++;
			} else if (z == 17) {
				y = (int) cs.getBitsSync(4);
				y += 4;
				for (int j = 0; j < y; j++)
					lzxState.MainTreeLenTable[i++] = 0;
			} else if (z == 18) {
				y = (int) cs.getBitsSync(5);
				y += 20;
				for (int j = 0; j < y; j++)
					lzxState.MainTreeLenTable[i++] = 0;
			} else if (z == 19) {
				y = (int) cs.getBitsSync(1);
				y += 4;
				z = pretreetable[(int) cs.getBitsDesync(bits, 0)];
				/**
				 * if(z>=maxsymbol) { x=(1<<(32-bits)); do { x>>>=1; z<<=1;
				 * z|=(((cs.val & x)==0) ? 0 : 1);
				 * }while((z=pretreetable[z])>=maxsymbol); }
				 */
				if (z >= maxsymbol) {
					x = bits;
					do {
						x++;
						z <<= 1;
						z += cs.checkBit(x);
						// z+=(1-cs.checkBit(x));
					} while ((z = pretreetable[z]) >= maxsymbol);
				}
				cs.getBitsSync(prelentable[z]);
				z = lzxState.MainTreeLenTable[i] - z;
				if (z < 0)
					z = z + 17;
				for (int j = 0; j < y; j++)
					lzxState.MainTreeLenTable[i++] = (short) z;
			}
			// System.out.print(i-1);
			// System.out.print("   ");
			// System.out.println(lzxState.MainTreeLenTable[i-1]);
			// if(i>298)
			// continue;
		}
	}

	/**
	 * create pretree table fro length table
	 */
	private short[] createPreLenTable(ChmSeg cs) {
		int tablelen = LZX_PRETREE_MAXSYMBOLS;
		int bits = LZX_PRETREE_NUM_ELEMENTS_BITS;
		short[] tmp = new short[tablelen];
		for (int i = 0; i < tablelen; i++) {
			tmp[i] = (short) cs.getBitsSync(bits);
		}
		// System.out.println((short)cs.getBitsSync(6));
		return tmp;
	}

	/**
	 * decompress verbatim block
	 */
	public void decompressVerbatimBlock(ChmSeg chmSeg, int len,
			byte[] prevcontent) {
		// int len=blockLen.intValue()-;
		// byte[] tmp=new byte[len];
		long R0 = lzxState.R0;
		long R1 = lzxState.R1;
		long R2 = lzxState.R2;
		short s;
		int x, i;
		int mainbits = LZX_MAINTREE_TABLEBITS;// 12
		int lenbits = LZX_LENGTH_TABLEBITS;// 12
		int mainmaxsym = MAIN_TREE_ELEMENTS;// NUM_CHARS + NUM_POSITION_SLOTS*8;
											// 256+42*8 when window=20
		int lenmaxsym = NUM_SECONDARY_LENGTHS;// 249
//		String st;
//		StringBuffer sb = new StringBuffer();
		int matchlen = 0, matchfooter = 0, extra, rundest, runsrc;
		int matchoffset = 0;
		for (i = contentlen; i < len; i++) {
			int f = (int) chmSeg.getBitsDesync(mainbits, 0);
			s = lzxState.MainTreeTable[f];
			if (s >= mainmaxsym) {
				x = mainbits;
				do {
					x++;
					s <<= 1;
					s += chmSeg.checkBit(x);// get the bit following f, and put
											// it in the end of s
					// z+=(1-cs.checkBit(x));
				} while ((s = lzxState.MainTreeTable[s]) >= mainmaxsym);
			}
			chmSeg.getBitsSync(lzxState.MainTreeLenTable[s]);// kick off the
																// bits
			if (s < NUM_CHARS) {
				content[i] = (byte) s;
				// sb.append(tmp[i]);
			} else {

				s -= NUM_CHARS;
				matchlen = s & LZX_NUM_PRIMARY_LENGTHS; // LZX_NUM_PRIMARY_LENGTHS=7
				if (matchlen == LZX_NUM_PRIMARY_LENGTHS) { // if it has end bits
															// 111, it may be
															// longer, so get
															// another 12 bits
					matchfooter = lzxState.LengthTreeTable[(int) chmSeg
							.getBitsDesync(lenbits, 0)];
					if (matchfooter >= lenmaxsym) {
						x = lenbits;
						do {
							x++;
							matchfooter <<= 1;
							matchfooter += chmSeg.checkBit(x);
							// z+=(1-cs.checkBit(x));
						} while ((matchfooter = lzxState.LengthTreeTable[matchfooter]) >= lenmaxsym);
					}
					chmSeg.getBitsSync(lzxState.LengthTreeLenTable[matchfooter]);
					matchlen += matchfooter;
				}
				matchlen += MIN_MATCH; // MIN_MATCH=2, min match should not
										// shorter than 2
				matchoffset = s >>> 3;
				if (matchoffset > 2) {
					if (matchoffset != 3) { // should get other bits to retrieve
											// offset
						extra = extra_bits[matchoffset];
						long l = chmSeg.getBitsSync(extra);
						// fr.removeBit(extra);
						matchoffset = (int) (position_base[matchoffset] - 2 + l);
					} else {
						matchoffset = 1;
					}
					R2 = R1;
					R1 = R0;
					R0 = matchoffset;
				} else if (matchoffset == 0) {
					matchoffset = (int) R0;
				} else if (matchoffset == 1) {
					matchoffset = (int) R1;
					R1 = R0;
					R0 = matchoffset;
				} else /** match_offset == 2 */
				{
					matchoffset = (int) R2;
					R2 = R0;
					R0 = matchoffset;
				}
				rundest = i;
				runsrc = rundest - matchoffset;
				i += (matchlen - 1);
				if (i > len)
					break;// return null;
				// this_run -= match_length;
				if (runsrc < 0) {
					// System.out.println(runsrc);
					if (matchlen + runsrc <= 0) {
						runsrc = prevcontent.length + runsrc;
						while (matchlen-- > 0)
							content[rundest++] = prevcontent[runsrc++];
					} else {
						runsrc = prevcontent.length + runsrc;
						while (runsrc < prevcontent.length)
							content[rundest++] = prevcontent[runsrc++];
						matchlen = matchlen + runsrc - prevcontent.length;
						runsrc = 0;
						while (matchlen-- > 0)
							content[rundest++] = content[runsrc++];
					}

				} else {
					/** copy any wrapped around source data */
					while ((runsrc < 0) && (matchlen-- > 0)) {
						content[rundest++] = content[runsrc + blockLen];
						// sb.append(tmp[runsrc + len]);
						runsrc++;
					}
					/** copy match data - no worries about destination wraps */
					while (matchlen-- > 0)
						content[rundest++] = content[runsrc++];
				}
				// sb.append(tmp[rundest-1]);

				// if (i>475) //240
				// break;// continue;//
			}
			// sb.append(tmp[i]);
			// System.out.println(i);
			// System.out.println(sb.toString());
			// if(i>475)break;

		}
		// contentlen=i;
		// st=sb.toString();
		/*
		 * FileOutputStream fops=null; try { fops=new
		 * FileOutputStream("res.txt"); // DataPutStream dps=new
		 * DataPutStream(fops); //dps.
		 * 
		 * fops.write(content); fops.close(); } catch(IOException e) {
		 * 
		 * }
		 */
		contentlen = len;
		if (contentlen == blockLen) {
			lzxState.R0 = R0;
			lzxState.R1 = R1;
			lzxState.R2 = R2;
		}
		// return tmp;
	}

	/**
	 * get result data ,they are bytes
	 * 
	 */
	public byte[] getContent() {
		// if(content==null)
		// content=decompressVerbatimBlock(cs);
		return content;
	}

	/**
	 * get result data ,they are bytes
	 * 
	 */
	public byte[] getContent(int start) {
		int i = getContent().length - start;
		byte[] tmp = new byte[i];
		for (int j = 0; j < getContent().length - start; j++)
			tmp[j] = content[start + j];
		return tmp;
	}

	/**
	 * get result data ,they are bytes
	 * 
	 */
	public byte[] getContent(int start, int length) {
		if (start + length > getContent().length)
			length = getContent().length - start;
		byte[] tmp = new byte[length];
		for (int j = 0; j < length; j++)
			tmp[j] = content[start + j];
		return tmp;
	}

	/**
	 * create tree table
	 */
	private short[] createTreeTable2(short[] lentable, int tablelen, int bits,
			int maxsymbol) {
		short[] tmp = new short[tablelen];
		short sym;
		int leaf;
		int bit_num = 1;
		long fill;
		int pos = 0;
		/** the current position in the decode table */
		long table_mask = (1 << bits); // 64,table_mask=
										// 1����??nbits??,??maintreelen??1����??12????4096
		long bit_mask = (table_mask >> 1);
		/** don't do 0 length codes */
		// 32
		long next_symbol = bit_mask;
		/** base of allocation for long codes */

		/** fill entries for codes short enough for a direct mapping */
		while (bit_num <= bits) {
			for (sym = 0; sym < lentable.length; sym++) {
				if (lentable[sym] == bit_num) {
					leaf = pos;// pos=0

					if ((pos += bit_mask) > table_mask)
						return null;
					/** table overrun */
					// bitmask=16,

					/**
					 * fill all possible lookups of this symbol with the symbol
					 * itself
					 */
					fill = bit_mask;
					while (fill-- > 0)
						tmp[leaf++] = sym;
				}
			}
			bit_mask >>= 1;
			bit_num++;
		}

		/** if there are any codes longer than nbits */
		if (pos != table_mask) {
			/** clear the remainder of the table */
			for (leaf = pos; leaf < table_mask; leaf++)
				tmp[leaf] = 0;

			/** give ourselves room for codes to grow by up to 16 more bits */
			pos <<= 16;
			table_mask <<= 16;
			bit_mask = 1 << 15;

			while (bit_num <= 16) {
				for (sym = 0; sym < lentable.length; sym++) {
					if (lentable[sym] == bit_num) {
						leaf = pos >> 16;
						for (fill = 0; fill < bit_num - bits; fill++) {
							/**
							 * if this path hasn't been taken yet, 'allocate'
							 * two entries
							 */
							if (tmp[leaf] == 0) {
								tmp[(int) (next_symbol << 1)] = 0;
								tmp[(int) (next_symbol << 1) + 1] = 0;
								tmp[leaf] = (short) next_symbol++;
							}
							/**
							 * follow the path and select either left or right
							 * for next bit
							 */
							leaf = tmp[leaf] << 1;
							if (((pos >> (15 - fill)) & 1) != 0)
								leaf++;
						}
						tmp[leaf] = sym;

						if ((pos += bit_mask) > table_mask)
							return null;
						/** table overflow */
					}
				}
				bit_mask >>= 1;
				bit_num++;
			}
		}

		/** full table? */
		if (pos == table_mask)
			return tmp;

		/** either erroneous table, or all elements are 0 - let's find out. */
		// for (leaf = 0; leaf <maxsymbol; leaf++) if (lentable[leaf]) return
		// null;
		return tmp;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public void saveTree(short[] tree, String file) {

		FileWriter fops = null;
		try {
			fops = new FileWriter(file);
			// DataPutStream dps=new DataPutStream(fops);
			// dps.
			for (int i = 0; i < tree.length; i++) {
				fops.write(Integer.toString(i));
				fops.write("    ");
				fops.write(Short.toString(tree[i]));
				fops.write("\n");
			}
			fops.close();
		} catch (IOException e) {

		}
	}
	/*
	 * //////////////////////////////////////////////////////////////////////////
	 * ///////////////////////////////////// public LzxBlock(int blockn,long
	 * window,ChmSeg cs,BigInteger blocklen,LzxBlock prevblock,int t){
	 * this(window); //byte[] b=new byte[4]; blockNo=blockn; if(prevblock==null)
	 * { prevblock=new LzxBlock(window); } //else { lzxState=prevblock.lzxState;
	 * // lzxState.intelFileSize=prevblock.lzxState.intelFileSize;
	 * //lzxState.type=prevblock.lzxState.type; //
	 * lzxState.length=prevblock.lzxState.length; //
	 * lzxState.remaining=prevblock.lzxState.remaining; //prevBlock=prevblock;
	 * //R0=prevBlock.R0; //R1=prevBlock.R1; //R2=prevBlock.R2; //chmSeg=cs;
	 * byte [] prevcontent=null;
	 * if(prevblock.lzxState.length>prevblock.lzxState.remaining)
	 * prevcontent=prevblock.content; blockLen=blocklen.intValue(); content=new
	 * byte[blockLen]; cs.ready4Bits(); while(contentlen<blockLen) { int len;
	 * if(lzxState.remaining==0) { if(!lzxState.hadHeader) {
	 * lzxState.hadHeader=true; if(cs.getBitsSync(1)==1)
	 * lzxState.intelFileSize=(cs.getBitsSync(16)<<16)+cs.getBitsSync(16);
	 * //forward 4 bytes } lzxState.type=(int)cs.getBitsSync(3); //
	 * length=(int)cs.getBitsSync(24);
	 * lzxState.length=(int)((cs.getBitsSync(16)<<8)+cs.getBitsSync(8));
	 * lzxState.remaining=lzxState.length; createMainTreeTable(cs);
	 * createLengthTreeTable(cs); //printTable("MainTree.txt",
	 * lzxState.MainTreeTable); //printTable("MainTreeLen.txt",lzxState.
	 * MainTreeLenTable); // printTable("LengthTree.txt",
	 * lzxState.LengthTreeTable); //printTable("LengthTreeLen.txt",lzxState.
	 * LengthTreeLenTable); //decompressVerbatimBlock(cs); }
	 * if(contentlen+lzxState.remaining>blockLen) {
	 * lzxState.remaining=contentlen+lzxState.remaining-blockLen; len=blockLen;
	 * } else { len=contentlen+lzxState.remaining; lzxState.remaining=0; }
	 * decompressVerbatimBlock(cs,len,prevcontent); //contentlen+=len; //
	 * lzxState.remaining=lzxState.length-(contentlen-orilen); } } private void
	 * printTable(String file,short[] table) { FileWriter fops=null; try {
	 * fops=new FileWriter(file); // DataPutStream dps=new DataPutStream(fops);
	 * //dps. for(int i=0;i<table.length;i++) { fops.write(Integer.toString(i));
	 * fops.write("        "); fops.write(Short.toString(table[i]));
	 * fops.write("\n"); } fops.close(); } catch(IOException e) {
	 * 
	 * } } /**create tree table
	 * 
	 * private short [] createTreeTable(short[] lentable,int tablelen,int
	 * bits,int maxsymbol) { //short[] lentable=createPreLenTable(fr); //int
	 * tablelen=1<<LZX_PRETREE_TABLEBITS+LZX_PRETREE_MAXSYMBOLS<<1; //int
	 * bits=LZX_PRETREE_TABLEBITS;
	 * 
	 * int tablemask=(1<<(bits-1)); short[] tmp=new short[tablelen]; int n=0;
	 * for(int i=1;i<(1<<bits);i++) { for(int j=0;j<maxsymbol;j++) {
	 * //lentable.length // System.out.println(j); //if(j==510) // continue; if
	 * (lentable[j]==i) { int k; for(k=n;k<n+tablemask;k++) { tmp[k]=(short)j;
	 * System.out.println(tmp[k]); } n=n+tablemask; } } tablemask=tablemask>>>1;
	 * } return tmp; }
	 */

}

class LzxBlockType {
	public final static int Undefined = 0;
	public final static int Verbatim = 1;
	public final static int AlignedOffset = 2;
	public final static int Uncompressed = 3;
	// 4-7 Undefined
}
