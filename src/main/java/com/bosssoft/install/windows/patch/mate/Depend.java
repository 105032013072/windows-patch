package com.bosssoft.install.windows.patch.mate;

public class Depend {
   private String type;
   private String name;
   private String[] versions;
   private String platformName;
   private String[] platformVersions;
   
   
   
public Depend(String type, String name, String versions, String platformName, String platformVersions) {
	this.type = type;
	this.name = name;
	this.versions = versions.split(",");
	this.platformName = platformName;
	this.platformVersions = platformVersions.split(",");
}


public Depend() {
}


public String getType() {
	return type;
}
public void setType(String type) {
	this.type = type;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String[] getVersions() {
	return versions;
}
public void setVersions(String[] versions) {
	this.versions = versions;
}
public String getPlatformName() {
	return platformName;
}
public void setPlatformName(String platformName) {
	this.platformName = platformName;
}
public String[] getPlatformVersions() {
	return platformVersions;
}
public void setPlatformVersions(String[] platformVersions) {
	this.platformVersions = platformVersions;
}
   
   
}
