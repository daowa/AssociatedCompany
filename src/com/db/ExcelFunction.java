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

	//����excel��ַ��Sheet�±��ȡһ�ű��
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
	
	//��ȡexcel��sheet������
	public static int getSheetNumber(String fileName) throws FileNotFoundException, IOException{
		POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(fileName));
		HSSFWorkbook wb = new HSSFWorkbook(fs);
		return wb.getNumberOfSheets();
	}
	
	//�����±�ɾ����
	//����װ����ȫ������Ҫ��ԭ����������workbook.write��Ż���Ч
	public static void removeRow(String fileName, int sheetIndex, List<Integer> listIndex) throws IOException{
		U.print("��ʼɾ����");
		FileInputStream is = new FileInputStream(fileName);
        HSSFWorkbook workbook = new HSSFWorkbook(is);
		int offset = 0;//ÿɾ��һ������index��ƫ���������1����ԭ��indexΪ30�ļ�¼������Ϊ29��
		HSSFSheet sheet = workbook.getSheetAt(sheetIndex);
		for(int index : listIndex){
			U.print("��ʼɾ��" + index + "��ƫ����Ϊ" + offset + ",lastRowNumΪ" + sheet.getLastRowNum());
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
