package com.bosssoft.install.windows.patch.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.bosssoft.install.windows.patch.util.LogUtil;
import com.bosssoft.install.windows.patch.util.PatchUtil;
import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.core.util.InstallerFileManager;
import com.bosssoft.platform.installer.io.FileUtils;

public class ReloadLog implements IAction{

	transient Logger logger = Logger.getLogger(getClass());
	public void execute(IContext context, Map params) throws InstallException {
		String bosshome=context.getStringValue("BOSSSOFT_HOME");
		String timeSuffix=new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		String logFile=bosshome+File.separator+"patch_log"+File.separator+"app_"+timeSuffix+".log";
	    configLog4j(logFile);//重新加载日志
	    carryLog(context,logFile);//将之前的日志搬到新的日志文件中
	    
	    context.setValue("INSTALL_LOGFILE_PATH",logFile);
		context.setValue("INSTALL_LOGFILE_NAME", logFile.substring(logFile.lastIndexOf("/") + 1));
	}

	private void carryLog(IContext context, String logFile) {
		String oldLog=context.getStringValue("INSTALL_LOGFILE_PATH");
		String logconContent=PatchUtil.readFile(oldLog);
		try{
			if(!new File(logFile).exists()) new File(logFile).createNewFile();
			PatchUtil.writeToFile(logconContent, logFile);
			//删除旧日志
			FileUtils.delete(new File(oldLog), null, null);
		}catch(Exception e){
			throw new InstallException(e);
		}
	}

	private void configLog4j(String logFile) {

		Properties properties = new Properties();
		String configPath = InstallerFileManager.getLoggerDir() + "/log4j.properties";
		try {
			properties.load(new FileInputStream(configPath));
		} catch (IOException e) {
			System.out.println("Cann't find file log4j.properties in path:" + configPath);
			e.printStackTrace();
			return;
		}
		properties.setProperty("log4j.appender.InstallFile.File", logFile);

		if (System.getProperty("install.debug", "false").equals("true")) {
			properties.setProperty("log4j.appender.InstallFile.Threshold", "DEBUG");
		}

		PropertyConfigurator.configure(properties);
	}

	public void rollback(IContext context, Map params) throws InstallException {
	
		
	}

}
