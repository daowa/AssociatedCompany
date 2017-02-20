package com.myClass;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.db.FileFunction;
import com.myClass.U;
import com.sun.jna.Library;
import com.sun.jna.Native;

public class NLPIR {

	// 定义接口CLibrary，继承自com.sun.jna.Library
	public interface CLibrary extends Library {
			
			// 定义并初始化接口的静态变量 这一个语句是来加载dll的，注意dll文件的路径可以是绝对路径也可以是相对路径，只需要填写dll的文件名，不能加后缀。
			CLibrary Instance = (CLibrary)Native.loadLibrary("E:\\JavaWorkspace\\textMining_Disney\\NLPIR", CLibrary.class);
			
			// 初始化函数声明
			public boolean NLPIR_Init(byte[] sDataPath, int encoding, byte[] sLicenceCode);
			
			//执行分词函数声明
			public String NLPIR_ParagraphProcess(String sSrc, int bPOSTagged);
			
			//提取关键词函数声明
			public String NLPIR_GetKeyWords(String sLine, int nMaxKeyLimit, boolean bWeightOut);
			
			public String NLPIR_GetFileKeyWords(String sLine, int nMaxKeyLimit, boolean bWeightOut);
			
			public int NLPIR_AddUserWord(String sWord);//add by qp 2008.11.10
			
			public int NLPIR_DelUsrWord(String sWord);//add by qp 2008.11.10
			
			public void NLPIR_SaveTheUsrDic();
			
			public String NLPIR_GetLastErrorMsg();
			
			
			//添加新词
			
			public boolean NLPIR_NWI_Start();//启动新词发现功能
			
			public boolean NLPIR_NWI_AddFile(String address); //添加新词训练的文件，可反复添加
			
			public boolean NLPIR_NWI_Complete();//添加文件或者训练内容结束
			
			public boolean NLPIR_NWI_Result2UserDict();//将新词识别结果导入到用户词典中;
													   //需要在运行NLPIR_NWI_Complete()之后，才有效
													   //如果需要将新词结果永久保存，建议在执行NLPIR_SaveTheUsrDic
			
			public String NLPIR_NWI_GetResult();
			
			//从字符串中获取新词,添加新词范围里仅这个执行有效
		    public String NLPIR_GetNewWords(String sLine, int nMaxKeyLimit, boolean bWeightOut);
		    
		    public String NLPIR_GetFileNewWords(String sTextFile,int nMaxKeyLimit, boolean bWeightOut);


			
			//退出函数声明
			public void NLPIR_Exit();
	}
	
	
	
	
	
	
	public static String transString(String aidString, String ori_encoding,
			String new_encoding) {
		try {
			return new String(aidString.getBytes(ori_encoding), new_encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void NlpirInit() throws UnsupportedEncodingException{
		String argu = "";
		
		String system_charset = "UTF-8";
		
		int charset_type = 1;
		
		if (!CLibrary.Instance.NLPIR_Init(argu.getBytes(system_charset), charset_type, "0".getBytes(system_charset))) {
			System.err.println("初始化失败！");
		}
	}
	
	public static void NlpirExit(){
		CLibrary.Instance.NLPIR_Exit();
	}
	
	//分词，返回含有词性的词数组
	public static String[] wordSegmentateWithCharacteristic(String sInput){
		String nativeBytes = CLibrary.Instance.NLPIR_ParagraphProcess(sInput, 3);
		return nativeBytes.split(" ");
	}
	
	//分词，返回不含有词性的词数组
	public static String[] wordSegmentateWithoutCharacteristic(String sInput){
		String nativeBytes_justWord = CLibrary.Instance.NLPIR_ParagraphProcess(sInput, 0);
		return nativeBytes_justWord.split(" ");
	}
	
	//将停用词读入内存
	public static List<String> getStopWords(){
		String stopWordsAddress = "E:\\work\\smda气象+数据竞赛\\vocabulary\\stopWords.txt";
		List<String> stopWords = FileFunction.readTxt_StopWords(stopWordsAddress);
		return stopWords;
	}
	
	//添加用户词典
	public static void addUserDict(String word, String characteritic){
		CLibrary.Instance.NLPIR_AddUserWord(word + " " + characteritic);
		CLibrary.Instance.NLPIR_SaveTheUsrDic();
	}
	
	public static String getNewWord(String rawString) throws IOException{
		return CLibrary.Instance.NLPIR_GetNewWords(rawString, 50, false);
	}
	
	//读取用户词典并添加
	public static void addUserDicFromTxt() throws IOException{
		NLPIR.NlpirInit();
		File file = new File("E:\\work\\smda气象+数据竞赛\\vocabulary\\userdic.txt");
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = "";
		while((line = reader.readLine()) != null){
			line = line.trim();
			NLPIR.addUserDict(line, "n");
			U.print(line);
		}
		CLibrary.Instance.NLPIR_SaveTheUsrDic();
		NLPIR.NlpirExit();
		U.print("添加用户词典完成");
	}
	
}
