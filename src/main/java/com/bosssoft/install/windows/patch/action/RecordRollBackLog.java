package com.bosssoft.install.windows.patch.action;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.bosssoft.install.windows.patch.util.PatchFileManager;
import com.bosssoft.install.windows.patch.util.PatchUtil;
import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;

import oracle.net.aso.i;

public class RecordRollBackLog implements IAction{

	transient Logger logger = Logger.getLogger(getClass());
	public void execute(IContext context, Map params) throws InstallException {
		try {
			Map<String,String> map=(Map<String, String>) context.getValue("REPLACE_APPS_INFO");
			Iterator iter=map.entrySet().iterator();
		    while(iter.hasNext()){
		    	Map.Entry<String,String> entry=(Entry) iter.next();
		        String appName=entry.getKey();
		        String oldVersion=entry.getValue();
		        record(appName,oldVersion,context,params);
		    }
		    
		    String []deleteApps=context.getStringValue("DELETE_APPS").split(",");
		    for (String appName : deleteApps) {
				record(appName, "0.0.0", context,params);//卸载的版本对应为0.0.0
			}
		    
		} catch (Exception e) {
			throw new InstallException("faild to record rollback log "+e);
		}
		
	}

	private void record(String appName, String oldVersion, IContext context, Map params) throws Exception {
		String upgradePath=PatchFileManager.getPatchUpgradeFile(context, appName);
		File upgradeFile=new File(upgradePath);
		Document doc=null;
		if(upgradeFile.exists()){
			SAXReader reader=new SAXReader();
			doc=reader.read(upgradeFile);
		}else{
			PatchUtil.createFile(upgradePath);
			doc=DocumentHelper.createDocument();
			Element root=DocumentHelper.createElement("upgrades");
     		doc.setRootElement(root);	
		}
		Element root=doc.getRootElement();
		Element ele=createRollbackEle(appName,oldVersion,context,params);
		root.add(ele);
		
		 //写入文件
        OutputFormat format =OutputFormat.createPrettyPrint(); 
		  format.setEncoding("utf-8");//设置编码格式 
		  format.setNewLineAfterDeclaration(false);
       XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(upgradePath),format);
	    xmlWriter.write(doc);
	    xmlWriter.close();
	}

	private Element createRollbackEle(String appName, String oldVersion, IContext context, Map params) {
		
		Element rollback=DocumentHelper.createElement("rollback");
		rollback.addElement("rollback-time").addText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		rollback.addElement("rollback-version").addText(oldVersion);
		rollback.addElement("result").addText(getResult(params));
		rollback.addElement("logfile").addText(context.getStringValue("INSTALL_LOGFILE_PATH"));
		return rollback;
	}

	private String getResult(Map params) {
		String result=null;
		if("true".equals(params.get("IS_ROLLBACK_SUCCESS").toString())){
			result="success";
		}
		else result="faild";
		return result;
	}

	public void rollback(IContext context, Map params) throws InstallException {
		// TODO Auto-generated method stub
		
	}

}
