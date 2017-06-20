package com.bosssoft.install.windows_patch;

import java.io.File;

import com.bosssoft.platform.installer.io.FileUtils;

public class IOTest {

	public static void main(String[] args) {
		//String sourcePath="D:\\test\\apache-tomcat-7.0.76\\webapps\\appframe-web";
		String destPath="D:\\test\\backup\\appframe-web";
		try {
			if(new File(destPath).exists())
				FileUtils.delete(new File(destPath), null, null);
			//FileUtils.copy(new File(sourcePath), new File(destPath), null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
