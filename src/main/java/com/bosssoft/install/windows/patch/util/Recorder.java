package com.bosssoft.install.windows.patch.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.util.I18nUtil;
import com.bosssoft.platform.installer.wizard.action.SetXMLNodeValue;

public class Recorder {
   private static List<String> delFiles=new ArrayList<String>();
   private static List<String> delDirs=new ArrayList<String>();
	
    private static StringBuffer logbuffer=new StringBuffer("");
    
    public static void createFileLog(String filePath){
    	String message=I18nUtil.getString("LOGGER_CREATE_FILE")+filePath;
    	logbuffer.append(message);
    	logbuffer.append(System.lineSeparator());
    }
    
    public static void editeFileLog(String filePath){
    	String message=I18nUtil.getString("LOGGER_EDITE_FILE")+filePath;
    	logbuffer.append(message);
    	logbuffer.append(System.lineSeparator());
    }
    
    public static void unzipLog(String sourcePath,String destPath){
    	String message=sourcePath+I18nUtil.getString("LOGGER_UNZIP")+destPath;
    	logbuffer.append(message);
    	logbuffer.append(System.lineSeparator());
    }
    
    public static void copyFileLog(String sourcePath,String destPath){
    	String message=sourcePath+I18nUtil.getString("LOGGER_COPY")+destPath;
    	logbuffer.append(message);
    	logbuffer.append(System.lineSeparator());
    }
    public static void  rollbackDeleteFile(String filePath){
    	delFiles.add(filePath);
    }
    
    public static void rollbackDeleteDir(String dirPath){
    	delDirs.add(dirPath);
    }
    
    public static void saveRollback(){
    	try{
    		String path=PatchFileManager.getPatchRollBackFile();
        	File file=new File(path);
        	 Document doc=null;
        	if(file.exists()){
               SAXReader reader=new SAXReader();
               doc=reader.read(file);
              
        	}else{
        		file.createNewFile();
        		SAXReader reader=new SAXReader();
        		doc=reader.read(file);
        		doc.add(DocumentHelper.createElement("rollback"));
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
        	root.add(delet);
        	
        	OutputFormat format =OutputFormat.createPrettyPrint(); 
			  format.setEncoding("utf-8");//设置编码格式 
			  format.setNewLineAfterDeclaration(false);
	       XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(path),format);
		    xmlWriter.write(doc);
		    xmlWriter.close();
    	}catch(Exception e){
    		throw new InstallException("faild to save roll back because "+e);
    	} 
    	
    }
    
    public static String getPatchLog(){
    	return logbuffer.toString();
    }
    
}
