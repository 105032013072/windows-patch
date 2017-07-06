package com.bosssoft.install.windows.patch.action;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
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

import com.bosssoft.install.windows.patch.util.ExceptionProcessor;
import com.bosssoft.install.windows.patch.util.PatchFileManager;
import com.bosssoft.install.windows.patch.util.Recorder;
import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;

public class UpdateVersion implements IAction{
	transient Logger logger = Logger.getLogger(getClass());

	public void execute(IContext context, Map params) throws InstallException {
		if("true".equals(context.getStringValue("IS_ROLLBACK")))return;
		
		logger.info("update version.....");
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
			logger.error(e);
			ExceptionProcessor processor=new ExceptionProcessor();
			processor.doHandle(e, context, params);
		}
	}

	private void setAppVersion(Element appelement, IContext context) throws Exception{
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

			 Recorder.createFileLog(file);
			 logger.debug("Update Product: create  new app version file "+file);
		}catch(Exception e){
			throw new InstallException("faild to create app version file because "+e);
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
		
		Recorder.editeFileLog(file);
		logger.debug("Update Product: update the version infomation of app "+file);

		}catch(Exception e){
			throw new InstallException("faild to update App Version because "+e);
		}
			
	}

	private void updateProductVersion(Element product, IContext context) {
           try {
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
   		
   		    Recorder.editeFileLog(file);
   		    logger.debug("Update Product: update the version infomation of product "+file);
		} catch (Exception e) {
			throw new InstallException("faild to update Product version because "+e);
		}
			
		}

	public void rollback(IContext context, Map params) throws InstallException {
		
	}
}
