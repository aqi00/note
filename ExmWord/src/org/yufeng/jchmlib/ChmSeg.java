/**
 * ChmSeg.java
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

import java.math.*;

/***
 * 
 * @author yufeng it control any segments retrieved from chm to easily handle
 */
public class ChmSeg {

	/***
	 * kicked bytes of b
	 */
	int kicks;
	/**
	 * remained bits in bitbuffer,
	 */
	int remains;
	/**
	 * bit buffer. is an int with bits less than 33 bits
	 */
	int val;
	/**
	 * all bytes read from chm are saved here
	 */
	byte[] b;

	/**
	 * just reverse bytes order,
	 **/
	public static byte[] reverseBytesOrder(byte[] b) {
		if (b == null)
			return null;
		byte[] tmp = new byte[b.length];
		for (int i = 0; i < b.length; i++)
			tmp[b.length - i - 1] = b[i];
		return tmp;
	}

	public ChmSeg(byte[] bs) {
		kicks = 0;
		b = bs;
		val = 0;
		remains = 0;
	}

	/**
	 * check the specifed position in bit buff is 1 or 0 bitstream will have 4
	 * bytes
	 */
	public int checkBit(int i) {
		int n = (1 << (remains - i));
		if ((val & n) == 0)
			return 0;
		return 1;
	}

	/**
	 * get bits and kick them
	 */
	public int getBitsSync(int getbits) {
		return getBitsDesync(getbits, getbits);
	}

	/**
	 * get some bits,but kick different number of them
	 **/
	public int getBitsDesync(int getbits, int removebits) {
		// int a=remains/16;
		while (remains < 16) {
			// System.out.println("getubyte");
			// int s=getUByte();
			// System.out.print(s);
			// System.out.print("...........");
			// int d=getUByte();
			// System.out.println(d);
			val = (val << 16) + getUByte() + (getUByte() << 8);
			// val=(val<<16)+ getUByte()+(getUByte()<<8);
			remains += 16;
		}
		int tmp = (val >>> (remains - getbits));
		remains = remains - removebits;
		int t = (val >>> remains);
		val = val - (t << remains);
		return tmp;
	}

	/**
	 * get byte but return int it ensure that the leading three bytes don't
	 * change into fffff
	 */
	public int getUByte() {
		// kicks++;
		return (int) (getByte() & 255);
	}

	/**
	 * check whether the bit stream is ready
	 * 
	 */
	public boolean ready4Bits() {
		// getBytes(offset);
		remains = 0;
		val = 0;
		return true;
	}

	/**
	 * get length of all bytes
	 */
	public int getLen() {
		return b.length;
	}

	/**
	 * get length of left bytes
	 */
	public int getLeft() {
		return b.length - kicks;
	}

	/**
	 * this create ascii byte array from string, these chars are Ascii, but in
	 * java string they have two bytes each so, use each byte to represent one
	 * char
	 **/
	public static byte[] string2AsciiBytes(String s) {
		char[] c = s.toCharArray();
		byte[] byteval = new byte[c.length];
		// this.length=c.length;
		for (int i = 0; i < c.length; i++)
			byteval[i] = (byte) c[i];
		return byteval;
	}

	/**
	 * get an unsigned number which has i bytes
	 */
	public BigInteger getBigInteger(int i) {
		// int i=8;
		// byte[] b=new byte[i];

		if (b == null)
			return BigInteger.ZERO;
		if (b.length - kicks < i)
			i = b.length - kicks;
		byte[] tmp = new byte[i];
		for (int j = i - 1; j >= 0; j--) {
			tmp[i - j - 1] = b[kicks + j];
		}
		kicks = kicks + i;
		return new BigInteger(tmp);
	}

	/**
	 * get unsigned number with 8 bytes
	 */
	public BigInteger getUlong() {
		return getBigInteger(8);
	}

	/**
	 * get unsigned number with 4 bytes
	 */
	public long getUInt() {
		return getBigInteger(4).longValue();
	}

	/**
	 * get a number with 4 bytes
	 */
	public int getInt() {
		return getBigInteger(4).intValue();
	}

	/**
	 * compare to specified bytes, if >orig, return integer t,e.g. 5 means that
	 * the 5th byte is >orig.
	 **/
	public int compareTo(byte[] orig) {
		int i = orig.length;
		int t = 0;
		for (int j = 0; j < i; j++) {
			if (orig[j] > b[kicks + j]) {
				t = -(j + 1);
				break;
			} else if (orig[j] < b[kicks + j]) {
				t = j + 1;
				break;
			}
		}
		kicks = kicks + i;
		return t;
	}

	/**
	 * get i bytes
	 */
	public byte[] getBytes(int i) {
		if (i == 0)
			return null;
		byte[] t = new byte[i];
		for (int j = 0; j < i; j++)
			t[j] = b[j + kicks];
		kicks = kicks + i;
		return t;
	}

	/**
	 * get one byte
	 */
	public byte getByte() {
		if (kicks < b.length) {
			kicks++;
			return b[kicks - 1];
		} else
			return 0;
	}

	/**
	 * get encint integer in bytes,get no encint, 0,1,2,3 offset: from where to
	 * get bytes num: get which encint,1st? 2nd? 3rd?
	 */
	public BigInteger getEncint() {
		byte ob;
		BigInteger bi = BigInteger.ZERO;
		byte[] nb = new byte[1];
//		int i = 0;

		while ((ob = this.getByte()) < 0) {
			nb[0] = (byte) ((ob & 0x7f));
			bi = bi.shiftLeft(7).add(new BigInteger(nb));
		}
		nb[0] = (byte) ((ob & 0x7f));
		bi = bi.shiftLeft(7).add(new BigInteger(nb));
		return bi;
	}

	/**
	 * get and trans utf8 char to unicode char modified utf8 was seemed to be
	 * used in chm,it meats the following constraints: 1.only 1 byte or 3 bytes
	 * are used 2.1 byte like 0*******,3 bytes like 1110**** 10****** 10******
	 * so try to catch them
	 */
	public char getUtfChar() {
		byte ob;
		int i = 1;
		byte[] ba;
		ob = this.getByte();
		if (ob < 0) {
			i = 2;
			while ((ob << (24 + i)) < 0)
				i++;
		}
		ba = new byte[i];
		ba[0] = ob;
		int j = 1;
		while (j < i) {
			ba[j] = this.getByte();
			j++;
		}
		i = ba.length;
		// char tmp;
		if (i == 1)
			return (char) ba[0];
		else {
			int n;
			n = ba[0] & 15; // 00001111b,get last 4 bits
			j = 1;
			while (j < i)
				n = (n << 6) + (ba[j++] & 63);// 00111111b,get last 6 bits
			return (char) n;
		}
	}

	/**
	 * get and trans utf8 string to unicode string num refering to bytes number
	 */
	public String getUtfString(int num) {
		// char[] tmp=new char[num];
		int okicks = kicks;
		StringBuffer sb = new StringBuffer();
		while (kicks - okicks < num)
			sb.append(getUtfChar());
		return sb.toString();
	}

}
