package com.bosssoft.install.windows.patch.action;

import java.util.Map;

import org.apache.log4j.Logger;

import com.bosssoft.install.windows.patch.util.ExceptionProcessor;
import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.wizard.action.InitDBSqlScript;

public class InitDB implements IAction{
	transient Logger logger = Logger.getLogger(getClass());
	public void execute(IContext context, Map params) throws InstallException {
		if("true".equals(context.getStringValue("IS_ROLLBACK")))return;
		
		logger.info("init DB....");
		InitDBSqlScript dbSqlScript=new InitDBSqlScript();
		try{
			dbSqlScript.execute(context, params);
		}catch(Exception e){
			logger.error(e);
			ExceptionProcessor processor=new ExceptionProcessor();
			processor.doHandle(e, context, params);
		}
		
		
	}

	public void rollback(IContext context, Map params) throws InstallException {
		// TODO Auto-generated method stub
		
	}

}
