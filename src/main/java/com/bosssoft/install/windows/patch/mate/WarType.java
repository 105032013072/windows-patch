package com.bosssoft.install.windows.patch.mate;

import java.io.File;
import java.io.IOException;

import javax.swing.plaf.PanelUI;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.net.SyslogAppender;

import com.bosssoft.install.windows.patch.util.PatchFileManager;
import com.bosssoft.install.windows.patch.util.PatchUtil;
import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.util.PathUtil;
import com.bosssoft.platform.installer.io.FileUtils;
import com.bosssoft.platform.installer.io.operation.exception.OperationException;

public class WarType implements IType{
	private String name;
	private String sourcePath;
	private String destPath;
	 private Boolean isInstalled;

	public void update(IContext context) {
		if(isInstalled)
			copyToSvr(context);
		else deploy(context);//部署新的war
		
	}
	private void deploy(IContext context) {
		copyToSvr(context);//将war解压到应用服务器下
		copyToBossHome(context);//配置文件拷贝到BossHome下
		configNginx(context);//nginx配置转发
	    editUninstall(context);//修改uninstall.xml
	}
	
	private void editUninstall(IContext context) {
		String sourceFile=context.getStringValue("PRODUCT_INSTALL_DIR")+File.separator+"uninstall.bat";
		String sourceContext=PatchUtil.readFile(sourceFile,"GBK");
		int index=sourceContext.indexOf("rd");
		StringBuffer add=new StringBuffer();
		//删除应用服务器下
		add.append("rd /s /q ")
		    .append(context.getStringValue("APP_DEPLOY_DIR"))
		    .append(File.separator)
		    .append(name.substring(0,name.indexOf(".war")))
		    .append(System.lineSeparator());
		//删除boss_home下的配置文件
		add.append("rd /s /q ")
		   .append(context.getStringValue("BOSSSOFT_HOME"))
		   .append(File.separator)
		   .append(name.substring(0,name.indexOf(".war")))
		   .append(System.lineSeparator());
		
		String result=new StringBuffer(sourceContext).insert(index, add).toString();
		
		PatchUtil.writeToFile(result, sourceFile,"GBK");
	}
	private void configNginx(IContext context) {
		String sourceFile=context.getStringValue("PRODUCT_INSTALL_DIR")+File.separator+"nginx-1.13.0"+File.separator+"conf"+File.separator+"nginx.conf";
	    String sourceContent=PatchUtil.readFile(sourceFile);
	    String tempPath=PatchFileManager.getPacthTemplateDir()+File.separator+"nginx_template.txt";
	    String tempContext=PatchUtil.readFile(tempPath);
	    int index=sourceContent.indexOf("location",sourceContent.indexOf("server"));
	    String port=context.getStringValue("SERVER_PORT");
	    String tc=tempContext.replace("%app_name%", name.substring(0,name.indexOf(".war")))
	              .replace("%ip%","127.0.0.1")
	              .replace("%port%", port)+System.lineSeparator();
	    String result=(new StringBuffer(sourceContent).insert(index, tc)).toString();
	    PatchUtil.writeToFile(result, sourceFile);
	    
	    
	}
	private void copyToBossHome(IContext context) {
		String appName=name.substring(0,name.indexOf(".war"));
		String homePath=context.getStringValue("BOSSSOFT_HOME")+File.separator+appName+File.separator+"conf";
		File homedir=new File(homePath);
		if(!homedir.exists())
			homedir.mkdirs();
		String copyFiledir=context.getStringValue("APP_DEPLOY_DIR")+File.separator+appName+File.separator+"WEB-INF"+File.separator+"classes";
		//将配置文件拷贝到bosssoft_home目录下
		File[] files = new File(copyFiledir).listFiles();
		for (File file : files) {
			try {
				if(!file.isDirectory()&&file.getName().endsWith(".properties")){
	                String fileName=file.getName();
					String dest=fileName.substring(fileName.lastIndexOf("/")+1,fileName.length());
					FileUtils.copy(file, new File(homedir+File.separator+dest), null, null);
				}
				
			} catch (OperationException e) {
			 throw new InstallException("faild copy property files to BOSSSOFT_HOME"+e);
			}
		}
	}
	/**
	 * war包拷贝到应用服务器下（若已存在先删除）
	 * @param context
	 */
	private void copyToSvr(IContext context) {
		String appName=name.substring(0,name.indexOf(".war"));
		File destAppDir = new File(destPath, appName);
		File sourceFile=new File(sourcePath);
		if(destAppDir.exists()){
			try {
				FileUtils.delete(destAppDir, null, null);
			} catch (OperationException e) {
				throw new InstallException(e.getMessage());
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
	public Boolean getIsInstalled() {
		return isInstalled;
	}
	public void setIsInstalled(Boolean isInstalled) {
		this.isInstalled = isInstalled;
	}

	
    
	
	
}
