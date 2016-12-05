package com.db;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.myClass.U;

public class ExcelFunction {
	
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
	
	//根据下标删除行
	//还封装不完全，还需要在原方法处进行workbook.write后才会生效
	public static void removeRow(String fileName, int sheetIndex, List<Integer> listIndex) throws IOException{
		U.print("开始删除行");
		FileInputStream is = new FileInputStream(fileName);
        HSSFWorkbook workbook = new HSSFWorkbook(is);
		int offset = 0;//每删除一个数，index的偏移量都会加1（即原本index为30的记录，现在为29）
		HSSFSheet sheet = workbook.getSheetAt(sheetIndex);
		for(int index : listIndex){
			U.print("开始删除" + index + "，偏移量为" + offset + ",lastRowNum为" + sheet.getLastRowNum());
			if(index - offset + 1 > sheet.getLastRowNum()) break;
			sheet.shiftRows(index - offset + 1, sheet.getLastRowNum(), -1);
			offset ++;
		}
        FileOutputStream os = new FileOutputStream(fileName);
        workbook.write(os);
        is.close();
        os.close();
	}	
	
}
