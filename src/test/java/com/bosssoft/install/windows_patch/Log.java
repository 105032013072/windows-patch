package com.bosssoft.install.windows_patch;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.bosssoft.install.windows.patch.util.PatchFileManager;
import com.bosssoft.platform.installer.core.util.InstallerFileManager;

public class Log {
	 Logger logger = Logger.getLogger(getClass());
	 
	 
	 public void runTest(){
		 configlog();
		 logger.info("加载1");
		 
		//设置log4j的相对路径 
			String workDir=PatchFileManager.getPatchWordDir()+"logging";
			System.setProperty ("WORKDIR", workDir);
		 configlog();
		 logger.info("加载2");
	 }
	 
	 
	 public  String configlog(){
		  String logPath = null;
			String userHome = System.getProperty("user.home");
			String logFileName = System.getProperty("install.logfile");
			if ((logFileName == null) || (logFileName.trim().length() == 0))
				logFileName = "app.log";
			userHome = userHome.replace('\\', '/');
			File logfile = new File(userHome + "/" + logFileName);
			if (logfile.exists())
				logfile.delete();

			Properties properties = new Properties();
			String configPath = InstallerFileManager.getLoggerDir() + "/log4j.properties";
			try {
				properties.load(new FileInputStream(configPath));
			} catch (IOException e) {
				System.out.println("Cann't find file log4j.properties in path:" + configPath);
				e.printStackTrace();
				return null;
			}

			logPath = userHome + "/" + logFileName;
			properties.setProperty("log4j.appender.InstallFile.File", logPath);

			if (System.getProperty("install.debug", "false").equals("true")) {
				properties.setProperty("log4j.appender.InstallFile.Threshold", "DEBUG");
			}

			PropertyConfigurator.configure(properties);
			return logPath;
	  }
	 
}
