package com.bosssoft.install.windows.patch.mate;

import java.util.ArrayList;
import java.util.List;

public class PatchApp {
   private String appName;
   
   private List<IType> patchFiles=new ArrayList<IType>();
   
   private String checkFileDir;

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
   
}
