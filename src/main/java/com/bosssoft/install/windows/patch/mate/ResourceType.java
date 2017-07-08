package com.bosssoft.install.windows.patch.mate;

import java.io.File;

import org.apache.log4j.Logger;

import com.bosssoft.install.windows.patch.util.Recorder;
import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.message.FileOperationMessageListener;
import com.bosssoft.platform.installer.io.FileUtils;
import com.bosssoft.platform.installer.io.operation.exception.OperationException;

public class ResourceType implements IType{
	private String sourcePath;
	private String destPath;
    transient Logger logger = Logger.getLogger(getClass());
	
	public void update(IContext context,PatchApp patchApp) throws InstallException{
		if (this.sourcePath == null) {
			throw new InstallException("The src attribute must be present.");
		}
		if (this.destPath == null) {
			throw new InstallException("The dest attribute must be present.");
		}
		File srcFile = new File(sourcePath);
		File destFile = new File(destPath);
		if (!srcFile.exists()) {
			throw new InstallException("src " + sourcePath + " does not exist.");
		}
		
		try {
			FileUtils.copy(srcFile, destFile, null, FileOperationMessageListener.INSTANCE);
		    logger.debug("Update Product: copy "+srcFile+" to "+destPath);
		} catch (OperationException e) {
			throw new InstallException("Failed to copy " + sourcePath + " to " + destPath, e);
		}
	}

  public void record4Rollback(IContext context,PatchApp patchApp) throws InstallException{
		
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
