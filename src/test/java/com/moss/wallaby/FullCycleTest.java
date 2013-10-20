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
package com.moss.wallaby;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.URL;

import junit.framework.ComparisonFailure;
import junit.framework.TestCase;

import bmsi.util.JavaDiff;

import com.moss.diff.DiffCommand;
import com.moss.diff.DiffCommandException;
import com.moss.wallaby.CSVWriter.QuoteSpec;
import com.moss.wallaby.CSVWriter.QuoteSpec.QuoteMode;

public class FullCycleTest extends TestCase {
	
	public void testFileA(){
		run(getClass().getResource("/testA.csv"));
	}
	
	private void run(URL source) {
		try {
			final String expected = readString(source.openStream());
			
			File temp = File.createTempFile(getClass().getName(), ".csv");
			File temp2 = File.createTempFile(getClass().getName(), ".csv");
			csvCopy(source.openStream(), new FileOutputStream(temp));
			csvCopy(new FileInputStream(temp), new FileOutputStream(temp2));
			
			final String actual = readString(new FileInputStream(temp2));
			
			if(!expected.equals(actual)){
				DiffCommand diff = new JavaDiff();
				try {
					String differences = diff.unifiedDiff(expected, actual);
					System.out.println(differences);
				} catch (DiffCommandException e) {
					e.printStackTrace();
				}
				System.out.println("#### EXPECTED####\n" + expected + "\n##############");
				System.out.println("#### ACTUAL####\n" + actual + "\n##############");
				throw new ComparisonFailure("Strings not equal", expected, actual);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void csvCopy(InputStream source, OutputStream dest) throws IOException {
		final CSVReader in = new CSVReader(source);
		final CSVWriter out = new CSVWriter(new OutputStreamWriter(dest), new QuoteSpec("\"", "'", QuoteMode.AS_NEEDED));
		while(in.hasNext()){
			in.next();
			for(String value : in.getColumns()){
				out.write(value);
			}
			if(in.hasNext()){
				out.newLine();
			}
		}
		in.close();
		out.flush();
		dest.close();
	}
	
	private String readString(InputStream stream) throws IOException {
		char[] buffer = new char[1024*1024];
		Reader reader = new InputStreamReader(stream);
		StringBuilder text = new StringBuilder();
		for(int numRead = reader.read(buffer); numRead!=-1;numRead = reader.read(buffer)){
			text.append(buffer, 0, numRead);
		}
		reader.close();
		return text.toString();
	}
}
