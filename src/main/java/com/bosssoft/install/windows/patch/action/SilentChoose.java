package com.bosssoft.install.windows.patch.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.log4j.Logger;

import com.bosssoft.install.windows.patch.util.PatchFileManager;
import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.core.util.I18nUtil;

public class SilentChoose implements IAction{

	transient Logger logger = Logger.getLogger(getClass());
	public void execute(IContext context, Map params) throws InstallException {
		Boolean flag=true;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in ));
		String read = null;
		
		System.out.println(I18nUtil.getString("CHOOSE.SILENT.PROMPT"));
		System.out.println(I18nUtil.getString("CHOOSE.SILENT.UPDATE.PROMPT"));
		System.out.println(I18nUtil.getString("CHOOSE.SILENT.ROLLBACK.PROMPT"));
	
		while(flag){
			try {
				read=br.readLine();
				if("U".equalsIgnoreCase(read)){
					flag=false;
					context.setValue("IS_UPDATE", true);
				}else if("R".equalsIgnoreCase(read)){
					//判断是否可回滚
					File rollBackFile=new File(PatchFileManager.getPatchRollBackFile());
					if(rollBackFile.exists()){
						flag=false;
						context.setValue("IS_UPDATE", false);
					}else{
						System.out.print(I18nUtil.getString("CHOOSE.SILENT.CANNOT.ROLLBACK"));
					}
				}else{
					System.out.print(I18nUtil.getString("CHOOSE.SILENT.ILLEGAL.INPUT"));
				}
			} catch (Exception e) {
				throw new InstallException();
			}

		}
	}

	public void rollback(IContext context, Map params) throws InstallException {
		// TODO Auto-generated method stub
		
	}

}
