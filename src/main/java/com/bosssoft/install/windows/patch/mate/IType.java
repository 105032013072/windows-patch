package com.bosssoft.install.windows.patch.mate;

import com.bosssoft.platform.installer.core.IContext;

public interface IType {
  
   public void update(IContext context);
   
   public void record4Rollback(IContext context);
}
