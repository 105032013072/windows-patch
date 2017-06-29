package com.bosssoft.install.windows_patch;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.bosssoft.install.windows.patch.util.PatchFileManager;
import com.bosssoft.platform.installer.io.FileUtils;
import com.bosssoft.platform.installer.io.operation.exception.OperationException;

public class GetTest {
  public static void main(String[] args){
	      
	try {
		 SAXReader reader=new SAXReader();
			Document doc=DocumentHelper.createDocument();
			Element root=DocumentHelper.createElement("rollback");
			//记录备份时间
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			root.addAttribute("backupTime", df.format(new Date()));
			doc.setRootElement(root);
			
			OutputFormat format =OutputFormat.createPrettyPrint(); 
			  format.setEncoding("utf-8");//设置编码格式 
			  format.setNewLineAfterDeclaration(false);
	     XMLWriter xmlWriter = new XMLWriter(new FileOutputStream("D://log.xml"),format);
		    xmlWriter.write(doc);
		    xmlWriter.close();
	} catch (Exception e) {
	e.printStackTrace();
	}
	
	 
  }
}
