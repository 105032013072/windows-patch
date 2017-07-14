package com.bosssoft.install.windows.patch.action;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.bosssoft.install.windows.patch.util.PatchFileManager;
import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;

import java_cup.version;

public class LoadRollbackConfig implements IAction{

	transient Logger logger = Logger.getLogger(getClass());
	public void execute(IContext context, Map params) throws InstallException {
		String rollBackpath=PatchFileManager.getPatchRollBackFile();
		try {
			SAXReader reader=new SAXReader();
			Document doc=reader.read(new File(rollBackpath));
			Element root=doc.getRootElement();
			Element eleReplace=root.element("replace");
		    if(eleReplace!=null) {
		    	Map<String,String> map=getReplace(eleReplace,context);
		    	context.setValue("REPLACE_APPS_INFO", map);
		    }
		    
		    Element eleDelete=root.element("delete");
		    if(eleDelete!=null) {
		        String str=getDelete(eleDelete);
		        context.setValue("DELETE_APPS", str);
		    }
		    
		    String pov=getProductRollVersion(context);//获取回滚的产品版本信息
		    context.setValue("ROLL_PRODUCt_VERSION", pov);
		} catch (Exception e) {
			throw new InstallException("faild to load rollback config "+e);
		}
	}

	private String getProductRollVersion(IContext context) throws DocumentException {
		String productName=context.getStringValue("PRODUCT_NAME");
		String file=PatchFileManager.getPatchBackupDir(context, productName)+File.separator+"version"+File.separator+productName+"_info.xml";
		SAXReader reader=new SAXReader();
		Document doc=reader.read(file);
		
		return doc.getRootElement().elementText("version");
	}

	private String getDelete(Element eleDelete) {
		StringBuffer deleteApps=new StringBuffer("");
		Iterator<Element> it=eleDelete.elementIterator();
		while(it.hasNext()){
			Element e=it.next();
			if("app".equals(e.getQualifiedName())) deleteApps.append(e.attributeValue("appName")+",");
		}
		return deleteApps.toString();
	}

	private Map<String, String> getReplace(Element eleReplace, IContext context) throws DocumentException {
		Map<String,String> map=new HashMap<String, String>();
		Iterator<Element> it=eleReplace.elementIterator();
		//从备份的版本信息文件中获取应用即将回滚的版本号，存放在map中
		while(it.hasNext()){
			Element e=it.next();
			if("app".equals(e.getQualifiedName())){
				String appName=e.attributeValue("appName");
				String backUpVersionFile=PatchFileManager.getPatchBackupDir(context, appName)+"version.xml";
				SAXReader reader=new SAXReader();
				Document doc=reader.read(new File(backUpVersionFile));
				String oldversion=doc.getRootElement().attributeValue("version");
				map.put(appName, oldversion);
			} 
		}
		return map;
	}

	public void rollback(IContext context, Map params) throws InstallException {
		
		
	}

}
