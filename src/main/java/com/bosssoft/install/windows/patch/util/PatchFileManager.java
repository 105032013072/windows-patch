package com.bosssoft.install.windows.patch.util;

import java.io.File;
import java.net.URISyntaxException;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.util.InstallerFileManager;

public class PatchFileManager {
	private static String patchWordDir=setPatchWordDir();
	private static String patchHome = setpatchHomePath();
	private static String patchConfig=patchHome.concat("/resource_info.xml");
    private static String  pacthTemplateDir=patchWordDir.concat(File.separator).concat("template");
	
    //应用的补丁资源所在目录
    private static  String pathResourceDir=null;
   
    private static String patchRollBackFile=patchWordDir.concat(File.separator).concat("rollback").concat(File.separator).concat("rollback.xml");
    
    //更新依赖的配置文件
    private static String patchCheckFile=patchHome.concat(File.separator).concat("depend.xml");
	
    //应用备份目录
    private static String patchBackupDir=null;
    private static String PatchPublicBackupDir=null;
    
    //应用的升级记录文件
    private static String patchUpgradeFile=null;
    
    private static String patchProdcutVersionFile=null;
    
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
	
    
	
	public static String getPatchBackupDir(IContext context,String appName) {
		String buDir=context.getStringValue("BOSSSOFT_HOME");
		String backupDir=buDir+File.separator
				+appName+File.separator
				+"patchs"+File.separator
				+"backup"+File.separator;
		return backupDir;
	}


	public static String getPatchUpgradeFile(IContext context,String appName) {
		String rootDir=context.getStringValue("BOSSSOFT_HOME");
		String filePath=rootDir+File.separator
				+appName+File.separator
				+"patchs"+File.separator
				+"upgrade"+File.separator
				+"upgrade-log.xml";
		return filePath;
	}

	
	
	public static String getPatchProdcutVersionFile(IContext context) {
		String file=context.getStringValue("BOSSSOFT_HOME")+File.separator+context.getStringValue("PRODUCT_NAME")+"_info.xml";
	    return file;
	}

	public static String getPatchConfig() {
		return patchConfig;
	}

	public static String getPacthTemplateDir() {
		return pacthTemplateDir;
	}

	public static String getPatchHome() {
		return patchHome;
	}

	public static String getPatchWordDir() {
		return patchWordDir;
	}

	public static String getPatchRollBackFile() {
		return patchRollBackFile;
	}
	
	public static String getPatchCheckFile(){
		return patchCheckFile;
	}
	
}
