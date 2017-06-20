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

public class GetTest {
  public static void main(String[] args){
	  try{
		  String path="D:"+File.separator+"test"+File.separator+"backups.xml";
		  SAXReader reader=new SAXReader();
	      Document doc=reader.read(new File(path));
	      
	      Element root=doc.getRootElement();
	      Element e=DocumentHelper.createElement("delete");
	      root.add(e);
	      e.add(DocumentHelper.createElement("file"));
	      e.add(DocumentHelper.createElement("file"));
	      
	      OutputFormat format =OutputFormat.createPrettyPrint(); 
		  format.setEncoding("utf-8");//设置编码格式 
		  format.setNewLineAfterDeclaration(false);
       XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(path),format);
	    xmlWriter.write(doc);
	    xmlWriter.close();
	  }catch(Exception e){
		  e.printStackTrace();
	  }
	  
	  
  }
}
