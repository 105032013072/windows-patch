package com.bosssoft.install.windows_patch;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.bosssoft.platform.installer.io.FileUtils;
import com.bosssoft.platform.installer.io.operation.exception.OperationException;

public class GetTest {
  public static void main(String[] args){
	      
		  String path="D:"+File.separator+"test"+File.separator+"me.xml";
		  if(new File(path).exists()) System.out.println("存在");
		  else System.out.println("不存在");
		  try {
			FileUtils.delete(new File(path), null, null);
		} catch (OperationException e) {
			e.printStackTrace();
		}
  }
}
