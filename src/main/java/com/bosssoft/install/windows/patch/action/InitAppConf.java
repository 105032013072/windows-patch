package com.bosssoft.install.windows.patch.action;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import com.bosssoft.install.windows.patch.util.ExceptionProcessor;
import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.core.util.ExpressionParser;

public class InitAppConf implements IAction{

	transient Logger logger = Logger.getLogger(getClass());
	public void execute(IContext context, Map params) throws InstallException {
		if("true".equals(context.getStringValue("IS_ROLLBACK")))return;
		
		logger.info("init app config.....");
		String initFiles=params.get("INIT_FILES").toString();
		String[] files=initFiles.split(",");
		for (String ifile : files) {
			Properties p=new Properties();
			try{
				InputStream fis=new FileInputStream(ifile);
				p.load(fis);
				fis.close();
				String appName=p.getProperty("APP_NAME");
				String appConfig=context.getStringValue("APP_DEPLOY_DIR")+p.getProperty("APP_CONFIG");
				String conftemp=ExpressionParser.parseString(p.getProperty("CONFIG_TEMPLET"));
				String tempVars=p.getProperty("TEMPLET_variables");
				doinit(appConfig,conftemp,tempVars,context);//初始化应用服务器下的配置文件
				logger.debug("Update Product: init "+appName+"config file "+appConfig);
				
				//初始化外部路径下的配置
				appConfig=context.getStringValue("BOSSSOFT_HOME")+File.separator
						  +appName+File.separator
						  +"conf"+File.separator
						  +new File(appConfig).getName();
				if(new File(appConfig).exists()) {
					doinit(appConfig,conftemp, tempVars, context);
					logger.debug("Update Product: init "+appName+"config file "+appConfig);
				}
			}catch(Exception e){
				logger.error(e);
				ExceptionProcessor processor=new ExceptionProcessor();
				processor.doHandle(e, context, params);
			}
			
		}
	
	}
	
    private void doinit(String configFile,String tempFile,String tempVars,IContext context){
        File f=new File(tempFile);
		
		Properties p = new Properties();
       
        p.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, f.getParent());
        p.setProperty(Velocity.ENCODING_DEFAULT, "UTF-8");
        p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
        
        Velocity.init(p);
		
		Template t = Velocity.getTemplate(f.getName()); 
		VelocityContext vc = new VelocityContext();
		
		String[] vars=tempVars.split(",");
		for (String v : vars) {
			vc.put(v, context.getStringValue(v));
		}
		StringWriter writer = new StringWriter();
		t.merge(vc, writer); 
		
		
		try {
			BufferedWriter bw = new BufferedWriter (new OutputStreamWriter (new FileOutputStream(configFile)));
			bw.write (writer.toString());
			bw.close();
		} catch (IOException e) {
			throw new InstallException("faild to init app config file becuse "+e);
		}
		
    }
	
	public void rollback(IContext context, Map params) throws InstallException {
		
		
	}

}
