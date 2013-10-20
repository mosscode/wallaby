/**
 * Copyright (C) 2013, Moss Computing Inc.
 *
 * This file is part of wallaby.
 *
 * wallaby is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * wallaby is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with wallaby; see the file COPYING.  If not, write to the
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 *
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library.  Thus, the terms and
 * conditions of the GNU General Public License cover the whole
 * combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under
 * terms of your choice, provided that you also meet, for each linked
 * independent module, the terms and conditions of the license of that
 * module.  An independent module is a module which is not derived from
 * or based on this library.  If you modify this library, you may extend
 * this exception to your version of the library, but you are not
 * obligated to do so.  If you do not wish to do so, delete this
 * exception statement from your version.
 */
package com.moss.wallaby.util;

import java.io.IOException;

import com.moss.wallaby.CSVReader;

public final class CSVComparer {
	public static final class AssertionFailedError extends Error {

		public AssertionFailedError() {
			super();
		}

		public AssertionFailedError(String message, Throwable cause) {
			super(message, cause);
		}

		public AssertionFailedError(String message) {
			super(message);
		}

		public AssertionFailedError(Throwable cause) {
			super(cause);
		}
		
	}
	public void assertEquals(CSVReader expected, CSVReader actual) throws IOException {
		int line = 0;
		while(expected.hasNext()){
			line++;
			if(!actual.hasNext()){
				throw new AssertionFailedError("Unexpected end of input: was expecting a row like: " + expected.getCurrentLine() );
			}
			expected.next();
			actual.next();
			
			String[] expectedCells = expected.getColumns();
			String[] actualCells = actual.getColumns();
			
			if(expectedCells.length!=actualCells.length){
				throw new AssertionFailedError(
						"   Expected Line: " + expected.getCurrentLine() + "\n" + 
						"     Actual Line: "  + actual.getCurrentLine()
				);
			}
			
			for(int x =0; x<expectedCells.length;x++){
				String expectedValue = expectedCells[x];
				String actualValue = actualCells[x];
				if(!expectedValue.equals(actualValue)){
					throw new AssertionFailedError("Line " + line + ", column " + x + ": Expected '" + expectedValue + "' but was '" + actualValue + "'\n" + 
							"   Expected Line: " + expected.getCurrentLine() + "\n" + 
							"     Actual Line: "  + actual.getCurrentLine()
						);
				}
			}
		}
		if(actual.hasNext()){
			actual.next();
			throw new AssertionFailedError("Wasn't expecting any more rows, but found: " + actual.getCurrentLine());
		}
	}
}
