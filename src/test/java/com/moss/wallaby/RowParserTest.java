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

import junit.framework.AssertionFailedError;
import junit.framework.ComparisonFailure;
import junit.framework.TestCase;

import bmsi.util.JavaDiff;

import com.moss.diff.DiffCommand;
import com.moss.diff.DiffCommandException;

public class RowParserTest extends TestCase {
	public void testRun(){
		
		String row = "1,14,\"KAREN\",\"FORD\",\"A\",\"248433723\",\"1972-02-06\",\"Brown\",\"Black / African American\",\"5.02\",\"160\",\"F\",\\N,0.0000,1400.0000,\"VGSFOW\",0,1,\"2008-08-25 16:03:17\",\"Referral\",\"KAY-FORD@SUNCOM.NET\",0,\"Good\"";
		assertEquals(RowParser.parse(row, ',').length, 23);
		
		String row2 = "2,14,\"TONYA\",\"STANFORD\",\"D\",\"238570513\",\"1976-02-03\",\"Green\",\"White / Caucasian\",\"5'5\\\"\",\"\",\"F\",\\N,0.0000,2015.0000,\"HEKLFM\",0,1,\"2008-08-25 17:06:44\",\"Newspaper Ad\",\"TONYASANFORD@YAHOO.COM\",0,\"Good\"";
		assertEquals(RowParser.parse(row2, ',').length, 23);
		
		String row3 = "939209,16,282843,\"RB\",\"2009-02-24\",83.8100,0.0000,83.8100,0.0000,0.0000,0.0000";
		String[] values3 = RowParser.parse(row3, ',');
		assertEquals(values3.length, 11);
		assertEquals(values3[4], "2009-02-24");
		assertEquals(values3[5], "83.8100");
		assertEquals(values3[6], "0.0000");
		assertEquals(values3[7], "83.8100");
		
		
		assertEquals(RowParser.parse("\"alpha\",beta,\"gamma\",delta", ',').length, 4);
		assertEquals(RowParser.parse("alpha,beta,\"gamma\",delta", ',').length, 4);
		assertEquals(RowParser.parse("alpha,beta,gamma,delta", ',').length, 4);
		
//				Invoice,00100,"February 17, 2010",,paid,"February 17, 2010",USD,,10404.00,0.00,Y,,,13269169,PORTER a,1.0,10404.00,10404.00,2006,Shareholder Loans,,,,,,,,,,,,1090,Undeposited Funds
//				Invoice,FB00158,"February 5, 2010",,,"February 5, 2010",USD,,34.84,34.84,N,,,13190291,"lateFEE: Late Fee on INVOICE #00069
//				INVOICE Date: September 30, 2009
				
		testParse(
				"Type,Invoice #,Date,P.O. #,Terms,Due Date,Currency,Exchange Rate,Total Due,Balance Due,Paid?,Memo,Contact Name,Entry ID,Description,Quantity,Rate,Total,Account #,Account Name,Item Name,Taxes,Payment Date,Payment Account Number,Payment Account Name,Payment Amount,Exchanged?,Payment Exchange Rate,Payment Memo,Tax Name,Tax Amount,Account #,Account Name",
				new String[]{"Type", "Invoice #", "Date","P.O. #","Terms","Due Date","Currency","Exchange Rate","Total Due","Balance Due","Paid?","Memo","Contact Name","Entry ID","Description","Quantity","Rate","Total","Account #","Account Name","Item Name","Taxes","Payment Date","Payment Account Number","Payment Account Name","Payment Amount","Exchanged?","Payment Exchange Rate","Payment Memo","Tax Name","Tax Amount","Account #","Account Name"}
				);
		testParse(
				"Invoice,00100,\"February 17, 2010\",,paid,\"February 17, 2010\",USD,,10404.00,0.00,Y,,,13269169,PORTER a,1.0,10404.00,10404.00,2006,Shareholder Loans,,,,,,,,,,,,1090,Undeposited Funds",
				new String[]{"Invoice","00100","February 17, 2010","","paid","February 17, 2010","USD","","10404.00","0.00","Y","","","13269169","PORTER a","1.0","10404.00","10404.00","2006","Shareholder Loans","","","","","","","","","","","","1090","Undeposited Funds"}
				);

//		\n\n,
//		
		
		String lineFragmentA = "Invoice,FB00158,\"February 5, 2010\",,,\"February 5, 2010\",USD,,34.84,34.84,N,,,13190291,\"lateFEE: Late Fee on INVOICE #00069";
		String lineFragmentB = "INVOICE Date: September 30, 2009";
		String lineFragmentC = "Amount Due: $686.98 USD\",0.015,686.98,10.30,4080,Fees Invoiced,,,,,,,,,,,,1100,Accounts Receivable";
		
		testParse(
				lineFragmentA,
				null
				);
		testParse(
				lineFragmentA + '\n' + lineFragmentB,
				null
				);
		testParse(
				lineFragmentA + '\n' + lineFragmentB + '\n' + lineFragmentC,
				new String[]{"Invoice","FB00158","February 5, 2010","","","February 5, 2010","USD","","34.84","34.84","N","","","13190291","lateFEE: Late Fee on INVOICE #00069\nINVOICE Date: September 30, 2009\nAmount Due: $686.98 USD","0.015","686.98","10.30","4080","Fees Invoiced","","","","","","","","","","","","1100","Accounts Receivable"}
				);

				
	}
	
