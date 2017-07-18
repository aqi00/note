/*
 * RandomAccessChm.java
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

/**
 * 
 * @author yufeng
 * 
 *         it provide the random method to read chm now it has nothing to do but
 *         inherit from randomaccessfile
 */
public class RandomAccessChm extends RandomAccessFile {

	private static int remains = 0;
	/** Creates a new instance of RandomAccessChm */
	private static long val = 0;

	public RandomAccessChm(String filename, String mode)
			throws FileNotFoundException {
		super(filename, mode);
	}

}
