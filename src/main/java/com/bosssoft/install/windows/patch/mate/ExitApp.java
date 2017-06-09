package com.bosssoft.install.windows.patch.mate;
/**
 * 已安装的应用信息
 * @author Windows
 *
 */
public class ExitApp {
   private String appversion;
   private String platformName;
   private String platformVersion;

public String getAppversion() {
	return appversion;
}
public void setAppversion(String appversion) {
	this.appversion = appversion;
}

public String getPlatformName() {
	return platformName;
}
public void setPlatformName(String platformName) {
	this.platformName = platformName;
}
public String getPlatformVersion() {
	return platformVersion;
}
public void setPlatformVersion(String platformVersion) {
	this.platformVersion = platformVersion;
}
   
   
}
