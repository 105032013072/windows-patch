package com.bosssoft.install.windows.patch.util;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.bosssoft.install.windows.patch.mate.DelApp;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.util.I18nUtil;
import com.bosssoft.platform.installer.wizard.action.SetXMLNodeValue;

public class Recorder {
   private static List<String> delFiles=new ArrayList<String>();
   private static List<String> delDirs=new ArrayList<String>();
   private static List<DelApp> delApps=new ArrayList<DelApp>();

    public static void  rollbackDeleteFile(String filePath){
    	delFiles.add(filePath);
    }
    
    public static void rollbackDeleteDir(String dirPath){
    	delDirs.add(dirPath);
    }
    
    public static void rollbackDeleteApp(String appPath,String appName){
    	DelApp app=new DelApp(appName, appPath);
    	delApps.add(app);
    }
    public static void saveRollback() throws Exception{
    	    if(delDirs.size()==0&&delFiles.size()==0&&delApps.size()==0)return;
    	
    		String path=PatchFileManager.getPatchRollBackFile();
        	File file=new File(path);
        	 Document doc=null;
        	if(file.exists()){
               SAXReader reader=new SAXReader();
               doc=reader.read(file);
              
        	}else{
        		 doc=DocumentHelper.createDocument();
        		 Element root=DocumentHelper.createElement("rollback");
        		//记录备份时间
     			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
     			root.addAttribute("backupTime", df.format(new Date()));
         		doc.setRootElement(root);	
        	}
        	Element root=doc.getRootElement();
        	Element delet=DocumentHelper.createElement("delete");
        	
        	for (String dir : delDirs) {
				Element e=DocumentHelper.createElement("dir");
				e.addAttribute("path", dir);
				delet.add(e);
			}
        	for(String df:delFiles){
        		Element e=DocumentHelper.createElement("file");
				e.addAttribute("path", df);
				delet.add(e);
        	}
        	for (DelApp delApp : delApps) {
        		Element e=DocumentHelper.createElement("app");
        		e.addAttribute("path", delApp.getAppPath());
        		e.addAttribute("appName", delApp.getAppName());
				delet.add(e);
			}
        	root.add(delet);
        	
        	
        	if(!new File(path).exists()) PatchUtil.createFile(path);
        	OutputFormat format =OutputFormat.createPrettyPrint(); 
			  format.setEncoding("utf-8");//设置编码格式 
			  format.setNewLineAfterDeclaration(false);
	       XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(path),format);
		    xmlWriter.write(doc);
		    xmlWriter.close();
    	
    }
    
}
