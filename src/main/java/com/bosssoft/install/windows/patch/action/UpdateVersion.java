package com.bosssoft.install.windows.patch.action;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.bosssoft.install.windows.patch.util.PatchFileManager;
import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;

public class UpdateVersion implements IAction{
	transient Logger logger = Logger.getLogger(getClass());

	public void execute(IContext context, Map params) throws InstallException {
		String patchVersion=PatchFileManager.getPatchHome()+File.separator+"version.xml";
		try{
			SAXReader reader = new SAXReader();
			Document document = reader.read(patchVersion);
			Element product= document.getRootElement();
			
			if(product.attribute("version")!=null){//更新产品版本
				updateProductVersion(product,context);
			}
			//更新或者创建应用版本信息
		Iterator<Element> it=product.elementIterator();
		while(it.hasNext()){
			Element appelement=it.next();
			setAppVersion(appelement,context);
		}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void setAppVersion(Element appelement, IContext context) {
		String file=context.getStringValue("BOSSSOFT_HOME")+File.separator+appelement.attributeValue("name")+File.separator+"version.xml";
		if(new File(file).exists()) updateAppVersion(appelement,file);
		else createAppVersion(appelement,file);
		
	}

	//创建新应用的版本信息文件
	private void createAppVersion(Element appelement, String file) {
		try{
			BufferedWriter bw = new BufferedWriter (new OutputStreamWriter (new FileOutputStream(file)));
			bw.write (appelement.asXML());
			bw.close();
		}catch(Exception e){
			e.printStackTrace();
		} 
		
	}

	//修改应用版本信息
	private void updateAppVersion(Element appelement, String file) {
		try{
			SAXReader reader = new SAXReader();
			Document document = reader.read(file);
			Element sourceApp=document.getRootElement();
			if(appelement.attributeValue("version")!=null){//需要更新应用版本信息
				Attribute attr = sourceApp.attribute("version");
				attr.setValue(appelement.attributeValue("version"));
			}
			 
			 //根据是否有<platform>标签判断是否修改平台信息
			 Element platformele=appelement.element("platform");
			 if(platformele!=null){
				 //修改平台信息
				 Element sourceP=sourceApp.element("platform");
				 sourceP.attribute("name").setValue(platformele.attributeValue("name"));
				 sourceP.attribute("version").setValue(platformele.attributeValue("version"));
			 }
			 
			 OutputFormat format =OutputFormat.createPrettyPrint(); 
			  format.setEncoding("utf-8");//设置编码格式 
			  format.setNewLineAfterDeclaration(false);//去掉第二行的空行
	       XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(file),format);
		    xmlWriter.write(document);
		    xmlWriter.close();
			 
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	private void updateProductVersion(Element product, IContext context) {

		try{
			String file=context.getStringValue("BOSSSOFT_HOME")+File.separator+context.getStringValue("PRODUCT_NAME")+"_version.xml";
			SAXReader reader = new SAXReader();
			Document document = reader.read(file);
	       Node n= document.selectSingleNode("product/version");
	       n.setText(product.attributeValue("version"));
	       
	       OutputFormat format =OutputFormat.createPrettyPrint(); 
			  format.setEncoding("utf-8");//设置编码格式 
			  format.setNewLineAfterDeclaration(false);
	       XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(file),format);
		    xmlWriter.write(document);
		    xmlWriter.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	
		
	}

	public void rollback(IContext context, Map params) throws InstallException {
		
		
	}
}
