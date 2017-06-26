package com.bosssoft.install.windows.patch.action;

import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

import com.bosssoft.install.windows.patch.mate.IType;
import com.bosssoft.install.windows.patch.mate.PatchApp;
import com.bosssoft.install.windows.patch.util.PatchUtil;
import com.bosssoft.install.windows.patch.util.Recorder;
import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.MainFrameController;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.core.runtime.SwingRunner;
import com.bosssoft.platform.installer.core.util.I18nUtil;

public class UpdateProduct implements IAction{
	transient Logger logger = Logger.getLogger(getClass());

	public void execute(IContext context, Map params) throws InstallException {
		List<PatchApp> list=(List<PatchApp>) context.getValue("PATCH_APPS");
		for (PatchApp patchApp : list) {
			List<IType> patchFiles=patchApp.getPatchFiles();
			for (IType iType : patchFiles) {
				iType.record4Rollback(context);
		        try {
		        	iType.update(context);
				} catch (Exception e) {
					int i=PatchUtil.showConfirmMessage("是否进行回滚", "异常提示");
		            System.out.println(i);
					//rollback(context, params);
				}
			}
		}
		
	}

	public void rollback(IContext context, Map params) throws InstallException {
		try {
			Recorder.saveRollback();
			
			//执行回滚操作
			RollBack rollBack=RollBack.class.newInstance();
			rollBack.execute(context, params);
		} catch (Exception e) {
			throw new InstallException(e);
		}
	}
}
