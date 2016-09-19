package com.data;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import com.db.ExcelFunction;
import com.myClass.M;
import com.myClass.U;

public class ZYY {

	public static void ZYY() throws IOException, ClassNotFoundException, SQLException{
		HSSFCell cell = null;
		List<String> listProvince = new ArrayList<>();
		List<String> listCity = new ArrayList<>();
		//读入省名
		String fileName = "E:\\work\\悦悦姐数据分析需求\\以此为准.xls";
		HSSFSheet sheet = ExcelFunction.getSheet(fileName, 0);
		int rowCount = sheet.getLastRowNum();
		for(int k = 1; k < rowCount; k++){
			//读入省名
			cell = sheet.getRow(k).getCell(1);
			String province = replace(U.getCellStringValue(cell));
//			if(!listProvince.contains(province))
				listProvince.add(province);//不排除重名省了，为了能把省和市对齐
		}
		for(int k = 1; k < rowCount; k++){
			//读入市名
			cell = sheet.getRow(k).getCell(2);
			String city = replace(U.getCellStringValue(cell));
			listCity.add(city);
		}
//		listCity.add("其它");
		
		//读取access中的数据
		Map<String, Map<Integer, List<String>>> map = new HashMap<>();
		
        String sDriver="sun.jdbc.odbc.JdbcOdbcDriver";  
        try{  
            Class.forName(sDriver);  
        }  
        catch(Exception e){  
            System.out.println("无法加载驱动程序");  
            return;  
        }  
        String sCon="jdbc:odbc:2006";
		Connection c = DriverManager.getConnection(sCon);
        Statement s = c.createStatement();     
        ResultSet  r = s.executeQuery("SELECT 地区,商品代码,金额,数量 FROM 200602");
        String address = "";
        int id = -1;
        String priceAndQuantity = "";
        String province;
        String city;
        int indexCity = -1;//记录是第几个城市匹配正确的，以此来确定省份
        String pc;//省市
        int k = 0;//不加这句跑了一会儿会换成区异常，虽然不知道为什么..
        while(r.next()){
        	k++;
        	U.print(k);
//        	if(k > 10) break;
//        	List<String> listPriceAndQuantity = new ArrayList<>();
        	address = r.getString("地区"); if(address == null) continue;
        	id = Integer.parseInt(r.getString("商品代码"));
        	priceAndQuantity = (int)Double.parseDouble(r.getString("金额")) + "," + r.getString("数量");
        	//获取省和市的名称
        	province = "?";
            city = "?";
            indexCity = -1;
        	for(int i = 0; i < listProvince.size(); i++){
        		if(address.contains(listProvince.get(i))){
        			province = listProvince.get(i);
        			address = address.replaceAll(listProvince.get(i), "");
        			break;
        		}
        	}
        	address = replace(address);
        	for(int i = 0; i < listCity.size(); i++){
        		if(address.contains(listCity.get(i))){
        			city = listCity.get(i);
        			indexCity = i;
        		}
        	}
        	if(province.equals("重庆")) city = "重庆";
        	if(province.equals("北京")) city = "北京";
        	if(province.equals("天津")) city = "天津";
        	if(province.equals("海南")) city = "海南";
        	pc = (province.equals("?") ? (indexCity != -1 ? listProvince.get(indexCity) : "未知") : province) + "," + (city.equals("?") ? address+"?" : city);
        	//存放数据
        	if(map.get(pc) == null){//还没有该省市组合的情况
        		Map<Integer, List<String>> tempMap = new HashMap<>();
        		List<String> tempList = new ArrayList<>();
        		tempList.add(priceAndQuantity);
        		tempMap.put(id, tempList);
        		map.put(pc, tempMap);
        	}
        	else{
        		if(map.get(pc).get(id) == null){//省市组合下，还没有该商品id的情况
        			List<String> tempList = new ArrayList<>();
            		tempList.add(priceAndQuantity);
            		map.get(pc).put(id, tempList);
        		}
        		else{//全有，则直接增加list<"金额,数量">即可
        			map.get(pc).get(id).add(priceAndQuantity);
        		}
        	}
        }
        
        //输出到txt
        FileWriter fw = new FileWriter("E:\\work\\悦悦姐数据分析需求\\middle.txt");
        Set<String> setPC = map.keySet();
        for(String keyPC : setPC){
        	String ssPC[] = keyPC.split(",");
        	Set<Integer> setID = map.get(keyPC).keySet();
        	for(int keyID : setID){
        		fw.write(ssPC[0] + "\t" + ssPC[1] + "\t");
        		fw.write(keyID + "\t");
        		List<String> tempList = map.get(keyPC).get(keyID);
        		List<Double> listPrice = new ArrayList<>();//金额
    			List<Double> listQuantity = new ArrayList<>();//数量
    			List<Double> listPer = new ArrayList<>();//单价
        		for(int i = 0; i < tempList.size(); i++){
        			String ss[] = tempList.get(i).split(",");
        			listPrice.add(Double.parseDouble(ss[0]));
        			listQuantity.add(Double.parseDouble(ss[1]));
        			listPer.add(Double.parseDouble(ss[0]) / Double.parseDouble(ss[1]));
        		}
        		fw.write(U.MATH_getSum(listPrice) + "\t");
        		fw.write(U.MATH_getSum(listQuantity) + "\t");
        		fw.write(U.MATH_getAverage(listPer) + "\t");
        		fw.write(U.MATH_getVariance(listPer) + "\t");
        		fw.write("\r\n");
        	}
        }
        fw.close();
        U.print(map.size());
        U.print("已输出到middle.txt");
        
        s.close();  
	}
	
