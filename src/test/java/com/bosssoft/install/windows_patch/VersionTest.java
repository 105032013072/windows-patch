package com.bosssoft.install.windows_patch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.bosssoft.install.windows.patch.util.PatchFileManager;

public class VersionTest {

	public static void main(String[] args) {
		String patchVersion=PatchFileManager.getPatchHome()+"/version.xml";
		try{
			SAXReader reader = new SAXReader();
			Document document = reader.read(patchVersion);
			Element product= document.getRootElement();
			
			if(product.attribute("version")!=null){//更新产品版本
				updateProductVersion(product);
			}
			//更新或者创建应用版本信息
		Iterator<Element> it=product.elementIterator();
		while(it.hasNext()){
			Element appelement=it.next();
			setAppVersion(appelement);
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	private static void setAppVersion(Element appelement) {
		String file="D:\\test\\boss_home"+File.pathSeparator+appelement.attributeValue("name")+File.pathSeparator+"version.xml";
		if(new File(file).exists()) updateAppVersion(appelement,file);
		else createAppVersion(appelement,file);
		
	}

	//创建新应用的版本信息文件
	private static void createAppVersion(Element appelement, String file) {
		try{
			BufferedWriter bw = new BufferedWriter (new OutputStreamWriter (new FileOutputStream(file)));
			bw.write (appelement.asXML());
			bw.close();
		}catch(Exception e){
			e.printStackTrace();
		} 
		
		
	}

	//修改应用版本信息
	private static void updateAppVersion(Element appelement, String file) {
		try{
			SAXReader reader = new SAXReader();
			Document document = reader.read(file);
			Element sourceApp=document.getRootElement();
			Attribute attr = sourceApp.attribute("version");
			attr.setValue(appelement.attributeValue("version"));
			 
			 //根据是否有<platform>标签判断是否修改平台信息
			 Element platformele=appelement.element("platform");
			 if(platformele!=null){
				 //修改平台信息
				 Element sourceP=sourceApp.element("platform");
				 sourceP.attribute("name").setValue(platformele.attributeValue("name"));
				 sourceP.attribute("version").setValue(platformele.attributeValue("version"));
			 }
			 
			 OutputFormat format = new OutputFormat("    ",true);
			  format.setEncoding("utf-8");//设置编码格式  
	       XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(file),format);
		    xmlWriter.write(document);
		    xmlWriter.close();
			 
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}

	private static void updateProductVersion(Element product) {
		try{
			String file="D:\test\boss_home\nontax_version.xml";
			SAXReader reader = new SAXReader();
			Document document = reader.read(file);
	       Node n= document.selectSingleNode("product/version");
	       n.setText(product.attributeValue("version"));
	       
	       OutputFormat format = new OutputFormat("    ",true);
			  format.setEncoding("utf-8");//设置编码格式  
	       XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(file),format);
		    xmlWriter.write(document);
		    xmlWriter.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	

}
