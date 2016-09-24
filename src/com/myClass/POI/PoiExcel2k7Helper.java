package com.myClass.POI;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.myClass.U;

public class PoiExcel2k7Helper extends PoiExcelHelper {
    /** 获取sheet列表 */  
    public ArrayList<String> getSheetList(String filePath) {  
        ArrayList<String> sheetList = new ArrayList<String>(0);  
        try {  
            XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(filePath));  
            int i = 0;  
            while (true) {  
                try {  
                    String name = wb.getSheetName(i);  
                    sheetList.add(name);  
                    i++;  
                } catch (Exception e) {  
                    break;  
                }  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return sheetList;  
    }  
  
    /** 读取Excel文件内容 */  
    public ArrayList<ArrayList<String>> readExcel(String filePath, int sheetIndex, String rows, String columns) {  
    	U.print("读取" + filePath);
        ArrayList<ArrayList<String>> dataList = new ArrayList<ArrayList<String>> ();  
        try {  
            XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(filePath));  
            XSSFSheet sheet = wb.getSheetAt(sheetIndex);  
            
            dataList = readExcel(sheet, rows, getColumnNumber(sheet, columns));  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return dataList;  
    }  
      
    /** 读取Excel文件内容 */  
    public ArrayList<ArrayList<String>> readExcel(String filePath, int sheetIndex, String rows, int[] cols) { 
    	U.print("读取" + filePath);
        ArrayList<ArrayList<String>> dataList = new ArrayList<ArrayList<String>> ();  
        try {  
            XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(filePath));  
            XSSFSheet sheet = wb.getSheetAt(sheetIndex);  
              
            dataList = readExcel(sheet, rows, cols);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return dataList;
    }
}
