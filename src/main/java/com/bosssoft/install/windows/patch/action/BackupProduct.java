package com.bosssoft.install.windows.patch.action;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.core.util.ExpressionParser;
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
			throw new InstallException("faild to Backup Product"+e);
		}
		
	}

	private void createRollBackFile(String buDir, IContext context, Map params) {

		List<PatchApp> list=(List<PatchApp>) context.getValue("PATCH_APPS");
		List<IType> typeList=new ArrayList<IType>();
		for (PatchApp patchApp : list) {
			typeList.addAll(patchApp.getPatchFiles());
		}
		
		try{
			SAXReader reader=new SAXReader();
			Document doc=DocumentHelper.createDocument();
			Element root=DocumentHelper.createElement("rollback");
			doc.setRootElement(root);
			
			Element replaceEle=DocumentHelper.createElement("replace");
			//根据补丁资源配置备份信息
			addPatchResource(replaceEle,typeList,buDir);
			
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

	private void addPatchResource(Element replaceEle, List<IType> typeList, String buDir) {
		for (IType iType : typeList) {
			if(iType instanceof WarType){
				WarType war=(WarType) iType;
				if(war.getIsInstalled()){//旧的war升级，回滚时替换
				  Element dire=DocumentHelper.createElement("dir");
				  dire.addAttribute("dest", war.getDestPath()+File.separator+war.getAppName());
				  dire.addAttribute("source", buDir+File.separator+war.getAppName());
				  replaceEle.add(dire);
				}
			}else if(iType instanceof ResourceType){
				ResourceType resource=(ResourceType) iType;
				if(resource.getIsInstalled()){//旧文件升级，回滚时替换
					Element fe=DocumentHelper.createElement("file");
					String resourceName=new File(resource.getSourcePath()).getName();
					fe.addAttribute("dest", resource.getDestPath());
					fe.addAttribute("source",buDir+File.separator+resource.getAppName()+File.separator+resourceName);
					replaceEle.add(fe);
				}
			}
		}
		
	}

	private void doBackup(Element element) {
		try {
		File sourceFile=new File(element.attributeValue("dest"));
		File destFile=new File(element.attributeValue("source"));
		
		if(destFile.exists()) FileUtils.delete(destFile, null, null);
		
		FileUtils.copy(sourceFile, destFile, null, null);
		
	  } catch (Exception e) {
			throw new InstallException("faild to Backup Product"+e);
		}
	}

	public void rollback(IContext context, Map params) throws InstallException {
		
	}

}
