package com.myClass;

import java.util.Comparator;
import java.util.Map;

public class ValueComparator2 implements Comparator<String> {
  
	Map<String, Double> base;  
  	public ValueComparator2(Map<String, Double> base) {  
  		this.base = base;  
  	}  
    // Note: this comparator imposes orderings that are inconsistent with equals.      
    public int compare(String a, String b) {  
        if (base.get(a) >= base.get(b)) {  
            return -1;  
        }
        else if(base.get(a) < base.get(b)){  
            return 1;  
        } 
        else {
        	return 0;// returning 0 would merge keys  
        }
    } 
}
