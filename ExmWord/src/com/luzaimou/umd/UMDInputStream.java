package com.luzaimou.umd;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/*------------------------------------------------------------------------------
 * COPYRIGHT liuyuan 2009
 *
 * The copyright to the computer program(s) herein is the property of
 * MR.Yuan Liu . The programs may be used and/or copied only with written
 * permission from MR.Yuan Liu. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *----------------------------------------------------------------------------*/
public class UMDInputStream {
	private volatile InputStream in;

	public UMDInputStream(InputStream in) {
		this.in = in;
	}

	public final int read(byte b[]) throws IOException {
		return in.read(b, 0, b.length);
	}

	public final byte readByte() throws IOException {
		int ch = in.read();
		if (ch < 0) {
			throw new EOFException();
		}
		return (byte) (ch);
	}

	public final int readUnsignedByte() throws IOException {
		int ch = in.read();
		if (ch < 0) {
			throw new EOFException();
		}
		return ch;
	}

	public final boolean readBoolean() throws IOException {
		int ch = in.read();
		if (ch < 0) {
			throw new EOFException();
		}
		return (ch != 0);
	}

	public final short readShort() throws IOException {
		int ch2 = in.read();
		int ch1 = in.read();
		if ((ch1 | ch2) < 0) {
			throw new EOFException();
		}
		return (short) ((ch1 << 8) + (ch2 << 0));
	}

	public final int readUnsignedShort() throws IOException {
		int ch2 = in.read();
		int ch1 = in.read();
		if ((ch1 | ch2) < 0) {
			throw new EOFException();
		}
		return (ch1 << 8) + (ch2 << 0);
	}

	public final char readChar() throws IOException {
		int ch2 = in.read();
		int ch1 = in.read();
		if ((ch1 | ch2) < 0) {
			throw new EOFException();
		}
		return (char) ((ch1 << 8) + (ch2 << 0));
	}

	public final int readInt() throws IOException {
		int ch4 = in.read();
		int ch3 = in.read();
		int ch2 = in.read();
		int ch1 = in.read();
		if ((ch1 | ch2 | ch3 | ch4) < 0) {
			throw new EOFException();
		}
		return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
	}

	public final int skipBytes(int n) throws IOException {
		int total = 0;
		int cur = 0;
		while ((total < n) && ((cur = (int) in.skip(n - total)) > 0)) {
			total += cur;
		}
		return total;
	}

	public String toUnicodeString() throws IOException {
		StringBuffer buffer = new StringBuffer();
		while (true) {
			try {
				buffer.append(readChar());
			} catch (EOFException e) {
				break;
			}
		}
		return buffer.toString();
	}

	public void close() throws IOException {
		in.close();
	}

	public int available() throws IOException {
		return in.available();
	}
}
