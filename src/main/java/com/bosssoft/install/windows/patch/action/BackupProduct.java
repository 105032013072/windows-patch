package com.bosssoft.install.windows.patch.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.bosssoft.install.windows.patch.mate.IType;
import com.bosssoft.install.windows.patch.mate.PatchApp;
import com.bosssoft.install.windows.patch.mate.ResourceType;
import com.bosssoft.install.windows.patch.mate.WarType;
import com.bosssoft.install.windows.patch.util.PatchFileManager;
import com.bosssoft.install.windows.patch.util.PatchUtil;
import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.MainFrameController;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.core.util.ExpressionParser;
import com.bosssoft.platform.installer.core.util.I18nUtil;
import com.bosssoft.platform.installer.io.FileUtils;

public class BackupProduct implements IAction{
	transient Logger logger = Logger.getLogger(getClass());

	public void execute(IContext context, Map params) throws InstallException {
		String buDir=params.get("BACKUP_DIR").toString();
		createRollBackFile(buDir,context,params);//创建rollback.xml
		//读取rollback.xml，执行备份
		try{
			SAXReader reader = new SAXReader();
			Document document = reader.read(PatchFileManager.getPatchRollBackFile());
			Element replace=document.getRootElement().element("replace");
			Iterator<Element> it=replace.elementIterator();
			while(it.hasNext()){
				Element element=it.next();
				doBackup(element);
			}
		}catch(Exception e){
			logger.error(e);
			if("true".equals(context.getStringValue("IS_WINDOWS"))) op4Swing(e,context,params);
			else op4Silent(e,context,params);
		}
		
	}

	private void op4Silent(Exception e, IContext context, Map params) {

		Boolean flag=true;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in ));
		String read = null;
		
		System.out.print(I18nUtil.getString("BACKUP.ERROR.INFO")+"(Y/N)");
		while(flag){
			try {
				read=br.readLine();
				if("Y".equalsIgnoreCase(read)){
					logger.debug("backups: error and go on update");
					flag=false;
				}else if("N".equalsIgnoreCase(read)){
					System.exit(0);
				}else {
					System.out.print(I18nUtil.getString("CHOOSE.SILENT.ILLEGAL.INPUT"));
				}
				
			} catch (IOException e1) {
				throw new InstallException(e1);
			}
		}	
	}

	private void op4Swing(Exception e, IContext context, Map params) {
		int i=MainFrameController.showConfirmDialog(I18nUtil.getString("BACKUP.ERROR.INFO"), I18nUtil.getString("BACKUP.ERROR.TITLE"), JOptionPane.YES_NO_OPTION, 2);
		if(i==0){
			logger.debug("backups: error and go on update");
		}else System.exit(0);
	}

	private void createRollBackFile(String buDir, IContext context, Map params) {

		List<PatchApp> list=(List<PatchApp>) context.getValue("PATCH_APPS");
		
		try{
			SAXReader reader=new SAXReader();
			Document doc=DocumentHelper.createDocument();
			Element root=DocumentHelper.createElement("rollback");
			//记录备份时间
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			root.addAttribute("backupTime", df.format(new Date()));
			doc.setRootElement(root);
			
			Element replaceEle=DocumentHelper.createElement("replace");
			//根据补丁资源配置备份信息
			addPatchResource(replaceEle,list,buDir,context);
			
			//根据install.xml中的设置的额外备份文件配置备份信息
			addExtraFiles(replaceEle,params,buDir);
	        
			//备份信息中配置版本信息文件
			addVersionFiles(replaceEle,buDir,context);
			
			root.add(replaceEle);
			
			OutputFormat format =OutputFormat.createPrettyPrint(); 
			  format.setEncoding("utf-8");//设置编码格式 
			  format.setNewLineAfterDeclaration(false);
	       XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(PatchFileManager.getPatchRollBackFile()),format);
		    xmlWriter.write(doc);
		    xmlWriter.close();
		}catch(Exception e){
			throw new InstallException("faild to create rollback config file because "+e);
		}
		
	    
		
	}

	private void addVersionFiles(Element replaceEle, String buDir, IContext context) {
		//app版本信息
		List<PatchApp> list=(List<PatchApp>) context.getValue("PATCH_APPS");
		for (PatchApp patchApp : list) {
			if(patchApp.getIsInstalled()){
				String avp=patchApp.getAppName()+File.separator+"version.xml";
				Element e=DocumentHelper.createElement("file");
				e.addAttribute("dest", context.getStringValue("BOSSSOFT_HOME")+File.separator+avp);
				e.addAttribute("source", buDir+File.separator+"version"+File.separator+avp);
				replaceEle.add(e);
			}
		}
		
		//产品版本信息
		String pv=File.separator+context.getStringValue("PRODUCT_NAME")+"_version.xml";
		Element pe=DocumentHelper.createElement("file");
		pe.addAttribute("dest", context.getStringValue("BOSSSOFT_HOME")+File.separator+pv);
		pe.addAttribute("source", buDir+File.separator+"version"+File.separator+pv);
		replaceEle.add(pe);
	}

	private void addExtraFiles(Element replaceEle, Map params, String buDir) {
		Object para=params.get("EXTRA_FILES");
		if(para!=null){
			String[] extraFiles=para.toString().split(",");
			for (String file : extraFiles) {
				Element e=DocumentHelper.createElement("file");
				String str=file.substring(file.indexOf(File.separator),file.length());
				e.addAttribute("dest", file);
				e.addAttribute("source", buDir+File.separator+str);
				replaceEle.add(e);
			}
		}
		
	}

	private void addPatchResource(Element replaceEle, List<PatchApp> list, String buDir, IContext context) {
		
		for (PatchApp patchApp : list) {
			if(patchApp.getIsInstalled()){
				String dest=context.getStringValue("APP_DEPLOY_DIR")+File.separator+patchApp.getAppName();
				String source=buDir+File.separator+patchApp.getAppName();
				Element dire=DocumentHelper.createElement("app");
				dire.addAttribute("appName", patchApp.getAppName());
				dire.addAttribute("dest", dest);
			    dire.addAttribute("source", source);
				replaceEle.add(dire);
			}
		}
	}

	private void doBackup(Element element) {
		try {
		File sourceFile=new File(element.attributeValue("dest"));
		File destFile=new File(element.attributeValue("source"));
		
		if(destFile.exists()) FileUtils.delete(destFile, null, null);
		
		FileUtils.copy(sourceFile, destFile, null, null);
		
		logger.debug("backups: cpoy "+sourceFile+" to "+destFile);
		
	  } catch (Exception e) {
			throw new InstallException("faild to Backup Product"+e);
		}
	}

	public void rollback(IContext context, Map params) throws InstallException {
		
	}

}
