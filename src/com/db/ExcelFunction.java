package com.db;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.myClass.U;

public class ExcelFunction {

	//根据excel地址和Sheet下标获取一张表格
	public static HSSFSheet getSheet(String fileName, int sheetIndex) throws IOException{
		POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(fileName));   
		HSSFWorkbook wb = new HSSFWorkbook(fs);
		HSSFSheet sheet = wb.getSheetAt(0);
		return sheet;
	}
	
	//获取excel中sheet的数量
	public static int getSheetNumber(String fileName) throws FileNotFoundException, IOException{
		POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(fileName));
		HSSFWorkbook wb = new HSSFWorkbook(fs);
		return wb.getNumberOfSheets();
	}
	
	
}