	private static String replace(String str){
		str = str.trim().replaceAll(" ", "").replaceAll("省", "").replaceAll("自治区", "").replaceAll("市", "").replaceAll("壮族", "")
			.replaceAll("回族", "").replaceAll("维吾尔", "").replaceAll("汨罗", "岳阳").replaceAll("胶州", "青岛").replaceAll("黄岩", "台州")
			.replaceAll("新泰", "泰安").replaceAll("通县", "北京").replaceAll("张家港", "苏州").replaceAll("龙口", "烟台").replaceAll("綦江县", "重庆")
			.replaceAll("石景山", "北京").replaceAll("汉沽", "天津").replaceAll("老河口", "襄阳").replaceAll("株州", "株洲").replaceAll("南汇", "浦东新")
			.replaceAll("丰台", "北京").replaceAll("顺义", "北京").replaceAll("巴南区", "重庆").replaceAll("城口县", "重庆").replaceAll("蛟河", "吉林")
			.replaceAll("常熟", "苏州").replaceAll("浑江", "白山").replaceAll("南岸区", "重庆").replaceAll("河西", "天津").replaceAll("黔江", "重庆")
			.replaceAll("青州", "潍坊").replaceAll("潼南", "重庆").replaceAll("凭祥", "崇左").replaceAll("诸暨", "绍兴").replaceAll("海淀", "北京")
			.replaceAll("义马", "三门峡").replaceAll("马尾", "福州").replaceAll("巴音", "阿拉善左旗").replaceAll("六盘山", "六盘水").replaceAll("慈溪", "宁波")
			.replaceAll("永安", "三明").replaceAll("沁阳", "焦作").replaceAll("大庸", "张家界").replaceAll("诸城", "潍坊").replaceAll("宜兴", "无锡")
			.replaceAll("阿拉善盟", "阿拉善左旗").replaceAll("梅河口", "通化").replaceAll("垫江", "重庆").replaceAll("昆山", "苏州").replaceAll("开原", "铁岭")
			.replaceAll("石河子", "石河子").replaceAll("金桥", "浦东新").replaceAll("淮阴", "淮安").replaceAll("瑞安", "温州").replaceAll("枣阳", "襄阳")
			.replaceAll("静海", "天津").replaceAll("江山", "衢州").replaceAll("滕州", "枣庄").replaceAll("曲阜", "济宁").replaceAll("巢湖", "合肥")
			.replaceAll("石狮", "泉州").replaceAll("东郊", "天津").replaceAll("川沙" , "浦东新").replaceAll("襄樊", "襄阳").replaceAll("丰都县", "重庆")
			.replaceAll("瓦房店", "大连").replaceAll("玉林", "南宁").replaceAll("忠县", "重庆").replaceAll("江北区", "重庆").replaceAll("顺德", "佛山")
			.replaceAll("门头沟", "北京").replaceAll("昌平", "北京").replaceAll("满州里", "呼伦贝尔").replaceAll("达县", "达州").replaceAll("迪庆", "香格里拉")
			.replaceAll("富锦", "佳木斯").replaceAll("即墨", "青岛").replaceAll("卢湾", "黄浦").replaceAll("宣武区", "北京").replaceAll("广汉", "德阳")
			.replaceAll("酉阳土家族苗族自治县", "重庆").replaceAll("阿城", "哈尔滨").replaceAll("松花江", "哈尔滨").replaceAll("张家港", "苏州").replaceAll("西城区", "北京")
			.replaceAll("宿县", "宿州").replaceAll("滁县", "滁州").replaceAll("莱阳", "烟台").replaceAll("郧阳", "十堰").replaceAll("辉县", "新乡")
			.replaceAll("崇文区", "北京").replaceAll("南川", "重庆").replaceAll("鄂西", "襄阳").replaceAll("沉阳", "沈阳").replaceAll("湘乡", "湘潭")
			.replaceAll("哲里木盟", "通辽").replaceAll("塘沽", "天津").replaceAll("天竺出口加工区", "北京").replaceAll("丹阳", "镇江").replaceAll("醴陵", "株洲")
			.replaceAll("九龙坡区", "重庆").replaceAll("二连", "锡林郭勒盟").replaceAll("仪征", "扬州").replaceAll("漕河泾", "徐汇区").replaceAll("思茅", "普洱")
			.replaceAll("铁力", "伊春").replaceAll("铁法", "铁岭").replaceAll("余姚", "宁波").replaceAll("零陵", "永州").replaceAll("密山", "鸡西")
			.replaceAll("海宁", "嘉兴").replaceAll("集安", "通化").replaceAll("江油", "绵阳").replaceAll("平度", "青岛").replaceAll("都江堰", "成都")
			.replaceAll("兴化", "泰州").replaceAll("甘南", "齐齐哈尔").replaceAll("北票", "朝阳").replaceAll("东台", "盐城").replaceAll("来阳", "衡阳")
			.replaceAll("番禹", "广州").replaceAll("惠民", "滨州").replaceAll("文登", "威海").replaceAll("外高桥保税区", "浦东新").replaceAll("卫辉", "新乡")
			.replaceAll("九台", "长春").replaceAll("东兴", "防城港").replaceAll("太仓", "苏州").replaceAll("浦东", "浦东新").replaceAll("瑞昌", "九江")
			.replaceAll("荣城", "威海").replaceAll("武安", "邯郸").replaceAll("义乌", "金华").replaceAll("图们", "延边").replaceAll("海城", "鞍山")
			.replaceAll("锦西", "葫芦岛").replaceAll("兰溪", "金华").replaceAll("东阳", "金华").replaceAll("奉化", "宁波").replaceAll("漕河泾出口加工区", "徐汇区")
			.replaceAll("伊克昭盟", "鄂尔多斯").replaceAll("延边", "延吉").replaceAll("萧山", "杭州").replaceAll("黔西南", "兴义").replaceAll("黔东南", "凯里")
			
			.replaceAll("黔南", "都匀").replaceAll("南海", "佛山").replaceAll("银南", "固原").replaceAll("同江", "佳木斯市").replaceAll("黔东南", "凯里")
			.replaceAll("吴江", "苏州").replaceAll("兴城", "葫芦岛").replaceAll("启东", "南通").replaceAll("禹州", "许昌").replaceAll("梧州", "贺州")
			.replaceAll("绥芬河", "牡丹江").replaceAll("渮泽", "菏泽").replaceAll("启东", "南通").replaceAll("禹州", "许昌").replaceAll("梧州", "贺州");
		return str;
	}
}
