package com.bosssoft.install.windows.patch.mate;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.io.FileUtils;
import com.bosssoft.platform.installer.io.operation.exception.OperationException;

public class WarType implements IType{
	private String name;
	private String sourcePath;
	private String destPath;
	 private Boolean isInstalled;
	 private String appName;

	public void update(IContext context) {
		if(isInstalled)
			copyToSvr(context);
		else deploy(context);//部署新的war
		
	}
	private void deploy(IContext context) {
		copyToSvr(context);//将war解压到应用服务器下
		copyToBossHome(context);//配置文件拷贝到BossHome下
		configNginx(context);//nginx配置转发
	    initcong(context);//初始化配置文件
	    editUninstall(context);//修改uninstall.xml
	}
	
	private void editUninstall(IContext context) {
		
		
	}
	private void initcong(IContext context) {
		
		
	}
	private void configNginx(IContext context) {
		// TODO Auto-generated method stub
		
	}
	private void copyToBossHome(IContext context) {
		String homePath=context.getStringValue("BOSSSOFT_HOME")+File.separator+moduleDef.getNameKey()+File.separator+"conf";
		
	}
	/**
	 * war包拷贝到应用服务器下（若已存在会覆盖）
	 * @param context
	 */
	private void copyToSvr(IContext context) {
		String appName=name.substring(0,name.indexOf(".war"));
		File destAppDir = new File(destPath, appName);
		File sourceFile=new File(sourcePath);
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
