package com.bosssoft.install.windows.patch.mate;

import java.util.ArrayList;
import java.util.List;

public class PatchApp {
   private String appName;
   
   private List<IType> patchFiles=new ArrayList<IType>();
   
   private String checkFileDir;
   
   private ExitApp exitApp=new ExitApp();
   
   private Boolean isInstalled;
   
   private String serverDeployDir;
   
   private String serverPort;
   public void setExitAppInfo(String appVersion,String platformName,String platformVersion){
		exitApp.setAppversion(appVersion);
		exitApp.setPlatformName(platformName);
		exitApp.setPlatformVersion(platformVersion);
   }

public String getAppName() {
	return appName;
}

public void setAppName(String appName) {
	this.appName = appName;
}


   
public String getCheckFileDir() {
	return checkFileDir;
}

public void setCheckFileDir(String checkFileDir) {
	this.checkFileDir = checkFileDir;
}

public void addPatchFile(IType iType){
	patchFiles.add(iType);
}

public Boolean getIsInstalled() {
	return isInstalled;
}

public void setIsInstalled(Boolean isInstalled) {
	this.isInstalled = isInstalled;
}
  public String getAppversion(){
	 return getExitApp().getAppversion();
  }
  
  public String getPlatformName(){
	  return getExitApp().getPlatformName();
  }
  
  public String getPlatformVersion(){
	  return getExitApp().getPlatformVersion();
  }

public ExitApp getExitApp() {
	return exitApp;
}

public void setExitApp(ExitApp exitApp) {
	this.exitApp = exitApp;
}

public List<IType> getPatchFiles() {
	return patchFiles;
}

public String getServerDeployDir() {
	return serverDeployDir;
}

public void setServerDeployDir(String serverDeployDir) {
	this.serverDeployDir = serverDeployDir;
}

public String getServerPort() {
	return serverPort;
}

public void setServerPort(String serverPort) {
	this.serverPort = serverPort;
}
 


}
