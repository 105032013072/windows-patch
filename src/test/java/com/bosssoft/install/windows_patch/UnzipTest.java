package com.bosssoft.install.windows_patch;

import java.io.File;

import org.apache.commons.lang.StringUtils;

import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.io.FileUtils;
import com.bosssoft.platform.installer.io.operation.exception.OperationException;

public class UnzipTest {
  public static void main(String[] args){
	  String appName="appframe-web";
		File destAppDir = new File("D:\\test\\apache-tomcat-7.0.76\\webapps", appName);
		File sourceFile=new File("D:\\test\\appframe-web.war");
		if(destAppDir.exists())
			try {
				FileUtils.delete(destAppDir, null, null);
			} catch (OperationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
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
  
}