	private void testParse(final String line, final String[] expected){
		String[] actual = RowParser.parse(line, ',');
		if(expected==null){
			if(actual!=null){
				throw new AssertionFailedError("Expected incomplete line, but line was found to be complete");
			}
		}else{
			this.assertEquals(expected, actual);
		}
	}
//	
//	public void testClarityExport() throws IOException{
//		CSVReader r = new CSVReader(getClass().getResourceAsStream("/testA.csv"));
//		assertTrue(r.hasNext());
//		r.next();
//		String[] header = r.getColumns();
//		assertEquals(
//				new String[]{"Type", "Invoice #", "Date","P.O. #","Terms","Due Date","Currency","Exchange Rate","Total Due","Balance Due","Paid?","Memo","Contact Name","Entry ID","Description","Quantity","Rate","Total","Account #","Account Name","Item Name","Taxes","Payment Date","Payment Account Number","Payment Account Name","Payment Amount","Exchanged?","Payment Exchange Rate","Payment Memo","Tax Name","Tax Amount","Account #","Account Name"}, 
//				header
//				);
//		assertTrue(r.hasNext());
//		r.next();
//		String[] row1 = r.getColumns();
//		assertEquals(
//				new String[]{"Invoice","00100","February 17, 2010","","paid","February 17, 2010","USD","","10404.00","0.00","Y","","","13269169","PORTER a","1.0","10404.00","10404.00","2006","Shareholder Loans","","","","","","","","","","","","1090","Undeposited Funds"}, 
//				row1
//				);
//		assertTrue(r.hasNext());
//		r.next();
//		String[] row2 = r.getColumns();
//		assertEquals(
//				new String[]{
//						"Invoice","FB00158","February 5, 2010","","","February 5, 2010","USD","","34.84","34.84","N","","","13190291","lateFEE: Late Fee on INVOICE #00069\n" + 
//						"INVOICE Date: September 30"," 2009\n" + 
//						"Amount Due: $686.98 USD","0.015","686.98","10.30","4080","Fees Invoiced","","","","","","","","","","","","1100","Accounts Receivable"
//				}, 
//				row2
//				);
//		
//		assertEquals(row1.length, row2.length);
//	}
	
	
	private void assertEquals(String[] expected, String[] actual){
		int n=expected.length<actual.length?actual.length:expected.length;
		for(int x=0;x<n;x++){
			if(x>expected.length-1) throw new AssertionFailedError("Actual is longer than expected.  Expected " + n + "but was " + actual.length);
			String expectedV = expected[x];
			if(x>actual.length-1) throw new AssertionFailedError("Expected \"" + expectedV + " but got end-of-array: Actual is shorter than expected.");
			String actualV = actual[x];
			if(!expectedV.equals(actualV)){
				DiffCommand diff = new JavaDiff();
				try {
					String differences = diff.unifiedDiff(expectedV, actualV);
					System.out.println(differences);
				} catch (DiffCommandException e) {
					e.printStackTrace();
				}
				System.out.println("#### EXPECTED####\n" + expectedV + "\n##############");
				System.out.println("#### ACTUAL####\n" + actualV + "\n##############");
				throw new ComparisonFailure("Strings not equal on column " + (x+1), expectedV, actualV);
			}
		}
	}
}
