package com.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import com.myClass.U;

public class WordFunction {
	
    public static List<String> getRowList(String path) {
         try {
//             InputStream is = new FileInputStream(new File("2003.doc"));
//             WordExtractor ex = new WordExtractor(is);
//             String text2003 = ex.getText();
//             System.out.println(text2003);

//             OPCPackage opcPackage = POIXMLDocument.openPackage("E:\\work\\关联公司\\方案\\1103\\央企上市公司名单.docx");
//             POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage);
             InputStream is = new FileInputStream(path);  
             XWPFDocument doc = new XWPFDocument(is);  
             XWPFWordExtractor extractor = new XWPFWordExtractor(doc);
             String text2007 = extractor.getText();
             String[] temp = text2007.split("\n");
             List<String> result = Arrays.asList(temp);
             return result;
             
         } catch (Exception e) {
             e.printStackTrace();
         }
		return null;
     }
    
 }