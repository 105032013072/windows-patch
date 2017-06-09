package com.bosssoft.install.windows.patch.util;

import java.io.File;
import java.net.URISyntaxException;

public class PatchFileManager {
	private static String patchHome = getpatchHomePath();
	private static String patchConfig=patchHome.concat("/resource_info.xml");
    private static  String pathResourceDir=null;
    private static  String pathCheckDir=null;
	
	private static String getpatchHomePath() {
		String path=null;
		try {
			path=Thread.currentThread().getContextClassLoader().getResource("install").toURI().getPath();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return path;
	}
	
	public static String getPathResourceDir(String appName){
		return patchHome.concat(File.separator).concat(appName);
	}
	
	public static String getPathCheckDir(String appName){
		return patchHome.concat(File.separator).concat(appName).concat(File.separator).concat("check");
	}

	public static String getPatchConfig() {
		return patchConfig;
	}
}
