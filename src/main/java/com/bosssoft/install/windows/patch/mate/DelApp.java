package com.bosssoft.install.windows.patch.mate;

public class DelApp {
  
	private String appName;
	private String appPath;
	
	
	public DelApp(String appName, String appPath) {
		this.appName = appName;
		this.appPath = appPath;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getAppPath() {
		return appPath;
	}
	public void setAppPath(String appPath) {
		this.appPath = appPath;
	}
	
	
}
