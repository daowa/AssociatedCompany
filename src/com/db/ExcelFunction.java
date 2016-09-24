package com.db;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.myClass.U;

public class ExcelFunction {
	
	//
	public static Workbook getWorkBook(String fileName, int sheetIndex) throws IOException{
		Workbook wb = null;
        try {
            wb = new XSSFWorkbook(new FileInputStream(fileName));
        } catch (Exception ex) {
            wb = new HSSFWorkbook(new FileInputStream(fileName));
        }
		return wb;
	}

	//根据excel地址和Sheet下标获取一张表格
	public static HSSFSheet getSheet_HSSF(String fileName, int sheetIndex) throws IOException{
		POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(fileName));
		HSSFWorkbook wb = new HSSFWorkbook(fs);
		HSSFSheet sheet = wb.getSheetAt(sheetIndex);
		return sheet;
	}
	public static XSSFSheet getSheet_XSSF(String fileName, int sheetIndex) throws IOException{
		InputStream is = new FileInputStream(fileName);
		XSSFWorkbook wb = new XSSFWorkbook(is);
		XSSFSheet sheet = wb.getSheetAt(sheetIndex);
		return sheet;
	}
	
	//获取excel中sheet的数量
	public static int getSheetNumber(String fileName) throws FileNotFoundException, IOException{
		POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(fileName));
		HSSFWorkbook wb = new HSSFWorkbook(fs);
		return wb.getNumberOfSheets();
	}
	
	
}
