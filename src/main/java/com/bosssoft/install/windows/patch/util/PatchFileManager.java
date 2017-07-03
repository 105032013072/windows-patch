package com.bosssoft.install.windows.patch.util;

import java.io.File;
import java.net.URISyntaxException;

public class PatchFileManager {
	private static String patchWordDir=setPatchWordDir();
	private static String patchHome = setpatchHomePath();
	private static String patchConfig=patchHome.concat("/resource_info.xml");
    private static String  pacthTemplateDir=null;
	private static  String pathResourceDir=null;
    private static String patchRollBackFile=null;
    private static String patchCheckFile=null;
	
	private static String setpatchHomePath() {
		String path=null;
		try {
			path=Thread.currentThread().getContextClassLoader().getResource("install").toURI().getPath();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return path;
	}
	
	private static String setPatchWordDir() {
		
		String path=null;
		try {
			path=Thread.currentThread().getContextClassLoader().getResource("").toURI().getPath();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return path;
	}

	public static String getPathResourceDir(String appName){
		return patchHome.concat(File.separator).concat(appName);
	}
	

	public static String getPatchConfig() {
		return patchConfig;
	}

	public static String getPacthTemplateDir() {
		return patchWordDir.concat(File.separator).concat("template");
	}

	public static String getPatchHome() {
		return patchHome;
	}

	public static String getPatchWordDir() {
		return patchWordDir;
	}

	public static String getPatchRollBackFile() {
		return patchWordDir.concat(File.separator).concat("rollback").concat(File.separator).concat("rollback.xml");
	}
	
	public static String getPatchCheckFile(){
		return patchHome.concat(File.separator).concat("depend.xml");
	}
	
}
