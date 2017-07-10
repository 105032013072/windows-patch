package com.bosssoft.install.windows.patch.mate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.plaf.PanelUI;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.net.SyslogAppender;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.bosssoft.install.windows.patch.util.PatchFileManager;
import com.bosssoft.install.windows.patch.util.PatchUtil;
import com.bosssoft.install.windows.patch.util.Recorder;
import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.util.PathUtil;
import com.bosssoft.platform.installer.io.FileUtils;
import com.bosssoft.platform.installer.io.operation.exception.OperationException;

public class WarType implements IType{
	
	private String name;
	private String sourcePath;
	private String destPath;
	 transient Logger logger = Logger.getLogger(getClass());

	public void update(IContext context,PatchApp patchApp) throws InstallException{
		String appName=patchApp.getAppName();
		String serverDeployDir=patchApp.getServerDeployDir();
		String serverPort=patchApp.getServerPort();
		
		if(patchApp.getIsInstalled())
			copyToSvr(context,appName);
		else deploy(context,appName,serverDeployDir,serverPort);//部署新的war	
	
	}
	private void deploy(IContext context, String appName, String serverDeployDir, String serverPort) {
		copyToSvr(context,appName);//将war解压到应用服务器下
		copyToBossHome(context,serverDeployDir,appName);//配置文件拷贝到BossHome下
		addToProductInfo(context,appName,serverDeployDir,serverPort);//产品的信息文件中增加新增应用的信息
		configNginx(context,appName,serverPort);//nginx配置转发
		editeUninstall(context,appName,serverDeployDir);//修改uninstall.bat或者uninstall.sh
	}
	
