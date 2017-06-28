package com.bosssoft.install.windows.patch.action;

import java.util.Map;

import org.apache.log4j.Logger;

import com.bosssoft.install.windows.patch.util.Recorder;
import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.io.operation.impl.XOSPermissionOperation;

public class FinishedPatch implements IAction{

	transient Logger logger = Logger.getLogger(getClass());
	public void execute(IContext context, Map params) throws InstallException {
		context.setValue("PATCH_LOG", Recorder.getPatchLog());
		//创建回滚的操作文件
		try {
			Recorder.saveRollback();
		} catch (Exception e) {
			throw new InstallException(e);
		}
	   logger.info("finished patch");
		
	}

	public void rollback(IContext context, Map params) throws InstallException {
		// TODO Auto-generated method stub
		
	}

}
