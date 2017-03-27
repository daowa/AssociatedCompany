package com.Others;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SaxHandler extends DefaultHandler{

    /* �˷������������� 
       arg0�Ǵ��������ַ����飬�����Ԫ������ 
       arg1��arg2�ֱ�������Ŀ�ʼλ�úͽ���λ�� */ 
    @Override 
    public void characters(char[] arg0, int arg1, int arg2) throws SAXException { 
        String content = new String(arg0, arg1, arg2); 
        System.out.println(content); 
        super.characters(arg0, arg1, arg2); 
    } 

    @Override 
    public void endDocument() throws SAXException { 
        System.out.println("\n�����������������ĵ���������"); 
        super.endDocument(); 
    } 

    /* arg0�����ƿռ� 
       arg1�ǰ������ƿռ�ı�ǩ�����û�����ƿռ䣬��Ϊ�� 
       arg2�ǲ��������ƿռ�ı�ǩ */ 
    @Override 
    public void endElement(String arg0, String arg1, String arg2) 
            throws SAXException { 
        System.out.println("��������Ԫ��  " + arg2); 
        super.endElement(arg0, arg1, arg2); 
    } 

    @Override 
    public void startDocument() throws SAXException { 
        System.out.println("����������ʼ�����ĵ���������\n"); 
        super.startDocument(); 
    } 

    /*arg0�����ƿռ� 
      arg1�ǰ������ƿռ�ı�ǩ�����û�����ƿռ䣬��Ϊ�� 
      arg2�ǲ��������ƿռ�ı�ǩ 
      arg3�����Եļ��� */
    @Override
    public void startElement(String arg0, String arg1, String arg2, 
            Attributes arg3) throws SAXException { 
        System.out.println("��ʼ����Ԫ�� " + arg2); 
        if (arg3 != null) { 
            for (int i = 0; i < arg3.getLength(); i++) { 
                 // getQName()�ǻ�ȡ�������ƣ� 
                System.out.print(arg3.getQName(i) + "=\"" + arg3.getValue(i) + "\""); 
            }
        }
        System.out.print(arg2 + ":"); 
        super.startElement(arg0, arg1, arg2, arg3); 
    }
}
