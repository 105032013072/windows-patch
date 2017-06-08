package com.bosssoft.install.windows.patch.mate;

public class ResourceType implements IType{
	private String sourcePath;
	private String destPath;

	public void update() {
		
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
