package com.bosssoft.install.windows.patch.event;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.event.IStepInterceptor;

public class RollbackDigestInterceptor implements IStepInterceptor{

	public void beforeStep(IContext context) {
		
		
	}

	public boolean isIgnoreThis(IContext context) {
		if("true".equals(context.getStringValue("IS_UPDATE"))) return true;
		else return false;
	}

}