	private void addToProductInfo(IContext context, String appName, String serverDeployDir, String serverPort) {
	  try {
		  String productInfoPath=PatchFileManager.getPatchProdcutInfoFile(context);
		  SAXReader reader=new SAXReader();
		  Document doc=reader.read(new File(productInfoPath));
		  Element applications=doc.getRootElement().element("applications");
		  Element app=DocumentHelper.createElement("application");
		  app.addElement("appName").addText(appName);
		  app.addElement("deployDir").addText(serverDeployDir);
		  app.addElement("serverPort").addText(serverPort);
		  applications.add(app);
		  
		  OutputFormat format =OutputFormat.createPrettyPrint(); 
		  format.setEncoding("utf-8");//设置编码格式  
		  format.setNewLineAfterDeclaration(false);
	      XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(productInfoPath),format);

	     xmlWriter.write(doc);
	     xmlWriter.close();
	} catch (Exception e) {
	   throw new InstallException("faild to update "+PatchFileManager.getPatchProdcutInfoFile(context)+e);
	}
		
		
	}
	private void editeUninstall(IContext context, String appName, String serverDeployDir) {
		if("true".equals(context.getStringValue("IS_WINDOWS"))) editeBat(context,appName,serverDeployDir);
		else editeSh(context,appName,serverDeployDir);
	}
	private void editeSh(IContext context, String appName, String serverDeployDir) {
		String sourceFile=context.getStringValue("PRODUCT_INSTALL_DIR")+File.separator+"uninstall.sh";
		StringBuffer add=new StringBuffer(System.lineSeparator());
		//删除应用服务器下
		add.append("rm -rf ")
		    .append(serverDeployDir)
		    .append(File.separator)
		    .append(appName)
		    .append(System.lineSeparator());
		//删除boss_home下的配置文件
		add.append("rm -rf ")
		   .append(context.getStringValue("BOSSSOFT_HOME"))
		   .append(File.separator)
		   .append(appName)
		   .append(System.lineSeparator());	
		PatchUtil.wirteAppendFile(add.toString(), sourceFile);
		
	    logger.debug("Update Product: modify "+sourceFile);
	}
	private void editeBat(IContext context, String appName, String serverDeployDir) {
		String sourceFile=context.getStringValue("PRODUCT_INSTALL_DIR")+File.separator+"uninstall.bat";
		String sourceContext=PatchUtil.readFile(sourceFile,"GBK");
		int index=sourceContext.indexOf("rd");
		StringBuffer add=new StringBuffer();
		//删除应用服务器下
		add.append("rd /s /q ")
		    .append(serverDeployDir)
		    .append(File.separator)
		    .append(appName)
		    .append(System.lineSeparator());
		//删除boss_home下的配置文件
		add.append("rd /s /q ")
		   .append(context.getStringValue("BOSSSOFT_HOME"))
		   .append(File.separator)
		   .append(appName)
		   .append(System.lineSeparator());
		
		String result=new StringBuffer(sourceContext).insert(index, add).toString();
		
		PatchUtil.writeToFile(result, sourceFile,"GBK");
		
		logger.debug("Update Product: modify "+sourceFile);
		
		
	}
	private void configNginx(IContext context, String appName, String serverPort) {
		String sourceFile=context.getStringValue("PRODUCT_INSTALL_DIR")+File.separator+"nginx-1.13.0"+File.separator+"conf"+File.separator+"nginx.conf";
	    String sourceContent=PatchUtil.readFile(sourceFile);
	    String tempPath=PatchFileManager.getPacthTemplateDir()+File.separator+"nginx_template.txt";
	    String tempContext=PatchUtil.readFile(tempPath);
	    int index=sourceContent.indexOf("location",sourceContent.indexOf("server"));
	    String tc=tempContext.replace("%app_name%", appName)
	              .replace("%ip%","127.0.0.1")
	              .replace("%port%", serverPort)+System.lineSeparator();
	    String result=(new StringBuffer(sourceContent).insert(index, tc)).toString();
	    PatchUtil.writeToFile(result, sourceFile);
	    
	    logger.debug("Update Product: modify "+sourceFile);
	}
	private void copyToBossHome(IContext context, String serverDeployDir, String appName) {
		String homePath=context.getStringValue("BOSSSOFT_HOME")+File.separator+appName+File.separator+"conf";
		File homedir=new File(homePath);
		if(!homedir.exists())
			homedir.mkdirs();
		String copyFiledir=serverDeployDir+File.separator+appName+File.separator+"WEB-INF"+File.separator+"classes";
		//将配置文件拷贝到bosssoft_home目录下
		File[] files = new File(copyFiledir).listFiles();
		for (File file : files) {
			try {
				if(!file.isDirectory()&&file.getName().endsWith(".properties")){
	                String fileName=file.getName();
					String dest=fileName.substring(fileName.lastIndexOf("/")+1,fileName.length());
					File targetFile=new File(homedir+File.separator+dest);
					FileUtils.copy(file, targetFile, null, null);
					
					logger.debug("Update Product: copy "+file.getPath()+" to "+targetFile.getPath());
				}
				
			} catch (OperationException e) {
			 throw new InstallException("faild copy property files to BOSSSOFT_HOME"+e);
			}
		}
	}
	/**
	 * war包拷贝到应用服务器下（若已存在先删除）
	 * @param context
	 * @param appName 
	 */
	private void copyToSvr(IContext context, String appName) {
		File destAppDir = new File(destPath, appName);
		File sourceFile=new File(sourcePath);
		if(destAppDir.exists()){
			try {
				FileUtils.delete(destAppDir, null, null);
			} catch (OperationException e) {
				throw new InstallException(e);
			}
		}
		
		try {
			FileUtils.unzip(sourceFile, destAppDir, null, null);
		} catch (OperationException e) {
			String message = e.getMessage();
			if ((StringUtils.isEmpty(message)) && (e.getCause() != null)) {
				message = e.getCause().getMessage();
			}
			throw new InstallException(message);
		}
		logger.debug("Update Product:unzip "+sourceFile.getPath()+" to "+destPath);
		
	}
	
   public void record4Rollback(IContext context,PatchApp patchApp) throws InstallException{
		//新增的应用需要记录回滚时要删除的文件
	   if(!patchApp.getIsInstalled()){
		   String appName=patchApp.getAppName();
			//记录删除应用服务器下的程序
		   String path=destPath+File.separator+appName;
		   Recorder.rollbackDeleteApp(path, appName);
		   
		   //记录删除bosshome下的配置文件
		   path=context.getStringValue("BOSSSOFT_HOME")+File.separator+appName+File.separator+"conf";
		   Recorder.rollbackDeleteDir(path);
		 
		   //记录删除bosshome下的版本信息文件
		   path=context.getStringValue("BOSSSOFT_HOME")+File.separator+appName+File.separator+"version.xml";
		   Recorder.rollbackDeleteDir(path);
	   }
		
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public String getDestPath() {
		return destPath;
	}

	public void setDestPath(String destPath) {
		this.destPath = destPath;
	}
    
	
    
	
	
}
