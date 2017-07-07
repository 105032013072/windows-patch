package com.bosssoft.install.windows.patch.action;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;


import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.bosssoft.install.windows.patch.mate.PatchApp;
import com.bosssoft.install.windows.patch.util.PatchFileManager;
import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;

public class RecordUpdateLog implements IAction{
	transient Logger logger = Logger.getLogger(getClass());
	
	public void execute(IContext context, Map params) throws InstallException {
		List<PatchApp> list=(List<PatchApp>) context.getValue("PATCH_APPS");
		for (PatchApp patchApp : list) {
			String appName=patchApp.getAppName();
			String upgradePath=PatchFileManager.getPatchUpgradeFile(context, appName);
            try {
				record(appName,upgradePath, context);
			} catch (Exception e) {
				throw new InstallException(e);
			}
		}

	}

	private void record(String appName, String upgradePath,IContext context) throws Exception {
			File upgradeFile=new File(upgradePath);
			Document doc=null;
			if(upgradeFile.exists()){
				SAXReader reader=new SAXReader();
				doc=reader.read(upgradeFile);
			}else{
				doc=DocumentHelper.createDocument();
				Element root=DocumentHelper.createElement("upgrades");
         		doc.setRootElement(root);	
			}
			Element root=doc.getRootElement();
			Element upgrad=DocumentHelper.createElement("upgrade");
			upgrad.addElement("upgrade-time").addText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			upgrad.addElement("upgrade-version").addText(getVersion(appName,context));
		    upgrad.addElement("result").addText(getResult(context));
		    upgrad.addElement("logfile").addText(context.getStringValue("INSTALL_LOGFILE_PATH"));
	        root.add(upgrad);
	        
	        //写入文件
	        OutputFormat format =OutputFormat.createPrettyPrint(); 
			  format.setEncoding("utf-8");//设置编码格式 
			  format.setNewLineAfterDeclaration(false);
	       XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(upgradePath),format);
		    xmlWriter.write(doc);
		    xmlWriter.close();
	}

	//获取本次操作的执行结果
	private String getResult(IContext context) {
		
		if("true".equals(context.getStringValue("IS_ROLLBACK")))return "fail";
		else return "success";
	}

	//获取应用新的版本号
	private String getVersion(String appName, IContext context) throws DocumentException {
		String versionFile=context.getStringValue("BOSSSOFT_HOME")+File.separator+appName+File.separator+"version.xml";
		SAXReader reader=new SAXReader();
		Document doc=reader.read(versionFile);
		return doc.getRootElement().attributeValue("version");
	}

	public void rollback(IContext context, Map params) throws InstallException {
		
		
	}

}
