package com.bosssoft.install.windows.patch.mate;

public class WarType implements IType{
	private String name;
	private String sourcePath;
	private String destPath;

	public void update() {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public String getDestPath() {
		return destPath;
	}

	public void setDestPath(String destPath) {
		this.destPath = destPath;
	}
    
	
	
}
