package com.bosssoft.install.windows.patch.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

import com.bosssoft.platform.installer.core.util.InstallerFileManager;

public class LogUtil {
   public static void configLog4j(String logFile){
		Properties properties = new Properties();
		String configPath = InstallerFileManager.getLoggerDir() + "/log4j.properties";
		try {
			properties.load(new FileInputStream(configPath));
		} catch (IOException e) {
			System.out.println("Cann't find file log4j.properties in path:" + configPath);
			e.printStackTrace();
			return;
		}
		properties.setProperty("log4j.appender.R.File", logFile);

		if (System.getProperty("install.debug", "false").equals("true")) {
			properties.setProperty("log4j.appender.InstallFile.Threshold", "DEBUG");
		}

		PropertyConfigurator.configure(properties);
	}
   }
