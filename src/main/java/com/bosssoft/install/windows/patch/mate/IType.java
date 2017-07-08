package com.bosssoft.install.windows.patch.mate;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;

public interface IType {
	
  
   public void update (IContext context,PatchApp patchApp)throws InstallException;
   
   public void record4Rollback(IContext context,PatchApp patchApp) throws InstallException;
}
