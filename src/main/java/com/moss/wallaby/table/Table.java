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
package com.moss.wallaby.table;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.moss.wallaby.CSVReader;

/**
 * Assumes the first record in a CSV file is the header, each column containing
 * that column's name. Assumes that each column name is unique in a CSV file.
 */
public final class Table implements Iterable<Row> {

	private final File file;
	private final HeaderInfo info;
	
	public Table(File file) throws IOException {
		if (file == null) throw new NullPointerException();
		this.file = file;
		CSVReader r = new CSVReader(file);
		info = new HeaderInfo(r.next());
		r.close();
	}
	
	public HeaderInfo info() {
		return info;
	}

	public TableIterator iterator() {
		try {
			return new TableIterator(new FileReader(file));
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
