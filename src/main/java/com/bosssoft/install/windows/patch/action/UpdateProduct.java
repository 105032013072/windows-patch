package com.bosssoft.install.windows.patch.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
import com.bosssoft.platform.installer.core.message.MessageManager;
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
					logger.error(e);
					
					if("true".equals(context.getStringValue("IS_WINDOWS"))) op4Swing(e,context,params);
					else op4Silent(e,context,params);
				}
			}
		}
		
	}

	
	
	
	private void op4Silent(Exception e, IContext context, Map params) {
		Boolean flag=true;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in ));
		String read = null;
		
		System.out.print(I18nUtil.getString("SILENT.ROLLBACK.PROMPT"));
		while(flag){
			try {
				read=br.readLine();
				if("Y".equalsIgnoreCase(read)){
					logger.info(I18nUtil.getString("ROLLBACK.INFO"));
					rollback(context, params);
		        	doFinish(e,context);
		        	flag=false;
		        	logger.info("回滚完成");
				}else if("N".equalsIgnoreCase(read)){
					System.exit(0);
				}else {
					System.out.print(I18nUtil.getString("CHOOSE.SILENT.ILLEGAL.INPUT"));
				}
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
	}




	private void op4Swing(Exception e, IContext context, Map params) {
        int i=MainFrameController.showConfirmDialog(I18nUtil.getString("ROLLBACK.MSG"), I18nUtil.getString("ROLLBACK.TITLE"), JOptionPane.YES_NO_OPTION, 2);
		if(i==0){//回滚
        	MessageManager.syncSendMessage(I18nUtil.getString("ROLLBACK.INFO"));
        	logger.info(I18nUtil.getString("ROLLBACK.INFO"));
        	rollback(context, params);
        	doFinish(e,context);
        }else System.exit(0);
		
	}




	private void doFinish(Exception e, IContext context) {
	  Recorder.rollBackLog(e);//操作日志
	  context.setValue("PATCH_LOG", Recorder.getPatchLog());
	  context.setValue("IS_ROLLBACK", true);
	}

	public void rollback(IContext context, Map params) throws InstallException {
		try {
			Recorder.saveRollback();//回滚信息写入rollback.xml
			//执行回滚操作
			RollBack rollBack=RollBack.class.newInstance();
			rollBack.execute(context, params);
		} catch (Exception e) {
			throw new InstallException(e);
		}
	}
	
}
